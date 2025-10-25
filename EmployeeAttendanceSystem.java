package test;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

// Employee Class
class Employee {
    private String id;
    private String name;
    private String department;
    private String position;

    public Employee(String id, String name, String department, String position) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.position = position;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public String getPosition() { return position; }
}

// Attendance Record Class
class AttendanceRecord {
    private String employeeId;
    private Date date;
    private String timeIn;
    private String timeOut;
    private String status;

    public AttendanceRecord(String employeeId, Date date, String timeIn, String timeOut, String status) {
        this.employeeId = employeeId;
        this.date = date;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.status = status;
    }

    public String getEmployeeId() { return employeeId; }
    public Date getDate() { return date; }
    public String getTimeIn() { return timeIn; }
    public String getTimeOut() { return timeOut; }
    public String getStatus() { return status; }
    public void setTimeOut(String timeOut) { this.timeOut = timeOut; }
}

// Main Application Class
public class EmployeeAttendanceSystem extends JFrame {
    private Map<String, Employee> employees;
    private List<AttendanceRecord> attendanceRecords;
    private JTabbedPane tabbedPane;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;

    public EmployeeAttendanceSystem() {
        employees = new HashMap<>();
        attendanceRecords = new ArrayList<>();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        timeFormat = new SimpleDateFormat("HH:mm:ss");

        // Setup UI
        setTitle("Employee Attendance Management System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Dashboard", createDashboardPanel());
        tabbedPane.addTab("Mark Attendance", createAttendancePanel());
        tabbedPane.addTab("View Records", createRecordsPanel());
        tabbedPane.addTab("Manage Employees", createEmployeePanel());

        add(tabbedPane);
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Attendance Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(title, BorderLayout.NORTH);

        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        
        statsPanel.add(createStatCard("Total Employees", String.valueOf(employees.size()), new Color(52, 152, 219)));
        statsPanel.add(createStatCard("Present Today", String.valueOf(getTodayPresentCount()), new Color(46, 204, 113)));
        statsPanel.add(createStatCard("Absent Today", String.valueOf(getTodayAbsentCount()), new Color(231, 76, 60)));
        statsPanel.add(createStatCard("Total Records", String.valueOf(attendanceRecords.size()), new Color(155, 89, 182)));

        panel.add(statsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatCard(String label, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createLineBorder(color.darker(), 2));

        JLabel lblTitle = new JLabel(label, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));

        JLabel lblValue = new JLabel(value, SwingConstants.CENTER);
        lblValue.setFont(new Font("Arial", Font.BOLD, 36));
        lblValue.setForeground(Color.WHITE);
        lblValue.setBorder(BorderFactory.createEmptyBorder(5, 10, 15, 10));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);

