package jon.com.ua.view;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: al1
 * Date: 7/10/22
 */

public class Wall extends Element {
    private static Color color = Color.GRAY;

    public Wall(int x, int y) {
        super(color, "", x, y);
    }
}
