package fuzs.universalbonemeal.common.init;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import fuzs.multiloaderdataextensions.common.api.v2.DataMapRegistrar;
import fuzs.multiloaderdataextensions.common.api.v2.DataMapToken;
import fuzs.puzzleslib.common.api.init.v3.registry.RegistryFactory;
import fuzs.puzzleslib.common.api.init.v3.registry.RegistryManager;
import fuzs.puzzleslib.common.api.init.v3.tags.TagFactory;
import fuzs.universalbonemeal.common.UniversalBoneMeal;
import fuzs.universalbonemeal.common.util.stateproviders.CopySourceProvider;
import fuzs.universalbonemeal.common.util.valueproviders.VineIntProvider;
import fuzs.universalbonemeal.common.world.level.block.bonemeal.*;
import fuzs.universalbonemeal.common.world.level.block.bonemeal.behavior.*;
import fuzs.universalbonemeal.common.world.level.levelgen.blockpredicates.*;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.minecraft.world.level.storage.loot.LootTable;

public class ModRegistry {
    public static final ResourceKey<Registry<MapCodec<? extends BoneMealBehavior>>> BONE_MEAL_BEHAVIOR_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(
            UniversalBoneMeal.id("bone_meal_behavior_type"));
    public static final Registry<MapCodec<? extends BoneMealBehavior>> BONE_MEAL_BEHAVIOR_TYPE_REGISTRY = RegistryFactory.INSTANCE.create(
            BONE_MEAL_BEHAVIOR_TYPE_REGISTRY_KEY);
    public static final ResourceKey<Registry<BlockPredicate>> PREDICATE_REGISTRY_KEY = ResourceKey.createRegistryKey(
            UniversalBoneMeal.id("block_predicate"));
    public static final Codec<Holder<BlockPredicate>> PREDICATE_CODEC = RegistryCodecs.holder(PREDICATE_REGISTRY_KEY,
            BlockPredicate.CODEC);

    static final RegistryManager REGISTRIES = RegistryManager.from(UniversalBoneMeal.MOD_ID);
    public static final Holder.Reference<BlockPredicateType<?>> GROWING_PLANT_BLOCK_PREDICATE_TYPE = REGISTRIES.register(
            Registries.BLOCK_PREDICATE_TYPE,
            "growing_plant",
            () -> (BlockPredicateType<GrowingPlantPredicate>) () -> GrowingPlantPredicate.CODEC);
    public static final Holder.Reference<BlockPredicateType<?>> VINE_FACE_BLOCK_PREDICATE_TYPE = REGISTRIES.register(
            Registries.BLOCK_PREDICATE_TYPE,
            "vine_face",
            () -> (BlockPredicateType<VineFacePredicate>) () -> VineFacePredicate.CODEC);
    public static final Holder.Reference<BlockPredicateType<?>> BLOCK_PROPERTY_BLOCK_PREDICATE_TYPE = REGISTRIES.register(
            Registries.BLOCK_PREDICATE_TYPE,
            "block_property",
            () -> (BlockPredicateType<BlockPropertyPredicate>) () -> BlockPropertyPredicate.CODEC);
    public static final Holder.Reference<BlockPredicateType<?>> CHORUS_PLANT_BLOCK_PREDICATE_TYPE = REGISTRIES.register(
            Registries.BLOCK_PREDICATE_TYPE,
            "chorus_plant",
            () -> (BlockPredicateType<ChorusPlantPredicate>) () -> ChorusPlantPredicate.CODEC);
    public static final Holder.Reference<BlockPredicateType<?>> FRUIT_STEM_BLOCK_PREDICATE_TYPE = REGISTRIES.register(
            Registries.BLOCK_PREDICATE_TYPE,
            "fruit_stem",
            () -> (BlockPredicateType<FruitStemPredicate>) () -> FruitStemPredicate.CODEC);
    public static final Holder.Reference<BlockPredicateType<?>> NEIGHBOR_CONVERSION_BLOCK_PREDICATE_TYPE = REGISTRIES.register(
            Registries.BLOCK_PREDICATE_TYPE,
            "neighbor_conversion",
            () -> (BlockPredicateType<NeighborConversionPredicate>) () -> NeighborConversionPredicate.CODEC);
    public static final ResourceKey<LootTable> SPORE_BLOSSOM_LOOT_TABLE = REGISTRIES.makeResourceKey(Registries.LOOT_TABLE,
            "spore_blossom");

    static final TagFactory TAGS = TagFactory.make(UniversalBoneMeal.MOD_ID);
    public static final TagKey<Block> FERTILIZER_RESISTANT_PLANTS_BLOCK_TAG = TAGS.registerBlockTag(
            "fertilizer_resistant_plants");

    public static final DataMapToken<Block, ConditionalBoneMealBehavior> BONE_MEAL_BEHAVIORS_DATA_MAP = DataMapRegistrar.register(
            UniversalBoneMeal.id("bone_meal_behaviors"),
            Registries.BLOCK,
            ConditionalBoneMealBehavior.CODEC,
            ConditionalBoneMealBehavior.CODEC,
            false);

    public static void bootstrap() {
        REGISTRIES.register(Registries.INT_PROVIDER_TYPE, "vine", () -> VineIntProvider.CODEC);
        REGISTRIES.register(Registries.BLOCK_STATE_PROVIDER_TYPE, "copy_source", () -> CopySourceProvider.CODEC);
        registerBoneMealBehaviorType("growing_plant", GrowingPlantBehavior.CODEC);
        registerBoneMealBehaviorType("vine", VineBehavior.CODEC);
        registerBoneMealBehaviorType("crop_growth", CropGrowthBehavior.CODEC);
        registerBoneMealBehaviorType("fruit_stem", RandomTickBehavior.CODEC);
        registerBoneMealBehaviorType("neighbor_spread", NeighborSpreadBehavior.CODEC);
        registerBoneMealBehaviorType("vegetation_scatter", VegetationScatterBehavior.CODEC);
        registerBoneMealBehaviorType("placed_feature", PlacedFeatureBehavior.CODEC);
        registerBoneMealBehaviorType("chorus_plant", ChorusPlantBehavior.CODEC);
        registerBoneMealBehaviorType("neighbor_conversion", NeighborConversionBehavior.CODEC);
        registerBoneMealBehaviorType("vegetation_spread", VegetationSpreadBehavior.CODEC);
        registerBoneMealBehaviorType("pop_resource", PopResourceBehavior.CODEC);
    }

    private static void registerBoneMealBehaviorType(String path, MapCodec<? extends BoneMealBehavior> codec) {
        REGISTRIES.register(BONE_MEAL_BEHAVIOR_TYPE_REGISTRY_KEY, path, () -> codec);
    }

}
