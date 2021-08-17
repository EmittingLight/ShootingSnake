package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class View {
    private JLabel label;
    private Controller controller;
    private JLabel scores;
    private JFrame frame;
    private JLabel pauseLabel;


    public void create(int width, int height) {
        frame = new JFrame();
        frame.setSize(width, height);
        frame.setUndecorated(true);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                controller.handleKeyPress(e.getKeyCode());
            }
        });

        pauseLabel = new JLabel("Pause");
        pauseLabel.setFont(new Font("Arial", Font.PLAIN, 30));
        pauseLabel.setBounds((width - 100) / 2, (height - 30) / 2, 100, 30);
        pauseLabel.setHorizontalAlignment(SwingConstants.CENTER);
        pauseLabel.setVerticalAlignment(SwingConstants.CENTER);
        pauseLabel.setForeground(Color.WHITE);
        pauseLabel.setVisible(false);
        frame.add(pauseLabel);


        scores = new JLabel("0");
        scores.setFont(new Font("Arial", Font.PLAIN, 15));
        scores.setBounds(10, 10, 100, 15);
        scores.setForeground(Color.WHITE);
        frame.add(scores);

        label = new JLabel();
        label.setBounds(0, 0, width, height);
        frame.add(label);

        frame.setVisible(true);

    }

    public void close() {
        frame.dispose();
    }

    public void setImage(BufferedImage image) {
        label.setIcon(new ImageIcon(image));
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setScores(int score) {
        scores.setText("Score: " + score);
    }

    public void setPauseVisible(boolean b) {
        pauseLabel.setVisible(b);
    }
}
