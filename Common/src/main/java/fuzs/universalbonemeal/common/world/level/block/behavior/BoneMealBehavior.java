package fuzs.universalbonemeal.common.world.level.block.behavior;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BonemealableBlock;

public interface BoneMealBehavior extends BonemealableBlock {

    /**
     * @return the codec for this bone meal behavior type
     *
     * @see fuzs.universalbonemeal.common.init.ModRegistry#BONE_MEAL_BEHAVIOR_TYPE_REGISTRY
     */
    MapCodec<? extends BoneMealBehavior> codec();

    @Deprecated
    @Override
    default BlockPos getParticlePos(BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    default Type getType() {
        throw new UnsupportedOperationException();
    }
}
