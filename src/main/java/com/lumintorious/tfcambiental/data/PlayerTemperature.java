package com.lumintorious.tfcambiental.data;

import com.lumintorious.tfcambiental.TFCAmbiental;
import com.lumintorious.tfcambiental.TFCAmbientalConfig;
import com.lumintorious.tfcambiental.api.*;
import com.lumintorious.tfcambiental.item.ClothesItem;
import net.dries007.tfc.common.player.IPlayerInfo;
import net.dries007.tfc.common.player.PlayerInfo;
import net.dries007.tfc.util.Helpers;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;

public class PlayerTemperature{

    // TODO use or remove
    public boolean isDefault;

    private int tick = 0;
    private int damageTick = 0;
    private int durabilityTick = 0;

    // Current values
    public float temperature;
    public float wetness;

    // Target values based on current modifiers
    private float target = 15;
    private float targetWetness = 0;

    // Rate of change towards target
    private float potency = 0;

    // Is inside building (cached value)
    private boolean isInside = false;
    private boolean isUnderRoof = false;

    private TemperatureModifier.Cache modifiers = new TemperatureModifier.Cache();

    public static final float BAD_MULTIPLIER = 0.001f;
    public static final float GOOD_MULTIPLIER = 0.0025f;
    public static final float CHANGE_CAP = 8F;
    public static final float WET_CHANGE_CAP = 2.0f;
    public static final float HIGH_CHANGE = 0.004f;

    public PlayerTemperature() {
        this(false);
    }

    public PlayerTemperature(boolean isDefault) {
        this.temperature = TFCAmbientalConfig.averageTemperature.get().floatValue();
        this.isDefault = isDefault;
    }

    public float getTemperatureChange() {
        float speed = getPotency() * 0.022f * TFCAmbientalConfig.temperatureChangeSpeed.get().floatValue();
        float change = Math.min(CHANGE_CAP, Math.max(-CHANGE_CAP, getTargetTemperature() - this.temperature));
        float AVERAGE = TFCAmbientalConfig.averageTemperature.get().floatValue();
        float changeSign = Math.signum(change);
        change = change + changeSign * 3;
        float newTemp = this.temperature + change;
        if ((this.temperature < AVERAGE && newTemp > this.temperature) || (this.temperature > AVERAGE && newTemp < this.temperature)) {
            speed *= GOOD_MULTIPLIER * TFCAmbientalConfig.goodTemperatureChangeSpeed.get().floatValue();
        } else {
            speed *= BAD_MULTIPLIER * TFCAmbientalConfig.badTemperatureChangeSpeed.get().floatValue();
        }
        return change * speed;
    }

    public float getWetnessChange() {
        float AVERAGE = TFCAmbientalConfig.averageTemperature.get().floatValue();
        float speed = (getTemperature() > AVERAGE ? 0.001f : 0.0005f) * TFCAmbientalConfig.wetnessChangeSpeed.get().floatValue();
        // Getting wet is fast, drying is slow
        if (getTargetWetness() > this.wetness) {
            speed *= 16;
        }
        float change = Math.min(WET_CHANGE_CAP, Math.max(-WET_CHANGE_CAP, getTargetWetness() - this.wetness));
        return change * speed;
    }


    public void evaluateModifiers(Player player) {
        this.modifiers = new TemperatureModifier.Cache();
        ItemTemperatureProvider.evaluateAll(player, this.modifiers);
        EnvironmentalTemperatureProvider.evaluateAll(player, this.modifiers);
        BlockTemperatureProvider.evaluateAll(player, this.modifiers);
        BlockEntityTemperatureProvider.evaluateAll(player, this.modifiers);
        EquipmentTemperatureProvider.evaluateAll(player, this.modifiers);
        EntityTemperatureProvider.evaluateAll(player, this.modifiers);
        this.modifiers.keepOnlyNEach(4);

        this.target = this.modifiers.getTargetTemperature();
        this.potency = this.modifiers.getTotalPotency();
        this.targetWetness = this.modifiers.getTargetWetness();

        if (this.target > this.temperature && this.temperature > TFCAmbientalConfig.hotThreshold.get().floatValue()) {
            this.potency /= this.potency;
        }
        if (this.target < this.temperature && this.temperature < TFCAmbientalConfig.coolThreshold.get().floatValue()) {
            this.potency /= this.potency;
        }

        this.potency = Math.max(1f, this.potency);
    }

    public float getTargetTemperature() {
        return this.target;
    }

    public float getTargetWetness() {
        return this.targetWetness;
    }

    public float getPotency() {
        return this.potency;
    }

