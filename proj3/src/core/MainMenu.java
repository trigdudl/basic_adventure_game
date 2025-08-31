package core;

import tileengine.TERenderer;
import tileengine.TETile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

public class MainMenu extends JFrame implements ActionListener, KeyListener {
    JButton N;
    JButton L;
    JButton Q;

    public MainMenu() {
        setTitle("Our Game");
        setSize(1000,700);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel Title = new JLabel("Our Game", JLabel.CENTER);
        Title.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(Title);

        N = new JButton("(N) New Game");
        N.addActionListener(this);
        N.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(N);

        L = new JButton("(L) Load Game");
        L.addActionListener(this);
        L.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(L);

        Q = new JButton("(Q) Quit Game");
        Q.addActionListener(this);
        Q.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(Q);

        setVisible(true);
        this.addKeyListener(this);
        this.setFocusable(true);
        this.requestFocusInWindow();
        this.requestFocus();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == N) {
            this.setVisible(false);
            this.dispose();
            new SeedInputMenu();

//            TERenderer ter = new TERenderer();
//            ter.initialize(100, 70);
//
//            long seed = 8364032287222877186L;
//            Random rand = new Random(seed);
//
//            TETile[][] world = World.generateWorld(8364032287222877186L);
//
//            new Thread(() -> {
//                Avatar test1 = new Avatar(world, ter, rand);
//                test1.startGameLoop();
//            }).start();

        } else if (e.getSource() == L) {
            this.setVisible(false);
            this.dispose();
            SaveData data = SaveLoad.loadGame();

            if (data == null) {
                JOptionPane.showMessageDialog(this, "No Game to Load!");
            } else {
                TERenderer ter = new TERenderer();
                ter.initialize(100, 70);

                Random rand = new Random(data.seed);
                TETile[][] world = World.generateWorld(rand);
                new Thread(() -> {
                    Avatar test2 = new Avatar(world, ter, rand, data.avatarX, data.avatarY, data.coinsCollected, data.coinPositions, data.doorPosition, data.lineOfSightEnabled);
                    test2.startGameLoop();
                }).start();
            }
        } else if (e.getSource() == Q) {
            System.exit(0);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        char key = (e.getKeyChar());
        if (key == 'N' || key == 'n') {
            this.setVisible(false);
            this.dispose();
            new SeedInputMenu();
        } else if (key == 'L' || key == 'l') {
            L.doClick();
        } else if (key == 'Q' || key == 'q') {
            Q.doClick();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Not needed
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Not needed
    }

    public static void main(String[] args) {
        new MainMenu();
    }
}
