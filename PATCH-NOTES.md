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
