package org.isatools.helpbrowser.ui;

import org.isatools.helpbrowser.common.UIHelper;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;


public class HistoryListCellRenderer extends JComponent implements ListCellRenderer {

     @InjectedResource
    private ImageIcon selectedIcon, unselectedIcon;

    public static final Color SELECTED_COLOR = UIHelper.LIGHT_GREEN_COLOR;
    public static final Color UNSELECTED_COLOR = Color.white;

    private DefaultListCellRenderer listCellRenderer;

    public HistoryListCellRenderer() {
        ResourceInjector.get("webbrowser-package.style").inject(this);

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(new SpecialListImage(), BorderLayout.WEST);

        listCellRenderer = new DefaultListCellRenderer();
        add(listCellRenderer, BorderLayout.CENTER);

        setBorder(new EmptyBorder(2, 2, 2, 2));
    }


    public Component getListCellRendererComponent(JList list, Object value, int index, boolean selected, boolean cellGotFocus) {
        listCellRenderer.getListCellRendererComponent(list, value, index,
                selected, cellGotFocus);
        listCellRenderer.setBorder(null);

        //image.checkIsIdEntered(selected);
        Component[] components = getComponents();

        for (Component c : components) {
            ((JComponent) c).setToolTipText(value.toString());
            if (c instanceof SpecialListImage) {
                ((SpecialListImage) c).setSelected(selected);
            } else {
                if (selected) {
                    c.setBackground(SELECTED_COLOR);
                    c.setFont(UIHelper.VER_10_BOLD);
                    c.setForeground(Color.white);
                } else {
                    c.setBackground(UNSELECTED_COLOR);
                    c.setFont(UIHelper.VER_10_BOLD);
                    c.setForeground(UIHelper.DARK_GREEN_COLOR);
                }
            }
        }

        return this;
    }

    class SpecialListImage extends JPanel {
        // this will contain the general panel layout for the list item and modifier elements to allow for changing of images
        // when rendering an item as being selected and so forth.
        private JLabel itemSelectedIndicator;

        SpecialListImage() {
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            itemSelectedIndicator = new JLabel(unselectedIcon);

            add(itemSelectedIndicator);
            add(Box.createHorizontalStrut(2));
        }

        public void setSelected(boolean selected) {
            if (selected) {
                itemSelectedIndicator.setIcon(selectedIcon);
            } else {
                itemSelectedIndicator.setIcon(unselectedIcon);
            }
        }
    }
}
