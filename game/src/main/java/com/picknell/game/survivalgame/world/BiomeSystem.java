package com.picknell.game.survivalgame.world;

import com.jme3.math.ColorRGBA;
import com.picknell.game.survivalgame.util.OpenSimplexNoise;

/** Stable biomes derived from elevation and seeded world-space moisture. */
public final class BiomeSystem {
    public enum Biome { OCEAN, SHORE, MEADOW, WOODLAND, HIGHLAND }

    private final OpenSimplexNoise moisture;

    public BiomeSystem(long seed) {
        moisture = new OpenSimplexNoise(seed ^ 0x61C8864680B583EBL);
    }

    public Biome at(float x, float z, float elevation) {
        if (elevation < -0.15f) return Biome.OCEAN;
        if (elevation < 0.7f) return Biome.SHORE;
        if (elevation > 4.6f) return Biome.HIGHLAND;
        if (moisture.eval(x * 0.024, z * 0.024) > 0.10) return Biome.WOODLAND;
        return Biome.MEADOW;
    }

    public ColorRGBA color(Biome biome) {
        switch (biome) {
            case OCEAN:    return new ColorRGBA(0.16f, 0.27f, 0.32f, 1f);
            case SHORE:    return new ColorRGBA(0.73f, 0.65f, 0.42f, 1f);
            case WOODLAND: return new ColorRGBA(0.12f, 0.37f, 0.20f, 1f);
            case HIGHLAND: return new ColorRGBA(0.41f, 0.44f, 0.37f, 1f);
            default:       return new ColorRGBA(0.27f, 0.55f, 0.27f, 1f);
        }
    }
}
