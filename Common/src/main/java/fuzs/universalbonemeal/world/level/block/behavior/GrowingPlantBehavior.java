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
import org.jetbrains.annotations.MustBeInvokedByOverriders;

public abstract class GrowingPlantBehavior implements BoneMealBehavior {

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos blockPos, BlockState blockState) {
        BlockPos headPos = this.getHeadPos(level, blockPos, blockState.getBlock());
        return this.canGrowInto(level.getBlockState(headPos.relative(this.getGrowthDirection())));
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

    @MustBeInvokedByOverriders
    protected void performBonemealTop(ServerLevel serverLevel, RandomSource randomSource, BlockPos topPos, BlockState sourceState) {
        BlockPos blockPos = topPos.relative(this.getGrowthDirection());
        int j = this.getBlocksToGrowWhenBonemealed(randomSource);
        for (int k = 0; k < j && this.canGrowInto(serverLevel.getBlockState(blockPos)); ++k) {
            BlockState blockState = this.getGrownBlockState(sourceState, randomSource);
            serverLevel.setBlockAndUpdate(blockPos, blockState);
            // stop if we grew a block that is not the default plant block, like a cactus flower on a cactus
            if (!blockState.is(sourceState.getBlock())) {
                break;
            }

            blockPos = blockPos.relative(this.getGrowthDirection());
        }
    }

    private BlockPos getHeadPos(BlockGetter level, BlockPos blockPos, Block block) {
        return getTopConnectedBlock(level, blockPos, block, this.getGrowthDirection());
    }

    public static BlockPos getTopConnectedBlock(BlockGetter level, BlockPos pos, Block block, Direction direction) {
        BlockPos.MutableBlockPos mutable = pos.mutable();
        BlockState blockstate;
        do {
            mutable.move(direction);
            blockstate = level.getBlockState(mutable);
        } while (blockstate.is(block));
        return mutable.move(direction.getOpposite());
    }

    protected abstract Direction getGrowthDirection();

    protected abstract int getBlocksToGrowWhenBonemealed(RandomSource random);

    protected abstract boolean canGrowInto(BlockState state);

    protected abstract BlockState getGrownBlockState(BlockState sourceState, RandomSource randomSource);
}
