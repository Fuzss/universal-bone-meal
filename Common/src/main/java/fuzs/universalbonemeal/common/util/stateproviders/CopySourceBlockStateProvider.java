package fuzs.universalbonemeal.common.util.stateproviders;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

/**
 * A {@link BlockStateProvider} that copies the block state of the neighbouring block in the given direction, which
 * allows e.g. hanging plants to keep the shape of the block they grow from.
 */
public record CopySourceBlockStateProvider(Direction direction) implements BlockStateProvider {
    public static final MapCodec<CopySourceBlockStateProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Direction.CODEC.fieldOf("direction").forGetter(CopySourceBlockStateProvider::direction))
            .apply(instance, CopySourceBlockStateProvider::new));

    @Override
    public MapCodec<CopySourceBlockStateProvider> codec() {
        return CODEC;
    }

    @Override
    public BlockState getState(LevelAccessor level, RandomSource random, BlockPos pos) {
        return level.getBlockState(pos.relative(this.direction));
    }
}
