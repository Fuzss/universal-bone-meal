package fuzs.universalbonemeal.fabric.client;

import fuzs.puzzleslib.common.api.client.core.v1.ClientModConstructor;
import fuzs.universalbonemeal.common.UniversalBoneMeal;
import fuzs.universalbonemeal.common.client.UniversalBoneMealClient;
import net.fabricmc.api.ClientModInitializer;

public class UniversalBoneMealFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(UniversalBoneMeal.MOD_ID, UniversalBoneMealClient::new);
    }
}
