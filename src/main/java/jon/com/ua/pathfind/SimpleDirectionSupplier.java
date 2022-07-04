package jon.com.ua.pathfind;

import jon.com.ua.view.Direction;
import jon.com.ua.view.Element;
import jon.com.ua.view.Fruit;
import jon.com.ua.view.Snake;

import java.awt.*;
import java.util.List;
import java.util.TreeMap;

/**
 * Created with IntelliJ IDEA.
 * User: al1
 * Date: 8/23/13
 */
public class SimpleDirectionSupplier implements DirectionSupplier {
    private Snake snake;
    private List<Fruit> fruits;

    public SimpleDirectionSupplier(Snake snake, List<Fruit> fruits) {
        this.snake = snake;
        this.fruits = fruits;
    }

    @Override
    public Direction getDirection() {
        Element destinationElement = fruits.get(0);
        Element sourceElement = snake.getLead();
        Direction direction = calcDirectionByVerteces(sourceElement, destinationElement);
        return direction;
    }

    private Direction calcDirectionByVerteces(Element sourceElement, Element targetElement) {
        //Direction res = Direction.RIGHT;
        TreeMap<Double, Direction> dists = new TreeMap<Double, Direction>();

        Element rightElement = new Element(Color.BLACK, "r",sourceElement.getX() + 1, sourceElement.getY());
        Element leftElement = new Element(Color.BLACK, "l",sourceElement.getX() - 1, sourceElement.getY());
        Element downElement = new Element(Color.BLACK, "d", sourceElement.getX(), sourceElement.getY() + 1);
        Element upElement = new Element(Color.BLACK, "u", sourceElement.getX(), sourceElement.getY() - 1);

        calcDistanceAndSave(targetElement, dists, rightElement, Direction.RIGHT, new DirectionCompass(rightElement));
        calcDistanceAndSave(targetElement, dists, leftElement, Direction.LEFT, new DirectionCompass(leftElement));
        calcDistanceAndSave(targetElement, dists, downElement, Direction.DOWN, new DirectionCompass(downElement));
        calcDistanceAndSave(targetElement, dists, upElement, Direction.UP, new DirectionCompass(upElement));

        // dead end
        if (dists.isEmpty()) {
            return Direction.RIGHT;
        }

        Double nearestKey = dists.keySet().iterator().next();
        System.out.println(dists);
        Direction nearestDirection = dists.get(nearestKey);
        return nearestDirection;
    }

    private void calcDistanceAndSave(Element targetElement, TreeMap<Double, Direction> dists, Element element,
                                     Direction value, DirectionCompass compass) {
        double distToElem = calcDistance(targetElement, element);
        if (!snake.isBody(element) && !snake.isDeadGlass(element, compass)) {
            dists.put(distToElem, value);
        }
    }

    private double calcDistance(Element targetElement, Element rightElement) {
        double width = targetElement.getX() - rightElement.getX();
        double height = targetElement.getY() - rightElement.getY();
        double dist = Math.sqrt(Math.abs(width * width) + Math.abs(height * height));
        return dist;
    }
}
