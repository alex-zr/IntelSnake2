package jon.com.ua.pathfind.dijkstra2;

import jon.com.ua.view.Element;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: al1
 * Date: 8/23/13
 */
public class Dijkstra {
    public static void computePaths(Vertex source) {
        if (source == null) {
            System.out.println();
        }
        source.setMinDistance(0);
        PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>();
        vertexQueue.add(source);

        while (!vertexQueue.isEmpty()) {
            Vertex current = vertexQueue.poll();

            // Visit each edge exiting current
            for (Edge edge : current.getAdjacencies()) {
                Vertex neighbor = edge.target;
                double weight = edge.weight;
                double distanceToNeighbor = current.getMinDistance() + weight;
                if (neighbor != null && distanceToNeighbor < neighbor.getMinDistance()) {
                    vertexQueue.remove(neighbor);
                    neighbor.setMinDistance(distanceToNeighbor);
                    neighbor.setPrevious(current);
                    vertexQueue.add(neighbor);
                }
            }
        }
    }

    public static List<Vertex> getShortestPathTo(Vertex target) {
        List<Vertex> path = new ArrayList<Vertex>();
        for (Vertex vertex = target; vertex != null; vertex = vertex.getPrevious())
            path.add(vertex);
        Collections.reverse(path);
        return path;
    }

    public static void main(String[] args) {
        Vertex v0 = new Vertex(new Element(Color.BLACK, "0", 3,4));//"Redvile");
        Vertex v1 = new Vertex(new Element(Color.BLACK, "1", 3,4));//"Blueville");
        Vertex v2 = new Vertex(new Element(Color.BLACK, "2", 3,4));//"Greenville");
        Vertex v3 = new Vertex(new Element(Color.BLACK, "3", 3,4));//"Orangeville");
        Vertex v4 = new Vertex(new Element(Color.BLACK, "4", 3,4));//"Purpleville");

/*        v0.getAdjacencies().addAll(Arrays.asList(
                new Edge(v1, 5),
                new Edge(v2, 10),
                new Edge(v3, 8)));
        v1.getAdjacencies().addAll(Arrays.asList(
                new Edge(v0, 5),
                new Edge(v2, 3),
                new Edge(v4, 7)));
        v2.getAdjacencies().addAll(Arrays.asList(
                new Edge(v0, 10),
                new Edge(v1, 3)));
        v3.getAdjacencies().addAll(Arrays.asList(
                new Edge(v0, 8),
                new Edge(v4, 2)));
        v4.getAdjacencies().addAll(Arrays.asList(
                new Edge(v1, 7),
                new Edge(v3, 2)));*/
        v0.getAdjacencies().addAll(Arrays.asList(
                new Edge(v1, 1),
                new Edge(v2, 1),
                new Edge(v3, 1)));
        v1.getAdjacencies().addAll(Arrays.asList(
                new Edge(v0, 1),
                new Edge(v2, 1),
                new Edge(v4, 1)));
        v2.getAdjacencies().addAll(Arrays.asList(
                new Edge(v0, 1),
                new Edge(v1, 1)));
        v3.getAdjacencies().addAll(Arrays.asList(
                new Edge(v0, 1),
                new Edge(v4, 1)));
        v4.getAdjacencies().addAll(Arrays.asList(
                new Edge(v1, 1),
                new Edge(v3, 1)));
        computePaths(v0);
        List<Vertex> path = getShortestPathTo(v4);
        System.out.println("Path: " + path);
    }
}
