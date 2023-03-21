package jon.com.ua.client;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;

public class Dijkstra {

    public static final int SNAKE_MAX_SIZE = 45;

    public static class Vertex implements Comparable<Vertex>, Cloneable {
        Point point;
        Set<Edge> edges = new HashSet<>();
        double minDistance = Double.POSITIVE_INFINITY;
        boolean visited;
        Vertex previous;

        public Vertex(Point point) {
            this.point = point;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof Vertex))
                return false;
            Vertex vertex = (Vertex) o;
            return Objects.equals(point, vertex.point);
        }

        @Override
        public int hashCode() {
            return Objects.hash(point);
        }

        @Override
        public int compareTo(Vertex other) {
            return Double.compare(minDistance, other.minDistance);
        }

        @Override
        public String toString() {
            return "" + point;
        }

        @Override
        public Object clone() {
            try {
                return super.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static record Edge(Vertex target, double weight) {
    }

    public static String getDirection(Board board, List<Point> path) {
        Vertex[][] vertices = createGraph(board);
        addStone(vertices, board);
        Point head = board.getHead();
        Point apple = board.getApples().get(0);
        Point stone = board.getStones().get(0);
        Point tail = board.getFirst(Elements.TAIL_END_DOWN, Elements.TAIL_END_UP, Elements.TAIL_END_LEFT, Elements.TAIL_END_RIGHT);
        Point tailEmpty = NearestEmpty.getFirstEmpty(board, tail);
        Vertex headVertex = vertices[head.getX()][head.getY()];
        Vertex appleVertex = vertices[apple.getX()][apple.getY()];
        Vertex stoneVertex = vertices[stone.getX()][stone.getY()];
        Vertex tailVertex = vertices[tailEmpty.getX()][tailEmpty.getY()];
        computePaths(headVertex);
        List<Point> shortestPathToApple = getShortestPathTo(appleVertex);
        List<Point> shortestPathToStone = getShortestPathTo(stoneVertex);
        List<Point> shortestPathToTail = getShortestPathTo(tailVertex);
        System.out.println("Path: " + shortestPathToApple);
        int headArea = BiggerArea.calcVertices(appleVertex, vertices);
        BiggerArea.clearVisiting(vertices);
        int stoneArea = BiggerArea.calcVertices(stoneVertex, vertices);
        int snakeSize = board.getSnake().size();
        String areaDirection = BiggerArea.getDirection(board, vertices);
        System.out.println("Stone path: " + shortestPathToStone);
        System.out.println("Snake size: " + snakeSize);
        System.out.println("Area direction: " + areaDirection);
        headArea = BiggerArea.calcVertices(appleVertex, vertices);
        System.out.println("New head area: " + headArea);
        if (board.getSnake().size() < SNAKE_MAX_SIZE && shortestPathToApple.size() > 1
                && (headArea > board.getSnake().size() - shortestPathToApple.size() - 1
                    || shortestPathToTail.size() > 1)) {
            path.addAll(shortestPathToApple);
            String dijkstraDirection = direction(head, shortestPathToApple.get(1)).toString();
            return dijkstraDirection;
        } else if ((board.getSnake().size() >= SNAKE_MAX_SIZE && shortestPathToStone.size() > 1) || (shortestPathToStone.size() > 1 && stoneArea <= board.getSnake().size() - shortestPathToApple.size() - 1  && board.getSnake().size() > 10)) {
            path.addAll(shortestPathToStone);
            return direction(head, shortestPathToStone.get(1)).toString();
        } else if (shortestPathToTail.size() > 1) {
//            path.addAll(shortestPathToApple);
//            return BiggerArea.getDirection(board, vertices);
            path.addAll(shortestPathToTail);
            String tailDirection = direction(head, shortestPathToTail.get(1)).toString();
            return tailDirection;

        } else {
            path.addAll(shortestPathToApple);
            return BiggerArea.getDirection(board, vertices);
        }
        /*else {
            //path.addAll(shortestPathToApple);
            areaDirection = BiggerArea.getDirection(board, vertices);
            String nearestDirection1 = NearestEmpty.getDirection(board, shortestPathToApple);
            return nearestDirection1;
        }*/
    }

    private static void addStone(Vertex[][] vertices, Board board) {
        Point stone = board.getStones().get(0);
        Vertex stoneVertex = new Vertex(stone);
        vertices[stone.getX()][stone.getY()] = stoneVertex;
        List<Vertex> neighbours = new ArrayList<>(4);
        Vertex right = vertices[stone.getX() + 1][stone.getY()];
        Vertex left = vertices[stone.getX() - 1][stone.getY()];
        Vertex up = vertices[stone.getX()][stone.getY() + 1];
        Vertex down = vertices[stone.getX()][stone.getY() - 1];
        if (right != null) {
            neighbours.add(right);
        }
        if (left != null) {
            neighbours.add(left);
        }
        if (up != null) {
            neighbours.add(up);
        }
        if (down != null) {
            neighbours.add(down);
        }
        for (Vertex neighbour : neighbours) {
            neighbour.edges.add(new Edge(stoneVertex, 100));
            stoneVertex.edges.add(new Edge(neighbour, 1));
        }
    }

    public static void computePaths(Vertex source) {
        if (source == null) {
            return;
        }
        source.minDistance = 0;
        PriorityQueue<Vertex> vertexQueue = new PriorityQueue<>();
        vertexQueue.add(source);

        while (!vertexQueue.isEmpty()) {
            Vertex current = vertexQueue.poll();

            // Visit each edge exiting current
            for (Edge edge : current.edges) {
                Vertex neighbor = edge.target;
                double weight = edge.weight;
                double distanceToNeighbor = current.minDistance + weight;
                if (neighbor != null && distanceToNeighbor < neighbor.minDistance) {
                    vertexQueue.remove(neighbor);
                    neighbor.minDistance = distanceToNeighbor;
                    neighbor.previous = current;
                    vertexQueue.add(neighbor);
                }
            }
        }
    }

    private static List<Point> getShortestPathTo(Vertex target) {
        List<Point> path = new ArrayList<>();
        for (Vertex vertex = target; vertex != null; vertex = vertex.previous) {
            path.add(vertex.point);
        }
        Collections.reverse(path);
        return path;
    }

    private static Direction direction(Point from, Point to) {
        return Direction.getValues().stream()
                .filter((direction) -> direction.change(from).itsMe(to))
                .findFirst()
                .orElse(null);
    }

    private static Vertex[][] createGraph(Board board) {
        Vertex[][] vertices = new Vertex[board.size()][board.size()]; //instance variable
        for (int i = 1; i < vertices.length - 1; i++) {
            for (int j = 1; j < vertices[0].length - 1; j++) {
                if (board.isAt(i, j, Elements.NONE)) {
                    vertices[i][j] = new Vertex(new PointImpl(i, j));
                }
            }
        }
        Point source = board.getHead();
        vertices[source.getX()][source.getY()] = new Vertex(source);
        Point destination = board.getApples().get(0);
        vertices[destination.getX()][destination.getY()] = new Vertex(destination);

        for (int i = 1; i < vertices.length - 1; i++) {
            for (int j = 1; j < vertices[0].length - 1; j++) {
                if (vertices[i][j] != null) {
                    Vertex vertex = vertices[i][j];
                    addNeighbourIfExists(vertex, vertices[i - 1][j]);
                    addNeighbourIfExists(vertex, vertices[i + 1][j]);
                    addNeighbourIfExists(vertex, vertices[i][j + 1]);
                    addNeighbourIfExists(vertex, vertices[i][j - 1]);
                }
            }
        }

        return vertices;
    }

    private static void addNeighbourIfExists(Vertex vertex, Vertex neighbour) {
        if (neighbour != null) {
            neighbour.edges.add(new Edge(vertex, 1));
            vertex.edges.add(new Edge(neighbour, 1));
        }
    }
}
