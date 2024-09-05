package fuzs.universalbonemeal.data;

import fuzs.puzzleslib.api.data.v2.AbstractTagProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.universalbonemeal.init.ModRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.Blocks;

public class ModBlockTagProvider extends AbstractTagProvider.Blocks {

    public ModBlockTagProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addTags(HolderLookup.Provider provider) {
        this.tag(ModRegistry.FERTILIZER_RESISTANT_FLOWER_BLOCK_TAG).add(Blocks.WITHER_ROSE, Blocks.TORCHFLOWER, Blocks.PINK_PETALS);
    }
}
