package fuzs.universalbonemeal.common.world.level.block.behavior;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

public interface BoneMealBehavior extends BonemealableBlock {
    /**
     * @return the codec for this bone meal behavior type
     *
     * @see fuzs.universalbonemeal.common.init.ModRegistry#BONE_MEAL_BEHAVIOR_TYPE_REGISTRY
     */
    MapCodec<? extends BoneMealBehavior> codec();

    @Override
    default boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        return true;
    }

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
