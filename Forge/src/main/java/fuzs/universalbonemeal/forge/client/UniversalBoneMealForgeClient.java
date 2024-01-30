package fuzs.universalbonemeal.forge.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.universalbonemeal.UniversalBoneMeal;
import fuzs.universalbonemeal.client.UniversalBoneMealClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = UniversalBoneMeal.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class UniversalBoneMealForgeClient {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ClientModConstructor.construct(UniversalBoneMeal.MOD_ID, UniversalBoneMealClient::new);
    }
}
