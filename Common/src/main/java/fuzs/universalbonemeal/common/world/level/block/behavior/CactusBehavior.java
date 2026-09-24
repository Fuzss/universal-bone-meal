package fuzs.universalbonemeal.common.world.level.block.behavior;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class CactusBehavior extends SimpleGrowingPlantBehavior {
    public static final MapCodec<CactusBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    IntProviders.codec(0, 128)
                            .fieldOf("blocks_to_grow")
                            .forGetter(SimpleGrowingPlantBehavior::getBlocksToGrow),
                    BlockStateProvider.CODEC.fieldOf("flower").forGetter(CactusBehavior::getFlower),
                    Codec.floatRange(0.0F, 1.0F).fieldOf("flower_chance").forGetter(CactusBehavior::getFlowerChance))
            .apply(instance, CactusBehavior::new));

    private final Holder<BlockStateProvider> flower;
    private final float flowerChance;

    public CactusBehavior(IntProvider blocksToGrow, Holder<BlockStateProvider> flower, float flowerChance) {
        super(blocksToGrow);
        this.flower = flower;
        this.flowerChance = flowerChance;
    }

    public Holder<BlockStateProvider> getFlower() {
        return this.flower;
    }

    public float getFlowerChance() {
        return this.flowerChance;
    }

    @Override
    public MapCodec<CactusBehavior> codec() {
        return CODEC;
    }

    @Override
    protected BlockState getGrownBlockState(BlockState sourceState, RandomSource randomSource, ServerLevel level, BlockPos pos) {
        return randomSource.nextDouble() < this.flowerChance ? this.flower.value()
                .getState(level, randomSource, pos) : super.getGrownBlockState(sourceState,
                randomSource,
                level,
                pos);
    }
}
