package jon.com.ua.client;

import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.services.RandomDice;
import jon.com.ua.view.View;

import java.util.Comparator;
import java.util.stream.Stream;

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
        Direction direction = Stream.of(
                        new PointImpl(head.getX() - 1, head.getY()),
                        new PointImpl(head.getX() + 1, head.getY()),
                        new PointImpl(head.getX(), head.getY() + 1),
                        new PointImpl(head.getX(), head.getY() - 1)
                )
                .filter(p -> !board.getSnake().contains(p))
                .filter(p -> !board.getBarriers().contains(p))
                .min(Comparator.comparingDouble(target::distance))
                .map(head::direction)
                .orElse(Direction.UP);
        return direction.toString();
    }

    public static void main(String[] args) {
        View.runClient(
                new YourSolver(new RandomDice()),
                new Board());
    }

}
