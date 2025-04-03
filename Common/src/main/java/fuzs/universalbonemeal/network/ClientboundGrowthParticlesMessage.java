package fuzs.universalbonemeal.network;

import fuzs.puzzleslib.api.network.v4.message.MessageListener;
import fuzs.puzzleslib.api.network.v4.message.play.ClientboundPlayMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ParticleUtils;
import net.minecraft.world.level.Level;

public record ClientboundGrowthParticlesMessage(BlockPos blockPos) implements ClientboundPlayMessage {
    public static final StreamCodec<ByteBuf, ClientboundGrowthParticlesMessage> STREAM_CODEC = BlockPos.STREAM_CODEC.map(
            ClientboundGrowthParticlesMessage::new,
            ClientboundGrowthParticlesMessage::blockPos);

    @Override
    public MessageListener<Context> getListener() {
        return new MessageListener<Context>() {

            @Override
            public void accept(Context context) {
                this.addGrowthParticles(context.level(), ClientboundGrowthParticlesMessage.this.blockPos, 15);
            }

            private void addGrowthParticles(Level level, BlockPos blockPos, int data) {
                if (level.getBlockState(blockPos).isSolidRender()) {
                    ParticleUtils.spawnParticles(level,
                            blockPos.above(),
                            data * 3,
                            3.0,
                            1.0,
                            false,
                            ParticleTypes.HAPPY_VILLAGER);
                } else {
                    ParticleUtils.spawnParticleInBlock(level, blockPos, data, ParticleTypes.HAPPY_VILLAGER);
                }
            }
        };
    }
}
