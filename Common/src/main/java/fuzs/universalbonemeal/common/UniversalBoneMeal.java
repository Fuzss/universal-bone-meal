package fuzs.universalbonemeal.common;

import fuzs.puzzleslib.common.api.core.v1.ModConstructor;
import fuzs.puzzleslib.common.api.core.v1.context.DataPackRegistriesContext;
import fuzs.puzzleslib.common.api.core.v1.context.GameRegistriesContext;
import fuzs.puzzleslib.common.api.core.v1.context.PayloadTypesContext;
import fuzs.puzzleslib.common.api.event.v1.level.UseBoneMealCallback;
import fuzs.universalbonemeal.common.handler.UseBoneMealHandler;
import fuzs.universalbonemeal.common.init.ModRegistry;
import fuzs.universalbonemeal.common.network.ClientboundGrowthParticlesMessage;
import fuzs.universalbonemeal.common.world.level.block.behavior.BoneMealBehavior;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UniversalBoneMeal implements ModConstructor {
    public static final String MOD_ID = "universalbonemeal";
    public static final String MOD_NAME = "Universal Bone Meal";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    @Override
    public void onConstructMod() {
        ModRegistry.bootstrap();
        UseBoneMealCallback.EVENT.register(UseBoneMealHandler::onUseBoneMeal);
    }

    @Override
    public void onRegisterGameRegistries(GameRegistriesContext context) {
        context.registerRegistry(ModRegistry.BONE_MEAL_BEHAVIOR_TYPE_REGISTRY);
    }

    @Override
    public void onRegisterDataPackRegistries(DataPackRegistriesContext context) {
        context.registerSyncedRegistry(BoneMealBehavior.REGISTRY_KEY,
                BoneMealBehavior.DIRECT_CODEC);
    }

    @Override
    public void onRegisterPayloadTypes(PayloadTypesContext context) {
        context.playToClient(ClientboundGrowthParticlesMessage.class, ClientboundGrowthParticlesMessage.STREAM_CODEC);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
