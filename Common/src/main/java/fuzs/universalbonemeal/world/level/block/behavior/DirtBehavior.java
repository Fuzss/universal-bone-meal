package fuzs.universalbonemeal.world.level.block.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class DirtBehavior implements BoneMealBehavior {

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos blockPos, BlockState blockState) {
        if (level.getBlockState(blockPos.above()).propagatesSkylightDown()) {
            for (BlockPos blockpos : BlockPos.betweenClosed(blockPos.offset(-1, -1, -1), blockPos.offset(1, 1, 1))) {
                BlockState state = level.getBlockState(blockpos);
                if (state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.MYCELIUM)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos blockPos, BlockState blockState) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos blockPos, BlockState blockState) {
        boolean foundMyceliumBlock = false;
        boolean foundGrassBlock = false;
        for(BlockPos blockpos : BlockPos.betweenClosed(blockPos.offset(-1, -1, -1), blockPos.offset(1, 1, 1))) {
            BlockState blockstate = level.getBlockState(blockpos);
            if (blockstate.is(Blocks.GRASS_BLOCK)) {
                foundGrassBlock = true;
            }
            if (blockstate.is(Blocks.MYCELIUM)) {
                foundMyceliumBlock = true;
            }

            if (foundGrassBlock && foundMyceliumBlock) {
                break;
            }
        }
        if (foundGrassBlock && foundMyceliumBlock) {
            level.setBlock(blockPos, random.nextBoolean() ? Blocks.GRASS_BLOCK.defaultBlockState() : Blocks.MYCELIUM.defaultBlockState(), 3);
        } else if (foundGrassBlock) {
            level.setBlock(blockPos, Blocks.GRASS_BLOCK.defaultBlockState(), 3);
        } else if (foundMyceliumBlock) {
            level.setBlock(blockPos, Blocks.MYCELIUM.defaultBlockState(), 3);
        }
    }
}
