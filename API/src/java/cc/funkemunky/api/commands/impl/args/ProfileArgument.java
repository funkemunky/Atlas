package cc.funkemunky.api.commands.impl.args;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.commands.FunkeArgument;
import cc.funkemunky.api.commands.FunkeCommand;
import cc.funkemunky.api.utils.Color;
import cc.funkemunky.api.utils.MathUtils;
import cc.funkemunky.api.utils.MiscUtils;
import cc.funkemunky.api.utils.Pastebin;
import org.apache.commons.lang.time.DateFormatUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class ProfileArgument extends FunkeArgument {
    public ProfileArgument(FunkeCommand parent, String name, String display, String description, String... permission) {
        super(parent, name, display, description, permission);

        addTabComplete(2, "reset");
    }

    @Override
    public void onArgument(CommandSender sender, Command cmd, String[] args) {
        if(args.length > 1) {
            if(args[1].equalsIgnoreCase("reset")) {
                Atlas.getInstance().getProfile().reset();
            } else {
                makePaste(sender);
            }
        } else {
           makePaste(sender);
        }
    }

    private void makePaste(CommandSender sender) {
        List<String> body = new ArrayList<>();
        body.add(MiscUtils.lineNoStrike());
        float totalPCT = 0;
        long totalTime = MathUtils.elapsed(Atlas.getInstance().getProfileStart());
        for (String string : Atlas.getInstance().getProfile().total.keySet()) {
            body.add(string);
            double stringTotal = TimeUnit.NANOSECONDS.toMillis(Atlas.getInstance().getProfile().total.get(string));
            int calls = Atlas.getInstance().getProfile().calls.get(string);
            double pct = stringTotal / totalTime;
            body.add("Latency: " + stringTotal / calls + "ms");
            body.add("Calls: " + calls);
            body.add("STD: " + Atlas.getInstance().getProfile().stddev.get(string));
            body.add("PCT Usage: " + MathUtils.round(pct, 8));
            body.add("PCT Lag:" + stringTotal / calls);
            totalPCT += (pct);
        }
        body.add("Total PCT: " + MathUtils.round(totalPCT, 4) + "%");
        body.add("Total Time: " + totalTime + "ms");
        body.add("Total Calls: " + Atlas.getInstance().getProfile().totalCalls);
        body.add("PCT Lag: " + totalTime / Atlas.getInstance().getProfile().totalCalls);
        body.add(MiscUtils.lineNoStrike());

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
