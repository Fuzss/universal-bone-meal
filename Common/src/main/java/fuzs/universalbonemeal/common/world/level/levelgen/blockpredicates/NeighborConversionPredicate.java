package fuzs.universalbonemeal.common.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.universalbonemeal.common.init.ModRegistry;
import fuzs.universalbonemeal.common.world.level.block.bonemeal.SpreadSourcesCollector;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;

public record NeighborConversionPredicate(HolderSet<Block> spreadSources,
                                          int spreadWidth,
                                          int spreadHeight) implements BlockPredicate, SpreadSourcesCollector {
    public static final MapCodec<NeighborConversionPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    RegistryCodecs.holderSet(Registries.BLOCK)
                            .fieldOf("spread_sources")
                            .forGetter(NeighborConversionPredicate::spreadSources),
                    Codec.intRange(1, 16).fieldOf("spread_width").forGetter(NeighborConversionPredicate::spreadWidth),
                    Codec.intRange(0, 16).fieldOf("spread_height").forGetter(NeighborConversionPredicate::spreadHeight))
            .apply(instance, NeighborConversionPredicate::new));

    @Override
    public boolean test(LevelAccessor level, BlockPos pos) {
        if (level.getBlockState(pos.above()).propagatesSkylightDown()) {
            return this.findSpreadSources(level, pos).findAny().isPresent();
        } else {
            return false;
        }
    }

    @Override
    public BlockPredicateType<?> type() {
        return ModRegistry.NEIGHBOR_CONVERSION_BLOCK_PREDICATE_TYPE.value();
    }
}
