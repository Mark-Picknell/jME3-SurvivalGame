package com.picknell.game.survivalgame;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

/**
 * Shared Android/desktop entry point.
 *
 * A lightweight, asset-free test world makes a successful renderer startup
 * visible on either platform. Legacy gameplay states remain opt-in until
 * their controls and physics are adapted for mobile.
 */
public final class SurvivalGame extends SimpleApplication {

    private static final ColorRGBA GROUND = new ColorRGBA(0.10f, 0.30f, 0.21f, 1f);
    private static final ColorRGBA GRID = new ColorRGBA(0.19f, 0.47f, 0.32f, 1f);
    private static final ColorRGBA TRUNK = new ColorRGBA(0.43f, 0.25f, 0.13f, 1f);
    private static final ColorRGBA LEAVES = new ColorRGBA(0.19f, 0.58f, 0.31f, 1f);
    private static final ColorRGBA LEAVES_LIGHT = new ColorRGBA(0.37f, 0.73f, 0.39f, 1f);
    private static final ColorRGBA RED = new ColorRGBA(0.92f, 0.07f, 0.12f, 1f);
    private static final ColorRGBA GOLD = new ColorRGBA(1f, 0.70f, 0.19f, 1f);

    private Node beacon;
    private float elapsed;

    @Override
    public void simpleInitApp() {
        // No mouse capture or keyboard controls on the Android test scene.
        flyCam.setEnabled(false);

        viewPort.setBackgroundColor(new ColorRGBA(0.035f, 0.055f, 0.09f, 1f));
        cam.setLocation(new Vector3f(7.5f, 5.8f, 9.5f));
        cam.lookAt(new Vector3f(0f, 0.95f, 0f), Vector3f.UNIT_Y);

        createTestWorld();
        createBeacon();
        createHud();
    }

    private void createTestWorld() {
        // The top of the platform is y=0. Nothing requires external assets,
        // bullet physics, touch mappings, or a platform-specific renderer.
        box(rootNode, "Grass Platform", 3.7f, 0.18f, 3.7f,
                0f, -0.18f, 0f, GROUND);

        for (int i = -3; i <= 3; i++) {
            box(rootNode, "Grid X " + i, 3.66f, 0.010f, 0.012f,
                    0f, 0.013f, i, GRID);
            box(rootNode, "Grid Z " + i, 0.012f, 0.010f, 3.66f,
                    i, 0.013f, 0f, GRID);
        }

        tree(-2.5f, -2.2f);
        tree(2.5f, -2.1f);
        box(rootNode, "Harvestable Test Stone", 0.38f, 0.28f, 0.32f,
                -2.1f, 0.28f, 1.5f, ColorRGBA.LightGray);
    }

    private void tree(float x, float z) {
        box(rootNode, "Tree Trunk", 0.14f, 0.58f, 0.14f,
                x, 0.58f, z, TRUNK);
        box(rootNode, "Tree Lower Canopy", 0.68f, 0.38f, 0.68f,
                x, 1.35f, z, LEAVES);
        box(rootNode, "Tree Upper Canopy", 0.44f, 0.38f, 0.44f,
                x, 1.98f, z, LEAVES_LIGHT);
    }

    private void createBeacon() {
        box(rootNode, "Beacon Base", 0.68f, 0.20f, 0.68f,
                0f, 0.20f, 0f, GOLD);
        box(rootNode, "Beacon Support", 0.18f, 0.57f, 0.18f,
                0f, 0.97f, 0f, ColorRGBA.DarkGray);

        beacon = new Node("Animated Beacon");
        beacon.setLocalTranslation(0f, 2.25f, 0f);
        box(beacon, "Candy Apple Red Core", 0.55f, 0.55f, 0.55f,
                0f, 0f, 0f, RED);
        box(beacon, "Gold Beacon Band", 0.59f, 0.09f, 0.59f,
                0f, 0f, 0f, GOLD);
        rootNode.attachChild(beacon);
    }

    private void createHud() {
        // The GUI is screen-space: keep the text away from the edges on phones.
        float titleSize = Math.max(24f, Math.min(52f, cam.getWidth() / 32f));
        BitmapText title = new BitmapText(guiFont);
        title.setText("SURVIVAL GAME");
        title.setSize(titleSize);
        title.setColor(GOLD);
        title.setLocalTranslation(24f, cam.getHeight() - 24f, 0f);
        guiNode.attachChild(title);

        BitmapText subtitle = new BitmapText(guiFont);
        subtitle.setText("WORLD ONLINE  |  RENDER TEST 001");
        subtitle.setSize(titleSize * 0.55f);
        subtitle.setColor(ColorRGBA.White);
        subtitle.setLocalTranslation(24f, cam.getHeight() - 32f - titleSize, 0f);
        guiNode.attachChild(subtitle);
    }

    private Geometry box(Node parent, String name, float halfX, float halfY,
                         float halfZ, float x, float y, float z, ColorRGBA color) {
        Geometry geometry = new Geometry(name, new Box(halfX, halfY, halfZ));
        Material material = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        material.setColor("Color", color);
        geometry.setMaterial(material);
        geometry.setLocalTranslation(x, y, z);
        parent.attachChild(geometry);
        return geometry;
    }

    @Override
    public void simpleUpdate(float tpf) {
        elapsed += tpf;
        beacon.rotate(0f, tpf * 0.8f, 0f);
        beacon.setLocalTranslation(
                0f, 2.25f + 0.12f * (float) Math.sin(elapsed * 2f), 0f);
    }
}
