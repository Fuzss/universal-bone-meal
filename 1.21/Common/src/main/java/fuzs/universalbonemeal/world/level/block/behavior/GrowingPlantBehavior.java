package fuzs.universalbonemeal.world.level.block.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public abstract class GrowingPlantBehavior implements BoneMealBehavior {
    protected final Direction growthDirection;

    public GrowingPlantBehavior(Direction growthDirection) {
        this.growthDirection = growthDirection;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos blockPos, BlockState blockState) {
        BlockPos headPos = this.getHeadPos(level, blockPos, blockState.getBlock());
        return this.canGrowInto(level.getBlockState(headPos.relative(this.growthDirection)));
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos sourcePos, BlockState sourceState) {
        BlockPos topPos = this.getHeadPos(level, sourcePos, sourceState.getBlock());
        this.performBonemealTop(level, random, topPos, sourceState);
    }

    private void performBonemealTop(ServerLevel level, RandomSource randomSource, BlockPos topPos, BlockState sourceState) {
        BlockPos blockpos = topPos.relative(this.growthDirection);
        int j = this.getBlocksToGrowWhenBonemealed(randomSource);
        for(int k = 0; k < j && this.canGrowInto(level.getBlockState(blockpos)); ++k) {
            level.setBlockAndUpdate(blockpos, this.getGrownBlockState(sourceState.getBlock(), sourceState));
            blockpos = blockpos.relative(this.growthDirection);
        }
    }

    private BlockPos getHeadPos(BlockGetter level, BlockPos blockPos, Block block) {
        return getTopConnectedBlock(level, blockPos, block, this.growthDirection);
    }

    public static BlockPos getTopConnectedBlock(BlockGetter level, BlockPos pos, Block block, Direction direction) {
        BlockPos.MutableBlockPos mutable = pos.mutable();
        BlockState blockstate;
        do {
            mutable.move(direction);
            blockstate = level.getBlockState(mutable);
        } while(blockstate.is(block));
        return mutable.move(direction.getOpposite());
    }

    protected abstract int getBlocksToGrowWhenBonemealed(RandomSource random);

    protected abstract boolean canGrowInto(BlockState state);

    protected abstract BlockState getGrownBlockState(Block sourceBlock, BlockState sourceState);
}
