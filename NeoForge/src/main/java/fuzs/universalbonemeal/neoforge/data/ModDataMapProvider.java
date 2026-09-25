package fuzs.universalbonemeal.neoforge.data;

import fuzs.multiloaderdataextensions.neoforge.api.v2.NeoForgeDataMapToken;
import fuzs.puzzleslib.common.api.data.v3.core.DataProviderContext;
import fuzs.universalbonemeal.common.init.BoneMealBehaviors;
import fuzs.universalbonemeal.common.init.BoneMealBlockPredicates;
import fuzs.universalbonemeal.common.init.ModRegistry;
import fuzs.universalbonemeal.common.world.level.block.bonemeal.Bonemealable;
import fuzs.universalbonemeal.common.world.level.block.bonemeal.behavior.BoneMealBehavior;
import fuzs.universalbonemeal.common.world.level.block.bonemeal.conditional.SimpleConditionalBoneMealBehavior;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
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
    protected void gather(HolderLookup.Provider lookupProvider) {
        this.builder(NeoForgeDataMapToken.unwrap(ModRegistry.BONE_MEAL_BEHAVIORS_DATA_MAP))
                .add(Blocks.CACTUS.builtInRegistryHolder(),
                        simple(lookupProvider, BoneMealBehaviors.CACTUS, BoneMealBlockPredicates.CACTUS),
                        false)
                .add(Blocks.SUGAR_CANE.builtInRegistryHolder(),
                        simple(lookupProvider, BoneMealBehaviors.SUGAR_CANE, BoneMealBlockPredicates.SUGAR_CANE),
                        false)
                .add(Blocks.VINE.builtInRegistryHolder(),
                        simple(lookupProvider, BoneMealBehaviors.VINE, BoneMealBlockPredicates.VINE),
                        false)
                .add(Blocks.NETHER_WART.builtInRegistryHolder(),
                        simple(lookupProvider, BoneMealBehaviors.NETHER_WART, BoneMealBlockPredicates.NETHER_WART),
                        false)
                .add(Blocks.MELON_STEM.builtInRegistryHolder(),
                        simple(lookupProvider, BoneMealBehaviors.MELON_STEM, BoneMealBlockPredicates.MELON_STEM, true),
                        false)
                .add(Blocks.PUMPKIN_STEM.builtInRegistryHolder(),
                        simple(lookupProvider,
                                BoneMealBehaviors.PUMPKIN_STEM,
                                BoneMealBlockPredicates.PUMPKIN_STEM,
                                true),
                        false)
                .add(Blocks.LILY_PAD.builtInRegistryHolder(), simple(lookupProvider, BoneMealBehaviors.LILY_PAD), false)
                .add(Blocks.DEAD_BUSH.builtInRegistryHolder(),
                        simple(lookupProvider, BoneMealBehaviors.DEAD_BUSH),
                        false)
                .add(BlockTags.SMALL_FLOWERS, simple(lookupProvider, BoneMealBehaviors.SMALL_FLOWER), false)
                .add(Blocks.TUBE_CORAL.builtInRegistryHolder(),
                        simple(lookupProvider, BoneMealBehaviors.TUBE_CORAL, BoneMealBlockPredicates.CORAL),
                        false)
                .add(Blocks.BRAIN_CORAL.builtInRegistryHolder(),
                        simple(lookupProvider, BoneMealBehaviors.BRAIN_CORAL, BoneMealBlockPredicates.CORAL),
                        false)
                .add(Blocks.BUBBLE_CORAL.builtInRegistryHolder(),
                        simple(lookupProvider, BoneMealBehaviors.BUBBLE_CORAL, BoneMealBlockPredicates.CORAL),
                        false)
                .add(Blocks.FIRE_CORAL.builtInRegistryHolder(),
                        simple(lookupProvider, BoneMealBehaviors.FIRE_CORAL, BoneMealBlockPredicates.CORAL),
                        false)
                .add(Blocks.HORN_CORAL.builtInRegistryHolder(),
                        simple(lookupProvider, BoneMealBehaviors.HORN_CORAL, BoneMealBlockPredicates.CORAL),
                        false)
                .add(Blocks.CHORUS_FLOWER.builtInRegistryHolder(),
                        simple(lookupProvider, BoneMealBehaviors.CHORUS_FLOWER, BoneMealBlockPredicates.CHORUS_FLOWER),
                        false)
                .add(Blocks.CHORUS_PLANT.builtInRegistryHolder(),
                        simple(lookupProvider, BoneMealBehaviors.CHORUS_PLANT, BoneMealBlockPredicates.CHORUS_PLANT),
                        false)
                .add(Blocks.MYCELIUM.builtInRegistryHolder(),
                        simple(lookupProvider, BoneMealBehaviors.MYCELIUM, BoneMealBlockPredicates.AIR_ABOVE),
                        false)
                .add(BlockTags.DIRT,
                        simple(lookupProvider, BoneMealBehaviors.DIRT, BoneMealBlockPredicates.NEIGHBOR_CONVERSION),
                        false)
                .add(Blocks.DIRT_PATH.builtInRegistryHolder(),
                        simple(lookupProvider, BoneMealBehaviors.DIRT, BoneMealBlockPredicates.NEIGHBOR_CONVERSION),
                        false)
                .add(Blocks.PODZOL.builtInRegistryHolder(),
                        simple(lookupProvider, BoneMealBehaviors.PODZOL, BoneMealBlockPredicates.AIR_ABOVE),
                        false)
                .add(Blocks.SPORE_BLOSSOM.builtInRegistryHolder(),
                        simple(lookupProvider, BoneMealBehaviors.SPORE_BLOSSOM),
                        false);
    }

    private static Bonemealable simple(HolderLookup.Provider lookupProvider, ResourceKey<BoneMealBehavior> behavior) {
        return new Bonemealable(new SimpleConditionalBoneMealBehavior(lookupProvider.getOrThrow(behavior)), false);
    }

    private static Bonemealable simple(HolderLookup.Provider lookupProvider, ResourceKey<BoneMealBehavior> behavior, ResourceKey<BlockPredicate> predicate) {
        return simple(lookupProvider, behavior, predicate, false);
    }

    private static Bonemealable simple(HolderLookup.Provider lookupProvider, ResourceKey<BoneMealBehavior> behavior, ResourceKey<BlockPredicate> predicate, boolean replace) {
        return new Bonemealable(new SimpleConditionalBoneMealBehavior(lookupProvider.getOrThrow(behavior),
                lookupProvider.getOrThrow(predicate)), replace);
    }
}
