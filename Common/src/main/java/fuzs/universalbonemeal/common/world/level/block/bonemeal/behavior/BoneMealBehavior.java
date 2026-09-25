package fuzs.universalbonemeal.common.world.level.block.bonemeal.behavior;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import fuzs.universalbonemeal.common.UniversalBoneMeal;
import fuzs.universalbonemeal.common.init.ModRegistry;
import fuzs.universalbonemeal.common.world.level.block.bonemeal.conditional.ConditionalBoneMealBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;

import java.util.function.Function;

/**
 * A bone meal action. Bone meal validity is not part of the action itself, but is decided by a {@link BlockPredicate}
 * referenced alongside the action in {@link ConditionalBoneMealBehavior}.
 */
public interface BoneMealBehavior {
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

    /**
     * @see net.minecraft.world.level.block.BonemealableBlock#isBonemealSuccess(Level, RandomSource, BlockPos,
     *         BlockState, BonemealSource)
     */
    default boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        return true;
    }

    /**
     * @see net.minecraft.world.level.block.BonemealableBlock#performBonemeal(ServerLevel, RandomSource, BlockPos,
     *         BlockState, BonemealSource)
     */
    void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source);
}
