package fuzs.universalbonemeal.common.data.tags;

import fuzs.puzzleslib.common.api.data.v3.core.DataProviderContext;
import fuzs.puzzleslib.common.api.data.v3.tags.AbstractTagsProvider;
import fuzs.universalbonemeal.common.init.ModRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.references.BlockItemIds;
import net.minecraft.world.level.block.Block;

public class ModBlockTagsProvider extends AbstractTagsProvider<Block> {

    public ModBlockTagsProvider(DataProviderContext context) {
        super(Registries.BLOCK, context);
    }

    @Override
    public void addTags(HolderLookup.Provider provider) {
        this.tag(ModRegistry.FERTILIZER_RESISTANT_FLOWERS_BLOCK_TAG)
                .add(BlockItemIds.WITHER_ROSE.block(),
                        BlockItemIds.TORCHFLOWER.block(),
                        BlockItemIds.PINK_PETALS.block());
    }
}
