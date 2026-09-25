package fuzs.universalbonemeal.common.world.level.block.behavior;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.universalbonemeal.common.UniversalBoneMeal;
import fuzs.universalbonemeal.common.init.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

public interface BoneMealBehavior extends BonemealableBlock {
    ResourceKey<Registry<BoneMealBehavior>> REGISTRY_KEY = ResourceKey.createRegistryKey(UniversalBoneMeal.id(
            "bone_meal_behavior"));
    Codec<BoneMealBehavior> DIRECT_CODEC = ModRegistry.BONE_MEAL_BEHAVIOR_TYPE_REGISTRY.byNameCodec()
            .dispatch(BoneMealBehavior::codec, Function.identity());
    Codec<Holder<BoneMealBehavior>> CODEC = RegistryCodecs.holder(REGISTRY_KEY, DIRECT_CODEC);

    /**
     * @return the codec for this bone meal behavior type
     *
     * @see fuzs.universalbonemeal.common.init.ModRegistry#BONE_MEAL_BEHAVIOR_TYPE_REGISTRY
     */
    MapCodec<? extends BoneMealBehavior> codec();

    @Override
    default boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        return true;
    }

    @Deprecated
    @Override
    default BlockPos getParticlePos(BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default Type getType() {
        throw new UnsupportedOperationException();
    }

    record Configured(Holder<BoneMealBehavior> behavior, boolean replace) {
        public static final Codec<Configured> SIMPLE_CODEC = BoneMealBehavior.CODEC.xmap(Configured::new,
                Configured::behavior);
        public static final Codec<Configured> CODEC = Codec.withAlternative(RecordCodecBuilder.create(instance -> instance.group(
                        BoneMealBehavior.CODEC.fieldOf("behavior").forGetter(Configured::behavior),
                        Codec.BOOL.optionalFieldOf("replace", false).forGetter(Configured::replace))
                .apply(instance, Configured::new)), SIMPLE_CODEC);

        public Configured(Holder<BoneMealBehavior> behavior) {
            this(behavior, false);
        }
    }
}
