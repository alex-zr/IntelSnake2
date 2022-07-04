package jon.com.ua.view;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: al1
 * Date: 8/20/13
 */
public class Fruit extends Element {
    private static final int SCORE = 100;
    private static Color fruitColor = Color.PINK;

    public Fruit(int x, int y) {
        super(fruitColor, "", x, y);
    }

    public int getSCORE() {
        return SCORE;
    }
}
