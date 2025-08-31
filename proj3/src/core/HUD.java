package core;

import tileengine.TETile;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.Color;

public class HUD {
    private final TETile[][] world;
    private int coinsCollected;
    private int totalCoins;
    public HUD(TETile[][] world) {
        this.world = world;
        this.coinsCollected = 0;
        this.totalCoins = 4;
    }
    public void updateCoins(int coinsCollected, int totalCoins) {
        this.coinsCollected = coinsCollected;
        this.totalCoins = totalCoins;
    }
    public void draw() {
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new java.awt.Font("Monaco", java.awt.Font.PLAIN, 14));
        double mouseX = StdDraw.mouseX();
        double mouseY = StdDraw.mouseY();
        int tileX = (int) mouseX;
        int tileY = (int) mouseY;
        String description = "";
        if (tileX >= 0 && tileX < world.length && tileY >= 0 && tileY < world[0].length) {
            description = world[tileX][tileY].description();
        }
        StdDraw.setPenColor(new Color(0, 0, 0, 150));
        StdDraw.filledRectangle(world.length / 2.0, world[0].length - 0.5, world.length / 2.0, 0.5);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.textLeft(1, world[0].length - 0.5, "Tile: " + description);
        StdDraw.textLeft(1, world[0].length - 1.5, "Coins: " + coinsCollected + "/" + totalCoins);
    }
}