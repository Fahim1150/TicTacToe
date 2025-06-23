// LoginPage.java
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class LoginPage extends JFrame {
    public LoginPage() {
        setTitle("Login");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");

        loginButton.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            try (Connection conn = DatabaseHelper.connect()) {
                String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, email);
                pstmt.setString(2, password);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Login successful!");

                    String gameData = rs.getString("game_data");
                    int userId = rs.getInt("id");

                    dispose();

                    if (gameData != null && !gameData.isEmpty()) {
                        int option = JOptionPane.showConfirmDialog(null, "You have an unfinished game. Do you want to resume?", "Resume Game", JOptionPane.YES_NO_OPTION);
                        if (option == JOptionPane.YES_OPTION) {
                            new GamePage(userId, gameData); // resume game
                        } else {
                            new GamePage(userId, null); // start new game
                        }
                    } else {
                        new GamePage(userId, null); // no saved game
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid email or password.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        setLayout(new GridLayout(3, 2, 5, 5));
        add(new JLabel("Email:"));
        add(emailField);
        add(new JLabel("Password:"));
        add(passwordField);
        add(new JLabel(""));
        add(loginButton);

        setVisible(true);
    }
}
