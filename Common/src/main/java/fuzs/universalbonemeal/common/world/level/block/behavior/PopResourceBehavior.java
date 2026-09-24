package fuzs.universalbonemeal.common.world.level.block.behavior;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.state.BlockState;

public record PopResourceBehavior(Direction direction) implements BoneMealBehavior {
    public static final MapCodec<PopResourceBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Direction.CODEC.fieldOf("direction").forGetter(PopResourceBehavior::direction))
            .apply(instance, PopResourceBehavior::new));

    @Override
    public MapCodec<PopResourceBehavior> codec() {
        return CODEC;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        Block.popResourceFromFace(level, pos, this.direction, new ItemStack(state.getBlock()));
    }
}
