package me.lukiiy.grill;

import io.papermc.paper.chat.ChatRenderer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.object.ObjectContents;
import org.bukkit.entity.Player;

public class GrillChatRenderer implements ChatRenderer {
    @Override
    public Component render(Player source, Component sourceDisplayName, Component message, Audience viewer) {
        return Component.translatable("chat.type.text", Component.empty().append(Component.object(ObjectContents.playerHead(source.getPlayerProfile()))).appendSpace().append(sourceDisplayName), message);
    }
}
