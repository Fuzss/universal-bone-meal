package fuzs.universalbonemeal.world.level.block.behavior;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseCoralWallFanBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SeaPickleBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class CoralBehavior implements BoneMealBehavior {
    private static Map<Block, Block> plantToBlock;

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos blockPos, BlockState blockState) {
        return level.getBiome(blockPos).is(BiomeTags.PRODUCES_CORALS_FROM_BONEMEAL);
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos blockPos, BlockState blockState) {
        return (double) random.nextFloat() < 0.4D;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos blockPos, BlockState blockState) {
        this.place(level, blockPos, blockState, random);
    }

    private boolean place(ServerLevel level, BlockPos blockPos, BlockState blockState, RandomSource randomSource) {
        level.removeBlock(blockPos, false);
        this.dissolve();
        if (this.placeFeature(level, randomSource, blockPos, this.getBlockEquivalent(blockState, randomSource).defaultBlockState())) {
            return true;
        } else {
            level.setBlock(blockPos, blockState, 3);
            return false;
        }
    }

    public static void invalidate() {
        plantToBlock = null;
    }

    private void dissolve() {
        if (plantToBlock == null) {
            Map<Block, Block> map = new IdentityHashMap<>();
            for (Holder<Block> holder : BuiltInRegistries.BLOCK.getTagOrEmpty(BlockTags.CORAL_PLANTS)) {
                Block block = this.getBlockEquivalent(holder.value());
                if (block != null) {
                    map.put(holder.value(), block);
                }
            }
            plantToBlock = map;
        }
    }

    @Nullable
    private Block getBlockEquivalent(Block block) {
        // hopeful this will be enough for mod compat with e.g. upgrade aquatic
        String name = BuiltInRegistries.BLOCK.getKey(block).getPath();
        name = name.substring(0, name.indexOf("_coral"));
        for (Holder<Block> holder : BuiltInRegistries.BLOCK.getTagOrEmpty(BlockTags.CORAL_BLOCKS)) {
            if (BuiltInRegistries.BLOCK.getKey(holder.value()).getPath().contains(name)) {
                return holder.value();
            }
        }
        return null;
    }

    private Block getBlockEquivalent(BlockState blockState, RandomSource random) {
        Block block = plantToBlock.get(blockState.getBlock());
        if (block != null) return block;
        return BuiltInRegistries.BLOCK.getTag(BlockTags.CORAL_BLOCKS).flatMap((holders) -> {
            return holders.getRandomElement(random);
        }).map(Holder::value).orElseThrow();
    }

    private boolean placeFeature(LevelAccessor level, RandomSource random, BlockPos pos, BlockState blockState) {
        BlockPos.MutableBlockPos mutable = pos.mutable();
        int trunkHeight = random.nextInt(3) + 1;
        if (!this.isValidPosition(level, pos, trunkHeight)) return false;
        for(int j = 0; j < trunkHeight; ++j) {
            if (!this.placeCoralBlock(level, random, mutable, blockState, j == trunkHeight - 1)) {
                return j != 0;
            }
            mutable.move(Direction.UP);
        }
        BlockPos blockpos = mutable.immutable();
        int arms = random.nextInt(3) + 2;
        List<Direction> list = Lists.newArrayList(Direction.Plane.HORIZONTAL);
        Collections.shuffle(list);
        for (Direction direction : list.subList(0, arms)) {
            mutable.set(blockpos);
            mutable.move(direction);
            int armLength = random.nextInt(5) + 2;
            int i1 = 0;
            for (int j1 = 0; j1 < armLength && this.placeCoralBlock(level, random, mutable, blockState, true); ++j1) {
                ++i1;
                mutable.move(Direction.UP);
                if (j1 == 0 || i1 >= 2 && random.nextFloat() < 0.25F) {
                    mutable.move(direction);
                    i1 = 0;
                }
            }
        }
        return true;
    }

    private boolean isValidPosition(LevelAccessor level, BlockPos blockPos, int height) {
        int i = blockPos.getY();
        if (i >= level.getMinBuildHeight() + 1 && i + height + 1 < level.getMaxBuildHeight()) {
            BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
            for(int j = 0; j <= height + 4; ++j) {
                int k = j < height ? 0 : (j - height) / 2 + 1;
                for(int l = -k; l <= k; ++l) {
                    for(int i1 = -k; i1 <= k; ++i1) {
                        BlockState blockstate1 = level.getBlockState(mutable.setWithOffset(blockPos, l, j, i1));
                        if (!this.isCoralReplaceable(blockstate1)) {
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

    private boolean isCoralReplaceable(BlockState blockState) {
        return blockState.is(Blocks.WATER) || blockState.is(BlockTags.CORAL_BLOCKS) || blockState.is(BlockTags.CORALS) || blockState.is(BlockTags.WALL_CORALS);
    }

    private boolean placeCoralBlock(LevelAccessor level, RandomSource random, BlockPos pos, BlockState blockState, boolean decorateTop) {
        BlockPos blockpos = pos.above();
        BlockState blockstate = level.getBlockState(pos);
        // allows coral trees to grow into each other, just like actual trees
        if (this.isCoralReplaceable(blockstate) && this.isCoralReplaceable(level.getBlockState(blockpos))) {
            level.setBlock(pos, blockState, 3);
            // vanilla always decorates top, resulting in trunks sometimes being cut off
            if (decorateTop) {
                if (random.nextFloat() < 0.25F) {
                    BuiltInRegistries.BLOCK.getTag(BlockTags.CORALS).flatMap((holders) -> {
                        return holders.getRandomElement(random);
                    }).map(Holder::value).ifPresent((p_204720_) -> {
                        level.setBlock(blockpos, p_204720_.defaultBlockState(), 2);
                    });
                } else if (random.nextFloat() < 0.05F) {
                    level.setBlock(blockpos, Blocks.SEA_PICKLE.defaultBlockState().setValue(SeaPickleBlock.PICKLES, random.nextInt(4) + 1), 2);
                }
            }
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                if (random.nextFloat() < 0.2F) {
                    BlockPos blockpos1 = pos.relative(direction);
                    if (level.getBlockState(blockpos1).is(Blocks.WATER)) {
                        BuiltInRegistries.BLOCK.getTag(BlockTags.WALL_CORALS).flatMap((holders) -> {
                            return holders.getRandomElement(random);
                        }).map(Holder::value).ifPresent((block) -> {
                            BlockState blockstate1 = block.defaultBlockState();
                            if (blockstate1.hasProperty(BaseCoralWallFanBlock.FACING)) {
                                blockstate1 = blockstate1.setValue(BaseCoralWallFanBlock.FACING, direction);
                            }
                            level.setBlock(blockpos1, blockstate1, 2);
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
