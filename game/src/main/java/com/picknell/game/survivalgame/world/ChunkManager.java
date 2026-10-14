package com.picknell.game.survivalgame.world;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/** Maintains a 3x3 neighborhood of terrain patches as the viewpoint travels. */
public final class ChunkManager {
    private static final int RADIUS = 1;

    private final TerrainManager terrain;
    private final AssetManager assets;
    private final Node world;
    private final Map<Long, Node> loaded = new HashMap<>();
    private final Material trunk;
    private final Material canopy;
    private final Material canopyLight;
    private final Material rock;
    private final Geometry ocean;
    private int lastCenterX = Integer.MIN_VALUE;
    private int lastCenterZ = Integer.MIN_VALUE;

    public ChunkManager(TerrainManager terrain, AssetManager assets, Node world) {
        this.terrain = terrain;
        this.assets = assets;
        this.world = world;
        trunk = colored(new ColorRGBA(0.39f, 0.24f, 0.13f, 1f));
        canopy = colored(new ColorRGBA(0.16f, 0.44f, 0.23f, 1f));
        canopyLight = colored(new ColorRGBA(0.28f, 0.62f, 0.29f, 1f));
        rock = colored(new ColorRGBA(0.49f, 0.53f, 0.52f, 1f));
        Material water = colored(new ColorRGBA(0.07f, 0.36f, 0.57f, 1f));
        float waterHalfSize = TerrainManager.CHUNK_SIZE * (RADIUS + 1.3f);
        ocean = new Geometry("Sea level", new Box(waterHalfSize, 0.015f, waterHalfSize));
        ocean.setMaterial(water);
        ocean.setLocalTranslation(0f, TerrainManager.SEA_LEVEL - 0.035f, 0f);
        world.attachChild(ocean);
    }

    public int loadedCount() { return loaded.size(); }

    public void update(float x, float z) {
        int cx = (int) Math.floor(x / TerrainManager.CHUNK_SIZE);
        int cz = (int) Math.floor(z / TerrainManager.CHUNK_SIZE);
        if (cx == lastCenterX && cz == lastCenterZ) return;
        Set<Long> wanted = new HashSet<>();
        for (int dz = -RADIUS; dz <= RADIUS; dz++) {
            for (int dx = -RADIUS; dx <= RADIUS; dx++) {
                int ix = cx + dx;
                int iz = cz + dz;
                long key = key(ix, iz);
                wanted.add(key);
                if (!loaded.containsKey(key)) {
                    Node patch = createPatch(ix, iz);
                    world.attachChild(patch);
                    loaded.put(key, patch);
                }
            }
        }
        // Remove patches outside the view neighborhood, without keeping old meshes alive.
        loaded.entrySet().removeIf(entry -> {
            if (wanted.contains(entry.getKey())) return false;
            entry.getValue().removeFromParent();
            return true;
        });
        lastCenterX = cx;
        lastCenterZ = cz;
        ocean.setLocalTranslation((cx + 0.5f) * TerrainManager.CHUNK_SIZE,
                TerrainManager.SEA_LEVEL - 0.035f,
                (cz + 0.5f) * TerrainManager.CHUNK_SIZE);
    }

    public void clear() {
        for (Node patch : loaded.values()) patch.removeFromParent();
        loaded.clear();
        ocean.removeFromParent();
    }

    private Node createPatch(int cx, int cz) {
        Node patch = new Node("Chunk " + cx + "," + cz);
        patch.setLocalTranslation(cx * TerrainManager.CHUNK_SIZE, 0f,
                cz * TerrainManager.CHUNK_SIZE);
        patch.attachChild(terrain.createChunk(assets, cx, cz));

        long hash = terrain.seed() ^ ((long) cx * 0x632BE59BD9B4E019L)
                ^ ((long) cz * 0x9E3779B97F4A7C15L);
        Random random = new Random(hash);
        for (int i = 0; i < 9; i++) {
            float lx = 2.5f + random.nextFloat() * (TerrainManager.CHUNK_SIZE - 5f);
            float lz = 2.5f + random.nextFloat() * (TerrainManager.CHUNK_SIZE - 5f);
            float wx = cx * TerrainManager.CHUNK_SIZE + lx;
            float wz = cz * TerrainManager.CHUNK_SIZE + lz;
            float y = terrain.heightAt(wx, wz);
            if (y <= 0.75f) continue;
            BiomeSystem.Biome biome = terrain.biomeAt(wx, wz);
            if ((biome == BiomeSystem.Biome.WOODLAND ||
                    (biome == BiomeSystem.Biome.MEADOW && i % 3 == 0)) && i < 6) {
                block(patch, "Tree trunk", lx, y + 0.57f, lz, 0.13f, 0.57f, 0.13f, trunk);
                block(patch, "Canopy", lx, y + 1.40f, lz, 0.62f, 0.35f, 0.62f, canopy);
                block(patch, "Upper canopy", lx, y + 1.98f, lz, 0.43f, 0.34f, 0.43f, canopyLight);
            } else if (i % 4 == 0) {
                block(patch, "Stone", lx, y + 0.25f, lz, 0.33f, 0.25f, 0.29f, rock);
            }
        }
        return patch;
    }

    private Material colored(ColorRGBA color) {
        Material m = new Material(assets, "Common/MatDefs/Misc/Unshaded.j3md");
        m.setColor("Color", color);
        return m;
    }

    private static void block(Node parent, String name, float x, float y, float z,
                              float sx, float sy, float sz, Material material) {
        Geometry geometry = new Geometry(name, new Box(sx, sy, sz));
        geometry.setLocalTranslation(x, y, z);
        geometry.setMaterial(material);
        parent.attachChild(geometry);
    }

    private static long key(int x, int z) {
        return ((long) x << 32) | (z & 0xFFFFFFFFL);
    }
}
