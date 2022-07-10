package jon.com.ua.view;

import com.codenjoy.dojo.services.Direction;

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
//    private List<Vertex> path;

    public Snake(int length, List<Wall> walls, Apple apple, BadApple badApple) {
        this.walls = walls;
        this.apple = apple;
        this.badApple = badApple;
        this.direction = Direction.RIGHT;
        create(length);
    }

    public void create(int length) {
        heads = new LinkedList<>();
        int x = BoardExt.SIZE / 2;
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
/*    public void paintPath(Graphics g, int cellHeight, int cellWidth) {
        Vertex previous = null;
        int headX = 0;
        int headY = 0;
        if (path == null) {
            return;
        }
        for (Vertex current : path) {
            if (previous == null) {
                previous = current;
                continue;
            }
            int prevX = previous.getElement().getX() * cellWidth + cellWidth/2;
            int prevY = previous.getElement().getY() * cellHeight + cellHeight/2;
            headX = current.getElement().getX() * cellWidth + cellWidth/2;
            headY = current.getElement().getY() * cellHeight + cellHeight/2;
            g.setColor(Color.ORANGE);
            drawDashedLine(g, prevX, prevY, headX, headY);

            previous = current;
        }
        g.setColor(Color.RED);
        g.drawRect(headX, headY, 2, 2);
    }*/

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
        Element newHead = null;
        switch (direction) {
            case RIGHT:
                newHead = new Element(snakeColor, "", lead.getX() + 1, lead.getY());
                break;
            case LEFT:
                newHead = new Element(snakeColor, "", lead.getX() - 1, lead.getY());
                break;
            case UP:
                newHead = new Element(snakeColor, "", lead.getX(), lead.getY() + 1);
                break;
            case DOWN:
                newHead = new Element(snakeColor, "", lead.getX(), lead.getY() - 1);
                break;
        }
        return newHead;
    }

    public int size() {
        return this.heads.size();
    }

    public void hide() {
        for (int i = 0; i < heads.size(); i++) {
            Element head = heads.get(i);
            head.setX(Integer.MAX_VALUE);
            head.setY(Integer.MAX_VALUE);
        }
    }

    public void grow() {
        isGrow = true;
    }

    public boolean isBittenItselfOrWall() {
        Element lead = heads.getFirst();
        boolean isMe = heads.stream().skip(1).anyMatch(h -> h.itsMe(lead));
/*
        for (int i = 1; i < heads.size(); i++) {
            Element head = heads.get(i);
            if (lead.itsMe(head)) {
                return true;
            }
        }
*/
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

/*    public boolean isBody(Vertex vertex) {
        return isHead(vertex.getElement());
    }*/

    public Element getLead() {
        return heads.getFirst();
    }

    public boolean isHead(Element element) {
        Element lead = heads.getFirst();
        return isCollision(lead, element);
    }

/*    public boolean isDeadGlass(Element nextElement, DirectionCompass compass) {
        boolean isGlass = true;
        for (Element element : compass.asList()) {
            if (!isBody(element)) {
                isGlass = false;
                break;
            }
        }
        if (isGlass) {
            System.out.println("---- Dead glass ----");
        }
        return isGlass;
    }*/

/*    public void setPath(List<Vertex> path) {
        this.path = path;
    }*/

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Element head : heads) {
            sb.append("[" + head.getX() + "," + head.getY() + "]");
        }
        return sb.toString();
    }

    public void decrease(int length) {
        if (length < size()) {
            for (int i = 0; i < length; i++) {
                this.heads.removeLast();
            }
        }
    }
}
