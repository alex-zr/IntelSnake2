package jon.com.ua.view;

public class MatrixUtil {
    private static boolean isXOutOfBounds(int x, int width) {
        if (x < 0 || x > width) {
            return true;
        }
        return false;
    }

    private static boolean isYOutOfBounds(int y, int height) {
        if (y < 0 || y > height) {
            return true;
        }
        return false;
    }

    public static boolean isInBounds(Element element, int width, int height) {
        boolean isXInBounds = !isXOutOfBounds(element.getX(), width);
        boolean isYInBounds = !isYOutOfBounds(element.getY(), height);
        return isXInBounds && isYInBounds;
    }
}
