package fuzs.universalbonemeal.common.world.level.block.behavior;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.Optional;

public class VineBehavior extends GrowingPlantBehavior {
    public static final MapCodec<VineBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance).apply(
            instance,
            VineBehavior::new));

    public VineBehavior(Direction direction, IntProvider blocksToGrow, BlockPredicate canGrowInto, Holder<BlockStateProvider> vegetation, IntProvider maxHeight) {
        super(direction, blocksToGrow, canGrowInto, vegetation, maxHeight, Optional.empty());
    }

    @Override
    public MapCodec<VineBehavior> codec() {
        return CODEC;
    }

    /**
     * @see VineBlock#getUpdatedState(BlockState, BlockGetter, BlockPos)
     */
    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, BonemealSource source) {
        // Only grow from a vine attached to a horizontal face;
        // otherwise the grown blocks are updated automatically by the block itself to show all faces.
        return this.hasHorizontalFace(state) && super.isValidBonemealTarget(level, pos, state, source);
    }

    private boolean hasHorizontalFace(BlockState state) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BooleanProperty property = VineBlock.getPropertyForFace(direction);
            if (state.hasProperty(property) && state.getValue(property)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected BlockState getGrownBlockState(ServerLevel level, RandomSource random, BlockPos offsetPos, BlockState sourceState) {
        // The top face is never valid for a newly grown block below the source, strip it so the state stays consistent.
        return super.getGrownBlockState(level, random, offsetPos, sourceState).trySetValue(VineBlock.UP, Boolean.FALSE);
    }

    /**
     * @see VineBlock#getUpdatedState(BlockState, BlockGetter, BlockPos)
     */
    @Override
    protected void placeBlock(ServerLevel level, BlockPos pos, BlockState state) {
        // Skip shape updates, they might clear the copied vine faces again.
        level.setBlock(pos, state, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
    }
}
