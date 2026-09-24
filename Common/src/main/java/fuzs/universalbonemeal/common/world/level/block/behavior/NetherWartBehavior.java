package fuzs.universalbonemeal.common.world.level.block.behavior;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public record NetherWartBehavior(int maxAge) implements BoneMealBehavior {
    public static final MapCodec<NetherWartBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.intRange(1, 3).fieldOf("max_age").forGetter(NetherWartBehavior::maxAge))
            .apply(instance, NetherWartBehavior::new));

    @Override
    public MapCodec<NetherWartBehavior> codec() {
        return CODEC;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos blockPos, BlockState blockState, BonemealSource bonemealSource) {
        return !this.isMaxAge(blockState);
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource randomSource, BlockPos blockPos, BlockState blockState, BonemealSource bonemealSource) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos blockPos, BlockState blockState, BonemealSource bonemealSource) {
        this.growCrops(level, blockPos, blockState);
    }

    public void growCrops(Level level, BlockPos blockPos, BlockState blockState) {
        int i = this.getAge(blockState) + this.getBoneMealAgeIncrease(level);
        if (i > this.maxAge) {
            i = this.maxAge;
        }

        level.setBlock(blockPos, this.getStateForAge(blockState, i), 2);
    }

    public BlockState getStateForAge(BlockState blockState, int plantAge) {
        return blockState.getBlock().defaultBlockState().setValue(this.getAgeProperty(), plantAge);
    }

    private int getBoneMealAgeIncrease(Level level) {
        return Mth.nextInt(level.getRandom(), 2, 5) / 3;
    }

    private int getAge(BlockState blockState) {
        return blockState.getValue(this.getAgeProperty());
    }

    public IntegerProperty getAgeProperty() {
        return NetherWartBlock.AGE;
    }

    public boolean isMaxAge(BlockState blockState) {
        return blockState.getValue(this.getAgeProperty()) >= this.maxAge;
    }
}
