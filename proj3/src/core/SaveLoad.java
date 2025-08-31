package core;

import edu.princeton.cs.algs4.In;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SaveLoad {

    public static void saveGame(long seed, int avatarX, int avatarY, int coinsCollected, List<int[]> coinPositions, int[] doorPosition, boolean lineOfSightEnabled) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("save.txt"));
            writer.write(Long.toString(seed));
            writer.newLine();
            writer.write(avatarX + " " + avatarY);
            writer.newLine();
            writer.write(Integer.toString(coinsCollected));
            writer.newLine();
            writer.write("lights:" + lineOfSightEnabled);
            writer.newLine();
            for (int[] pos : coinPositions) {
                writer.write(pos[0] + "," + pos[1]);
                writer.newLine();
            }
            writer.write("door:" + doorPosition[0] + "," + doorPosition[1]);
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SaveData loadGame() {
        try {
            In in = new In("save.txt");
            long seed = Long.parseLong(in.readLine());
            String[] pos = in.readLine().split(" ");
            int avatarX = Integer.parseInt(pos[0]);
            int avatarY = Integer.parseInt(pos[1]);
            int coinsCollected = Integer.parseInt(in.readLine());
            boolean lineOfSightEnabled = false;

            List<int[]> coinPositions = new ArrayList<>();
            int[] doorPosition = new int[2];


            while (!in.isEmpty()) {
                String line = in.readLine();
                if (line.startsWith("door:")) {
                    String[] doorParts = line.substring(5).split(",");
                    doorPosition[0] = Integer.parseInt(doorParts[0]);
                    doorPosition[1] = Integer.parseInt(doorParts[1]);
                } else if (line.startsWith("lights:")) {
                    lineOfSightEnabled = Boolean.parseBoolean(line.substring(7));
                } else {
                    String[] parts = line.split(",");
                    coinPositions.add(new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1])});
                }
            }

            return new SaveData(seed, avatarX, avatarY, coinsCollected, coinPositions, doorPosition, lineOfSightEnabled);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
