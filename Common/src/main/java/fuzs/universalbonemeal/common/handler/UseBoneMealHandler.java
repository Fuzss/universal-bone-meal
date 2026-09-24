package fuzs.universalbonemeal.common.handler;

import fuzs.multiloaderdataextensions.common.api.v2.DataMapLookup;
import fuzs.puzzleslib.common.api.event.v1.core.EventResult;
import fuzs.puzzleslib.common.api.item.v2.ItemHelper;
import fuzs.puzzleslib.common.api.network.v4.MessageSender;
import fuzs.puzzleslib.common.api.network.v4.PlayerSet;
import fuzs.universalbonemeal.common.init.ModRegistry;
import fuzs.universalbonemeal.common.network.ClientboundGrowthParticlesMessage;
import fuzs.universalbonemeal.common.world.level.block.behavior.BoneMealBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.function.Consumers;

public class UseBoneMealHandler {

    public static EventResult onUseBoneMeal(Level level, BlockPos blockPos, BlockState blockState, ItemStack itemStack) {
        Holder<BoneMealBehavior> holder = DataMapLookup.getData(ModRegistry.BONE_MEAL_BEHAVIORS_DATA_MAP,
                blockState.typeHolder());
        if (holder != null) {
            BoneMealBehavior boneMealBehavior = holder.value();
            if (boneMealBehavior.isValidBonemealTarget(level, blockPos, blockState, BonemealSource.INTERACTION)) {
                if (level instanceof ServerLevel serverLevel) {
                    if (boneMealBehavior.isBonemealSuccess(level,
                            level.getRandom(),
                            blockPos,
                            blockState,
                            BonemealSource.INTERACTION)) {
                        boneMealBehavior.performBonemeal(serverLevel,
                                level.getRandom(),
                                blockPos,
                                blockState,
                                BonemealSource.INTERACTION);
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
}
