package cc.funkemunky.api.commands.impl;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.commands.FunkeCommand;
import cc.funkemunky.api.commands.impl.args.*;

public class AtlasCommand extends FunkeCommand {
    public AtlasCommand() {
        super(Atlas.getInstance(), "atlas", "Atlas", "The Atlas main ancmd.", "atlas.admin");
    }

    @Override
    protected void addArguments() {
        getArguments().add(new UpdateArgument(this));
        getArguments().add(new ReloadArgument(this));
        getArguments().add(new ToggleArgument(this, "toggle", "toggle <packets, events>", "toggle parts of Atlas on or off.", "atlas.toggle"));
        getArguments().add(new ProfileArgument(this, "profile", "profile [reset]", "view or reset the lag information gathered.", "atlas.profile"));
        //Disabled due to its unnecessary need to be enabled since I only use it for testing certain parts of Atlas at times.
        //getArguments().add(new DevArgument(this, "dev", "dev", "do devevloper things."));
    }
}
