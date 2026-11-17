package kz.atu.lab12.repository;

import kz.atu.lab12.model.Student;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Все SQL-запросы к таблице students находятся только в этом классе. */
public class StudentRepository {

    private final String url = DbConfig.url();

    public void initializeDatabase() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS students (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    full_name TEXT NOT NULL,
                    group_name TEXT NOT NULL,
                    gpa REAL NOT NULL CHECK (gpa BETWEEN 0 AND 4)
                )
                """;
        try (Connection c = DriverManager.getConnection(url);
             Statement st = c.createStatement()) {
            st.execute(sql);
        }
    }

    public List<Student> findAll() throws SQLException {
        List<Student> result = new ArrayList<>();
        String sql = "SELECT id, full_name, group_name, gpa FROM students ORDER BY id";
        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new Student(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("group_name"),
                        rs.getDouble("gpa")));
            }
        }
        return result;
    }

    public void insert(String name, String group, double gpa) throws SQLException {
        String sql = "INSERT INTO students(full_name, group_name, gpa) VALUES (?, ?, ?)";
        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, group);
            ps.setDouble(3, gpa);
            ps.executeUpdate();
        }
    }

    public int update(int id, String name, String group, double gpa) throws SQLException {
        String sql = "UPDATE students SET full_name=?, group_name=?, gpa=? WHERE id=?";
        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, group);
            ps.setDouble(3, gpa);
            ps.setInt(4, id);
            return ps.executeUpdate();
        }
    }

    public int delete(int id) throws SQLException {
        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement("DELETE FROM students WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }
}
