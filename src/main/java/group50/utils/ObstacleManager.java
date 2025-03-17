package group50.utils;

import group50.model.Obstacle;
import java.util.ArrayList;
import java.util.List;
public class ObstacleManager {
    private static final List<Obstacle> obstacleList = new ArrayList<>();

    static {
        obstacleList.add(new Obstacle(1, 1,  50));
        obstacleList.add(new Obstacle(2, 2,  100));
        obstacleList.add(new Obstacle(3, 12, 200));
        obstacleList.add(new Obstacle(4, 8, 150));
    }

    public static List<Obstacle> getObstacles() {
        return obstacleList;
    }

    public static void addObstacle(Obstacle obstacle) {
        obstacleList.add(obstacle);
    }
}
