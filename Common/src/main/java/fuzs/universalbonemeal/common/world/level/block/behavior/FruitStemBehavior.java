package fuzs.universalbonemeal.common.world.level.block.behavior;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public record FruitStemBehavior(HolderSet<Block> fruitSupportBlocks, int maxAge) implements BoneMealBehavior {
    public static final MapCodec<FruitStemBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    RegistryCodecs.holderSet(Registries.BLOCK)
                            .fieldOf("fruit_support_blocks")
                            .forGetter(FruitStemBehavior::fruitSupportBlocks),
                    Codec.intRange(1, BlockStateProperties.MAX_AGE_7).fieldOf("max_age").forGetter(FruitStemBehavior::maxAge))
            .apply(instance, FruitStemBehavior::new));

    @Override
    public MapCodec<FruitStemBehavior> codec() {
        return CODEC;
    }

    /**
     * @see StemBlock#randomTick(BlockState, ServerLevel, BlockPos, RandomSource)
     */
    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, BonemealSource source) {
        // Let vanilla run if this is not the case.
        if (!state.hasProperty(StemBlock.AGE) || state.getValue(StemBlock.AGE) < this.maxAge) {
            return false;
        }

        // No need to check if attached to a fruit already, since attached stems are completely different block.
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos fruitPos = pos.relative(direction);
            BlockState soilState = level.getBlockState(fruitPos.below());
            if (level.getBlockState(fruitPos).isAir() && this.fruitSupportBlocks.contains(soilState.typeHolder())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        // growing fruit from stem blocks takes forever, let's speed it up a little
        while (level.getBlockState(pos) == state && random.nextInt(3) != 0) {
            state.randomTick(level, pos, random);
        }
    }
}
