package vn.edu.quanlysinhvien.ui;

import vn.edu.quanlysinhvien.dao.StudentDao;
import vn.edu.quanlysinhvien.model.Student;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StudentPanel extends JPanel {
    private final StudentDao dao = new StudentDao();
    private final JTextField search = new JTextField(24);
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"Mã SV", "Họ tên", "Ngày sinh", "Giới tính", "Địa chỉ", "Điện thoại", "Email", "Mã lớp", "Trạng thái"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);

    public StudentPanel() {
        super(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Tìm mã/tên:")); top.add(search);
        JButton find = new JButton("Tìm kiếm"), refresh = new JButton("Làm mới");
        top.add(find); top.add(refresh); add(top, BorderLayout.NORTH);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton add = new JButton("Thêm sinh viên"), edit = new JButton("Sửa"), delete = new JButton("Xóa");
        actions.add(add); actions.add(edit); actions.add(delete); add(actions, BorderLayout.SOUTH);
        find.addActionListener(e -> load()); refresh.addActionListener(e -> { search.setText(""); load(); });
        search.addActionListener(e -> load());
        add.addActionListener(e -> editStudent(null)); edit.addActionListener(e -> editStudent(selectedStudent()));
        delete.addActionListener(e -> deleteStudent());
        load();
    }

    private void load() {
        try {
            model.setRowCount(0);
            for (Student s : dao.find(search.getText())) {
                model.addRow(new Object[]{s.id(), s.name(), s.birthDate() == null ? "" : s.birthDate().format(DateTimeFormatter.ISO_DATE),
                        s.gender(), s.address(), s.phone(), s.email(), s.classId(), s.status()});
            }
        } catch (SQLException ex) { showError(ex); }
    }

    private Student selectedStudent() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn sinh viên."); return null; }
        int row = table.convertRowIndexToModel(viewRow);
        String id = model.getValueAt(row, 0).toString();
        try { return dao.find(id).stream().filter(s -> s.id().equals(id)).findFirst().orElse(null); }
        catch (SQLException ex) { showError(ex); return null; }
    }

    private void editStudent(Student current) {
        if (current == null && table.getSelectedRow() >= 0) return;
        try {
            List<String> classes = dao.findClassIds();
            if (classes.isEmpty()) { JOptionPane.showMessageDialog(this, "Cần tạo lớp học trước khi thêm sinh viên."); return; }
            StudentFormDialog dialog = new StudentFormDialog(SwingUtilities.getWindowAncestor(this), current, classes);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                if (current == null) dao.insert(dialog.getStudent()); else dao.update(dialog.getStudent());
                load();
            }
        } catch (SQLException ex) { showError(ex); }
    }

    private void deleteStudent() {
        Student s = selectedStudent(); if (s == null) return;
        int answer = JOptionPane.showConfirmDialog(this, "Xóa sinh viên " + s.name() + " (" + s.id() + ")?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) return;
        try { dao.delete(s.id()); load(); }
        catch (SQLException ex) { showError(ex); }
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, "Không thể hoàn tất thao tác. Kiểm tra kết nối/cấu trúc SQL Server.\n" + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
