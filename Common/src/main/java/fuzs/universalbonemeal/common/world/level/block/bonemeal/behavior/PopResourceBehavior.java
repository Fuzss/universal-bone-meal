package fuzs.universalbonemeal.common.world.level.block.bonemeal.behavior;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.BlockTransformer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;

public record PopResourceBehavior(ResourceKey<LootTable> loot, Drop drop) implements BoneMealBehavior {
    public static final MapCodec<PopResourceBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LootTable.KEY_CODEC.fieldOf("loot").forGetter(PopResourceBehavior::loot),
            Drop.MAP_CODEC.forGetter(PopResourceBehavior::drop)).apply(instance, PopResourceBehavior::new));

    @Override
    public MapCodec<PopResourceBehavior> codec() {
        return CODEC;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        Block.dropFromBlockInteractLootTable(level,
                this.loot,
                pos,
                state,
                level.getBlockEntity(pos),
                ItemStack.EMPTY,
                null,
                (ServerLevel serverLevel, ItemStack itemStack) -> {
                    this.drop.pop(serverLevel, pos, itemStack);
                });
    }

    public interface Drop {
        MapCodec<Drop> MAP_CODEC = BlockTransformer.DropStrategy.CODEC.dispatchMap("drop_strategy",
                Drop::type,
                Drop::codec);

        BlockTransformer.DropStrategy type();

        MapCodec<? extends Drop> codec();

        void pop(Level level, BlockPos pos, ItemStack stack);

        static MapCodec<? extends Drop> codec(BlockTransformer.DropStrategy dropStrategy) {
            return switch (dropStrategy) {
                case CLICKED_FACE -> ClickedFace.CODEC;
                case FROM_MIDDLE -> FromMiddle.CODEC;
            };
        }

        record FromMiddle() implements Drop {
            public static final MapCodec<FromMiddle> CODEC = MapCodec.unit(FromMiddle::new);

            @Override
            public BlockTransformer.DropStrategy type() {
                return BlockTransformer.DropStrategy.FROM_MIDDLE;
            }

            @Override
            public MapCodec<FromMiddle> codec() {
                return CODEC;
            }

            @Override
            public void pop(Level level, BlockPos pos, ItemStack stack) {
                this.type().pop(level, pos, Direction.UP, stack);
            }
        }

        record ClickedFace(Direction direction) implements Drop {
            public static final MapCodec<ClickedFace> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                            Direction.CODEC.fieldOf("direction").forGetter(ClickedFace::direction))
                    .apply(instance, ClickedFace::new));

            @Override
            public BlockTransformer.DropStrategy type() {
                return BlockTransformer.DropStrategy.CLICKED_FACE;
            }

            @Override
            public MapCodec<ClickedFace> codec() {
                return CODEC;
            }

            @Override
            public void pop(Level level, BlockPos pos, ItemStack stack) {
                this.type().pop(level, pos, this.direction, stack);
            }
        }
    }
}
