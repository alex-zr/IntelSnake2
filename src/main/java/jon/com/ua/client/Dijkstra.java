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
public class Dijkstra { // 2.57(130, 140), 2.63(100, 110), 3.13 - 2.99 (80, 90), 3.31 - 3.17 (70, 80), 3.10 - 3.04 (60, 70), 2.87 - 2.95 (50, 60)
    //70, 80 - 31 - 10898
    //100, 110 - 31 - 9533
    //80, 90


    public static final int SNAKE_MAX_SIZE = 50; // TODO change 100
    public static final int SNAKE_HUGE_SIZE = 60; // TODO change 110
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

    public static String getDirection(Board board, List<Point> path, int moveCounter) {
        int snakeSize = board.getSnake().size();
        boolean loop = (snakeSize > Dijkstra.SNAKE_MAX_SIZE / 2
                && moveCounter > snakeSize * 1.5) || moveCounter > snakeSize * 3;
        if (loop) {
            System.out.println("--- Loop ---");
        }
        Vertex[][] verticesWithHeadWithoutTailStone = BoardUtil.createGraph(board, true, false, false);
        Point stone = board.getStones().get(0);
        Point head = board.getHead();
        Point apple = board.getApples().get(0);
        Point tail = BoardUtil.getTail(board);
        // if snake without tail after reduction
        tail = tail == null ? head : tail;
        System.out.println("Tail:" + tail);
        // TODO камень можно добавлять через создания графа
        addPoint(verticesWithHeadWithoutTailStone, stone);
        Vertex[][] verticesWithoutHeadTailWithStone = BoardUtil.createGraph(board, false, false, true);
        Vertex[][] verticesWithoutTailWithHeadStone = BoardUtil.createGraph(board, true, false, true);
        Vertex[][] verticesWithoutHeadWithTailStone = BoardUtil.createGraph(board, false, true, true);
        Vertex[][] verticesWithoutHeadStoneWithTail = BoardUtil.createGraph(board, false, true, false);
        boolean isTailNearApple = loop
                ? isPointsNear(board, apple, tail, verticesWithoutHeadWithTailStone)
                : isPointsNear(board, apple, tail, verticesWithoutHeadStoneWithTail);
        // TODO review this hack with Rebuild new graph
        verticesWithoutHeadWithTailStone = BoardUtil.createGraph(board, false, true, true);
        List<Point> shortestPathToStoneFromTail = getShortestPathWithoutSource(board, stone, tail, verticesWithoutHeadWithTailStone);
        //verticesWithoutHeadWithTailStone = BoardUtil.createGraph(board, false, true, true);
        List<Point> shortestPathToAppleWithStone = getShortestPathWithoutSource(board, head, apple, verticesWithoutTailWithHeadStone);
        boolean isTailNearStone = !shortestPathToStoneFromTail.isEmpty();
        Vertex headVertex = verticesWithHeadWithoutTailStone[head.getX()][head.getY()];
        Vertex appleVertex = verticesWithHeadWithoutTailStone[apple.getX()][apple.getY()];
        Vertex stoneVertex = verticesWithHeadWithoutTailStone[stone.getX()][stone.getY()];

        BoardUtil.computePaths(headVertex, board);
        List<Point> shortestPathToApple = getShortestPathTo(appleVertex);
        List<Point> shortestPathToStone = getShortestPathTo(stoneVertex);
        List<Point> shortestPathToTail = BoardUtil.getPathToTail(board, head,
                BoardUtil.getAnyEmptyDesirableWithoutStone(board, tail, loop), moveCounter);
        int appleArea = BoardUtil.calcVertices(apple, verticesWithoutHeadTailWithStone);
        BoardUtil.clearVisiting(verticesWithHeadWithoutTailStone);
        int stoneArea = BoardUtil.calcVertices(stone, verticesWithoutHeadTailWithStone);
//        String biggerDirection = BiggerArea.getDirection(board, verticesWithHeadWithoutTailStone);
//        String areaDirection = biggerDirection;
        String stoneDirection = direction(head, getFirst(shortestPathToStone));
//        System.out.println("Stone path: " + shortestPathToStone);
        System.out.print(",  Snake size: " + snakeSize);
//        System.out.print(",  Area direction: " + areaDirection);
        Point nextStepToApple = shortestPathToApple.size() < 2 ? null : getFirst(shortestPathToApple);
//        Point nextStepToTail = shortestPathToTail.size() < 2 ? null : getFirst(shortestPathToTail);
        int toAppleArea = nextStepToApple == null ? 0 : BoardUtil.calcVertices(nextStepToApple, verticesWithHeadWithoutTailStone);
//        int toTailArea = nextStepToTail == null ? 0 : BoardUtil.calcVertices(nextStepToTail, verticesWithHeadWithoutTailStone);


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
            if (loop && !shortestPathToAppleWithStone.isEmpty()) {
                path.addAll(shortestPathToAppleWithStone);
                String dijkstraDirection = direction(head, shortestPathToAppleWithStone.get(0));
                System.out.println("Apple direction with stone: " + dijkstraDirection);
                return dijkstraDirection;
            } else {
                path.addAll(shortestPathToApple);
                String dijkstraDirection = direction(head, getFirst(shortestPathToApple));
                System.out.println("Apple direction: " + dijkstraDirection);
                return dijkstraDirection;
            }
        } else if ((isEnoughPlaceAroundStone
                && !isSnakeSizeLessMax
                && stoneIsAvailable)
                // TODO go to stone through apple
                || (snakeHuge && stoneIsAvailable && tailIsAvailable && isTailNearStone)) {
            path.addAll(shortestPathToStone);
            System.out.println("Stone direction: " + stoneDirection);
            return stoneDirection;
        } else if (snakeSize > SNAKE_MAX_SIZE && moveCounter > snakeSize * 4) {
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
            //tail = BoardUtil.getAnyEmptyDesirableWithoutStone(board, tail, loop);
            String longestDirection = NearestEmpty.getLongDirection(board, head, tail, path, loop, shortestPathToTail);
            System.out.println("Longest direction: " + longestDirection);
            return longestDirection;
        } else {
            path.addAll(shortestPathToApple);
            Vertex bettrVertex = BiggerArea.isAreaBetter(board, verticesWithoutHeadTailWithStone);
            if (bettrVertex != null) {
                String biggerDirection = direction(headVertex.point, bettrVertex.point);
                System.out.println("Bigger direction: " + biggerDirection);
                return biggerDirection;
            }

            String awayDirection = AwayFromTail.getDirection(board, verticesWithHeadWithoutTailStone);
            System.out.println("Away direction: " + awayDirection);
            return awayDirection;
        }


        /*else {
            //path.addAll(shortestPathToApple);
            areaDirection = BiggerArea.getDirection(board, verticesWithHeadWithoutTailStone);
            String nearestDirection1 = NearestEmpty.getDirection(board, shortestPathToApple);
            return nearestDirection1;
        }*/
    }

    private static boolean isPointsNear(Board board, Point point1, Point point2, Vertex[][] vertices) {
        List<Point> shortestPathToApple = getShortestPathWithoutSource(board, point1, point2, vertices);
        return !shortestPathToApple.isEmpty();
    }
    private static List<Point> getShortestPathWithoutSource(Board board, Point point1, Point point2, Vertex[][] vertices) {
        Vertex destination = vertices[point2.getX()][point2.getY()];
        Vertex source = vertices[point1.getX()][point1.getY()];
        BoardUtil.clearVisiting(vertices);
        BoardUtil.computePaths(source, board);
        return getShortestPathWithoutSource(destination);

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
