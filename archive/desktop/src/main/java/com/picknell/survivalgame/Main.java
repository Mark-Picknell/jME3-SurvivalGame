package com.picknell.survivalgame;

import com.jme3.system.AppSettings;
import com.picknell.survivalgame.app.SurvivalGame;

/**
 * Used to launch a jme application in desktop environment
 *
 * @author Mark E. Picknell
 */
public class Main {
    
    public static final String TITLE = "JME3-SurvivalGame";
    public static final int DEFAULT_WIDTH = 960;
    public static final int DEFAULT_HEIGHT = 544;
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
        game.setShowSettings(false);
        game.start();
    }
    
}
