package mobiarmy.war;
import java.util.ArrayList;
public class PathSimulator {
    private short startX, startY;
    public short targetX;
    public short targetY;
    private MapData mapData;
    public ArrayList<short[]> pathFrames = new ArrayList<>();
    public PathSimulator(int startX, int startY, int targetX, int targetY, MapData mapData) {
        this.startX = (short) startX;
        this.startY = (short) startY;
        this.targetX = (short) targetX;
        this.targetY = (short) targetY;
        this.mapData = mapData;
    }
    public boolean simulate() {
        short currentX = startX;
        short currentY = startY;
        pathFrames.clear();
        for (int i = 0; (currentX != targetX || currentY != targetY) && i < 60; i++) {
            pathFrames.add(new short[]{currentX, currentY});
            int prevX = currentX;
            int prevY = currentY;
            if (this.targetX < currentX) {
                currentX--;
            } else if (this.targetX > currentX) {
                currentX++;
            }
            if (mapData.isCollisionMap(currentX, currentY - 5)) {
                currentY--;
            } else {
                currentY = simulateFall(currentX, currentY);
            }
            if (currentY > mapData.height + 100) {
                return false;
            }
            if (prevX == currentX && prevY == currentY) {
                return false;
            }
        }
        pathFrames.add(new short[]{currentX, currentY});
        return true;
    }
    private short simulateFall(short x, short y) {
        while (y < mapData.height + 200) {
            if (mapData.isCollisionMap(x, y)) {
                return y;
            }
            y++;
        }
        return y;
    }
}