package fuzs.universalbonemeal.common.world.level.block.behavior;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;

public class HangingPlantBehavior extends AbstractGrowingPlantBehavior {
    public static final MapCodec<HangingPlantBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    IntProviders.codec(0, 128)
                            .fieldOf("blocks_to_grow")
                            .forGetter(AbstractGrowingPlantBehavior::getBlocksToGrow))
            .apply(instance, HangingPlantBehavior::new));

    public HangingPlantBehavior(IntProvider blocksToGrow) {
        super(blocksToGrow);
    }

    @Override
    public MapCodec<HangingPlantBehavior> codec() {
        return CODEC;
    }

    @Override
    protected Direction getGrowthDirection() {
        return Direction.DOWN;
    }

    @Override
    protected BlockState getGrownBlockState(BlockState sourceState, RandomSource random, ServerLevel level, BlockPos pos) {
        return sourceState.setValue(VineBlock.UP, false);
    }
}
