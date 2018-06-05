package fr.swing.adapter;

import fr.battledroid.core.asset.Canvas;
import fr.battledroid.core.asset.Color;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public final class CanvasAdapter implements Canvas {
    private final JComponent component;
    private Graphics g;

    public CanvasAdapter(JComponent component) {
        this.component = Objects.requireNonNull(component);
    }

    public CanvasAdapter init(Graphics g) {
        this.g = g;
        return this;
    }

    @Override
    public int getWidth() {
        return component.getWidth();
    }

    @Override
    public int getHeight() {
        return component.getHeight();
    }

    @Override
    public void drawImage(Object img, float x, float y) {
        g.drawImage((Image) img, (int) x, (int) y, null);
    }

    @Override
    public void drawRect(float x, float y, float width, float height, Color color) {
        g.drawRect((int) x, (int) y, (int) width, (int) height);
    }

    @Override
    public void drawCircle(float x, float y, float radius, Color color) {
    }

    @Override
    public void drawColor(Color color) {
        g.setColor((java.awt.Color) color.get());
        g.fillRect(0, 0, component.getWidth(), component.getHeight());
    }
}
