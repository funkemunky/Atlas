package cc.funkemunky.api.commands.impl;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.commands.FunkeCommand;
import cc.funkemunky.api.commands.impl.args.UpdateArgument;
import org.bukkit.plugin.java.JavaPlugin;

public class AtlasCommand extends FunkeCommand {
    public AtlasCommand() {
        super(Atlas.getInstance(), "atlas", "Atlas", "The Atlas main command.", "atlas.admin");
    }

    @Override
    protected void addArguments() {
        getArguments().add(new UpdateArgument("update", "update <check, download> [args]", "manage updates for Atlas.", "atlas.update"));
    }
}
