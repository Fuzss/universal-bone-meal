package fuzs.universalbonemeal.common.world.level.levelgen.blockpredicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.universalbonemeal.common.init.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.minecraft.world.level.levelgen.blockpredicates.StateTestingPredicate;

/**
 * @see VineBlock#getUpdatedState(BlockState, net.minecraft.world.level.BlockGetter, BlockPos)
 */
public final class VinePredicate extends StateTestingPredicate {
    public static final MapCodec<VinePredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> stateTestingCodec(
            instance).apply(instance, VinePredicate::new));

    public VinePredicate(Vec3i offset) {
        super(offset);
    }

    public VinePredicate() {
        this(Vec3i.ZERO);
    }

    @Override
    protected boolean test(BlockState state) {
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
        return ModRegistry.VINE_BLOCK_PREDICATE_TYPE.value();
    }
}
