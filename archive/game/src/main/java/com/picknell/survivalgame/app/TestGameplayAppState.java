package com.picknell.survivalgame.app;

import com.jme3.app.Application;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator;
import com.picknell.survivalgame.util.TestHelper;

import java.util.logging.Level;
import java.util.logging.Logger;

public class TestGameplayAppState extends BaseAppState implements ActionListener {

    private static final Logger _LOGGER = Logger.getLogger(TestGameplayAppState.class.getName());

    private static final String INPUT_MAPPING_FORWARD = "forward";
    private static final String INPUT_MAPPING_BACKWARD = "backward";
    private static final String INPUT_MAPPING_LEFT = "left";
    private static final String INPUT_MAPPING_RIGHT = "right";

    private static final String INPUT_MAPPING_INTERACT = "interact";
    private static final String INPUT_MAPPING_JUMP = "jump";
    private static final String INPUT_MAPPING_RUN = "run";

    private Camera camera;
    private Node rootNode;
    private Node guiNode;

    private int stoneCount = 0;
    private int woodCount = 0;

    private BulletAppState physicsState;
    private PhysicsSpace physicsSpace;

    private TerrainQuad terrain;

    private Node player;
    private BetterCharacterControl playerControl;
    private ChaseCamera chaseCamera;

    private final Vector3f cameraDirection = new Vector3f();
    private final Vector3f cameraLeft = new Vector3f();
    private final Vector3f walkDirection = new Vector3f();
    private final Vector3f viewDirection = new Vector3f();

    private boolean forward, backward, left, right, run;
    private float walkingSpeed = 2.5f;
    private float runningSpeed = 12.5f;
    private float walkSpeed = walkingSpeed;

    private Node stone;

    private BitmapText crosshair;

    private BitmapText debugText;

