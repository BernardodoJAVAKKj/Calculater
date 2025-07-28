package org.example;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.net.URL;
import java.util.Arrays;

public class Calculator {

    // Frame and GIF dimensions
    int boardWidth = 320;
    int boardHeight = 580;
    int gifWidth = 50;
    int gifHeight = 50;

    // Color palette for the UI
    Color Darkgreen  = new Color (2, 181, 107, 192);
    Color background = new Color(30, 30, 30);
    Color numberButtonColor = new Color(50, 50, 50);
    Color operatorButtonColor = new Color(255, 100, 20);
    Color topButtonColor = new Color(80, 80, 80);
    Color displayTextColor = new Color(240, 240, 240);
    Color displayBackgroundColor = new Color(40, 40, 40);


    // All calculator button labels
    String[] buttonValues = {
            "AC", "+/-", "%", "÷",
            "7", "8", "9", "x",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "0", ".", "√", "="
    };

    // Grouped symbols for easier logic handling
    String[] rightSymbols = {"÷", "x", "-", "+", "="};
    String[] topSymbols = {"AC", "+/-", "%"};

    // Main application window
    JFrame frame = new JFrame("Calculator");

    // UI components
    JLabel displayLabel = new JLabel();
    JLabel previewLabel = new JLabel();
    JPanel displayPanel = new JPanel();
    JPanel buttonsPanel = new JPanel();

    // Logic variables
    String A = "0";
    String operator = null;
    String B = null;

    // Constructor: Initializes UI and logic
    public Calculator() {
        URL iconURL = getClass().getResource("/pikachu.png");
        if (iconURL != null) {
            frame.setIconImage(new ImageIcon(iconURL).getImage());
        }

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        // Layered pane allows stacking elements (like GIF over UI)
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(boardWidth, boardHeight));
        layeredPane.setLayout(null);
        frame.setContentPane(layeredPane);
        frame.pack();
        frame.setLocationRelativeTo(null);

        // Preview label (for showing A + operator)
        previewLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        previewLabel.setForeground(Color.LIGHT_GRAY);
        previewLabel.setBackground(displayBackgroundColor);
        previewLabel.setOpaque(true);
        previewLabel.setHorizontalAlignment(JLabel.RIGHT);
        previewLabel.setText("");
        previewLabel.setBorder(new EmptyBorder(5, 20, 0, 20));

        // Main display label
        displayLabel.setFont(new Font("Arial", Font.BOLD, 48));
        displayLabel.setForeground(displayTextColor);
        displayLabel.setBackground(displayBackgroundColor);
        displayLabel.setOpaque(true);
        displayLabel.setHorizontalAlignment(JLabel.RIGHT);
        displayLabel.setText("0");
        displayLabel.setBorder(new EmptyBorder(0, 20, 20, 20));

        // Display panel at the top
        displayPanel.setLayout(new BorderLayout());
        displayPanel.setBackground(background);
        displayPanel.setBounds(0, 0, boardWidth, 100);
        displayPanel.add(previewLabel, BorderLayout.NORTH);
        displayPanel.add(displayLabel, BorderLayout.CENTER);
        layeredPane.add(displayPanel, JLayeredPane.DEFAULT_LAYER);

        // Panel with calculator buttons
        buttonsPanel.setLayout(new GridLayout(5, 4, 10, 10));
        buttonsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        buttonsPanel.setBackground(background);
        buttonsPanel.setBounds(0, 100, boardWidth, boardHeight - 100);
        layeredPane.add(buttonsPanel, JLayeredPane.DEFAULT_LAYER);

