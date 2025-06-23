// RegisterPage.java
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class RegisterPage extends JFrame {
    public RegisterPage() {
        setTitle("Register");
        setSize(300, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton registerButton = new JButton("Register");

        registerButton.addActionListener(e -> {
            String name = nameField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields");
            } else {
                try (Connection conn = DatabaseHelper.connect()) {
                    String sql = "INSERT INTO users(name, email, password) VALUES (?, ?, ?)";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, name);
                    pstmt.setString(2, email);
                    pstmt.setString(3, password);
                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Registration successful!");
                    dispose();
                    new LoginPage();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Email already exists!");
                }
            }
        });

        setLayout(new GridLayout(4, 2, 5, 5));
        add(new JLabel("Name:"));
        add(nameField);
        add(new JLabel("Email:"));
        add(emailField);
        add(new JLabel("Password:"));
        add(passwordField);
        add(new JLabel(""));
        add(registerButton);

        setVisible(true);
    }
}
