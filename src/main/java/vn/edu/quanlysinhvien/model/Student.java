package vn.edu.quanlysinhvien.model;

import java.time.LocalDate;

public record Student(String id, String name, LocalDate birthDate, String gender, String address,
                      String phone, String email, String classId, String status) { }
