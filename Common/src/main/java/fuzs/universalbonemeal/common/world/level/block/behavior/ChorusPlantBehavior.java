package fuzs.universalbonemeal.common.world.level.block.behavior;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class ChorusPlantBehavior extends ChorusFlowerBehavior {
    public static final MapCodec<ChorusPlantBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    RegistryCodecs.holderSet(Registries.BLOCK).fieldOf("plant").forGetter(ChorusPlantBehavior::getPlant),
                    RegistryCodecs.holderSet(Registries.BLOCK)
                            .fieldOf("flower")
                            .forGetter(ChorusPlantBehavior::getFlower),
                    Codec.intRange(1, 256)
                            .fieldOf("search_range")
                            .forGetter(ChorusPlantBehavior::getSearchRange),
                    Codec.intRange(1, 5).fieldOf("max_age").forGetter(ChorusFlowerBehavior::getMaxAge))
            .apply(instance, ChorusPlantBehavior::new));

    private final HolderSet<Block> plant;
    private final HolderSet<Block> flower;
    private final int searchRange;

    public ChorusPlantBehavior(HolderSet<Block> plant, HolderSet<Block> flower, int searchRange, int maxAge) {
        super(maxAge);
        this.plant = plant;
        this.flower = flower;
        this.searchRange = searchRange;
    }

    public HolderSet<Block> getPlant() {
        return this.plant;
    }

    public HolderSet<Block> getFlower() {
        return this.flower;
    }

    public int getSearchRange() {
        return this.searchRange;
    }

    @Override
    public MapCodec<ChorusPlantBehavior> codec() {
        return CODEC;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, BonemealSource source) {
        for (BlockPos flowerPos : this.getFlowerPositions(level, pos)) {
            if (super.isValidBonemealTarget(level, flowerPos, level.getBlockState(flowerPos), source)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        for (BlockPos flowerPos : this.getFlowerPositions(level, pos)) {
            super.performBonemeal(level, random, flowerPos, level.getBlockState(flowerPos), source);
        }
    }

    private Collection<BlockPos> getFlowerPositions(BlockGetter level, BlockPos startPos) {
        Set<BlockPos> targets = Sets.newHashSet();
        getTopConnectedBlock(level,
                startPos.mutable(),
                this.plant,
                this.flower,
                targets,
                Direction.DOWN,
                this.searchRange);
        return targets;
    }

    public static void getTopConnectedBlock(BlockGetter level, BlockPos.MutableBlockPos sourcePos, HolderSet<Block> sourceBlocks, HolderSet<Block> targetBlocks, Collection<BlockPos> targets, Direction sourceDirection, int depth) {
        BlockState sourceState = level.getBlockState(sourcePos);
        if (depth <= 0 || !sourceBlocks.contains(sourceState.typeHolder())) {
            if (targetBlocks.contains(sourceState.typeHolder())) {
                targets.add(sourcePos.immutable());
            }
            return;
        }
        for (Map.Entry<Direction, BooleanProperty> entry : PipeBlock.PROPERTY_BY_DIRECTION.entrySet()) {
            Direction direction = entry.getKey();
            if (direction != Direction.DOWN && direction != sourceDirection && sourceState.getValue(entry.getValue())) {
                sourcePos.move(direction);
                getTopConnectedBlock(level,
                        sourcePos,
                        sourceBlocks,
                        targetBlocks,
                        targets,
                        direction.getOpposite(),
                        depth - 1);
                sourcePos.move(direction.getOpposite());
            }
        }
    }
}
