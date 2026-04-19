package com.picknell.game.survival.desktopmodule;

import com.jme3.system.AppSettings;
import com.picknell.game.survival.game.SurvivalGame;

/**
 * Used to launch a jme application in desktop environment
 *
 * @author Mark E. Picknell
 */
public class DesktopLauncher {
    
    public static final String TITLE = "Untitled";

    public static final int DEFAULT_WIDTH = 960;    // PlayStation Vita screen width.
    public static final int DEFAULT_HEIGHT = 544;   // PlayStation Vita screen height.
    public static final int DEFAULT_FRAME_RATE = 60;
    public static final boolean DEFAULT_FULLSCREEN = false;
    
    public static void main(String[] args) {
        final SurvivalGame game = new SurvivalGame();

        AppSettings settings = new AppSettings(true);
        settings.setTitle(TITLE);
        settings.setWidth(DEFAULT_WIDTH);
        settings.setHeight(DEFAULT_HEIGHT);
        settings.setFrameRate(DEFAULT_FRAME_RATE);
        settings.setFullscreen(DEFAULT_FULLSCREEN);
        
        game.setSettings(settings);
        // TODO: create and add branding splash screen for jMonkeyEngine 3.
        game.setShowSettings(false);    // Settings Window not supported on Mac
        game.start();
    }
    
}
