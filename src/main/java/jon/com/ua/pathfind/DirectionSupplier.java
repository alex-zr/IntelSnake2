package jon.com.ua.pathfind;

import jon.com.ua.view.Direction;
import jon.com.ua.pathfind.dijkstra2.Vertex;

import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: al1
 * Date: 8/21/13
 */
public interface DirectionSupplier {
    Direction getDirection();

    default void setDirection(Direction direction) {}

    default List<Vertex> getPath() {
        return Collections.emptyList();
    }
}
