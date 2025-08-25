package fuzs.universalbonemeal.world.level.block.behavior;

import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.NetherVines;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;

public class VineBehavior extends GrowingPlantBehavior {

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
    protected BlockState getGrownBlockState(BlockState sourceState, RandomSource randomSource) {
        return sourceState.setValue(VineBlock.UP, false);
    }
}
