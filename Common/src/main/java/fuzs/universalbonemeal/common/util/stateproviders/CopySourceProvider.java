package fuzs.universalbonemeal.common.util.stateproviders;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

/**
 * A {@link BlockStateProvider} that copies the block state of the neighboring block in the given direction, which
 * allows e.g. hanging plants to keep the shape of the block they grow from.
 */
public record CopySourceProvider() implements BlockStateProvider {
    public static final MapCodec<CopySourceProvider> CODEC = MapCodec.unit(CopySourceProvider::new);

    @Override
    public MapCodec<CopySourceProvider> codec() {
        return CODEC;
    }

    @Override
    public BlockState getState(LevelAccessor level, RandomSource random, BlockPos pos) {
        return level.getBlockState(pos);
    }
}
