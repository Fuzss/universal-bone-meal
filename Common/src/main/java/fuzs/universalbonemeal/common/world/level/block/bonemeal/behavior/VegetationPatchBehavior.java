package fuzs.universalbonemeal.common.world.level.block.bonemeal.behavior;

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
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.jspecify.annotations.Nullable;

/**
 * @see net.minecraft.world.level.levelgen.feature.VegetationPatchFeature
 */
public record VegetationPatchBehavior(HolderSet<Block> replaceable,
                                      Holder<BlockStateProvider> groundState,
                                      HolderSet<Block> bonemealableBlocks,
                                      IntProvider attempts,
                                      IntProvider attemptsPerStep) implements BoneMealBehavior {
    public static final MapCodec<VegetationPatchBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    RegistryCodecs.holderSet(Registries.BLOCK)
                            .fieldOf("replaceable")
                            .forGetter(VegetationPatchBehavior::replaceable),
                    BlockStateProvider.CODEC.fieldOf("ground_state").forGetter(VegetationPatchBehavior::groundState),
                    RegistryCodecs.holderSet(Registries.BLOCK)
                            .fieldOf("bonemealable_blocks")
                            .forGetter(VegetationPatchBehavior::bonemealableBlocks),
                    IntProviders.codec(1, 1024).fieldOf("attempts").forGetter(VegetationPatchBehavior::attempts),
                    IntProviders.codec(1, 128)
                            .fieldOf("attempts_per_step")
                            .forGetter(VegetationPatchBehavior::attemptsPerStep))
            .apply(instance, VegetationPatchBehavior::new));

    @Override
    public MapCodec<VegetationPatchBehavior> codec() {
        return CODEC;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        int attempts = this.attempts.sample(random);
        int attemptsPerStep = this.attemptsPerStep.sample(random);
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int attempt = 0; attempt < attempts; ++attempt) {
            BlockPos offsetPos = this.getOffsetPos(level,
                    random,
                    mutablePos.setWithOffset(pos, Direction.UP),
                    attempt,
                    attemptsPerStep);
            if (offsetPos != null) {
                BlockState randomState = level.getBlockState(offsetPos);
                if (this.bonemealableBlocks.contains(randomState.typeHolder())
                        && randomState.getBlock() instanceof BonemealableBlock block && random.nextInt(10) == 0) {
                    block.performBonemeal(level, random, offsetPos, randomState, source);
                }

                if (randomState.isAir() && random.nextInt(5) == 0 && level.isEmptyBlock(offsetPos)
                        && offsetPos.getY() > level.getMinY()) {
                    BlockState groundState = this.groundState.value().getState(level, random, offsetPos);
                    level.setBlock(offsetPos, groundState, Block.UPDATE_CLIENTS);
                }
            }
        }
    }

    private @Nullable BlockPos getOffsetPos(ServerLevel level, RandomSource random, BlockPos.MutableBlockPos mutablePos, int attempt, int attemptsPerStep) {
        for (int step = 0; step < attempt / attemptsPerStep; ++step) {
            mutablePos.move(random.nextInt(3) - 1,
                    (random.nextInt(3) - 1) * random.nextInt(3) / 2,
                    random.nextInt(3) - 1);
            if (!this.replaceable.contains(level.getBlockState(mutablePos.below()).typeHolder())
                    || level.getBlockState(mutablePos).isCollisionShapeFullBlock(level, mutablePos)) {
                return null;
            }
        }

        return mutablePos.immutable();
    }
}
