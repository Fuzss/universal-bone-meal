package fuzs.universalbonemeal.common.world.level.block.behavior;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.ChorusFlowerBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ChorusFlowerBehavior implements BoneMealBehavior {
    public static final MapCodec<ChorusFlowerBehavior> CODEC = MapCodec.unit(ChorusFlowerBehavior::new);

    @Override
    public MapCodec<? extends BoneMealBehavior> codec() {
        return CODEC;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, BonemealSource source) {
        return state.hasProperty(ChorusFlowerBlock.AGE) && state.getValue(ChorusFlowerBlock.AGE) < 5;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        state.randomTick(level, pos, random);
    }
}
