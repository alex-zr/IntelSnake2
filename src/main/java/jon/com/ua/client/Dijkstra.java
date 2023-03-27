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

//11.58 - (211:61-150, 224:74-150)
public class Dijkstra {

    public static final int SNAKE_MAX_SIZE = 80;

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

    public static String getDirection(Board board, List<Point> path, boolean justEat, boolean loop) {
        Vertex[][] verticesWithHead = createGraph(board, true, false, false);
        Point stone = board.getStones().get(0);
        Point head = board.getHead();
        // TODO камень можно добавлять через создания графа
        addPoint(verticesWithHead, stone);
        Vertex[][] verticesWithoutHeadTailWithStone = createGraph(board, false, false, true);
        boolean isTailNearApple = isTailNearApple(board);
//        addPoint(verticesWithoutHeadTailWithStone, head);
        Point apple = board.getApples().get(0);
        Point tail = BoardUtil.getTail(board);
        Point tailEmpty = BoardUtil.getNearestEmpty(board, tail, head);
        Vertex headVertex = verticesWithHead[head.getX()][head.getY()];
        Vertex appleVertex = verticesWithHead[apple.getX()][apple.getY()];
        Vertex stoneVertex = verticesWithHead[stone.getX()][stone.getY()];
        int snakeSize = board.getSnake().size();
        Vertex tailVertex = verticesWithHead[tailEmpty.getX()][tailEmpty.getY()];
        computePaths(headVertex, board);
        List<Point> shortestPathToApple = getShortestPathTo(appleVertex);
        List<Point> shortestPathToStone = getShortestPathTo(stoneVertex);
        List<Point> shortestPathToTail = getShortestPathToTail(board, justEat);
//        System.out.println("Path: " + shortestPathToApple);
        int appleArea = BiggerArea.calcVertices(apple, verticesWithoutHeadTailWithStone);
        BiggerArea.clearVisiting(verticesWithHead);
        int stoneArea = BiggerArea.calcVertices(stone, verticesWithoutHeadTailWithStone);
        String biggerDirection = BiggerArea.getDirection(board, verticesWithHead);
        String areaDirection = biggerDirection;
        String stoneDirection = direction(head, getFirst(shortestPathToStone));
//        System.out.println("Stone path: " + shortestPathToStone);
        System.out.print(",  Snake size: " + snakeSize);
        System.out.print(",  Area direction: " + areaDirection);
        Point nextStepToApple = shortestPathToApple.size() < 2 ? null : getFirst(shortestPathToApple);
        Point nextStepToTail = shortestPathToTail.size() < 2 ? null : getFirst(shortestPathToTail);
        int toAppleArea = nextStepToApple == null ? 0 : BiggerArea.calcVertices(nextStepToApple, verticesWithHead);
        int toTailArea = nextStepToTail == null ? 0 : BiggerArea.calcVertices(nextStepToTail, verticesWithHead);


        boolean isEnoughPlaceToApple = toAppleArea > snakeSize - shortestPathToApple.size() - 1;
        boolean isEnoughPlaceAroundApple = appleArea > snakeSize - shortestPathToApple.size() - 1;
        boolean isEnoughPlaceAroundStone = stoneArea > snakeSize - shortestPathToStone.size() - 1;
        boolean isSnakeSizeLessMax = snakeSize < SNAKE_MAX_SIZE;
        boolean tailIsAvailable = shortestPathToTail.size() > 1;
        boolean appleIsAvailable = shortestPathToApple.size() > 1;
        boolean stoneIsAvailable = shortestPathToStone.size() > 1;
        boolean snakeHuge = snakeSize > SNAKE_MAX_SIZE * 2;
//        boolean isTailNearApple = toTailDirection == toAppleDirection;
        //boolean isTailNearApple = direction(head, getFirst(shortestPathToTail)) == direction(head, getFirst(shortestPathToApple));
        System.out.print(",  New head area: " + toAppleArea);
        System.out.print(",  Around apple area: " + appleArea);
        System.out.print(",  Tail near apple: " + isTailNearApple);
        System.out.print(",  Tail is available: " + tailIsAvailable);
        System.out.println(",  Around stone area: " + stoneArea);
        appleArea = BiggerArea.calcVertices(apple, verticesWithoutHeadTailWithStone);
        stoneArea = BiggerArea.calcVertices(stone, verticesWithoutHeadTailWithStone);
        if (((isEnoughPlaceToApple
                && isEnoughPlaceAroundApple
                && appleIsAvailable
                && isSnakeSizeLessMax)
                || (tailIsAvailable && appleIsAvailable && isTailNearApple))
            /*&& board.getSnake().size() < SNAKE_MAX_SIZE*/) {
            path.addAll(shortestPathToApple);
            String dijkstraDirection = direction(head, getFirst(shortestPathToApple));
            System.out.println("Apple direction: " + dijkstraDirection);
            return dijkstraDirection;
        } else if ((isEnoughPlaceAroundStone
                && !isSnakeSizeLessMax
                && stoneIsAvailable)
                || (snakeHuge && stoneIsAvailable && tailIsAvailable)
            //|| (stoneIsAvailable && stoneArea >= appleArea)
                            /*|| (shortestPathToStone.size() > 1
                                && stoneArea <= board.getSnake().size() - shortestPathToApple.size() - 1
                                && board.getSnake().size() > 10)*/) {
            path.addAll(shortestPathToStone);
            //String stoneDirection = direction(head, getFirst(shortestPathToStone)).toString();
            System.out.println("Stone direction: " + stoneDirection);
            return stoneDirection;
        } else if ((tailIsAvailable && !loop)
                    || (tailIsAvailable && board.countNear(board.getHead(), Elements.NONE) < 2)) {
//            path.addAll(shortestPathToApple);
//            return BiggerArea.getDirection(board, verticesWithHead);
            path.addAll(shortestPathToTail);
            String tailDirection = direction(head, getFirst(shortestPathToTail)).toString();
            System.out.println("Tail direction: " + tailDirection);
            return tailDirection;
        } else {
            path.addAll(shortestPathToApple);
            if (BiggerArea.isAreaBetter(board, verticesWithoutHeadTailWithStone)) {
                System.out.println("Bigger direction: " + biggerDirection);
                // TODO почему то при повторном вызове меняется значение
                biggerDirection = BiggerArea.getDirection(board, verticesWithHead);
                return biggerDirection;
            }

            String awayDirection = AwayFromTail.getDirection(board, verticesWithHead);
            System.out.println("Away direction: " + awayDirection);
            return awayDirection;
        }


        /*else {
            //path.addAll(shortestPathToApple);
            areaDirection = BiggerArea.getDirection(board, verticesWithHead);
            String nearestDirection1 = NearestEmpty.getDirection(board, shortestPathToApple);
            return nearestDirection1;
        }*/
    }

