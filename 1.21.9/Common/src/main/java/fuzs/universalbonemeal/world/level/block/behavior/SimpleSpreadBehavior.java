package fuzs.universalbonemeal.world.level.block.behavior;

public class SimpleSpreadBehavior extends SpreadAwayBehavior {
    private final int spreadWidth;
    private final int mostSuccesses;

    public SimpleSpreadBehavior(int spreadWidth, int mostSuccesses) {
        this.spreadWidth = spreadWidth;
        this.mostSuccesses = mostSuccesses;
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
