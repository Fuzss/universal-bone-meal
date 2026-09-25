package fuzs.universalbonemeal.common.world.level.block.bonemeal.behavior;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.universalbonemeal.common.world.level.block.bonemeal.SpreadSourcesCollector;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public record NeighborConversionBehavior(HolderSet<Block> spreadSources,
                                         int spreadWidth,
                                         int spreadHeight) implements BoneMealBehavior, SpreadSourcesCollector {
    public static final MapCodec<NeighborConversionBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    RegistryCodecs.holderSet(Registries.BLOCK)
                            .fieldOf("spread_sources")
                            .forGetter(NeighborConversionBehavior::spreadSources),
                    Codec.intRange(1, 16).fieldOf("spread_width").forGetter(NeighborConversionBehavior::spreadWidth),
                    Codec.intRange(0, 16).fieldOf("spread_height").forGetter(NeighborConversionBehavior::spreadHeight))
            .apply(instance, NeighborConversionBehavior::new));

    @Override
    public MapCodec<NeighborConversionBehavior> codec() {
        return CODEC;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        List<Block> foundBlocks = this.findSpreadSources(level, pos).toList();
        if (!foundBlocks.isEmpty()) {
            Block block = Util.getRandom(foundBlocks, random);
            level.setBlock(pos, block.defaultBlockState(), Block.UPDATE_ALL);
        }
    }
}