        // Create and add buttons
        for (String value : buttonValues) {
            JButton button = createButton(value);
            buttonsPanel.add(button);

            button.addActionListener(e -> {
                String value1 = ((JButton) e.getSource()).getText();

                // Handle operator buttons
                if (Arrays.asList(rightSymbols).contains(value1)) {
                    if (value1.equals("=")) {
                        if (A != null && operator != null) {
                            B = displayLabel.getText();
                            double numA = Double.parseDouble(A);
                            double numB = Double.parseDouble(B);
                            double result = 0;

                            switch (operator) {
                                case "+": result = numA + numB; break;
                                case "-": result = numA - numB; break;
                                case "x": result = numA * numB; break;
                                case "÷":
                                    if (numB != 0) {
                                        result = numA / numB;
                                    } else {
                                        displayLabel.setText("Erro");
                                        return;
                                    }
                                    break;
                            }

                            displayLabel.setText(removeZeroDecimal(result));
                            previewLabel.setText("");
                            clear();
                        }
                    } else {
                        if (operator == null) {
                            A = displayLabel.getText();
                            displayLabel.setText("0");
                            B = "0";
                            operator = value1;
                            previewLabel.setText(A + " " + operator);
                        }
                    }

                    // Handle top buttons
                } else if (Arrays.asList(topSymbols).contains(value1)) {
                    switch (value1) {
                        case "AC":
                            clear();
                            displayLabel.setText("0");
                            previewLabel.setText("");
                            break;
                        case "+/-":
                            double num = Double.parseDouble(displayLabel.getText());
                            num *= -1;
                            displayLabel.setText(removeZeroDecimal(num));
                            break;
                        case "%":
                            num = Double.parseDouble(displayLabel.getText());
                            num /= 100;
                            displayLabel.setText(removeZeroDecimal(num));
                            break;
                    }

                    // Handle square root
                } else if (value1.equals("√")) {
                    double num = Double.parseDouble(displayLabel.getText());
                    if (num >= 0) {
                        double raiz = Math.sqrt(num);
                        displayLabel.setText(removeZeroDecimal(raiz));
                    } else {
                        displayLabel.setText("Erro");
                    }

                    // Handle digits and decimal
                } else {
                    if (value1.equals(".")) {
                        if (!displayLabel.getText().contains(".")) {
                            displayLabel.setText(displayLabel.getText() + ".");
                        }
                    } else if ("0123456789".contains(value1)) {
                        if (displayLabel.getText().equals("0")) {
                            displayLabel.setText(value1);
                        } else {
                            displayLabel.setText(displayLabel.getText() + value1);
                        }
                    }
                }
            });
        }

        // Load and animate the GIF
        URL pokemonGifURL = getClass().getResource("/pokemon.gif");
        if (pokemonGifURL != null) {
            ImageIcon originalIcon = new ImageIcon(pokemonGifURL);
            Image resizedImage = originalIcon.getImage().getScaledInstance(gifWidth, gifHeight, Image.SCALE_DEFAULT);
            ImageIcon pokemonIcon = new ImageIcon(resizedImage);

            JLabel pokemonLabel = new JLabel(pokemonIcon);
            pokemonLabel.setSize(gifWidth, gifHeight);
            pokemonLabel.setLocation(-gifWidth, displayPanel.getHeight() / 2 - gifHeight / 2);
            layeredPane.add(pokemonLabel, JLayeredPane.PALETTE_LAYER);

            Timer timer = new Timer(30, new ActionListener() {
                int x = -gifWidth;
                int y = displayPanel.getHeight() / 2 - gifHeight / 2;

                @Override
                public void actionPerformed(ActionEvent e) {
                    x += 5;
                    if (x > boardWidth) {
                        x = -gifWidth;
                    }
                    pokemonLabel.setLocation(x, y);
                }
            });
            timer.start();
        }

        frame.setVisible(true);
    }

    // Creates a styled calculator button
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 22));
        button.setFocusPainted(false);
        button.setForeground(Color.WHITE);
        button.setBorder(new LineBorder(background, 1, true));
        button.setOpaque(true);

        if (Arrays.asList(topSymbols).contains(text)) {
            button.setBackground(topButtonColor);
        } else if (Arrays.asList(rightSymbols).contains(text) || text.equals("√")) {
            button.setBackground(operatorButtonColor);
        } else {
            button.setBackground(numberButtonColor);
        }

        return button;
    }

    // Clears stored values and operator
    void clear() {
        A = "0";
        operator = null;
        B = null;
    }

    // Removes trailing .0 if the number is whole
    String removeZeroDecimal(double num) {
        if (num % 1 == 0) {
            return Integer.toString((int) num);
        }
        return Double.toString(num);
    }

}
