package com.picknell.game.survivalgame.world;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.picknell.game.survivalgame.util.OpenSimplexNoise;

/** Deterministic, continuous terrain heights and mobile-friendly colored mesh patches. */
public final class TerrainManager {
    public static final int CHUNK_SIZE = 32;
    public static final int SEGMENTS = 24;
    public static final float SEA_LEVEL = 0f;

    private final OpenSimplexNoise shape;
    private final OpenSimplexNoise detail;
    private final BiomeSystem biomes;
    private final long seed;
    private Material terrainMaterial;

    public TerrainManager(long seed) {
        this.seed = seed;
        shape = new OpenSimplexNoise(seed);
        detail = new OpenSimplexNoise(seed ^ 0x2D98E1FD6B3C4A51L);
        biomes = new BiomeSystem(seed);
    }

    public long seed() { return seed; }
    public BiomeSystem biomes() { return biomes; }

    /** Sample using absolute coordinates so adjacent chunks share identical edge heights. */
    public float heightAt(float x, float z) {
        double broad = shape.eval(x * 0.018, z * 0.018);
        double middle = shape.eval(x * 0.061 + 19.7, z * 0.061 - 9.4);
        double small = detail.eval(x * 0.16, z * 0.16);
        return (float) (0.65 + broad * 5.2 + middle * 1.65 + small * 0.38);
    }

    public BiomeSystem.Biome biomeAt(float x, float z) {
        return biomes.at(x, z, heightAt(x, z));
    }

    public Geometry createChunk(AssetManager assets, int chunkX, int chunkZ) {
        if (terrainMaterial == null) {
            terrainMaterial = new Material(assets, "Common/MatDefs/Misc/Unshaded.j3md");
            terrainMaterial.setBoolean("VertexColor", true);
        }
        final int stride = SEGMENTS + 1;
        final int points = stride * stride;
        float[] positions = new float[points * 3];
        float[] colors = new float[points * 4];
        int[] indices = new int[SEGMENTS * SEGMENTS * 6];
        float step = CHUNK_SIZE / (float) SEGMENTS;
        float originX = chunkX * (float) CHUNK_SIZE;
        float originZ = chunkZ * (float) CHUNK_SIZE;

        int vertex = 0;
        for (int z = 0; z <= SEGMENTS; z++) {
            float localZ = z * step;
            for (int x = 0; x <= SEGMENTS; x++) {
                float localX = x * step;
                float wx = originX + localX;
                float wz = originZ + localZ;
                float elevation = heightAt(wx, wz);
                ColorRGBA c = biomes.color(biomes.at(wx, wz, elevation));
                int p = vertex * 3;
                positions[p] = localX;
                positions[p + 1] = elevation;
                positions[p + 2] = localZ;
                int k = vertex * 4;
                colors[k] = c.r;
                colors[k + 1] = c.g;
                colors[k + 2] = c.b;
                colors[k + 3] = 1f;
                vertex++;
            }
        }
        // Winding is counterclockwise when viewed from above (+Y).
        int index = 0;
        for (int z = 0; z < SEGMENTS; z++) {
            for (int x = 0; x < SEGMENTS; x++) {
                int a = z * stride + x;
                int b = a + 1;
                int c = a + stride;
                int d = c + 1;
                indices[index++] = a;
                indices[index++] = c;
                indices[index++] = b;
                indices[index++] = b;
                indices[index++] = c;
                indices[index++] = d;
            }
        }
        Mesh mesh = new Mesh();
        mesh.setBuffer(VertexBuffer.Type.Position, 3, positions);
        mesh.setBuffer(VertexBuffer.Type.Color, 4, colors);
        mesh.setBuffer(VertexBuffer.Type.Index, 3, indices);
        mesh.updateBound();
        mesh.updateCounts();
        mesh.setStatic();
        Geometry geometry = new Geometry("Terrain " + chunkX + "," + chunkZ, mesh);
        geometry.setMaterial(terrainMaterial);
        return geometry;
    }
}
