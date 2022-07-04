package jon.com.ua.pathfind.dijkstra2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: al1
 * Date: 8/24/13
 */
public class Compass {
    private Vertex up;
    private Vertex down;
    private Vertex left;
    private Vertex right;

    public Compass(Vertex up, Vertex down, Vertex left, Vertex right) {
        this.up = up;
        this.down = down;
        this.left = left;
        this.right = right;
    }

    public Vertex getUp() {
        return up;
    }

    public void setUp(Vertex up) {
        this.up = up;
    }

    public Vertex getDown() {
        return down;
    }

    public void setDown(Vertex down) {
        this.down = down;
    }

    public Vertex getLeft() {
        return left;
    }

    public void setLeft(Vertex left) {
        this.left = left;
    }

    public Vertex getRight() {
        return right;
    }

    public void setRight(Vertex right) {
        this.right = right;
    }

    @Override
    public String toString() {
        return "Compass{" +
                "up=" + up +
                ", down=" + down +
                ", left=" + left +
                ", right=" + right +
                '}';
    }

    public List<Edge> getNotNullDirections() {
        List<Edge> edges = new ArrayList<Edge>();
        if (up != null) {
            edges.add(new Edge(up, 1));
        }
        if (down != null) {
            edges.add(new Edge(down, 1));
        }
        if (left != null) {
            edges.add(new Edge(left, 1));
        }
        if (right != null) {
            edges.add(new Edge(right, 1));
        }
        return edges;
    }
}
