package fuzs.universalbonemeal.world.level.block.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public abstract class SpreadAroundBehavior implements BonemealBehavior {
    private final BlockStateProvider blockStateProvider;

    public SpreadAroundBehavior(BlockStateProvider blockStateProvider) {
        this.blockStateProvider = blockStateProvider;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader p_55064_, BlockPos p_55065_, BlockState p_55066_, boolean p_55067_) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level p_55069_, RandomSource p_55070_, BlockPos p_55071_, BlockState p_55072_) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState blockState) {
        this.placeNetherVegetation(level, random, pos);
    }

    private void placeNetherVegetation(ServerLevel level, RandomSource random, BlockPos pos) {
        int i = pos.getY();
        if (i >= level.getMinBuildHeight() + 1 && i + 1 < level.getMaxBuildHeight()) {
            final int spreadWidth = this.getSpreadWidth();
            final int spreadHeight = this.getSpreadHeight();
            for(int k = 0; k < spreadWidth * spreadWidth; ++k) {
                BlockPos blockpos1 = pos.offset(random.nextInt(spreadWidth) - random.nextInt(spreadWidth), random.nextInt(spreadHeight) - random.nextInt(spreadHeight), random.nextInt(spreadWidth) - random.nextInt(spreadWidth));
                BlockState blockstate1 = this.blockStateProvider.getState(random, blockpos1);
                if (level.isEmptyBlock(blockpos1) && blockpos1.getY() > level.getMinBuildHeight() && blockstate1.canSurvive(level, blockpos1)) {
                    ((WorldGenLevel) level).setBlock(blockpos1, blockstate1, 2);
                }
            }
        }
    }

    protected abstract int getSpreadWidth();

    protected abstract int getSpreadHeight();
}
