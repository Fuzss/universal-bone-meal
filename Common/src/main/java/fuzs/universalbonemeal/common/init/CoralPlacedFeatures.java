package fuzs.universalbonemeal.common.init;

import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.CoralTreeFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.List;

/**
 * Vanilla coral trees are not standalone placed features (they only exist inline in {@code warm_ocean_vegetation}), so
 * we build them here on top of the matching vanilla {@code minecraft:coral/<type>_block} feature.
 *
 * @see net.minecraft.data.worldgen.features.AquaticFeatures
 */
public class CoralPlacedFeatures {
    public static final ResourceKey<PlacedFeature> TUBE_CORAL = register("coral/tube");
    public static final ResourceKey<PlacedFeature> BRAIN_CORAL = register("coral/brain");
    public static final ResourceKey<PlacedFeature> BUBBLE_CORAL = register("coral/bubble");
    public static final ResourceKey<PlacedFeature> FIRE_CORAL = register("coral/fire");
    public static final ResourceKey<PlacedFeature> HORN_CORAL = register("coral/horn");

    private static final List<PlacementModifier> CORAL_PLACEMENT = List.of(BlockPredicateFilter.forPredicate(
            BlockPredicate.allOf(BlockPredicate.anyOf(BlockPredicate.matchesBlocks(Blocks.WATER),
                            BlockPredicate.matchesTag(BlockTags.CORALS)),
                    BlockPredicate.matchesBlocks(Direction.UP, Blocks.WATER))));

    private static ResourceKey<PlacedFeature> register(String path) {
        return ModRegistry.REGISTRIES.makeResourceKey(Registries.PLACED_FEATURE, path);
    }

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        HolderGetter<Feature> features = context.lookup(Registries.FEATURE);
        register(context, features, TUBE_CORAL, "coral/tube_block");
        register(context, features, BRAIN_CORAL, "coral/brain_block");
        register(context, features, BUBBLE_CORAL, "coral/bubble_block");
        register(context, features, FIRE_CORAL, "coral/fire_block");
        register(context, features, HORN_CORAL, "coral/horn_block");
    }

    private static void register(BootstrapContext<PlacedFeature> context, HolderGetter<Feature> features, ResourceKey<PlacedFeature> key, String featurePath) {
        Holder<Feature> coralBlockFeature = features.getOrThrow(ResourceKey.create(Registries.FEATURE,
                Identifier.withDefaultNamespace(featurePath)));
        PlacedFeature coralBlockPlacedFeature = new PlacedFeature(coralBlockFeature, CORAL_PLACEMENT);
        CoralTreeFeature coralTreeFeature = new CoralTreeFeature(Holder.direct(coralBlockPlacedFeature));
        context.register(key, new PlacedFeature(Holder.direct(coralTreeFeature), List.of()));
    }
}
