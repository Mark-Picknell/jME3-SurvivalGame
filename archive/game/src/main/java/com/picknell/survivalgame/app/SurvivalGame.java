package com.picknell.survivalgame.app;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.renderer.RenderManager;

/**
 * The JMonkeyEngine app entry, you should only do initializations for your app here, app logic is handled by
 * Custom states {@link com.jme3.app.state.BaseAppState}, Custom controls {@link com.jme3.scene.control.AbstractControl}
 * and your custom entities implementations of the previous.
 *
 * @author Mark E. Picknell
 */
public class SurvivalGame extends SimpleApplication {

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
        //stateManager.attach(new TestGameplayAppState());
        stateManager.attach(new TestDisplayAppState());
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: Remove redundant method if no custom render code will be implemented here.
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: Remove redundant method if no custom render code will be implemented here.
    }

}