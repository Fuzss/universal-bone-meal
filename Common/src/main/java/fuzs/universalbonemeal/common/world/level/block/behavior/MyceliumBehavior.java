package fuzs.universalbonemeal.common.world.level.block.behavior;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class MyceliumBehavior extends SpreadAroundBehavior {
    public static final MapCodec<MyceliumBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    BlockStateProvider.CODEC.fieldOf("vegetation").forGetter(MyceliumBehavior::getBlockStateProvider),
                    Codec.intRange(1, 16).fieldOf("spread_width").forGetter(MyceliumBehavior::getSpreadWidth),
                    Codec.intRange(0, 16).fieldOf("spread_height").forGetter(MyceliumBehavior::getSpreadHeight))
            .apply(instance, MyceliumBehavior::new));

    private final int spreadWidth;
    private final int spreadHeight;

    public MyceliumBehavior(Holder<BlockStateProvider> vegetation, int spreadWidth, int spreadHeight) {
        super(vegetation);
        this.spreadWidth = spreadWidth;
        this.spreadHeight = spreadHeight;
    }

    @Override
    public MapCodec<MyceliumBehavior> codec() {
        return CODEC;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos blockPos, BlockState blockState, BonemealSource bonemealSource) {
        return level.getBlockState(blockPos.above()).isAir();
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState blockState, BonemealSource bonemealSource) {
        super.performBonemeal(level, random, pos.above(), blockState, bonemealSource);
    }

    @Override
    protected int getSpreadWidth() {
        return this.spreadWidth;
    }

    @Override
    protected int getSpreadHeight() {
        return this.spreadHeight;
    }
}
