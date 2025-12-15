package fuzs.universalbonemeal.neoforge;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import fuzs.universalbonemeal.UniversalBoneMeal;
import fuzs.universalbonemeal.data.ModBlockTagProvider;
import net.neoforged.fml.common.Mod;

@Mod(UniversalBoneMeal.MOD_ID)
public class UniversalBoneMealNeoForge {

    public UniversalBoneMealNeoForge() {
        ModConstructor.construct(UniversalBoneMeal.MOD_ID, UniversalBoneMeal::new);
        DataProviderHelper.registerDataProviders(UniversalBoneMeal.MOD_ID, ModBlockTagProvider::new);
    }
}
