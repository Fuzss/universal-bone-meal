package fuzs.universalbonemeal.common.world.level.block.bonemeal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Shared traversal logic for finding chorus flowers connected to a chorus plant, used by both the bone meal action and
 * the target predicate.
 */
public interface ChorusPlantTraverser {

    HolderSet<Block> plant();

    HolderSet<Block> flower();

    int searchRange();

    default Collection<BlockPos> getFlowerPositions(BlockGetter level, BlockPos startPos) {
        Set<BlockPos> targets = new HashSet<>();
        this.getTopConnectedBlock(level,
                startPos.mutable(),
                this.plant(),
                this.flower(),
                targets,
                Direction.DOWN,
                this.searchRange());
        return targets;
    }

    private void getTopConnectedBlock(BlockGetter level, BlockPos.MutableBlockPos sourcePos, HolderSet<Block> sourceBlocks, HolderSet<Block> targetBlocks, Collection<BlockPos> targets, Direction sourceDirection, int depth) {
        BlockState sourceState = level.getBlockState(sourcePos);
        if (depth <= 0 || !sourceBlocks.contains(sourceState.typeHolder())) {
            if (targetBlocks.contains(sourceState.typeHolder())) {
                targets.add(sourcePos.immutable());
            }

            return;
        }

        for (Map.Entry<Direction, BooleanProperty> entry : PipeBlock.PROPERTY_BY_DIRECTION.entrySet()) {
            Direction direction = entry.getKey();
            if (direction != Direction.DOWN && direction != sourceDirection && sourceState.getValue(entry.getValue())) {
                sourcePos.move(direction);
                this.getTopConnectedBlock(level,
                        sourcePos,
                        sourceBlocks,
                        targetBlocks,
                        targets,
                        direction.getOpposite(),
                        depth - 1);
                sourcePos.move(direction.getOpposite());
            }
        }
    }
}
