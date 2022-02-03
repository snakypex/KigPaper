package net.minecraft.server;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 2)
@Measurement(iterations = 2)
public class ChunkCollectionsBenchmark {
    private static <C extends Collection<ChunkCoordIntPair>> void consumeCollection
            (Blackhole blackhole, Supplier<C> collectionSupplier, BiConsumer<C, ChunkCoordIntPair> updater, Consumer<C> sorter) {
        // Assumptions
        int chunkX = 0;
        int chunkZ = 0;
        int viewDistance = 12;

        C chunkList = collectionSupplier.get();

        // PaperSpigot start - Player view distance API
        for (int x = chunkX - viewDistance; x <= chunkX + viewDistance; ++x) {
            for (int z = chunkZ - viewDistance; z <= chunkZ + viewDistance; ++z) {
                // PaperSpigot end
                updater.accept(chunkList, new ChunkCoordIntPair(x, z));
            }
        }
        sorter.accept(chunkList);
        for (ChunkCoordIntPair pair : chunkList) {
            blackhole.consume(pair);
        }
    }

    @Benchmark
    public void vanilla(Blackhole blackhole) {
        consumeCollection(blackhole, LinkedList::new, LinkedList::add,
                l -> l.sort(new PlayerChunkMap.ChunkCoordComparator(0, 0)));
    }

    @Benchmark
    public void preAllocArrayList(Blackhole blackhole) {
        consumeCollection(blackhole, () -> new ArrayList<>(24 * 24), ArrayList::add,
                l -> l.sort(new PlayerChunkMap.ChunkCoordComparator(0, 0)));
    }

    @Benchmark
    public void treeSet(Blackhole blackhole) {
        consumeCollection(blackhole, () -> new TreeSet<>(new PlayerChunkMap.ChunkCoordComparator(0, 0)),
                TreeSet::add, $ -> {});
    }
}
