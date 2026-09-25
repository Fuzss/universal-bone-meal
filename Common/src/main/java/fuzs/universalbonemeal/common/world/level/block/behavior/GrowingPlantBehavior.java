package fuzs.universalbonemeal.common.world.level.block.behavior;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class GrowingPlantBehavior implements BoneMealBehavior {
    public static final MapCodec<GrowingPlantBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(
            instance).and(Codec.STRING.optionalFieldOf("property").forGetter(GrowingPlantBehavior::property))
            .apply(instance, GrowingPlantBehavior::new));

    private final Direction direction;
    private final IntProvider blocksToGrow;
    private final BlockPredicate canGrowInto;
    private final Holder<BlockStateProvider> vegetation;
    private final IntProvider maxHeight;
    private final Optional<String> property;
    private @Nullable IntegerProperty ageProperty;

    public GrowingPlantBehavior(Direction direction, IntProvider blocksToGrow, BlockPredicate canGrowInto, Holder<BlockStateProvider> vegetation, IntProvider maxHeight, Optional<String> property) {
        this.direction = direction;
        this.blocksToGrow = blocksToGrow;
        this.canGrowInto = canGrowInto;
        this.vegetation = vegetation;
        this.maxHeight = maxHeight;
        this.property = property;
    }

    public GrowingPlantBehavior(Direction direction, IntProvider blocksToGrow, BlockPredicate canGrowInto, Holder<BlockStateProvider> vegetation, IntProvider maxHeight, IntegerProperty ageProperty) {
        this(direction, blocksToGrow, canGrowInto, vegetation, maxHeight, Optional.of(ageProperty.getName()));
        this.ageProperty = ageProperty;
    }

    protected static <T extends GrowingPlantBehavior> Products.P5<RecordCodecBuilder.Mu<T>, Direction, IntProvider, BlockPredicate, Holder<BlockStateProvider>, IntProvider> commonFields(RecordCodecBuilder.Instance<T> instance) {
        return instance.group(Direction.CODEC.optionalFieldOf("direction", Direction.UP)
                        .forGetter(GrowingPlantBehavior::direction),
                IntProviders.codec(0, 128).fieldOf("blocks_to_grow").forGetter(GrowingPlantBehavior::blocksToGrow),
                BlockPredicate.CODEC.optionalFieldOf("can_grow_into", BlockPredicate.ONLY_IN_AIR_PREDICATE)
                        .forGetter(GrowingPlantBehavior::canGrowInto),
                BlockStateProvider.CODEC.fieldOf("vegetation").forGetter(GrowingPlantBehavior::vegetation),
                IntProviders.codec(1, 128).fieldOf("max_height").forGetter(GrowingPlantBehavior::maxHeight));
    }

    public Direction direction() {
        return this.direction;
    }

    public IntProvider blocksToGrow() {
        return this.blocksToGrow;
    }

    public BlockPredicate canGrowInto() {
        return this.canGrowInto;
    }

    public Holder<BlockStateProvider> vegetation() {
        return this.vegetation;
    }

    public IntProvider maxHeight() {
        return this.maxHeight;
    }

    public Optional<String> property() {
        return this.property;
    }

    @Override
    public MapCodec<? extends BoneMealBehavior> codec() {
        return CODEC;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, BonemealSource source) {
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

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        BlockPos headPos = getHeadPos(level, pos, state.getBlock(), this.direction);
        this.growPlant(level, random, headPos, state);
    }

    private void growPlant(ServerLevel level, RandomSource random, BlockPos pos, BlockState sourceState) {
        BlockPos.MutableBlockPos offsetPos = pos.relative(this.direction).mutable();
        int blocksToGrow = this.blocksToGrow.sample(random);
        for (int growthAttempt = 0;
             growthAttempt < blocksToGrow && this.canGrowInto.test(level, offsetPos); ++growthAttempt) {
            BlockState vegetationState = this.getGrownBlockState(level, random, offsetPos, sourceState);
            this.placeBlock(level, offsetPos, vegetationState);
            // Stop if we grew a block that is not the default plant block, like a cactus flower on a cactus.
            if (!vegetationState.is(sourceState.getBlock())) {
                break;
            }

            offsetPos.move(this.direction);
        }

        this.resetAgeProperty(level, pos);
    }

    protected BlockState getGrownBlockState(ServerLevel level, RandomSource random, BlockPos offsetPos, BlockState sourceState) {
        return this.vegetation.value().getState(level, random, offsetPos.relative(this.direction.getOpposite()));
    }

    protected void placeBlock(ServerLevel level, BlockPos pos, BlockState state) {
        level.setBlockAndUpdate(pos, state);
    }

    /**
     * Reset the age of the top block so the plant can be bone mealed again.
     */
    private void resetAgeProperty(ServerLevel level, BlockPos topPos) {
        String property = this.property.orElse(null);
        if (property == null) {
            return;
        }

        BlockState state = level.getBlockState(topPos);
        IntegerProperty ageProperty = this.getAgeProperty(state, property);
        if (ageProperty != null) {
            state = state.setValue(ageProperty, 0);
            level.setBlockAndUpdate(topPos, state);
            state.updateNeighbourShapes(level, topPos, Block.UPDATE_ALL);
        }
    }

    private @Nullable IntegerProperty getAgeProperty(BlockState state, String property) {
        if (this.ageProperty == null || !state.hasProperty(this.ageProperty)) {
            return this.ageProperty = CropGrowthBehavior.findProperty(state, property);
        } else {
            return this.ageProperty;
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
    private static BlockPos getTopConnectedBlock(BlockGetter level, BlockPos pos, Block block, Direction direction) {
        BlockPos.MutableBlockPos mutablePos = pos.mutable();
        BlockState state;
        do {
            mutablePos.move(direction);
            state = level.getBlockState(mutablePos);
        } while (state.is(block));
        return mutablePos.move(direction.getOpposite());
    }
}
