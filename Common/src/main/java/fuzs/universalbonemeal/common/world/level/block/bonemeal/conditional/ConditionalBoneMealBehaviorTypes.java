package fuzs.universalbonemeal.common.world.level.block.bonemeal.conditional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import fuzs.universalbonemeal.common.UniversalBoneMeal;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;

import java.util.function.Function;

public final class ConditionalBoneMealBehaviorTypes {
    private static final ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<? extends ConditionalBoneMealBehavior>> ID_MAPPER = new ExtraCodecs.LateBoundIdMapper<>();
    public static final MapCodec<ConditionalBoneMealBehavior> MAP_CODEC = ID_MAPPER.codec(Identifier.CODEC)
            .dispatchMap("type", ConditionalBoneMealBehavior::codec, Function.identity());
    public static final Codec<ConditionalBoneMealBehavior> CODEC = MAP_CODEC.codec();

    private ConditionalBoneMealBehaviorTypes() {
        // NO-OP
    }

    public static void bootstrap() {
        ID_MAPPER.put(UniversalBoneMeal.id("conditional"), SimpleConditionalBoneMealBehavior.CODEC);
        ID_MAPPER.put(UniversalBoneMeal.id("combined"), CombinedConditionalBoneMealBehavior.CODEC);
    }
}
