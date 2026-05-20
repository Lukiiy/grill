package me.lukiiy.grill;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import io.papermc.paper.block.bed.BedEnterAction;
import io.papermc.paper.block.bed.BedRuleResult;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.lukiiy.grill.utils.GrillChatRenderer;
import me.lukiiy.grill.utils.StateDebouncer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Listen implements Listener {
    private final ChatRenderer chatRenderer = new GrillChatRenderer();
    private final StateDebouncer<UUID, Boolean> hookDebouncer = new StateDebouncer<>(Duration.ofMinutes(1));

    @EventHandler
    public void join(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        DiscordHook hook = Grill.getInstance().getDiscordHook();

        if (hook != null) hookDebouncer.submit(player.getUniqueId(), true, () -> hook.sendMessage(Grill.getInstance().getConfig().getString("dcWebhook.join", "").replace("%p", PlainTextComponentSerializer.plainText().serialize(player.displayName()))));

        player.sendMessage(MiniMessage.miniMessage().deserialize(Grill.getInstance().getConfig().getString("welcomeMsg", "")));

        AttributeInstance waypointTransmit = player.getAttribute(Attribute.WAYPOINT_TRANSMIT_RANGE);
        if (waypointTransmit != null) waypointTransmit.setBaseValue(256);
    }

    @EventHandler
    public void quit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        DiscordHook hook = Grill.getInstance().getDiscordHook();

        if (hook != null) hookDebouncer.submit(player.getUniqueId(), false, () -> hook.sendMessage(Grill.getInstance().getConfig().getString("dcWebhook.leave", "").replace("%p", PlainTextComponentSerializer.plainText().serialize(player.displayName()))));
    }

    @EventHandler
    public void respawn(PlayerPostRespawnEvent e) { // Mostra uma mensagem no chat que indica onde você morreu.
        Player p = e.getPlayer();
        Location loc = p.getLastDeathLocation();
        if (loc == null) return;

        String dim;
        switch (p.getLastDeathLocation().getWorld().getEnvironment()) {
            case NORMAL -> dim = "Overworld";
            case NETHER -> dim = "Nether";
            case THE_END -> dim = "The End";
            default -> dim = "Custom";
        }

        String msg = Grill.getInstance().getConfig().getString("deathRecordMsg", "").replace("%x", loc.blockX() + "").replace("%y", loc.blockY() + "").replace("%z", loc.blockZ() + "").replace("%d", dim);

        p.sendMessage(MiniMessage.miniMessage().deserialize(msg));
    }

    @EventHandler
    public void chat(AsyncChatEvent e) {
        e.renderer(chatRenderer);
    }

    @EventHandler
    public void bed(PlayerBedEnterEvent e) { // Anuncia uma mensagem aleatória de uma lista
        BedEnterAction action = e.enterAction();
        List<String> messages = Grill.getInstance().getConfig().getStringList("bedMsgs");

        if (messages.isEmpty() || action.problem() != null || e.enterAction().canSleep() != BedRuleResult.ALLOWED) return;

        String msg = messages.get(ThreadLocalRandom.current().nextInt(messages.size())).replace("%p", MiniMessage.miniMessage().serialize(e.getPlayer().displayName()));

        Bukkit.getServer().broadcast(MiniMessage.miniMessage().deserialize(msg).color(NamedTextColor.YELLOW));
    }
}