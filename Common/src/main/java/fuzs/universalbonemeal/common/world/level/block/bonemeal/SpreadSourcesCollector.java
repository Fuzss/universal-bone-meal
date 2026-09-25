package fuzs.universalbonemeal.common.world.level.block.bonemeal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.stream.Stream;

public interface SpreadSourcesCollector {

    HolderSet<Block> spreadSources();

    int spreadWidth();

    int spreadHeight();

    default Stream<Block> findSpreadSources(LevelAccessor level, BlockPos pos) {
        return BlockPos.betweenClosedStream(pos.offset(-this.spreadWidth(), -this.spreadHeight(), -this.spreadWidth()),
                        pos.offset(this.spreadWidth(), this.spreadHeight(), this.spreadWidth()))
                .map(level::getBlockState)
                .filter((BlockState state) -> this.spreadSources().contains(state.typeHolder()))
                .map(BlockState::getBlock)
                .distinct();
    }
}
