package com.picknell.game.survivalgame.desktop;

import com.jme3.system.AppSettings;
import com.picknell.game.survivalgame.SurvivalGame;

public final class DesktopLauncher {

    private DesktopLauncher() {
    }

    public static void main(String[] arguments) {
        SurvivalGame game = new SurvivalGame();

        AppSettings settings = new AppSettings(true);
        settings.setTitle("Survival Game");
        settings.setResolution(1280, 720);

        game.setSettings(settings);
        game.start();
    }
}
