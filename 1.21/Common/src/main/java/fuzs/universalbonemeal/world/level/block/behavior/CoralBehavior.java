package fuzs.universalbonemeal.world.level.block.behavior;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.BaseCoralWallFanBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SeaPickleBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CoralBehavior implements BonemealBehavior {
    private static Map<Block, Block> plantToBlock;

    @Override
    public boolean isValidBonemealTarget(LevelReader p_54870_, BlockPos p_54871_, BlockState p_54872_) {
        return p_54870_.getBiome(p_54871_).is(Biomes.WARM_OCEAN);
    }

    @Override
    public boolean isBonemealSuccess(Level p_54875_, RandomSource p_54876_, BlockPos p_54877_, BlockState p_54878_) {
        return (double) p_54876_.nextFloat() < 0.4D;
    }

    @Override
    public void performBonemeal(ServerLevel p_54865_, RandomSource p_54866_, BlockPos p_54867_, BlockState p_54868_) {
        this.place(p_54865_, p_54867_, p_54868_, p_54866_);
    }

    private boolean place(ServerLevel p_54860_, BlockPos p_54861_, BlockState p_54862_, RandomSource p_54863_) {
        p_54860_.removeBlock(p_54861_, false);
        this.dissolve();
        if (this.placeFeature(p_54860_, p_54863_, p_54861_, this.getBlockEquivalent(p_54862_, p_54863_).defaultBlockState())) {
            return true;
        } else {
            p_54860_.setBlock(p_54861_, p_54862_, 3);
            return false;
        }
    }

    public static void invalidate() {
        plantToBlock = null;
    }

    private void dissolve() {
        if (plantToBlock == null) {
            Map<Block, Block> map = Maps.newHashMap();
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
        return BuiltInRegistries.BLOCK.getTag(BlockTags.CORAL_BLOCKS).flatMap((p_204728_) -> {
            return p_204728_.getRandomElement(random);
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

    private boolean isValidPosition(LevelAccessor p_65099_, BlockPos p_65100_, int height) {
        int i = p_65100_.getY();
        if (i >= p_65099_.getMinBuildHeight() + 1 && i + height + 1 < p_65099_.getMaxBuildHeight()) {
            BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
            for(int j = 0; j <= height + 4; ++j) {
                int k = j < height ? 0 : (j - height) / 2 + 1;
                for(int l = -k; l <= k; ++l) {
                    for(int i1 = -k; i1 <= k; ++i1) {
                        BlockState blockstate1 = p_65099_.getBlockState(mutable.setWithOffset(p_65100_, l, j, i1));
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

    private boolean isCoralReplaceable(BlockState blockstate1) {
        return blockstate1.is(Blocks.WATER) || blockstate1.is(BlockTags.CORAL_BLOCKS) || blockstate1.is(BlockTags.CORALS) || blockstate1.is(BlockTags.WALL_CORALS);
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
                    BuiltInRegistries.BLOCK.getTag(BlockTags.CORALS).flatMap((p_204731_) -> {
                        return p_204731_.getRandomElement(random);
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
                        BuiltInRegistries.BLOCK.getTag(BlockTags.WALL_CORALS).flatMap((p_204728_) -> {
                            return p_204728_.getRandomElement(random);
                        }).map(Holder::value).ifPresent((p_204725_) -> {
                            BlockState blockstate1 = p_204725_.defaultBlockState();
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
