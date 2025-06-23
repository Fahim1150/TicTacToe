// Main.java
import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // Ensure DB Table exists
        DatabaseHelper.createUserTable();

        JFrame frame = new JFrame("Game Portal");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        loginButton.addActionListener(e -> {
            frame.dispose();
            new LoginPage();
        });

        registerButton.addActionListener(e -> {
            frame.dispose();
            new RegisterPage();
        });

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1, 10, 10));
        panel.add(loginButton);
        panel.add(registerButton);

        frame.add(panel);
        frame.setVisible(true);
    }
}
