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

public class SimpleGrowingPlantBehavior extends GrowingPlantBehavior {
    public static final MapCodec<SimpleGrowingPlantBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    IntProviders.codec(0, 128)
                            .fieldOf("blocks_to_grow")
                            .forGetter(SimpleGrowingPlantBehavior::getBlocksToGrow))
            .apply(instance, SimpleGrowingPlantBehavior::new));

    private final IntProvider blocksToGrow;

    public SimpleGrowingPlantBehavior(IntProvider blocksToGrow) {
        this.blocksToGrow = blocksToGrow;
    }

    public IntProvider getBlocksToGrow() {
        return this.blocksToGrow;
    }

    @Override
    public MapCodec<? extends BoneMealBehavior> codec() {
        return CODEC;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos blockPos, BlockState blockState, BonemealSource bonemealSource) {
        if (this.getConnectedPlantHeight(level, blockPos, blockState.getBlock())
                < this.getMaxHeightAtPosition(blockPos.getX(), blockPos.getZ())) {
            return super.isValidBonemealTarget(level, blockPos, blockState, bonemealSource);
        } else {
            return false;
        }
    }

    private int getConnectedPlantHeight(BlockGetter blockGetter, BlockPos pos, Block block) {
        BlockPos pos1 = getTopConnectedBlock(blockGetter, pos, block, this.getGrowthDirection());
        BlockPos pos2 = getTopConnectedBlock(blockGetter, pos, block, this.getGrowthDirection().getOpposite());
        return Math.abs(pos1.getY() - pos2.getY());
    }

    private int getMaxHeightAtPosition(int posX, int posZ) {
        // always use 0 seed, as client does not have access to world seed
        return 12 + WorldgenRandom.seedSlimeChunk(posX, posZ, 0, 987234911L).nextInt(5);
    }

    @Override
    protected void performBonemealTop(ServerLevel serverLevel, RandomSource randomSource, BlockPos topPos, BlockState sourceState) {
        super.performBonemealTop(serverLevel, randomSource, topPos, sourceState);
        BlockState blockState = serverLevel.getBlockState(topPos).setValue(this.getAgeProperty(), 0);
        serverLevel.setBlockAndUpdate(topPos, blockState);
        blockState.updateNeighbourShapes(serverLevel, topPos, Block.UPDATE_ALL);
    }

    @Override
    protected Direction getGrowthDirection() {
        return Direction.UP;
    }

    protected IntegerProperty getAgeProperty() {
        return BlockStateProperties.AGE_15;
    }

    @Override
    protected int getBlocksToGrowWhenBonemealed(RandomSource random) {
        return this.blocksToGrow.sample(random);
    }

    @Override
    protected boolean canGrowInto(BlockState state) {
        return state.isAir();
    }

    @Override
    protected BlockState getGrownBlockState(BlockState sourceState, RandomSource randomSource, ServerLevel level, BlockPos pos) {
        return sourceState.getBlock().defaultBlockState();
    }
}
