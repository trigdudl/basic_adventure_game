package core;

import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class Avatar {
    private final TETile avatarTile;
    private final TETile doorTile;
    private int avatarX;
    private int avatarY;
    private final TETile[][] world;
    private final TERenderer ter;
    private final HUD hud;
    private final SaveLoad save;
    private List<int[]> coinPositions = new ArrayList<>();
    private int coinsCollected = 0;
    private int totalCoins = 4;
    private int[] doorPosition;
    private long seed;
    private final StringBuilder inputBuffer = new StringBuilder();
    private boolean lineOfSightEnabled = false;
    private final int sightRadius = 5;


    public Avatar(TETile[][] world, TERenderer ter, Random rand, long seed) {
        this.world = world;
        this.ter = ter;
        this.seed = seed;
        this.hud = new HUD(world);
        this.save = new SaveLoad();

        avatarTile = new TETile('@', Color.WHITE, Color.BLACK, "daquavius", 7);
        doorTile = new TETile('D', Color.YELLOW, Color.BLACK, "Door", 9);

        spawnCoins(rand);

        int[] playerPos = randomFloorPosition(rand, world);
        this.avatarX = playerPos[0];
        this.avatarY = playerPos[1];
        world[avatarX][avatarY] = avatarTile;

        int[] doorPos = randomWallPositionFarFromPlayer(rand, world, avatarX, avatarY, 10);
        world[doorPos[0]][doorPos[1]] = doorTile;
        this.doorPosition = doorPos;

        ter.renderFrame(world);
    }

    public Avatar(TETile[][] world, TERenderer ter, Random rand, int avatarX, int avatarY, int coinsCollected, List<int[]> coinPositions, int[] doorPosition, boolean lineOfSightEnabled) {
        this.world = world;
        this.ter = ter;
        this.hud = new HUD(world);
        this.save = new SaveLoad();
        this.lineOfSightEnabled = lineOfSightEnabled;

        avatarTile = new TETile('@', Color.WHITE, Color.BLACK, "daquavius", 7);
        doorTile = new TETile('D', Color.YELLOW, Color.BLACK, "Door", 9);

        this.avatarX = avatarX;
        this.avatarY = avatarY;
        this.coinsCollected = coinsCollected;
        hud.updateCoins(coinsCollected, totalCoins);

        world[avatarX][avatarY] = avatarTile;

        this.coinPositions = coinPositions;
        for (int[] pos : coinPositions) {
            world[pos[0]][pos[1]] = Tileset.COIN;
        }

        // (you'll still need to spawn door if you didn't save it)
        this.doorPosition = doorPosition;
        world[doorPosition[0]][doorPosition[1]] = doorTile;

        ter.renderFrame(world);
    }


    public void startGameLoop() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                handleInput(key);
            }
            if (lineOfSightEnabled) {
                TETile[][] masked = getVisibleTiles();
                ter.renderFrame(masked);
            } else {
                ter.renderFrame(world);
            }
            hud.draw();
            StdDraw.show();
            StdDraw.pause(20);
        }
    }

    public void handleInput(char c) {
        inputBuffer.append(c);

        if (inputBuffer.length() > 2) {
            inputBuffer.deleteCharAt(0);  // Keep buffer size to 2
        }

        // Handle save
        if (inputBuffer.toString().equals(":q") || inputBuffer.toString().equals(":Q")) {
            SaveLoad.saveGame(seed, avatarX, avatarY, coinsCollected, coinPositions, doorPosition, lineOfSightEnabled);
            System.exit(0);
        }

        // Movement logic (don't break old behavior)
        int dx = 0, dy = 0;
        if (c == 'w' || c == 'W') {
            dy = 1;
        } else if (c == 's' || c == 'S') {
            dy = -1;
        } else if (c == 'a' || c == 'A') {
            dx = -1;
        } else if (c == 'd' || c == 'D') {
            dx = 1;
        }

        if (c == 'l' || c == 'L') {
            lineOfSightEnabled = !lineOfSightEnabled;
        }

        moveAvatar(dx, dy);
        checkWinCondition();
    }

    private void checkWinCondition() {
        if (world[avatarX][avatarY] == doorTile) {
            showWinScreen();
        }
    }

    private void showWinScreen() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("Pacifico", Font.BOLD, 40));
        StdDraw.text(50, 35, "YOU WIN!");
        StdDraw.show();
        StdDraw.pause(3000);
        System.exit(0);
    }

    public static int[] randomFloorPosition(Random rand, TETile[][] world) {
        while (true) {
            int x = rand.nextInt(world.length);
            int y = rand.nextInt(world[0].length);
            if (world[x][y] == Tileset.FLOOR) {
                return new int[]{x, y};
            }
        }
    }

    public static int[] randomWallPositionFarFromPlayer(Random rand, TETile[][] world, int playerX, int playerY, int minDistance) {
        while (true) {
            int x = rand.nextInt(world.length);
            int y = rand.nextInt(world[0].length);
            if (world[x][y] == Tileset.WALL && hasFloorNearby(x, y, world)) {
                int dx = x - playerX;
                int dy = y - playerY;
                if (Math.sqrt(dx * dx + dy * dy) >= minDistance) {
                    return new int[]{x, y};
                }
            }
        }
    }

    private static boolean hasFloorNearby(int x, int y, TETile[][] world) {
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] i : directions) {
            int nx = x + i[0];
            int ny = y + i[1];
            if (nx >= 0 && nx < world.length && ny >= 0 && ny < world[0].length) {
                if (world[nx][ny] == Tileset.FLOOR) {
                    return true;
                }
            }
        }
        return false;
    }

    public void moveAvatar(int dx, int dy) {
        int newX = avatarX + dx;
        int newY = avatarY + dy;

        if (newX >= 0 && newX < world.length && newY >= 0 && newY < world[0].length) {
            TETile destinationTile = world[newX][newY];

            if (destinationTile.character() == 'C') {
                coinsCollected++;
                hud.updateCoins(coinsCollected, totalCoins);
                System.out.println("Collected a coin! (" + coinsCollected + "/" + totalCoins + ")");
                world[newX][newY] = Tileset.FLOOR;


                coinPositions.removeIf(pos -> pos[0] == newX && pos[1] == newY);
            }
            if (destinationTile == Tileset.FLOOR || destinationTile.character() == 'D') {
                if (destinationTile.character() == 'D') {
                    if (coinsCollected == totalCoins) {
                        showWinScreen();
                    } else {
                        System.out.println("Collect the coins first!");
                        return;
                    }
                }

                world[avatarX][avatarY] = Tileset.FLOOR;
                avatarX = newX;
                avatarY = newY;
                world[avatarX][avatarY] = avatarTile;
            }
        }
    }

    private void spawnCoins(Random rand) {
        for (int i = 0; i < totalCoins; i++) {
            int x, y;
            do {
                x = rand.nextInt(world.length);
                y = rand.nextInt(world[0].length);
            } while (!world[x][y].description().equals("floor"));

            world[x][y] = Tileset.COIN;
            coinPositions.add(new int[]{x, y});
        }
    }

    private TETile[][] getVisibleTiles() {
        int width = world.length;
        int height = world[0].length;
        TETile[][] visible = new TETile[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int dx = x - avatarX;
                int dy = y - avatarY;
                if (dx * dx + dy * dy <= sightRadius * sightRadius) {
                    visible[x][y] = world[x][y];
                } else {
                    visible[x][y] = Tileset.NOTHING;
                }
            }
        }
        return visible;
    }

//    private boolean validMove(int x, int y) {
//        String desc = world[x][y].description();
//        return desc.equals("floor") || desc.equals("door");
//    }
}
