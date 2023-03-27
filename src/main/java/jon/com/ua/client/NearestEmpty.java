package jon.com.ua.client;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class NearestEmpty {
    private static final int REDUCE_SIZE = 40;

    public static String getDirection(Board board, List<Point> path) {
        Point head = board.getHead();
        Point target = board.getSnake().size() < REDUCE_SIZE ? board.getApples().get(0) : board.getStones().get(0);
        path = new ArrayList<>();
        path.add(head);
        path.add(target);

        Direction direction = Stream.of(
                        new PointImpl(head.getX() - 1, head.getY()),
                        new PointImpl(head.getX() + 1, head.getY()),
                        new PointImpl(head.getX(), head.getY() + 1),
                        new PointImpl(head.getX(), head.getY() - 1)
                )
                .filter(p -> !board.getSnake().contains(p))
                .filter(p -> !board.getWalls().contains(p))
                .min(Comparator.comparingDouble(target::distance))
                .map(t -> direction(head, t))
                .orElse(Direction.UP);
        return direction.toString();
    }

    public static Point getFirstEmpty(Board board, Point point) {
        return Stream.of(
                        new PointImpl(point.getX() - 1, point.getY()),
                        new PointImpl(point.getX() + 1, point.getY()),
                        new PointImpl(point.getX(), point.getY() + 1),
                        new PointImpl(point.getX(), point.getY() - 1)
                )
                .filter(p -> !board.getSnake().contains(p))
                .filter(p -> !board.getWalls().contains(p))
                .findFirst()
                .orElseThrow();
    }

    public static Direction direction(Point from, Point to) {
        return Direction.getValues().stream()
                .filter((direction) -> direction.change(from).itsMe(to))
                .findFirst()
                .orElse(null);
    }
}
