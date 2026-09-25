package fuzs.universalbonemeal.common.world.level.block.bonemeal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.universalbonemeal.common.world.level.block.bonemeal.conditional.ConditionalBoneMealBehavior;
import fuzs.universalbonemeal.common.world.level.block.bonemeal.conditional.ConditionalBoneMealBehaviorTypes;

public record Bonemealable(ConditionalBoneMealBehavior behavior, boolean replace) {
    public static final Codec<Bonemealable> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    ConditionalBoneMealBehaviorTypes.MAP_CODEC.forGetter(Bonemealable::behavior),
                    Codec.BOOL.optionalFieldOf("replace", false).forGetter(Bonemealable::replace))
            .apply(instance, Bonemealable::new));
}