    @Override
    protected void initialize(Application app) {
        FlyCamAppState flyCamAppState = getState(FlyCamAppState.class);
        if(flyCamAppState != null) {
            flyCamAppState.setEnabled(false);
        }

        SimpleApplication application = ((SimpleApplication)(getApplication()));
        camera = application.getCamera();
        rootNode = application.getRootNode();
        guiNode = application.getGuiNode();


        initPhysics();
        initTerrain();
        initPlayer();
        initInputs();
        initTestRock();
        initDisplay();
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

    @Override
    public void update(float tpf) {

        camera.getDirection(cameraDirection);
        camera.getLeft(cameraLeft);

        walkSpeed = run ? runningSpeed : walkingSpeed;

        walkDirection.set(Vector3f.ZERO);
        if (forward) {
            walkDirection.addLocal(cameraDirection);
        }
        if (backward) {
            walkDirection.subtractLocal(cameraDirection);
        }
        if (left) {
            walkDirection.addLocal(cameraLeft);
        }
        if (right) {
            walkDirection.subtractLocal(cameraLeft);
        }
        walkDirection.setY(0);
        if (walkDirection.lengthSquared() > 0f) {
            walkDirection.normalizeLocal();
            viewDirection.set(walkDirection);
            walkDirection.multLocal(walkSpeed);
        } else {
            walkDirection.set(Vector3f.ZERO);
        }
        playerControl.setWalkDirection(walkDirection);
        playerControl.setViewDirection(viewDirection);

        debugText.setText("Stone: " + stoneCount +
                "\nWood: " + woodCount +
                "\nPos: " + player.getWorldTranslation()
        );
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        switch (name) {
            case INPUT_MAPPING_FORWARD:
                forward = isPressed;
                break;
            case INPUT_MAPPING_BACKWARD:
                backward = isPressed;
                break;
            case INPUT_MAPPING_LEFT:
                left = isPressed;
                break;
            case INPUT_MAPPING_RIGHT:
                right = isPressed;
                break;
            case  INPUT_MAPPING_INTERACT:
                if(isPressed) {
                    interact();
                }
                break;
            case INPUT_MAPPING_JUMP:
                if (isPressed) {
                    playerControl.jump();
                }
                break;
            case INPUT_MAPPING_RUN:
                run = isPressed;
                break;
        }
    }


    private void interact() {
        CollisionResults results = new CollisionResults();

        //TODO: Make ray cast functional when playe mesh is implemented.

        Ray ray = new Ray(
                camera.getLocation(),
                camera.getDirection()
        );

        rootNode.collideWith(ray, results);

        if (results.size() == 0) {
            return;
        }

        Spatial hit = results.getClosestCollision().getGeometry();
        while (hit != null) {
            _LOGGER.log(Level.INFO, "Found hit at {0} {1}", new Object[] {hit.getName(), hit.getWorldTranslation()});

            if ("Stone".equals(hit.getName())) {

                Integer amount =
                        hit.getUserData("resourceAmount");

                if (amount != null && amount > 0) {

                    stoneCount++;

                    amount--;

                    hit.setUserData("resourceAmount", amount);

                    if (amount <= 0) {
                        hit.removeFromParent();
                    }
                }

                return;
            }

            hit = hit.getParent();
        }
    }

    private void initPhysics() {
        physicsState = getState(BulletAppState.class);
        if(physicsState == null) {
            physicsState = new BulletAppState();
            physicsState.setBroadphaseType(PhysicsSpace.BroadphaseType.DBVT);
            physicsState.setThreadingType(BulletAppState.ThreadingType.PARALLEL);
            physicsState.setDebugEnabled(true);
            getStateManager().attach(physicsState);
        }
        physicsSpace = physicsState.getPhysicsSpace();
    }

    private void initTerrain() {
        int patchSize = 65;
        int heightMapSize = 513;
        float[] heightMap = new float[heightMapSize * heightMapSize];

        Material terrainMaterial = TestHelper.createUnshadedMaterial(getApplication().getAssetManager(), ColorRGBA.Green);
        terrainMaterial.getAdditionalRenderState().setWireframe(true);

        terrain = new TerrainQuad("Terrain", patchSize, heightMapSize, heightMap);
        terrain.setMaterial(terrainMaterial);
        rootNode.attachChild(terrain);

        RigidBodyControl terrainRigidBody = new RigidBodyControl(0);
        terrain.addControl(terrainRigidBody);
        physicsSpace.add(terrainRigidBody);

        TerrainLodControl terrainLod = new TerrainLodControl(terrain, camera);
        DistanceLodCalculator terrainLodCalculator = new DistanceLodCalculator(patchSize, 2.7f);
        terrainLod.setLodCalculator(terrainLodCalculator);
        terrain.addControl(terrainLod);
    }

    private void initPlayer() {
        float radius = 0.2413f; // 19" wide
        float height = 1.8288f; // 6' tall
        float mass = 81.6f;     // 180lb

        float z = 0;
        float x = 0;
        float y = terrain.getHeight(new Vector2f(x, z));

        player = new Node("Player");
        rootNode.attachChild(player);

        playerControl = new BetterCharacterControl(radius, height, mass);
        player.addControl(playerControl);
        physicsSpace.add(playerControl);

        playerControl.warp(new Vector3f(x, y, z));

        chaseCamera = new ChaseCamera(camera, player, getApplication().getInputManager());
        chaseCamera.setInvertVerticalAxis(false);
        chaseCamera.setDefaultDistance(6f);
        chaseCamera.setMinDistance(2f);
        chaseCamera.setMaxDistance(12f);
        chaseCamera.setDefaultVerticalRotation(0.35f);
        chaseCamera.setLookAtOffset(new Vector3f(0, 1.5f, 0));
        chaseCamera.setRotationSpeed(3f);
        chaseCamera.setTrailingEnabled(false);
    }

    private void initInputs() {
        InputManager inputManager = getApplication().getInputManager();
        inputManager.addMapping(INPUT_MAPPING_FORWARD, new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping(INPUT_MAPPING_BACKWARD, new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping(INPUT_MAPPING_LEFT, new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping(INPUT_MAPPING_RIGHT, new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping(INPUT_MAPPING_INTERACT, new KeyTrigger(KeyInput.KEY_E));
        inputManager.addMapping(INPUT_MAPPING_JUMP, new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping(INPUT_MAPPING_RUN, new KeyTrigger(KeyInput.KEY_LSHIFT));

        inputManager.addListener(this, INPUT_MAPPING_FORWARD, INPUT_MAPPING_BACKWARD, INPUT_MAPPING_LEFT, INPUT_MAPPING_RIGHT, INPUT_MAPPING_INTERACT, INPUT_MAPPING_JUMP, INPUT_MAPPING_RUN);
    }

    private void initTestRock() {
        Box stoneMesh = new Box(1f, 1f, 1f);
        Geometry stoneGeometry = new Geometry("Stone.Geometry", stoneMesh);

        Material stoneMaterial = TestHelper.createUnshadedMaterial(getApplication().getAssetManager(), ColorRGBA.LightGray);
        stoneMaterial.getAdditionalRenderState().setWireframe(true);

        stoneGeometry.setMaterial(stoneMaterial);

        stone = new Node("Stone");
        stone.attachChild(stoneGeometry);

        stone.setLocalTranslation(8, terrain.getHeight(new Vector2f(8, 0)) + 1.0f, 0);

        stone.setUserData("resourceType", "Stone");
        stone.setUserData("resourceAmount", 10);

        rootNode.attachChild(stone);
    }

    private void initDisplay() {
        BitmapFont font = getApplication().getAssetManager().loadFont("Interface/Fonts/Roboto34.fnt");

        debugText = new BitmapText(font);
        debugText.setSize(18);
        debugText.setLocalTranslation(10, camera.getHeight() - 10, 0);
        guiNode.attachChild(debugText);

        crosshair = new BitmapText(font);
        crosshair.setSize(32);
        crosshair.setText("+");
        crosshair.setLocalTranslation(
                camera.getWidth() / 2f,
                camera.getHeight() / 2f,
                0
        );

        guiNode.attachChild(crosshair);
    }
}
