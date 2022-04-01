package org.bukkit.metadata;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.metadata.BlockMetadataStore;
import org.bukkit.craftbukkit.metadata.SpecializedBlockMetadataStore;
import org.bukkit.plugin.Plugin;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jol.info.GraphLayout;

@State(Scope.Thread)
public class BlockMetadataStoreBenchmark {
    private static MetadataStore<Block> oldStore;
    private static MetadataStore<Block> newStore;
    private static Block[] blocks;
    private static Plugin owningPlugin; // Need the reference to stay alive
    private static String key;
    private static MetadataValue value;

    @State(Scope.Benchmark)
    public static class OldStoreRead {
        MetadataStore<Block> populatedStore;
        Plugin owningPlugin;

        @Setup(Level.Invocation) // Need to generate each time - value might be altered
        public void setup() {
            owningPlugin = new TestPlugin();
            populatedStore = new BlockMetadataStore(null);
            MetadataValue value = new FixedMetadataValue(owningPlugin, 2);
            for (Block block : generateBlocks()) {
                populatedStore.setMetadata(block, "READ-KEY", value);
            }
        }
    }

    @State(Scope.Benchmark)
    public static class NewStoreRead {
        MetadataStore<Block> populatedStore;
        Plugin owningPlugin;

        @Setup(Level.Invocation) // Need to generate each time - value might be altered
        public void setup() {
            owningPlugin = new TestPlugin();
            populatedStore = new SpecializedBlockMetadataStore(null);
            MetadataValue value = new FixedMetadataValue(owningPlugin, 2);
            for (Block block : generateBlocks()) {
                populatedStore.setMetadata(block, "READ-KEY", value);
            }
        }
    }

    @Setup(Level.Trial)
    public void once() {
        Block[] blocks = generateBlocks();

        MetadataStore<Block> oldStore = new BlockMetadataStore(null);
        MetadataStore<Block> newStore = new SpecializedBlockMetadataStore(null);
        MetadataValue value = new FixedMetadataValue(new TestPlugin(), 2);

        for (Block block : blocks) {
            oldStore.setMetadata(block, "SIZE-KEY", value);
            newStore.setMetadata(block, "SIZE-KEY", value);
        }

        System.out.println();
        System.out.println("Memory usage comparison (bytes, " + blocks.length + " blocks registered)");
        System.out.println("Old store: " + GraphLayout.parseInstance(oldStore).totalSize());
        System.out.println("New store: " + GraphLayout.parseInstance(newStore).totalSize());
        System.out.println();
    }

    @Setup(Level.Invocation) // Need to generate each time - value might be altered
    public void init() {
        owningPlugin = new TestPlugin();
        key = "TEST-KEY-123";
        value = new FixedMetadataValue(owningPlugin, 1);
        oldStore = new BlockMetadataStore(null);
        newStore = new SpecializedBlockMetadataStore(null);
        blocks = generateBlocks();
    }

    @Benchmark
    public void insertOld(Blackhole blackhole) {
        for (Block block : blocks) {
            oldStore.setMetadata(block, key, value);
        }
        blackhole.consume(oldStore);
    }

    @Benchmark
    public void insertNew(Blackhole blackhole) {
        for (Block block : blocks) {
            newStore.setMetadata(block, key, value);
        }
        blackhole.consume(newStore);
    }

    @Benchmark
    public void getOld(Blackhole blackhole, OldStoreRead state) {
        for (Block block : blocks) {
            blackhole.consume(state.populatedStore.getMetadata(block, key));
        }
    }

    @Benchmark
    public void getNew(Blackhole blackhole, NewStoreRead state) {
        for (Block block : blocks) {
            blackhole.consume(state.populatedStore.getMetadata(block, key));
        }
    }

    @Benchmark
    public void hasOld(Blackhole blackhole, OldStoreRead state) {
        for (Block block : blocks) {
            blackhole.consume(state.populatedStore.hasMetadata(block, key));
        }
    }

    @Benchmark
    public void hasNew(Blackhole blackhole, NewStoreRead state) {
        for (Block block : blocks) {
            blackhole.consume(state.populatedStore.hasMetadata(block, key));
        }
    }

    @Benchmark
    public void removeOld(Blackhole blackhole, OldStoreRead state) {
        for (Block block : blocks) {
            state.populatedStore.removeMetadata(block, key, state.owningPlugin);
        }
        blackhole.consume(state.populatedStore);
    }

    @Benchmark
    public void removeNew(Blackhole blackhole, NewStoreRead state) {
        for (Block block : blocks) {
            state.populatedStore.removeMetadata(block, key, state.owningPlugin);
        }
        blackhole.consume(state.populatedStore);
    }

    private static Block[] generateBlocks() {
        Block[] blocks = new Block[20];
        for (int x = 2, y = 0, z = 2, i = 0; i < blocks.length; i++, x += 300, z += 200, y += 10) {
            blocks[i] = makeBlock(x, y, z);
        }
        return blocks;
    }

    private static Block makeBlock(int x, int y, int z) {
        return new TestBlock(x, y, z);
    }

    private static class TestPlugin implements Plugin {

    }

    private static class TestBlock implements Block {
        private final int x;
        private final int y;
        private final int z;

        protected TestBlock(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public int getX() {
            return x;
        }

        @Override
        public int getY() {
            return y;
        }

        @Override
        public int getZ() {
            return z;
        }

        @Override
        public World getWorld() {
            return null;
        }
    }
}
