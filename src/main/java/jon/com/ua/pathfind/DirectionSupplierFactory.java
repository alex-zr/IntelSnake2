package jon.com.ua.pathfind;

import jon.com.ua.view.Field;
import jon.com.ua.view.Fruit;
import jon.com.ua.view.Snake;
import jon.com.ua.pathfind.dijkstra2.DijkstraDirectionSupplier;

import javax.swing.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: al1
 * Date: 8/21/13
 */
public class DirectionSupplierFactory {
    private Field field;
    private Snake snake;
    private List<Fruit> fruits;
    private JFrame main;

    public DirectionSupplierFactory(JFrame main, Field field, Snake snake, List<Fruit> fruits) {
        this.field = field;
        this.snake = snake;
        this.fruits = fruits;
        this.main = main;
    }

    public DirectionSupplier getKeyboardController() {
        return new KeyboardDirectionSupplier(main);
    }

    public DirectionSupplier getDijkstraBridge() {
        return new DijkstraDirectionSupplier(snake, fruits, field);
    }

    public DirectionSupplier getSimpleDirectionBridge() {
        return new SimpleDirectionSupplier(snake, fruits);
    }
}
