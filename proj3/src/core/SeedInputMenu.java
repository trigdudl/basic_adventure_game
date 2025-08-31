package core;

import tileengine.TERenderer;
import tileengine.TETile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

public class SeedInputMenu extends JFrame implements KeyListener {
    private final StringBuilder seedBuilder;
    private final JLabel seedLabel;

    public SeedInputMenu() {
        setTitle("Enter Seed");
        setSize(800, 600);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel prompt = new JLabel("Please enter a seed (digits only), then press S to start:", JLabel.CENTER);
        prompt.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(prompt);

        seedLabel = new JLabel("Seed: ", JLabel.CENTER);
        seedLabel.setFont(new Font("Monaco", Font.BOLD, 24));
        seedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(seedLabel);

        seedBuilder = new StringBuilder();

        setVisible(true);
        this.addKeyListener(this);
        this.setFocusable(true);
        this.requestFocus();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        char key = e.getKeyChar();
        if (Character.isDigit(key)) {
            seedBuilder.append(key);
            updateSeedDisplay();
        } else if (key == 's' || key == 'S') {
            if (seedBuilder.length() > 0) {
                try {
                    long seed = Long.parseLong(seedBuilder.toString());
                    startGame(seed);
                } catch (NumberFormatException ex) {
                    System.out.println("Seed too large! Defaulting to maximum allowed seed.");
                    startGame(Long.MAX_VALUE);
                }

            }
        }
    }

    private void updateSeedDisplay() {
        seedLabel.setText("Seed: " + seedBuilder.toString());
    }

    private void startGame(long seed) {
        this.setVisible(false);
        this.dispose();

        TERenderer ter = new TERenderer();
        ter.initialize(100, 70);
        Random rand = new Random(seed);
        TETile[][] world = World.generateWorld(rand);


        new Thread(() -> {
            Avatar avatar = new Avatar(world, ter, rand, seed);
            avatar.startGameLoop();
        }).start();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Not needed
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Not needed
    }
}