package fuzs.universalbonemeal.common.init;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import fuzs.multiloaderdataextensions.common.api.v2.DataMapRegistrar;
import fuzs.multiloaderdataextensions.common.api.v2.DataMapToken;
import fuzs.puzzleslib.common.api.init.v3.registry.RegistryFactory;
import fuzs.puzzleslib.common.api.init.v3.registry.RegistryManager;
import fuzs.puzzleslib.common.api.init.v3.tags.TagFactory;
import fuzs.universalbonemeal.common.UniversalBoneMeal;
import fuzs.universalbonemeal.common.world.level.block.behavior.BoneMealBehavior;
import fuzs.universalbonemeal.common.world.level.block.behavior.CactusBehavior;
import fuzs.universalbonemeal.common.world.level.block.behavior.ChorusFlowerBehavior;
import fuzs.universalbonemeal.common.world.level.block.behavior.ChorusPlantBehavior;
import fuzs.universalbonemeal.common.world.level.block.behavior.CoralBehavior;
import fuzs.universalbonemeal.common.world.level.block.behavior.DirtBehavior;
import fuzs.universalbonemeal.common.world.level.block.behavior.FruitStemBehavior;
import fuzs.universalbonemeal.common.world.level.block.behavior.MyceliumBehavior;
import fuzs.universalbonemeal.common.world.level.block.behavior.NetherWartBehavior;
import fuzs.universalbonemeal.common.world.level.block.behavior.PodzolBehavior;
import fuzs.universalbonemeal.common.world.level.block.behavior.PopResourceBehavior;
import fuzs.universalbonemeal.common.world.level.block.behavior.SimpleGrowingPlantBehavior;
import fuzs.universalbonemeal.common.world.level.block.behavior.SimpleSpreadBehavior;
import fuzs.universalbonemeal.common.world.level.block.behavior.VineBehavior;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;

public class ModRegistry {
    static final TagFactory TAGS = TagFactory.make(UniversalBoneMeal.MOD_ID);
    public static final TagKey<Block> FERTILIZER_RESISTANT_FLOWERS_BLOCK_TAG = TAGS.registerBlockTag(
            "fertilizer_resistant_flowers");

    static final RegistryManager REGISTRIES = RegistryManager.from(UniversalBoneMeal.MOD_ID);
    public static final ResourceKey<Registry<MapCodec<? extends BoneMealBehavior>>> BONE_MEAL_BEHAVIOR_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(
            UniversalBoneMeal.id("bone_meal_behavior_type"));
    public static final Registry<MapCodec<? extends BoneMealBehavior>> BONE_MEAL_BEHAVIOR_TYPE_REGISTRY = RegistryFactory.INSTANCE.create(
            BONE_MEAL_BEHAVIOR_TYPE_REGISTRY_KEY);
    public static final ResourceKey<Registry<BoneMealBehavior>> BONE_MEAL_BEHAVIOR_REGISTRY_KEY = ResourceKey.createRegistryKey(
            UniversalBoneMeal.id("bone_meal_behavior"));
    public static final Codec<BoneMealBehavior> BONE_MEAL_BEHAVIOR_CODEC = BONE_MEAL_BEHAVIOR_TYPE_REGISTRY.byNameCodec()
            .dispatch(BoneMealBehavior::codec, Function.identity());

    public static final DataMapToken<Block, Holder<BoneMealBehavior>> BONE_MEAL_BEHAVIORS_DATA_MAP = DataMapRegistrar.register(
            UniversalBoneMeal.id("bone_meal_behaviors"),
            Registries.BLOCK,
            RegistryCodecs.holder(BONE_MEAL_BEHAVIOR_REGISTRY_KEY, BONE_MEAL_BEHAVIOR_CODEC, false),
            RegistryCodecs.holder(BONE_MEAL_BEHAVIOR_REGISTRY_KEY, BONE_MEAL_BEHAVIOR_CODEC, false),
            true);

    public static void bootstrap() {
        registerBoneMealBehaviorType("growing_plant", SimpleGrowingPlantBehavior.CODEC);
        registerBoneMealBehaviorType("hanging_plant", VineBehavior.CODEC);
        registerBoneMealBehaviorType("cactus", CactusBehavior.CODEC);
        registerBoneMealBehaviorType("crop_growth", NetherWartBehavior.CODEC);
        registerBoneMealBehaviorType("fruit_stem", FruitStemBehavior.CODEC);
        registerBoneMealBehaviorType("neighbor_spread", SimpleSpreadBehavior.CODEC);
        registerBoneMealBehaviorType("vegetation_scatter", MyceliumBehavior.CODEC);
        registerBoneMealBehaviorType("coral_tree", CoralBehavior.CODEC);
        registerBoneMealBehaviorType("chorus_flower", ChorusFlowerBehavior.CODEC);
        registerBoneMealBehaviorType("chorus_plant", ChorusPlantBehavior.CODEC);
        registerBoneMealBehaviorType("dirt_conversion", DirtBehavior.CODEC);
        registerBoneMealBehaviorType("podzol_vegetation", PodzolBehavior.CODEC);
        registerBoneMealBehaviorType("pop_resource", PopResourceBehavior.CODEC);
    }

    private static void registerBoneMealBehaviorType(String path, MapCodec<? extends BoneMealBehavior> codec) {
        REGISTRIES.register(BONE_MEAL_BEHAVIOR_TYPE_REGISTRY_KEY, path, () -> codec);
    }
}
