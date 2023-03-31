package jon.com.ua.client;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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

    public static String getLongDirection(Board board, Point head, Point tail, List<Point> path, boolean loop, List<Point> shortestPathToTail) {
        Point target = BoardUtil.getAnyEmptyDesirableWithoutStone(board, tail, loop);
        path.add(head);
        path.add(target);
        Direction defaultDirection = shortestPathToTail.size() > 1 ? BoardUtil.direction(head, shortestPathToTail.get(1)) : BoardUtil.direction(head, target);
        if (defaultDirection == null) {
            System.out.println("Default direction is null!");
        }
        defaultDirection = defaultDirection == null ? BoardUtil.direction(head, tail) : defaultDirection;
        defaultDirection = defaultDirection == null ? Direction.UP : defaultDirection;
        PointImpl left = new PointImpl(head.getX() - 1, head.getY());
        PointImpl right = new PointImpl(head.getX() + 1, head.getY());
        PointImpl up = new PointImpl(head.getX(), head.getY() + 1);
        PointImpl down = new PointImpl(head.getX(), head.getY() - 1);
        PointImpl tailP = new PointImpl(tail.getX(), tail.getY() - 1);

        System.out.print("Left: " + left);
        System.out.print(", Right: " + right);
        System.out.print(", Up: " + up);
        System.out.print(", Down: " + down);

        System.out.println(", Default direction: " + defaultDirection);
        List<PointImpl> points = Stream.of(
                        left,
                        right,
                        up,
                        down
                )
                .filter(h -> !board.getSnake().contains(h) || h.itsMe(tail))
                .filter(h -> !board.getWalls().contains(h))
                .collect(Collectors.toList());
        System.out.println("Longest direction available: " + points);
        List<List<Point>> pointFiltered = points.stream()
                .map(h -> {
                    List<Point> pathToTailWithoutHead = BoardUtil.getPathToTailWithoutHead(board, h, target, true);
                    //pathToTailWithoutHead.removeIf(p -> p.itsMe(target));
                    System.out.printf("Path to tail without head %s: %s\n", h, pathToTailWithoutHead);
                    // Add step point to path as first step
                    pathToTailWithoutHead.add(0, h);
                    pathToTailWithoutHead.remove(pathToTailWithoutHead.size() - 1);
                    return pathToTailWithoutHead;
                })
                .collect(Collectors.toList());
        System.out.println("Points filtered: " + pointFiltered);


        Optional<List<Point>> maxNotEmpty = pointFiltered.stream()
                //.filter(h -> BoardUtil.direction(h, target) != null)
                .filter(l -> {
                    return !l.isEmpty();
                })
                .filter(l -> {
                    if (l.size() == 1) {
                        return BoardUtil.direction(l.get(0), target) != null || BoardUtil.direction(l.get(0), tail) != null;
                    }
                    return true;
                })
                .max(Comparator.comparingDouble(h -> h.size()));
        System.out.println("Max point: " + maxNotEmpty);
        Optional<Direction> optionalDirection = maxNotEmpty
                .map(list -> {
                    Direction maxDirection = BoardUtil.direction(head, list.get(0));
                    maxDirection = maxDirection == null ? BoardUtil.direction(head, target) : maxDirection;
                    if (maxDirection == Direction.UP) {
                        System.out.println("Direction is up !!!");
                    }
                    return maxDirection;
                });
        Direction direction = optionalDirection
                .orElse(defaultDirection);
        /*        Direction direction = Stream.of(
                        left,
                        right,
                        up,
                        down
                )
                .filter(p -> !board.getSnake().contains(p))
                .filter(p -> !board.getWalls().contains(p))
                .max((p1, p2) -> {
                    List<Point> pathToTail1 = BoardUtil.getPathToTailWithoutHead(board, false, p1);
                    List<Point> pathToTail2 = BoardUtil.getPathToTailWithoutHead(board, false, p2);
                    return Integer.compare(pathToTail1.size(), pathToTail2.size());
                })
                .map(t -> BoardUtil.direction(head, t))
                .orElse(defaultDirection);*/
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
