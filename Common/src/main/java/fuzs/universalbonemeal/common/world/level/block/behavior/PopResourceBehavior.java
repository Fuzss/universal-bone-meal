package fuzs.universalbonemeal.common.world.level.block.behavior;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.BlockTransformer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;

public record PopResourceBehavior(ResourceKey<LootTable> loot,
                                  BlockTransformer.DropStrategy dropStrategy,
                                  Direction direction) implements BoneMealBehavior {
    public static final MapCodec<PopResourceBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    LootTable.KEY_CODEC.fieldOf("loot").forGetter(PopResourceBehavior::loot),
                    BlockTransformer.DropStrategy.CODEC.fieldOf("drop_strategy").forGetter(PopResourceBehavior::dropStrategy),
                    Direction.CODEC.fieldOf("direction").forGetter(PopResourceBehavior::direction))
            .apply(instance, PopResourceBehavior::new));

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
                    this.dropStrategy.pop(serverLevel, pos, this.direction, itemStack);
                });
    }
}
