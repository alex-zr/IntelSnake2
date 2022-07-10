package jon.com.ua.view;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import jon.com.ua.client.Board;
import jon.com.ua.client.Elements;

import java.util.Random;

import static jon.com.ua.client.Elements.HEAD_DOWN;
import static jon.com.ua.client.Elements.HEAD_LEFT;
import static jon.com.ua.client.Elements.HEAD_RIGHT;
import static jon.com.ua.client.Elements.HEAD_UP;
import static jon.com.ua.client.Elements.TAIL_END_DOWN;
import static jon.com.ua.client.Elements.TAIL_END_LEFT;
import static jon.com.ua.client.Elements.TAIL_END_RIGHT;
import static jon.com.ua.client.Elements.TAIL_END_UP;
import static jon.com.ua.client.Elements.NONE;

@Deprecated
public class BoardUtil {
    private static final Random rnd = new Random();

    public static boolean isSnake(Board board, Point point) {
        return board.getSnake().stream().anyMatch(p -> p.itsMe(point));
    }

    public static boolean isBarriers(Board board, Point point) {
        return board.getBarriers().stream().anyMatch(p -> p.itsMe(point));
    }

    public static void clearApple(Board board) {
        Point apple = board.getApples().get(0);
        board.set(apple.getX(), apple.getY(), Elements.NONE.ch());
    }

/*    @Deprecated
    public static void initSnake(Board board, int snakeLength) {
        int rndX = board.size() / 2;
        int rndY = board.size() / 2;
        Point snake = new PointImpl(rndX, rndY);
        board.set(snake.getX(), snake.getY(), Elements.HEAD_RIGHT.ch());
        board.set(snake.getX() - 1, snake.getY(), Elements.TAIL_END_DOWN.ch());
        Direction snakeDirection = board.getSnakeDirection();
    }*/

/*
    public static void putApple(Board board) {
        Point apple;
        do {
            // Avoiding walls
            int rndX = rnd.nextInt(board.size() - 1) + 1;
            int rndY = rnd.nextInt(board.size() - 1) + 1;
            apple = new PointImpl(rndX, rndY);
        } while (BoardUtil.isBarriers(board, apple) );
//        } while (snake.isBody(fruit));

//        fruits.add(fruit);
        board.set(apple.getX(), apple.getY(), Elements.GOOD_APPLE.ch());
    }
*/

    public static void setSnakeDirection(Board board, Direction direction) {
        Point head = board.getHead();
        char element = switch (direction) {
            case RIGHT -> HEAD_RIGHT.ch();
            case LEFT -> HEAD_LEFT.ch();
            case UP -> HEAD_UP.ch();
            case DOWN -> HEAD_DOWN.ch();
            default -> HEAD_RIGHT.ch();
        };
        board.set(head.getX(), head.getY(), element);
    }

/*    @Deprecated
    public static void snakeMove(Board board) {
        Direction snakeDirection = board.getSnakeDirection();
        Point newHead = board.getHead();
        List<Point> snake = board.getSnake();
        Point head = board.getHead();
        Elements headElement = board.getAt(newHead);
        newHead.move(snakeDirection);
        if (board.isAt(newHead, Elements.NONE)) {
            board.set(newHead.getX(), newHead.getY(), headElement.ch());
            board.set(head.getX(), head.getY(), Elements.TAIL_HORIZONTAL.ch());
            Point lastElement = snake.stream().filter(p -> Elements.TAIL_END_DOWN == board.getAt(p)).findFirst().orElseThrow();
            board.set(lastElement.getX(), lastElement.getY(), Elements.NONE.ch());
        } else if (board.getBarriers().stream().anyMatch(p -> p.itsMe(newHead))) {
            board.set(head.getX(), head.getY(), Elements.NONE.ch());
        } else if (board.isAt(newHead, Elements.GOOD_APPLE)) {
            board.set(newHead.getX(), newHead.getY(), headElement.ch());
            board.set(head.getX(), head.getY(), Elements.TAIL_END_DOWN.ch());
            // increase score
            putApple(board);
        } else if (board.isAt(newHead, Elements.BAD_APPLE)) {

        }
    }*/

/*    public static void putWalls(Board board) {
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
    }*/

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

/*    public static void render(Board board, Snake snake, Apple apple, BadApple badApple) {
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
    }*/
}
