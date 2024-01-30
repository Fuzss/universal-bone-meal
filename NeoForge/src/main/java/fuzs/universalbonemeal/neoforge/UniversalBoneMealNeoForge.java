package fuzs.universalbonemeal.neoforge;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import fuzs.universalbonemeal.UniversalBoneMeal;
import fuzs.universalbonemeal.data.ModBlockTagProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;

@Mod(UniversalBoneMeal.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class UniversalBoneMealNeoForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ModConstructor.construct(UniversalBoneMeal.MOD_ID, UniversalBoneMeal::new);
        DataProviderHelper.registerDataProviders(UniversalBoneMeal.MOD_ID, ModBlockTagProvider::new);
    }
}
