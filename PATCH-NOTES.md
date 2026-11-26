# Hydrological 0.15.3 — worldgen performance patch

Fork of [Apothicon02/Hydrological](https://github.com/Apothicon02/Hydrological) (MIT), branch `1.20.1`,
with one change to `NoiseBasedChunkGeneratorMixin.doFill`.

## The problem

For **every water or lava block placed**, the aquifer logic rescanned the whole column from Y=240
downwards in steps of 4, looking for the first solid block — up to 56 `getBlockState` calls, each
allocating a fresh `BlockPos`. In a mod built around water, that is tens of thousands of redundant
lookups and allocations per chunk.

## The change

The scan's answer depends only on x and z. The chunk is filled strictly top-down, so once a solid
block has been seen above the current row, nothing above it can change again and the answer is
final for that column. It is now computed once per column and cached in an `int[256]`.

A column that has not yet produced a solid block is deliberately *not* cached, because a later row
could still produce one — so the result is identical to rescanning every time.

Also hoisted out of the inner loop, all constant for the whole chunk:
`settings.value()`, `defaultBlock()`, `isAquifersEnabled()`, `SharedConstants.debugVoidTerrain(pos)`.

## Output

Block-for-block identical to upstream. No noise, density function, interpolation or surface rule was
touched — the same seed produces the same world.

---

# Change 2 — the global lock in `ToHeightmap`

## The problem

`ToHeightmap.compute` is a density function, so it runs **once per density sample** — on the order
of 100,000 times per chunk. Every one of those calls did this:

```java
synchronized (heightmap) {
    heightmap.defaultReturnValue(Double.NaN);
    storedValue = heightmap.get(key);
}
```

One static map, one monitor, every sample, every thread. With a multithreaded chunk generator such
as C2ME, every worldgen thread in the game queues behind that single lock for every sample it
takes, so the generator runs effectively single-threaded with lock-handoff overhead on top. This is
why the mod can be slower than terrain mods that do far more work — the cost is contention, not
mathematics.

Two further hazards came with it:

- `heightmap` is **reassigned** on `ServerStoppingEvent` while other threads may still be
  synchronising on the old instance. `synchronized (heightmap)` locks whatever the field points at,
  so after reassignment two threads can hold "the" lock at once and write concurrently into a
  `Long2DoubleOpenHashMap`, which is not thread safe. That can corrupt the map or spin forever.
- `defaultReturnValue` was being set inside the lock on every read, mutating shared state to a
  constant for no reason.

## The change

The map becomes `HeightmapCache`: 64 striped `Long2DoubleOpenHashMap`s, each with its own monitor,
key mixed before striping because raw chunk keys are structured in their low bits. No boxing, and
contention becomes rare rather than total. `defaultReturnValue` is set once per stripe at
construction.

## Output

Identical. Same keys, same values, same hit/miss behaviour.

---

# Noted but deliberately NOT changed

`ToHeightmap` returns `newY - 20 > y ? -1 : value` when it computes a column fresh, but
`newY - 30 > y ? -1 : value` when it reads that same column back from the cache. The two paths
disagree by 10 blocks.

Because the cache is global and persists across chunks, whether a given sample takes the fresh path
or the cached path depends on **which chunk was generated first** — which depends on where the
player walked. So the same seed can produce slightly different terrain between playthroughs.

This is upstream behaviour. Making the two paths agree would make generation deterministic, but it
would also change terrain relative to existing worlds, so it is left exactly as it is.

Also left alone, as they would change output or carry reentrancy risk:

- `ChunkPos.asLong(x/4, z/4)` truncates toward zero, so columns at x = -3 and x = +3 share a cache
  entry. Fixing the negative-coordinate folding would change terrain near the axes.
- The `new SinglePointContext(...)` allocations in `CubicalScale` and in `ToHeightmap`'s scan loop.
  A shared mutable context would be faster, but these functions can nest, and a nested call would
  corrupt the outer one's coordinates mid-evaluation.
