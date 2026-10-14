package com.picknell.survivalgame.util;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.texture.Texture;

public class TestHelper {

    public static Material createUnshadedMaterial(AssetManager assetManager, ColorRGBA color) {
        return createUnshadedMaterial(assetManager, null, null, color);
    }

    public static Material createUnshadedMaterial(AssetManager assetManager, Texture colorMap) {
        return createUnshadedMaterial(assetManager, colorMap, null, null);
    }

    public static Material createUnshadedMaterial(AssetManager assetManager, Texture colorMap, ColorRGBA color) {
        return createUnshadedMaterial(assetManager, colorMap, null, color);
    }

    public static Material createUnshadedMaterial(AssetManager assetManager, Texture colorMap, Texture lightMap, ColorRGBA color) {
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        if(colorMap != null) {
            material.setTexture("ColorMap", colorMap);
        }
        if(lightMap != null) {
            material.setTexture("LightMap", lightMap);
        }
        if(color != null) {
            material.setColor("Color", color);
        }
        //material.setBoolean("UseVertexColor", useVertexColor);
        return material;
    }

}
