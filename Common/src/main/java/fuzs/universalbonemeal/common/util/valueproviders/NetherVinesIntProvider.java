package fuzs.universalbonemeal.common.util.valueproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.NetherVines;

/**
 * An {@link IntProvider} that reproduces the decreasing probability distribution vanilla uses when bone mealing nether
 * vines, capped at {@link #maxInclusive}.
 */
public record NetherVinesIntProvider(int maxInclusive) implements IntProvider {
    public static final MapCodec<NetherVinesIntProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.intRange(1, 128)
                    .optionalFieldOf("max_inclusive", 128)
                    .forGetter(NetherVinesIntProvider::maxInclusive)).apply(instance, NetherVinesIntProvider::new));

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
    public MapCodec<NetherVinesIntProvider> codec() {
        return CODEC;
    }
}
