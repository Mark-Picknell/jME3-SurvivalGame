package com.picknell.survivalgame.app;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.picknell.survivalgame.util.TestHelper;

public class TestDisplayAppState extends BaseAppState {

    @Override
    protected void initialize(Application app) {
        SimpleApplication application =  (SimpleApplication) app;

        AssetManager assetManager = application.getAssetManager();

        Camera camera = application.getCamera();
        Node rootNode = application.getRootNode();
        Node guiNode = application.getGuiNode();

        BitmapFont font =  assetManager.loadFont("Interface/Fonts/Console.fnt");

        Node healthStatus = createHealthBar(assetManager, font);
        healthStatus.setLocalTranslation(16, camera.getHeight() - (32 + 16), 0);
        guiNode.attachChild(healthStatus);

        final int numberOfSlots = 5;
        final int slotSize = 64;

        Node quickAccess = createQuickAccess(assetManager, font, numberOfSlots, slotSize);
        quickAccess.setLocalTranslation((camera.getWidth() - (numberOfSlots * slotSize)) * 0.5f, 16, 0);
        guiNode.attachChild(quickAccess);


    }

    @Override
    protected void cleanup(Application app) {

    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

    private Node createHealthBar(AssetManager assetManager, BitmapFont font) {
        Geometry backgroundGeometry = new Geometry("Health.Background.Geometry", new Quad(256, 32));

        Material backgroundMaterial = TestHelper.createUnshadedMaterial(assetManager, ColorRGBA.White);
        backgroundMaterial.getAdditionalRenderState().setWireframe(true);
        backgroundGeometry.setMaterial(backgroundMaterial);

        Node background = new Node("Health.Foreground");
        background.attachChild(backgroundGeometry);

        Geometry indicatorGeometry = new Geometry("Health.Indicator.Geometry", new Quad(256 - 32 - 4 - 4, 32 - 4 - 4));

        Material indicatorMaterial = TestHelper.createUnshadedMaterial(assetManager, ColorRGBA.Red);
        indicatorMaterial.getAdditionalRenderState().setWireframe(true);
        indicatorGeometry.setMaterial(indicatorMaterial);

        Node indicator = new Node("Health.Indicator");
        indicator.setLocalTranslation(32 + 4, 4, 0);
        indicator.attachChild(indicatorGeometry);

        Geometry foregroundGeometry = new Geometry("Health.Foreground.Geometry", new Quad(256, 32));

        Material foregroundMaterial = TestHelper.createUnshadedMaterial(assetManager, ColorRGBA.White);
        foregroundMaterial.getAdditionalRenderState().setWireframe(true);
        foregroundGeometry.setMaterial(foregroundMaterial);

        Node foreground = new Node("Health.Foreground");
        foreground.attachChild(foregroundGeometry);

        BitmapText label = font.createLabel("HP");
        label.setName("Health.Label");
        label.setLocalTranslation((32 - label.getLineWidth()) * 0.5f, (32 + label.getHeight()) * 0.5f, 0);

        Node node = new Node("Health");
        node.attachChild(background);
        node.attachChild(indicator);
        node.attachChild(foreground);
        node.attachChild(label);
        return node;
    }

    private Node createQuickAccess(AssetManager assetManager, BitmapFont font, int numberOfSlots, int slotSize) {
        Node node = new Node("QuickAccess");

        for(int slotIndex = 0; slotIndex < numberOfSlots; slotIndex++) {
            Node slot = createQuickAccessSlot(assetManager, font, slotIndex, slotSize);
            node.attachChild(slot);
        }

        return node;
    }

    private Node createQuickAccessSlot(AssetManager assetManager, BitmapFont font, int slotIndex, int slotSize) {
        String name = "QuickAccess.Slot" + (slotIndex + 1);

        // Create quick access node background
        Geometry backgroundGeometry = new Geometry(name + ".Background.Geometry", new Quad(slotSize, slotSize));

        Material backgroundMaterial = TestHelper.createUnshadedMaterial(assetManager, ColorRGBA.White);
        backgroundMaterial.getAdditionalRenderState().setWireframe(true);
        backgroundGeometry.setMaterial(backgroundMaterial);

        Node background = new Node(name + ".Background");
        background.attachChild(backgroundGeometry);

        // Create quick access node foreground
        Geometry foregroundGeometry = new Geometry(name + ".Foreground.Geometry", new Quad(slotSize, slotSize));

        Material foregroundMaterial =  TestHelper.createUnshadedMaterial(assetManager, ColorRGBA.White);
        foregroundMaterial.getAdditionalRenderState().setWireframe(true);
        foregroundGeometry.setMaterial(foregroundMaterial);

        Node foreground = new Node(name + ".Foreground");
        foreground.attachChild(foregroundGeometry);

        BitmapText label = font.createLabel(String.valueOf(slotIndex + 1));
        label.setName(name + ".Label");
        label.setLocalTranslation(3, slotSize - 3, 0);

        // Create quick access node
        Node node = new Node(name);
        node.attachChild(background);
        node.attachChild(foreground);
        node.attachChild(label);
        node.setLocalTranslation(slotSize * slotIndex, 0, 0);
        return node;
    }

}
