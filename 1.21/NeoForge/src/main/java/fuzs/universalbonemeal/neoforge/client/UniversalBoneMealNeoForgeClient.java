package fuzs.universalbonemeal.neoforge.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.universalbonemeal.UniversalBoneMeal;
import fuzs.universalbonemeal.client.UniversalBoneMealClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = UniversalBoneMeal.MOD_ID, dist = Dist.CLIENT)
public class UniversalBoneMealNeoForgeClient {

    public UniversalBoneMealNeoForgeClient() {
        ClientModConstructor.construct(UniversalBoneMeal.MOD_ID, UniversalBoneMealClient::new);
    }
}
