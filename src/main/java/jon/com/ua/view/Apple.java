package jon.com.ua.view;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: al1
 * Date: 8/20/13
 */
public class Apple extends Element {
    private static final int SCORE = 100;
    private static Color color = Color.PINK;

    public Apple(int x, int y) {
        super(color, "", x, y);
    }

    public int getSCORE() {
        return SCORE;
    }
}
