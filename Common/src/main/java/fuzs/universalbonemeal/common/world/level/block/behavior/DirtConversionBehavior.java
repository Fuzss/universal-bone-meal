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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.stream.Stream;

public record DirtConversionBehavior(HolderSet<Block> spreadSources, int searchRange) implements BoneMealBehavior {
    public static final MapCodec<DirtConversionBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    RegistryCodecs.holderSet(Registries.BLOCK)
                            .fieldOf("spread_sources")
                            .forGetter(DirtConversionBehavior::spreadSources),
                    Codec.intRange(1, 64).fieldOf("search_range").forGetter(DirtConversionBehavior::searchRange))
            .apply(instance, DirtConversionBehavior::new));

    @Override
    public MapCodec<DirtConversionBehavior> codec() {
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
            Block block = foundBlocks.get(random.nextInt(foundBlocks.size()));
            level.setBlock(pos, block.defaultBlockState(), Block.UPDATE_ALL);
        }
    }

    private Stream<Block> findSpreadSources(BlockGetter level, BlockPos pos) {
        return BlockPos.betweenClosedStream(pos.offset(-this.searchRange, -this.searchRange, -this.searchRange),
                        pos.offset(this.searchRange, this.searchRange, this.searchRange))
                .map(level::getBlockState)
                .filter(state -> this.spreadSources.contains(state.typeHolder()))
                .map(BlockState::getBlock)
                .distinct();
    }
}
