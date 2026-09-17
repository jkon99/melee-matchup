import javax.swing.*;
import java.awt.*;

public class grid extends JFrame {

    private static final int SIZE = 26;

    private final JComboBox<String>[][] grid;

    // Every possible orientation is available
    // in every dropdown.
    private final String[] ratios = {
            " ",
            "10:90",
            "15:85",
            "20:80",
            "25:75",
            "30:70",
            "35:65",
            "40:60",
            "45:55",
            "50:50",
            "55:45",
            "60:40",
            "65:35",
            "70:30",
            "75:25",
            "80:20",
            "85:15",
            "90:10"
    };

    // Prevents the automatic mirror update from
    // triggering another update back to the original.
    private boolean updatingMirror = false;

    @SuppressWarnings("unchecked")
    public grid() {

        setTitle("Matchup Chart");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        grid = new JComboBox[SIZE][SIZE];

        JPanel panel = new JPanel(
                new GridLayout(SIZE + 1, SIZE + 1, 2, 2));

        // Top-left corner
        panel.add(new JLabel(" "));

        // Column labels
        for (int col = 0; col < SIZE; col++) {

            ImageIcon icon = new ImageIcon("images/option" + (col + 1) + ".png");

            JLabel label = new JLabel(icon);
            label.setHorizontalAlignment(SwingConstants.CENTER);

            panel.add(label);

        }

        // Create rows and cells
        for (int row = 0; row < SIZE; row++) {

            // Row label
            ImageIcon icon = new ImageIcon("images/option" + (row + 1) + ".png");

            JLabel rowLabel = new JLabel(icon);
            rowLabel.setHorizontalAlignment(SwingConstants.CENTER);

            panel.add(rowLabel);

            // Create cells
            for (int col = 0; col < SIZE; col++) {

                JComboBox<String> combo = new JComboBox<>(ratios);

                grid[row][col] = combo;

                // Every diagonal cell starts at 50:50
                // but remains editable.
                if (row == col) {
                    combo.setSelectedItem("50:50");
                } else {
                    combo.setSelectedItem(" ");
                }

                final int r = row;
                final int c = col;

                combo.addActionListener(e -> {

                    if (!updatingMirror) {
                        updateMirror(r, c);
                    }
                });

                panel.add(combo);
            }
        }

        add(panel);
    }

    /**
     * Updates the cell across the diagonal.
     *
     * Example:
     *
     * (2,3) = 60:40
     * automatically changes
     * (3,2) = 40:60
     */
    private void updateMirror(int row, int col) {

        // A diagonal cell mirrors itself.
        if (row == col) {
            return;
        }

        String selected = (String) grid[row][col].getSelectedItem();

        if (selected == null) {
            return;
        }

        // Find the opposite ratio.
        String mirroredRatio = mirrorRatio(selected);

        int mirrorRow = col;
        int mirrorCol = row;

        // Prevent the mirrored change from
        // triggering this method again.
        updatingMirror = true;

        grid[mirrorRow][mirrorCol]
                .setSelectedItem(mirroredRatio);

        updatingMirror = false;
    }

    /**
     * Reverses the two sides of a ratio.
     *
     * 60:40 -> 40:60
     * 55:45 -> 45:55
     * 50:50 -> 50:50
     * 20:80 -> 80:20
     */
    private String mirrorRatio(String ratio) {

        String[] parts = ratio.split(":");

        int first = Integer.parseInt(parts[0]);
        int second = Integer.parseInt(parts[1]);

        return second + ":" + first;
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            grid ui = new grid();

            ui.setVisible(true);
        });
    }
}