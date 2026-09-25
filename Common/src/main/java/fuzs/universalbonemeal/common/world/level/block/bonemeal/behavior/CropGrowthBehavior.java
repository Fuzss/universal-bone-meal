package fuzs.universalbonemeal.common.world.level.block.bonemeal.behavior;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.universalbonemeal.common.world.level.block.bonemeal.LazyBlockProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jspecify.annotations.Nullable;

/**
 * @see net.minecraft.world.level.levelgen.feature.stateproviders.RandomizedIntStateProvider
 */
public final class CropGrowthBehavior implements BoneMealBehavior, LazyBlockProperty {
    public static final MapCodec<CropGrowthBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.STRING.fieldOf("property").forGetter(CropGrowthBehavior::propertyName),
                    IntProviders.NON_NEGATIVE_CODEC.fieldOf("increase").forGetter(CropGrowthBehavior::increase))
            .apply(instance, CropGrowthBehavior::new));

    private final String propertyName;
    private final IntProvider increase;
    private @Nullable IntegerProperty property;

    public CropGrowthBehavior(String propertyName, IntProvider increase) {
        this.propertyName = propertyName;
        this.increase = increase;
    }

    public CropGrowthBehavior(IntegerProperty property, IntProvider increase) {
        this(property.getName(), increase);
        this.property = property;
    }

    @Override
    public String propertyName() {
        return this.propertyName;
    }

    public IntProvider increase() {
        return this.increase;
    }

    @Override
    public MapCodec<CropGrowthBehavior> codec() {
        return CODEC;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        IntegerProperty property = this.getProperty(state);
        if (property == null) {
            return;
        }

        int value = Math.min(state.getValue(property) + this.increase.sample(random), this.getMaxValue(property));
        level.setBlock(pos, state.setValue(property, value), Block.UPDATE_CLIENTS);
    }

    private @Nullable IntegerProperty getProperty(BlockState state) {
        if (this.property == null || !state.hasProperty(this.property)) {
            return this.property = this.findProperty(state);
        } else {
            return this.property;
        }
    }
}
