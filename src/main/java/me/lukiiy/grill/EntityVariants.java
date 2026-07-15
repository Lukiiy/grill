package me.lukiiy.grill;

import me.lukiiy.grill.utils.MobVariant;
import net.minecraft.world.entity.variant.SpawnContext;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class EntityVariants implements Listener {
    private final Random random = ThreadLocalRandom.current();
    private static final EnumSet<CreatureSpawnEvent.SpawnReason> IGNORED_REASONS = EnumSet.of(CreatureSpawnEvent.SpawnReason.CURED, CreatureSpawnEvent.SpawnReason.BREEDING, CreatureSpawnEvent.SpawnReason.CUSTOM, CreatureSpawnEvent.SpawnReason.COMMAND, CreatureSpawnEvent.SpawnReason.SPAWNER); // TODO: Command?

    private static final NamespacedKey ATTRIBUTE_MODIFIER_KEY = new NamespacedKey(Grill.getInstance(), "mod");

    private final List<MobVariant<?>> variants = List.of(
            // Charged creeper abaixo da camada 0! 15%
            new MobVariant<>(Creeper.class, (b) -> b.getLocation().y() < 0, .15, (creeper, _) -> creeper.setPowered(true)),

            // Zumbis um pouco mais rápidos acima da camada 64! 10%
            new MobVariant<>(Zombie.class, (b) -> b.getLocation().y() > 64, .10,
                    (zombie, _) -> {
                        AttributeInstance instance = zombie.getAttribute(Attribute.MOVEMENT_SPEED);

                        if (instance != null) instance.addModifier(new AttributeModifier(ATTRIBUTE_MODIFIER_KEY, .05, AttributeModifier.Operation.ADD_NUMBER));
                    }
            ),

            // Esqueleto sem arco. 10%
            new MobVariant<>(Skeleton.class, (_) -> true, .10, (skeleton, _) -> skeleton.getEquipment().setItemInMainHand(new ItemStack(Material.AIR)))
    );

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void spawn(CreatureSpawnEvent event) {
        if (IGNORED_REASONS.contains(event.getSpawnReason())) return;

        LivingEntity entity = event.getEntity();
        Block block = entity.getLocation().getBlock();

        for (MobVariant<?> variant : variants) {
            if (!variant.type().isInstance(entity) || !variant.condition().test(block) || random.nextDouble() >= variant.chance()) continue;

            @SuppressWarnings("unchecked")
            MobVariant<LivingEntity> v = (MobVariant<LivingEntity>) variant;

            v.action().accept(entity, block);
            break; // Apenas 1 variante por entidade :)
        }
    }
}
