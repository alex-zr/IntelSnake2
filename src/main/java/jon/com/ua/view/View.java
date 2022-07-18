package jon.com.ua.view;

import com.codenjoy.dojo.services.Direction;
import jon.com.ua.client.Board;
import jon.com.ua.client.Elements;
import jon.com.ua.client.YourSolver;
import jon.com.ua.view.snake.Snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

/**
 * Created with IntelliJ IDEA.
 * User: al1
 * Date: 1/23/13
 */
public class View extends javax.swing.JPanel {
    public static final int CELL_SIZE = 30;
    public static final Color FIELD_COLOR = Color.LIGHT_GRAY;
    public static final Color WALL_COLOR = Color.GRAY;
    private static final int GAME_OVER_DELAY = 2000;
    public static final int SNAKE_LENGTH = 2;
    public static final int DECREASE_LENGTH = 10;
    public static int DELAY = 400;

    public static boolean muteSound = true;
    public boolean isEditMode = false;
    public boolean isPaintSprites = false;
    public boolean isPaintPath = true;
    private BoardExt board;
    private List<Wall> walls = new ArrayList<>();
    private Apple apple;
    private BadApple badApple;
    private Snake snake;
    private final Random rnd = new Random();
    private String textOnCenter;
    private YourSolver solver;
    private int score;
    private boolean isPause;
    private PlaySound playSound;
    private int backFramePosition;
    private int framePosition;

