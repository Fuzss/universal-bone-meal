package fuzs.universalbonemeal.common.world.level.block.behavior;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

/**
 * @see net.minecraft.world.level.levelgen.feature.stateproviders.RandomizedIntStateProvider
 */
public class CropGrowthBehavior implements BoneMealBehavior {
    public static final MapCodec<CropGrowthBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.STRING.fieldOf("property").forGetter(CropGrowthBehavior::getProperty),
                    IntProviders.NON_NEGATIVE_CODEC.fieldOf("age_increase").forGetter(CropGrowthBehavior::getAgeIncrease))
            .apply(instance, CropGrowthBehavior::new));

    private final String property;
    private final IntProvider ageIncrease;
    private @Nullable IntegerProperty ageProperty;

    public CropGrowthBehavior(String property, IntProvider ageIncrease) {
        this.property = property;
        this.ageIncrease = ageIncrease;
    }

    public CropGrowthBehavior(IntegerProperty ageProperty, IntProvider ageIncrease) {
        this.property = ageProperty.getName();
        this.ageIncrease = ageIncrease;
        this.ageProperty = ageProperty;

        Collection<Integer> possibleValues = ageProperty.getPossibleValues();
        for (int i = ageIncrease.minInclusive(); i <= ageIncrease.maxInclusive(); i++) {
            if (!possibleValues.contains(i)) {
                throw new IllegalArgumentException("Property value out of range: " + ageProperty.getName() + ": " + i);
            }
        }
    }

    public String getProperty() {
        return this.property;
    }

    public IntProvider getAgeIncrease() {
        return this.ageIncrease;
    }

    @Override
    public MapCodec<CropGrowthBehavior> codec() {
        return CODEC;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, BonemealSource source) {
        IntegerProperty ageProperty = this.getAgeProperty(state);
        return ageProperty != null && state.getValue(ageProperty) < getMaxAge(ageProperty);
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        IntegerProperty ageProperty = this.getAgeProperty(state);
        if (ageProperty == null) {
            return;
        }

        int age = Math.min(state.getValue(ageProperty) + this.ageIncrease.sample(random), getMaxAge(ageProperty));
        level.setBlock(pos, state.getBlock().defaultBlockState().setValue(ageProperty, age), Block.UPDATE_CLIENTS);
    }

    private @Nullable IntegerProperty getAgeProperty(BlockState state) {
        if (this.ageProperty == null || !state.hasProperty(this.ageProperty)) {
            this.ageProperty = findProperty(state, this.property);
        }

        return this.ageProperty;
    }

    private static int getMaxAge(IntegerProperty ageProperty) {
        return Collections.max(ageProperty.getPossibleValues());
    }

    private static @Nullable IntegerProperty findProperty(BlockState source, String propertyName) {
        Collection<Property<?>> properties = source.getProperties();
        return properties.stream()
                .filter(property -> property.getName().equals(propertyName))
                .filter(property -> property instanceof IntegerProperty)
                .map(property -> (IntegerProperty) property)
                .findAny()
                .orElse(null);
    }
}
