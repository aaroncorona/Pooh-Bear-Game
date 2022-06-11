package com.example.poohbearsnakegame;

import javax.swing.*;

public class SnakeFrame extends JFrame {

    // Create Panel within Frame
    SnakePanel panel = new SnakePanel();

    // Constructor
    SnakeFrame() {

        // Set frame details
        this.setTitle("POOH BEAR");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.add(panel);
        this.pack(); // to automatically size the Frame to the Panel settings
        this.setLayout(null);
        this.setLocationRelativeTo(null);

    }
}

