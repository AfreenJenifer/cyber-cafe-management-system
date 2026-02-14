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

public class ComputerPanel extends JPanel {
    public JTable table;
    private DefaultTableModel model;

    public ComputerPanel() {
        
        setLayout(new BorderLayout());
        model = new DefaultTableModel(new Object[]{"ID","Name","Status","Hourly Rate"}, 0) {
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

        JPanel btns = new JPanel();
        JButton add = new JButton("Add");
        JButton edit = new JButton("Edit");
        JButton del = new JButton("Delete");
        JButton markMaintenance = new JButton("Mark Maintenance");
     
        JButton refresh = new JButton("Refresh");
        btns.add(add); btns.add(edit); btns.add(markMaintenance); btns.add(del); btns.add(refresh);
        btns.setOpaque(false);
        add(btns, BorderLayout.SOUTH);
        
        add.addActionListener(e -> showComputerDialog(null));
        edit.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r < 0) { JOptionPane.showMessageDialog(this, "Select a row"); return; }
            Integer id = (Integer) model.getValueAt(r, 0);
            showComputerDialog(id);
        });
        markMaintenance.addActionListener(e -> changeStatus("maintenance"));
        del.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r < 0) { JOptionPane.showMessageDialog(this, "Select a row"); return; }
            Integer id = (Integer) model.getValueAt(r, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Delete computer?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) deleteCustomer(id);
        });
        
        refresh.addActionListener(e -> loadComputers());
        loadComputers();
        table.setOpaque(false);
((JComponent) table.getParent()).setOpaque(false); // scrollpane viewport
    }
    public void refreshData(){
        loadComputers();
    }

    public void loadComputers() {
        model.setRowCount(0);
        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT id, name, status, hourly_rate FROM computers")) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("status"),
                        rs.getBigDecimal("hourly_rate")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void showComputerDialog(Integer id) {
        JTextField tfName = new JTextField(15);
        JTextField tfRate = new JTextField(8);

        if (id != null) {
            try (Connection c = DBConnection.getConnection();
                 PreparedStatement ps = c.prepareStatement("SELECT name, hourly_rate FROM computers WHERE id=?")) {
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    tfName.setText(rs.getString("name"));
                    tfRate.setText(rs.getBigDecimal("hourly_rate").toPlainString());
                }
            } catch (SQLException ex) { ex.printStackTrace(); }
        }

        JPanel p = new JPanel(new GridLayout(0,1));
        p.add(new JLabel("Name:")); p.add(tfName);
        p.add(new JLabel("Hourly Rate:")); p.add(tfRate);

        int res = JOptionPane.showConfirmDialog(this, p, id==null?"Add Computer":"Edit Computer", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            if (id == null) addComputer(tfName.getText(), tfRate.getText());
            else updateComputer(id, tfName.getText(), tfRate.getText());
        }
    }

    private void addComputer(String name, String rateStr) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("INSERT INTO computers (name, hourly_rate) VALUES (?, ?)")) {
            ps.setString(1, name);
            ps.setBigDecimal(2, new java.math.BigDecimal(rateStr));
            ps.executeUpdate();
            loadComputers();
        } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Error adding: " + e.getMessage()); }
    }

    private void updateComputer(int id, String name, String rateStr) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE computers SET name=?, hourly_rate=? WHERE id=?")) {
            ps.setString(1, name);
            ps.setBigDecimal(2, new java.math.BigDecimal(rateStr));
            ps.setInt(3, id);
            ps.executeUpdate();
            loadComputers();
        } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Error updating: " + e.getMessage()); }
    }

    private void changeStatus(String newStatus) {
        int r = table.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Select a row"); return; }
        int id = (Integer) model.getValueAt(r, 0);
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE computers SET status=? WHERE id=?")) {
            ps.setString(1, newStatus);
            ps.setInt(2, id);
            ps.executeUpdate();
            loadComputers();
        } catch (SQLException e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }
    private void deleteCustomer(int id) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM computers WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            loadComputers();
        } catch (SQLException e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Error deleting: " + e.getMessage()); }
    }
}
