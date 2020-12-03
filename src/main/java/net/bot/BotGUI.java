package net.bot;

import javax.swing.*;
import java.awt.*;

public class BotGUI extends JFrame {
    public static boolean working = false;
    BotGUI() {
        setSize(300, 80);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(new BorderLayout());
        JButton button = new JButton("Start bot");
        JLabel label = new JLabel(" inactive ..");

        panel.add(button, BorderLayout.NORTH);
        panel.add(label, BorderLayout.SOUTH);

        button.addActionListener(e -> {
            if (!working) {
                button.setText("Stop bot");
                working = true;
            } else {
                System.exit(0);
            }

            Thread th = new Thread(() -> BotLoader.load(label, button));
            th.start();
        });

        add(panel);
    }

    void start() {
        setVisible(true);
    }
}
