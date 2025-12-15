package fuzs.universalbonemeal.world.level.block.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;

public class FruitStemBehavior implements BoneMealBehavior {

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        // let vanilla run otherwise
        if (!state.hasProperty(StemBlock.AGE) || state.getValue(StemBlock.AGE) != 7) return false;
        // no need to check if attached to a fruit already, since attached stems are completely different block for some reason
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos fruitBlockPos = pos.relative(direction);
            BlockState soilBlockState = level.getBlockState(fruitBlockPos.below());
            if (level.isEmptyBlock(fruitBlockPos) &&
                    (soilBlockState.getBlock() instanceof FarmBlock || soilBlockState.is(BlockTags.DIRT))) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        // growing fruit from stem blocks takes forever, let's speed it up a little
        while (level.getBlockState(pos) == state && random.nextInt(3) != 0) {
            state.randomTick(level, pos, random);
        }
    }
}
