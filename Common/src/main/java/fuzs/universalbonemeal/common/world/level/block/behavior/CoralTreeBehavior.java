package fuzs.universalbonemeal.common.world.level.block.behavior;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public record CoralTreeBehavior(HolderSet<Block> coralBlocks,
                                HolderSet<Block> corals,
                                HolderSet<Block> wallCorals,
                                Holder<Block> seaPickle) implements BoneMealBehavior {
    public static final MapCodec<CoralTreeBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    RegistryCodecs.holderSet(Registries.BLOCK)
                            .fieldOf("coral_blocks")
                            .forGetter(CoralTreeBehavior::coralBlocks),
                    RegistryCodecs.holderSet(Registries.BLOCK).fieldOf("corals").forGetter(CoralTreeBehavior::corals),
                    RegistryCodecs.holderSet(Registries.BLOCK).fieldOf("wall_corals").forGetter(CoralTreeBehavior::wallCorals),
                    RegistryCodecs.holder(Registries.BLOCK).fieldOf("sea_pickle").forGetter(CoralTreeBehavior::seaPickle))
            .apply(instance, CoralTreeBehavior::new));

    @Override
    public MapCodec<CoralTreeBehavior> codec() {
        return CODEC;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, BonemealSource source) {
        return level.getBiome(pos).is(BiomeTags.PRODUCES_CORALS_FROM_BONEMEAL);
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        return (double) random.nextFloat() < 0.4D;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        level.removeBlock(pos, false);
        BlockState coralState = this.getCoralBlock(state, random).defaultBlockState();
        if (!this.placeCoralTree(level, random, pos, coralState)) {
            level.setBlock(pos, state, Block.UPDATE_ALL);
        }
    }

    private Block getCoralBlock(BlockState state, RandomSource random) {
        Block block = this.getCoralBlock(state.getBlock());
        return block != null ? block : this.coralBlocks.getRandomElement(random).map(Holder::value).orElseThrow();
    }

    @Nullable
    private Block getCoralBlock(Block block) {
        // hopeful this will be enough for mod compat with e.g. upgrade aquatic
        String name = BuiltInRegistries.BLOCK.getKey(block).getPath();
        name = name.substring(0, name.indexOf("_coral"));
        for (Holder<Block> holder : this.coralBlocks) {
            if (BuiltInRegistries.BLOCK.getKey(holder.value()).getPath().contains(name)) {
                return holder.value();
            }
        }
        return null;
    }

    private boolean placeCoralTree(LevelAccessor level, RandomSource random, BlockPos pos, BlockState state) {
        BlockPos.MutableBlockPos mutablePos = pos.mutable();
        int trunkHeight = random.nextInt(3) + 1;
        if (!this.isValidPosition(level, pos, trunkHeight)) return false;
        for (int i = 0; i < trunkHeight; ++i) {
            if (!this.placeCoralBlock(level, random, mutablePos, state, i == trunkHeight - 1)) {
                return i != 0;
            }
            mutablePos.move(Direction.UP);
        }
        BlockPos trunkTopPos = mutablePos.immutable();
        int arms = random.nextInt(3) + 2;
        List<Direction> directions = Lists.newArrayList(Direction.Plane.HORIZONTAL);
        Collections.shuffle(directions);
        for (Direction direction : directions.subList(0, arms)) {
            mutablePos.set(trunkTopPos);
            mutablePos.move(direction);
            int armLength = random.nextInt(5) + 2;
            int segmentLength = 0;
            for (int i = 0; i < armLength && this.placeCoralBlock(level, random, mutablePos, state, true); ++i) {
                ++segmentLength;
                mutablePos.move(Direction.UP);
                if (i == 0 || segmentLength >= 2 && random.nextFloat() < 0.25F) {
                    mutablePos.move(direction);
                    segmentLength = 0;
                }
            }
        }
        return true;
    }

    private boolean isValidPosition(LevelAccessor level, BlockPos pos, int height) {
        int y = pos.getY();
        if (y >= level.getMinY() + 1 && y + height + 1 < level.getMaxY()) {
            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
            for (int i = 0; i <= height + 4; ++i) {
                int radius = i < height ? 0 : (i - height) / 2 + 1;
                for (int x = -radius; x <= radius; ++x) {
                    for (int z = -radius; z <= radius; ++z) {
                        BlockState state = level.getBlockState(mutablePos.setWithOffset(pos, x, i, z));
                        if (!this.isCoralReplaceable(state)) {
                            return false;
                        }
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private boolean isCoralReplaceable(BlockState state) {
        return state.is(Blocks.WATER) || this.coralBlocks.contains(state.typeHolder())
                || this.corals.contains(state.typeHolder()) || this.wallCorals.contains(state.typeHolder());
    }

    private boolean placeCoralBlock(LevelAccessor level, RandomSource random, BlockPos pos, BlockState state, boolean decorateTop) {
        BlockPos abovePos = pos.above();
        BlockState stateAtPos = level.getBlockState(pos);
        // allows coral trees to grow into each other, just like actual trees
        if (this.isCoralReplaceable(stateAtPos) && this.isCoralReplaceable(level.getBlockState(abovePos))) {
            level.setBlock(pos, state, Block.UPDATE_ALL);
            // vanilla always decorates top, resulting in trunks sometimes being cut off
            if (decorateTop) {
                if (random.nextFloat() < 0.25F) {
                    this.corals.getRandomElement(random)
                            .map(Holder::value)
                            .ifPresent(coral -> level.setBlock(abovePos,
                                    coral.defaultBlockState(),
                                    Block.UPDATE_CLIENTS));
                } else if (random.nextFloat() < 0.05F) {
                    level.setBlock(abovePos,
                            this.seaPickle.value()
                                    .defaultBlockState()
                                    .setValue(SeaPickleBlock.PICKLES, random.nextInt(4) + 1),
                            Block.UPDATE_CLIENTS);
                }
            }
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                if (random.nextFloat() < 0.2F) {
                    BlockPos sidePos = pos.relative(direction);
                    if (level.getBlockState(sidePos).is(Blocks.WATER)) {
                        this.wallCorals.getRandomElement(random).map(Holder::value).ifPresent((coral) -> {
                            BlockState coralState = coral.defaultBlockState();
                            if (coralState.hasProperty(BaseCoralWallFanBlock.FACING)) {
                                coralState = coralState.setValue(BaseCoralWallFanBlock.FACING, direction);
                            }
                            level.setBlock(sidePos, coralState, Block.UPDATE_CLIENTS);
                        });
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
