package fuzs.universalbonemeal.world.level.block.behavior;

import fuzs.universalbonemeal.core.CommonAbstractions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;

public class FruitStemBehavior implements BonemealBehavior {

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
        // let vanilla run otherwise
        if (!state.hasProperty(StemBlock.AGE) || state.getValue(StemBlock.AGE) != 7) return false;
        // no need to check if attached to a fruit already, since attached stems are completely different block for some reason
        return Direction.Plane.HORIZONTAL.stream().anyMatch(direction -> {
            return this.canSustainPlant(level, pos, state, direction);
        });
    }

    private boolean canSustainPlant(LevelReader level, BlockPos sourcePos, BlockState sourceBlock, Direction direction) {
        BlockPos blockpos = sourcePos.relative(direction);
        BlockState blockstate = level.getBlockState(blockpos.below());
        Block block = blockstate.getBlock();
        return level.isEmptyBlock(blockpos) && (CommonAbstractions.INSTANCE.canSustainPlant(blockstate, level, blockpos.below(), Direction.UP, sourceBlock.getBlock()) || block == Blocks.FARMLAND || block == Blocks.DIRT || block == Blocks.COARSE_DIRT || block == Blocks.PODZOL || block == Blocks.GRASS_BLOCK);
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
