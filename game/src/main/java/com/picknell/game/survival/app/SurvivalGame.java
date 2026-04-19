package com.picknell.game.survival.app;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The JMonkeyEngine app entry, you should only do initializations for your app here, app logic is handled by
 * Custom states {@link com.jme3.app.state.BaseAppState}, Custom controls {@link com.jme3.scene.control.AbstractControl}
 * and your custom entities implementations of the previous.
 *
 * @author Mark E. Picknell
 */
public class SurvivalGame extends SimpleApplication implements ActionListener {

    private static final Logger LOGGER = Logger.getLogger(SurvivalGame.class.getName());

    private static final String INPUT_MAPPING_FORWARD = "forward";
    private static final String INPUT_MAPPING_BACKWARD = "backward";
    private static final String INPUT_MAPPING_LEFT = "left";
    private static final String INPUT_MAPPING_RIGHT = "right";

    private static final String INPUT_MAPPING_JUMP = "jump";
    private static final String INPUT_MAPPING_RUN = "run";
    
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

    public SurvivalGame() {
        
    }
    
    public SurvivalGame(AppState... initialStates) {
        super(initialStates);
    }
    
    @Override
    public void simpleInitApp() {
        // TODO: Remember to set logging level to SEVERE before release.
        flyCam.setEnabled(false);
        // flyCam.setMoveSpeed(50);

        initPhysics();
        initTerrain();
        initPlayer();
        initInputs();
    }

    @Override
    public void simpleUpdate(float tpf) {
        Camera camera = getCamera();
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

    }

    @Override
    public void simpleRender(RenderManager rm) {
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

    private void initPhysics() {
        LOGGER.log(Level.INFO, "Initalizing physics for test scene.");

        physicsState = new BulletAppState();
        physicsState.setBroadphaseType(PhysicsSpace.BroadphaseType.DBVT);
        physicsState.setThreadingType(BulletAppState.ThreadingType.PARALLEL);
        physicsState.setDebugEnabled(true);
        stateManager.attach(physicsState);

        physicsSpace = physicsState.getPhysicsSpace();
    }

    private void initTerrain() {
        int patchSize = 65;
        int heightMapSize = 513;
        float[] heightMap = new float[heightMapSize * heightMapSize];

        LOGGER.log(Level.INFO, "Initalizing terrain for test scene.");

        Material terrainMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //terrainMaterial.setBoolean("UseMaterialColors", true);
        terrainMaterial.setColor("Color", ColorRGBA.Green);
        terrainMaterial.getAdditionalRenderState().setWireframe(true);

        terrain = new TerrainQuad("Terrain", patchSize, heightMapSize, heightMap);
        terrain.setMaterial(terrainMaterial);
        rootNode.attachChild(terrain);

        RigidBodyControl terrainRigidBody = new RigidBodyControl(0);
        terrain.addControl(terrainRigidBody);
        physicsSpace.add(terrainRigidBody);

        TerrainLodControl terrainLod = new TerrainLodControl(terrain, getCamera());
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
        
        LOGGER.log(Level.INFO, "Initalizing player for test scene.");

        player = new Node("Player");
        rootNode.attachChild(player);

        playerControl = new BetterCharacterControl(radius, height, mass);
        player.addControl(playerControl);
        physicsSpace.add(playerControl);
        
        playerControl.warp(new Vector3f(x, y, z));

        chaseCamera = new ChaseCamera(getCamera(), player, inputManager);
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
        inputManager.addMapping(INPUT_MAPPING_FORWARD, new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping(INPUT_MAPPING_BACKWARD, new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping(INPUT_MAPPING_LEFT, new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping(INPUT_MAPPING_RIGHT, new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping(INPUT_MAPPING_JUMP, new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping(INPUT_MAPPING_RUN, new KeyTrigger(KeyInput.KEY_LSHIFT));

        inputManager.addListener(this, INPUT_MAPPING_FORWARD, INPUT_MAPPING_BACKWARD, INPUT_MAPPING_LEFT, INPUT_MAPPING_RIGHT, INPUT_MAPPING_JUMP, INPUT_MAPPING_RUN);
    }

}