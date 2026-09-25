package fuzs.universalbonemeal.common.init;

import fuzs.universalbonemeal.common.world.level.levelgen.blockpredicates.*;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChorusFlowerBlock;
import net.minecraft.world.level.block.FlowerBedBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.MatchingBiomesPredicate;

public class BoneMealBlockPredicates {
    public static final ResourceKey<BlockPredicate> CACTUS = register("cactus");
    public static final ResourceKey<BlockPredicate> SUGAR_CANE = register("sugar_cane");
    public static final ResourceKey<BlockPredicate> VINE = register("vine");
    public static final ResourceKey<BlockPredicate> NETHER_WART = register("nether_wart");
    public static final ResourceKey<BlockPredicate> MELON_STEM = register("melon_stem");
    public static final ResourceKey<BlockPredicate> PUMPKIN_STEM = register("pumpkin_stem");
    public static final ResourceKey<BlockPredicate> CORAL = register("coral");
    public static final ResourceKey<BlockPredicate> CHORUS_FLOWER = register("chorus_flower");
    public static final ResourceKey<BlockPredicate> CHORUS_PLANT = register("chorus_plant");
    public static final ResourceKey<BlockPredicate> AIR_ABOVE = register("air_above");
    public static final ResourceKey<BlockPredicate> NEIGHBOR_CONVERSION = register("neighbor_conversion");
    public static final ResourceKey<BlockPredicate> FLOWER_AMOUNT = register("flower_amount");

    private static ResourceKey<BlockPredicate> register(String path) {
        return ModRegistry.REGISTRIES.makeResourceKey(ModRegistry.PREDICATE_REGISTRY_KEY, path);
    }

    public static void bootstrap(BootstrapContext<BlockPredicate> context) {
        HolderSet<Block> melonSupport = context.lookup(Registries.BLOCK)
                .getOrThrow(BlockTags.SUPPORTS_MELON_STEM_FRUIT);
        HolderSet<Block> pumpkinSupport = context.lookup(Registries.BLOCK)
                .getOrThrow(BlockTags.SUPPORTS_PUMPKIN_STEM_FRUIT);
        BlockPredicate coralBiomes = new MatchingBiomesPredicate(context.lookup(Registries.BIOME)
                .getOrThrow(BiomeTags.PRODUCES_CORALS_FROM_BONEMEAL));
        context.register(CACTUS,
                new GrowingPlantPredicate(Direction.UP, UniformInt.of(12, 16), BlockPredicate.ONLY_IN_AIR_PREDICATE));
        context.register(SUGAR_CANE,
                new GrowingPlantPredicate(Direction.UP, UniformInt.of(12, 16), BlockPredicate.ONLY_IN_AIR_PREDICATE));
        context.register(VINE,
                BlockPredicate.allOf(new GrowingPlantPredicate(Direction.DOWN,
                        ConstantInt.of(128),
                        BlockPredicate.ONLY_IN_AIR_PREDICATE), new VineFacePredicate()));
        context.register(NETHER_WART, new BlockPropertyPredicate(NetherWartBlock.AGE));
        context.register(MELON_STEM, new FruitStemPredicate(melonSupport));
        context.register(PUMPKIN_STEM, new FruitStemPredicate(pumpkinSupport));
        context.register(CORAL, coralBiomes);
        context.register(CHORUS_FLOWER, new BlockPropertyPredicate(ChorusFlowerBlock.AGE));
        context.register(CHORUS_PLANT,
                new ChorusPlantPredicate(HolderSet.direct(Blocks.CHORUS_PLANT.builtInRegistryHolder()),
                        HolderSet.direct(Blocks.CHORUS_FLOWER.builtInRegistryHolder()),
                        128));
        context.register(AIR_ABOVE, BlockPredicate.matchesTag(Direction.UP, BlockTags.AIR));
        context.register(NEIGHBOR_CONVERSION,
                new NeighborConversionPredicate(HolderSet.direct(Blocks.GRASS_BLOCK.builtInRegistryHolder(),
                        Blocks.MYCELIUM.builtInRegistryHolder()), 1, 1));
        context.register(FLOWER_AMOUNT, new BlockPropertyPredicate(FlowerBedBlock.AMOUNT));
    }
}
