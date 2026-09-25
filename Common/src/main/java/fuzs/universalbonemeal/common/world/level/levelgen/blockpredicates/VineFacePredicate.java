package fuzs.universalbonemeal.common.world.level.levelgen.blockpredicates;

import com.mojang.serialization.MapCodec;
import fuzs.universalbonemeal.common.init.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;

/**
 * @see VineBlock#getUpdatedState(BlockState, net.minecraft.world.level.BlockGetter, BlockPos)
 */
public record VineFacePredicate() implements BlockPredicate {
    public static final MapCodec<VineFacePredicate> CODEC = MapCodec.unit(VineFacePredicate::new);

    @Override
    public boolean test(LevelAccessor level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BooleanProperty property = VineBlock.getPropertyForFace(direction);
            if (state.hasProperty(property) && state.getValue(property)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public BlockPredicateType<?> type() {
        return ModRegistry.VINE_FACE_BLOCK_PREDICATE_TYPE.value();
    }
}
