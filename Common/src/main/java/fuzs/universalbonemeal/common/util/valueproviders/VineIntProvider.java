package fuzs.universalbonemeal.common.util.valueproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.NetherVines;

/**
 * @see NetherVines#getBlocksToGrowWhenBonemealed(RandomSource)
 */
public record VineIntProvider(int maxInclusive) implements IntProvider {
    /**
     * This is derived from the implementation of the vanilla method used for sampling.
     */
    private static final int MIN_INCLUSIVE = 1;
    private static final int MAX_INCLUSIVE = 128;
    public static final MapCodec<VineIntProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Codec.intRange(
                    MIN_INCLUSIVE,
                    MAX_INCLUSIVE).optionalFieldOf("max_inclusive", MAX_INCLUSIVE).forGetter(VineIntProvider::maxInclusive))
            .apply(instance, VineIntProvider::new));

    @Override
    public int sample(RandomSource random) {
        return Math.min(NetherVines.getBlocksToGrowWhenBonemealed(random), this.maxInclusive);
    }

    @Override
    public int minInclusive() {
        return MIN_INCLUSIVE;
    }

    @Override
    public int maxInclusive() {
        return this.maxInclusive;
    }

    @Override
    public MapCodec<VineIntProvider> codec() {
        return CODEC;
    }
}
