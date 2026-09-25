package fuzs.universalbonemeal.common.world.level.block.bonemeal;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jspecify.annotations.Nullable;

import java.util.Collections;

/**
 * Shared integer property resolution, used by both the bone meal actions and the target predicates.
 */
public interface LazyBlockProperty {

    @Nullable String propertyName();

    default @Nullable IntegerProperty findProperty(BlockState source) {
        String propertyName = this.propertyName();
        return propertyName != null ? source.getProperties()
                .stream()
                .filter((Property<?> property) -> property.getName().equals(propertyName))
                .filter((Property<?> property) -> property instanceof IntegerProperty)
                .map((Property<?> property) -> (IntegerProperty) property)
                .findAny()
                .orElse(null) : null;
    }

    default int getMaxValue(IntegerProperty property) {
        return Collections.max(property.getPossibleValues());
    }
}
