package jon.com.ua.client;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BoardUtil {
    public static Point getTail(Board board) {
        List<Point> tail = board.get(Elements.TAIL_END_DOWN, Elements.TAIL_END_UP, Elements.TAIL_END_LEFT, Elements.TAIL_END_RIGHT);
        if (tail.isEmpty()) {
            return null;
        }
        return tail.get(0);
    }

/*    public static Point getFirstEmpty(Board board, Point point) {
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
    }*/

    public static Point getAnyEmptyDesirableWithoutStone(Board board, Point point, boolean loop) {
        Point stone = board.getStones().get(0);
        List<PointImpl> availablePoints = Stream.of(
                        new PointImpl(point.getX() - 1, point.getY()),
                        new PointImpl(point.getX() + 1, point.getY()),
                        new PointImpl(point.getX(), point.getY() + 1),
                        new PointImpl(point.getX(), point.getY() - 1)
                )
                .filter(p -> !board.getSnake().contains(p))
                .filter(p -> !board.getWalls().contains(p))
                .collect(Collectors.toList());
        //availablePoints = new ArrayList<>(availablePoints);
        Collections.shuffle(availablePoints);
        if (!loop && availablePoints.size() > 1) {
            return availablePoints.stream()
                    .filter(p -> !stone.itsMe(p))
                    .findAny()
                    .orElse((PointImpl) point);
        } else {
            return availablePoints.stream()
                    .findAny()
                    .orElse((PointImpl) point);
        }
    }

/*    public static Point getNearestEmpty(Board board, Point source, Point dest) {
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
    }*/

    public static Direction direction(Point from, Point to) {
        if (from == null || to == null) {
            return null;
        }
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

    public static void clearVisiting(Dijkstra.Vertex[][] vertices) {
        for (Dijkstra.Vertex[] line : vertices) {
            for (Dijkstra.Vertex vertex : line) {
                if (vertex != null) {
                    vertex.visited = false;
                }
            }
        }
    }

    public static Dijkstra.Vertex[][] createGraph(Board board, boolean withHead, boolean withTail, boolean withStone) {
        Dijkstra.Vertex[][] vertices = new Dijkstra.Vertex[board.size()][board.size()]; //instance variable
        for (int i = 1; i < vertices.length - 1; i++) {
            for (int j = 1; j < vertices[0].length - 1; j++) {
                if (board.isAt(i, j, Elements.NONE)) {
                    vertices[i][j] = new Dijkstra.Vertex(new PointImpl(i, j));
                }
            }
        }
        if (withHead) {
            Point head = board.getHead();
            vertices[head.getX()][head.getY()] = new Dijkstra.Vertex(head);
        }
        if (withTail) {
            Point tail = BoardUtil.getTail(board);
            vertices[tail.getX()][tail.getY()] = new Dijkstra.Vertex(tail);
        }
        if (withStone) {
            Point stone = board.getStones().get(0);
            vertices[stone.getX()][stone.getY()] = new Dijkstra.Vertex(stone);
        }
        Point destination = board.getApples().get(0);
        vertices[destination.getX()][destination.getY()] = new Dijkstra.Vertex(destination);

        for (int i = 1; i < vertices.length - 1; i++) {
            for (int j = 1; j < vertices[0].length - 1; j++) {
                if (vertices[i][j] != null) {
                    Dijkstra.Vertex vertex = vertices[i][j];
                    addNeighbourIfExists(vertex, vertices[i - 1][j]);
                    addNeighbourIfExists(vertex, vertices[i + 1][j]);
                    addNeighbourIfExists(vertex, vertices[i][j + 1]);
                    addNeighbourIfExists(vertex, vertices[i][j - 1]);
                }
            }
        }

        return vertices;
    }

    private static void addNeighbourIfExists(Dijkstra.Vertex vertex, Dijkstra.Vertex neighbour) {
        if (neighbour != null) {
            neighbour.edges.add(new Dijkstra.Edge(vertex, 1));
            vertex.edges.add(new Dijkstra.Edge(neighbour, 1));
        }
    }

    public static List<Point> getPathToTail(Board board, Point head, Point tail, int moveCounter) {
        // TODO check if withStone needed
        boolean withStone = board.getSnake().size() > 10;
        Dijkstra.Vertex[][] verticesWithHeadTailStone = BoardUtil.createGraph(board, true, true, withStone);
        Dijkstra.Vertex[][] verticesWithHeadTail = BoardUtil.createGraph(board, true, true, false);
        List<Point> pathToTailAvoidStone = getPathToTail(board, verticesWithHeadTail, head, tail, false);
        List<Point> pathToTailWithStone = getPathToTail(board, verticesWithHeadTailStone, head, tail, true);
        if (moveCounter > board.getSnake().size() * 1.5) {
            return pathToTailWithStone;
        }
        if (pathToTailAvoidStone.size() > 1) {
            return pathToTailAvoidStone;
        } else {
            return pathToTailWithStone;
        }
    }

    public static List<Point> getPathToTailWithoutHead(Board board, Point head, Point tail, boolean loop) {
        // TODO check if withStone needed
        boolean withStone = board.getSnake().size() > 10;
        Dijkstra.Vertex[][] verticesWithHeadTailStone = BoardUtil.createGraph(board, false, true, withStone);
        Dijkstra.Vertex[][] verticesWithHeadTail = BoardUtil.createGraph(board, false, true, false);

//        tail = BoardUtil.getAnyEmptyDesirableWithoutStone(board, tail, loop);
        List<Point> pathToTail = getPathToTail(board, verticesWithHeadTail, head, tail, loop);
        if (pathToTail.size() > 1) {
            return pathToTail;
        } else {
            List<Point> pathToTailWithStone = getPathToTail(board, verticesWithHeadTailStone, head, tail, loop);
            return pathToTailWithStone;
        }
    }

    private static List<Point> getPathToTail(Board board, Dijkstra.Vertex[][] verticesWithHeadTail, Point head, Point tail, boolean loop) {
        //Point tail = BoardUtil.getTail(board);
        System.out.println("Tail point: " + tail);
        //tail = justEat ? BoardUtil.getNearestEmpty(board, tail, head) : tail;
        Dijkstra.Vertex headVertex = verticesWithHeadTail[head.getX()][head.getY()];
        Dijkstra.Vertex tailVertex = verticesWithHeadTail[tail.getX()][tail.getY()];

        computePaths(headVertex, board);

        List<Point> path = new ArrayList<>();
        for (Dijkstra.Vertex vertex = tailVertex; vertex != null; vertex = vertex.previous) {
            path.add(vertex.point);
        }
        Collections.reverse(path);
        return path;
    }

    public static void computePaths(Dijkstra.Vertex source, Board board) {
        if (source == null) {
            return;
        }
        source.minDistance = 0;
        Comparator<Dijkstra.Vertex> shortComparator = getShortComparator(board);
        PriorityQueue<Dijkstra.Vertex> vertexQueue = new PriorityQueue<>(
//        return Double.compare(minDistance, other.minDistance);
                shortComparator);

/*
        (v1, v2) -> {
            if (BiggerArea.calcVertices(v1.point, vertices) == BiggerArea.calcVertices(v2.point, vertices)) {
                return Double.compare(tailPoint.distance(v1.point), tailPoint.distance(v2.point));
            }
            if (BiggerArea.calcVertices(v1.point, vertices) > BiggerArea.calcVertices(v2.point, vertices)) {
                return 1;
            }
            return -1;
        });
*/
        vertexQueue.add(source);

        while (!vertexQueue.isEmpty()) {
            Dijkstra.Vertex current = vertexQueue.poll();

            // Visit each edge exiting current
            for (Dijkstra.Edge edge : current.edges) {
                Dijkstra.Vertex neighbor = edge.target();
                double weight = edge.weight();
                double distanceToNeighbor = current.minDistance + weight;
                if (neighbor != null && distanceToNeighbor < neighbor.minDistance) {
                    //vertexQueue.remove(neighbor);
                    neighbor.minDistance = distanceToNeighbor;
                    neighbor.previous = current;
                    vertexQueue.add(neighbor);
                }
            }
        }
    }

    private static Comparator<Dijkstra.Vertex> getShortComparator(Board board) {
        Point tailPoint = BoardUtil.getTail(board);
        Comparator<Dijkstra.Vertex> shortComparator = (v1, v2) -> {
            if (v1.minDistance == v2.minDistance) {
                return Double.compare(tailPoint.distance(v2.point), tailPoint.distance(v1.point));
/*                        if (tailPoint.distance(v2.point) == tailPoint.distance(v1.point)) {
                            if (v1.point.itsMe(stone)) {
                                return -1;
                            }
                            if (v2.point.itsMe(stone)) {
                                return 1;
                            }
                            return 0;
                        }
                        if (tailPoint.distance(v2.point) > tailPoint.distance(v1.point)) {
                            return 1;
                        }
                        return -1;*/
            }
            if (v1.minDistance > v2.minDistance) {
                return 1;
            }
            return -1;
        };
        return shortComparator;
    }
}
