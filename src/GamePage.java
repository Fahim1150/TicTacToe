import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class GamePage extends JFrame {
    private JButton[][] buttons = new JButton[3][3];
    private boolean isXturn = true;
    private int userId;

    public GamePage(int userId, String gameData) {
        this.userId = userId;
        setTitle("Tic Tac Toe");
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel boardPanel = new JPanel(new GridLayout(3, 3));
        Font font = new Font("Arial", Font.BOLD, 60);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                JButton btn = new JButton("");
                btn.setFont(font);
                final int row = i, col = j;
                btn.addActionListener(e -> handleMove(btn, row, col));
                buttons[i][j] = btn;
                boardPanel.add(btn);
            }
        }

        // Load previous game if any
        if (gameData != null && !gameData.isEmpty()) {
            System.out.println("Loading saved game: " + gameData);
            loadGameData(gameData);
        }

        // Bottom buttons
        JButton resetButton = new JButton("Reset Game");
        resetButton.addActionListener(e -> resetBoard());

        JButton saveButton = new JButton("Save Game");
        saveButton.addActionListener(e -> saveGameData());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(resetButton);
        bottomPanel.add(saveButton);

        getContentPane().add(boardPanel, BorderLayout.CENTER);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void loadGameData(String data) {
        if (data == null || data.isEmpty()) {
            System.out.println("No saved game data. Starting fresh.");
            return;
        }

        String[] cells = data.split(",", -1);
        if (cells.length != 10) { // 9 cells + 1 for turn
            System.out.println("Invalid saved game data (found " + cells.length + " cells). Starting fresh.");
            return;
        }

        for (int i = 0; i < 9; i++) {
            int row = i / 3;
            int col = i % 3;
            buttons[row][col].setText(cells[i]);
        }

        isXturn = cells[9].equals("X");

        for (int i = 0; i < 9; i++) {
            int row = i / 3;
            int col = i % 3;
            buttons[row][col].setText(cells[i]);
        }
    }

    private void handleMove(JButton btn, int row, int col) {
        if (!btn.getText().equals("")) return;

        btn.setText(isXturn ? "X" : "O");
        if (checkWin()) {
            JOptionPane.showMessageDialog(this, (isXturn ? "X" : "O") + " wins!");
            disableBoard();
        } else if (isDraw()) {
            JOptionPane.showMessageDialog(this, "It's a draw!");
        }

        isXturn = !isXturn;
    }

    private boolean checkWin() {
        for (int i = 0; i < 3; i++) {
            if (!buttons[i][0].getText().isEmpty() &&
                    buttons[i][0].getText().equals(buttons[i][1].getText()) &&
                    buttons[i][0].getText().equals(buttons[i][2].getText())) return true;

            if (!buttons[0][i].getText().isEmpty() &&
                    buttons[0][i].getText().equals(buttons[1][i].getText()) &&
                    buttons[0][i].getText().equals(buttons[2][i].getText())) return true;
        }

        if (!buttons[0][0].getText().isEmpty() &&
                buttons[0][0].getText().equals(buttons[1][1].getText()) &&
                buttons[0][0].getText().equals(buttons[2][2].getText())) return true;

        if (!buttons[0][2].getText().isEmpty() &&
                buttons[0][2].getText().equals(buttons[1][1].getText()) &&
                buttons[0][2].getText().equals(buttons[2][0].getText())) return true;

        return false;
    }

    private boolean isDraw() {
        for (JButton[] row : buttons) {
            for (JButton btn : row) {
                if (btn.getText().isEmpty()) return false;
            }
        }
        return true;
    }

    private void disableBoard() {
        for (JButton[] row : buttons) {
            for (JButton btn : row) {
                btn.setEnabled(false);
            }
        }
    }

    private void resetBoard() {
        for (JButton[] row : buttons) {
            for (JButton btn : row) {
                btn.setText("");
                btn.setEnabled(true);
            }
        }
        isXturn = true;
    }

    private void saveGameData() {
        StringBuilder sb = new StringBuilder();
        for (JButton[] row : buttons) {
            for (JButton btn : row) {
                sb.append(btn.getText()).append(",");
            }
        }

        String data = sb.toString() + (isXturn ? "X" : "O");
// Remove last comma if exists
        if (data.endsWith(",")) {
            data = data.substring(0, data.length() - 1);
        }

        try (Connection conn = DatabaseHelper.connect()) {
            String sql = "UPDATE users SET game_data = ? WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, data);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Game Saved!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
