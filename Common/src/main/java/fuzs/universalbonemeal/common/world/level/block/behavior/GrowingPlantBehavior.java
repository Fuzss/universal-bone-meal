package fuzs.universalbonemeal.common.world.level.block.behavior;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.WorldgenRandom;

public class GrowingPlantBehavior extends AbstractGrowingPlantBehavior {
    public static final MapCodec<GrowingPlantBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    IntProviders.codec(0, 128)
                            .fieldOf("blocks_to_grow")
                            .forGetter(AbstractGrowingPlantBehavior::getBlocksToGrow),
                    IntProviders.codec(1, 128).fieldOf("max_height").forGetter(GrowingPlantBehavior::getMaxHeight))
            .apply(instance, GrowingPlantBehavior::new));

    private final IntProvider maxHeight;

    public GrowingPlantBehavior(IntProvider blocksToGrow, IntProvider maxHeight) {
        super(blocksToGrow);
        this.maxHeight = maxHeight;
    }

    public IntProvider getMaxHeight() {
        return this.maxHeight;
    }

    @Override
    public MapCodec<? extends BoneMealBehavior> codec() {
        return CODEC;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, BonemealSource source) {
        if (this.getConnectedPlantHeight(level, pos, state.getBlock()) < this.getMaxHeightAtPosition(pos)) {
            return super.isValidBonemealTarget(level, pos, state, source);
        } else {
            return false;
        }
    }

    private int getConnectedPlantHeight(BlockGetter level, BlockPos pos, Block block) {
        BlockPos topPos = getTopConnectedBlock(level, pos, block, this.getGrowthDirection());
        BlockPos bottomPos = getTopConnectedBlock(level, pos, block, this.getGrowthDirection().getOpposite());
        return Math.abs(topPos.getY() - bottomPos.getY());
    }

    private int getMaxHeightAtPosition(BlockPos pos) {
        // always use 0 seed, as client does not have access to world seed
        return this.maxHeight.sample(WorldgenRandom.seedSlimeChunk(pos.getX(), pos.getZ(), 0, 987234911L));
    }

    @Override
    protected void performBonemealTop(ServerLevel level, RandomSource random, BlockPos topPos, BlockState sourceState) {
        super.performBonemealTop(level, random, topPos, sourceState);
        BlockState state = level.getBlockState(topPos).setValue(this.getAgeProperty(), 0);
        level.setBlockAndUpdate(topPos, state);
        state.updateNeighbourShapes(level, topPos, Block.UPDATE_ALL);
    }

    @Override
    protected Direction getGrowthDirection() {
        return Direction.UP;
    }

    protected IntegerProperty getAgeProperty() {
        return BlockStateProperties.AGE_15;
    }
}
