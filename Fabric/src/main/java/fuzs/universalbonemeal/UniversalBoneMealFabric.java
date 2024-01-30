package fuzs.universalbonemeal;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.fabricmc.api.ModInitializer;

public class UniversalBoneMealFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConstructor.construct(UniversalBoneMeal.MOD_ID, UniversalBoneMeal::new);
    }
}
