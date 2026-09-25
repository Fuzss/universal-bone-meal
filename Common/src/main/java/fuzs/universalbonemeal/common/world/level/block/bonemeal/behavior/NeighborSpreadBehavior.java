package fuzs.universalbonemeal.common.world.level.block.bonemeal.behavior;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record NeighborSpreadBehavior(Holder<BlockStateProvider> vegetation,
                                     int spreadWidth,
                                     int mostSuccesses) implements BoneMealBehavior {
    public static final MapCodec<NeighborSpreadBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    BlockStateProvider.CODEC.fieldOf("vegetation").forGetter(NeighborSpreadBehavior::vegetation),
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
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int spread = (this.spreadWidth + 1) * 16 - 1; spread >= 0; spread--) {
            if (this.placeVegetationAtOffset(level, random, mutablePos.set(pos), pos, spread)) {
                if (++successes >= this.mostSuccesses) {
                    return;
                }
            }
        }
    }

    private boolean placeVegetationAtOffset(ServerLevel level, RandomSource random, BlockPos.MutableBlockPos offsetPos, BlockPos origin, int steps) {
        BlockState vegetationState = null;
        for (int step = 0; step < steps / 16; ++step) {
            offsetPos.move(random.nextInt(3) - 1,
                    (random.nextInt(3) - 1) * random.nextInt(3) / 2,
                    random.nextInt(3) - 1);
            vegetationState = this.vegetation.value().getState(level, random, origin);
            if (!vegetationState.canSurvive(level, offsetPos) || level.getBlockState(offsetPos)
                    .isCollisionShapeFullBlock(level, offsetPos)) {
                return false;
            }
        }

        if (vegetationState != null && level.isEmptyBlock(offsetPos) && offsetPos.getY() > level.getMinY()) {
            level.setBlock(offsetPos, vegetationState, Block.UPDATE_CLIENTS);
            return true;
        } else {
            return false;
        }
    }
}
