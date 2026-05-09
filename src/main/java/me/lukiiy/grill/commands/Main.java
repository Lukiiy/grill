package me.lukiiy.grill.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.lukiiy.grill.Grill;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.Nullable;

public class Main implements BasicCommand {
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
