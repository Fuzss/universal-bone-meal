package fuzs.universalbonemeal.neoforge;

import fuzs.puzzleslib.common.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import fuzs.universalbonemeal.common.UniversalBoneMeal;
import fuzs.universalbonemeal.common.data.ModBlockTagProvider;
import net.neoforged.fml.common.Mod;

@Mod(UniversalBoneMeal.MOD_ID)
public class UniversalBoneMealNeoForge {

    public UniversalBoneMealNeoForge() {
        ModConstructor.construct(UniversalBoneMeal.MOD_ID, UniversalBoneMeal::new);
        DataProviderHelper.registerDataProviders(UniversalBoneMeal.MOD_ID, ModBlockTagProvider::new);
    }
}
