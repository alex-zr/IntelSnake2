package jon.com.ua.view;

/**
 * Created with IntelliJ IDEA.
 * User: al1
 * Date: 1/23/13
 */
public class Field {
    private int fieldWidth;
    private int fieldHeight;

    public Field(int fieldWidth, int fieldHeight) {
        this.fieldWidth = fieldWidth;
        this.fieldHeight = fieldHeight;
    }

    public int getWidth() {
        return fieldWidth;
    }

    public int getHeight() {
        return fieldHeight;
    }

/*    public boolean isXOutOfBounds(int x) {
        if (x < 0 || x > fieldWidth) {
            return true;
        }
        return false;
    }

    public boolean isYOutOfBounds(int y) {
        if (y < 0 || y > fieldHeight) {
            return true;
        }
        return false;
    }*/

    public boolean isInBounds(Element element) {
        return MatrixUtil.isInBounds(element, fieldWidth, fieldHeight);
    }
}
