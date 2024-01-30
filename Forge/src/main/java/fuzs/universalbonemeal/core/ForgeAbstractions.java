package fuzs.universalbonemeal.core;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IPlantable;

public class ForgeAbstractions implements CommonAbstractions {

    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter level, BlockPos pos, Direction facing, Block plantable) {
        return state.canSustainPlant(level, pos, facing, (IPlantable) plantable);
    }
}
