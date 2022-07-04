package jon.com.ua.pathfind;

import com.codenjoy.dojo.client.AbstractBoard;
import jon.com.ua.client.Elements;
import jon.com.ua.view.Fruit;
import jon.com.ua.view.Snake;

import javax.swing.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: al1
 * Date: 8/21/13
 */
public class DirectionSupplierFactory {
    private AbstractBoard board;
    private Snake snake;
    private List<Fruit> fruits;
    private JFrame main;

    public DirectionSupplierFactory(JFrame main, AbstractBoard<Elements> board, Snake snake, List<Fruit> fruits) {
        this.board = board;
        this.snake = snake;
        this.fruits = fruits;
        this.main = main;
    }

    public DirectionSupplier getKeyboardController() {
        return new KeyboardDirectionSupplier(main);
    }

    public DirectionSupplier getSimpleDirectionBridge() {
        return new SimpleDirectionSupplier(snake, fruits);
    }
}
