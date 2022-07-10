package jon.com.ua.view;

import com.codenjoy.dojo.services.Direction;
import jon.com.ua.client.Board;
import jon.com.ua.client.Elements;

import java.util.List;

import static jon.com.ua.client.Elements.HEAD_DOWN;
import static jon.com.ua.client.Elements.HEAD_LEFT;
import static jon.com.ua.client.Elements.HEAD_RIGHT;
import static jon.com.ua.client.Elements.HEAD_UP;
import static jon.com.ua.client.Elements.NONE;
import static jon.com.ua.client.Elements.TAIL_END_DOWN;
import static jon.com.ua.client.Elements.TAIL_END_LEFT;
import static jon.com.ua.client.Elements.TAIL_END_RIGHT;
import static jon.com.ua.client.Elements.TAIL_END_UP;

public class BoardExt extends Board implements Cloneable {
    public static final int SIZE = 15;

    public BoardExt(int size) {
        super(size);
        clear();
    }

    public BoardExt() {
        super(SIZE);
        clear();
    }

    public void clear() {
        this.field = new char[1][this.size][this.size];
        for (int i = 0; i < this.field[0].length; i++) {
            for (int j = 0; j < this.field[0][i].length; j++) {
                this.field[0][i][j] = Elements.NONE.ch();
            }
        }
    }

    private static boolean isXOutOfBounds(int x, int width) {
        return x < 0 || x > width;
    }

    private static boolean isYOutOfBounds(int y, int height) {
        return y < 0 || y > height;
    }

    public static boolean isInBounds(Element element, int width, int height) {
        boolean isXInBounds = !isXOutOfBounds(element.getX(), width);
        boolean isYInBounds = !isYOutOfBounds(element.getY(), height);
        return isXInBounds && isYInBounds;
    }

    public void render(Snake snake, Apple apple, BadApple badApple, List<Wall> walls) {
        clearHigh();
        putWalls(walls);
        putApple(apple);
        putBadApple(badApple);
        putSnake(snake);
    }

    public void clearHigh() {
        for (int x = 0; x < size(); x++) {
            for (int y = 0; y < size(); y++) {
                set(x, y, Elements.NONE.ch());
            }
        }
    }

    public void putWalls(List<Wall> walls) {
        walls.forEach(w -> set(w.getX(), w.getY(), Elements.BREAK.ch()));
    }

    public void putApple(Apple apple) {
        set(apple.getX(), apple.getY(), Elements.GOOD_APPLE.ch());
    }

    public void putBadApple(BadApple badApple) {
        set(badApple.getX(), badApple.getY(), Elements.BAD_APPLE.ch());
    }

    public void putSnake(Snake snake) {
        for (Element head : snake.getHeads()) {
            if ((snake.isHead(head))) {
                Elements headElement = getHeadElement(snake.getDirection());
                set(head.getX(), head.getY(), headElement.ch());
            } else {
                Elements tailElement = getTailElement(snake.getDirection());
                set(head.getX(), head.getY(), tailElement.ch());
            }
        }
    }

    public static Elements getTailElement(Direction direction) {
        return switch (direction) {
            case DOWN -> TAIL_END_DOWN;
            case UP -> TAIL_END_UP;
            case LEFT -> TAIL_END_LEFT;
            case RIGHT -> TAIL_END_RIGHT;
            default -> NONE;
        };
    }

    public static Elements getHeadElement(Direction direction) {
        return switch (direction) {
            case DOWN -> HEAD_DOWN;
            case UP -> HEAD_UP;
            case LEFT -> HEAD_LEFT;
            case RIGHT -> HEAD_RIGHT;
            default -> NONE;
        };
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
