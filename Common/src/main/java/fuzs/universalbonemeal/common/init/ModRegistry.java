package fuzs.universalbonemeal.common.init;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import fuzs.multiloaderdataextensions.common.api.v2.DataMapRegistrar;
import fuzs.multiloaderdataextensions.common.api.v2.DataMapToken;
import fuzs.puzzleslib.common.api.init.v3.registry.RegistryFactory;
import fuzs.puzzleslib.common.api.init.v3.registry.RegistryManager;
import fuzs.puzzleslib.common.api.init.v3.tags.TagFactory;
import fuzs.universalbonemeal.common.UniversalBoneMeal;
import fuzs.universalbonemeal.common.util.stateproviders.CopySourceBlockStateProvider;
import fuzs.universalbonemeal.common.util.valueproviders.VinesIntProvider;
import fuzs.universalbonemeal.common.world.level.block.behavior.*;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

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

    public static final ResourceKey<LootTable> SPORE_BLOSSOM_LOOT_TABLE = REGISTRIES.makeResourceKey(Registries.LOOT_TABLE,
            "spore_blossom");

    public static final DataMapToken<Block, Holder<BoneMealBehavior>> BONE_MEAL_BEHAVIORS_DATA_MAP = DataMapRegistrar.register(
            UniversalBoneMeal.id("bone_meal_behaviors"),
            Registries.BLOCK,
            RegistryCodecs.holder(BONE_MEAL_BEHAVIOR_REGISTRY_KEY, BONE_MEAL_BEHAVIOR_CODEC, false),
            RegistryCodecs.holder(BONE_MEAL_BEHAVIOR_REGISTRY_KEY, BONE_MEAL_BEHAVIOR_CODEC, false),
            true);

    public static void bootstrap() {
        REGISTRIES.register(Registries.INT_PROVIDER_TYPE, "vines", () -> VinesIntProvider.CODEC);
        REGISTRIES.register(Registries.BLOCK_STATE_PROVIDER_TYPE,
                "copy_source",
                () -> CopySourceBlockStateProvider.CODEC);
        registerBoneMealBehaviorType("growing_plant", GrowingPlantBehavior.CODEC);
        registerBoneMealBehaviorType("crop_growth", CropGrowthBehavior.CODEC);
        registerBoneMealBehaviorType("fruit_stem", FruitStemBehavior.CODEC);
        registerBoneMealBehaviorType("neighbor_spread", NeighborSpreadBehavior.CODEC);
        registerBoneMealBehaviorType("vegetation_scatter", VegetationScatterBehavior.CODEC);
        registerBoneMealBehaviorType("placed_feature", PlacedFeatureBehavior.CODEC);
        registerBoneMealBehaviorType("chorus_flower", ChorusFlowerBehavior.CODEC);
        registerBoneMealBehaviorType("chorus_plant", ChorusPlantBehavior.CODEC);
        registerBoneMealBehaviorType("neighbor_conversion", NeighborConversionBehavior.CODEC);
        registerBoneMealBehaviorType("vegetation_spread", VegetationSpreadBehavior.CODEC);
        registerBoneMealBehaviorType("pop_resource", PopResourceBehavior.CODEC);
    }

    private static void registerBoneMealBehaviorType(String path, MapCodec<? extends BoneMealBehavior> codec) {
        REGISTRIES.register(BONE_MEAL_BEHAVIOR_TYPE_REGISTRY_KEY, path, () -> codec);
    }
}
