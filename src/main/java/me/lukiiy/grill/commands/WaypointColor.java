package me.lukiiy.grill.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.lukiiy.grill.Grill;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;

public class WaypointColor implements BasicCommand {
    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        if (!(commandSourceStack.getSender() instanceof Player player)) {
            commandSourceStack.getSender().sendMessage(Grill.COMMAND_ERR_NONPLAYER);
            return;
        }

        if (args.length < 1) {
            player.sendMessage(Component.text("Você precisa especificar a cor em HEX (ex.: FF2C5A)!").color(TextColor.color(0xFF2C5A)));
            return;
        }

        String hexStr = args[0].startsWith("#") ? args[0].substring(1) : args[0];

        if (!hexStr.matches("^#?[0-9A-Fa-f]{6}$")) {
            player.sendMessage(Component.text("Cor inválida!").color(TextColor.color(0xFF2C5A)));
            return;
        }

        int red = Integer.parseInt(hexStr.substring(0, 2), 16);
        int green = Integer.parseInt(hexStr.substring(2, 4), 16);
        int blue = Integer.parseInt(hexStr.substring(4, 6), 16);

        player.setWaypointColor(Color.fromRGB(red, green, blue));
        player.sendMessage(Component.text("Você atualizou a cor do seu waypoint para ").append(Component.text("⏹").color(TextColor.color(red, green, blue))));
    }
}
