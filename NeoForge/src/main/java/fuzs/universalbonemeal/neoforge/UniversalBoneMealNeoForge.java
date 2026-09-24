package fuzs.universalbonemeal.neoforge;

import fuzs.puzzleslib.common.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v3.core.DataProviderBuilder;
import fuzs.universalbonemeal.common.UniversalBoneMeal;
import fuzs.universalbonemeal.common.init.BoneMealBehaviors;
import fuzs.universalbonemeal.common.data.tags.ModBlockTagsProvider;
import fuzs.universalbonemeal.common.init.ModRegistry;
import fuzs.universalbonemeal.neoforge.data.ModDataMapProvider;
import net.neoforged.fml.common.Mod;

@Mod(UniversalBoneMeal.MOD_ID)
public class UniversalBoneMealNeoForge {

    public UniversalBoneMealNeoForge() {
        ModConstructor.construct(UniversalBoneMeal.MOD_ID, UniversalBoneMeal::new);
        DataProviderBuilder.of(UniversalBoneMeal.MOD_ID)
                .add(ModRegistry.BONE_MEAL_BEHAVIOR_REGISTRY_KEY, BoneMealBehaviors::bootstrap)
                .addProvider(ModBlockTagsProvider::new)
                .addProvider(ModDataMapProvider::new);
    }
}
