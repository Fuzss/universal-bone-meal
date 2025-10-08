package fuzs.universalbonemeal.fabric.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.universalbonemeal.UniversalBoneMeal;
import fuzs.universalbonemeal.client.UniversalBoneMealClient;
import net.fabricmc.api.ClientModInitializer;

public class UniversalBoneMealFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(UniversalBoneMeal.MOD_ID, UniversalBoneMealClient::new);
    }
}