    private static boolean isTailNearApple(Board board) {
        Vertex[][] verticesWithTail = createGraph(board, false, true, true);
        Point apple = board.getApples().get(0);
        Point tail = BoardUtil.getTail(board);
//        Point tailEmpty = BoardUtil.getFirstEmpty(board, tail);
        Vertex tailVertex = verticesWithTail[tail.getX()][tail.getY()];
        Vertex appleVertex = verticesWithTail[apple.getX()][apple.getY()];
        computePaths(appleVertex, board);
        List<Point> shortestPathToApple = getShortestPathWithoutSource(tailVertex);
        return !shortestPathToApple.isEmpty();
    }



    private static Point getFirst(List<Point> shortestPath) {
        return shortestPath.size() < 2 ? null : shortestPath.get(1);
    }

    private static void addPoint(Vertex[][] vertices, Point stone) {
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

    public static void computePaths(Vertex source, Board board) {
        if (source == null) {
            return;
        }
        source.minDistance = 0;
        Point tailPoint = BoardUtil.getTail(board);
        PriorityQueue<Vertex> vertexQueue = new PriorityQueue<>(
//        return Double.compare(minDistance, other.minDistance);
                (v1, v2) -> {
                    if (v1.minDistance == v2.minDistance) {
                        return Double.compare(tailPoint.distance(v2.point), tailPoint.distance(v1.point));
                    }
                    if (v1.minDistance > v2.minDistance) {
                        return 1;
                    }
                    return -1;
                });

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


    private static List<Point> getShortestPathToTail(Board board, boolean justEat) {
        boolean withStone = board.getSnake().size() > 10;
        Vertex[][] verticesWithHeadTail = createGraph(board, true, true, withStone);
        Point head = board.getHead();
        Point tail = BoardUtil.getTail(board);
        tail = BoardUtil.getAnyEmpty(board, tail);
        tail = justEat ? BoardUtil.getNearestEmpty(board, tail, head) : tail;
        Vertex headVertex = verticesWithHeadTail[head.getX()][head.getY()];
        Vertex tailVertex = verticesWithHeadTail[tail.getX()][tail.getY()];
        computePaths(headVertex, board);

        List<Point> path = new ArrayList<>();
        for (Vertex vertex = tailVertex; vertex != null; vertex = vertex.previous) {
            path.add(vertex.point);
        }
        Collections.reverse(path);
        return path;
    }

    private static List<Point> getShortestPathWithoutSource(Vertex target) {
        List<Point> path = new ArrayList<>();
        for (Vertex vertex = target; vertex != null; vertex = vertex.previous) {
            path.add(vertex.point);
        }
        if (path.size() >= 1) {
            path.remove(path.size() - 1);
        }
        Collections.reverse(path);
        return path;
    }

    private static String direction(Point from, Point to) {
        if (from == null || to == null) {
            return null;
        }
        return Direction.getValues().stream()
                .filter((direction) -> direction.change(from).itsMe(to))
                .map(Direction::toString)
                .findFirst()
                .orElse(null);
    }

    private static Vertex[][] createGraph(Board board, boolean withHead, boolean withTail, boolean withStone) {
        Vertex[][] vertices = new Vertex[board.size()][board.size()]; //instance variable
        for (int i = 1; i < vertices.length - 1; i++) {
            for (int j = 1; j < vertices[0].length - 1; j++) {
                if (board.isAt(i, j, Elements.NONE)) {
                    vertices[i][j] = new Vertex(new PointImpl(i, j));
                }
            }
        }
        if (withHead) {
            Point head = board.getHead();
            vertices[head.getX()][head.getY()] = new Vertex(head);
        }
        if (withTail) {
            Point tail = BoardUtil.getTail(board);
            vertices[tail.getX()][tail.getY()] = new Vertex(tail);
        }
        if (withStone) {
            Point stone = board.getStones().get(0);
            vertices[stone.getX()][stone.getY()] = new Vertex(stone);
        }
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
