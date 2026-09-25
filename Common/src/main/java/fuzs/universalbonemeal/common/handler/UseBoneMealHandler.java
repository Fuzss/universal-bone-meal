package fuzs.universalbonemeal.common.handler;

import fuzs.multiloaderdataextensions.common.api.v2.DataMapLookup;
import fuzs.puzzleslib.common.api.event.v1.core.EventResult;
import fuzs.puzzleslib.common.api.network.v4.MessageSender;
import fuzs.puzzleslib.common.api.network.v4.PlayerSet;
import fuzs.universalbonemeal.common.init.ModRegistry;
import fuzs.universalbonemeal.common.network.ClientboundGrowthParticlesMessage;
import fuzs.universalbonemeal.common.world.level.block.bonemeal.ConditionalBoneMealBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

public class UseBoneMealHandler {

    public static EventResult onUseBoneMeal(Level level, BlockPos pos, BlockState state, ItemStack item) {
        if (state.is(ModRegistry.FERTILIZER_RESISTANT_PLANTS_BLOCK_TAG)) {
            return EventResult.DENY;
        }

        ConditionalBoneMealBehavior behavior = DataMapLookup.getData(ModRegistry.BONE_MEAL_BEHAVIORS_DATA_MAP,
                state.typeHolder());
        if (behavior != null && (!(state.getBlock() instanceof BonemealableBlock) || behavior.replace())) {
            if (behavior.isValidBonemealTarget(level, pos, state, BonemealSource.INTERACTION)) {
                if (level instanceof ServerLevel serverLevel) {
                    if (behavior.isBonemealSuccess(level, level.getRandom(), pos, state, BonemealSource.INTERACTION)) {
                        behavior.performBonemeal(serverLevel,
                                level.getRandom(),
                                pos,
                                state,
                                BonemealSource.INTERACTION);
                    }

                    shrinkOrHurtItem(serverLevel, item);
                    broadcastGrowthParticles(serverLevel, pos, state);
                }

                return EventResult.ALLOW;
            }
        }

        return EventResult.PASS;
    }

    /**
     * Vanilla only spawns particles for blocks that implement {@link BonemealableBlock} now. In our case that only
     * applies to stem blocks. Therefore, we send a custom packet for all others.
     */
    private static void broadcastGrowthParticles(ServerLevel level, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof BonemealableBlock)) {
            MessageSender.broadcast(PlayerSet.nearPosition(pos, level), new ClientboundGrowthParticlesMessage(pos));
        }
    }

    private static void shrinkOrHurtItem(ServerLevel level, ItemStack item) {
        if (!item.isDamageableItem()) {
            item.shrink(1);
        } else {
            item.hurtAndBreak(1, level, null, Function.identity()::apply);
        }
    }
}
