package jon.com.ua.pathfind;

import com.codenjoy.dojo.services.Direction;

/**
 * Created with IntelliJ IDEA.
 * User: al1
 * Date: 8/21/13
 */
public interface DirectionSupplier {
    Direction getDirection();

    default void setDirection(Direction direction) {}

//    default List<Vertex> getPath() {
//        return Collections.emptyList();
//    }
}
