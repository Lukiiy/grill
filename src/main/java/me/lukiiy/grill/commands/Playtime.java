package me.lukiiy.grill.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

public class Playtime implements BasicCommand {
    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        if (!(commandSourceStack.getSender() instanceof Player player)) {
            commandSourceStack.getSender().sendMessage("§cApenas jogadores podem utilizar esse comando!");
            return;
        }

        int hours = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 72000;

        player.sendMessage(Component.text("Tempo de jogo: " + Math.round(hours) + " horas").color(TextColor.color(0x7EAEFF)));
        return;
    }
}
