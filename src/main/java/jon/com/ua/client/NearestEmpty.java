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
                .map(t -> BoardUtil.direction(head, t))
                .orElse(Direction.UP);
        return direction.toString();
    }

    public static String getLongDirection(Board board, Point head, Point target, List<Point> path) {
        path.add(head);
        path.add(target);
        Direction defaultDirection = BoardUtil.direction(head, target);
        defaultDirection = defaultDirection == null ? Direction.UP : defaultDirection;
        Direction direction = Stream.of(
                        new PointImpl(head.getX() - 1, head.getY()),
                        new PointImpl(head.getX() + 1, head.getY()),
                        new PointImpl(head.getX(), head.getY() + 1),
                        new PointImpl(head.getX(), head.getY() - 1)
                )
                .filter(p -> !board.getSnake().contains(p))
                .filter(p -> !board.getWalls().contains(p))
                .max((p1, p2) -> {
                    List<Point> pathToTail1 = BoardUtil.getPathToTailWithoutHead(board, false, p1);
                    List<Point> pathToTail2 = BoardUtil.getPathToTailWithoutHead(board, false, p2);
                    return Integer.compare(pathToTail1.size(), pathToTail2.size());
                })
                .map(t -> BoardUtil.direction(head, t))
                .orElse(defaultDirection);
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

}
