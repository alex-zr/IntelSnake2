package jon.com.ua.client;

import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import jon.com.ua.view.View;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * User: your name
 */
public class YourSolver implements Solver<Board> {
    private Dice dice;
    private Board board;
    private List<Point> path;
    private static int lastSize = 0;
    private static int moveCounter = 0;

    public YourSolver(Dice dice) {
        this.dice = dice;
        this.path = new ArrayList<>();
    }

    @Override
    public String get(Board board) {
        if (board.isGameOver()) {
            return Direction.UP.toString();
        }

        int snakeSize = board.getSnake().size();
        if (snakeSize == 2) {
            lastSize = 2;
            moveCounter = 0;
        }
        if (snakeSize < lastSize) {
            lastSize = snakeSize;
        }
        boolean justEat = snakeSize > lastSize;
        if (justEat) {
            System.out.println("--- Just eat ---");
            lastSize = snakeSize;
            moveCounter = 0;
        }
        moveCounter++;
        boolean loop = snakeSize > Dijkstra.SNAKE_MAX_SIZE && moveCounter > snakeSize * 2.5;
        System.out.println("Move counter: " + moveCounter);
        if (loop) {
            System.out.println("--- Loop ---");
        }

        String direction = Dijkstra.getDirection(board, path, justEat, moveCounter);
//        String direction = NearestEmpty.getDirection(board, path);
        return direction;
    }

    public List<Point> getPath() {
        return path;
    }

    public static void main(String[] args) {
        View.runClient(
                new YourSolver(null),
                new Board());
    }

}
