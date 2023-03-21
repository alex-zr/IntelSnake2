package jon.com.ua.client;

import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import jon.com.ua.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * User: your name
 */
public class YourSolver implements Solver<Board> {


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
        path = new ArrayList<>();
        return NearestEmpty.getDirection(board, path);
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
