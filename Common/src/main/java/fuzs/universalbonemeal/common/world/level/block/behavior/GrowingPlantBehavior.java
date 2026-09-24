package fuzs.universalbonemeal.common.world.level.block.behavior;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record GrowingPlantBehavior(Direction direction,
                                   IntProvider blocksToGrow,
                                   BlockPredicate canGrowInto,
                                   Holder<BlockStateProvider> vegetation,
                                   IntProvider maxHeight) implements BoneMealBehavior {
    public static final MapCodec<GrowingPlantBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Direction.CODEC.optionalFieldOf("direction", Direction.UP).forGetter(GrowingPlantBehavior::direction),
                    IntProviders.codec(0, 128).fieldOf("blocks_to_grow").forGetter(GrowingPlantBehavior::blocksToGrow),
                    BlockPredicate.CODEC.optionalFieldOf("can_grow_into", BlockPredicate.ONLY_IN_AIR_PREDICATE)
                            .forGetter(GrowingPlantBehavior::canGrowInto),
                    BlockStateProvider.CODEC.fieldOf("vegetation").forGetter(GrowingPlantBehavior::vegetation),
                    IntProviders.codec(1, 128).fieldOf("max_height").forGetter(GrowingPlantBehavior::maxHeight))
            .apply(instance, GrowingPlantBehavior::new));

    @Override
    public MapCodec<GrowingPlantBehavior> codec() {
        return CODEC;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, BonemealSource source) {
        // TODO only for vines
        if (!test(state)) {
            return false;
        }

        if (this.getConnectedPlantHeight(level, pos, state.getBlock()) < this.getMaxHeightAtPosition(pos)) {
            BlockPos headPos = getHeadPos(level, pos, state.getBlock(), this.direction);
            BlockPos targetPos = headPos.relative(this.direction);
            if (level instanceof LevelAccessor levelAccessor) {
                return this.canGrowInto.test(levelAccessor, targetPos);
            } else {
                return level.getBlockState(targetPos).isAir();
            }
        } else {
            return false;
        }
    }

    private static boolean test(BlockState state) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BooleanProperty property = VineBlock.getPropertyForFace(direction);
            if (state.hasProperty(property) && state.getValue(property)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        BlockPos headPos = getHeadPos(level, pos, state.getBlock(), this.direction);
        this.growPlant(level, random, headPos, state);
    }

    private void growPlant(ServerLevel level, RandomSource random, BlockPos pos, BlockState sourceState) {
        BlockPos.MutableBlockPos offsetPos = pos.relative(this.direction).mutable();
        int blocksToGrow = this.blocksToGrow.sample(random);
        for (int i = 0; i < blocksToGrow && this.canGrowInto.test(level, offsetPos); ++i) {
            BlockState vegetationState = this.vegetation.value()
                    .getState(level, random, offsetPos.relative(this.direction.getOpposite()));
            if (vegetationState.hasProperty(VineBlock.UP)) {
                vegetationState = vegetationState.setValue(VineBlock.UP, false);
            }

            level.setBlock(offsetPos, vegetationState, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
            // Stop if we grew a block that is not the default plant block, like a cactus flower on a cactus.
            if (!vegetationState.is(sourceState.getBlock())) {
                break;
            }

            offsetPos.move(this.direction);
        }

        this.resetAgeProperty(level, pos);
    }

    private void resetAgeProperty(ServerLevel level, BlockPos topPos) {
        // Reset the age of the top block so the plant can be bone mealed again.
        if (this.direction == Direction.UP) {
            BlockState state = level.getBlockState(topPos);
            if (state.hasProperty(BlockStateProperties.AGE_15)) {
                state = state.setValue(BlockStateProperties.AGE_15, 0);
                level.setBlockAndUpdate(topPos, state);
                state.updateNeighbourShapes(level, topPos, Block.UPDATE_ALL);
            }
        }
    }

    private int getConnectedPlantHeight(BlockGetter level, BlockPos pos, Block block) {
        BlockPos topPos = getTopConnectedBlock(level, pos, block, this.direction);
        BlockPos bottomPos = getTopConnectedBlock(level, pos, block, this.direction.getOpposite());
        return Math.abs(topPos.getY() - bottomPos.getY());
    }

    private int getMaxHeightAtPosition(BlockPos pos) {
        // always use 0 seed, as client does not have access to world seed
        return this.maxHeight.sample(WorldgenRandom.seedSlimeChunk(pos.getX(), pos.getZ(), 0, 987234911L));
    }

    /**
     * @see net.minecraft.world.level.block.GrowingPlantBodyBlock#getHeadPos(BlockGetter, BlockPos, Block)
     */
    private static BlockPos getHeadPos(BlockGetter level, BlockPos pos, Block block, Direction direction) {
        return getTopConnectedBlock(level, pos, block, direction);
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
}
