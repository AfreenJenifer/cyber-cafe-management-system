/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
     
package com.cybercafe.ui;

import javax.swing.*;
import java.awt.*;

public class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    public BackgroundPanel(String imagePath) {
        setOpaque(false);

        // Load image from resources
        backgroundImage = new ImageIcon(getClass().getResource(imagePath)).getImage();
        setLayout(new BorderLayout()); // So you can add components
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw scaled background
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}


