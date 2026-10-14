package com.picknell.game.survivalgame.player;

import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

import java.util.HashSet;
import java.util.Set;

/** Desktop mouse/keyboard and Android gesture-driven exploration camera. */
public final class OrbitCameraController implements RawInputListener {
    private final Camera camera;
    private final Set<Integer> fingers = new HashSet<>();
    private final Vector3f eye = new Vector3f();
    private final Vector3f target = new Vector3f();
    private float x, z;
    private float yaw = 0.75f;
    private float pitch = 0.52f;
    private float distance = 36f;
    private float idle = 5f;
    private boolean mouseDrag;
    private boolean touchSeen;
    private boolean forward, backward, left, right;

    public OrbitCameraController(Camera camera) { this.camera = camera; }
    public float focusX() { return x; }
    public float focusZ() { return z; }

    public void update(float tpf, float groundY) {
        float step = Math.min(tpf, 0.1f) * 11f;
        if (forward) move(0f, -step);
        if (backward) move(0f, step);
        if (left) move(-step, 0f);
        if (right) move(step, 0f);
        if (idle > 4f) yaw += tpf * 0.045f;
        idle += tpf;
        target.set(x, groundY + 1.6f, z);
        float ring = distance * (float) Math.cos(pitch);
        eye.set(x + (float) Math.sin(yaw) * ring,
                target.y + distance * (float) Math.sin(pitch),
                z + (float) Math.cos(yaw) * ring);
        camera.setLocation(eye);
        camera.lookAt(target, Vector3f.UNIT_Y);
    }

    private void rotate(float dx, float dy) {
        yaw -= dx * 0.005f;
        pitch = clamp(pitch + dy * 0.004f, 0.20f, 1.20f);
        idle = 0f;
    }

    private void move(float dx, float dz) {
        x += (float) Math.cos(yaw) * dx + (float) Math.sin(yaw) * dz;
        z += -(float) Math.sin(yaw) * dx + (float) Math.cos(yaw) * dz;
        idle = 0f;
    }

    private void zoom(float multiplier) {
        distance = clamp(distance * multiplier, 9f, 80f);
        idle = 0f;
    }

    private static float clamp(float value, float minimum, float maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }

    @Override public void beginInput() { }
    @Override public void endInput() { }
    @Override public void onJoyAxisEvent(JoyAxisEvent evt) { }
    @Override public void onJoyButtonEvent(JoyButtonEvent evt) { }

    @Override
    public void onMouseMotionEvent(MouseMotionEvent evt) {
        if (touchSeen) return; // Android may synthesize mouse events for touch.
        if (mouseDrag) rotate(evt.getDX(), evt.getDY());
        if (evt.getDeltaWheel() != 0)
            zoom((float) Math.pow(0.92, evt.getDeltaWheel() / 120.0));
    }

    @Override
    public void onMouseButtonEvent(MouseButtonEvent evt) {
        if (!touchSeen && evt.getButtonIndex() == MouseInput.BUTTON_LEFT)
            mouseDrag = evt.isPressed();
    }

    @Override
    public void onKeyEvent(KeyInputEvent evt) {
        boolean pressed = evt.isPressed();
        switch (evt.getKeyCode()) {
            case KeyInput.KEY_W: forward = pressed; break;
            case KeyInput.KEY_S: backward = pressed; break;
            case KeyInput.KEY_A: left = pressed; break;
            case KeyInput.KEY_D: right = pressed; break;
            default: break;
        }
    }

    @Override
    public void onTouchEvent(TouchEvent evt) {
        touchSeen = true;
        if (evt.getType() == TouchEvent.Type.DOWN) {
            fingers.add(evt.getPointerId());
        } else if (evt.getType() == TouchEvent.Type.UP) {
            fingers.remove(evt.getPointerId());
        } else if (evt.getType() == TouchEvent.Type.MOVE) {
            if (fingers.size() >= 2) {
                float scale = distance * 2.5f / Math.max(camera.getWidth(), 1);
                move(-evt.getDeltaX() * scale, evt.getDeltaY() * scale);
            } else {
                rotate(evt.getDeltaX(), evt.getDeltaY());
            }
        } else if (evt.getType() == TouchEvent.Type.SCALE_MOVE) {
            float scale = evt.getScaleFactor();
            if (scale > 0.01f) zoom(1f / scale);
        }
    }
}
