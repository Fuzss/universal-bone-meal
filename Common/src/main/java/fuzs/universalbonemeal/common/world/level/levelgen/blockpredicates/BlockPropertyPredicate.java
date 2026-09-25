package fuzs.universalbonemeal.common.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.universalbonemeal.common.init.ModRegistry;
import fuzs.universalbonemeal.common.world.level.block.bonemeal.LazyBlockProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import org.jspecify.annotations.Nullable;

public final class BlockPropertyPredicate implements BlockPredicate, LazyBlockProperty {
    public static final MapCodec<BlockPropertyPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.STRING.fieldOf("property").forGetter(BlockPropertyPredicate::propertyName))
            .apply(instance, BlockPropertyPredicate::new));

    private final String propertyName;
    private @Nullable IntegerProperty property;

    public BlockPropertyPredicate(String propertyName) {
        this.propertyName = propertyName;
    }

    public BlockPropertyPredicate(IntegerProperty property) {
        this(property.getName());
        this.property = property;
    }

    @Override
    public String propertyName() {
        return this.propertyName;
    }

    @Override
    public boolean test(LevelAccessor level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
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
