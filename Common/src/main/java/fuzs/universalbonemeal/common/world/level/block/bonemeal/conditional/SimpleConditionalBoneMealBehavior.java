package fuzs.universalbonemeal.common.world.level.block.bonemeal.conditional;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.universalbonemeal.common.init.ModRegistry;
import fuzs.universalbonemeal.common.world.level.block.bonemeal.behavior.BoneMealBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;

import java.util.Optional;

/**
 * Runs a single {@link BoneMealBehavior}, optionally gated behind a target predicate which decides bone meal validity.
 */
public record SimpleConditionalBoneMealBehavior(Holder<BoneMealBehavior> behavior,
                                                Optional<Holder<BlockPredicate>> predicate) implements ConditionalBoneMealBehavior {
    public static final MapCodec<SimpleConditionalBoneMealBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    BoneMealBehavior.CODEC.fieldOf("behavior").forGetter(SimpleConditionalBoneMealBehavior::behavior),
                    ModRegistry.PREDICATE_CODEC.optionalFieldOf("when").forGetter(SimpleConditionalBoneMealBehavior::predicate))
            .apply(instance, SimpleConditionalBoneMealBehavior::new));

    public SimpleConditionalBoneMealBehavior(Holder<BoneMealBehavior> behavior) {
        this(behavior, Optional.empty());
    }

    public SimpleConditionalBoneMealBehavior(Holder<BoneMealBehavior> behavior, Holder<BlockPredicate> targetPredicate) {
        this(behavior, Optional.of(targetPredicate));
    }

    @Override
    public MapCodec<SimpleConditionalBoneMealBehavior> codec() {
        return CODEC;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, BonemealSource source) {
        return level instanceof LevelAccessor levelAccessor && (this.predicate.isEmpty() || this.predicate.get()
                .value()
                .test(levelAccessor, pos));
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        return this.behavior.value().isBonemealSuccess(level, random, pos, state, source);
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        this.behavior.value().performBonemeal(level, random, pos, state, source);
    }
}
