package fuzs.universalbonemeal.world.level.block.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;

public abstract class SpreadAwayBehavior implements BoneMealBehavior {

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos blockPos, BlockState blockState) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
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
            BlockState defaultBlockState = blockState.getBlock().defaultBlockState();
            for (int j = 0; j < i / 16; ++j) {
                currentPos = currentPos.offset(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
                if (!defaultBlockState.canSurvive(level, currentPos) || level.getBlockState(currentPos).isCollisionShapeFullBlock(level, currentPos)) {
                    continue label;
                }
            }
            if (level.isEmptyBlock(currentPos) && currentPos.getY() > level.getMinY()) {
                ((WorldGenLevel) level).setBlock(currentPos, defaultBlockState, 2);
                if (++successes >= this.getMostSuccesses()) {
                    return;
                }
            }
        }
    }

    protected abstract int getSpreadWidth();

    protected abstract int getMostSuccesses();
}
