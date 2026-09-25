package fuzs.universalbonemeal.common.world.level.block.bonemeal.behavior;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.state.BlockState;

public record RandomTickBehavior(IntProvider growthAttempts) implements BoneMealBehavior {
    public static final MapCodec<RandomTickBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    IntProviders.codec(1, 64).fieldOf("growth_attempts").forGetter(RandomTickBehavior::growthAttempts))
            .apply(instance, RandomTickBehavior::new));

    @Override
    public MapCodec<RandomTickBehavior> codec() {
        return CODEC;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        int growthAttempts = this.growthAttempts.sample(random);
        for (int attempt = 0; attempt < growthAttempts && level.getBlockState(pos) == state; attempt++) {
            state.randomTick(level, pos, random);
        }
    }
}
