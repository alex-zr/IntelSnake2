package jon.com.ua.view.snake;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import jon.com.ua.view.Apple;
import jon.com.ua.view.BadApple;
import jon.com.ua.view.BoardExt;
import jon.com.ua.view.Element;
import jon.com.ua.view.Wall;


import java.awt.*;
import java.util.LinkedList;
import java.util.List;

import static com.codenjoy.dojo.games.snake.Element.HEAD_DOWN;
import static com.codenjoy.dojo.games.snake.Element.HEAD_LEFT;
import static com.codenjoy.dojo.games.snake.Element.HEAD_RIGHT;
import static com.codenjoy.dojo.games.snake.Element.HEAD_UP;
import static com.codenjoy.dojo.games.snake.Element.NONE;
import static com.codenjoy.dojo.games.snake.Element.TAIL_END_DOWN;
import static com.codenjoy.dojo.games.snake.Element.TAIL_END_LEFT;
import static com.codenjoy.dojo.games.snake.Element.TAIL_END_RIGHT;
import static com.codenjoy.dojo.games.snake.Element.TAIL_END_UP;
import static com.codenjoy.dojo.games.snake.Element.TAIL_HORIZONTAL;
import static com.codenjoy.dojo.games.snake.Element.TAIL_LEFT_DOWN;
import static com.codenjoy.dojo.games.snake.Element.TAIL_LEFT_UP;
import static com.codenjoy.dojo.games.snake.Element.TAIL_RIGHT_DOWN;
import static com.codenjoy.dojo.games.snake.Element.TAIL_RIGHT_UP;
import static com.codenjoy.dojo.games.snake.Element.TAIL_VERTICAL;
import static com.codenjoy.dojo.services.Direction.DOWN;
import static com.codenjoy.dojo.services.Direction.LEFT;
import static com.codenjoy.dojo.services.Direction.RIGHT;
import static com.codenjoy.dojo.services.Direction.UP;
import static jon.com.ua.view.snake.BodyDirection.*;

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

    public boolean isBodyWithoutHeadAndTail(Element element) {
        for (Element head : heads) {
            if (!isHead(element) && isCollision(head, element) && !isCollision(head, getTail())) {
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

    public BodyDirection getBodyDirection(Element curr) {
        int currIndex = heads.indexOf(curr);
        Element prev = heads.get(currIndex - 1);
        Element next = heads.get(currIndex + 1);

        BodyDirection nextPrev = orientation(next, prev);
        if (nextPrev != null) {
            return nextPrev;
        }

        if (orientation(prev, curr) == HORIZONTAL) {
            boolean clockwise = curr.getY() < next.getY() ^ curr.getX() > prev.getX();
            if (curr.getY() < next.getY()) {
                return (clockwise)?TURNED_RIGHT_UP:TURNED_LEFT_UP;
            } else {
                return (clockwise)?TURNED_LEFT_DOWN:TURNED_RIGHT_DOWN;
            }
        } else {
            boolean clockwise = curr.getX() < next.getX() ^ curr.getY() < prev.getY();
            if (curr.getX() < next.getX()) {
                return (clockwise)?TURNED_RIGHT_DOWN:TURNED_RIGHT_UP;
            } else {
                return (clockwise)?TURNED_LEFT_UP:TURNED_LEFT_DOWN;
            }
        }
    }

    public BodyDirection orientation(Element curr, Element next) {
        if (curr.getX() == next.getX()) {
            return VERTICAL;
        } else if (curr.getY() == next.getY()) {
            return HORIZONTAL;
        } else {
            return null;
        }
    }

    public Element getTail() {
        return heads.getLast();
    }

    public Direction getTailDirection() {
        Element prev = heads.get(1);
        Element tail = getTail();

        if (prev.getX() == tail.getX()) {
            return (prev.getY() < tail.getY())?UP:DOWN;
        } else {
            return (prev.getX() < tail.getX())?RIGHT:LEFT;
        }
    }

    public com.codenjoy.dojo.games.snake.Element getTailLastElement() {
        return getTailLastElement(getTailDirection());
    }

    public com.codenjoy.dojo.games.snake.Element getTailLastElement(Direction direction) {
        switch (direction) {
        case DOWN : return TAIL_END_DOWN;
        case UP : return TAIL_END_UP;
        case LEFT : return TAIL_END_LEFT;
        case RIGHT : return TAIL_END_RIGHT;
        default : return NONE;
        }
    }

    public com.codenjoy.dojo.games.snake.Element getHead() {
        return getHead(getDirection());
    }

    public com.codenjoy.dojo.games.snake.Element getHead(Direction direction) {
        switch (direction) {
        case DOWN : return HEAD_DOWN;
        case UP : return HEAD_UP;
        case LEFT : return HEAD_LEFT;
        case RIGHT : return HEAD_RIGHT;
        default : return NONE;
        }
    }

    public com.codenjoy.dojo.games.snake.Element getBody(Element element) {
        return getBody(getBodyDirection(element));
    }

    public com.codenjoy.dojo.games.snake.Element getBody(BodyDirection bodyDirection) {
        switch (bodyDirection) {
        case HORIZONTAL : return TAIL_HORIZONTAL;
        case VERTICAL : return TAIL_VERTICAL;
        case TURNED_LEFT_DOWN : return TAIL_LEFT_DOWN;
        case TURNED_LEFT_UP : return TAIL_LEFT_UP;
        case TURNED_RIGHT_DOWN : return TAIL_RIGHT_DOWN;
        case TURNED_RIGHT_UP : return TAIL_RIGHT_UP;
        default : return NONE;
        }
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
