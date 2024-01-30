package fuzs.universalbonemeal.handler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.universalbonemeal.world.level.block.behavior.BonemealBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class BonemealHandler {
    private static final List<AbstractBehaviorData> BONE_MEAL_BEHAVIORS = Lists.newArrayList();

    private static Map<Block, BonemealBehavior> blockToBehavior;

    public static EventResult onBonemeal(Level level, BlockPos pos, BlockState block, ItemStack stack) {
        BonemealBehavior behavior = dissolve().get(block.getBlock());
        if (behavior != null) {
            if (behavior.isValidBonemealTarget(level, pos, block, level.isClientSide)) {
                if (level instanceof ServerLevel) {
                    if (behavior.isBonemealSuccess(level, level.random, pos, block)) {
                        behavior.performBonemeal((ServerLevel) level, level.random, pos, block);
                    }
                }
                return EventResult.ALLOW;
            }
        }
        return EventResult.PASS;
    }

    private static Map<Block, BonemealBehavior> dissolve() {
        Map<Block, BonemealBehavior> map = blockToBehavior;
        if (map == null) {
            HashMap<Block, BonemealBehavior> newMap = Maps.newHashMap();
            for (AbstractBehaviorData behavior : BONE_MEAL_BEHAVIORS) {
                if (behavior.allow()) {
                    behavior.compile(newMap);
                }
            }
            return blockToBehavior = Collections.unmodifiableMap(newMap);
        } else {
            return map;
        }
    }

    public static void invalidate() {
        blockToBehavior = null;
    }

    public static void registerBehavior(Block block, Supplier<BonemealBehavior> factory, BooleanSupplier config) {
        BONE_MEAL_BEHAVIORS.add(new BlockBehaviorData(block, factory, config));
    }

    public static void registerBehavior(Set<Block> blocks, Supplier<BonemealBehavior> factory, BooleanSupplier config) {
        BONE_MEAL_BEHAVIORS.add(new MultiBlockBehaviorData(blocks, factory, config));
    }

    public static void registerBehavior(TagKey<Block> tag, Supplier<BonemealBehavior> factory, BooleanSupplier config) {
        BONE_MEAL_BEHAVIORS.add(new BlockTagBehaviorData(tag, factory, config));
    }

    private abstract static class AbstractBehaviorData {
        final BonemealBehavior behavior;
        private final BooleanSupplier config;

        public AbstractBehaviorData(Supplier<BonemealBehavior> factory, BooleanSupplier config) {
            this.behavior = factory.get();
            this.config = config;
        }

        public abstract void compile(Map<Block, BonemealBehavior> map);

        public boolean allow() {
            return this.config.getAsBoolean();
        }
    }

    private static class BlockBehaviorData extends AbstractBehaviorData {
        private final Block block;

        public BlockBehaviorData(Block block, Supplier<BonemealBehavior> factory, BooleanSupplier config) {
            super(factory, config);
            this.block = block;
        }

        @Override
        public void compile(Map<Block, BonemealBehavior> map) {
            map.put(this.block, this.behavior);
        }
    }

    private static class MultiBlockBehaviorData extends AbstractBehaviorData {
        private final Set<Block> targets;

        public MultiBlockBehaviorData(Set<Block> targets, Supplier<BonemealBehavior> factory, BooleanSupplier config) {
            super(factory, config);
            this.targets = targets;
        }

        @Override
        public void compile(Map<Block, BonemealBehavior> map) {
            for (Block target : this.targets) {
                map.put(target, this.behavior);
            }
        }
    }

    private static class BlockTagBehaviorData extends AbstractBehaviorData {
        private final TagKey<Block> tag;

        public BlockTagBehaviorData(TagKey<Block> tag, Supplier<BonemealBehavior> factory, BooleanSupplier config) {
            super(factory, config);
            this.tag = tag;
        }

        @Override
        public void compile(Map<Block, BonemealBehavior> map) {
            for (Holder<Block> value : Registry.BLOCK.getTagOrEmpty(this.tag)) {
                map.putIfAbsent(value.value(), this.behavior);
            }
        }
    }
}
