package jon.com.ua.view;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: al1
 * Date: 7/10/22
 */
public class BadApple extends Element {
    private static final int SCORE = 100;
    private static Color color = Color.RED;

    public BadApple(int x, int y) {
        super(color, "", x, y);
    }

    public int getSCORE() {
        return SCORE;
    }
}
