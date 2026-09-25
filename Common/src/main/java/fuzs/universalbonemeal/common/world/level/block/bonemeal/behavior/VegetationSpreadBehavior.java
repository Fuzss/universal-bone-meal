package fuzs.universalbonemeal.common.world.level.block.bonemeal.behavior;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.jspecify.annotations.Nullable;

public record VegetationSpreadBehavior(HolderSet<Block> groundBlocks,
                                       Holder<BlockStateProvider> vegetation,
                                       HolderSet<Block> bonemealableBlocks,
                                       int attempts,
                                       int attemptsPerStep) implements BoneMealBehavior {
    public static final MapCodec<VegetationSpreadBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    RegistryCodecs.holderSet(Registries.BLOCK)
                            .fieldOf("ground_blocks")
                            .forGetter(VegetationSpreadBehavior::groundBlocks),
                    BlockStateProvider.CODEC.fieldOf("vegetation").forGetter(VegetationSpreadBehavior::vegetation),
                    RegistryCodecs.holderSet(Registries.BLOCK)
                            .fieldOf("bonemealable_blocks")
                            .forGetter(VegetationSpreadBehavior::bonemealableBlocks),
                    Codec.intRange(1, 1024).fieldOf("attempts").forGetter(VegetationSpreadBehavior::attempts),
                    Codec.intRange(1, 128).fieldOf("attempts_per_step").forGetter(VegetationSpreadBehavior::attemptsPerStep))
            .apply(instance, VegetationSpreadBehavior::new));

    @Override
    public MapCodec<VegetationSpreadBehavior> codec() {
        return CODEC;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int attempt = 0; attempt < this.attempts; ++attempt) {
            BlockPos offsetPos = this.getOffsetPos(level, random, mutablePos.setWithOffset(pos, Direction.UP), attempt);
            if (offsetPos != null) {
                BlockState randomState = level.getBlockState(offsetPos);
                if (this.bonemealableBlocks.contains(randomState.typeHolder())
                        && randomState.getBlock() instanceof BonemealableBlock block && random.nextInt(10) == 0) {
                    block.performBonemeal(level, random, offsetPos, randomState, source);
                }

                if (randomState.isAir() && random.nextInt(5) == 0 && level.isEmptyBlock(offsetPos)
                        && offsetPos.getY() > level.getMinY()) {
                    BlockState vegetationState = this.vegetation.value().getState(level, random, offsetPos);
                    level.setBlock(offsetPos, vegetationState, Block.UPDATE_CLIENTS);
                }
            }
        }
    }

    private @Nullable BlockPos getOffsetPos(ServerLevel level, RandomSource random, BlockPos.MutableBlockPos mutablePos, int attempt) {
        for (int step = 0; step < attempt / this.attemptsPerStep; ++step) {
            mutablePos.move(random.nextInt(3) - 1,
                    (random.nextInt(3) - 1) * random.nextInt(3) / 2,
                    random.nextInt(3) - 1);
            if (!this.groundBlocks.contains(level.getBlockState(mutablePos.below()).typeHolder())
                    || level.getBlockState(mutablePos).isCollisionShapeFullBlock(level, mutablePos)) {
                return null;
            }
        }

        return mutablePos.immutable();
    }
}
