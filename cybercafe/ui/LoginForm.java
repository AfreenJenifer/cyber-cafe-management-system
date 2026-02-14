/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cybercafe.ui;

import com.cybercafe.util.DBConnection;
import com.cybercafe.util.PasswordUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginForm extends JFrame {
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JButton btnLogin;

    public LoginForm() {
        setTitle("Cyber Cafe - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900,600);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel p = new BackgroundPanel("/download (2).jpg");
        p.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel lbuser=new JLabel("Username : ");
        lbuser.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 0; p.add(lbuser, gbc);
        gbc.gridx = 1; txtUser = new JTextField(16); p.add(txtUser, gbc);
        JLabel lbpass=new JLabel("Password : ");
        lbpass.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 1; p.add(lbpass, gbc);
        gbc.gridx = 1; txtPass = new JPasswordField(16); p.add(txtPass, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        btnLogin = new JButton("Login");
        p.add(btnLogin, gbc);

        btnLogin.addActionListener(e -> authenticate());

        getContentPane().add(p);
    }

    private void authenticate() {
        String user = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword());
        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter username and password");
            return;
        }
        String hashed = PasswordUtil.hash(pass);
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT id, role FROM users WHERE username = ? AND password = ?")) {
            ps.setString(1, user);
            ps.setString(2, hashed);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int uid = rs.getInt("id");
                String role = rs.getString("role");
                SwingUtilities.invokeLater(() -> {
                    dispose();
                    MainFrame main = new MainFrame();
                    main.setVisible(true);
                });
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}

