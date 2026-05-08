package me.lukiiy.grill;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.Nullable;

public class Cmd implements BasicCommand {
    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        if (args.length == 0) {
            Grill.getInstance().reloadConfig();

            commandSourceStack.getSender().sendMessage(Component.text("Recarregado..!"));
        }
    }

    @Override
    public @Nullable String permission() {
        return "grill.cmd";
    }
}
