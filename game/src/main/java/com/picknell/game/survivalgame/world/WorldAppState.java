package com.picknell.game.survivalgame.world;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/** Owns world lifecycle independently of the Android and desktop launchers. */
public final class WorldAppState extends BaseAppState {
    private final TerrainManager terrain;
    private Node worldRoot;
    private ChunkManager chunks;

    public WorldAppState(long seed) {
        terrain = new TerrainManager(seed);
    }

    public long seed() { return terrain.seed(); }
    public float heightAt(float x, float z) { return terrain.heightAt(x, z); }
    public BiomeSystem.Biome biomeAt(float x, float z) {
        return terrain.biomeAt(x, z);
    }
    public int loadedChunkCount() { return chunks == null ? 0 : chunks.loadedCount(); }

    public void setFocus(float x, float z) {
        if (chunks != null && isEnabled()) chunks.update(x, z);
    }

    @Override
    protected void initialize(Application app) {
        worldRoot = new Node("Seeded procedural world");
        ((SimpleApplication) app).getRootNode().attachChild(worldRoot);
        chunks = new ChunkManager(terrain, app.getAssetManager(), worldRoot);
        chunks.update(0f, 0f);
    }

    @Override
    protected void cleanup(Application app) {
        if (chunks != null) chunks.clear();
        if (worldRoot != null) worldRoot.removeFromParent();
        chunks = null;
        worldRoot = null;
    }

    @Override
    protected void onEnable() {
        if (worldRoot != null) worldRoot.setCullHint(Spatial.CullHint.Inherit);
    }

    @Override
    protected void onDisable() {
        if (worldRoot != null) worldRoot.setCullHint(Spatial.CullHint.Always);
    }
}
