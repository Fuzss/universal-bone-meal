package fuzs.universalbonemeal.neoforge.data;

import fuzs.multiloaderdataextensions.neoforge.api.v2.NeoForgeDataMapToken;
import fuzs.puzzleslib.common.api.data.v3.core.DataProviderContext;
import fuzs.universalbonemeal.common.init.BoneMealBehaviors;
import fuzs.universalbonemeal.common.init.ModRegistry;
import fuzs.universalbonemeal.common.world.level.block.behavior.BoneMealBehavior;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.DataMapProvider;

import java.util.concurrent.CompletableFuture;

public class ModDataMapProvider extends DataMapProvider {

    public ModDataMapProvider(DataProviderContext context) {
        this(context.getPackOutput(), context.getWorldRegistries());
    }

    public ModDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider registries) {
        HolderLookup.RegistryLookup<BoneMealBehavior> lookup = registries.lookupOrThrow(BoneMealBehavior.REGISTRY_KEY);
        this.builder(NeoForgeDataMapToken.unwrap(ModRegistry.BONE_MEAL_BEHAVIORS_DATA_MAP))
                .add(Blocks.CACTUS.builtInRegistryHolder(),
                        new BoneMealBehavior.Configured(lookup.getOrThrow(BoneMealBehaviors.CACTUS)),
                        false)
                .add(Blocks.SUGAR_CANE.builtInRegistryHolder(),
                        new BoneMealBehavior.Configured(lookup.getOrThrow(BoneMealBehaviors.SUGAR_CANE)),
                        false)
                .add(Blocks.VINE.builtInRegistryHolder(),
                        new BoneMealBehavior.Configured(lookup.getOrThrow(BoneMealBehaviors.VINES)),
                        false)
                .add(Blocks.NETHER_WART.builtInRegistryHolder(),
                        new BoneMealBehavior.Configured(lookup.getOrThrow(BoneMealBehaviors.NETHER_WART)),
                        false)
                .add(Blocks.MELON_STEM.builtInRegistryHolder(),
                        new BoneMealBehavior.Configured(lookup.getOrThrow(BoneMealBehaviors.MELON_STEM)),
                        false)
                .add(Blocks.PUMPKIN_STEM.builtInRegistryHolder(),
                        new BoneMealBehavior.Configured(lookup.getOrThrow(BoneMealBehaviors.PUMPKIN_STEM)),
                        false)
                .add(Blocks.LILY_PAD.builtInRegistryHolder(),
                        new BoneMealBehavior.Configured(lookup.getOrThrow(BoneMealBehaviors.LILY_PAD)),
                        false)
                .add(Blocks.DEAD_BUSH.builtInRegistryHolder(),
                        new BoneMealBehavior.Configured(lookup.getOrThrow(BoneMealBehaviors.DEAD_BUSH)),
                        false)
                .add(BlockTags.SMALL_FLOWERS,
                        new BoneMealBehavior.Configured(lookup.getOrThrow(BoneMealBehaviors.SMALL_FLOWER)),
                        false)
                .add(Blocks.TUBE_CORAL.builtInRegistryHolder(),
                        new BoneMealBehavior.Configured(lookup.getOrThrow(BoneMealBehaviors.TUBE_CORAL)),
                        false)
                .add(Blocks.BRAIN_CORAL.builtInRegistryHolder(),
                        new BoneMealBehavior.Configured(lookup.getOrThrow(BoneMealBehaviors.BRAIN_CORAL)),
                        false)
                .add(Blocks.BUBBLE_CORAL.builtInRegistryHolder(),
                        new BoneMealBehavior.Configured(lookup.getOrThrow(BoneMealBehaviors.BUBBLE_CORAL)),
                        false)
                .add(Blocks.FIRE_CORAL.builtInRegistryHolder(),
                        new BoneMealBehavior.Configured(lookup.getOrThrow(BoneMealBehaviors.FIRE_CORAL)),
                        false)
                .add(Blocks.HORN_CORAL.builtInRegistryHolder(),
                        new BoneMealBehavior.Configured(lookup.getOrThrow(BoneMealBehaviors.HORN_CORAL)),
                        false)
                .add(Blocks.CHORUS_FLOWER.builtInRegistryHolder(),
                        new BoneMealBehavior.Configured(lookup.getOrThrow(BoneMealBehaviors.CHORUS_FLOWER)),
                        false)
                .add(Blocks.CHORUS_PLANT.builtInRegistryHolder(),
                        new BoneMealBehavior.Configured(lookup.getOrThrow(BoneMealBehaviors.CHORUS_PLANT)),
                        false)
                .add(Blocks.MYCELIUM.builtInRegistryHolder(),
                        new BoneMealBehavior.Configured(lookup.getOrThrow(BoneMealBehaviors.MYCELIUM)),
                        false)
                .add(Blocks.DIRT.builtInRegistryHolder(),
                        new BoneMealBehavior.Configured(lookup.getOrThrow(BoneMealBehaviors.DIRT)),
                        false)
                .add(Blocks.COARSE_DIRT.builtInRegistryHolder(),
                        new BoneMealBehavior.Configured(lookup.getOrThrow(BoneMealBehaviors.DIRT)),
                        false)
                .add(Blocks.DIRT_PATH.builtInRegistryHolder(),
                        new BoneMealBehavior.Configured(lookup.getOrThrow(BoneMealBehaviors.DIRT)),
                        false)
                .add(Blocks.PODZOL.builtInRegistryHolder(),
                        new BoneMealBehavior.Configured(lookup.getOrThrow(BoneMealBehaviors.PODZOL)),
                        false)
                .add(Blocks.SPORE_BLOSSOM.builtInRegistryHolder(),
                        new BoneMealBehavior.Configured(lookup.getOrThrow(BoneMealBehaviors.SPORE_BLOSSOM)),
                        false);
    }
}
