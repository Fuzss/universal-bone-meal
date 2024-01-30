package fuzs.universalbonemeal.world.level.block.behavior;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.NetherVines;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public class VineBehavior extends GrowingPlantBehavior {

    public VineBehavior() {
        super(Direction.DOWN);
    }

    @Override
    protected int getBlocksToGrowWhenBonemealed(Random random) {
        return NetherVines.getBlocksToGrowWhenBonemealed(random);
    }

    @Override
    protected boolean canGrowInto(BlockState state) {
        return state.isAir();
    }

    @Override
    protected BlockState getGrownBlockState(Block sourceBlock, BlockState sourceState) {
        return sourceState.setValue(VineBlock.UP, false);
    }
}
