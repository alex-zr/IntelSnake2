package jon.com.ua.client;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;

import java.util.Comparator;
import java.util.stream.Stream;

public class BoardUtil {
    public static Point getTail(Board board) {
        return board.get(Elements.TAIL_END_DOWN, Elements.TAIL_END_UP, Elements.TAIL_END_LEFT, Elements.TAIL_END_RIGHT).get(0);
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
                .orElse((PointImpl) point);
    }

    public static Point getAnyEmpty(Board board, Point point) {
        return Stream.of(
                        new PointImpl(point.getX() - 1, point.getY()),
                        new PointImpl(point.getX() + 1, point.getY()),
                        new PointImpl(point.getX(), point.getY() + 1),
                        new PointImpl(point.getX(), point.getY() - 1)
                )
                .filter(p -> !board.getSnake().contains(p))
                .filter(p -> !board.getWalls().contains(p))
                .findAny()
                .orElse((PointImpl) point);
    }

    public static Point getNearestEmpty(Board board, Point source, Point dest) {
        return Stream.of(
                        new PointImpl(source.getX() - 1, source.getY()),
                        new PointImpl(source.getX() + 1, source.getY()),
                        new PointImpl(source.getX(), source.getY() + 1),
                        new PointImpl(source.getX(), source.getY() - 1)
                )
                .filter(p -> !board.getSnake().contains(p))
                .filter(p -> !board.getWalls().contains(p))
                .min(Comparator.comparingDouble(dest::distance))
                .orElse((PointImpl) source);
    }

    public static Direction direction(Point from, Point to) {
        if (from == null || to == null) {
            return null;
        }
        return Direction.getValues().stream()
                .filter((direction) -> direction.change(from).itsMe(to))
                .findFirst()
                .orElse(null);
    }
}
