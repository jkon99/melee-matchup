import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

public class grid extends JFrame {

    private static final int SIZE = 26;

    private final JComboBox<String>[][] grid;

    private final String[] options = {
            "Fox",
            "Marth",
            "Puff",
            "Falco",
            "Sheik",
            "Falcon",
            "Peach",
            "Icies",
            "Yoshi",
            "Pikachu",
            "Luigi",
            "Samus",
            "DK",
            "Doc",
            "Ganon",
            "Link",
            "Yink",
            "Mario",
            "G&W",
            "Mewtwo",
            "Roy",
            "Zelda",
            "Ness",
            "Pichu",
            "Kirby",
            "Bowser",
    };

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
        ImageIcon appIcon = new ImageIcon("images/appIcon.png");
        setIconImage(appIcon.getImage());
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

                combo.setRenderer(new RatioRenderer());

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

                    updateComboColor(combo);

                    if (!updatingMirror) {
                        updateMirror(r, c);
                    }
                });

                updateComboColor(combo);

                panel.add(combo);

            }
        }

        JComboBox<String> optionSelector = new JComboBox<>(options);

        JButton exportButton = new JButton("Export Matchup");

        exportButton.addActionListener(e -> {
            int selectedOption = optionSelector.getSelectedIndex();
            showMatchupWindow(selectedOption);
        });

        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));

        sidePanel.add(new JLabel("Select Option:"));
        sidePanel.add(optionSelector);
        sidePanel.add(exportButton);

        add(sidePanel, BorderLayout.EAST);

        setLayout(new BorderLayout());

        add(panel, BorderLayout.CENTER);
        add(sidePanel, BorderLayout.EAST);

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

    private class RatioRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(
                JList<?> list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {

            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list,
                    value,
                    index,
                    isSelected,
                    cellHasFocus);

            if (value != null && !value.toString().equals(" ")) {

                String ratio = value.toString();

                String[] parts = ratio.split(":");

                int first = Integer.parseInt(parts[0]);

                // 10 -> red
                // 50 -> yellow
                // 90 -> green

                Color color = ratioColor(first);

                if (isSelected) {
                    label.setBackground(color.darker());
                    label.setForeground(Color.WHITE);
                } else {
                    label.setBackground(color);
                    label.setForeground(Color.BLACK);
                }

            } else {
                label.setBackground(Color.WHITE);
                label.setForeground(Color.BLACK);
            }

            return label;
        }
    }

    private Color ratioColor(int value) {

        // Clamp the value between 10 and 90
        value = Math.max(10, Math.min(90, value));

        float position = (value - 10) / 80.0f;

        // Red -> Yellow -> Green

        if (position < 0.5f) {

            // Red -> Yellow
            float t = position / 0.5f;

            int red = 255;

            int green = (int) (255 * t);

            return new Color(
                    red,
                    green,
                    0);

        } else {

            // Yellow -> Green
            float t = (position - 0.5f) / 0.5f;

            int red = (int) (255 * (1 - t));

            int green = 255;

            return new Color(
                    red,
                    green,
                    0);
        }
    }

    private void updateComboColor(JComboBox<String> combo) {

        String selected = (String) combo.getSelectedItem();

        if (selected == null || selected.equals(" ")) {
            combo.setBackground(Color.WHITE);
            combo.setForeground(Color.BLACK);
            return;
        }

        String[] parts = selected.split(":");
        int first = Integer.parseInt(parts[0]);

        Color color = ratioColor(first);

        combo.setBackground(color);
        combo.setForeground(Color.BLACK);
    }

    private void showMatchupWindow(int selectedOption) {

        JFrame matchupFrame = new JFrame(
                "Matchup Chart - " + options[selectedOption]);

        matchupFrame.setIconImage(
                new ImageIcon("images/appIcon.png").getImage());

        matchupFrame.setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE);

        matchupFrame.setSize(1200, 900);
        matchupFrame.setLocationRelativeTo(this);

        matchupFrame.setLayout(new BorderLayout());

        // ==========================================
        // MATCHUP PANEL
        // ==========================================

        JPanel matchupPanel = new JPanel();

        matchupPanel.setLayout(
                new BoxLayout(matchupPanel, BoxLayout.Y_AXIS));

        matchupPanel.setBackground(
                new Color(235, 235, 235));

        // ==========================================
        // EDITABLE TITLE
        // ==========================================

        JPanel titlePanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER));

        titlePanel.setBackground(
                new Color(235, 235, 235));

        JTextField titleField = new JTextField(
                options[selectedOption] + " Match Up Chart");

        titleField.setFont(
                new Font("Arial", Font.BOLD, 28));

        titleField.setHorizontalAlignment(
                SwingConstants.CENTER);

        titleField.setPreferredSize(
                new Dimension(500, 50));

        titlePanel.add(titleField);

        matchupPanel.add(titlePanel);

        // ==========================================
        // SELECTED OPTION IMAGE
        // ==========================================

        ImageIcon selectedIcon = new ImageIcon(
                "images/option" + (selectedOption + 1) + ".png");

        JLabel selectedLabel = new JLabel(selectedIcon);

        selectedLabel.setAlignmentX(
                Component.CENTER_ALIGNMENT);

        matchupPanel.add(selectedLabel);

        // ==========================================
        // BUILD THE TIERS
        // ==========================================

        for (int ratio = 90; ratio >= 10; ratio -= 5) {

            boolean ratioUsed = false;

            JPanel tier = new JPanel(
                    new FlowLayout(
                            FlowLayout.LEFT,
                            10,
                            10));

            tier.setBackground(Color.WHITE);

            // Border around entire tier
            tier.setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(
                                    Color.GRAY,
                                    2),
                            BorderFactory.createEmptyBorder(
                                    5,
                                    10,
                                    5,
                                    10)));

            // ==========================================
            // EDITABLE RATIO FIELD
            // ==========================================

            JTextField ratioField = new JTextField(
                    ratio + ":" + (100 - ratio));

            ratioField.setOpaque(true);

            ratioField.setHorizontalAlignment(
                    SwingConstants.CENTER);

            ratioField.setFont(
                    new Font("Arial", Font.BOLD, 14));

            ratioField.setPreferredSize(
                    new Dimension(80, 35));

            // Color based on ORIGINAL ratio
            Color color = ratioColor(ratio);

            ratioField.setBackground(color);
            ratioField.setForeground(Color.BLACK);

            ratioField.setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(
                                    Color.DARK_GRAY,
                                    1),
                            BorderFactory.createEmptyBorder(
                                    5,
                                    5,
                                    5,
                                    5)));

            tier.add(ratioField);

            // ==========================================
            // LOOK THROUGH EVERY OTHER OPTION
            // ==========================================

            for (int other = 0; other < SIZE; other++) {

                if (other == selectedOption) {
                    continue;
                }

                String value = (String) grid[selectedOption][other]
                        .getSelectedItem();

                // If this matchup has the current ratio
                if (value != null &&
                        value.equals(
                                ratio + ":" + (100 - ratio))) {

                    ratioUsed = true;

                    ImageIcon opponentIcon = new ImageIcon(
                            "images/option"
                                    + (other + 1)
                                    + ".png");

                    JLabel opponentLabel = new JLabel(opponentIcon);

                    opponentLabel.setBorder(
                            BorderFactory.createEmptyBorder(
                                    3,
                                    3,
                                    3,
                                    3));

                    tier.add(opponentLabel);
                }
            }

            // ==========================================
            // ONLY ADD USED TIERS
            // ==========================================

            if (ratioUsed) {
                matchupPanel.add(tier);
            }
        }

        // ==========================================
        // SCROLL PANE
        // ==========================================

        JScrollPane scrollPane = new JScrollPane(matchupPanel);

        matchupFrame.add(
                scrollPane,
                BorderLayout.CENTER);

        // ==========================================
        // SAVE BUTTON
        // ==========================================

        JPanel buttonPanel = new JPanel();

        JButton saveButton = new JButton("Save PNG");

        saveButton.addActionListener(e -> {

            // Remove focus from any text field
            // so the most recent edit is committed.
            matchupFrame.requestFocus();

            savePanelAsPNG(matchupPanel);

        });

        buttonPanel.add(saveButton);

        matchupFrame.add(
                buttonPanel,
                BorderLayout.SOUTH);

        // ==========================================
        // SHOW WINDOW
        // ==========================================

        matchupFrame.setVisible(true);
    }

    private void savePanelAsPNG(JPanel panel) {

        JFileChooser chooser = new JFileChooser();

        chooser.setDialogTitle("Save Matchup Chart");

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        // Make sure the panel is laid out at its
        // full preferred size.
        Dimension size = panel.getPreferredSize();

        panel.setSize(size);

        panel.doLayout();

        // Create the image
        BufferedImage image = new BufferedImage(
                size.width,
                size.height,
                BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = image.createGraphics();

        // White background
        g2.setColor(Color.WHITE);

        g2.fillRect(
                0,
                0,
                size.width,
                size.height);

        // Draw the panel
        panel.printAll(g2);

        g2.dispose();

        try {

            File file = chooser.getSelectedFile();

            // Automatically add .png
            // if the user didn't type it.
            String filePath = file.getAbsolutePath();

            if (!filePath.toLowerCase()
                    .endsWith(".png")) {

                file = new File(
                        filePath + ".png");
            }

            ImageIO.write(
                    image,
                    "png",
                    file);

            JOptionPane.showMessageDialog(
                    this,
                    "PNG saved successfully!");

        } catch (IOException ex) {

            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Could not save the PNG.");
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            grid ui = new grid();

            ui.setVisible(true);
        });
    }

}