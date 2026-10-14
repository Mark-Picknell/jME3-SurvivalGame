package com.picknell.game.survivalgame.android;

import com.jme3.app.AndroidHarness;

/**
 * Thin Android launcher for the shared Survival Game application.
 */
public final class MainActivity extends AndroidHarness {

    public MainActivity() {
        appClass = "com.picknell.game.survivalgame.SurvivalGame";
        screenFullScreen = true;
        screenShowTitle = false;
        exitDialogTitle = "Exit Survival Game?";
        exitDialogMessage = "Quit the game?";
    }
}