    public float getTemperature() {
        return this.temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getWetness() {
        return this.wetness;
    }

    public void setWetness(float wetness) {
        this.wetness = Math.max(0, wetness);
    }

    public boolean isInside() {
        return isInside;
    }

    public void setInside(boolean isInside) {
        this.isInside = isInside;
    }

    public boolean isUnderRoof() {
        return isUnderRoof;
    }

    public void setUnderRoof(boolean isUnderRoof) {
        this.isUnderRoof = isUnderRoof;
    }

    public TemperatureModifier.Cache getModifiers() {
        return modifiers;
    }

    public void update(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            this.setTemperature(this.getTemperature() + this.getTemperatureChange());
            this.setWetness(this.getWetness() + this.getWetnessChange());
            float envTemp = EnvironmentalTemperatureProvider.getEnvironmentTemperatureWithTimeOfDay(player);
            float COLD = TFCAmbientalConfig.coolThreshold.get().floatValue();
            float HOT = TFCAmbientalConfig.hotThreshold.get().floatValue();

            if (envTemp > HOT || envTemp < COLD) {
                if (this.durabilityTick <= 600) {
                    this.durabilityTick++;
                } else {
                    this.durabilityTick = 0;
                    CuriosApi.getCuriosHelper().getEquippedCurios(player).ifPresent(c -> {
                        for (int i = 0; i < c.getSlots(); i++) {
                            ItemStack stack = c.getStackInSlot(i);
                            if (stack.getItem() instanceof ClothesItem) {
                                stack.setDamageValue(stack.getDamageValue() + 1);
                                if (stack.getDamageValue() > stack.getMaxDamage()) {
                                    stack.setCount(0);
                                }
                            }
                        }
                    });
                    player.getArmorSlots().forEach(stack -> {
                        if (stack.getItem() instanceof ClothesItem) {
                            stack.setDamageValue(stack.getDamageValue() + 1);
                            if (stack.getDamageValue() > stack.getMaxDamage()) {
                                stack.setCount(0);
                            }
                        }
                    });
                }
            }

            if (tick <= 20) {
                tick++;
                return;
            } else {
                tick = 0;
                this.damageTick++;
                this.evaluateModifiers(player);
                if (damageTick % 15 == 0) {
                    if (this.getTemperature() > TFCAmbientalConfig.burnThreshold.get().floatValue()) {
                        player.hurt(new DamageSource(player.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(TFCAmbiental.HOT)), 1f);
                    } else if (this.getTemperature() < TFCAmbientalConfig.freezeThreshold.get().floatValue()) {
                        player.hurt(new DamageSource(player.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(TFCAmbiental.FREEZE)), 1f);
                    }
                }
                if (damageTick % 90 == 0) {
                    if (IPlayerInfo.get(player) instanceof PlayerInfo stats) {
                        if (this.getTemperature() > TFCAmbientalConfig.hotThreshold.get().floatValue()) {
                            stats.addThirst(-5);
                        } else if (this.getTemperature() < TFCAmbientalConfig.coolThreshold.get().floatValue()) {
                            stats.addExhaustion(4f);
                        }
                    }
                }
            }

            PacketDistributor.sendToPlayer(serverPlayer, PlayerTemperaturePacket.from(this));
        }

    }

    public static PlayerTemperature read(CompoundTag compoundTag) {
        var temperature = new PlayerTemperature();
        temperature.temperature = compoundTag.getFloat("temperature");
        temperature.target = compoundTag.getFloat("target");
        temperature.potency = compoundTag.getFloat("potency");
        temperature.targetWetness = compoundTag.getFloat("targetWetness");
        temperature.wetness = compoundTag.getFloat("wetness");
        temperature.isUnderRoof = compoundTag.getBoolean("isUnderRoof");
        temperature.isInside = compoundTag.getBoolean("isInside");
        return temperature;
    }

    public @Nullable CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("temperature", this.getTemperature());
        tag.putFloat("target", this.target);
        tag.putFloat("potency", this.potency);
        tag.putFloat("targetWetness", this.targetWetness);
        tag.putFloat("wetness", this.wetness);
        tag.putBoolean("isUnderRoof", this.isUnderRoof);
        tag.putBoolean("isInside", this.isInside);
        return tag;
    }

    public static final IAttachmentSerializer<CompoundTag, PlayerTemperature> SERIALIZER = new IAttachmentSerializer<CompoundTag, PlayerTemperature>() {
        @Override
        public PlayerTemperature read(IAttachmentHolder iAttachmentHolder, CompoundTag compoundTag, HolderLookup.Provider provider) {
           return PlayerTemperature.read(compoundTag);
        }

        @Override
        public @Nullable CompoundTag write(PlayerTemperature temperature, HolderLookup.Provider provider) {
            return temperature.write();
        }
    };
}
