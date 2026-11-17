package kz.atu.lab12.repository;

import kz.atu.lab12.model.Employee;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Репозиторий индивидуального варианта №3 «Сотрудники». */
public class EmployeeRepository {

    private final String url = DbConfig.url();

    public void initializeDatabase() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS employees (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    full_name TEXT NOT NULL,
                    department TEXT NOT NULL,
                    experience INTEGER NOT NULL CHECK (experience BETWEEN 0 AND 60)
                )
                """;
        try (Connection c = DriverManager.getConnection(url);
             Statement st = c.createStatement()) {
            st.execute(sql);
        }
    }

    public List<Employee> findAll() throws SQLException {
        List<Employee> result = new ArrayList<>();
        String sql = "SELECT id, full_name, department, experience FROM employees ORDER BY id";
        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new Employee(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("department"),
                        rs.getInt("experience")));
            }
        }
        return result;
    }

    public void insert(String name, String department, int experience) throws SQLException {
        String sql = "INSERT INTO employees(full_name, department, experience) VALUES (?, ?, ?)";
        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, department);
            ps.setInt(3, experience);
            ps.executeUpdate();
        }
    }

    public int update(int id, String name, String department, int experience) throws SQLException {
        String sql = "UPDATE employees SET full_name=?, department=?, experience=? WHERE id=?";
        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, department);
            ps.setInt(3, experience);
            ps.setInt(4, id);
            return ps.executeUpdate();
        }
    }

    public int delete(int id) throws SQLException {
        try (Connection c = DriverManager.getConnection(url);
             PreparedStatement ps = c.prepareStatement("DELETE FROM employees WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }
}
