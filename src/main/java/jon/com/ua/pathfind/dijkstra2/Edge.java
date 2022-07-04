package jon.com.ua.pathfind.dijkstra2;

import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 * User: al1
 * Date: 8/23/13
 */
class Edge {
    public final Vertex target;
    public final double weight;

    public Edge(Vertex argTarget, double argWeight) {
        target = argTarget;
        weight = argWeight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        if (target == null) {
            return false;
        }
        return target.equals(edge.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(target);
    }
}
