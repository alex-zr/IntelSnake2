package jon.com.ua.client;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;

//11.58 - (211:61-150, 224:74-150)
public class Dijkstra {

    public static final int SNAKE_MAX_SIZE = 130; // TODO change 100
    public static final int SNAKE_HUGE_SIZE = 140; // TODO change 110
    public static final int SNAKE_KILL_SIZE = 160;

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

    public static String getDirection(Board board, List<Point> path, boolean justEat, int moveCounter) {
        int snakeSize = board.getSnake().size();
        boolean loop = snakeSize > Dijkstra.SNAKE_MAX_SIZE / 2
                && moveCounter > snakeSize * 1.5;
        if (loop) {
            System.out.println("--- Loop ---");
        }
        Vertex[][] verticesWithHead = BoardUtil.createGraph(board, true, false, false);
        Point stone = board.getStones().get(0);
        Point head = board.getHead();
        Point apple = board.getApples().get(0);
        Point tail = BoardUtil.getTail(board);
        // if snake without tail after reduction
        tail = tail == null ? head : tail;
        // TODO камень можно добавлять через создания графа
        addPoint(verticesWithHead, stone);
        Vertex[][] verticesWithoutHeadTailWithStone = BoardUtil.createGraph(board, false, false, true);
        Vertex[][] verticesWithTailStone = BoardUtil.createGraph(board, false, true, true);
        Vertex[][] verticesWithTail = BoardUtil.createGraph(board, false, true, false);
        boolean isTailNearApple = loop
                ? isPointsNear(board, apple, tail, verticesWithTailStone)
                : isPointsNear(board, apple, tail, verticesWithTail);
        // TODO review this hack with Rebuild new graph
        verticesWithTailStone = BoardUtil.createGraph(board, false, true, true);
        boolean isTailNearStone = isPointsNear(board, stone, tail, verticesWithTailStone);
        Vertex headVertex = verticesWithHead[head.getX()][head.getY()];
        Vertex appleVertex = verticesWithHead[apple.getX()][apple.getY()];
        Vertex stoneVertex = verticesWithHead[stone.getX()][stone.getY()];

        BoardUtil.computePaths(headVertex, board);
        List<Point> shortestPathToApple = getShortestPathTo(appleVertex);
        List<Point> shortestPathToStone = getShortestPathTo(stoneVertex);
        List<Point> shortestPathToTail = BoardUtil.getPathToTail(board, justEat, head);
        int appleArea = BoardUtil.calcVertices(apple, verticesWithoutHeadTailWithStone);
        BoardUtil.clearVisiting(verticesWithHead);
        int stoneArea = BoardUtil.calcVertices(stone, verticesWithoutHeadTailWithStone);
//        String biggerDirection = BiggerArea.getDirection(board, verticesWithHead);
//        String areaDirection = biggerDirection;
        String stoneDirection = direction(head, getFirst(shortestPathToStone));
//        System.out.println("Stone path: " + shortestPathToStone);
        System.out.print(",  Snake size: " + snakeSize);
//        System.out.print(",  Area direction: " + areaDirection);
        Point nextStepToApple = shortestPathToApple.size() < 2 ? null : getFirst(shortestPathToApple);
//        Point nextStepToTail = shortestPathToTail.size() < 2 ? null : getFirst(shortestPathToTail);
        int toAppleArea = nextStepToApple == null ? 0 : BoardUtil.calcVertices(nextStepToApple, verticesWithHead);
//        int toTailArea = nextStepToTail == null ? 0 : BoardUtil.calcVertices(nextStepToTail, verticesWithHead);


        boolean isEnoughPlaceToApple = toAppleArea > snakeSize - shortestPathToApple.size() - 1;
        boolean isEnoughPlaceAroundApple = appleArea > snakeSize * 1.5;
        boolean isEnoughPlaceAroundStone = stoneArea > snakeSize - shortestPathToStone.size() - 1;
        boolean isSnakeSizeLessMax = snakeSize < SNAKE_MAX_SIZE;
        boolean tailIsAvailable = shortestPathToTail.size() > 1;
        boolean appleIsAvailable = shortestPathToApple.size() > 1;
        boolean stoneIsAvailable = shortestPathToStone.size() > 1;
        boolean snakeHuge = snakeSize > SNAKE_HUGE_SIZE;
        System.out.print(",  Around apple area: " + appleArea);
        System.out.print(",  Enough place around apple: " + isEnoughPlaceAroundApple);
        System.out.print(",  Shortest path to apple: " + shortestPathToApple.size());
        System.out.print(",  Shortest path to tail: " + shortestPathToTail.size());
        System.out.print(",  Apple is available: " + appleIsAvailable);
        System.out.print(",  Tail is available: " + tailIsAvailable);
        System.out.print(",  Tail near apple: " + isTailNearApple);
        System.out.println(",  Around stone area: " + stoneArea);

        if (((isEnoughPlaceToApple
                && isEnoughPlaceAroundApple
                && appleIsAvailable
                && isSnakeSizeLessMax)
                || (tailIsAvailable && appleIsAvailable && isTailNearApple))) {
            path.addAll(shortestPathToApple);
            String dijkstraDirection = direction(head, getFirst(shortestPathToApple));
            System.out.println("Apple direction: " + dijkstraDirection);
            return dijkstraDirection;
        } else if ((isEnoughPlaceAroundStone
                && !isSnakeSizeLessMax
                && stoneIsAvailable)
                // TODO go to stone through apple
                || (snakeHuge && stoneIsAvailable && tailIsAvailable && isTailNearStone)) {
            path.addAll(shortestPathToStone);
            System.out.println("Stone direction: " + stoneDirection);
            return stoneDirection;
        } else if (snakeSize > SNAKE_KILL_SIZE && moveCounter > snakeSize * 4) {
            System.out.printf("Self killing, size: %d > moveCounter: %d\n", snakeSize, moveCounter);
            return Direction.UP.toString();
        } else if ((tailIsAvailable && (!loop || moveCounter % 4 == 0))
                  //  || (tailIsAvailable && loop && moveCounter % 4 == 0)
                    || (tailIsAvailable && board.countNear(board.getHead(), Elements.NONE) < 2)) {
            path.addAll(shortestPathToTail);
            String tailDirection = direction(head, getFirst(shortestPathToTail));
            System.out.println("Tail direction: " + tailDirection);
            return tailDirection;
        } else if (snakeSize > SNAKE_KILL_SIZE) {
            System.out.printf("Self killing, size: %d > moveCounter: %d\n", snakeSize, moveCounter);
            return Direction.UP.toString();
        } else if (loop) {
            String longestDirection = NearestEmpty.getLongDirection(board, head, tail, path);
            System.out.println("Longest direction: " + longestDirection);
            return longestDirection;
        } else {
            path.addAll(shortestPathToApple);
/*            if (BiggerArea.isAreaBetter(board, verticesWithoutHeadTailWithStone)) {
                // TODO почему то при повторном вызове меняется значение
                String biggerDirection = BiggerArea.getDirection(board, verticesWithHead);
                System.out.println("Bigger direction: " + biggerDirection);
                return biggerDirection;
            }*/

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

    private static boolean isPointsNear(Board board, Point point1, Point point2, Vertex[][] vertices) {
        Vertex destination = vertices[point2.getX()][point2.getY()];
        Vertex source = vertices[point1.getX()][point1.getY()];
        BoardUtil.clearVisiting(vertices);
        BoardUtil.computePaths(source, board);
        List<Point> shortestPathToApple = getShortestPathWithoutSource(destination);
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

    private static List<Point> getShortestPathTo(Vertex target) {
        List<Point> path = new ArrayList<>();
        for (Vertex vertex = target; vertex != null; vertex = vertex.previous) {
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
}
