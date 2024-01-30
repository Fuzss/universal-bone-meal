package fuzs.universalbonemeal.world.level.block.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;

public abstract class SpreadAwayBehavior implements BonemealBehavior {

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
        this.placeOverworldGrass(level, random, pos, blockState);
    }

    private void placeOverworldGrass(ServerLevel level, RandomSource random, BlockPos pos, BlockState blockState) {
        int successes = 0;
        label:
        for (int i = (this.getSpreadWidth() + 1) * 16 - 1; i >= 0; i--) {
            BlockPos currentPos = pos;
            BlockState blockstate1 = blockState.getBlock().defaultBlockState();
            for (int j = 0; j < i / 16; ++j) {
                currentPos = currentPos.offset(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
                if (!blockstate1.canSurvive(level, currentPos) || level.getBlockState(currentPos).isCollisionShapeFullBlock(level, currentPos)) {
                    continue label;
                }
            }
            if (level.isEmptyBlock(currentPos) && currentPos.getY() > level.getMinBuildHeight()) {
                ((WorldGenLevel) level).setBlock(currentPos, blockstate1, 2);
                if (++successes >= this.getMostSuccesses()) {
                    return;
                }
            }
        }
    }

    protected abstract int getSpreadWidth();

    protected abstract int getMostSuccesses();
}
