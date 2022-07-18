package jon.com.ua.client;

import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.services.RandomDice;
import jon.com.ua.view.View;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * User: your name
 */
public class YourSolver implements Solver<Board> {
    public static final int REDUCE_SIZE = 20;

    private Dice dice;
    private Board board;
    private List<Point> path;

    public YourSolver(Dice dice) {
        this.dice = dice;
        this.path = new ArrayList<>();
    }

    @Override
    public String get(Board board) {
        if (board.isGameOver()) {
            return Direction.UP.toString();
        }

        return Direction.RIGHT.toString();
    }

    public List<Point> getPath() {
        return path;
    }

    public static void main(String[] args) {
        View.runClient(
                new YourSolver(new RandomDice()),
                new Board());
    }

}
