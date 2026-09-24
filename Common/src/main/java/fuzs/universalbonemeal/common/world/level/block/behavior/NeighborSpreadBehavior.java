package fuzs.universalbonemeal.common.world.level.block.behavior;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.state.BlockState;

public record NeighborSpreadBehavior(int spreadWidth, int mostSuccesses) implements BoneMealBehavior {
    public static final MapCodec<NeighborSpreadBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.intRange(1, 16).fieldOf("spread_width").forGetter(NeighborSpreadBehavior::spreadWidth),
                    Codec.intRange(1, 16).fieldOf("most_successes").forGetter(NeighborSpreadBehavior::mostSuccesses))
            .apply(instance, NeighborSpreadBehavior::new));

    @Override
    public MapCodec<NeighborSpreadBehavior> codec() {
        return CODEC;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        int successes = 0;
        BlockState blockState = state.getBlock().defaultBlockState();
        label:
        for (int i = (this.spreadWidth + 1) * 16 - 1; i >= 0; i--) {
            BlockPos randomPos = pos;
            for (int j = 0; j < i / 16; ++j) {
                randomPos = randomPos.offset(random.nextInt(3) - 1,
                        (random.nextInt(3) - 1) * random.nextInt(3) / 2,
                        random.nextInt(3) - 1);
                if (!blockState.canSurvive(level, randomPos) || level.getBlockState(randomPos)
                        .isCollisionShapeFullBlock(level, randomPos)) {
                    continue label;
                }
            }

            if (level.isEmptyBlock(randomPos) && randomPos.getY() > level.getMinY()) {
                level.setBlock(randomPos, blockState, Block.UPDATE_CLIENTS);
                if (++successes >= this.mostSuccesses) {
                    return;
                }
            }
        }
    }
}
