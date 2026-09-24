package fuzs.universalbonemeal.common.data.loot;

import fuzs.puzzleslib.common.api.data.v3.loot.AbstractLootSubProvider;
import fuzs.universalbonemeal.common.init.ModRegistry;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

public class ModLootProvider extends AbstractLootSubProvider {

    public ModLootProvider(LootTableSubProvider.Context output) {
        super(output);
    }

    @Override
    public void generate() {
        this.output.accept(ModRegistry.SPORE_BLOSSOM_LOOT_TABLE,
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .setRolls(ContextIntProviders.exactly(1))
                                .add(LootItem.lootTableItem(Items.SPORE_BLOSSOM))));
    }
}
