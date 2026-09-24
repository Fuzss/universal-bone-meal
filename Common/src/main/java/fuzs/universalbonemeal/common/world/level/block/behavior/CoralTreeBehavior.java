package fuzs.universalbonemeal.common.world.level.block.behavior;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public record CoralTreeBehavior(ResourceKey<PlacedFeature> feature,
                                HolderSet<Biome> biomes,
                                float successChance) implements BoneMealBehavior {
    public static final MapCodec<CoralTreeBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    ResourceKey.codec(Registries.PLACED_FEATURE)
                            .fieldOf("feature")
                            .forGetter(CoralTreeBehavior::feature),
                    RegistryCodecs.holderSet(Registries.BIOME).fieldOf("biomes").forGetter(CoralTreeBehavior::biomes),
                    Codec.floatRange(0.0F, 1.0F).fieldOf("success_chance").forGetter(CoralTreeBehavior::successChance))
            .apply(instance, CoralTreeBehavior::new));

    @Override
    public MapCodec<CoralTreeBehavior> codec() {
        return CODEC;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, BonemealSource source) {
        return this.biomes.contains(level.getBiome(pos));
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        return random.nextFloat() < this.successChance;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        PlacedFeature placedFeature = level.registryAccess()
                .lookupOrThrow(Registries.PLACED_FEATURE)
                .getValueOrThrow(this.feature);
        level.removeBlock(pos, false);
        if (!placedFeature.place(level, level.getChunkSource().getGenerator(), random, pos)) {
            level.setBlock(pos, state, Block.UPDATE_ALL);
        }
    }
}
