package fuzs.universalbonemeal.network;

import fuzs.puzzleslib.api.network.v3.ClientMessageListener;
import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.ParticleUtils;
import net.minecraft.world.level.LevelAccessor;

public record ClientboundGrowthParticlesMessage(BlockPos blockPos) implements ClientboundMessage<ClientboundGrowthParticlesMessage> {

    @Override
    public ClientMessageListener<ClientboundGrowthParticlesMessage> getHandler() {
        return new ClientMessageListener<ClientboundGrowthParticlesMessage>() {

            @Override
            public void handle(ClientboundGrowthParticlesMessage message, Minecraft minecraft, ClientPacketListener handler, LocalPlayer player, ClientLevel level) {
                addGrowthParticles(level, message.blockPos, 15);
            }

            public static void addGrowthParticles(LevelAccessor level, BlockPos blockPos, int data) {
                if (level.getBlockState(blockPos).isSolidRender(level, blockPos)) {
                    ParticleUtils.spawnParticles(level, blockPos.above(), data * 3, 3.0, 1.0, false,
                            ParticleTypes.HAPPY_VILLAGER
                    );
                } else {
                    ParticleUtils.spawnParticleInBlock(level, blockPos, data, ParticleTypes.HAPPY_VILLAGER);
                }
            }
        };
    }
}
