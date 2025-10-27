package com.lumintorious.tfcambiental.api;

import com.lumintorious.tfcambiental.TFCAmbiental;
import com.lumintorious.tfcambiental.TFCAmbientalTags;
import com.lumintorious.tfcambiental.data.TemperatureModifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@FunctionalInterface
public interface EntityTemperatureProvider
{
    Optional<TemperatureModifier> getModifier(Player player);

    static void evaluateAll(Player player, TemperatureModifier.Cache cache) {
        for (var fn : AmbientalRegistry.ENTITIES) {
            cache.add(fn.getModifier(player));
        }
    }

    static Optional<TemperatureModifier> handleHotEntities(Player player) {
        List<Entity> nearbyEntities = player.level().getEntitiesOfClass(Entity.class, (new AABB(player.blockPosition()).inflate(5.0D, 2.0D, 5.0D)));
        if (nearbyEntities.size() > 0) {
            AtomicReference<Float> change = new AtomicReference<>(0.0f);
            nearbyEntities.forEach(entity -> {
                if (entity.getType().is(TFCAmbientalTags.HOT_ENTITIES)) {
                    change.updateAndGet(v -> v + 1.0f);
                }
            });
            return TemperatureModifier.defined("hot_entity", change.get(), 0);
        }
        return TemperatureModifier.none();
    }

    static Optional<TemperatureModifier> handleColdEntities(Player player) {
        List<Entity> nearbyEntities = player.level().getEntitiesOfClass(Entity.class, (new AABB(player.blockPosition()).inflate(5.0D, 2.0D, 5.0D)));
        if (!nearbyEntities.isEmpty()) {
            AtomicReference<Float> change = new AtomicReference<>(0.0f);
            nearbyEntities.forEach(entity -> {
                if (entity.getType().is(TFCAmbientalTags.COLD_ENTITIES)) {
                    change.updateAndGet(v -> v + 1.0f);
                }
            });
            return TemperatureModifier.defined("cold_entity", -1 * change.get(), 0);
        }
        return TemperatureModifier.none();
    }
}
