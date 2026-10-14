package com.picknell.game.survivalgame;

import com.jme3.app.SimpleApplication;

/**
 * Shared JME3 Survival Game game application.
 *
 * Platform-specific launchers live in the desktop and Android modules.
 */
public final class SurvivalGame extends SimpleApplication {

    @Override
    public void simpleInitApp() {
        flyCam.setEnabled(false);
    }
}