    public View(JFrame main, YourSolver solver) {
        this.solver = solver;
        this.board = new BoardExt();
        createWalls();
        createApple();
        createBadApple();
        snake = new Snake(SNAKE_LENGTH, this.walls, this.apple, this.badApple, this.board);
        //new PlayBackground().start();
        if (PlayBackground.clip != null) {
            if (!muteSound) {
                startSound();
            } else {
                stopSound();
            }
        }
        main.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    isPause = !isPause;
                    if (!muteSound) {
                        if (PlayBackground.clip == null) {
                            return;
                        }
                        if (isPause) {
                            stopSound();
                        } else {
                            startSound();
                        }
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    DELAY -= 50;
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN && DELAY >= 50) {
                    DELAY += 50;
                }
                if (e.getKeyCode() == KeyEvent.VK_E) {
                    isEditMode = !isEditMode;
                    isPause = isEditMode;
                    if (!isEditMode) {
                        createApple();
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_S) {
                    isPaintSprites = !isPaintSprites;
                }
                if (e.getKeyCode() == KeyEvent.VK_P) {
                    isPaintPath = !isPaintPath;
                }
                if (e.getKeyCode() == KeyEvent.VK_M) {
                    muteSound = !muteSound;
                    if (!muteSound) {
                        startSound();
                    } else {
                        stopSound();
                    }
                }
            }


        });
        main.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int y = BoardElement.realToScr(e.getY() / CELL_SIZE);
                int x = e.getX() / CELL_SIZE;
                BoardElement boardElement = new BoardElement(null, null, x, y);
                if (!isWall(boardElement) && !snake.isBody(boardElement) && !badApple.itsMe(boardElement)) {
                    createApple(x, y);
                    isPause = false;
                }
            }
        });
        new Thread(() -> {
            while (!Thread.interrupted()) {
                if (isPause) {
                    continue;
                }
                this.board.render(snake, apple, badApple, walls);
                System.out.println(this.board);
                setSnakeDirection();
                setSnakePathToTarget();
                repaint();
                View.this.sleep(DELAY);
                snake.move();
                checkSnakeEatApple();
                checkSnakeEatBadApple();
                if (snake.isBittenItselfOrWall()) {
                    gameOver();
                }
                checkSnakeDead();
            }
        }).start();
    }

    private void startSound() {
        PlayBackground.clip.setFramePosition(backFramePosition);
        PlayBackground.clip.start();
        if (PlaySound.clip != null) {
            PlaySound.clip.setFramePosition(framePosition);
            PlaySound.clip.start();
        }
    }

    private void stopSound() {
        backFramePosition = PlayBackground.clip.getFramePosition();
        PlayBackground.clip.stop();
        if (PlaySound.clip != null) {
            framePosition = PlaySound.clip.getFramePosition();
            PlaySound.clip.stop();
        }
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
            String directionName = Optional.ofNullable(
                    solver.get((Board) board.clone())
            ).orElse(Direction.LEFT.toString());
            Direction direction = Direction.valueOf(directionName);
            snake.setDirection(direction);
        } catch (IllegalArgumentException | CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    private void setSnakePathToTarget() {
        snake.setPath(solver.getPath());
    }

    private BoardElement checkSnakeEatApple() {
        BoardElement eatedApple = tryToEatAndGet(this.apple);
        if (eatedApple != null) {
            if (isEditMode) {
                isPause = true;
            } else {
                createApple();
            }
            increaseScore(this.snake.size());
            snake.grow();
            // For disable event sound
            if (!muteSound) {
                playSound = new PlaySound();
                playSound.start();
            }
        }
        return null;
    }

    private BoardElement checkSnakeEatBadApple() {
        BoardElement eatBadApple = tryToEatAndGet(this.badApple);
        if (eatBadApple != null) {
            createBadApple();
            if (this.snake.size() <= DECREASE_LENGTH) {
                gameOver();
                return eatBadApple;
            }
            snake.decrease(DECREASE_LENGTH);
            // For disable event sound
            if (!muteSound) {
                playSound = new PlaySound();
                playSound.start();
            }
        }
        return null;
    }

    private void increaseScore(int score) {
        this.score += score;
    }

    private BoardElement tryToEatAndGet(BoardElement target) {
        for (BoardElement head : snake.getHeads()) {
            if (target.getX() == head.getX() && target.getY() == head.getY()) {
                return target;
            }
        }
        return null;
    }

    private void createApple(int x, int y) {
        apple = new Apple(x, y);
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

    private boolean isWall(BoardElement boardElement) {
        return walls.stream().anyMatch(w -> w.itsMe(boardElement));
    }

    private void checkSnakeDead() {
        textOnCenter = null;
        BoardElement head = snake.getHeads().peekFirst();
        if (!BoardExt.isInBounds(head, board.size(), board.size())) {
            gameOver();
        }
    }

    private void gameOver() {
        textOnCenter = "Game over";
        repaint();
        sleep(GAME_OVER_DELAY);
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

    private static void initWindow(YourSolver solver, Board board) {
        JFrame main = new JFrame("Intellectual snake");
        main.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        final View view = new View(main, solver);

        main.setContentPane(view);
        int fieldSize = CELL_SIZE * BoardExt.SIZE;
        main.setBounds(100, 100, fieldSize - 13, fieldSize - 5);
        main.setVisible(true);
    }

    public static void runClient(YourSolver solver, Board board) {
        SwingUtilities.invokeLater(() -> initWindow(solver, board));
    }

    @Override
    public void paintComponent(Graphics graphics) {
        if (isPaintSprites) {
            paintSprites(graphics);
        } else {
            paintSimple(graphics);
        }
    }

    private void paintSimple(Graphics graphics) {
        int cellHeight = getHeight() / board.size();
        int cellWidth = getWidth() / board.size();
        clearScreen(graphics, null);
        paintGrid(graphics, cellHeight, cellWidth);
        paintWalls(graphics, cellHeight, cellWidth, null);
        snake.paint(graphics, cellHeight, cellWidth, false);
        apple.paint(graphics, cellHeight, cellWidth, null);
        if (isPaintPath) {
            snake.paintPath(graphics, cellHeight, cellWidth, null);
        }
        badApple.paint(graphics, cellHeight, cellWidth, null);
        paintCenterText(graphics);
        paintScore(graphics);
        paintDelay(graphics);
        paintHelp(graphics);
        if (snake.isGrow()) {
            paintPlusScore(graphics);
        }
    }

    private void paintSprites(Graphics graphics) {
        int cellHeight = getHeight() / board.size();
        int cellWidth = getWidth() / board.size();
        clearScreen(graphics, Elements.NONE);
//        paintGrid(graphics, cellHeight, cellWidth);
        paintWalls(graphics, cellHeight, cellWidth, Elements.BREAK);
        snake.paint(graphics, cellHeight, cellWidth, true);
        apple.paint(graphics, cellHeight, cellWidth, board.getAt(apple.getX(), apple.getY()));
        if (isPaintPath) {
            snake.paintPath(graphics, cellHeight, cellWidth, null);
        }
        badApple.paint(graphics, cellHeight, cellWidth, board.getAt(badApple.getX(), badApple.getY()));
        paintCenterText(graphics);
        paintScore(graphics);
        paintDelay(graphics);
        paintHelp(graphics);
        if (snake.isGrow()) {
            paintPlusScore(graphics);
        }
    }

    private void paintWalls(Graphics g, int cellHeight, int cellWidth, Elements element) {
        for (BoardElement wall : this.walls) {
            wall.paint(g, cellHeight, cellWidth, element);
        }
    }

    private void paintScore(Graphics g) {
        Color tmpColor = g.getColor();
        int fontSize = 14;
        g.setFont(new Font("Arial", Font.BOLD, fontSize));
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 10, 12);
        g.setColor(tmpColor);
    }

    private void paintDelay(Graphics g) {
        Color tmpColor = g.getColor();
        int fontSize = 14;
        g.setFont(new Font("Arial", Font.PLAIN, fontSize));
        g.setColor(Color.WHITE);
        g.drawString("Delay: " + DELAY, 10, 26);
        g.setColor(tmpColor);
    }

    private void paintHelp(Graphics g) {
        Color tmpColor = g.getColor();
        int fontSize = 14;
        g.setFont(new Font("Arial", Font.PLAIN, fontSize));
        g.setColor(Color.WHITE);
        g.drawString("Pause: space  |  Edit mode: e  |  Speed: up/down", 100, 12);
        g.drawString("Path: p | Graphic: s", 100, 26);
        g.setColor(tmpColor);
    }

    private void paintCenterText(Graphics g) {
        if (textOnCenter == null) {
            return;
        }
        Rectangle clipBounds = g.getClipBounds();
        int fontSize = 30;
        g.setFont(new Font("Arial", Font.PLAIN, fontSize));
        Color tmpColor = g.getColor();
        g.setColor(Color.WHITE);
        g.drawString(textOnCenter, (int) (clipBounds.getWidth() / 2) - textOnCenter.length() / 2 * 20, (int) (clipBounds.getHeight() / 2));
        g.setColor(tmpColor);
    }

    private void paintPlusScore(Graphics g) {
        Rectangle clipBounds = g.getClipBounds();
        int fontSize = 50;
        g.setFont(new Font("Arial", Font.BOLD, fontSize));
        Color tmpColor = g.getColor();
        g.setColor(Color.RED);
        g.drawString("+" + snake.size(), (int) (clipBounds.getWidth() / 2), (int) (clipBounds.getHeight() / 2));
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

    private void clearScreen(Graphics graphics, Elements elements) {
        if (elements != null) {
            Stream.concat(
                            board.getApples().stream(),
                            board.getBarriers().stream()
                    )
                    .map(BoardElement::of)
                    .forEach(e -> e.paintSprite(graphics, getWidth(), getHeight(), elements));
        } else {
            graphics.setColor(FIELD_COLOR);
            graphics.fill3DRect(0, 0, getWidth(), getHeight(), true);
        }
    }
}
