package me.lukiiy.grill.utils;

import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

public record MobVariant<T extends LivingEntity>(Class<T> type, Predicate<Block> condition, double chance, BiConsumer<T, Block> action) {}