package fuzs.universalbonemeal;

import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.context.PayloadTypesContext;
import net.minecraft.resources.Identifier;
import fuzs.puzzleslib.api.event.v1.level.UseBoneMealCallback;
import fuzs.puzzleslib.api.event.v1.server.TagsUpdatedCallback;
import fuzs.universalbonemeal.config.ServerConfig;
import fuzs.universalbonemeal.handler.UseBoneMealHandler;
import fuzs.universalbonemeal.init.ModRegistry;
import fuzs.universalbonemeal.network.ClientboundGrowthParticlesMessage;
import fuzs.universalbonemeal.world.level.block.behavior.*;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class UniversalBoneMeal implements ModConstructor {
    public static final String MOD_ID = "universalbonemeal";
    public static final String MOD_NAME = "Universal Bone Meal";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static final ConfigHolder CONFIG = ConfigHolder.builder(MOD_ID).server(ServerConfig.class);

    @Override
    public void onConstructMod() {
        ModRegistry.bootstrap();
    }

    @Override
    public void onCommonSetup() {
        registerEventHandlers();
        registerBonemealBehaviors();
    }

    private static void registerEventHandlers() {
        UseBoneMealCallback.EVENT.register(UseBoneMealHandler::onUseBoneMeal);
        TagsUpdatedCallback.EVENT.register((HolderLookup.Provider registries, boolean client) -> {
            if (client) return;
            UseBoneMealHandler.invalidate();
            CoralBehavior.invalidate();
        });
    }

    private static void registerBonemealBehaviors() {
        UseBoneMealHandler.registerBehavior(Blocks.CACTUS,
                CactusBehavior::new,
                () -> CONFIG.get(ServerConfig.class).allowCactus);
        UseBoneMealHandler.registerBehavior(Blocks.SUGAR_CANE,
                SimpleGrowingPlantBehavior::new,
                () -> CONFIG.get(ServerConfig.class).allowSugarCane);
        UseBoneMealHandler.registerBehavior(Blocks.VINE,
                VineBehavior::new,
                () -> CONFIG.get(ServerConfig.class).allowVines);
        UseBoneMealHandler.registerBehavior(Blocks.NETHER_WART,
                NetherWartBehavior::new,
                () -> CONFIG.get(ServerConfig.class).allowNetherWart);
        UseBoneMealHandler.registerBehavior(Set.of(Blocks.MELON_STEM, Blocks.PUMPKIN_STEM),
                FruitStemBehavior::new,
                () -> CONFIG.get(ServerConfig.class).allowFruitStems);
        UseBoneMealHandler.registerBehavior(Blocks.LILY_PAD,
                () -> new SimpleSpreadBehavior(4, 3),
                () -> CONFIG.get(ServerConfig.class).allowLilyPad);
        UseBoneMealHandler.registerBehavior(Blocks.DEAD_BUSH,
                () -> new SimpleSpreadBehavior(4, 2),
                () -> CONFIG.get(ServerConfig.class).allowDeadBush);
        UseBoneMealHandler.registerBehavior(BlockTags.SMALL_FLOWERS,
                ModRegistry.FERTILIZER_RESISTANT_FLOWERS_BLOCK_TAG,
                () -> new SimpleSpreadBehavior(3, 1),
                () -> CONFIG.get(ServerConfig.class).allowSmallFlowers);
        UseBoneMealHandler.registerBehavior(BlockTags.CORAL_PLANTS,
                CoralBehavior::new,
                () -> CONFIG.get(ServerConfig.class).allowCorals);
        UseBoneMealHandler.registerBehavior(Blocks.CHORUS_FLOWER,
                ChorusFlowerBehavior::new,
                () -> CONFIG.get(ServerConfig.class).allowChorus);
        UseBoneMealHandler.registerBehavior(Blocks.CHORUS_PLANT,
                ChorusPlantBehavior::new,
                () -> CONFIG.get(ServerConfig.class).allowChorus);
        UseBoneMealHandler.registerBehavior(Blocks.MYCELIUM,
                MyceliumBehavior::new,
                () -> CONFIG.get(ServerConfig.class).allowMycelium);
        UseBoneMealHandler.registerBehavior(Set.of(Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.DIRT_PATH),
                DirtBehavior::new,
                () -> CONFIG.get(ServerConfig.class).allowDirt);
        UseBoneMealHandler.registerBehavior(Blocks.PODZOL,
                PodzolBehavior::new,
                () -> CONFIG.get(ServerConfig.class).allowPodzol);
        UseBoneMealHandler.registerBehavior(Blocks.SPORE_BLOSSOM,
                () -> new PopResourceBehavior(Direction.DOWN),
                () -> CONFIG.get(ServerConfig.class).allowSporeBlossom);
    }

    @Override
    public void onRegisterPayloadTypes(PayloadTypesContext context) {
        context.playToClient(ClientboundGrowthParticlesMessage.class, ClientboundGrowthParticlesMessage.STREAM_CODEC);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
