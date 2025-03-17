package group50.utils;

import group50.model.Obstacle;
import java.util.ArrayList;
import java.util.List;
public class ObstacleManager {
    private static final List<Obstacle> obstacleList = new ArrayList<>();
    private static int obstacleCounter = 0;

    static {
        obstacleList.add(new Obstacle("Tree", 1,  50));
        obstacleList.add(new Obstacle("Car", 2,  100));
        obstacleList.add(new Obstacle("Building", 12, 200));
        obstacleList.add(new Obstacle("Plane", 8, 150));
    }


    public static List<Obstacle> getObstacles() {
        System.out.println("Obstacle List: " + obstacleList);
        return obstacleList;
    }

    public static void addObstacle(Obstacle obstacle) {
        obstacleList.add(obstacle);
    }


}
