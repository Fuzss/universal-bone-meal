package fuzs.universalbonemeal.common.world.level.block.bonemeal;

import com.mojang.serialization.Codec;
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
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;

import java.util.Optional;

public record ConditionalBoneMealBehavior(Holder<BoneMealBehavior> behavior,
                                          Optional<Holder<BlockPredicate>> targetPredicate,
                                          boolean replace) implements BonemealableBlock {
    public static final Codec<ConditionalBoneMealBehavior> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    BoneMealBehavior.CODEC.fieldOf("behavior").forGetter(ConditionalBoneMealBehavior::behavior),
                    ModRegistry.PREDICATE_CODEC.optionalFieldOf("target_predicate")
                            .forGetter(ConditionalBoneMealBehavior::targetPredicate),
                    Codec.BOOL.optionalFieldOf("replace", false).forGetter(ConditionalBoneMealBehavior::replace))
            .apply(instance, ConditionalBoneMealBehavior::new));

    public ConditionalBoneMealBehavior(Holder<BoneMealBehavior> behavior) {
        this(behavior, Optional.empty(), false);
    }

    public ConditionalBoneMealBehavior(Holder<BoneMealBehavior> behavior, Holder<BlockPredicate> targetPredicate) {
        this(behavior, Optional.of(targetPredicate), false);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, BonemealSource source) {
        return level instanceof LevelAccessor levelAccessor && (this.targetPredicate.isEmpty()
                || this.targetPredicate.get().value().test(levelAccessor, pos));
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
