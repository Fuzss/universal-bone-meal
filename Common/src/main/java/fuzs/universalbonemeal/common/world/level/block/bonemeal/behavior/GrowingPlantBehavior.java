package fuzs.universalbonemeal.common.world.level.block.bonemeal.behavior;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.universalbonemeal.common.world.level.block.bonemeal.GrowingPlantTraverser;
import fuzs.universalbonemeal.common.world.level.block.bonemeal.LazyBlockProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public sealed class GrowingPlantBehavior implements BoneMealBehavior, GrowingPlantTraverser, LazyBlockProperty permits VineBehavior {
    public static final MapCodec<GrowingPlantBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(
            instance).and(Codec.STRING.optionalFieldOf("property").forGetter(behavior -> behavior.propertyName))
            .apply(instance, GrowingPlantBehavior::new));

    private final Direction direction;
    private final IntProvider blocksToGrow;
    private final BlockPredicate canGrowInto;
    private final Holder<BlockStateProvider> vegetation;
    private final Optional<String> propertyName;
    private @Nullable IntegerProperty property;

    public GrowingPlantBehavior(Direction direction, IntProvider blocksToGrow, BlockPredicate canGrowInto, Holder<BlockStateProvider> vegetation, Optional<String> propertyName) {
        this.direction = direction;
        this.blocksToGrow = blocksToGrow;
        this.canGrowInto = canGrowInto;
        this.vegetation = vegetation;
        this.propertyName = propertyName;
    }

    public GrowingPlantBehavior(Direction direction, IntProvider blocksToGrow, BlockPredicate canGrowInto, Holder<BlockStateProvider> vegetation, IntegerProperty property) {
        this(direction, blocksToGrow, canGrowInto, vegetation, Optional.of(property.getName()));
        this.property = property;
    }

    protected static <T extends GrowingPlantBehavior> Products.P4<RecordCodecBuilder.Mu<T>, Direction, IntProvider, BlockPredicate, Holder<BlockStateProvider>> commonFields(RecordCodecBuilder.Instance<T> instance) {
        return instance.group(Direction.CODEC.optionalFieldOf("direction", Direction.UP)
                        .forGetter(GrowingPlantBehavior::direction),
                IntProviders.codec(0, 128).fieldOf("blocks_to_grow").forGetter(GrowingPlantBehavior::blocksToGrow),
                BlockPredicate.CODEC.optionalFieldOf("can_grow_into", BlockPredicate.ONLY_IN_AIR_PREDICATE)
                        .forGetter(GrowingPlantBehavior::canGrowInto),
                BlockStateProvider.CODEC.fieldOf("vegetation").forGetter(GrowingPlantBehavior::vegetation));
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

    @Override
    public @Nullable String propertyName() {
        return this.propertyName.orElse(null);
    }

    @Override
    public MapCodec<? extends BoneMealBehavior> codec() {
        return CODEC;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        BlockPos headPos = this.getHeadPos(level, pos, state.getBlock(), this.direction);
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
        if (this.propertyName.isEmpty()) {
            return;
        }

        BlockState state = level.getBlockState(topPos);
        IntegerProperty ageProperty = this.getProperty(state);
        if (ageProperty != null) {
            state = state.setValue(ageProperty, 0);
            level.setBlockAndUpdate(topPos, state);
            state.updateNeighbourShapes(level, topPos, Block.UPDATE_ALL);
        }
    }

    private @Nullable IntegerProperty getProperty(BlockState state) {
        if (this.property == null || !state.hasProperty(this.property)) {
            return this.property = this.findProperty(state);
        } else {
            return this.property;
        }
    }
}