        return card;
    }

    private JPanel createAttendancePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Mark Attendance", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Employee ID:"), gbc);
        
        gbc.gridx = 1;
        JComboBox<String> empIdCombo = new JComboBox<>(employees.keySet().toArray(new String[0]));
        empIdCombo.setPreferredSize(new Dimension(200, 25));
        formPanel.add(empIdCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Employee Name:"), gbc);
        
        gbc.gridx = 1;
        JLabel empNameLabel = new JLabel();
        empNameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(empNameLabel, gbc);

        empIdCombo.addActionListener(e -> {
            String id = (String) empIdCombo.getSelectedItem();
            if (id != null && employees.containsKey(id)) {
                empNameLabel.setText(employees.get(id).getName());
            }
        });

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Status:"), gbc);
        
        gbc.gridx = 1;
        String[] statuses = {"Present", "Absent", "Late", "Half Day"};
        JComboBox<String> statusCombo = new JComboBox<>(statuses);
        formPanel.add(statusCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton checkInBtn = new JButton("Check In");
        checkInBtn.setBackground(new Color(46, 204, 113));
        checkInBtn.setForeground(Color.BLACK);
        checkInBtn.addActionListener(e -> {
            String empId = (String) empIdCombo.getSelectedItem();
            String status = (String) statusCombo.getSelectedItem();
            if (empId != null) {
                String currentTime = timeFormat.format(new Date());
                attendanceRecords.add(new AttendanceRecord(empId, new Date(), currentTime, "", status));
                JOptionPane.showMessageDialog(this, "Check-in recorded successfully!");
                refreshTabs();
            }
        });

        JButton checkOutBtn = new JButton("Check Out");
        checkOutBtn.setBackground(new Color(231, 76, 60));
        checkOutBtn.setForeground(Color.BLACK);
        checkOutBtn.addActionListener(e -> {
            String empId = (String) empIdCombo.getSelectedItem();
            if (empId != null) {
                AttendanceRecord todayRecord = getTodayRecord(empId);
                if (todayRecord != null && todayRecord.getTimeOut().isEmpty()) {
                    String currentTime = timeFormat.format(new Date());
                    todayRecord.setTimeOut(currentTime);
                    JOptionPane.showMessageDialog(this, "Check-out recorded successfully!");
                    refreshTabs();
                } else {
                    JOptionPane.showMessageDialog(this, "No check-in record found for today!");
                }
            }
        });

        buttonPanel.add(checkInBtn);
        buttonPanel.add(checkOutBtn);
        formPanel.add(buttonPanel, gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createRecordsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Attendance Records", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        String[] columns = {"Date", "Employee ID", "Name", "Time In", "Time Out", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        updateRecordsTable(model);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton removeBtn = new JButton("Remove");
        removeBtn.setBackground(new Color(231, 76, 60));
        removeBtn.setForeground(Color.BLACK);
        removeBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a record to remove!", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String date = (String) table.getValueAt(selectedRow, 0);
            String empId = (String) table.getValueAt(selectedRow, 1);
            String empName = (String) table.getValueAt(selectedRow, 2);
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove this attendance record?\n\n" +
                "Employee: " + empName + " (" + empId + ")\n" +
                "Date: " + date,
                "Confirm Removal",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                attendanceRecords.remove(selectedRow);
                updateRecordsTable(model);
                refreshTabs();
                JOptionPane.showMessageDialog(this, "Attendance record removed successfully!");
            }
        });
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> updateRecordsTable(model));
        
        bottomPanel.add(removeBtn);
        bottomPanel.add(refreshBtn);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createEmployeePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Employee Management", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        String[] columns = {"Employee ID", "Name", "Department", "Position"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        updateEmployeeTable(model);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton addBtn = new JButton("Add Employee");
        addBtn.setBackground(new Color(52, 152, 219));
        addBtn.setForeground(Color.BLACK);
        addBtn.addActionListener(e -> showAddEmployeeDialog(model));
        
        JButton removeBtn = new JButton("Remove");
        removeBtn.setBackground(new Color(231, 76, 60));
        removeBtn.setForeground(Color.BLACK);
        removeBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an employee to remove!", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String empId = (String) table.getValueAt(selectedRow, 0);
            String empName = (String) table.getValueAt(selectedRow, 1);
            String empDept = (String) table.getValueAt(selectedRow, 2);
            String empPos = (String) table.getValueAt(selectedRow, 3);
            
            // Count attendance records for this employee
            int recordCount = 0;
            for (AttendanceRecord record : attendanceRecords) {
                if (record.getEmployeeId().equals(empId)) {
                    recordCount++;
                }
            }
            
            String message = "Are you sure you want to remove this employee?\n\n" +
                "Employee ID: " + empId + "\n" +
                "Name: " + empName + "\n" +
                "Department: " + empDept + "\n" +
                "Position: " + empPos;
            
            if (recordCount > 0) {
                message += "\n\nWarning: This employee has " + recordCount + 
                    " attendance record(s).\nRemoving the employee will NOT delete their attendance records.";
            }
            
            int confirm = JOptionPane.showConfirmDialog(this,
                message,
                "Confirm Employee Removal",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                employees.remove(empId);
                updateEmployeeTable(model);
                refreshTabs();
                JOptionPane.showMessageDialog(this, "Employee removed successfully!");
            }
        });
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> updateEmployeeTable(model));

        buttonPanel.add(addBtn);
        buttonPanel.add(removeBtn);
        buttonPanel.add(refreshBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void showAddEmployeeDialog(DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Add Employee", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField idField = new JTextField(15);
        JTextField nameField = new JTextField(15);
        JTextField deptField = new JTextField(15);
        JTextField posField = new JTextField(15);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Employee ID:"), gbc);
        gbc.gridx = 1;
        panel.add(idField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1;
        panel.add(deptField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Position:"), gbc);
        gbc.gridx = 1;
        panel.add(posField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener(e -> {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String dept = deptField.getText().trim();
            String pos = posField.getText().trim();

            if (id.isEmpty() || name.isEmpty() || dept.isEmpty() || pos.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required!");
                return;
            }

            if (employees.containsKey(id)) {
                JOptionPane.showMessageDialog(dialog, "Employee ID already exists!");
                return;
            }

            employees.put(id, new Employee(id, name, dept, pos));
            updateEmployeeTable(model);
            refreshTabs();
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "Employee added successfully!");
        });
        panel.add(saveBtn, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void updateRecordsTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (AttendanceRecord record : attendanceRecords) {
            Employee emp = employees.get(record.getEmployeeId());
            String empName = emp != null ? emp.getName() : "Unknown";
            model.addRow(new Object[]{
                dateFormat.format(record.getDate()),
                record.getEmployeeId(),
                empName,
                record.getTimeIn(),
                record.getTimeOut().isEmpty() ? "Not checked out" : record.getTimeOut(),
                record.getStatus()
            });
        }
    }

    private void updateEmployeeTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (Employee emp : employees.values()) {
            model.addRow(new Object[]{
                emp.getId(),
                emp.getName(),
                emp.getDepartment(),
                emp.getPosition()
            });
        }
    }

    private int getTodayPresentCount() {
        String today = dateFormat.format(new Date());
        Set<String> presentEmployees = new HashSet<>();
        for (AttendanceRecord record : attendanceRecords) {
            if (dateFormat.format(record.getDate()).equals(today) && 
                !record.getStatus().equals("Absent")) {
                presentEmployees.add(record.getEmployeeId());
            }
        }
        return presentEmployees.size();
    }

    private int getTodayAbsentCount() {
        return employees.size() - getTodayPresentCount();
    }

    private AttendanceRecord getTodayRecord(String empId) {
        String today = dateFormat.format(new Date());
        for (AttendanceRecord record : attendanceRecords) {
            if (record.getEmployeeId().equals(empId) && 
                dateFormat.format(record.getDate()).equals(today)) {
                return record;
            }
        }
        return null;
    }

    private void refreshTabs() {
        tabbedPane.removeAll();
        tabbedPane.addTab("Dashboard", createDashboardPanel());
        tabbedPane.addTab("Mark Attendance", createAttendancePanel());
        tabbedPane.addTab("View Records", createRecordsPanel());
        tabbedPane.addTab("Manage Employees", createEmployeePanel());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new EmployeeAttendanceSystem().setVisible(true);
        });
    }
}