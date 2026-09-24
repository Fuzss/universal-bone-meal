package fuzs.universalbonemeal.common.world.level.block.behavior;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record PodzolVegetationBehavior(HolderSet<Block> groundBlocks,
                                       Holder<BlockStateProvider> vegetation,
                                       HolderSet<Block> bonemealableBlocks,
                                       int attempts,
                                       int attemptsPerStep) implements BoneMealBehavior {
    public static final MapCodec<PodzolVegetationBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    RegistryCodecs.holderSet(Registries.BLOCK)
                            .fieldOf("ground_blocks")
                            .forGetter(PodzolVegetationBehavior::groundBlocks),
                    BlockStateProvider.CODEC.fieldOf("vegetation").forGetter(PodzolVegetationBehavior::vegetation),
                    RegistryCodecs.holderSet(Registries.BLOCK)
                            .fieldOf("bonemealable_blocks")
                            .forGetter(PodzolVegetationBehavior::bonemealableBlocks),
                    Codec.intRange(1, 1024).fieldOf("attempts").forGetter(PodzolVegetationBehavior::attempts),
                    Codec.intRange(1, 128).fieldOf("attempts_per_step").forGetter(PodzolVegetationBehavior::attemptsPerStep))
            .apply(instance, PodzolVegetationBehavior::new));

    @Override
    public MapCodec<PodzolVegetationBehavior> codec() {
        return CODEC;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, BonemealSource source) {
        return level.getBlockState(pos.above()).isAir();
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        label:
        for (int attempt = 0; attempt < this.attempts; ++attempt) {
            BlockPos randomPos = pos.above();
            for (int step = 0; step < attempt / this.attemptsPerStep; ++step) {
                randomPos = randomPos.offset(random.nextInt(3) - 1,
                        (random.nextInt(3) - 1) * random.nextInt(3) / 2,
                        random.nextInt(3) - 1);
                if (!this.groundBlocks.contains(level.getBlockState(randomPos.below()).typeHolder())
                        || level.getBlockState(randomPos).isCollisionShapeFullBlock(level, randomPos)) {
                    continue label;
                }
            }

            BlockState randomState = level.getBlockState(randomPos);
            if (this.bonemealableBlocks.contains(randomState.typeHolder())
                    && randomState.getBlock() instanceof BonemealableBlock block && random.nextInt(10) == 0) {
                block.performBonemeal(level, random, randomPos, randomState, source);
            }

            if (randomState.isAir() && random.nextInt(5) == 0 && level.isEmptyBlock(randomPos)
                    && randomPos.getY() > level.getMinY()) {
                BlockState vegetationState = this.vegetation.value().getState(level, random, randomPos);
                level.setBlock(randomPos, vegetationState, Block.UPDATE_CLIENTS);
            }
        }
    }
}
