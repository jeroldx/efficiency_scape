package com.efficiencyScape;

import net.runelite.api.Client;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;


public class EfficiencyScapePanel extends PluginPanel
{
    private final JPanel overallPanel = new JPanel();

    //TESTING VARIABLES
    private String eventText = "TESTING\n";
    private JTextArea eventData = new JTextArea();
    private JButton clearButton = new JButton("Clear Text");
    //END TESTING

    EfficiencyScapePanel(EfficiencyScapePlugin efficiencyScapePlugin, EfficiencyScapeConfig efficiencyScapeConfig, Client client)
    {
        super();
        setBorder(new EmptyBorder(6, 6, 6, 6));
        setBackground(ColorScheme.DARK_GRAY_COLOR);
        setLayout(new BorderLayout());

        final JPanel layoutPanel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(layoutPanel, BoxLayout.Y_AXIS);
        layoutPanel.setLayout(boxLayout);
        add(layoutPanel, BorderLayout.NORTH);


        //TESTING
        clearButton.addActionListener(e -> clearText());
        layoutPanel.add(clearButton);
        eventData.setText(eventText);
        eventData.setLineWrap(true);
        eventData.setWrapStyleWord(true);
        layoutPanel.add(eventData);
        //END TESTING
    }

    void clearText(){
        eventText = "";
        eventData.setText(eventText);
    }

    // For Testing
    void addNewEvent(String newEvent)
    {
        eventText += newEvent;
        eventData.setText(eventText);
    }
}
