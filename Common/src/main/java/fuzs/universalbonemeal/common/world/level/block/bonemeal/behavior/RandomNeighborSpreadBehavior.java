package fuzs.universalbonemeal.common.world.level.block.bonemeal.behavior;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

/**
 * @see net.minecraft.world.level.levelgen.feature.RandomNeighborSpreadFeature
 */
public record RandomNeighborSpreadBehavior(Holder<BlockStateProvider> block,
                                           IntProvider spreadWidth,
                                           IntProvider mostSuccesses) implements BoneMealBehavior {
    public static final MapCodec<RandomNeighborSpreadBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    BlockStateProvider.CODEC.fieldOf("block").forGetter(RandomNeighborSpreadBehavior::block),
                    IntProviders.codec(1, 16).fieldOf("spread_width").forGetter(RandomNeighborSpreadBehavior::spreadWidth),
                    IntProviders.codec(1, 16).fieldOf("most_successes").forGetter(RandomNeighborSpreadBehavior::mostSuccesses))
            .apply(instance, RandomNeighborSpreadBehavior::new));

    @Override
    public MapCodec<RandomNeighborSpreadBehavior> codec() {
        return CODEC;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        int spreadWidth = this.spreadWidth.sample(random);
        int mostSuccesses = this.mostSuccesses.sample(random);
        int successes = 0;
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int spread = (spreadWidth + 1) * 16 - 1; spread >= 0; spread--) {
            if (this.placeBlockAtOffset(level, random, mutablePos.set(pos), pos, spread)) {
                if (++successes >= mostSuccesses) {
                    return;
                }
            }
        }
    }

    private boolean placeBlockAtOffset(ServerLevel level, RandomSource random, BlockPos.MutableBlockPos offsetPos, BlockPos origin, int steps) {
        BlockState state = null;
        for (int step = 0; step < steps / 16; ++step) {
            offsetPos.move(random.nextInt(3) - 1,
                    (random.nextInt(3) - 1) * random.nextInt(3) / 2,
                    random.nextInt(3) - 1);
            state = this.block.value().getState(level, random, origin);
            if (!state.canSurvive(level, offsetPos) || level.getBlockState(offsetPos)
                    .isCollisionShapeFullBlock(level, offsetPos)) {
                return false;
            }
        }

        if (state != null && level.isEmptyBlock(offsetPos) && offsetPos.getY() > level.getMinY()) {
            level.setBlock(offsetPos, state, Block.UPDATE_CLIENTS);
            return true;
        } else {
            return false;
        }
    }
}
