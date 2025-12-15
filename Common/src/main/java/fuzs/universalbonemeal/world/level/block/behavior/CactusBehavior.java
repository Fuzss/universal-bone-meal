package fuzs.universalbonemeal.world.level.block.behavior;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class CactusBehavior extends SimpleGrowingPlantBehavior {

    @Override
    protected BlockState getGrownBlockState(BlockState sourceState, RandomSource randomSource) {
        return randomSource.nextDouble() < 0.1 ? Blocks.CACTUS_FLOWER.defaultBlockState() :
                super.getGrownBlockState(sourceState, randomSource);
    }
}
