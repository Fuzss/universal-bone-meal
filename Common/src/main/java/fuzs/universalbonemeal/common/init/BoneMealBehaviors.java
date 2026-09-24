package fuzs.universalbonemeal.common.init;

import fuzs.universalbonemeal.common.UniversalBoneMeal;
import fuzs.universalbonemeal.common.util.valueproviders.NetherVinesIntProvider;
import fuzs.universalbonemeal.common.world.level.block.behavior.BoneMealBehavior;
import fuzs.universalbonemeal.common.world.level.block.behavior.CactusBehavior;
import fuzs.universalbonemeal.common.world.level.block.behavior.ChorusFlowerBehavior;
import fuzs.universalbonemeal.common.world.level.block.behavior.ChorusPlantBehavior;
import fuzs.universalbonemeal.common.world.level.block.behavior.CoralTreeBehavior;
import fuzs.universalbonemeal.common.world.level.block.behavior.CropGrowthBehavior;
import fuzs.universalbonemeal.common.world.level.block.behavior.DirtConversionBehavior;
import fuzs.universalbonemeal.common.world.level.block.behavior.FruitStemBehavior;
import fuzs.universalbonemeal.common.world.level.block.behavior.GrowingPlantBehavior;
import fuzs.universalbonemeal.common.world.level.block.behavior.HangingPlantBehavior;
import fuzs.universalbonemeal.common.world.level.block.behavior.NeighborSpreadBehavior;
import fuzs.universalbonemeal.common.world.level.block.behavior.PodzolVegetationBehavior;
import fuzs.universalbonemeal.common.world.level.block.behavior.PopResourceBehavior;
import fuzs.universalbonemeal.common.world.level.block.behavior.VegetationScatterBehavior;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.random.WeightedList;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;

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
    public static final ResourceKey<BoneMealBehavior> CORAL = register("coral");
    public static final ResourceKey<BoneMealBehavior> CHORUS_FLOWER = register("chorus_flower");
    public static final ResourceKey<BoneMealBehavior> CHORUS_PLANT = register("chorus_plant");
    public static final ResourceKey<BoneMealBehavior> MYCELIUM = register("mycelium");
    public static final ResourceKey<BoneMealBehavior> DIRT = register("dirt");
    public static final ResourceKey<BoneMealBehavior> PODZOL = register("podzol");
    public static final ResourceKey<BoneMealBehavior> SPORE_BLOSSOM = register("spore_blossom");

    private static final Holder<BlockStateProvider> CACTUS_FLOWER = Holder.direct(new SimpleStateProvider(Blocks.CACTUS_FLOWER.defaultBlockState()));
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
        return ResourceKey.create(ModRegistry.BONE_MEAL_BEHAVIOR_REGISTRY_KEY, UniversalBoneMeal.id(path));
    }

    public static void bootstrap(BootstrapContext<BoneMealBehavior> context) {
        context.register(CACTUS, new CactusBehavior(UniformInt.of(1, 2), UniformInt.of(12, 16), CACTUS_FLOWER, 0.1F));
        context.register(SUGAR_CANE, new GrowingPlantBehavior(UniformInt.of(1, 2), UniformInt.of(12, 16)));
        context.register(VINE, new HangingPlantBehavior(NetherVinesIntProvider.INSTANCE));
        context.register(NETHER_WART, new CropGrowthBehavior(3));
        context.register(MELON_STEM, new FruitStemBehavior(context.lookup(Registries.BLOCK)
                .getOrThrow(BlockTags.SUPPORTS_MELON_STEM_FRUIT)));
        context.register(PUMPKIN_STEM, new FruitStemBehavior(context.lookup(Registries.BLOCK)
                .getOrThrow(BlockTags.SUPPORTS_PUMPKIN_STEM_FRUIT)));
        context.register(LILY_PAD, new NeighborSpreadBehavior(4, 3));
        context.register(DEAD_BUSH, new NeighborSpreadBehavior(4, 2));
        context.register(SMALL_FLOWER, new NeighborSpreadBehavior(3, 1));
        context.register(CORAL, new CoralTreeBehavior(context.lookup(Registries.BLOCK)
                .getOrThrow(BlockTags.CORAL_BLOCKS),
                context.lookup(Registries.BLOCK).getOrThrow(BlockTags.CORALS),
                context.lookup(Registries.BLOCK).getOrThrow(BlockTags.WALL_CORALS),
                Blocks.SEA_PICKLE.builtInRegistryHolder()));
        context.register(CHORUS_FLOWER, new ChorusFlowerBehavior());
        context.register(CHORUS_PLANT, new ChorusPlantBehavior(HolderSet.direct(Blocks.CHORUS_PLANT.builtInRegistryHolder()),
                HolderSet.direct(Blocks.CHORUS_FLOWER.builtInRegistryHolder()),
                128));
        context.register(MYCELIUM, new VegetationScatterBehavior(MYCELIUM_VEGETATION, 3, 1));
        context.register(DIRT, new DirtConversionBehavior(HolderSet.direct(Blocks.GRASS_BLOCK.builtInRegistryHolder(),
                Blocks.MYCELIUM.builtInRegistryHolder()), 1));
        context.register(PODZOL, new PodzolVegetationBehavior(HolderSet.direct(Blocks.PODZOL.builtInRegistryHolder()),
                PODZOL_VEGETATION,
                HolderSet.direct(Blocks.FERN.builtInRegistryHolder())));
        context.register(SPORE_BLOSSOM, new PopResourceBehavior(Direction.DOWN));
    }
}
