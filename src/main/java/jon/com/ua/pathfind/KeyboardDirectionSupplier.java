package jon.com.ua.pathfind;

import jon.com.ua.view.Direction;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Created with IntelliJ IDEA.
 * User: al1
 * Date: 8/21/13
 */
public class KeyboardDirectionSupplier implements DirectionSupplier {
    public static final int KEY_LEFT = 37;
    public static final int KEY_DOWN = 40;
    public static final int KEY_RIGHT = 39;
    public static final int KEY_UP = 38;

    private Direction direction = Direction.RIGHT;

    @Override
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public KeyboardDirectionSupplier(JFrame main) {
        main.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
//                snake.setOldDirection(snake.getDirection());

                if (e.getKeyCode() == KEY_LEFT && direction != Direction.RIGHT) {
                    direction = Direction.LEFT;
                }

                if (e.getKeyCode() == KEY_RIGHT && direction != Direction.LEFT) {
                    direction = Direction.RIGHT;
                }

                if (e.getKeyCode() == KEY_UP && direction != Direction.DOWN) {
                    direction = Direction.UP;
                }

                if (e.getKeyCode() == KEY_DOWN && direction != Direction.UP) {
                    direction = Direction.DOWN;
                }
            }
        });
    }

    @Override
    public Direction getDirection() {
        return direction;
    }
}
