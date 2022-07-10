package jon.com.ua.view;

import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.services.Direction;
import jon.com.ua.client.Board;
import jon.com.ua.client.YourSolver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: al1
 * Date: 1/23/13
 */
public class View extends javax.swing.JPanel {
    public static final int CELL_SIZE = 50;
    public static final Color FIELD_COLOR = Color.LIGHT_GRAY;
    public static final Color WALL_COLOR = Color.GRAY;
    private static final int GAME_OVER_DELAY = 3000;
    public static final int SNAKE_LENGTH = 2;
    public static int DELAY = 250;

    private BoardExt board;
    //    private final Field field = new Field(FIELD_SIZE, FIELD_HEIGHT);
    List<Wall> walls = new ArrayList<>();
    private Apple apple;
    private BadApple badApple;
    private Snake snake;
    private final Random rnd = new Random();
    private String textOnCenter;
    private Solver<Board> solver;
    private int score;
    private boolean isPause;
    private PlaySound playSound;

    public View(JFrame main, Solver<Board> solver, BoardExt board) {
        this.solver = solver;
        this.board = new BoardExt();
        createWalls();
        createApple();
        createBadApple();
        snake = new Snake(SNAKE_LENGTH, this.walls, this.apple, this.badApple);
        new PlayBackground().start();
        main.addKeyListener(new KeyAdapter() {
            private int backFramePosition;
            private int framePosition;

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    isPause = !isPause;
                    if (isPause) {
                        backFramePosition = PlayBackground.clip.getFramePosition();
                        PlayBackground.clip.stop();
                        if (PlaySound.clip != null) {
                            framePosition = PlaySound.clip.getFramePosition();
                            PlaySound.clip.stop();
                        }
                    } else {
                        PlayBackground.clip.setFramePosition(backFramePosition);
                        PlayBackground.clip.start();
                        if (PlaySound.clip != null) {
                            PlaySound.clip.setFramePosition(framePosition);
                            PlaySound.clip.start();
                        }
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    DELAY += 50;
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN && DELAY >= 50) {
                    DELAY -= 50;
                }
            }
        });
        new Thread(() -> {
            while (!Thread.interrupted()) {
                if (isPause) {
                    continue;
                }
                View.this.sleep(DELAY);
                this.board.render(snake, apple, badApple, walls);
                System.out.println(this.board);
                setSnakeDirection();
                setSnakePathToTarget();
                snake.move();
                checkSnakeEatedFruit();
                if (snake.isBittenItselfOrWall()) {
                    gameOver();
                }
                repaint();
                checkSnakeDead();
            }
        }).start();
    }

    private void createWalls() {
        walls = new ArrayList<>();
        // Left
        for (int i = 0; i < BoardExt.SIZE; i++) {
            walls.add(new Wall(0, i));
        }
        // Right
        for (int i = 0; i < BoardExt.SIZE; i++) {
            walls.add(new Wall(BoardExt.SIZE - 1, i));
        }
        // Down
        for (int i = 0; i < BoardExt.SIZE; i++) {
            walls.add(new Wall(i, BoardExt.SIZE - 1));
        }
        // Up
        for (int i = 0; i < BoardExt.SIZE; i++) {
            walls.add(new Wall(i, 0));
        }
    }

    private void setSnakeDirection() {
        try {
            Direction direction = Direction.valueOf(solver.get(board));
            snake.setDirection(direction);
        } catch (IllegalArgumentException e) {
            // TODO print illegal direction
            e.printStackTrace();
        }
    }

    private void setSnakePathToTarget() {
//        snake.setPath(controller.getPath());
    }

    private Apple checkSnakeEatedFruit() {
        Apple eatedApple = tryToEatFruitAndGet();
        if (eatedApple != null) {
            incraseScore(eatedApple.getSCORE());
//            removeEatedFruit();
            createApple();
            snake.grow();
            // For disable event sound
            playSound = new PlaySound();
            playSound.start();
        }
        return null;
    }

    private void removeEatedFruit() {
        this.apple = null;
    }

    private void incraseScore(int score) {
        this.score += score;
    }

    private Apple tryToEatFruitAndGet() {
        for (Element head : snake.getHeads()) {
            if (apple.getX() == head.getX() && apple.getY() == head.getY()) {
                return apple;
            }
        }
        return null;
    }

    private void createApple() {
        do {
            int rndX = rnd.nextInt(board.size());
            int rndY = rnd.nextInt(board.size());
            apple = new Apple(rndX, rndY);
        } while ((snake != null && snake.isBody(apple)) || apple.itsMe(badApple) || isWall(apple));
    }

    private void createBadApple() {
        do {
            int rndX = rnd.nextInt(board.size());
            int rndY = rnd.nextInt(board.size());
            badApple = new BadApple(rndX, rndY);
        } while ((snake != null && snake.isBody(badApple)) || badApple.itsMe(apple) || isWall(badApple));
    }

    private boolean isWall(Element element) {
        return walls.stream().anyMatch(w -> w.itsMe(element));
    }

    private void checkSnakeDead() {
        textOnCenter = null;
        Element head = snake.getHeads().peekFirst();
        if (!BoardExt.isInBounds(head, board.size(), board.size())) {
            gameOver();
        }
    }

    private void gameOver() {
        // TODO rebuild to not clear score
//        snake.hide();
        textOnCenter = "Game over";
        //System.out.println("Snake direction: " + snake.getDirection());
        repaint();
        sleep(GAME_OVER_DELAY);
        score = 0;
//        apple = null;
        snake.create(SNAKE_LENGTH);
        createApple();
        createBadApple();
    }

    private void sleep(int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> initWindow(new YourSolver(null), null));
    }

    private static void initWindow(Solver<Board> solver, Board board) {
        JFrame main = new JFrame("Intellectual snake");
        main.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        final View view = new View(main, solver, new BoardExt());

        main.setContentPane(view);
        int fieldSize = CELL_SIZE * BoardExt.SIZE;
        main.setBounds(100, 100, fieldSize - 13, fieldSize - 5);
        main.setVisible(true);
    }

    public static void runClient(Solver<Board> solver, Board board) {
        SwingUtilities.invokeLater(() -> initWindow(solver, board));
    }

    public void paintComponent(Graphics graphics) {
        int cellHeight = getHeight() / board.size();
        int cellWidth = getWidth() / board.size();
        clearScreen(graphics);
        paintGrid(graphics, cellHeight, cellWidth);
        paintWalls(graphics, cellHeight, cellWidth);
        snake.paint(graphics, cellHeight, cellWidth);
        apple.paint(graphics, cellHeight, cellWidth);
        badApple.paint(graphics, cellHeight, cellWidth);
//        snake.paintPath(g, cellHeight, cellWidth);
        paintCenterText(graphics);
        paintScore(graphics);
        paintDelay(graphics);
    }

    private void paintWalls(Graphics g, int cellHeight, int cellWidth) {
        for (Element wall : this.walls) {
            wall.paint(g, cellHeight, cellWidth, WALL_COLOR);
        }
    }

    private void paintScore(Graphics g) {
        Color tmpColor = g.getColor();
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 50, 50);
        g.setColor(tmpColor);
    }

    private void paintDelay(Graphics g) {
        Color tmpColor = g.getColor();
        g.setColor(Color.WHITE);
        g.drawString("Delay: " + DELAY, 50, 70);
        g.setColor(tmpColor);
    }

    private void paintCenterText(Graphics g) {
        if (textOnCenter == null) {
            return;
        }
        Rectangle clipBounds = g.getClipBounds();
        int fintSize = 30;
        g.setFont(new Font("Arial", Font.PLAIN, fintSize));
        Color tmpColor = g.getColor();
        g.setColor(Color.WHITE);
        g.drawString(textOnCenter, (int) (clipBounds.getWidth() / 2) - textOnCenter.length() / 2 * 30, (int) (clipBounds.getHeight() / 2));
        g.setColor(tmpColor);
    }


    private void paintGrid(Graphics g, int cellHeight, int cellWidth) {
        int x = 0;
        int y = 0;
        g.setColor(Color.BLACK);
        for (int i = 0; i < board.size(); i++) {
            g.drawLine(0, y, getWidth(), y);
            y += cellHeight;
        }
        for (int i = 0; i < board.size(); i++) {
            g.drawLine(x, 0, x, getHeight());
            x += cellWidth;
        }
    }

    private void clearScreen(Graphics g) {
        g.setColor(FIELD_COLOR);
        g.fill3DRect(0, 0, getWidth(), getHeight(), true);
    }
}
