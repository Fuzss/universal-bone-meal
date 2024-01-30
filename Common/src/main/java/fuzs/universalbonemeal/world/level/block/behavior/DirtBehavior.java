package fuzs.universalbonemeal.world.level.block.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public class DirtBehavior implements BonemealBehavior {

    @Override
    public boolean isValidBonemealTarget(LevelReader p_55002_, BlockPos p_55003_, BlockState p_55004_, boolean p_55005_) {
        if (p_55002_.getBlockState(p_55003_.above()).propagatesSkylightDown(p_55002_, p_55003_)) {
            for (BlockPos blockpos : BlockPos.betweenClosed(p_55003_.offset(-1, -1, -1), p_55003_.offset(1, 1, 1))) {
                BlockState state = p_55002_.getBlockState(blockpos);
                if (state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.MYCELIUM)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isBonemealSuccess(Level p_55007_, Random p_55008_, BlockPos p_55009_, BlockState p_55010_) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel p_54997_, Random p_54998_, BlockPos p_54999_, BlockState p_55000_) {
        boolean flag = false;
        boolean flag1 = false;
        for(BlockPos blockpos : BlockPos.betweenClosed(p_54999_.offset(-1, -1, -1), p_54999_.offset(1, 1, 1))) {
            BlockState blockstate = p_54997_.getBlockState(blockpos);
            if (blockstate.is(Blocks.GRASS_BLOCK)) {
                flag1 = true;
            }
            if (blockstate.is(Blocks.MYCELIUM)) {
                flag = true;
            }

            if (flag1 && flag) {
                break;
            }
        }
        if (flag1 && flag) {
            p_54997_.setBlock(p_54999_, p_54998_.nextBoolean() ? Blocks.GRASS_BLOCK.defaultBlockState() : Blocks.MYCELIUM.defaultBlockState(), 3);
        } else if (flag1) {
            p_54997_.setBlock(p_54999_, Blocks.GRASS_BLOCK.defaultBlockState(), 3);
        } else if (flag) {
            p_54997_.setBlock(p_54999_, Blocks.MYCELIUM.defaultBlockState(), 3);
        }
    }
}
