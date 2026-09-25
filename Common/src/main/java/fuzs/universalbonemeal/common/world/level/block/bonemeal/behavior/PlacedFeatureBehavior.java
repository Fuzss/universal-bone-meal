package fuzs.universalbonemeal.common.world.level.block.bonemeal.behavior;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/**
 * @see net.minecraft.world.level.block.BonemealableFeaturePlacerBlock
 */
public record PlacedFeatureBehavior(ResourceKey<PlacedFeature> placedFeature,
                                    float successChance) implements BoneMealBehavior {
    public static final MapCodec<PlacedFeatureBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    ResourceKey.codec(Registries.PLACED_FEATURE)
                            .fieldOf("placed_feature")
                            .forGetter(PlacedFeatureBehavior::placedFeature),
                    Codec.floatRange(0.0F, 1.0F).fieldOf("success_chance").forGetter(PlacedFeatureBehavior::successChance))
            .apply(instance, PlacedFeatureBehavior::new));

    @Override
    public MapCodec<PlacedFeatureBehavior> codec() {
        return CODEC;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        return random.nextFloat() < this.successChance;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        Holder<PlacedFeature> placedFeature = level.registryAccess().getOrThrow(this.placedFeature);
        level.removeBlock(pos, false);
        if (!placedFeature.value().place(level, level.getChunkSource().getGenerator(), random, pos)) {
            level.setBlock(pos, state, Block.UPDATE_ALL);
        }
    }
}
