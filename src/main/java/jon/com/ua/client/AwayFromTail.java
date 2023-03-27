package jon.com.ua.client;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;

import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

public class AwayFromTail {
    public static String getDirection(Board board, Dijkstra.Vertex[][] vertices) {
        Point headPoint = board.getHead();
//        Point applePoint = board.getApples().get(0);
        Point tailPoint = BoardUtil.getTail(board);


        Direction direction = Stream.of(
                        vertices[headPoint.getX() - 1][headPoint.getY()],
                        vertices[headPoint.getX() + 1][headPoint.getY()],
                        vertices[headPoint.getX()][headPoint.getY() + 1],
                        vertices[headPoint.getX()][headPoint.getY() - 1]
                )
                .filter(Objects::nonNull)
                .filter(v -> !board.getSnake().contains(v.point))
                .filter(v -> !board.getWalls().contains(v.point))
//                .peek(v -> v.edges.removeIf(e -> e.target() == head))
//                .max(Comparator.comparingInt(v -> BiggerArea.calcVertices(v, vertices)))
                .max(Comparator.comparingDouble(v -> tailPoint.distance(v.point)))
                .map(v -> BoardUtil.direction(headPoint, v.point))
                .orElse(Direction.UP);

        return direction.toString();
    }
}
