package fuzs.universalbonemeal.common.init;

import fuzs.universalbonemeal.common.util.stateproviders.CopySourceBlockStateProvider;
import fuzs.universalbonemeal.common.util.valueproviders.NetherVinesIntProvider;
import fuzs.universalbonemeal.common.world.level.block.behavior.*;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.BlockTransformer;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.random.WeightedList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class BoneMealBehaviors {
    public static final ResourceKey<BoneMealBehavior> CACTUS = register("cactus");
    public static final ResourceKey<BoneMealBehavior> SUGAR_CANE = register("sugar_cane");
    public static final ResourceKey<BoneMealBehavior> VINE = register("vine");
    public static final ResourceKey<BoneMealBehavior> NETHER_WART = register("nether_wart");
    public static final ResourceKey<BoneMealBehavior> MELON_STEM = register("melon_stem");
    public static final ResourceKey<BoneMealBehavior> PUMPKIN_STEM = register("pumpkin_stem");
    public static final ResourceKey<BoneMealBehavior> LILY_PAD = register("lily_pad");
    public static final ResourceKey<BoneMealBehavior> DEAD_BUSH = register("dead_bush");
    public static final ResourceKey<BoneMealBehavior> SMALL_FLOWER = register("small_flower");
    public static final ResourceKey<BoneMealBehavior> TUBE_CORAL = register("tube_coral");
    public static final ResourceKey<BoneMealBehavior> BRAIN_CORAL = register("brain_coral");
    public static final ResourceKey<BoneMealBehavior> BUBBLE_CORAL = register("bubble_coral");
    public static final ResourceKey<BoneMealBehavior> FIRE_CORAL = register("fire_coral");
    public static final ResourceKey<BoneMealBehavior> HORN_CORAL = register("horn_coral");
    public static final ResourceKey<BoneMealBehavior> CHORUS_FLOWER = register("chorus_flower");
    public static final ResourceKey<BoneMealBehavior> CHORUS_PLANT = register("chorus_plant");
    public static final ResourceKey<BoneMealBehavior> MYCELIUM = register("mycelium");
    public static final ResourceKey<BoneMealBehavior> DIRT = register("dirt");
    public static final ResourceKey<BoneMealBehavior> PODZOL = register("podzol");
    public static final ResourceKey<BoneMealBehavior> SPORE_BLOSSOM = register("spore_blossom");

    private static final Holder<BlockStateProvider> MYCELIUM_VEGETATION = Holder.direct(new WeightedStateProvider(
            WeightedList.<BlockState>builder()
                    .add(Blocks.RED_MUSHROOM.defaultBlockState(), 1)
                    .add(Blocks.BROWN_MUSHROOM.defaultBlockState(), 1)));
    private static final Holder<BlockStateProvider> PODZOL_VEGETATION = Holder.direct(new WeightedStateProvider(
            WeightedList.<BlockState>builder()
                    .add(Blocks.SWEET_BERRY_BUSH.defaultBlockState().setValue(SweetBerryBushBlock.AGE, 3), 2)
                    .add(Blocks.SWEET_BERRY_BUSH.defaultBlockState().setValue(SweetBerryBushBlock.AGE, 2), 4)
                    .add(Blocks.SWEET_BERRY_BUSH.defaultBlockState().setValue(SweetBerryBushBlock.AGE, 1), 8)
                    .add(Blocks.SWEET_BERRY_BUSH.defaultBlockState().setValue(SweetBerryBushBlock.AGE, 0), 12)
                    .add(Blocks.FERN.defaultBlockState(), 120)
                    .add(Blocks.DEAD_BUSH.defaultBlockState(), 1)));

    private static ResourceKey<BoneMealBehavior> register(String path) {
        return ModRegistry.REGISTRIES.makeResourceKey(ModRegistry.BONE_MEAL_BEHAVIOR_REGISTRY_KEY, path);
    }

    public static void bootstrap(BootstrapContext<BoneMealBehavior> context) {
        context.register(SUGAR_CANE,
                new GrowingPlantBehavior(Direction.UP,
                        UniformInt.of(1, 2),
                        BlockPredicate.ONLY_IN_AIR_PREDICATE,
                        Holder.direct(new SimpleStateProvider(Blocks.SUGAR_CANE.defaultBlockState())),
                        UniformInt.of(12, 16)));
        context.register(CACTUS,
                new GrowingPlantBehavior(Direction.UP,
                        UniformInt.of(1, 2),
                        BlockPredicate.ONLY_IN_AIR_PREDICATE,
                        Holder.direct(new WeightedStateProvider(WeightedList.<BlockState>builder()
                                .add(Blocks.CACTUS.defaultBlockState(), 9)
                                .add(Blocks.CACTUS_FLOWER.defaultBlockState(), 1))),
                        UniformInt.of(12, 16)));
        context.register(VINE,
                new GrowingPlantBehavior(Direction.DOWN,
                        new NetherVinesIntProvider(32),
                        BlockPredicate.ONLY_IN_AIR_PREDICATE,
                        Holder.direct(new CopySourceBlockStateProvider(Direction.UP)),
                        ConstantInt.of(128)));
        context.register(NETHER_WART, new CropGrowthBehavior(UniformInt.of(0, 1)));
        context.register(MELON_STEM,
                new FruitStemBehavior(context.lookup(Registries.BLOCK).getOrThrow(BlockTags.SUPPORTS_MELON_STEM_FRUIT),
                        UniformInt.of(2, 5)));
        context.register(PUMPKIN_STEM,
                new FruitStemBehavior(context.lookup(Registries.BLOCK)
                        .getOrThrow(BlockTags.SUPPORTS_PUMPKIN_STEM_FRUIT),
                        UniformInt.of(2, 5)));
        context.register(LILY_PAD, new NeighborSpreadBehavior(4, 3));
        context.register(DEAD_BUSH, new NeighborSpreadBehavior(4, 2));
        context.register(SMALL_FLOWER, new NeighborSpreadBehavior(3, 1));
        HolderSet<Biome> coralBiomes = context.lookup(Registries.BIOME)
                .getOrThrow(BiomeTags.PRODUCES_CORALS_FROM_BONEMEAL);
        context.register(TUBE_CORAL, coralBehavior(context, coralBiomes, CoralPlacedFeatures.TUBE_CORAL));
        context.register(BRAIN_CORAL, coralBehavior(context, coralBiomes, CoralPlacedFeatures.BRAIN_CORAL));
        context.register(BUBBLE_CORAL, coralBehavior(context, coralBiomes, CoralPlacedFeatures.BUBBLE_CORAL));
        context.register(FIRE_CORAL, coralBehavior(context, coralBiomes, CoralPlacedFeatures.FIRE_CORAL));
        context.register(HORN_CORAL, coralBehavior(context, coralBiomes, CoralPlacedFeatures.HORN_CORAL));
        context.register(CHORUS_FLOWER, new ChorusFlowerBehavior());
        context.register(CHORUS_PLANT,
                new ChorusPlantBehavior(HolderSet.direct(Blocks.CHORUS_PLANT.builtInRegistryHolder()),
                        HolderSet.direct(Blocks.CHORUS_FLOWER.builtInRegistryHolder()),
                        128));
        context.register(MYCELIUM, new VegetationScatterBehavior(MYCELIUM_VEGETATION, 3, 1));
        context.register(DIRT,
                new DirtConversionBehavior(HolderSet.direct(Blocks.GRASS_BLOCK.builtInRegistryHolder(),
                        Blocks.MYCELIUM.builtInRegistryHolder()), 1, 1));
        context.register(PODZOL,
                new PodzolVegetationBehavior(HolderSet.direct(Blocks.PODZOL.builtInRegistryHolder()),
                        PODZOL_VEGETATION,
                        HolderSet.direct(Blocks.FERN.builtInRegistryHolder()),
                        128,
                        16));
        context.register(SPORE_BLOSSOM,
                new PopResourceBehavior(ModRegistry.SPORE_BLOSSOM_LOOT_TABLE,
                        BlockTransformer.DropStrategy.CLICKED_FACE,
                        Direction.DOWN));
    }

    private static CoralTreeBehavior coralBehavior(BootstrapContext<BoneMealBehavior> context, HolderSet<Biome> biomes, ResourceKey<PlacedFeature> feature) {
        return new CoralTreeBehavior(context.lookup(Registries.PLACED_FEATURE).getOrThrow(feature), biomes, 0.4F);
    }
}
