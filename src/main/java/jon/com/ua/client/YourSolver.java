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
    private static int globalMoveCounter = 0;
    private static double pointsPerMove = 0;
    private static int score = 0;

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
        moveCounter++;
        globalMoveCounter++;
        System.out.println("Move counter: " + moveCounter);

        boolean justEat = snakeSize > lastSize;
        if (justEat) {
            System.out.println("--- Just eat ---");
            lastSize = snakeSize;
            pointsPerMove = ((double) snakeSize / moveCounter);
            System.out.println("Points per move avg: " + (double)score / globalMoveCounter);
            score += snakeSize;
            System.out.println("Points per move: " + (pointsPerMove));
            moveCounter = 0;
        }

        String direction = Dijkstra.getDirection(board, path, moveCounter);
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
