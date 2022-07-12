package jon.com.ua.view;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;


import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: al1
 * Date: 1/23/13
 */
public class Snake {
    private Direction direction;
    private LinkedList<Element> heads;
    private Color snakeColor = Color.CYAN;
    private boolean isGrow;
    private List<Wall> walls;
    private BadApple badApple;
    private Apple apple;
    private List<Point> path;

    public Snake(int length, List<Wall> walls, Apple apple, BadApple badApple) {
        this.walls = walls;
        this.apple = apple;
        this.badApple = badApple;
        this.direction = Direction.RIGHT;
        create(length);
    }

    public void create(int length) {
        heads = new LinkedList<>();
        int x = BoardExt.SIZE / 2 - length;
        int y = BoardExt.SIZE / 2;
        for (int i = 0; i < length; i++) {
            heads.addFirst(new Element(snakeColor, "", x++, y));
        }
        direction = Direction.RIGHT;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public LinkedList<Element> getHeads() {
        return heads;
    }

    public void paint(Graphics g, int cellHeight, int cellWidth) {
        for (Element head : heads) {
            if ((isHead(head))) {
                head.paint(g, cellHeight, cellWidth, Color.BLUE);
            } else {
                head.paint(g, cellHeight, cellWidth);
            }
        }
    }

    // TODO implement path painting
    public void paintPath(Graphics g, int cellHeight, int cellWidth, List<Point> path) {
        if (path != null) {
            this.path = path;
        }
        if (this.path == null) {
            return;
        }

        Point previous = null;
        int headX = 0;
        int headY = 0;

        for (Point current : this.path) {
            if (previous == null) {
                previous = current;
                continue;
            }
            int prevX = previous.getX() * cellWidth + cellWidth/2;
            int prevY = Element.realToScr(previous.getY()) * cellHeight + cellHeight/2;
            headX = current.getX() * cellWidth + cellWidth/2;
            headY = Element.realToScr(current.getY()) * cellHeight + cellHeight/2;
            g.setColor(Color.ORANGE);
            drawDashedLine(g, prevX, prevY, headX, headY);

            previous = current;
        }
        g.setColor(Color.RED);
        g.drawRect(headX, headY, 2, 2);
    }

    public void drawDashedLine(Graphics g, int x1, int y1, int x2, int y2) {
        Graphics2D g2d = (Graphics2D) g.create();
        Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 9 }, 0);
        g2d.setStroke(dashed);
        g2d.drawLine(x1, y1, x2, y2);
        g2d.dispose();
    }

    public void move() {
        Element lead = heads.peekFirst();
        Element newHead = getNewHead(lead);
        heads.addFirst(newHead);
        if (!isGrow) {
            heads.removeLast();
        }
        isGrow = false;
    }

    private Element getNewHead(Element lead) {
        return switch (direction) {
            case RIGHT -> new Element(snakeColor, "", lead.getX() + 1, lead.getY());
            case LEFT -> new Element(snakeColor, "", lead.getX() - 1, lead.getY());
            case UP -> new Element(snakeColor, "", lead.getX(), lead.getY() + 1);
            case DOWN -> new Element(snakeColor, "", lead.getX(), lead.getY() - 1);
            default -> null;
        };
    }

    public int size() {
        return this.heads.size();
    }

    public void hide() {
        for (Element head : heads) {
            head.setX(Integer.MAX_VALUE);
            head.setY(Integer.MAX_VALUE);
        }
    }

    public void grow() {
        isGrow = true;
    }

    public boolean isGrow() {
        return isGrow;
    }

    public boolean isBittenItselfOrWall() {
        Element lead = heads.getFirst();
        boolean isMe = heads.stream().skip(1).anyMatch(h -> h.itsMe(lead));
        boolean isWall = walls.stream().anyMatch(w -> w.itsMe(lead));
        return isMe || isWall;
    }

    // TODO refactor to itsMe on Element
    public boolean isCollision(Element lead, Element head) {
        return lead.getX() == head.getX() && lead.getY() == head.getY();
    }

    public boolean isBody(Element element) {
        if (this.heads == null) {
            return false;
        }
        for (Element head : heads) {
            if (isCollision(head, element)) {
                return true;
            }
        }
        return false;
    }

    public boolean isBodyWithoutHead(Element element) {
        for (Element head : heads) {
            if (!isHead(element) && isCollision(head, element)) {
                return true;
            }
        }
        return false;
    }

    public boolean isHead(Element element) {
        Element lead = heads.getFirst();
        return isCollision(lead, element);
    }

    public void decrease(int length) {
        if (length < size()) {
            for (int i = 0; i < length; i++) {
                this.heads.removeLast();
            }
        }
    }

    public void setPath(List<Point> path) {
        this.path = path;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Element head : heads) {
            sb.append("[" + head.getX() + "," + head.getY() + "]");
        }
        return sb.toString();
    }

}
