package fuzs.universalbonemeal.neoforge;

import fuzs.puzzleslib.common.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v3.core.DataProviderBuilder;
import fuzs.universalbonemeal.common.UniversalBoneMeal;
import fuzs.universalbonemeal.common.data.loot.ModBlockInteractLootProvider;
import fuzs.universalbonemeal.common.data.tags.ModBlockTagsProvider;
import fuzs.universalbonemeal.common.init.BoneMealBehaviors;
import fuzs.universalbonemeal.common.init.BoneMealBlockPredicates;
import fuzs.universalbonemeal.common.init.CoralPlacedFeatures;
import fuzs.universalbonemeal.common.init.ModRegistry;
import fuzs.universalbonemeal.common.world.level.block.bonemeal.behavior.BoneMealBehavior;
import fuzs.universalbonemeal.neoforge.data.ModDataMapProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.fml.common.Mod;

@Mod(UniversalBoneMeal.MOD_ID)
public class UniversalBoneMealNeoForge {

    public UniversalBoneMealNeoForge() {
        ModConstructor.construct(UniversalBoneMeal.MOD_ID, UniversalBoneMeal::new);
        DataProviderBuilder.of(UniversalBoneMeal.MOD_ID)
                .addWorldBootstrap(Registries.PLACED_FEATURE, CoralPlacedFeatures::bootstrap)
                .addWorldBootstrap(BoneMealBehavior.REGISTRY_KEY, BoneMealBehaviors::bootstrap)
                .addWorldBootstrap(ModRegistry.PREDICATE_REGISTRY_KEY, BoneMealBlockPredicates::bootstrap)
                .addProvider(ModBlockTagsProvider::new)
                .addProvider(ModDataMapProvider::new)
                .addLootProvider(ModBlockInteractLootProvider::new, LootContextParamSets.BLOCK_INTERACT);
    }
}
