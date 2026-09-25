package fuzs.universalbonemeal.common.world.level.levelgen.blockpredicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.universalbonemeal.common.init.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;

/**
 * @see StemBlock#randomTick(BlockState, net.minecraft.server.level.ServerLevel, BlockPos, net.minecraft.util.RandomSource)
 */
public record FruitStemPredicate(HolderSet<Block> fruitSupportBlocks) implements BlockPredicate {
    public static final MapCodec<FruitStemPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    RegistryCodecs.holderSet(Registries.BLOCK)
                            .fieldOf("fruit_support_blocks")
                            .forGetter(FruitStemPredicate::fruitSupportBlocks))
            .apply(instance, FruitStemPredicate::new));

    @Override
    public boolean test(LevelAccessor level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        // Let vanilla run if this is not the case.
        if (!state.hasProperty(StemBlock.AGE) || state.getValue(StemBlock.AGE) < StemBlock.MAX_AGE) {
            return false;
        }

        // No need to check if attached to a fruit already, since attached stems are completely different block.
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos fruitPos = pos.relative(direction);
            BlockState soilState = level.getBlockState(fruitPos.below());
            if (level.getBlockState(fruitPos).isAir() && this.fruitSupportBlocks.contains(soilState.typeHolder())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public BlockPredicateType<?> type() {
        return ModRegistry.FRUIT_STEM_BLOCK_PREDICATE_TYPE.value();
    }
}
