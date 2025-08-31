package core;

import java.util.List;

public class SaveData {
    public final long seed;
    public final int avatarX, avatarY;
    public final int coinsCollected;
    public final List<int[]> coinPositions;
    public final int[] doorPosition;
    public final boolean lineOfSightEnabled;


    public SaveData(long seed, int avatarX, int avatarY, int coinsCollected, List<int[]> coinPositions, int[] doorPosition, boolean lineOfSightEnabled) {
        this.seed = seed;
        this.avatarX = avatarX;
        this.avatarY = avatarY;
        this.coinsCollected = coinsCollected;
        this.coinPositions = coinPositions;
        this.doorPosition = doorPosition;
        this.lineOfSightEnabled = lineOfSightEnabled;

    }


}

