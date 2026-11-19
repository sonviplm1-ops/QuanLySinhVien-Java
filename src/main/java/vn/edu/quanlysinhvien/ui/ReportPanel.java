package vn.edu.quanlysinhvien.ui;

import vn.edu.quanlysinhvien.db.Database;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

/** Read-only searchable screen backed by a fixed, application-owned SQL query. */
public class ReportPanel extends JPanel {
    private final String baseQuery;
    private final String searchPlaceholder;
    private final JTextField search = new JTextField(26);
    private final DefaultTableModel model = new DefaultTableModel() { @Override public boolean isCellEditable(int r, int c) { return false; } };
    private final JTable table = new JTable(model);

    public ReportPanel(String baseQuery, String searchPlaceholder) {
        super(new BorderLayout(8, 8)); this.baseQuery = baseQuery; this.searchPlaceholder = searchPlaceholder;
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT)); top.add(new JLabel("Tìm kiếm:")); top.add(search);
        JButton find = new JButton("Tìm kiếm"), refresh = new JButton("Làm mới"); top.add(find); top.add(refresh); add(top, BorderLayout.NORTH);
        table.setAutoCreateRowSorter(true); add(new JScrollPane(table), BorderLayout.CENTER);
        find.addActionListener(e -> load()); refresh.addActionListener(e -> { search.setText(""); load(); }); search.addActionListener(e -> load());
        load();
    }

    private void load() {
        String sql = "SELECT * FROM (" + baseQuery.replaceFirst("(?i)\\s+ORDER BY\\s+.*$", "") + ") AS report_data";
        String value = search.getText().trim();
        if (!value.isEmpty()) {
            int columns = baseQuery.startsWith("SELECT l.") ? 3 : 3;
            StringBuilder filter = new StringBuilder(" WHERE ");
            // Report queries provide these searchable text aliases.
            filter.append(baseQuery.startsWith("SELECT l.") ? "CAST([Mã lớp] AS NVARCHAR(200)) LIKE ? OR CAST([Tên lớp] AS NVARCHAR(200)) LIKE ? OR CAST([Khoa] AS NVARCHAR(200)) LIKE ?"
                    : "CAST([Mã sinh viên] AS NVARCHAR(200)) LIKE ? OR CAST([Họ tên] AS NVARCHAR(200)) LIKE ? OR CAST([Môn học] AS NVARCHAR(200)) LIKE ?");
            sql += filter;
        }
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (!value.isEmpty()) { String pattern = "%" + value + "%"; ps.setString(1, pattern); ps.setString(2, pattern); ps.setString(3, pattern); }
            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData md = rs.getMetaData(); model.setRowCount(0); model.setColumnCount(0);
                for (int i = 1; i <= md.getColumnCount(); i++) model.addColumn(md.getColumnLabel(i));
                while (rs.next()) { Object[] row = new Object[md.getColumnCount()]; for (int i = 0; i < row.length; i++) row[i] = rs.getObject(i + 1); model.addRow(row); }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Không tải được dữ liệu. Kiểm tra kết nối hoặc view vw_BangDiem.\n" + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
