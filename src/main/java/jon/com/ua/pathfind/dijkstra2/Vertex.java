package jon.com.ua.pathfind.dijkstra2;

import jon.com.ua.view.Element;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: al1
 * Date: 8/23/13
 */
public class Vertex implements Comparable<Vertex> {
    private final Element element;
    private List<Edge> adjacencies = new ArrayList<Edge>();
    private double minDistance = Double.POSITIVE_INFINITY;
    private Vertex previous;

    public Vertex(Element element) {
        this.element = element;
    }

    public Vertex(int i, int j) {
        this(new Element(Color.BLACK, "", i, j));
    }

    public Element getElement() {
        return element;
    }

    public List<Edge> getAdjacencies() {
        return adjacencies;
    }

    public double getMinDistance() {
        return minDistance;
    }

    public void setMinDistance(double minDistance) {
        this.minDistance = minDistance;
    }

    public Vertex getPrevious() {
        return previous;
    }

    public void setPrevious(Vertex previous) {
        this.previous = previous;
    }

    public int compareTo(Vertex other) {
        return Double.compare(minDistance, other.minDistance);
    }

    @Override
    public String toString() {
        return "{" +
                element +
                '}';
    }
}