package org.isatools.helpbrowser.ui;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.isatools.helpbrowser.common.UIHelper;
import org.isatools.helpbrowser.effects.AnimatableJFrame;
import org.isatools.helpbrowser.effects.TopPane;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class WebBrowser extends AnimatableJFrame {
    @InjectedResource
    private ImageIcon backIcon, backIconOver, forwardIcon, forwardIconOver, homeIcon, homeIconOver, historyIcon, historyIconOver;

    static {
        UIManager.put("Panel.background", Color.white);
        UIManager.put("ToolTip.foreground", UIHelper.LIGHT_GREEN_COLOR);
        UIManager.put("ToolTip.background", Color.white);

        ResourceInjector.addModule("org.jdesktop.fuse.swing.SwingModule");

        ResourceInjector.get("webbrowser-package.style").load(
                WebBrowser.class.getResource("/dependency-injections/webbrowser-package.properties"));
    }

    JLabel back, forward, home, viewHistory;

    private JEditorPane browserPane;
    private String homePage;
    private JLabel status;

    private Stack<String> backWardStack = new Stack<String>();
    private Stack<String> forwardStack = new Stack<String>();
    private Map<String, String> history = new HashMap<String, String>();

    /**
     * NOTE: You need to call createGUI() to build the interface.
     *
     * @param homePage - starting page
     */
    public WebBrowser(String homePage) {
        ResourceInjector.get("webbrowser-package.style").inject(this);

        this.homePage = homePage;
    }


    public void createGUI() {
        status = new JLabel(" ");
        status.setFont(new Font("Verdana", Font.BOLD, 10));
        status.setForeground(UIHelper.DARK_GREEN_COLOR);
        try {
            browserPane = new JEditorPane(homePage);
            browserPane.setContentType("text/html");
            browserPane.setEditable(false);
            browserPane.addHyperlinkListener(new MyHyperlinkListener());
        } catch (IOException e) {
            System.err.println("Page not found");
        } catch (Exception e) {
            System.err.println("Page not found");
        }

        JPanel topPanel = new JPanel(new BorderLayout());


        TopPane titlePanel = new TopPane(true);
        topPanel.add(titlePanel, BorderLayout.NORTH);


        topPanel.add(new AddressPanel(), BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        titlePanel.installListeners();

        JScrollPane mainScrollingWindow = new JScrollPane(browserPane);
        IAppWidgetFactory.makeIAppScrollPane(mainScrollingWindow);

        add(mainScrollingWindow, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);

        ((JComponent) getContentPane()).setBorder(new LineBorder(UIHelper.LIGHT_GREEN_COLOR));

        setUndecorated(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
        setSize(800, 600);
        setVisible(true);
    }

    class MyHyperlinkListener implements HyperlinkListener {
        public void hyperlinkUpdate(HyperlinkEvent e) {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                browserPane = (JEditorPane) e.getSource();
                try {
                    attemptToPutItemInStack(backWardStack, browserPane.getPage().toString());
                    ifStackIsEmptyDisableButton(backWardStack, back);
                    addToHistory(browserPane.getPage().toString());
                    browserPane.setPage(e.getURL());
                } catch (IOException ioe) {
                    System.err.println("Page not found");
                } catch (Exception oe) {
                    System.err.println("Page not found");
                    oe.printStackTrace();
                }
            } else if (e.getEventType() == HyperlinkEvent.EventType.ENTERED) {
                status.setText(e.getURL().toString());
            } else {
                status.setText(" ");
            }
        }
    }


    private void addToHistory(String url) {
        if (browserPane.getDocument().getProperty("title") != null) {
            history.put(browserPane.getDocument().getProperty("title").toString(), url);
        }
    }

    class AddressPanel extends JPanel {
        HistoryUI historyPane;

        public AddressPanel() {
            setLayout(new BoxLayout(AddressPanel.this, BoxLayout.LINE_AXIS));

            constructPanel();
            add(back);
            add(forward);
            add(viewHistory);
            add(home);

            add(Box.createHorizontalGlue());
        }

        private void constructPanel() {

            back = new JLabel(backIcon);
            back.setEnabled(false);
            back.setToolTipText("<html>Go back a page</html>");

            forward = new JLabel(forwardIcon);
            forward.setEnabled(false);
            forward.setToolTipText("<html>Go forward a page</html>");

            home = new JLabel(homeIcon);
            home.setToolTipText("<html>Go home</html>");

            viewHistory = new JLabel(historyIcon);
            viewHistory.setToolTipText("<html>View your history</html>");

            addListeners();
        }

        private void addListeners() {

            back.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent mouseEvent) {
                    back.setIcon(backIcon);
                    goBack();
                }

                @Override
                public void mouseEntered(MouseEvent mouseEvent) {
                    back.setIcon(backIconOver);
                }

                @Override
                public void mouseExited(MouseEvent mouseEvent) {
                    back.setIcon(backIcon);
                }
            });

            forward.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent mouseEvent) {
                    forward.setIcon(forwardIcon);
                    goForward();
                }

                @Override
                public void mouseEntered(MouseEvent mouseEvent) {
                    forward.setIcon(forwardIconOver);
                }

                @Override
                public void mouseExited(MouseEvent mouseEvent) {
                    forward.setIcon(forwardIcon);
                }
            });

            home.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent mouseEvent) {
                    home.setIcon(homeIcon);
                    goHome();
                }

                @Override
                public void mouseEntered(MouseEvent mouseEvent) {
                    home.setIcon(homeIconOver);
                }

                @Override
                public void mouseExited(MouseEvent mouseEvent) {
                    home.setIcon(homeIcon);
                }
            });

            viewHistory.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent mouseEvent) {
                    viewHistory.setIcon(historyIcon);
                    openHistory();
                }

                @Override
                public void mouseEntered(MouseEvent mouseEvent) {
                    viewHistory.setIcon(historyIconOver);
                }

                @Override
                public void mouseExited(MouseEvent mouseEvent) {
                    viewHistory.setIcon(historyIcon);
                }
            });


        }


        private void goBack() {

            if (!backWardStack.empty()) {
                String back = backWardStack.pop();

                attemptToPutItemInStack(forwardStack, browserPane.getPage().toString());

                ifStackIsEmptyDisableButton(forwardStack, forward);
                try {
                    browserPane.setPage(back);
                    addToHistory(browserPane.getPage().toString());
                } catch (IOException e) {
                   System.err.println("Page not found");
                }
            }

            ifStackIsEmptyDisableButton(backWardStack, back);
        }

        private void goForward() {

            if (!forwardStack.empty()) {
                String forward = forwardStack.pop();

                attemptToPutItemInStack(backWardStack, browserPane.getPage().toString());

                ifStackIsEmptyDisableButton(backWardStack, back);

                try {
                    browserPane.setPage(forward);
                    addToHistory(browserPane.getPage().toString());
                } catch (IOException e) {
                   System.err.println("Page not found");
                }
                backWardStack.add(forward);
            }
            ifStackIsEmptyDisableButton(forwardStack, forward);
        }

        private void goHome() {
            attemptToPutItemInStack(backWardStack, browserPane.getPage().toString());
            try {
                browserPane.setPage(homePage);
            } catch (IOException e) {
                System.err.println("Page not found");
            }
        }

        private void openHistory() {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (historyPane == null) {
                        historyPane = new HistoryUI(history);

                        historyPane.addPropertyChangeListener(new PropertyChangeListener() {
                            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                                if (propertyChangeEvent.getPropertyName().equals(HistoryUI.URL_SELECTED_EVENT)) {

                                    try {
                                        browserPane.setPage(new URL(propertyChangeEvent.getNewValue().toString()));
                                    } catch (IOException e) {
                                        hideSheet();
                                    } finally {
                                        hideSheet();
                                    }


                                } else if (propertyChangeEvent.getPropertyName().equals(HistoryUI.CLOSE)) {
                                    hideSheet();
                                }
                            }
                        });

                        historyPane.createGUI();

                        showJDialogAsSheet(historyPane);
                    } else {
                        historyPane.updateHistoryTerms();
                        showJDialogAsSheet(historyPane);

                    }
                }
            });
        }

    }

    private void attemptToPutItemInStack(Stack<String> stack, String item) {
        if (!stack.contains(item)) {
            stack.add(item);
        }
    }

    private void ifStackIsEmptyDisableButton(Stack stack, JLabel button) {
        button.setEnabled(!stack.empty());
    }

    public static void main(String[] args) {
        new WebBrowser("http://www.nufc.com").createGUI();
    }

}
