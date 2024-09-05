package fuzs.universalbonemeal.fabric.core;

import fuzs.universalbonemeal.core.CommonAbstractions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class FabricAbstractions implements CommonAbstractions {

    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter level, BlockPos pos, Direction facing, Block plantable) {
        return false;
    }
}
