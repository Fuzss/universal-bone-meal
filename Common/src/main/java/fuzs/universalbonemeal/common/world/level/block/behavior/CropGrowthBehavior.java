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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public record CropGrowthBehavior(int maxAge) implements BoneMealBehavior {
    public static final MapCodec<CropGrowthBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.intRange(1, 3).fieldOf("max_age").forGetter(CropGrowthBehavior::maxAge))
            .apply(instance, CropGrowthBehavior::new));

    @Override
    public MapCodec<CropGrowthBehavior> codec() {
        return CODEC;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, BonemealSource source) {
        return !this.isMaxAge(state);
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        this.growCrops(level, pos, state);
    }

    private void growCrops(Level level, BlockPos pos, BlockState state) {
        int age = this.getAge(state) + Mth.nextInt(level.getRandom(), 2, 5) / 3;
        if (age > this.maxAge) {
            age = this.maxAge;
        }

        level.setBlock(pos,
                state.getBlock().defaultBlockState().setValue(this.getAgeProperty(), age),
                Block.UPDATE_CLIENTS);
    }

    private int getAge(BlockState state) {
        return state.getValue(this.getAgeProperty());
    }

    private IntegerProperty getAgeProperty() {
        return NetherWartBlock.AGE;
    }

    private boolean isMaxAge(BlockState state) {
        return state.getValue(this.getAgeProperty()) >= this.maxAge;
    }
}
