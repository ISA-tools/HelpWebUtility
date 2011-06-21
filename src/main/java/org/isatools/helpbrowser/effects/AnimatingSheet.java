package org.isatools.helpbrowser.effects;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;

/**
 * AnimatingSheet
 * The component to be slid in and out.
 * Majority of code minus a few small changes from Marinacci, J. & Adamson, C.
 * Swing Hacks, O'Reilly 2005.
 *
 * @author Marinacci, J, Adamson, C.
 */
public class AnimatingSheet extends JComponent {
    BufferedImage offscreenImage;
    Dimension animatingSize = new Dimension(0, 1);
    JComponent source;

    public AnimatingSheet() {
        super();
        setOpaque(true);
    }

    public Dimension getMaximumSize() {
        return animatingSize;
    }

    public Dimension getMinimumSize() {
        return animatingSize;
    }

    public Dimension getPreferredSize() {
        return animatingSize;
    }

    private void makeOffscreenImage(JComponent source) {
        GraphicsConfiguration gfxConfig = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration();
        source.revalidate();

        int width = source.getWidth();

        int height = source.getHeight();
        width = (width == 0) ? 1 : width;
        height = (height == 0) ? 1 : height;

        offscreenImage = gfxConfig.createCompatibleImage(width, height);

        Graphics2D offscreenGraphics = (Graphics2D) offscreenImage.getGraphics();

        offscreenGraphics.setColor(source.getBackground());
        offscreenGraphics.fillRect(0, 0, source.getWidth(), height);
        try {
            source.paint(offscreenGraphics);
        } catch (Exception e) {
            System.out.println("problem occurred when making off-screen graphic.");
        }
    }

    public void paint(Graphics g) {
        // get the bottom-most n pixels of source and
        // paint them into g, where n is height
        try {
            BufferedImage fragment = offscreenImage.getSubimage(0,
                    offscreenImage.getHeight() - animatingSize.height,
                    source.getWidth(), animatingSize.height);

            g.drawImage(fragment, 0, 0, this);
        } catch (RasterFormatException rfe) {
            // ignore, since this error is down to contents of pane changing due to addition of extra components etc.
        }
    }

    public void setAnimatingHeight(int height) {
        animatingSize.height = height;
        setSize(animatingSize);
    }

    public void setSource(JComponent source) {
        this.source = source;
        animatingSize.width = source.getWidth();

        makeOffscreenImage(source);
    }

    public void update(Graphics g) {
        paint(g);
    }
}
