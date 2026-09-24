package fuzs.universalbonemeal.common.world.level.block.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

public abstract class AbstractGrowingPlantBehavior implements BoneMealBehavior {
    private final IntProvider blocksToGrow;

    protected AbstractGrowingPlantBehavior(IntProvider blocksToGrow) {
        this.blocksToGrow = blocksToGrow;
    }

    public IntProvider getBlocksToGrow() {
        return this.blocksToGrow;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, BonemealSource source) {
        BlockPos headPos = this.getHeadPos(level, pos, state.getBlock());
        return this.canGrowInto(level.getBlockState(headPos.relative(this.getGrowthDirection())));
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        BlockPos topPos = this.getHeadPos(level, pos, state.getBlock());
        this.performBonemealTop(level, random, topPos, state);
    }

    @MustBeInvokedByOverriders
    protected void performBonemealTop(ServerLevel level, RandomSource random, BlockPos topPos, BlockState sourceState) {
        BlockPos pos = topPos.relative(this.getGrowthDirection());
        int blocksToGrow = this.blocksToGrow.sample(random);
        for (int i = 0; i < blocksToGrow && this.canGrowInto(level.getBlockState(pos)); ++i) {
            BlockState state = this.getGrownBlockState(sourceState, random, level, pos);
            level.setBlockAndUpdate(pos, state);
            // stop if we grew a block that is not the default plant block, like a cactus flower on a cactus
            if (!state.is(sourceState.getBlock())) {
                break;
            }

            pos = pos.relative(this.getGrowthDirection());
        }
    }

    protected boolean canGrowInto(BlockState state) {
        return state.isAir();
    }

    protected BlockState getGrownBlockState(BlockState sourceState, RandomSource random, ServerLevel level, BlockPos pos) {
        return sourceState.getBlock().defaultBlockState();
    }

    private BlockPos getHeadPos(BlockGetter level, BlockPos pos, Block block) {
        return getTopConnectedBlock(level, pos, block, this.getGrowthDirection());
    }

    /**
     * @see net.minecraft.util.BlockUtil#getTopConnectedBlock(BlockGetter, BlockPos, Block, Direction, Block)
     */
    public static BlockPos getTopConnectedBlock(BlockGetter level, BlockPos pos, Block block, Direction direction) {
        BlockPos.MutableBlockPos mutablePos = pos.mutable();
        BlockState state;
        do {
            mutablePos.move(direction);
            state = level.getBlockState(mutablePos);
        } while (state.is(block));
        return mutablePos.move(direction.getOpposite());
    }

    protected abstract Direction getGrowthDirection();
}
