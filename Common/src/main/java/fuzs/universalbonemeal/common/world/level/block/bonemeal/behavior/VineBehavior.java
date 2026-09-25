package fuzs.universalbonemeal.common.world.level.block.bonemeal.behavior;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.Optional;

public final class VineBehavior extends GrowingPlantBehavior {
    public static final MapCodec<VineBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance).apply(
            instance,
            VineBehavior::new));

    public VineBehavior(Direction direction, IntProvider blocksToGrow, BlockPredicate canGrowInto, Holder<BlockStateProvider> vegetation) {
        super(direction, blocksToGrow, canGrowInto, vegetation, Optional.empty());
    }

    @Override
    public MapCodec<VineBehavior> codec() {
        return CODEC;
    }

    @Override
    protected BlockState getGrownBlockState(ServerLevel level, RandomSource random, BlockPos offsetPos, BlockState sourceState) {
        // The top face is never valid for a newly grown block below the source, strip it so the state stays consistent.
        return super.getGrownBlockState(level, random, offsetPos, sourceState).trySetValue(VineBlock.UP, Boolean.FALSE);
    }

    /**
     * @see VineBlock#getUpdatedState(BlockState, net.minecraft.world.level.BlockGetter, BlockPos)
     */
    @Override
    protected void placeBlock(ServerLevel level, BlockPos pos, BlockState state) {
        // Skip shape updates, they might clear the copied vine faces again.
        level.setBlock(pos, state, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
    }
}
