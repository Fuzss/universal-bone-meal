package fuzs.universalbonemeal.world.level.block.behavior;

import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class ChorusPlantBehavior extends ChorusFlowerBehavior {

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState blockState, boolean isClient) {
        for (BlockPos flowerPosition : this.getFlowerPositions(level, pos)) {
            if (super.isValidBonemealTarget(level, flowerPosition, level.getBlockState(flowerPosition), isClient)) return true;
        }
        return false;
    }

    @Override
    public void performBonemeal(ServerLevel level, Random random, BlockPos pos, BlockState blockState) {
        for (BlockPos flowerPosition : this.getFlowerPositions(level, pos)) {
            super.performBonemeal(level, random, flowerPosition, level.getBlockState(flowerPosition));
        }
    }

    private Collection<BlockPos> getFlowerPositions(BlockGetter level, BlockPos startPos) {
        Set<BlockPos> targets = Sets.newHashSet();
        getTopConnectedBlock(level, startPos.mutable(), Blocks.CHORUS_PLANT, Blocks.CHORUS_FLOWER, targets, Direction.DOWN, 128);
        return targets;
    }

    public static void getTopConnectedBlock(BlockGetter level, BlockPos.MutableBlockPos sourcePosition, Block sourceBlock, Block targetBlock, Collection<BlockPos> targets, Direction sourceDirection, int depth) {
        BlockState sourceState = level.getBlockState(sourcePosition);
        if (depth <= 0 || !sourceState.is(sourceBlock)) {
            if (sourceState.is(targetBlock)) targets.add(sourcePosition.immutable());
            return;
        }
        for (Map.Entry<Direction, BooleanProperty> entry : PipeBlock.PROPERTY_BY_DIRECTION.entrySet()) {
            Direction direction = entry.getKey();
            if (direction != Direction.DOWN && direction != sourceDirection) {
                if (sourceState.getValue(entry.getValue())) {
                    sourcePosition.move(direction);
                    getTopConnectedBlock(level, sourcePosition, sourceBlock, targetBlock, targets, direction.getOpposite(), depth - 1);
                    sourcePosition.move(direction.getOpposite());
                }
            }
        }
    }
}
