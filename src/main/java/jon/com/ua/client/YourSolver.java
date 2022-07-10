package jon.com.ua.client;

import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.services.RandomDice;
import jon.com.ua.view.View;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * User: your name
 */
public class YourSolver implements Solver<Board> {
    public static final int REDUCE_SIZE = 20;

    private Dice dice;
    private Board board;

    public YourSolver(Dice dice) {
        this.dice = dice;
    }

    @Override
    public String get(Board board) {
        if (board.isGameOver()) {
            return Direction.UP.toString();
        }
        this.board = board;

        Point head = board.getHead();
        Point target = board.getSnake().size() < REDUCE_SIZE ? board.getApples().get(0) : board.getStones().get(0);
        //board.isAt(target.getX(), target.getY(), Elements.NONE);
        List<Elements> allAt = board.getAllAt(head);
        List<Point> neighbours = getNearEmpty(board, head, target);
        Direction direction = neighbours.stream()
                //.peek(p -> System.out.println(p.toString() + ":" + p.distance(target)))
                .min(Comparator.comparingDouble(target::distance))
                .map(head::direction)
                .orElse(Direction.UP);
        return direction.toString();
    }

    private List<Point> getNearEmpty(Board board, Point head, Point target) {
        List<Point> neighbours = new ArrayList<>();
        Elements targetElement = board.getAt(target);

        Point left = new PointImpl(head.getX() - 1, head.getY());
        Point right = new PointImpl(head.getX() + 1, head.getY());
        Point up = new PointImpl(head.getX(), head.getY() + 1);
        Point down = new PointImpl(head.getX(), head.getY() - 1);

        if ((board.isAt(left, Elements.NONE) || board.isAt(left, targetElement))) {
            neighbours.add(left);
        }
        if ((board.isAt(right, Elements.NONE) || board.isAt(right, targetElement))) {
            neighbours.add(right);
        }
        if ((board.isAt(up, Elements.NONE) || board.isAt(up, targetElement))) {
            neighbours.add(up);
        }
        if ((board.isAt(down, Elements.NONE) || board.isAt(down, targetElement))) {
            neighbours.add(down);
        }
        return neighbours;
    }

    public static void main(String[] args) {
        View.runClient(
                new YourSolver(new RandomDice()),
                new Board());
    }

}
