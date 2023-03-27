package jon.com.ua.client;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Stream;

public class BiggerArea {
    /*    public static String getDirection(Board board, Dijkstra.Vertex[][] vertices) {
            Dijkstra.Vertex[][] verticesClone = vertices.clone();
            Point head = board.getHead();
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
        }*/
    public static String getDirection(Board board, Dijkstra.Vertex[][] vertices) {
        Point headPoint = board.getHead();
        Point applePoint = board.getApples().get(0);
        Point tailPoint = BoardUtil.getTail(board);
        Dijkstra.Vertex head = (Dijkstra.Vertex) vertices[headPoint.getX()][headPoint.getY()].clone();
//        int headArea = BiggerArea.calcVertices(headVertex);
        Dijkstra.Vertex left = (Dijkstra.Vertex) vertices[headPoint.getX() - 1][headPoint.getY()];
        if (left != null && left.point.itsMe(applePoint)) { // TODO почему надо есть яблоко не заботясь о свободном пространстве в направлении яблока ?!
            return Direction.LEFT.toString();
        }
        Dijkstra.Vertex right = (Dijkstra.Vertex) vertices[headPoint.getX() + 1][headPoint.getY()];
        if (right != null && right.point.itsMe(applePoint)) {
            return Direction.RIGHT.toString();
        }
        Dijkstra.Vertex up = (Dijkstra.Vertex) vertices[headPoint.getX()][headPoint.getY() + 1];
        if (up != null && up.point.itsMe(applePoint)) {
            return Direction.UP.toString();
        }
        Dijkstra.Vertex down = (Dijkstra.Vertex) vertices[headPoint.getX()][headPoint.getY() - 1];
        if (down != null && down.point.itsMe(applePoint)) {
            return Direction.DOWN.toString();
        }

        left = left == null ? null : (Dijkstra.Vertex) left.clone();
        right = right == null ? null : (Dijkstra.Vertex) right.clone();
        up = up == null ? null : (Dijkstra.Vertex) up.clone();
        down = down == null ? null : (Dijkstra.Vertex) down.clone();

//        vertices[headPoint.getX() - 1][headPoint.getY()].edges.removeIf(e -> e.target() == head); // left
//        vertices[headPoint.getX() + 1][headPoint.getY()].edges.removeIf(e -> e.target() == head); // right
//        vertices[headPoint.getX()][headPoint.getY() + 1].edges.removeIf(e -> e.target() == head); // up
//        vertices[headPoint.getX()][headPoint.getY() - 1].edges.removeIf(e -> e.target() == head); // down

//        int leftArea = BiggerArea.calcVertices(left, vertices);
//        int rightArea = BiggerArea.calcVertices(right, vertices);
//        int upArea = BiggerArea.calcVertices(up, vertices);
//        int downArea = BiggerArea.calcVertices(down, vertices);

        Direction direction = Stream.of(
                        vertices[headPoint.getX() - 1][headPoint.getY()],
                        vertices[headPoint.getX() + 1][headPoint.getY()],
                        vertices[headPoint.getX()][headPoint.getY() + 1],
                        vertices[headPoint.getX()][headPoint.getY() - 1]
                )
                .filter(Objects::nonNull)
                .filter(v -> !board.getSnake().contains(v.point))
                .filter(v -> !board.getWalls().contains(v.point))
                .peek(v -> v.edges.removeIf(e -> e.target() == head))
//                .max(Comparator.comparingInt(v -> BiggerArea.calcVertices(v, vertices)))
                .max((v1, v2) -> {
                    if (BiggerArea.calcVertices(v1.point, vertices) == BiggerArea.calcVertices(v2.point, vertices)) {
                        return Double.compare(tailPoint.distance(v1.point), tailPoint.distance(v2.point));
                    }
                    if (BiggerArea.calcVertices(v1.point, vertices) > BiggerArea.calcVertices(v2.point, vertices)) {
                        return 1;
                    }
                    return -1;
                })
                .map(v -> direction(headPoint, v.point))
                .orElse(Direction.UP);
        vertices[headPoint.getX() - 1][headPoint.getY()] = left;
        vertices[headPoint.getX() + 1][headPoint.getY()] = right;
        vertices[headPoint.getX()][headPoint.getY() + 1] = up;
        vertices[headPoint.getX()][headPoint.getY() - 1] = down;

        return direction.toString();
    }

    public static boolean isAreaBetter (Board board, Dijkstra.Vertex[][] vertices) {
        Point headPoint = board.getHead();
        Dijkstra.Vertex headVertex = new Dijkstra.Vertex(headPoint);
        int snakeSize = board.getSnake().size();
        List<Dijkstra.Vertex> directions = Stream.of(
                        vertices[headPoint.getX() - 1][headPoint.getY()],
                        vertices[headPoint.getX() + 1][headPoint.getY()],
                        vertices[headPoint.getX()][headPoint.getY() + 1],
                        vertices[headPoint.getX()][headPoint.getY() - 1]
                )
                .filter(Objects::nonNull)
                .filter(v -> !board.getSnake().contains(v.point))
                .filter(v -> !board.getWalls().contains(v.point))
                .toList();
        Dijkstra.Vertex maxVertex = directions.stream().max(Comparator.comparingDouble(v -> BiggerArea.calcVertices(v.point, vertices))).orElse(headVertex);
        Dijkstra.Vertex minVertex = directions.stream().min(Comparator.comparingDouble(v -> BiggerArea.calcVertices(v.point, vertices))).orElse(headVertex);
        int maxArea = BiggerArea.calcVertices(maxVertex.point, vertices);
        int minArea = BiggerArea.calcVertices(minVertex.point, vertices);
        System.out.printf("Max: %d, Min: %d\n", maxArea, minArea);
        return maxArea > minArea * 1.5;
    }

    public static void clearVisiting(Dijkstra.Vertex[][] vertices) {
        for (Dijkstra.Vertex[] line : vertices) {
            for (Dijkstra.Vertex vertex : line) {
                if (vertex != null) {
                    vertex.visited = false;
                }
            }
        }

    }

    private static Direction direction(Point from, Point to) {
        return Direction.getValues().stream()
                .filter((direction) -> direction.change(from).itsMe(to))
                .findFirst()
                .orElse(null);
    }

    public static int calcVertices(Point startPoint, Dijkstra.Vertex[][] vertices) {
        Dijkstra.Vertex start = vertices[startPoint.getX()][startPoint.getY()];
        if (start == null) {
            return 0;
        }

        Queue<Dijkstra.Vertex> queue = new LinkedList<>();
        queue.add(start);
        clearVisiting(vertices);
        start.visited = true;
        int verticesCounter = 0;

        Dijkstra.Vertex current;

        while (!queue.isEmpty()) {
            current = queue.poll();
            verticesCounter++;

            for (Dijkstra.Edge edge : current.edges) {
                if (!edge.target().visited) {
                    edge.target().visited = true;
                    queue.add(edge.target());
                }
            }
        }

        return verticesCounter;
    }
}
