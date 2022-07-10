package jon.com.ua.view;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: al1
 * Date: 1/23/13
 */
public class Element {
    private int x;
    private int y;
    private Color color;
    private String name;

    public Element(Color color, String name, int x, int y) {
        this.name = name;
        this.color = color;
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void paint(Graphics g, int cellHeight, int cellWidth) {
        paint(g, cellHeight, cellWidth, this.color);
    }

    public void paint(Graphics g, int cellHeight, int cellWidth, Color color) {
        int x = getX() * cellWidth;
        int y = realToScr(getY()) * cellHeight;

        g.setColor(color);
        g.fill3DRect(x,  y, cellWidth, cellHeight, true);
    }

    private int realToScr(int y) {
        return BoardExt.SIZE - y - 1;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Element element = (Element) o;

        if (x != element.x) return false;
        if (y != element.y) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    @Override
    public String toString() {
        return
                "x=" + x +
                ", y=" + y;
    }

    public boolean itsMe(Element element) {
        return element != null && x == element.getX() && y == element.getY();
    }
}
