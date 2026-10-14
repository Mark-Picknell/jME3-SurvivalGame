package com.picknell.game.survivalgame;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.picknell.game.survivalgame.player.OrbitCameraController;
import com.picknell.game.survivalgame.world.WorldAppState;

/** Shared Android and desktop entry point for the continuously generated world. */
public final class SurvivalGame extends SimpleApplication {
    private static final long WORLD_SEED = 0x5EED2026L;
    private static final ColorRGBA GOLD = new ColorRGBA(1f, 0.72f, 0.20f, 1f);

    private OrbitCameraController cameraRig;
    private WorldAppState world;
    private Node beacon;
    private BitmapText status;
    private float elapsed;
    private float hudTimer;

    @Override
    public void simpleInitApp() {
        flyCam.setEnabled(false);
        viewPort.setBackgroundColor(new ColorRGBA(0.055f, 0.11f, 0.17f, 1f));

        world = new WorldAppState(WORLD_SEED);
        stateManager.attach(world);
        cameraRig = new OrbitCameraController(cam);
        inputManager.addRawInputListener(cameraRig);

        createBeacon();
        createHud();
    }

    private void createBeacon() {
        float ground = world.heightAt(0f, 0f);
        box(rootNode, "Beacon base", 0f, ground + 0.18f, 0f,
                0.65f, 0.18f, 0.65f, GOLD);
        box(rootNode, "Beacon stem", 0f, ground + 0.92f, 0f,
                0.16f, 0.56f, 0.16f, ColorRGBA.DarkGray);
        beacon = new Node("Rotating arrival marker");
        box(beacon, "Red signal", 0f, 0f, 0f,
                0.50f, 0.50f, 0.50f, new ColorRGBA(0.93f, 0.08f, 0.12f, 1f));
        box(beacon, "Golden ring", 0f, 0f, 0f,
                0.55f, 0.09f, 0.55f, GOLD);
        beacon.setLocalTranslation(0f, ground + 2.15f, 0f);
        rootNode.attachChild(beacon);
    }

    private void createHud() {
        float size = Math.max(24f, Math.min(48f, cam.getWidth() / 30f));
        BitmapText title = new BitmapText(guiFont);
        title.setText("SURVIVAL GAME");
        title.setSize(size);
        title.setColor(GOLD);
        title.setLocalTranslation(24f, cam.getHeight() - 22f, 0f);
        guiNode.attachChild(title);

        status = new BitmapText(guiFont);
        status.setSize(size * 0.55f);
        status.setColor(ColorRGBA.White);
        status.setLocalTranslation(24f, cam.getHeight() - size - 28f, 0f);
        guiNode.attachChild(status);

        BitmapText help = new BitmapText(guiFont);
        help.setText("DRAG: ORBIT   |   2 FINGERS: PAN   |   PINCH: ZOOM");
        help.setSize(size * 0.46f);
        help.setColor(new ColorRGBA(0.82f, 0.90f, 0.94f, 1f));
        help.setLocalTranslation(24f, 46f, 0f);
        guiNode.attachChild(help);
        updateStatus();
    }

    private void box(Node parent, String name, float x, float y, float z,
                            float sx, float sy, float sz, ColorRGBA color) {
        // Kept intentionally small: the animated beacon is a navigation landmark,
        // not a dependency of the procedural terrain/biome systems.
        Geometry geometry = new Geometry(name, new Box(sx, sy, sz));
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setColor("Color", color);
        geometry.setMaterial(material);
        geometry.setLocalTranslation(x, y, z);
        parent.attachChild(geometry);
    }

    private void updateStatus() {
        status.setText("SEED " + WORLD_SEED + "  |  CHUNKS " + world.loadedChunkCount()
                + "  |  " + world.biomeAt(cameraRig.focusX(), cameraRig.focusZ())
                + "  |  " + Math.round(cameraRig.focusX()) + "," + Math.round(cameraRig.focusZ()));
    }

    @Override
    public void simpleUpdate(float tpf) {
        elapsed += tpf;
        if (world.isInitialized()) {
            world.setFocus(cameraRig.focusX(), cameraRig.focusZ());
        }
        cameraRig.update(tpf, world.heightAt(cameraRig.focusX(), cameraRig.focusZ()));
        beacon.rotate(0f, tpf * 0.8f, 0f);
        beacon.setLocalTranslation(0f, world.heightAt(0f, 0f) + 2.15f
                + (float) Math.sin(elapsed * 2f) * 0.12f, 0f);
        hudTimer += tpf;
        if (hudTimer >= 1f) {
            hudTimer = 0f;
            updateStatus();
        }
    }
}
