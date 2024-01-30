package fuzs.universalbonemeal.world.level.block.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import java.util.Random;

public class NetherWartBehavior implements BonemealBehavior {

    @Override
    public boolean isValidBonemealTarget(LevelReader p_52258_, BlockPos p_52259_, BlockState p_52260_, boolean p_52261_) {
        return !this.isMaxAge(p_52260_);
    }

    @Override
    public boolean isBonemealSuccess(Level p_52268_, Random p_52269_, BlockPos p_52270_, BlockState p_52271_) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel p_52249_, Random p_52250_, BlockPos p_52251_, BlockState p_52252_) {
        this.growCrops(p_52249_, p_52251_, p_52252_);
    }

    public void growCrops(Level p_52264_, BlockPos p_52265_, BlockState p_52266_) {
        int i = this.getAge(p_52266_) + this.getBonemealAgeIncrease(p_52264_);
        int j = this.getMaxAge();
        if (i > j) {
            i = j;
        }

        p_52264_.setBlock(p_52265_, this.getStateForAge(p_52266_, i), 2);
    }

    public BlockState getStateForAge(BlockState p_52266_, int p_52290_) {
        return p_52266_.getBlock().defaultBlockState().setValue(this.getAgeProperty(), p_52290_);
    }

    protected int getBonemealAgeIncrease(Level p_52262_) {
        return Mth.nextInt(p_52262_.random, 2, 5) / 3;
    }

    protected int getAge(BlockState p_52306_) {
        return p_52306_.getValue(this.getAgeProperty());
    }

    public IntegerProperty getAgeProperty() {
        return NetherWartBlock.AGE;
    }

    public int getMaxAge() {
        return 3;
    }

    public boolean isMaxAge(BlockState p_52308_) {
        return p_52308_.getValue(this.getAgeProperty()) >= this.getMaxAge();
    }
}
