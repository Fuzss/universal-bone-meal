package fuzs.universalbonemeal.common.world.level.block.behavior;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
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
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, BonemealSource source) {
        return level.getBlockState(pos.above()).isAir();
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        BlockPos origin = pos.above();
        if (origin.getY() >= level.getMinY() + 1 && origin.getY() + 1 < level.getMaxY()) {
            for (int i = 0; i < this.spreadWidth * this.spreadWidth; ++i) {
                BlockPos randomPos = origin.offset(random.nextInt(this.spreadWidth) - random.nextInt(this.spreadWidth),
                        random.nextInt(this.spreadHeight) - random.nextInt(this.spreadHeight),
                        random.nextInt(this.spreadWidth) - random.nextInt(this.spreadWidth));
                BlockState vegetationState = this.vegetation.value().getState(level, random, randomPos);
                if (level.isEmptyBlock(randomPos) && randomPos.getY() > level.getMinY() && vegetationState.canSurvive(
                        level,
                        randomPos)) {
                    level.setBlock(randomPos, vegetationState, Block.UPDATE_CLIENTS);
                }
            }
        }
    }
}
