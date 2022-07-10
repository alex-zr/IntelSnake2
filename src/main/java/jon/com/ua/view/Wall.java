package jon.com.ua.view;

import java.awt.*;

// TODO implement walls support
public class Wall extends Element {
    private static Color color = Color.GRAY;

    public Wall(int x, int y) {
        super(color, "", x, y);
    }
}
