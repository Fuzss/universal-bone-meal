package fuzs.universalbonemeal.common.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.universalbonemeal.common.init.ModRegistry;
import fuzs.universalbonemeal.common.world.level.block.bonemeal.ChorusPlantTraverser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChorusFlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;

public record ChorusPlantPredicate(HolderSet<Block> plant,
                                   HolderSet<Block> flower,
                                   int searchRange) implements BlockPredicate, ChorusPlantTraverser {
    public static final MapCodec<ChorusPlantPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    RegistryCodecs.holderSet(Registries.BLOCK).fieldOf("plant").forGetter(ChorusPlantPredicate::plant),
                    RegistryCodecs.holderSet(Registries.BLOCK).fieldOf("flower").forGetter(ChorusPlantPredicate::flower),
                    Codec.intRange(1, 256).fieldOf("search_range").forGetter(ChorusPlantPredicate::searchRange))
            .apply(instance, ChorusPlantPredicate::new));

    @Override
    public boolean test(LevelAccessor level, BlockPos pos) {
        for (BlockPos flowerPos : this.getFlowerPositions(level, pos)) {
            BlockState state = level.getBlockState(flowerPos);
            if (state.hasProperty(ChorusFlowerBlock.AGE)
                    && state.getValue(ChorusFlowerBlock.AGE) < ChorusFlowerBlock.DEAD_AGE) {
                return true;
            }
        }

        return false;
    }

    @Override
    public BlockPredicateType<?> type() {
        return ModRegistry.CHORUS_PLANT_BLOCK_PREDICATE_TYPE.value();
    }
}
