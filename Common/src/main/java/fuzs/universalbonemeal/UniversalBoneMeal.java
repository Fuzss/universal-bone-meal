package fuzs.universalbonemeal;

import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.event.v1.entity.player.BonemealCallback;
import fuzs.puzzleslib.api.event.v1.server.TagsUpdatedCallback;
import fuzs.universalbonemeal.config.ServerConfig;
import fuzs.universalbonemeal.handler.BonemealHandler;
import fuzs.universalbonemeal.init.ModRegistry;
import fuzs.universalbonemeal.world.level.block.behavior.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
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
        ModRegistry.touch();
        registerHandlers();
    }

    private static void registerHandlers() {
        BonemealCallback.EVENT.register(BonemealHandler::onBonemeal);
        TagsUpdatedCallback.EVENT.register((registryAccess, client) -> {
            if (client) return;
            BonemealHandler.invalidate();
            CoralBehavior.invalidate();
        });
    }

    @Override
    public void onCommonSetup() {
        registerBonemealBehaviors();
    }

    private static void registerBonemealBehaviors() {
        BonemealHandler.registerBehavior(Blocks.CACTUS, SimpleGrowingPlantBehavior::new, () -> CONFIG.get(ServerConfig.class).allowCactus);
        BonemealHandler.registerBehavior(Blocks.SUGAR_CANE, SimpleGrowingPlantBehavior::new, () -> CONFIG.get(ServerConfig.class).allowSugarCane);
        BonemealHandler.registerBehavior(Blocks.VINE, VineBehavior::new, () -> CONFIG.get(ServerConfig.class).allowVines);
        BonemealHandler.registerBehavior(Blocks.NETHER_WART, NetherWartBehavior::new, () -> CONFIG.get(ServerConfig.class).allowNetherWart);
        BonemealHandler.registerBehavior(Set.of(Blocks.MELON_STEM, Blocks.PUMPKIN_STEM), FruitStemBehavior::new, () -> CONFIG.get(ServerConfig.class).allowFruitStems);
        BonemealHandler.registerBehavior(Blocks.LILY_PAD, () -> new SimpleSpreadBehavior(4, 3), () -> CONFIG.get(ServerConfig.class).allowLilyPad);
        BonemealHandler.registerBehavior(Blocks.DEAD_BUSH, () -> new SimpleSpreadBehavior(4, 2), () -> CONFIG.get(ServerConfig.class).allowDeadBush);
        BonemealHandler.registerBehavior(BlockTags.FLOWERS, ModRegistry.FERTILIZER_RESISTANT_FLOWER_BLOCK_TAG, () -> new SimpleSpreadBehavior(3, 1), () -> CONFIG.get(ServerConfig.class).allowSmallFlowers);
        BonemealHandler.registerBehavior(BlockTags.CORAL_PLANTS, CoralBehavior::new, () -> CONFIG.get(ServerConfig.class).allowCorals);
        BonemealHandler.registerBehavior(Blocks.CHORUS_FLOWER, ChorusFlowerBehavior::new, () -> CONFIG.get(ServerConfig.class).allowChorus);
        BonemealHandler.registerBehavior(Blocks.CHORUS_PLANT, ChorusPlantBehavior::new, () -> CONFIG.get(ServerConfig.class).allowChorus);
        BonemealHandler.registerBehavior(Blocks.MYCELIUM, MyceliumBehavior::new, () -> CONFIG.get(ServerConfig.class).allowMycelium);
        BonemealHandler.registerBehavior(Set.of(Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.DIRT_PATH), DirtBehavior::new, () -> CONFIG.get(ServerConfig.class).allowDirt);
        BonemealHandler.registerBehavior(Blocks.PODZOL, PodzolBehavior::new, () -> CONFIG.get(ServerConfig.class).allowPodzol);
        BonemealHandler.registerBehavior(Blocks.SPORE_BLOSSOM, () -> new PopResourceBehavior(Direction.DOWN), () -> CONFIG.get(ServerConfig.class).allowSporeBlossom);
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
