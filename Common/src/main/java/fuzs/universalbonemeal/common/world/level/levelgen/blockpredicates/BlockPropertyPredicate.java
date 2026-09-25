package fuzs.universalbonemeal.common.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.universalbonemeal.common.init.ModRegistry;
import fuzs.universalbonemeal.common.world.level.block.bonemeal.LazyBlockProperty;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.minecraft.world.level.levelgen.blockpredicates.StateTestingPredicate;
import org.jspecify.annotations.Nullable;

public final class BlockPropertyPredicate extends StateTestingPredicate implements LazyBlockProperty {
    public static final MapCodec<BlockPropertyPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> stateTestingCodec(
            instance).and(Codec.STRING.fieldOf("property").forGetter(BlockPropertyPredicate::propertyName))
            .apply(instance, BlockPropertyPredicate::new));

    private final String propertyName;
    private @Nullable IntegerProperty property;

    public BlockPropertyPredicate(Vec3i offset, String propertyName) {
        super(offset);
        this.propertyName = propertyName;
    }

    public BlockPropertyPredicate(IntegerProperty property) {
        this(Vec3i.ZERO, property);
    }

    public BlockPropertyPredicate(Vec3i offset, IntegerProperty property) {
        this(offset, property.getName());
        this.property = property;
    }

    @Override
    public String propertyName() {
        return this.propertyName;
    }

    @Override
    protected boolean test(BlockState state) {
        IntegerProperty property = this.getProperty(state);
        return property != null && state.getValue(property) < this.getMaxValue(property);
    }

    private @Nullable IntegerProperty getProperty(BlockState state) {
        if (this.property == null || !state.hasProperty(this.property)) {
            return this.property = this.findProperty(state);
        } else {
            return this.property;
        }
    }

    @Override
    public BlockPredicateType<?> type() {
        return ModRegistry.BLOCK_PROPERTY_BLOCK_PREDICATE_TYPE.value();
    }
}
