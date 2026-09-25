package fuzs.universalbonemeal.common.world.level.block.bonemeal.conditional;

import com.mojang.serialization.Codec;
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

import java.util.List;
import java.util.Optional;

/**
 * Runs the first behavior whose predicate matches, falling back to another behavior otherwise.
 *
 * @see net.minecraft.world.level.levelgen.feature.stateproviders.RuleBasedStateProvider
 */
public record CombinedConditionalBoneMealBehavior(Optional<Holder<BoneMealBehavior>> fallback,
                                                  List<Condition> conditions) implements ConditionalBoneMealBehavior {
    public static final MapCodec<CombinedConditionalBoneMealBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    BoneMealBehavior.CODEC.optionalFieldOf("fallback").forGetter(CombinedConditionalBoneMealBehavior::fallback),
                    Condition.CODEC.listOf().fieldOf("conditions").forGetter(CombinedConditionalBoneMealBehavior::conditions))
            .apply(instance, CombinedConditionalBoneMealBehavior::new));

    @Override
    public MapCodec<CombinedConditionalBoneMealBehavior> codec() {
        return CODEC;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, BonemealSource source) {
        return this.select(level, pos).isPresent();
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        return this.select(level, pos).map((Holder<BoneMealBehavior> behavior) -> {
            return behavior.value().isBonemealSuccess(level, random, pos, state, source);
        }).orElse(false);
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        this.select(level, pos).ifPresent((Holder<BoneMealBehavior> behavior) -> {
            behavior.value().performBonemeal(level, random, pos, state, source);
        });
    }

    private Optional<Holder<BoneMealBehavior>> select(LevelReader level, BlockPos pos) {
        if (level instanceof LevelAccessor levelAccessor) {
            for (Condition condition : this.conditions) {
                if (condition.predicate().value().test(levelAccessor, pos)) {
                    return Optional.of(condition.behavior());
                }
            }
        }

        return this.fallback;
    }

    public record Condition(Holder<BlockPredicate> predicate, Holder<BoneMealBehavior> behavior) {
        public static final Codec<Condition> CODEC = RecordCodecBuilder.create(instance -> instance.group(ModRegistry.PREDICATE_CODEC.fieldOf(
                                "when").forGetter(Condition::predicate),
                        BoneMealBehavior.CODEC.fieldOf("behavior").forGetter(Condition::behavior))
                .apply(instance, Condition::new));
    }
}
