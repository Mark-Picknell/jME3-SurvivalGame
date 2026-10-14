# Recovered archive classes

The original prototype remains under `archive/` as a reference. This migration places **28 Java classes** from `archive/game/src/main/java/com/picknell/survivalgame/` into the platform-independent `game` module, under `com.picknell.game.survivalgame`.

## Intent

- The existing `com.picknell.game.survivalgame.SurvivalGame` remains the shared Android/desktop entry point; the archived `app/SurvivalGame.java` is **not** a second live application.
- The recovered `app/TestGameplayAppState` (terrain, player, inputs, resources) and `app/TestDisplayAppState` (HUD experiment) are available but **not attached automatically**. This preserves the proven Android bootstrap while we adapt legacy keyboard input, physics and UI consciously.
- `TestDisplayAppState` now uses JME's bundled `Interface/Fonts/Default.fnt` instead of the absent `Interface/Fonts/Console.fnt`.
- The gameplay prototype's Roboto34 bitmap font and atlas were copied to shared `assets/Interface/Fonts/`, which both platform launchers already package.
- Most classes in `interaction`, `items`, `player`, `save`, `temp`, `ui` and `world` are the original **empty placeholders**, not completed subsystems. The actual logic currently resides mainly in the two test AppStates and utility classes.
- Other archived assets, UI resources, shaders and models have **not** been blindly restored. Reintroduce as necessary and confirm mobile rendering support.

## Starting a prototype deliberately

Once its platform-specific input and physics assumptions are reviewed, an explicit initializer can attach `new com.picknell.game.survivalgame.app.TestGameplayAppState()` to the live application. Do not enable it globally just to confirm the migration; compile and launch must remain separate milestones.

## Verification

Original archive source is unmodified. Run `./gradlew :game:compileJava :desktop:classes :app:assembleDebug` on an environment with the Gradle and Android toolchains. An untouched Android black-screen baseline is expected until a state is attached.
