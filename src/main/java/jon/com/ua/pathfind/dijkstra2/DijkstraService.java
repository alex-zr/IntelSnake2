package jon.com.ua.pathfind.dijkstra2;

import jon.com.ua.view.Element;
import jon.com.ua.view.Field;
import jon.com.ua.view.Snake;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: al1
 * Date: 8/21/13
 */
public class DijkstraService {
    public static int size = 6;

    public static Vertex[][] createGraph(Snake snake, Field field) {
        Vertex[][] nodes = new Vertex[field.getHeight()][field.getWidth()]; //instance variable

        for (int i = 0; i < nodes.length; i++) {
            for (int j = 0; j < nodes[0].length; j++) {
                Element element = new Element(Color.WHITE, "empty", j, i);
                if (!snake.isBodyWithoutHead(element)) {
                    nodes[i][j] = new Vertex(element);
                }
            }
        }

        for (int i = 0; i < nodes.length; i++) {
            for (int j = 0; j < nodes[0].length; j++) {
                if (nodes[i][j] != null) {
                    addNeighborEdges(nodes, i, j);
                }
            }
        }

        return nodes;
    }

    private static void addNeighborEdges(Vertex[][] nodes, int y, int x) {
        Vertex currentVertex = nodes[y][x];
        if (currentVertex != null) {
            if ((x - 1 >= 0 && x - 1 < nodes.length) && !currentVertex.getAdjacencies().contains(new Edge(nodes[y][x - 1], 1))) {
                currentVertex.getAdjacencies().add(new Edge(nodes[y][x - 1], 1));
            }
            if ((x + 1 >= 0 && x + 1 < nodes.length) && !currentVertex.getAdjacencies().contains(new Edge(nodes[y][x + 1], 1))) {
                currentVertex.getAdjacencies().add(new Edge(nodes[y][x + 1], 1));
            }
            if ((y - 1 >= 0 && y - 1 < nodes.length) && !currentVertex.getAdjacencies().contains(new Edge(nodes[y - 1][x], 1))) {
                currentVertex.getAdjacencies().add(new Edge(nodes[y - 1][x], 1));
            }
            if ((y + 1 >= 0 && y + 1 < nodes.length) && !currentVertex.getAdjacencies().contains(new Edge(nodes[y + 1][x], 1))) {
                currentVertex.getAdjacencies().add(new Edge(nodes[y + 1][x], 1));
            }
        }
    }
}
