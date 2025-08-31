package core;
import java.util.*;

import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;
import utils.RandomUtils;

import java.util.Random;

public class World {
    static int WIDTH = 100;
    static int HEIGHT = 70;
    static TETile[][] world;
    // build your own world!
    public static TETile[][] generateWorld(Random rand) {
        //Random rand = new Random(seed);
        world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        List<int[]> centers = makeRoom(world, rand);
        new World().makeHallway(centers, world, rand);

        return world;
    }

    public static List<int[]> makeRoom(TETile[][] world, Random rand){
        //iterate through world and set the tiles = to walls ie world[x][y] = Tileset.WALL;
        //params
        int maxH = 16;
        int maxW = 20;
        int numRooms = RandomUtils.uniform(rand, 20, 25);
        int[] xPositions = new int[numRooms];
        int[] yPositions = new int[numRooms];

        List<int[]> centers = new ArrayList<>();

        for (int i = 0; i < numRooms; i++){
            xPositions[i] = RandomUtils.uniform(rand, 0,WIDTH - maxW);
            yPositions[i] = RandomUtils.uniform(rand, 0,HEIGHT - maxH);
        }

        for (int i = 0; i < numRooms; i++) {
            int attempts = 0;
            while (attempts < 20) {
                int randh = RandomUtils.uniform(rand,  5, maxH);
                int randw = RandomUtils.uniform(rand,  5, maxW);
                int x = RandomUtils.uniform(rand, 0, WIDTH - randw);
                int y = RandomUtils.uniform(rand, 0 , HEIGHT - randh);

                if (new World().isAreaEmpty(x, y, randw, randh, world)) {
                    int centerX = x + randw / 2;
                    int centerY = y + randh / 2;
                    centers.add(new int[]{centerX, centerY});
                    for (int j = x; j < x + randw; j++){
                        for (int k = y; k < y + randh; k++) {
                            if (j < WIDTH && k < HEIGHT) {
                                if (j == x || j == x + (randw - 1) || k == y || k == y + (randh - 1)) {
                                    world[j][k] = Tileset.WALL;
                                } else {
                                    world[j][k] = Tileset.FLOOR;
                                }
                            }
                        }
                    }
                    break;
                }
                attempts++;
            }

        }
        centers.sort(Comparator.comparingInt(c -> c[0]));
        return centers;
    }

    public void makeHallway(List<int[]> centers, TETile[][] world, Random rand){
        //remove walls between rooms
        for (int i = 0; i < centers.size() - 1; i++){
            int[] start = centers.get(i);
            int[] end = centers.get(i + 1);

            connectRooms(start[0], start[1], end[0], end[1], world);
        }
    }

    public void connectRooms(int x1, int y1, int x2, int y2, TETile[][] world) {
        boolean horizFirst = true;

        world[x1][y1] = Tileset.FLOOR;
        world[x2][y2] = Tileset.FLOOR;

        if (horizFirst) {
            drawHorizontal(x1, x2, y1, world);
            drawVertical(y1, y2, x2, world);
        } else {
            drawVertical(y1, y2, x1, world);
            drawHorizontal(x1, x2, y2, world);
        }
    }

    //correct
    public void drawHorizontal(int x1, int x2, int y, TETile[][] world) {
        for (int i = Math.min(x1, x2); i <= Math.max(x1, x2); i++) {
            setFloorWithWallTrim(i, y, world);
        }
    }

    //correct
    public void drawVertical(int y1, int y2, int x, TETile[][] world) {
        for (int i = Math.min(y1, y2); i <= Math.max(y1, y2); i++) {
            setFloorWithWallTrim(x, i, world);
        }
    }

    public void setFloorWithWallTrim(int x, int y, TETile[][] world) {
        if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT) {
            world[x][y] = Tileset.FLOOR;

            if (x + 1 < WIDTH && world[x + 1][y] == Tileset.NOTHING) {
                world[x + 1][y] = Tileset.WALL;
            }
            if (x - 1 >= 0 && world[x - 1][y] == Tileset.NOTHING) {
                world[x - 1][y] = Tileset.WALL;
            }
            if (y + 1 < HEIGHT && world[x][y + 1] == Tileset.NOTHING) {
                world[x][y + 1] = Tileset.WALL;
            }
            if (y - 1 >= 0 && world[x][y - 1] == Tileset.NOTHING) {
                world[x][y - 1] = Tileset.WALL;
            }
        }
    }

    public boolean isAreaEmpty(int x, int y, int w, int h, TETile[][] world) {
        for (int i = x; i < x + w; i++) {
            for (int j = y; j < y + h; j++) {
                if (i >= WIDTH || j >= HEIGHT || world[i][j] != Tileset.NOTHING) {
                    return false;
                }
            }
        }
        return true;
    }

    public void drawWorld(){
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        ter.renderFrame(world);
    }
}
