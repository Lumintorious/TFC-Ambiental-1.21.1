package com.lumintorious.tfcambiental.api;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.pathfinder.FlyNodeEvaluator;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import org.jetbrains.annotations.NotNull;

public class BuildingInsulationPathNodeEvaluator extends FlyNodeEvaluator {
    @Override
    public @NotNull PathType getPathType(@NotNull PathfindingContext context, int x, int y, int z) {
        var blockState = context.getBlockState(new BlockPos(x, y, z));
        if (blockState.getBlock() instanceof DoorBlock door && door.isOpen(blockState)) {
            return PathType.OPEN;
        }

        if (blockState.getBlock() instanceof TrapDoorBlock trapDoor) {
            if (blockState.getValue(TrapDoorBlock.OPEN)) {
                return PathType.OPEN;
            } else {
                return PathType.BLOCKED;
            }
        }

        if (blockState.is(TFCBlocks.THATCH.get())) {
            return PathType.OPEN;
        }

        return super.getPathType(context, x, y, z);
    }
}
