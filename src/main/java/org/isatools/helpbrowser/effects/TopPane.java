package org.isatools.helpbrowser.effects;

import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class TopPane extends JComponent {
    private JButton closeButton;

    @InjectedResource
    private Image backgroundGradient, grip, inactiveGrip, close, closeInactive, closeOver, closePressed;

    private int preferredHeight = 26;

    private boolean dispose;


    public TopPane(boolean dispose) {
        ResourceInjector.get("webbrowser-package.style").inject(this);


        this.dispose = dispose;
        setLayout(new GridBagLayout());

        createButtons();
        setBackground(Color.white);
    }

    public void installListeners() {
        MouseInputAdapter handler = new DraggablePaneMouseInputHandler(this);
        Window window = SwingUtilities.getWindowAncestor(this);
        window.addMouseListener(handler);
        window.addMouseMotionListener(handler);

        window.addWindowListener(new WindowHandler());
    }

    private void createButtons() {
        add(Box.createHorizontalGlue(),
                new GridBagConstraints(0, 0,
                        1, 1,
                        1.0, 1.0,
                        GridBagConstraints.EAST,
                        GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 0),
                        0, 0));

        add(closeButton = createButton(new CloseAction(),
                close, closePressed, closeOver),
                new GridBagConstraints(2, 0,
                        1, 1,
                        0.0, 1.0,
                        GridBagConstraints.NORTHEAST,
                        GridBagConstraints.NONE,
                        new Insets(1, 0, 0, 2),
                        0, 0));
    }

    private JButton createButton(final AbstractAction action,
                                 final Image image,
                                 final Image pressedImage,
                                 final Image overImage) {
        JButton button = new JButton(action);
        button.setIcon(new ImageIcon(image));
        button.setPressedIcon(new ImageIcon(pressedImage));
        button.setRolloverIcon(new ImageIcon(overImage));
        button.setRolloverEnabled(true);
        button.setBorder(null);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setFocusable(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(image.getWidth(null),
                image.getHeight(null)));
        return button;
    }

    private void close() {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (dispose) {
            w.dispatchEvent(new WindowEvent(w,
                    WindowEvent.WINDOW_CLOSING));
            w.dispose();
        } else {
            w.setVisible(false);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        size.height = preferredHeight;
        return size;
    }

    @Override
    public Dimension getMinimumSize() {
        Dimension size = super.getMinimumSize();
        size.height = preferredHeight;
        return size;
    }

    @Override
    public Dimension getMaximumSize() {
        Dimension size = super.getMaximumSize();
        size.height = preferredHeight;
        return size;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (!isVisible()) {
            return;
        }

        boolean active = SwingUtilities.getWindowAncestor(this).isActive();

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);

        Rectangle clip = g2.getClipBounds();

        g2.drawImage(backgroundGradient, clip.x, 0, clip.width, getHeight(), null);

        g2.drawImage(active ? grip : inactiveGrip, 0, 0, null);
    }

    private class CloseAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            close();
        }
    }


    private class WindowHandler extends WindowAdapter {
        @Override
        public void windowActivated(WindowEvent ev) {
            closeButton.setIcon(new ImageIcon(close));
            getRootPane().repaint();
        }

        @Override
        public void windowDeactivated(WindowEvent ev) {
            closeButton.setIcon(new ImageIcon(closeInactive));
            getRootPane().repaint();
        }
    }
}
