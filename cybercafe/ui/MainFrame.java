/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cybercafe.ui;

import javax.swing.*;
import java.awt.*;
public class MainFrame extends JFrame {
    private JTabbedPane tabs;
    
    public MainFrame() {
        setTitle("Cyber Cafe Management");
        setSize(900,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        
        tabs = new JTabbedPane();

        JPanel customerTab = new BackgroundPanel("/5.jpg");
        JPanel computerTab = new BackgroundPanel("/5.jpg");
        JPanel billingTab = new BackgroundPanel("/5.jpg");

        // ✅ Add real panels inside
        CustomerPanel customerPanel = new CustomerPanel();
        customerPanel.setOpaque(false);
        customerTab.setLayout(new BorderLayout());
        customerTab.add(customerPanel, BorderLayout.CENTER);

        ComputerPanel computerPanel = new ComputerPanel();
        computerPanel.setOpaque(false);
        
  
        computerTab.setLayout(new BorderLayout());
        computerTab.add(computerPanel, BorderLayout.CENTER);

        BillingPanel billingPanel = new BillingPanel(computerPanel);
        billingPanel.setOpaque(false);
        billingTab.setLayout(new BorderLayout());
        billingTab.add(billingPanel, BorderLayout.CENTER);
        

        // ✅ Add tabs
        
        tabs.addTab("Computers", computerTab);
        tabs.addTab("Billing", billingTab);
        tabs.addTab("Customers", customerTab);
        add(tabs);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}


