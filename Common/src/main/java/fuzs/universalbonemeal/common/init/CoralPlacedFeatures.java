package fuzs.universalbonemeal.common.init;

import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.AquaticFeatures;
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
 * Vanilla coral trees are not standalone placed features (they only exist inline in {@code warm_ocean_vegetation})
 *
 * @see net.minecraft.data.worldgen.features.AquaticFeatures
 */
public class CoralPlacedFeatures {
    public static final ResourceKey<PlacedFeature> TUBE_CORAL = register("tube_coral");
    public static final ResourceKey<PlacedFeature> BRAIN_CORAL = register("brain_coral");
    public static final ResourceKey<PlacedFeature> BUBBLE_CORAL = register("bubble_coral");
    public static final ResourceKey<PlacedFeature> FIRE_CORAL = register("fire_coral");
    public static final ResourceKey<PlacedFeature> HORN_CORAL = register("horn_coral");

    private static final List<PlacementModifier> CORAL_PLACEMENT = List.of(BlockPredicateFilter.forPredicate(
            BlockPredicate.allOf(BlockPredicate.anyOf(BlockPredicate.matchesBlocks(Blocks.WATER),
                            BlockPredicate.matchesTag(BlockTags.CORALS)),
                    BlockPredicate.matchesBlocks(Direction.UP, Blocks.WATER))));

    private static ResourceKey<PlacedFeature> register(String path) {
        return ModRegistry.REGISTRIES.makeResourceKey(Registries.PLACED_FEATURE, path);
    }

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        HolderGetter<Feature> features = context.lookup(Registries.FEATURE);
        register(context, features, TUBE_CORAL, AquaticFeatures.TUBE_CORAL_BLOCK);
        register(context, features, BRAIN_CORAL, AquaticFeatures.BRAIN_CORAL_BLOCK);
        register(context, features, BUBBLE_CORAL, AquaticFeatures.BUBBLE_CORAL_BLOCK);
        register(context, features, FIRE_CORAL, AquaticFeatures.FIRE_CORAL_BLOCK);
        register(context, features, HORN_CORAL, AquaticFeatures.HORN_CORAL_BLOCK);
    }

    private static void register(BootstrapContext<PlacedFeature> context, HolderGetter<Feature> features, ResourceKey<PlacedFeature> key, ResourceKey<Feature> featureKey) {
        Holder<Feature> coralBlockFeature = features.getOrThrow(featureKey);
        PlacedFeature coralBlockPlacedFeature = new PlacedFeature(coralBlockFeature, CORAL_PLACEMENT);
        CoralTreeFeature coralTreeFeature = new CoralTreeFeature(Holder.direct(coralBlockPlacedFeature));
        context.register(key, new PlacedFeature(Holder.direct(coralTreeFeature), List.of()));
    }
}
