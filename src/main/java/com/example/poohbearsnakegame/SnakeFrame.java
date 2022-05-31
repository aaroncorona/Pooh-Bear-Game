package com.example.poohbearsnakegame;

import javax.swing.*;

public class SnakeFrame extends JFrame {

    // Panel
    SnakePanel panel = new SnakePanel(); // Add Panel object to the Frame

    // Constructor, creates a Frame object
    SnakeFrame() {

        // Set frame details
        this.setTitle("SNAKE");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.add(panel);
        this.pack(); // Automatically sizes the Frame
        this.setLayout(null);
        this.setLocationRelativeTo(null);

    }
}

