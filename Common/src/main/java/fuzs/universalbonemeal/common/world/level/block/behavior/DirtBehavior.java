package fuzs.universalbonemeal.common.world.level.block.behavior;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public record DirtBehavior(HolderSet<Block> spreadSources) implements BoneMealBehavior {
    public static final MapCodec<DirtBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    RegistryCodecs.holderSet(Registries.BLOCK)
                            .fieldOf("spread_sources")
                            .forGetter(DirtBehavior::spreadSources))
            .apply(instance, DirtBehavior::new));

    @Override
    public MapCodec<DirtBehavior> codec() {
        return CODEC;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos blockPos, BlockState blockState, BonemealSource bonemealSource) {
        if (level.getBlockState(blockPos.above()).propagatesSkylightDown()) {
            for (BlockPos blockpos : BlockPos.betweenClosed(blockPos.offset(-1, -1, -1), blockPos.offset(1, 1, 1))) {
                BlockState state = level.getBlockState(blockpos);
                if (this.spreadSources.contains(state.typeHolder())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos blockPos, BlockState blockState, BonemealSource bonemealSource) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos blockPos, BlockState blockState, BonemealSource bonemealSource) {
        List<Block> foundBlocks = new ArrayList<>();
        for (BlockPos blockpos : BlockPos.betweenClosed(blockPos.offset(-1, -1, -1), blockPos.offset(1, 1, 1))) {
            BlockState state = level.getBlockState(blockpos);
            Block block = state.getBlock();
            if (this.spreadSources.contains(state.typeHolder()) && !foundBlocks.contains(block)) {
                foundBlocks.add(block);
            }
        }

        if (!foundBlocks.isEmpty()) {
            Block block = foundBlocks.get(random.nextInt(foundBlocks.size()));
            level.setBlock(blockPos, block.defaultBlockState(), 3);
        }
    }
}
