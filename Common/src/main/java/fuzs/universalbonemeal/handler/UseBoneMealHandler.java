package fuzs.universalbonemeal.handler;

import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.item.v2.ItemHelper;
import fuzs.puzzleslib.api.network.v4.MessageSender;
import fuzs.puzzleslib.api.network.v4.PlayerSet;
import fuzs.universalbonemeal.network.ClientboundGrowthParticlesMessage;
import fuzs.universalbonemeal.world.level.block.behavior.BoneMealBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.function.Consumers;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class UseBoneMealHandler {
    private static final List<AbstractBehaviorData> BONE_MEAL_BEHAVIORS = new ArrayList<>();
    private static Map<Block, BoneMealBehavior> blockToBehavior;

    public static EventResult onUseBoneMeal(Level level, BlockPos blockPos, BlockState blockState, ItemStack itemStack) {
        BoneMealBehavior boneMealBehavior = dissolve().get(blockState.getBlock());
        if (boneMealBehavior != null) {
            if (boneMealBehavior.isValidBonemealTarget(level, blockPos, blockState)) {
                if (level instanceof ServerLevel serverLevel) {
                    if (boneMealBehavior.isBonemealSuccess(level, level.random, blockPos, blockState)) {
                        boneMealBehavior.performBonemeal(serverLevel, level.random, blockPos, blockState);
                    }

                    if (itemStack.isStackable()) {
                        itemStack.shrink(1);
                    } else if (itemStack.isDamageableItem()) {
                        ItemHelper.hurtAndBreak(itemStack, 1, serverLevel, null, Consumers.nop());
                    }

                    // vanilla only spawns particles for blocks that implement BonemealableBlock now,
                    // which in our case only applies to stem blocks
                    // so send a custom packet for all others
                    if (!(blockState.getBlock() instanceof BonemealableBlock)) {
                        MessageSender.broadcast(PlayerSet.nearPosition(blockPos, serverLevel),
                                new ClientboundGrowthParticlesMessage(blockPos));
                    }
                }

                return EventResult.ALLOW;
            }
        }

        return EventResult.PASS;
    }

    private static Map<Block, BoneMealBehavior> dissolve() {
        Map<Block, BoneMealBehavior> map = blockToBehavior;
        if (map == null) {
            Map<Block, BoneMealBehavior> newMap = new IdentityHashMap<>();
            for (AbstractBehaviorData behavior : BONE_MEAL_BEHAVIORS) {
                if (behavior.allow()) {
                    behavior.compile(newMap);
                }
            }
            return blockToBehavior = ImmutableMap.copyOf(newMap);
        } else {
            return map;
        }
    }

    public static void invalidate() {
        blockToBehavior = null;
    }

    public static void registerBehavior(Block block, Supplier<BoneMealBehavior> factory, BooleanSupplier config) {
        BONE_MEAL_BEHAVIORS.add(new BlockBehaviorData(block, factory, config));
    }

    public static void registerBehavior(Set<Block> blocks, Supplier<BoneMealBehavior> factory, BooleanSupplier config) {
        BONE_MEAL_BEHAVIORS.add(new MultiBlockBehaviorData(blocks, factory, config));
    }

    public static void registerBehavior(TagKey<Block> tag, Supplier<BoneMealBehavior> factory, BooleanSupplier config) {
        BONE_MEAL_BEHAVIORS.add(new BlockTagBehaviorData(tag, null, factory, config));
    }

    public static void registerBehavior(TagKey<Block> allowedTag, TagKey<Block> disallowedTag, Supplier<BoneMealBehavior> factory, BooleanSupplier config) {
        BONE_MEAL_BEHAVIORS.add(new BlockTagBehaviorData(allowedTag, disallowedTag, factory, config));
    }

    private abstract static class AbstractBehaviorData {
        final BoneMealBehavior behavior;
        private final BooleanSupplier config;

        public AbstractBehaviorData(Supplier<BoneMealBehavior> factory, BooleanSupplier config) {
            this.behavior = factory.get();
            this.config = config;
        }

        public abstract void compile(Map<Block, BoneMealBehavior> map);

        public boolean allow() {
            return this.config.getAsBoolean();
        }
    }

    private static class BlockBehaviorData extends AbstractBehaviorData {
        private final Block block;

        public BlockBehaviorData(Block block, Supplier<BoneMealBehavior> factory, BooleanSupplier config) {
            super(factory, config);
            this.block = block;
        }

        @Override
        public void compile(Map<Block, BoneMealBehavior> map) {
            map.put(this.block, this.behavior);
        }
    }

    private static class MultiBlockBehaviorData extends AbstractBehaviorData {
        private final Set<Block> targets;

        public MultiBlockBehaviorData(Set<Block> targets, Supplier<BoneMealBehavior> factory, BooleanSupplier config) {
            super(factory, config);
            this.targets = targets;
        }

        @Override
        public void compile(Map<Block, BoneMealBehavior> map) {
            for (Block target : this.targets) {
                map.putIfAbsent(target, this.behavior);
            }
        }
    }

    private static class BlockTagBehaviorData extends AbstractBehaviorData {
        private final TagKey<Block> allowedTag;
        @Nullable
        private final TagKey<Block> disallowedTag;

        public BlockTagBehaviorData(TagKey<Block> allowedTag, @Nullable TagKey<Block> disallowedTag, Supplier<BoneMealBehavior> factory, BooleanSupplier config) {
            super(factory, config);
            this.allowedTag = allowedTag;
            this.disallowedTag = disallowedTag;
        }

        @Override
        public void compile(Map<Block, BoneMealBehavior> map) {
            for (Holder<Block> holder : BuiltInRegistries.BLOCK.getTagOrEmpty(this.allowedTag)) {
                if (this.disallowedTag == null || !holder.is(this.disallowedTag)) {
                    map.putIfAbsent(holder.value(), this.behavior);
                }
            }
        }
    }
}
