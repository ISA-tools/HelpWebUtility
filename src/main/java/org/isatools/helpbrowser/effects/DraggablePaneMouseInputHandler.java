package org.isatools.helpbrowser.effects;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;

public class DraggablePaneMouseInputHandler extends MouseInputAdapter {
    private boolean isMovingWindow;
    private int dragOffsetX;
    private int dragOffsetY;

    private static final int BORDER_DRAG_THICKNESS = 5;
    private Component container;

    public DraggablePaneMouseInputHandler(Component container) {
        this.container = container;
    }

    public void mousePressed(MouseEvent ev) {
        Point dragWindowOffset = ev.getPoint();
        Window w = (Window) ev.getSource();
        if (w != null) {
            w.toFront();
        }
        Point convertedDragWindowOffset = SwingUtilities.convertPoint(
                w, dragWindowOffset, container);

        Frame f = null;
        Dialog d = null;

        if (w instanceof Frame) {
            f = (Frame) w;
        } else if (w instanceof Dialog) {
            d = (Dialog) w;
        }

        int frameState = (f != null) ? f.getExtendedState() : 0;

        if (container.contains(convertedDragWindowOffset)) {
            if ((f != null && ((frameState & Frame.MAXIMIZED_BOTH) == 0)
                    || (d != null))
                    && dragWindowOffset.y >= BORDER_DRAG_THICKNESS
                    && dragWindowOffset.x >= BORDER_DRAG_THICKNESS
                    && dragWindowOffset.x < w.getWidth()
                    - BORDER_DRAG_THICKNESS) {
                isMovingWindow = true;
                dragOffsetX = dragWindowOffset.x;
                dragOffsetY = dragWindowOffset.y;
            }
        } else if (f != null && f.isResizable()
                && ((frameState & Frame.MAXIMIZED_BOTH) == 0)
                || (d != null && d.isResizable())) {
            dragOffsetX = dragWindowOffset.x;
            dragOffsetY = dragWindowOffset.y;
        }
    }

    public void mouseReleased(MouseEvent ev) {
        isMovingWindow = false;
    }

    public void mouseDragged(MouseEvent ev) {
        Window w = (Window) ev.getSource();

        if (isMovingWindow) {
            Point windowPt = MouseInfo.getPointerInfo().getLocation();
            windowPt.x = windowPt.x - dragOffsetX;
            windowPt.y = windowPt.y - dragOffsetY;
            w.setLocation(windowPt);
        }
    }

}