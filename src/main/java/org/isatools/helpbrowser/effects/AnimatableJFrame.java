package org.isatools.helpbrowser.effects;

import org.isatools.helpbrowser.common.UIHelper;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


/**
 * AnimatableJFrame
 * Provides functionality to allow JDialogs to scroll in and out like those in the Safari browser
 * Majority of code minus a few small changes from Marinacci, J. & Adamson, C.
 * Swing Hacks, O'Reilly 2005.
 *
 * @author Marinacci, J, Adamson, C.
 */
public class AnimatableJFrame extends JFrame implements ActionListener, MouseListener {

    public static final int INCOMING = 1;
    public static final int OUTGOING = -1;
    public static final float ANIMATION_DURATION = 200f;
    public static final int ANIMATION_SLEEP = 25;
    private AnimatingSheet animatingSheet;

    private JComponent sheet;
    private JPanel glass;
    private Timer animationTimer;
    private boolean animating;
    private boolean sheetInView = false;
    private int animationDirection;
    private long animationStart;

    public AnimatableJFrame() {
        super();
        setupPane();
    }

    // used by the Timer

    public void actionPerformed(ActionEvent e) {
        if (animating) {
            // calculate height to show
            float animationPercent = (System.currentTimeMillis() -
                    animationStart) / ANIMATION_DURATION;
            animationPercent = Math.min(1.0f, animationPercent);

            int animatingHeight;

            if (animationDirection == INCOMING) {
                animatingHeight = (int) (animationPercent * sheet.getHeight());
            } else {
                animatingHeight = (int) ((1.0f - animationPercent) * sheet.getHeight());
            }

            // clip off that much from sheet and blit it
            // into animatingSheet
            animatingSheet.setAnimatingHeight(animatingHeight);
            animatingSheet.repaint();

            if (animationPercent >= 1.0f) {
                stopAnimation();

                if (animationDirection == INCOMING) {
                    finishShowingSheet();
                    sheetInView = true;
                } else {
                    glass.removeAll();
                    animatingSheet = new AnimatingSheet();
                    glass.setVisible(false);
                    sheetInView = false;
                }
            }
        }
    }

    private void finishShowingSheet() {
        glass.removeAll();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.anchor = GridBagConstraints.NORTH;
                glass.add(sheet, gbc);
                gbc.gridy = 1;
                gbc.weighty = Integer.MAX_VALUE;
                glass.add(Box.createGlue(), gbc);
                glass.revalidate();
                glass.repaint();
            }
        });

    }

    public void hideSheet() {
        if (sheetInView) {
            glass.removeMouseListener(this);
            glass.setOpaque(false);
            glass.setVisible(false);
            animationDirection = OUTGOING;
            startAnimation();
        }
    }

    public void instantHideSheet() {
        glass.removeMouseListener(this);
        glass.removeAll();
        glass.setVisible(false);
    }

    private void setupAnimation() {
        glass.removeAll();
        // set back to be a transparent white colour.
        glass.setBackground(new Color(255, 255, 255, 100));
        glass.setOpaque(true);
        glass.setVisible(true);
        animationDirection = INCOMING;
        startAnimation();
    }

    public void setupPane() {
        glass = (JPanel) getGlassPane();
//		glass.addMouseListener(this);
        glass.setLayout(new GridBagLayout());
        glass.setBackground(Color.white);
        animatingSheet = new AnimatingSheet();
        animatingSheet.setBorder(new LineBorder(UIHelper.LIGHT_GREEN_COLOR));
    }

    public void maskOutMouseEvents() {
        glass.addMouseListener(this);
    }

  /**
     * Used to show JDialog windows as sheets
     *
     * @param dialog - Dialog to be shown as a sheet
     * @return - The AnimatingSheet object to be used in the animation
     */
    public JComponent showJDialogAsSheet(JDialog dialog) {
        sheet = (JComponent) dialog.getContentPane();
        sheet.setBorder(new LineBorder(UIHelper.LIGHT_GREEN_COLOR, 2));
        glass.addMouseListener(this);
        setupAnimation();

        return sheet;
    }

    private void startAnimation() {
        glass.repaint();
        // clear glasspane and set up animatingSheet
        animatingSheet.setSource(sheet);
        animatingSheet.revalidate();
        glass.removeAll();
        glass.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH;
        glass.add(animatingSheet, gbc);
        gbc.gridy = 1;
        gbc.weighty = Integer.MAX_VALUE;
        glass.add(Box.createGlue(), gbc);
        glass.setVisible(true);

        // start animation timer
        animationStart = System.currentTimeMillis();

        if (animationTimer == null) {
            animationTimer = new Timer(ANIMATION_SLEEP, this);
        }

        animating = true;
        animationTimer.start();
    }

    private void stopAnimation() {
        animationTimer.stop();
        animating = false;
    }


    public void mouseClicked(MouseEvent mouseEvent) {

    }

    public void mouseEntered(MouseEvent mouseEvent) {

    }

    public void mouseExited(MouseEvent mouseEvent) {

    }

    public void mousePressed(MouseEvent mouseEvent) {

    }

    public void mouseReleased(MouseEvent mouseEvent) {

    }
}

