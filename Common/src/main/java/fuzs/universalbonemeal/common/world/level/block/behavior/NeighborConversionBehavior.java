package fuzs.universalbonemeal.common.world.level.block.behavior;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.stream.Stream;

public record NeighborConversionBehavior(HolderSet<Block> spreadSources,
                                         int spreadWidth,
                                         int spreadHeight) implements BoneMealBehavior {
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
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, BonemealSource source) {
        if (level.getBlockState(pos.above()).propagatesSkylightDown()) {
            return this.findSpreadSources(level, pos).findAny().isPresent();
        }
        return false;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        List<Block> foundBlocks = this.findSpreadSources(level, pos).toList();
        if (!foundBlocks.isEmpty()) {
            Block block = Util.getRandom(foundBlocks, random);
            level.setBlock(pos, block.defaultBlockState(), Block.UPDATE_ALL);
        }
    }

    private Stream<Block> findSpreadSources(BlockGetter level, BlockPos pos) {
        return BlockPos.betweenClosedStream(pos.offset(-this.spreadWidth, -this.spreadHeight, -this.spreadWidth),
                        pos.offset(this.spreadWidth, this.spreadHeight, this.spreadWidth))
                .map(level::getBlockState)
                .filter((BlockState state) -> this.spreadSources.contains(state.typeHolder()))
                .map(BlockState::getBlock)
                .distinct();
    }
}
