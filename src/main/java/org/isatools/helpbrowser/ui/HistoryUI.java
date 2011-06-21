package org.isatools.helpbrowser.ui;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

public class HistoryUI extends JDialog {

    public static final String URL_SELECTED_EVENT = "URL_selected";
    public static final String CLOSE = "closeWindow";

    @InjectedResource
    private ImageIcon close, closeOver, goToURL, goToURLOver;

    private JLabel closeButton, goToURLButton;

    private DefaultListModel histListItems;

    private Map<String, String> history;

    public HistoryUI(Map<String, String> history) {
        ResourceInjector.get("webbrowser-package.style").inject(this);

        this.history = history;

        setLayout(new BorderLayout());

        setPreferredSize(new Dimension(400, 300));
    }

    public void createGUI() {
        histListItems = new DefaultListModel();

        updateHistoryTerms();
        final JList histList = new JList(histListItems);
        histList.setCellRenderer(new HistoryListCellRenderer());

        JScrollPane historyScrollPane = new JScrollPane(histList);
        historyScrollPane.setPreferredSize(new Dimension(395, 250));
        IAppWidgetFactory.makeIAppScrollPane(historyScrollPane);

        add(historyScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new BorderLayout());

        closeButton = new JLabel(close);
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                closeButton.setIcon(close);
                firePropertyChange(CLOSE, false, true);

            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                closeButton.setIcon(closeOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                closeButton.setIcon(close);
            }
        });

        goToURLButton = new JLabel(goToURL);
        goToURLButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                goToURLButton.setIcon(goToURL);

                if (!histList.isSelectionEmpty()) {
                    String toGo = history.get(histList.getSelectedValue().toString());
                    firePropertyChange(URL_SELECTED_EVENT, "none", toGo);
                }

            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                goToURLButton.setIcon(goToURLOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                goToURLButton.setIcon(goToURL);
            }
        });


        buttonPanel.add(closeButton, BorderLayout.WEST);
        buttonPanel.add(goToURLButton, BorderLayout.EAST);


        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void updateHistoryTerms() {
        histListItems.clear();
        for (String listItem : history.keySet()) {
            histListItems.addElement(listItem);
        }
    }
}
