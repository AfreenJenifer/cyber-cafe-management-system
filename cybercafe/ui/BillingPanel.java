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
import java.time.Duration;
import java.time.LocalDateTime;

public class BillingPanel extends JPanel {
    private JComboBox<Item> cbCustomers;
    private JComboBox<Item> cbComputers;
    private DefaultTableModel sessionsModel;
    private JTable sessionsTable;
    private final ComputerPanel computerPanel;

    public BillingPanel(ComputerPanel computerPanel) {
        
        this.computerPanel=computerPanel;
        setLayout(new BorderLayout());
        JPanel top = new JPanel();
        
        
        cbCustomers = new JComboBox<>();
        cbComputers = new JComboBox<>();
        JButton startBtn = new JButton("Start Session");
        startBtn.setContentAreaFilled(false); // 🔹 remove gray bg
        startBtn.setOpaque(false);
        startBtn.setBorderPainted(true); // keep border visible
        startBtn.setForeground(Color.white);
        
        JButton endBtn = new JButton("End Selected Session");
        endBtn.setContentAreaFilled(false);
        endBtn.setOpaque(false);
        endBtn.setBorderPainted(true);
        endBtn.setForeground(Color.white);
        JLabel j=new JLabel("Customer:");
        j.setForeground(Color.WHITE);
        top.add(j); top.add(cbCustomers);
        JLabel jj=new JLabel("Computer:");
        jj.setForeground(Color.WHITE);
        top.add(jj); top.add(cbComputers);
        top.add(startBtn); top.add(endBtn);
        add(top, BorderLayout.NORTH);
        top.setOpaque(false);
        
        top.setForeground(Color.WHITE);
        sessionsModel = new DefaultTableModel(new Object[]{"SessionID","Customer","Computer","Start Time"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        sessionsTable = new JTable(sessionsModel);
        sessionsTable.setOpaque(false);
        ((JComponent) sessionsTable.getDefaultRenderer(Object.class)).setOpaque(false);
        

        
        sessionsTable.setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(sessionsTable);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        
        sessionsTable.setSelectionBackground(new Color(255, 255, 255, 80)); // white with transparency
        sessionsTable.setSelectionForeground(Color.YELLOW); // text color on selection




        add(scrollPane, BorderLayout.CENTER);
        

        startBtn.addActionListener(e -> startSession());
        endBtn.addActionListener(e -> endSession());

        loadCustomers();
        loadAvailableComputers();
        loadActiveSessions();
        sessionsTable.setOpaque(false);
        ((JComponent) sessionsTable.getParent()).setOpaque(false); // scrollpane viewport
   
    }

    private void loadCustomers() {
        cbCustomers.removeAllItems();
        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT id, name FROM customers")) {
            while (rs.next()) cbCustomers.addItem(new Item(rs.getInt("id"), rs.getString("name")));
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void loadAvailableComputers() {
        cbComputers.removeAllItems();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT id, name FROM computers WHERE status='available'")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) cbComputers.addItem(new Item(rs.getInt("id"), rs.getString("name")));
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void loadActiveSessions() {
        sessionsModel.setRowCount(0);
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT s.id, c.name AS customer_name, comp.name AS computer_name, s.start_time " +
                             "FROM sessions s " +
                             "LEFT JOIN customers c ON s.customer_id = c.id " +
                             "LEFT JOIN computers comp ON s.computer_id = comp.id " +
                             "WHERE s.end_time IS NULL")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                sessionsModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("customer_name"),
                        rs.getString("computer_name"),
                        rs.getTimestamp("start_time")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void startSession() {
        
        Item cust = (Item) cbCustomers.getSelectedItem();
        Item comp = (Item) cbComputers.getSelectedItem();
        if (cust == null || comp == null) { JOptionPane.showMessageDialog(this, "Select customer and computer"); return; }
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement ps = c.prepareStatement("INSERT INTO sessions (customer_id, computer_id, start_time) VALUES (?, ?,now())", Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, cust.id);
                ps.setInt(2, comp.id);
                ps.executeUpdate();
                // mark computer occupied
                try (PreparedStatement ps2 = c.prepareStatement("UPDATE computers SET status='occupied' WHERE id=?")) {
                    ps2.setInt(1, comp.id);
                    ps2.executeUpdate();
                }
                c.commit();
                JOptionPane.showMessageDialog(this, "Session started.");
                

            } catch (Exception ex) { c.rollback(); throw ex; }
        } catch (Exception ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        loadAvailableComputers();
        loadActiveSessions();
        computerPanel.refreshData();
    }

    private void endSession() {
     
        int r = sessionsTable.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Select active session to end"); return; }
        int sessionId = (Integer) sessionsModel.getValueAt(r, 0);
        try (Connection c = DBConnection.getConnection()) {
            // fetch start_time and computer hourly rate
            Timestamp startTs = null;
            int compId = -1;
            double hourlyRate = 0.0;
            try (PreparedStatement ps = c.prepareStatement("SELECT s.start_time, s.computer_id, comp.hourly_rate FROM sessions s JOIN computers comp ON s.computer_id = comp.id WHERE s.id = ?")) {
                ps.setInt(1, sessionId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    startTs = rs.getTimestamp("start_time");
                    compId = rs.getInt("computer_id");
                    hourlyRate = rs.getDouble("hourly_rate");
                } else {
                    JOptionPane.showMessageDialog(this, "Session not found.");
                    return;
                }
            }

            LocalDateTime start = startTs.toLocalDateTime();
            LocalDateTime end = LocalDateTime.now();
            long minutes = Duration.between(start, end).toMinutes();
            if (minutes <= 0) minutes = 1; // minimum charge unit
            double amount = (minutes / 60.0) * hourlyRate;

            c.setAutoCommit(false);
            try (PreparedStatement ps = c.prepareStatement("UPDATE sessions SET end_time = now(), minutes = ?, amount = ? WHERE id = ?")) {
                
                ps.setInt(1, (int) minutes);
                ps.setDouble(2, Math.round(amount)); // round 2 decimals
                ps.setInt(3, sessionId);
                ps.executeUpdate();
            }
            // set computer available
            try (PreparedStatement ps2 = c.prepareStatement("UPDATE computers SET status='available' WHERE id = ?")) {
                ps2.setInt(1, compId);
                ps2.executeUpdate();
            }
            c.commit();

            // show simple bill
            String bill = String.format("Session ended.\nMinutes: %d\nRate/hr: %.2f\nAmount: %.2f", minutes, hourlyRate, amount);
            JOptionPane.showMessageDialog(this, bill, "Bill", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error ending session: " + ex.getMessage());
        }
        loadAvailableComputers();
        loadActiveSessions();
        computerPanel.refreshData();
    }

    // small helper to hold id+label in JComboBox
    private static class Item {
        int id; String label;
        Item(int id, String label) { this.id = id; this.label = label; }
        public String toString() { return label; }
    }
}

