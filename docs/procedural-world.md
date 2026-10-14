# Seeded exploration world (first runtime pass)

The live `game` module now contains a **procedurally generated landscape** rather
than a permanent fixed-size render-test platform. Android and desktop share one
`SurvivalGame` entry point.

## Runtime components

| Class | Responsibility |
| --- | --- |
| `WorldAppState` | Attaches/removes the world and owns its lifetime |
| `TerrainManager` | Samples seeded OpenSimplex heights using **absolute world coordinates**, builds 25×25-vertex colored meshes per 32×32-unit chunk |
| `BiomeSystem` | Selects ocean, shore, meadow, woodland, or highland from elevation and seeded moisture |
| `ChunkManager` | Loads a **3×3 neighborhood** around the exploration focus and unloads out-of-range chunks; scatters reproducible trees and rocks |
| `OrbitCameraController` | Android gestures and desktop mouse/keyboard exploration |

The world seed is currently `0x5EED2026L` in `SurvivalGame.java`.
At startup the game shows a rotating red/gold origin beacon, terrain, a sea-level
plane, procedurally scattered objects, and a status overlay with seed, chunk
count, biome, and current focus coordinates.

### Controls

- Android: **one-finger drag** orbits; **two-finger drag** pans the focus; **pinch** zooms.
- Desktop: **left mouse drag** orbits; **WASD** moves the focus; **mouse wheel** zooms.
- When idle the camera rotates slowly to make a launched scene visibly alive.

Controls use the shared jME input API. Verify Android touch dispatch on a
device; no platform-specific touch-library dependency was introduced.

### Boundaries and next tests

- Archived `TestGameplayAppState` is still **opt-in**. No Bullet physics, player
  collisions, harvesting, inventory, or save format is enabled.
- Meshes are intentionally unshaded, geometry-only, and texture-free to maximize
  initial Android compatibility. Shading, vegetation batching, LOD, persistent
  chunk state, and colliders are future work.
- Terrain edges share coordinates and deterministic noise; switching chunk
  neighborhoods should not leave geometry gaps.
- Chunk creation is currently synchronous on the render thread, which may
  introduce a short hitch at boundaries; measure before introducing async
  generation/mesh upload.
- Verify on the device:

  ```sh
  ./gradlew :game:compileJava :app:assembleDebug
  ```

  Then launch the APK and inspect the visible terrain, color bands, sea level,
  beacon, touch orbit, two-finger panning across chunk boundaries, and pinch zoom.
  Check that the CHUNKS overlay reads `9` once loaded.

The original archive remains unchanged and can be compared through Git history.
