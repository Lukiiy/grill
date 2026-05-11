package me.lukiiy.grill;

import io.papermc.paper.chat.ChatRenderer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.object.ObjectContents;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public final class GrillChatRenderer implements ChatRenderer {
    @Override
    public Component render(Player source, Component sourceDisplayName, Component message, Audience viewer) {
        Component displayName = sourceDisplayName;

        if (viewer instanceof Player) displayName = Component.empty().append(Component.object(ObjectContents.playerHead(source.getPlayerProfile()))).append(Component.text(" ")).append(sourceDisplayName);

        return Component.translatable("chat.type.text", displayName, decorateMentions(message));
    }

    private Component decorateMentions(Component message) {
        Component result = message;

        for (Player target : Bukkit.getOnlinePlayers()) {
            String name = PlainTextComponentSerializer.plainText().serialize(target.name());
            Pattern pattern = Pattern.compile("(?i)(?<![\\p{L}\\p{N}_])" + Pattern.quote(name) + "(?![\\p{L}\\p{N}_])");
            Component mention = Component.text(name, TextColor.color(0x9BDDFF));

            result = result.replaceText(builder -> builder.match(pattern).replacement(mention));
        }

        return result;
    }
}