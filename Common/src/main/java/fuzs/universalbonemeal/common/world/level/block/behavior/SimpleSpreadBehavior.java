package fuzs.universalbonemeal.common.world.level.block.behavior;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class SimpleSpreadBehavior extends SpreadAwayBehavior {
    public static final MapCodec<SimpleSpreadBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.intRange(1, 16).fieldOf("spread_width").forGetter(SimpleSpreadBehavior::getSpreadWidth),
                    Codec.intRange(1, 16).fieldOf("most_successes").forGetter(SimpleSpreadBehavior::getMostSuccesses))
            .apply(instance, SimpleSpreadBehavior::new));

    private final int spreadWidth;
    private final int mostSuccesses;

    public SimpleSpreadBehavior(int spreadWidth, int mostSuccesses) {
        this.spreadWidth = spreadWidth;
        this.mostSuccesses = mostSuccesses;
    }

    @Override
    public MapCodec<SimpleSpreadBehavior> codec() {
        return CODEC;
    }

    @Override
    protected int getSpreadWidth() {
        return this.spreadWidth;
    }

    @Override
    protected int getMostSuccesses() {
        return this.mostSuccesses;
    }
}
