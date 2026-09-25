package fuzs.universalbonemeal.common.world.level.levelgen.blockpredicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.universalbonemeal.common.init.ModRegistry;
import fuzs.universalbonemeal.common.world.level.block.bonemeal.GrowingPlantTraverser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;

public record GrowingPlantPredicate(Direction direction,
                                    IntProvider maxHeight,
                                    BlockPredicate canGrowInto) implements BlockPredicate, GrowingPlantTraverser {
    public static final MapCodec<GrowingPlantPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Direction.CODEC.optionalFieldOf("direction", Direction.UP).forGetter(GrowingPlantPredicate::direction),
            IntProviders.codec(1, 128).fieldOf("max_height").forGetter(GrowingPlantPredicate::maxHeight),
            BlockPredicate.CODEC.optionalFieldOf("can_grow_into", BlockPredicate.ONLY_IN_AIR_PREDICATE)
                    .forGetter(GrowingPlantPredicate::canGrowInto)).apply(instance, GrowingPlantPredicate::new));

    @Override
    public boolean test(LevelAccessor level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        if (this.getConnectedPlantHeight(level, pos, block, this.direction)
                < this.getMaxHeightAtPosition(this.maxHeight, pos)) {
            BlockPos targetPos = this.getHeadPos(level, pos, block, this.direction).relative(this.direction);
            return this.canGrowInto.test(level, targetPos);
        } else {
            return false;
        }
    }

    @Override
    public BlockPredicateType<?> type() {
        return ModRegistry.GROWING_PLANT_BLOCK_PREDICATE_TYPE.value();
    }
}
