package jon.com.ua.view;

import com.codenjoy.dojo.services.Direction;
import jon.com.ua.client.Board;
import jon.com.ua.client.Elements;

import static jon.com.ua.client.Elements.HEAD_DOWN;
import static jon.com.ua.client.Elements.HEAD_LEFT;
import static jon.com.ua.client.Elements.HEAD_RIGHT;
import static jon.com.ua.client.Elements.HEAD_UP;
import static jon.com.ua.client.Elements.NONE;
import static jon.com.ua.client.Elements.TAIL_END_DOWN;
import static jon.com.ua.client.Elements.TAIL_END_LEFT;
import static jon.com.ua.client.Elements.TAIL_END_RIGHT;
import static jon.com.ua.client.Elements.TAIL_END_UP;

public class BoardExt extends Board {
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
        if (x < 0 || x > width) {
            return true;
        }
        return false;
    }

    private static boolean isYOutOfBounds(int y, int height) {
        if (y < 0 || y > height) {
            return true;
        }
        return false;
    }

    public static boolean isInBounds(Element element, int width, int height) {
        boolean isXInBounds = !isXOutOfBounds(element.getX(), width);
        boolean isYInBounds = !isYOutOfBounds(element.getY(), height);
        return isXInBounds && isYInBounds;
    }

    // TODO remove board
    public static void render(Board board, Snake snake, Apple apple, BadApple badApple) {
        clearBoard(board);
        putWalls(board);
        putApple(board, apple);
        putBadApple(board, badApple);
        putSnake(board, snake);
    }

    public static void clearBoard(Board board) {
        for (int x = 0; x < board.size(); x++) {
            for (int y = 0; y < board.size(); y++) {
                board.set(x, y, Elements.NONE.ch());
            }
        }
    }

    public static void putWalls(Board board) {
        // Left
        for (int i = 0; i < board.size(); i++) {
            board.set(0, i, Elements.BREAK.ch());
        }
        // Right
        for (int i = 0; i < board.size(); i++) {
            board.set(board.size() - 1, i, Elements.BREAK.ch());
        }
        // Down
        for (int i = 0; i < board.size(); i++) {
            board.set(i, board.size() - 1, Elements.BREAK.ch());
        }
        // Up
        for (int i = 0; i < board.size(); i++) {
            board.set(i, 0, Elements.BREAK.ch());
        }
    }

    public static void putApple(Board board, Apple apple) {
        board.set(apple.getX(), apple.getY(), Elements.GOOD_APPLE.ch());
    }

    public static void putBadApple(Board board, BadApple badApple) {
        board.set(badApple.getX(), badApple.getY(), Elements.BAD_APPLE.ch());
    }

    public static void putSnake(Board board, Snake snake) {
        for (Element head : snake.getHeads()) {
            if ((snake.isHead(head))) {
                Elements headElement = getHeadElement(snake.getDirection());
                board.set(head.getX(), head.getY(), headElement.ch());
            } else {
                Elements tailElement = getTailElement(snake.getDirection());
                board.set(head.getX(), head.getY(), tailElement.ch());
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
}
