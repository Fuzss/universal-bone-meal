package fuzs.universalbonemeal.common.world.level.block.bonemeal.conditional;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BonemealableBlock;

/**
 * Bone meal logic for a single block, polymorphic so that simple one-action behaviors and more complex combinations can
 * be expressed through a single data map value.
 *
 * @see SimpleConditionalBoneMealBehavior
 * @see CombinedConditionalBoneMealBehavior
 */
public interface ConditionalBoneMealBehavior extends BonemealableBlock {

    /**
     * @return the codec for this conditional bone meal behavior type
     *
     * @see ConditionalBoneMealBehaviorTypes
     */
    MapCodec<? extends ConditionalBoneMealBehavior> codec();
}
