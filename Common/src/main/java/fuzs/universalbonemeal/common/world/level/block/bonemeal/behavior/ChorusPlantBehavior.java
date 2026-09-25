package fuzs.universalbonemeal.common.world.level.block.bonemeal.behavior;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.universalbonemeal.common.world.level.block.bonemeal.ChorusPlantTraverser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.state.BlockState;

public record ChorusPlantBehavior(HolderSet<Block> plant,
                                  HolderSet<Block> flower,
                                  int searchRange) implements BoneMealBehavior, ChorusPlantTraverser {
    public static final MapCodec<ChorusPlantBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    RegistryCodecs.holderSet(Registries.BLOCK).fieldOf("plant").forGetter(ChorusPlantBehavior::plant),
                    RegistryCodecs.holderSet(Registries.BLOCK).fieldOf("flower").forGetter(ChorusPlantBehavior::flower),
                    Codec.intRange(1, 256).fieldOf("search_range").forGetter(ChorusPlantBehavior::searchRange))
            .apply(instance, ChorusPlantBehavior::new));

    @Override
    public MapCodec<ChorusPlantBehavior> codec() {
        return CODEC;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        for (BlockPos flowerPos : this.getFlowerPositions(level, pos)) {
            level.getBlockState(flowerPos).randomTick(level, flowerPos, random);
        }
    }
}
