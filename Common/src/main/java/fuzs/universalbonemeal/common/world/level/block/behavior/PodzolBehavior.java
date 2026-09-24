package fuzs.universalbonemeal.common.world.level.block.behavior;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record PodzolBehavior(HolderSet<Block> groundBlocks,
                             Holder<BlockStateProvider> vegetation,
                             HolderSet<Block> bonemealableBlocks) implements BoneMealBehavior {
    public static final MapCodec<PodzolBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            RegistryCodecs.holderSet(Registries.BLOCK).fieldOf("ground_blocks").forGetter(PodzolBehavior::groundBlocks),
            BlockStateProvider.CODEC.fieldOf("vegetation").forGetter(PodzolBehavior::vegetation),
            RegistryCodecs.holderSet(Registries.BLOCK)
                    .fieldOf("bonemealable_blocks")
                    .forGetter(PodzolBehavior::bonemealableBlocks)).apply(instance, PodzolBehavior::new));

    @Override
    public MapCodec<PodzolBehavior> codec() {
        return CODEC;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos blockPos, BlockState blockState, BonemealSource bonemealSource) {
        return level.getBlockState(blockPos.above()).isAir();
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource randomSource, BlockPos blockPos, BlockState blockState, BonemealSource bonemealSource) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos blockPos, BlockState blockState, BonemealSource bonemealSource) {
        label:
        for (int i = 0; i < 128; ++i) {
            BlockPos randomPos = blockPos.above();
            for (int j = 0; j < i / 16; ++j) {
                randomPos = randomPos.offset(random.nextInt(3) - 1,
                        (random.nextInt(3) - 1) * random.nextInt(3) / 2,
                        random.nextInt(3) - 1);
                if (!this.groundBlocks.contains(level.getBlockState(randomPos.below()).typeHolder())
                        || level.getBlockState(randomPos).isCollisionShapeFullBlock(level, randomPos)) {
                    continue label;
                }
            }

            BlockState stateAtRandomPosition = level.getBlockState(randomPos);
            if (this.bonemealableBlocks.contains(stateAtRandomPosition.typeHolder())
                    && stateAtRandomPosition.getBlock() instanceof BonemealableBlock bonemealableBlock
                    && random.nextInt(10) == 0) {
                bonemealableBlock.performBonemeal(level, random, randomPos, stateAtRandomPosition, bonemealSource);
            }

            if (stateAtRandomPosition.isAir()) {
                if (random.nextInt(5) == 0) {
                    if (level.isEmptyBlock(randomPos) && randomPos.getY() > level.getMinY()) {
                        BlockState stateToPlace = this.vegetation.value().getState(level, random, randomPos);
                        level.setBlock(randomPos, stateToPlace, Block.UPDATE_CLIENTS);
                    }
                }

            }
        }
    }
}
