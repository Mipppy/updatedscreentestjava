package mavenmain;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class MainGUI {
    static JFrame mainFrame = new JFrame();
    static SmartBot bot = null;
    static Bot dumbBots = null;
    static JButton killButton = null;

    static void InitGUI() {
        createSmartBotSubPanel();
        createMassNoAIBotSubPanel();
        InitFrame();
    }

    static void InitFrame() {
        mainFrame.setLayout(new GridLayout(0, 1, 10, 10));
        mainFrame.setSize(500, 600);
        mainFrame.setVisible(true);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setTitle("Online Game Botter");
    }

    
    static void createMassNoAIBotSubPanel() {
        JPanel subPanel = CreateJPanel("Mass No-AI Bots", 200, 150);
        JFormattedTextField amountField = new JFormattedTextField(50);
        JButton submit = new JButton("Submit");
        String[] dropdownOptions = {"Kahoot", "Blooket", "Gimkit"};
        JComboBox<String> dropdown = new JComboBox<>(dropdownOptions);
        JFormattedTextField gameField = new JFormattedTextField("489834");

        amountField.setPreferredSize(new Dimension(80, 30));
        gameField.setPreferredSize(new Dimension(80, 30));
        submit.setPreferredSize(new Dimension(100, 30));
        dropdown.setPreferredSize(new Dimension(100, 30));

        submit.addActionListener(e -> {
            int numberOfBots = Integer.parseInt(amountField.getText());
            String siteToBot = (String) dropdown.getSelectedItem();
            Bot.numberOfBots = numberOfBots;
            switch (siteToBot) {
                case "Kahoot":
                    Bot.kahootThread.start();
                    break;
                case "Blooket":
                    Bot.blooketThread.start();
                    break;
                case "Gimkit":
                    Bot.gimkitThread.start();
                    break;
            }
        });

        subPanel.add(amountField);
        subPanel.add(dropdown);
        subPanel.add(submit);
        subPanel.add(gameField);
        mainFrame.add(subPanel);
    }

    static void createSmartBotSubPanel() {
        JPanel subPanel = CreateJPanel("Smartbot Controls", 200, 150);
        JTextField textField = new JTextField("412311");
        textField.setPreferredSize(new Dimension(150, 30));

        JButton submitButton = new JButton("Submit");
        submitButton.setPreferredSize(new Dimension(100, 30));

        submitButton.addActionListener(e -> {
            String input = textField.getText();
            if (!(input.matches("[0-9]+") && input.length() > 4 && input.length() < 9)) {
                JOptionPane.showMessageDialog(mainFrame, "Invalid input, bozo.");
                return;
            }

            bot = new SmartBot(Integer.parseInt(input));
            bot.smartBotThread.start();
            if (killButton == null) {
                submitButton.setVisible(false);
                killButton = new JButton("Kill Bot");
                killButton.setPreferredSize(new Dimension(100, 30));
                killButton.setBackground(Color.RED);
                killButton.setForeground(Color.WHITE);
                killButton.addActionListener(killEvent -> {
                    if (bot != null) {
                        bot.stopFlag = true;
                        bot = null;
                    }
                    submitButton.setVisible(true);
                    subPanel.remove(killButton);
                    killButton = null;
                    subPanel.revalidate();
                    subPanel.repaint();
                });
                subPanel.add(killButton);
                subPanel.revalidate();
                subPanel.repaint();
            }
        });

        subPanel.add(textField);
        subPanel.add(submitButton);
        mainFrame.add(subPanel);
    }

    static JPanel CreateJPanel(String title, int w, int h) {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(w, h));
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.GRAY, 1), title));
        return panel;
    }
}
