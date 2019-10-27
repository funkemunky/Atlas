package cc.funkemunky.api.commands.impl.args;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.commands.FunkeArgument;
import cc.funkemunky.api.commands.FunkeCommand;
import cc.funkemunky.api.profiling.ResultsType;
import cc.funkemunky.api.utils.Color;
import cc.funkemunky.api.utils.MiscUtils;
import cc.funkemunky.api.utils.Pastebin;
import cc.funkemunky.api.utils.Tuple;
import lombok.val;
import org.apache.commons.lang.time.DateFormatUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class ProfileArgument extends FunkeArgument {
    public ProfileArgument(FunkeCommand parent, String name, String display, String description, String... permission) {
        super(parent, name, display, description, permission);

        addTabComplete(2, "reset");
    }

    @Override
    public void onArgument(CommandSender sender, Command cmd, String[] args) {
        if(args.length > 1) {
            switch(args[1].toLowerCase()) {
                case "reset": {
                    Atlas.getInstance().getProfile().reset();
                    break;
                }
                case "average":
                case "avg": {
                    makePaste(sender, ResultsType.AVERAGE);
                    break;
                }
                case "tick": {
                    makePaste(sender, ResultsType.TICK);
                    break;
                }
                case "samples":
                case "sample": {
                    makePaste(sender, ResultsType.SAMPLES);
                    break;
                }
                default: {
                    makePaste(sender, ResultsType.TOTAL);
                    break;
                }
            }
        } else {
           makePaste(sender, ResultsType.TOTAL);
        }
    }

    private void makePaste(CommandSender sender, ResultsType type) {
        List<String> body = new ArrayList<>();
        body.add(MiscUtils.lineNoStrike());
        float totalPCT = 0;
        val results = Atlas.getInstance().getProfile().results(type);

        for (String key : results.keySet()) {
            //Converting nanoseconds to millis to be more readable.
            Tuple<Integer, Double> result = results.get(key);
            double amount = result.two / 1000000D;

            body.add(key + ": " + amount + "ms (" + result.one + " calls)");
        }
        StringBuilder builder = new StringBuilder();
        for (String aBody : body) {
            builder.append(aBody).append(";");
        }

        builder.deleteCharAt(body.size() - 1);

        String bodyString = builder.toString().replaceAll(";", "\n");

        try {
            sender.sendMessage(Color.Green + "Results: " + Pastebin.makePaste(bodyString, "Atlas Profile: " + DateFormatUtils.format(System.currentTimeMillis(), ", ", TimeZone.getTimeZone("604")), Pastebin.Privacy.UNLISTED));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
