package fuzs.universalbonemeal.common.init;

import fuzs.universalbonemeal.common.util.stateproviders.CopySourceProvider;
import fuzs.universalbonemeal.common.util.valueproviders.VineIntProvider;
import fuzs.universalbonemeal.common.world.level.block.bonemeal.behavior.*;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.WeightedList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.util.valueproviders.WeightedListInt;
import net.minecraft.world.level.block.*;
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
        return ModRegistry.REGISTRIES.makeResourceKey(BoneMealBehavior.REGISTRY_KEY, path);
    }

    public static void bootstrap(BootstrapContext<BoneMealBehavior> context) {
        context.register(SUGAR_CANE,
                new GrowingPlantBehavior(Direction.UP,
                        UniformInt.of(1, 2),
                        BlockPredicate.ONLY_IN_AIR_PREDICATE,
                        Holder.direct(new SimpleStateProvider(Blocks.SUGAR_CANE.defaultBlockState())),
                        SugarCaneBlock.AGE));
        context.register(CACTUS,
                new GrowingPlantBehavior(Direction.UP,
                        UniformInt.of(1, 2),
                        BlockPredicate.ONLY_IN_AIR_PREDICATE,
                        Holder.direct(new WeightedStateProvider(WeightedList.<BlockState>builder()
                                .add(Blocks.CACTUS.defaultBlockState(), 9)
                                .add(Blocks.CACTUS_FLOWER.defaultBlockState(), 1))),
                        CactusBlock.AGE));
        context.register(VINE,
                new VineBehavior(Direction.DOWN,
                        new VineIntProvider(5),
                        BlockPredicate.ONLY_IN_AIR_PREDICATE,
                        Holder.direct(new CopySourceProvider())));
        context.register(NETHER_WART,
                new CropGrowthBehavior(NetherWartBlock.AGE,
                        new WeightedListInt(WeightedList.<IntProvider>builder()
                                .add(ConstantInt.of(0), 1)
                                .add(ConstantInt.of(1), 3)
                                .build())));
        context.register(MELON_STEM, new RandomTickBehavior(UniformInt.of(2, 5)));
        context.register(PUMPKIN_STEM, new RandomTickBehavior(UniformInt.of(2, 5)));
        context.register(LILY_PAD, new NeighborSpreadBehavior(Holder.direct(new CopySourceProvider()), 4, 3));
        context.register(DEAD_BUSH, new NeighborSpreadBehavior(Holder.direct(new CopySourceProvider()), 4, 2));
        context.register(SMALL_FLOWER, new NeighborSpreadBehavior(Holder.direct(new CopySourceProvider()), 3, 1));
        context.register(TUBE_CORAL, placedFeatureBehavior(CoralPlacedFeatures.TUBE_CORAL));
        context.register(BRAIN_CORAL, placedFeatureBehavior(CoralPlacedFeatures.BRAIN_CORAL));
        context.register(BUBBLE_CORAL, placedFeatureBehavior(CoralPlacedFeatures.BUBBLE_CORAL));
        context.register(FIRE_CORAL, placedFeatureBehavior(CoralPlacedFeatures.FIRE_CORAL));
        context.register(HORN_CORAL, placedFeatureBehavior(CoralPlacedFeatures.HORN_CORAL));
        context.register(CHORUS_FLOWER, new RandomTickBehavior(ConstantInt.of(1)));
        context.register(CHORUS_PLANT,
                new ChorusPlantBehavior(HolderSet.direct(Blocks.CHORUS_PLANT.builtInRegistryHolder()),
                        HolderSet.direct(Blocks.CHORUS_FLOWER.builtInRegistryHolder()),
                        128));
        context.register(MYCELIUM, new VegetationScatterBehavior(MYCELIUM_VEGETATION, 3, 1));
        context.register(DIRT,
                new NeighborConversionBehavior(HolderSet.direct(Blocks.GRASS_BLOCK.builtInRegistryHolder(),
                        Blocks.MYCELIUM.builtInRegistryHolder()), 1, 1));
        context.register(PODZOL,
                new VegetationSpreadBehavior(HolderSet.direct(Blocks.PODZOL.builtInRegistryHolder()),
                        PODZOL_VEGETATION,
                        HolderSet.direct(Blocks.FERN.builtInRegistryHolder()),
                        128,
                        16));
        context.register(SPORE_BLOSSOM,
                new PopResourceBehavior(ModRegistry.SPORE_BLOSSOM_LOOT_TABLE,
                        new PopResourceBehavior.Drop.FromMiddle()));
    }

    /**
     * @see net.minecraft.world.level.block.NetherFungusBlock#BONEMEAL_SUCCESS_PROBABILITY
     */
    private static PlacedFeatureBehavior placedFeatureBehavior(ResourceKey<PlacedFeature> feature) {
        return new PlacedFeatureBehavior(feature, 0.4F);
    }
}
