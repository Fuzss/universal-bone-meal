package fuzs.universalbonemeal.common.world.level.block.bonemeal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.WorldgenRandom;

/**
 * Shared traversal logic for growing plants, used by both the bone meal action and the target predicate.
 */
public interface GrowingPlantTraverser {

    /**
     * @see net.minecraft.world.level.block.GrowingPlantBodyBlock#getHeadPos(BlockGetter, BlockPos, Block)
     */
    default BlockPos getHeadPos(BlockGetter level, BlockPos pos, Block block, Direction direction) {
        return this.getTopConnectedBlock(level, pos, block, direction);
    }

    /**
     * @see net.minecraft.util.BlockUtil#getTopConnectedBlock(BlockGetter, BlockPos, Block, Direction, Block)
     */
    default BlockPos getTopConnectedBlock(BlockGetter level, BlockPos pos, Block block, Direction direction) {
        BlockPos.MutableBlockPos mutablePos = pos.mutable();
        BlockState state;
        do {
            mutablePos.move(direction);
            state = level.getBlockState(mutablePos);
        } while (state.is(block));
        return mutablePos.move(direction.getOpposite());
    }

    default int getConnectedPlantHeight(BlockGetter level, BlockPos pos, Block block, Direction direction) {
        BlockPos topPos = this.getTopConnectedBlock(level, pos, block, direction);
        BlockPos bottomPos = this.getTopConnectedBlock(level, pos, block, direction.getOpposite());
        return Math.abs(topPos.getY() - bottomPos.getY());
    }

    default int getMaxHeightAtPosition(IntProvider maxHeight, BlockPos pos) {
        // Always use a fixed seed, as the client does not have access to the world seed.
        return maxHeight.sample(WorldgenRandom.seedSlimeChunk(pos.getX(), pos.getZ(), 0, 987234911L));
    }
}
