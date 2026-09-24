package fuzs.universalbonemeal.common.world.level.block.behavior;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.NetherVines;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;

public class VineBehavior extends GrowingPlantBehavior {
    public static final MapCodec<VineBehavior> CODEC = MapCodec.unit(VineBehavior::new);

    @Override
    public MapCodec<VineBehavior> codec() {
        return CODEC;
    }

    @Override
    protected Direction getGrowthDirection() {
        return Direction.DOWN;
    }

    @Override
    protected int getBlocksToGrowWhenBonemealed(RandomSource random) {
        return NetherVines.getBlocksToGrowWhenBonemealed(random);
    }

    @Override
    protected boolean canGrowInto(BlockState state) {
        return state.isAir();
    }

    @Override
    protected BlockState getGrownBlockState(BlockState sourceState, RandomSource randomSource, ServerLevel level, BlockPos pos) {
        return sourceState.setValue(VineBlock.UP, false);
    }
}
