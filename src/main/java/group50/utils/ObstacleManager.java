package group50.utils;

import group50.model.Obstacle;
import java.util.ArrayList;
import java.util.List;
public class ObstacleManager {
    private static final List<Obstacle> obstacleList = new ArrayList<>();
    private static int obstacleCounter = 0;

    static {
        obstacleList.add(new Obstacle("Tree", 1,  0, "tree.png", 600));
        obstacleList.add(new Obstacle("Car", 2,  0, "car.png", 500));
        obstacleList.add(new Obstacle("????", 12, 0, "ufo.png", 1000));
        obstacleList.add(new Obstacle("Plane", 8, 0, "plane.png", 1500));
    }


    public static List<Obstacle> getObstacles() {
        System.out.println("Obstacle List: " + obstacleList);
        return obstacleList;
    }

    public static void addObstacle(Obstacle obstacle) {
        obstacleList.add(obstacle);
    }
    public static Obstacle getObstacleByName(String name) {
        for (Obstacle obstacle : obstacleList) {
            if (obstacle.getName().equalsIgnoreCase(name)) {
                return obstacle;
            }
        }
        return null; // or throw an exception if you want
    }


}
