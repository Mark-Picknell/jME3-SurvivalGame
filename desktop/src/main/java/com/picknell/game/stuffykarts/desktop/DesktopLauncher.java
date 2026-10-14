package com.picknell.game.stuffykarts.desktop;

import com.jme3.system.AppSettings;
import com.picknell.game.stuffykarts.StuffyKarts;

public final class DesktopLauncher {

    private DesktopLauncher() {
    }

    public static void main(String[] arguments) {
        StuffyKarts game = new StuffyKarts();

        AppSettings settings = new AppSettings(true);
        settings.setTitle("Stuffy Karts");
        settings.setResolution(1280, 720);

        game.setSettings(settings);
        game.start();
    }
}
