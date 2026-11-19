package vn.edu.quanlysinhvien.ui;

import vn.edu.quanlysinhvien.model.Student;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class StudentFormDialog extends JDialog {
    private final JTextField id = new JTextField(20), name = new JTextField(20), birth = new JTextField(20), address = new JTextField(20), phone = new JTextField(20), email = new JTextField(20);
    private final JComboBox<String> gender = new JComboBox<>(new String[]{"Nam", "Nữ"});
    private final JComboBox<String> classId;
    private final JComboBox<String> status = new JComboBox<>(new String[]{"Đang học", "Bảo lưu", "Đã tốt nghiệp", "Thôi học"});
    private boolean saved;

    public StudentFormDialog(Window owner, Student student, List<String> classes) {
        super(owner, student == null ? "Thêm sinh viên" : "Cập nhật sinh viên", ModalityType.APPLICATION_MODAL);
        classId = new JComboBox<>(classes.toArray(String[]::new));
        JPanel form = new JPanel(new GridBagLayout()); form.setBorder(BorderFactory.createEmptyBorder(14, 18, 8, 18));
        addRow(form, 0, "Mã sinh viên *", id); addRow(form, 1, "Họ tên *", name); addRow(form, 2, "Ngày sinh (YYYY-MM-DD)", birth);
        addRow(form, 3, "Giới tính", gender); addRow(form, 4, "Địa chỉ", address); addRow(form, 5, "Điện thoại", phone);
        addRow(form, 6, "Email", email); addRow(form, 7, "Lớp *", classId); addRow(form, 8, "Trạng thái", status);
        if (student != null) {
            id.setText(student.id()); id.setEditable(false); name.setText(student.name());
            birth.setText(student.birthDate() == null ? "" : student.birthDate().toString());
            if (student.gender() != null) gender.setSelectedItem(student.gender()); address.setText(nvl(student.address()));
            phone.setText(nvl(student.phone())); email.setText(nvl(student.email())); classId.setSelectedItem(student.classId()); status.setSelectedItem(student.status());
        }
        JButton save = new JButton("Lưu"), cancel = new JButton("Hủy");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT)); buttons.add(save); buttons.add(cancel);
        save.addActionListener(e -> validateAndSave()); cancel.addActionListener(e -> dispose());
        add(form, BorderLayout.CENTER); add(buttons, BorderLayout.SOUTH); pack(); setResizable(false); setLocationRelativeTo(owner);
    }

    private void addRow(JPanel panel, int row, String label, Component field) {
        GridBagConstraints c = new GridBagConstraints(); c.insets = new Insets(5, 5, 5, 5); c.gridy = row;
        c.gridx = 0; c.anchor = GridBagConstraints.LINE_END; panel.add(new JLabel(label), c);
        c.gridx = 1; c.anchor = GridBagConstraints.LINE_START; c.fill = GridBagConstraints.HORIZONTAL; c.weightx = 1; panel.add(field, c);
    }

    private void validateAndSave() {
        if (id.getText().isBlank() || name.getText().isBlank()) { warn("Mã sinh viên và họ tên không được để trống."); return; }
        try { if (!birth.getText().isBlank()) LocalDate.parse(birth.getText().trim()); }
        catch (DateTimeParseException ex) { warn("Ngày sinh phải theo định dạng YYYY-MM-DD."); return; }
        saved = true; dispose();
    }

    public Student getStudent() {
        String date = birth.getText().trim();
        return new Student(id.getText().trim(), name.getText().trim(), date.isEmpty() ? null : LocalDate.parse(date),
                (String) gender.getSelectedItem(), emptyToNull(address.getText()), emptyToNull(phone.getText()),
                emptyToNull(email.getText()), (String) classId.getSelectedItem(), (String) status.getSelectedItem());
    }
    public boolean isSaved() { return saved; }
    private void warn(String text) { JOptionPane.showMessageDialog(this, text, "Dữ liệu chưa hợp lệ", JOptionPane.WARNING_MESSAGE); }
    private static String nvl(String s) { return s == null ? "" : s; }
    private static String emptyToNull(String s) { return s.isBlank() ? null : s.trim(); }
}
