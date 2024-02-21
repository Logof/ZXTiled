package org.github.logof.zxtiled.prototype.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainWindow extends JFrame {
    private JPanel contentPane;
    private JPanel mainMenuPanel;
    private JPanel toolbarPanel;
    private JPanel additionalPanel;
    private JPanel graphicsPanel;
    private JScrollPane mapViewerPanel;
    private JPanel statusPanel;
    private JButton paintButton;
    private JButton eraseButton;
    private JButton fillButton;
    private JButton startPositionButton;
    private JButton finishPositionButton;
    private JButton addHotspotObjectButton;
    private JButton addMovingObjectButton;
    private JButton deleteObjectButton;
    private JButton moveObjectButton;
    private JButton zoomOutButton;
    private JButton zoomInButton;
    private JMenuBar applicationMenuBar;

    public MainWindow() {
        setContentPane(contentPane);
        setSize(Toolkit.getDefaultToolkit().getScreenSize().getSize().width,
                Toolkit.getDefaultToolkit().getScreenSize().getSize().height);

        initListeners();

    }

    private void initListeners() {
        paintButton.addActionListener(actionEvent -> System.out.println(actionEvent.getID()));
        eraseButton.addActionListener(actionEvent -> System.out.println(actionEvent.getID()));
        fillButton.addActionListener(actionEvent -> System.out.println(actionEvent.getID()));
        startPositionButton.addActionListener(actionEvent -> System.out.println(actionEvent.getID()));
        finishPositionButton.addActionListener(actionEvent -> System.out.println(actionEvent.getID()));
        addHotspotObjectButton.addActionListener(actionEvent -> System.out.println(actionEvent.getID()));
        addMovingObjectButton.addActionListener(actionEvent -> System.out.println(actionEvent.getID()));
        deleteObjectButton.addActionListener(actionEvent -> System.out.println(actionEvent.getID()));
        moveObjectButton.addActionListener(actionEvent -> System.out.println(actionEvent.getID()));
        zoomOutButton.addActionListener(actionEvent -> System.out.println(actionEvent.getID()));
        zoomInButton.addActionListener(actionEvent -> System.out.println(actionEvent.getID()));
    }

    private void createUIComponents() {
        applicationMenuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem openFile = new JMenuItem("Open");
        fileMenu.add(openFile);
        applicationMenuBar.add(fileMenu);
        mainMenuPanel.add(applicationMenuBar);
    }
}
