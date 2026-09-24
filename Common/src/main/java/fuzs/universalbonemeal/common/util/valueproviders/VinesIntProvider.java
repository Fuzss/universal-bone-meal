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
public record VinesIntProvider(int maxInclusive) implements IntProvider {
    public static final MapCodec<VinesIntProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Codec.intRange(
                    1,
                    128).optionalFieldOf("max_inclusive", 128).forGetter(VinesIntProvider::maxInclusive))
            .apply(instance, VinesIntProvider::new));

    @Override
    public int sample(RandomSource random) {
        return Math.min(NetherVines.getBlocksToGrowWhenBonemealed(random), this.maxInclusive);
    }

    @Override
    public int minInclusive() {
        // This is derived from the implementation of the vanilla method used for sampling.
        return 1;
    }

    @Override
    public int maxInclusive() {
        return this.maxInclusive;
    }

    @Override
    public MapCodec<VinesIntProvider> codec() {
        return CODEC;
    }
}
