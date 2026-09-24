package fuzs.universalbonemeal.common.world.level.block.behavior;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.ChorusFlowerBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ChorusFlowerBehavior implements BoneMealBehavior {
    public static final MapCodec<ChorusFlowerBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.intRange(1, 5).fieldOf("max_age").forGetter(ChorusFlowerBehavior::getMaxAge))
            .apply(instance, ChorusFlowerBehavior::new));

    private final int maxAge;

    public ChorusFlowerBehavior(int maxAge) {
        this.maxAge = maxAge;
    }

    public int getMaxAge() {
        return this.maxAge;
    }

    @Override
    public MapCodec<? extends BoneMealBehavior> codec() {
        return CODEC;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, BonemealSource source) {
        return state.hasProperty(ChorusFlowerBlock.AGE) && state.getValue(ChorusFlowerBlock.AGE) < this.maxAge;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        state.randomTick(level, pos, random);
    }
}
