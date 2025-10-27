package com.lumintorious.tfcambiental.data;

import com.lumintorious.tfcambiental.TFCAmbiental;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.stream.Stream;

public record PlayerTemperaturePacket(CompoundTag tag) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<PlayerTemperaturePacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TFCAmbiental.MODID, "update_player_temperature"));

        public static final StreamCodec<ByteBuf, PlayerTemperaturePacket> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.COMPOUND_TAG,
                PlayerTemperaturePacket::tag,
                PlayerTemperaturePacket::new
        );

        public static PlayerTemperaturePacket from(PlayerTemperature playerTemperature) {
            return new PlayerTemperaturePacket(playerTemperature.write());
        }

        public PlayerTemperature create() {
            return PlayerTemperature.read(this.tag());
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
}
