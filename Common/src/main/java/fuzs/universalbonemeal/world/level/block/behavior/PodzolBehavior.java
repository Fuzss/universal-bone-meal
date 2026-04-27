package fuzs.universalbonemeal.world.level.block.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;

public class PodzolBehavior implements BoneMealBehavior {
    private static final BlockStateProvider PODZOL_VEGETATION_PROVIDER = new WeightedStateProvider(WeightedList.<BlockState>builder()
            .add(Blocks.SWEET_BERRY_BUSH.defaultBlockState().setValue(SweetBerryBushBlock.AGE, 3), 2)
            .add(Blocks.SWEET_BERRY_BUSH.defaultBlockState().setValue(SweetBerryBushBlock.AGE, 2), 4)
            .add(Blocks.SWEET_BERRY_BUSH.defaultBlockState().setValue(SweetBerryBushBlock.AGE, 1), 8)
            .add(Blocks.SWEET_BERRY_BUSH.defaultBlockState().setValue(SweetBerryBushBlock.AGE, 0), 12)
            .add(Blocks.FERN.defaultBlockState(), 120)
            .add(Blocks.DEAD_BUSH.defaultBlockState(), 1));

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos blockPos, BlockState blockState) {
        return level.getBlockState(blockPos.above()).isAir();
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos blockPos, BlockState blockState) {
        label:
        for (int i = 0; i < 128; ++i) {
            BlockPos randomPos = blockPos.above();
            for (int j = 0; j < i / 16; ++j) {
                randomPos = randomPos.offset(random.nextInt(3) - 1,
                        (random.nextInt(3) - 1) * random.nextInt(3) / 2,
                        random.nextInt(3) - 1);
                if (!level.getBlockState(randomPos.below()).is(Blocks.PODZOL) || level.getBlockState(randomPos)
                        .isCollisionShapeFullBlock(level, randomPos)) {
                    continue label;
                }
            }

            BlockState stateAtRandomPosition = level.getBlockState(randomPos);
            if (stateAtRandomPosition.is(Blocks.FERN) && random.nextInt(10) == 0) {
                ((BonemealableBlock) Blocks.FERN).performBonemeal(level, random, randomPos, stateAtRandomPosition);
            }

            if (stateAtRandomPosition.isAir()) {
                if (random.nextInt(5) == 0) {
                    if (level.isEmptyBlock(randomPos) && randomPos.getY() > level.getMinY()) {
                        BlockState stateToPlace = PODZOL_VEGETATION_PROVIDER.getState(level, random, randomPos);
                        level.setBlock(randomPos, stateToPlace, 2);
                    }
                }

            }
        }
    }
}
