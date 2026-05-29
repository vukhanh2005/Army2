package com.mygdx.game;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
public class DesktopLauncher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setForegroundFPS(40);
        config.setWindowIcon("res/icon.png");
        config.setTitle("Army2");
        config.setWindowedMode(1280, 720);
        config.setResizable(true);
        new Lwjgl3Application(createGame(), config);
    }

    private static ApplicationListener createGame() {
        try {
            return (ApplicationListener) Class.forName("com.teamobi.mobiarmy2.MainGame").getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Unable to start MainGame", e);
        }
    }
}
