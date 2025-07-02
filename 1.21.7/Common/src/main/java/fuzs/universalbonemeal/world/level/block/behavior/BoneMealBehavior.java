package fuzs.universalbonemeal.world.level.block.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BonemealableBlock;

public interface BoneMealBehavior extends BonemealableBlock {

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
