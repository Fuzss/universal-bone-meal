package fuzs.universalbonemeal.common.util.valueproviders;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.NetherVines;

/**
 * An {@link IntProvider} that reproduces the geometric distribution vanilla uses when bone mealing nether vines.
 */
public record NetherVinesIntProvider() implements IntProvider {
    public static final MapCodec<NetherVinesIntProvider> CODEC = MapCodec.unit(NetherVinesIntProvider::new);
    public static final NetherVinesIntProvider INSTANCE = new NetherVinesIntProvider();

    @Override
    public int sample(RandomSource random) {
        return NetherVines.getBlocksToGrowWhenBonemealed(random);
    }

    @Override
    public int minInclusive() {
        return 0;
    }

    @Override
    public int maxInclusive() {
        return 128;
    }

    @Override
    public MapCodec<NetherVinesIntProvider> codec() {
        return CODEC;
    }
}
