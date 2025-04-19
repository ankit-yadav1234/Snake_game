import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class SnakeGame extends Applet implements Runnable, KeyListener {
    int width = 600, height = 400;
    int boxSize = 20;
    int rows = height / boxSize;
    int cols = width / boxSize;

    ArrayList<Point> snake;
    Point food;
    int dirX = 1, dirY = 0; // Initially moving right
    boolean running = true;
    boolean gameOver = false;

    public void init() {
        setSize(width, height);
        snake = new ArrayList<>();
        snake.add(new Point(5, 5));
        placeFood();
        addKeyListener(this);
        Thread t = new Thread(this);
        t.start();
    }

    public void paint(Graphics g) {
        // Background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);

        if (gameOver) {
            g.setColor(Color.RED);
            g.drawString("GAME OVER! Press R to Restart", width / 2 - 80, height / 2);
            return;
        }

        // Draw food
        g.setColor(Color.GREEN);
        g.fillRect(food.x * boxSize, food.y * boxSize, boxSize, boxSize);

        // Draw snake
        g.setColor(Color.YELLOW);
        for (Point p : snake) {
            g.fillRect(p.x * boxSize, p.y * boxSize, boxSize - 1, boxSize - 1);
        }
    }

    public void run() {
        while (true) {
            if (running && !gameOver) {
                moveSnake();
                repaint();
            }
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {}
        }
    }

    public void moveSnake() {
        Point head = snake.get(0);
        Point newHead = new Point(head.x + dirX, head.y + dirY);

        // Collision with wall or self
        if (newHead.x < 0 || newHead.y < 0 || newHead.x >= cols || newHead.y >= rows || snake.contains(newHead)) {
            gameOver = true;
            return;
        }

        snake.add(0, newHead);

        // Eating food
        if (newHead.equals(food)) {
            placeFood();
        } else {
            snake.remove(snake.size() - 1); // Move forward
        }
    }

    public void placeFood() {
        Random rand = new Random();
        int fx, fy;
        do {
            fx = rand.nextInt(cols);
            fy = rand.nextInt(rows);
        } while (snake.contains(new Point(fx, fy)));
        food = new Point(fx, fy);
    }

    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (gameOver && code == KeyEvent.VK_R) {
            restartGame();
        }

        if (!gameOver) {
            if (code == KeyEvent.VK_UP && dirY != 1) {
                dirX = 0; dirY = -1;
            } else if (code == KeyEvent.VK_DOWN && dirY != -1) {
                dirX = 0; dirY = 1;
            } else if (code == KeyEvent.VK_LEFT && dirX != 1) {
                dirX = -1; dirY = 0;
            } else if (code == KeyEvent.VK_RIGHT && dirX != -1) {
                dirX = 1; dirY = 0;
            }
        }
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}

    public void restartGame() {
        snake.clear();
        snake.add(new Point(5, 5));
        dirX = 1;
        dirY = 0;
        gameOver = false;
        placeFood();
        repaint();
    }
}
