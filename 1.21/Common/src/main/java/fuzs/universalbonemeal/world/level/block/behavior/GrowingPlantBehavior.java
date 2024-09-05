package fuzs.universalbonemeal.world.level.block.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public abstract class GrowingPlantBehavior implements BonemealBehavior {
    protected final Direction growthDirection;

    public GrowingPlantBehavior(Direction growthDirection) {
        this.growthDirection = growthDirection;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader p_53900_, BlockPos p_53901_, BlockState p_53902_) {
        BlockPos headPos = this.getHeadPos(p_53900_, p_53901_, p_53902_.getBlock());
        return this.canGrowInto(p_53900_.getBlockState(headPos.relative(this.growthDirection)));
    }

    @Override
    public boolean isBonemealSuccess(Level p_53944_, RandomSource p_53945_, BlockPos p_53946_, BlockState p_53947_) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos sourcePos, BlockState sourceState) {
        BlockPos topPos = this.getHeadPos(level, sourcePos, sourceState.getBlock());
        this.performBonemealTop(level, random, topPos, sourceState);
    }

    private void performBonemealTop(ServerLevel level, RandomSource p_53935_, BlockPos topPos, BlockState sourceState) {
        BlockPos blockpos = topPos.relative(this.growthDirection);
        int j = this.getBlocksToGrowWhenBonemealed(p_53935_);
        for(int k = 0; k < j && this.canGrowInto(level.getBlockState(blockpos)); ++k) {
            level.setBlockAndUpdate(blockpos, this.getGrownBlockState(sourceState.getBlock(), sourceState));
            blockpos = blockpos.relative(this.growthDirection);
        }
    }

    private BlockPos getHeadPos(BlockGetter p_153323_, BlockPos p_153324_, Block p_153325_) {
        return getTopConnectedBlock(p_153323_, p_153324_, p_153325_, this.growthDirection);
    }

    public static BlockPos getTopConnectedBlock(BlockGetter level, BlockPos pos, Block block, Direction direction) {
        BlockPos.MutableBlockPos mutable = pos.mutable();
        BlockState blockstate;
        do {
            mutable.move(direction);
            blockstate = level.getBlockState(mutable);
        } while(blockstate.is(block));
        return mutable.move(direction.getOpposite());
    }

    protected abstract int getBlocksToGrowWhenBonemealed(RandomSource random);

    protected abstract boolean canGrowInto(BlockState state);

    protected abstract BlockState getGrownBlockState(Block sourceBlock, BlockState sourceState);
}
