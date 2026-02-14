/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cybercafe.ui;

import com.cybercafe.util.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class CustomerPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
   

    public CustomerPanel() {
        
        
        
        setLayout(new BorderLayout());
        model = new DefaultTableModel(new Object[]{"ID", "Name", "Phone", "Email"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        table.setOpaque(false);
        ((JComponent) table.getDefaultRenderer(Object.class)).setOpaque(false);
        
        table.setForeground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        table.setSelectionBackground(new Color(255, 255, 255, 80)); // white with transparency
        table.setSelectionForeground(Color.YELLOW); // text color on selection
        add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttons = new JPanel();
        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton delBtn = new JButton("Delete");
        buttons.add(addBtn); buttons.add(editBtn); buttons.add(delBtn);
        buttons.setOpaque(false);
        ((JComponent) table.getDefaultRenderer(Object.class)).setOpaque(false);
        
        table.setForeground(Color.WHITE);
        add(buttons, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> showCustomerDialog(null));
        editBtn.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r < 0) { JOptionPane.showMessageDialog(this, "Select a row"); return; }
            Integer id = (Integer) model.getValueAt(r, 0);
            showCustomerDialog(id);
        });
        delBtn.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r < 0) { JOptionPane.showMessageDialog(this, "Select a row"); return; }
            Integer id = (Integer) model.getValueAt(r, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Delete customer?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) deleteCustomer(id);
        });

        loadCustomers();
        
        
        
    }
    

    private void loadCustomers() {
        model.setRowCount(0);
        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT id, name, phone, email FROM customers")) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("email")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading customers: " + e.getMessage());
        }
    }

    private void showCustomerDialog(Integer id) {
        JTextField tfName = new JTextField(20);
        JTextField tfPhone = new JTextField(20);
        JTextField tfEmail = new JTextField(20);

        if (id != null) {
            try (Connection c = DBConnection.getConnection();
                 PreparedStatement ps = c.prepareStatement("SELECT name, phone, email FROM customers WHERE id = ?")) {
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    tfName.setText(rs.getString("name"));
                    tfPhone.setText(rs.getString("phone"));
                    tfEmail.setText(rs.getString("email"));
                }
            } catch (SQLException ex) { ex.printStackTrace(); }
        }

        JPanel p = new JPanel(new GridLayout(0, 1));
        p.add(new JLabel("Name:")); p.add(tfName);
        p.add(new JLabel("Phone:")); p.add(tfPhone);
        p.add(new JLabel("Email:")); p.add(tfEmail);

        int res = JOptionPane.showConfirmDialog(this, p, id == null ? "Add Customer" : "Edit Customer", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            if (id == null) addCustomer(tfName.getText(), tfPhone.getText(), tfEmail.getText());
            else updateCustomer(id, tfName.getText(), tfPhone.getText(), tfEmail.getText());
        }
    }

    private void addCustomer(String name, String phone, String email) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("INSERT INTO customers (name, phone, email) VALUES (?, ?, ?)")) {
            ps.setString(1, name);
            ps.setString(2, phone);
            ps.setString(3, email);
            ps.executeUpdate();
            loadCustomers();
        } catch (SQLException e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Error adding: " + e.getMessage()); }
    }

    private void updateCustomer(int id, String name, String phone, String email) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE customers SET name=?, phone=?, email=? WHERE id=?")) {
            ps.setString(1, name);
            ps.setString(2, phone);
            ps.setString(3, email);
            ps.setInt(4, id);
            ps.executeUpdate();
            loadCustomers();
        } catch (SQLException e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Error updating: " + e.getMessage()); }
    }

    private void deleteCustomer(int id) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM customers WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            loadCustomers();
        } catch (SQLException e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Error deleting: " + e.getMessage()); }
    }
    
}

