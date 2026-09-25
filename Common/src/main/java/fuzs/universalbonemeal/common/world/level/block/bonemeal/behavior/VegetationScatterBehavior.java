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

public record VegetationScatterBehavior(Holder<BlockStateProvider> vegetation,
                                        int spreadWidth,
                                        int spreadHeight) implements BoneMealBehavior {
    public static final MapCodec<VegetationScatterBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    BlockStateProvider.CODEC.fieldOf("vegetation").forGetter(VegetationScatterBehavior::vegetation),
                    Codec.intRange(1, 16).fieldOf("spread_width").forGetter(VegetationScatterBehavior::spreadWidth),
                    Codec.intRange(0, 16).fieldOf("spread_height").forGetter(VegetationScatterBehavior::spreadHeight))
            .apply(instance, VegetationScatterBehavior::new));

    @Override
    public MapCodec<VegetationScatterBehavior> codec() {
        return CODEC;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        BlockPos origin = pos.above();
        if (origin.getY() >= level.getMinY() + 1 && origin.getY() + 1 < level.getMaxY()) {
            for (int i = 0; i < this.spreadWidth * this.spreadWidth; ++i) {
                BlockPos offsetPos = this.getOffsetPos(random, origin);
                BlockState vegetationState = this.vegetation.value().getState(level, random, offsetPos);
                if (level.isEmptyBlock(offsetPos) && offsetPos.getY() > level.getMinY() && vegetationState.canSurvive(
                        level,
                        offsetPos)) {
                    level.setBlock(offsetPos, vegetationState, Block.UPDATE_CLIENTS);
                }
            }
        }
    }

    private BlockPos getOffsetPos(RandomSource random, BlockPos pos) {
        return pos.offset(random.nextInt(this.spreadWidth) - random.nextInt(this.spreadWidth),
                random.nextInt(this.spreadHeight) - random.nextInt(this.spreadHeight),
                random.nextInt(this.spreadWidth) - random.nextInt(this.spreadWidth));
    }
}
