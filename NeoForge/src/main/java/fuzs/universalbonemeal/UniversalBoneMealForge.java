package fuzs.universalbonemeal;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod(UniversalBoneMeal.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class UniversalBoneMealForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ModConstructor.construct(UniversalBoneMeal.MOD_ID, UniversalBoneMeal::new);
    }
}
