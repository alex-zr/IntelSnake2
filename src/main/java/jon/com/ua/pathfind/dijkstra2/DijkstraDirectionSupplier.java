package jon.com.ua.pathfind.dijkstra2;

import jon.com.ua.pathfind.DirectionSupplier;
import jon.com.ua.view.Direction;
import jon.com.ua.view.Element;
import jon.com.ua.view.Field;
import jon.com.ua.view.Fruit;
import jon.com.ua.view.Snake;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: al1
 * Date: 8/23/13
 */
public class DijkstraDirectionSupplier implements DirectionSupplier {
    private Dijkstra dijkstra = new Dijkstra();
    private Snake snake;
    private List<Fruit> fruits;
    private Field field;
    private List<Vertex> path;

    public DijkstraDirectionSupplier(Snake snake, List<Fruit> fruits, Field field) {
        this.snake = snake;
        this.fruits = fruits;
        this.field = field;
    }

    @Override
    public Direction getDirection() {
        Element destinationElement = fruits.get(0);
        Element sourceElement = snake.getLead();

        Vertex[][] nodes = DijkstraService.createGraph(snake, field);
        Vertex sourceVertex = nodes[sourceElement.getY()][sourceElement.getX()];
        Vertex destinationVertex = nodes[destinationElement.getY()][destinationElement.getX()];
        dijkstra.computePaths(sourceVertex);
        path = dijkstra.getShortestPathTo(destinationVertex);
        System.out.println("Path: " + path);
        Direction direction = calcDirectionByPath(sourceVertex, path, nodes);
        return direction;
    }

    @Override
    public List<Vertex> getPath() {
        return path;
    }

    private Direction calcDirectionByPath(Vertex sourceVertex, List<Vertex> path, Vertex[][] nodes) {
        if (path.size() < 2) {
            return getFreeArea(sourceVertex, nodes);
        }
        Element sourceElement = path.get(0).getElement();
        Element targetElement = path.get(1).getElement();
        Direction res = getDirection(sourceElement, targetElement);
        System.out.println("Snake: " + snake);
        System.out.println("Source element: " + sourceElement);
        System.out.println("Destination element: " + targetElement);
        System.out.println("Move: " + res);
        return res;
    }

    private Direction getDirection(Element sourceElement, Element targetElement) {
        Direction res;
        if (targetElement.getX() > sourceElement.getX()) {
            res = Direction.RIGHT;
        } else if (sourceElement.getX() > targetElement.getX()) {
            res = Direction.LEFT;
        } else if (targetElement.getY() > sourceElement.getY()) {
            res = Direction.DOWN;
        } else {
            res = Direction.UP;
        }
        return res;
    }

    private Direction getFreeArea(Vertex sourceVertex, Vertex[][] nodes) {
        for (Edge edge : sourceVertex.getAdjacencies()) {
            if (edge.target != null) {
                return getDirection(sourceVertex.getElement(), edge.target.getElement());
            }
        }
        return Direction.RIGHT;
    }
}
