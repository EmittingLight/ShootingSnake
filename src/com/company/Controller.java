package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Controller {
    private View view;
    private BufferedImage image;
    private static final int SQUARE_SIZE = 20;
    private static final int FIELD_WIDTH = 30;
    private static final int FIELD_HEIGHT = 30;
    private static final int SCREEN_WIDTH = SQUARE_SIZE * FIELD_WIDTH;
    private static final int SCREEN_HEIGHT = SQUARE_SIZE * FIELD_HEIGHT;
    private List<Point> snake = new ArrayList<>();
    private Direction direction = Direction.RIGHT;
    private Direction lastDirection;
    private Point apple;
    private int score = 0;
    private int snakeSpeed = 500;
    private boolean isGamePaused = false;
    private boolean isGameOver = false;
    private List<Point> poisonedApples = new ArrayList<>();
    private Point shootPoint;


    public void setView(View view) {
        this.view = view;
    }

    public void start() {
        view.create(SCREEN_WIDTH, SCREEN_HEIGHT);
        snake.add(new Point(FIELD_WIDTH / 2, FIELD_HEIGHT / 2));
        view.setScores(score);
        moveApple();
        generateImage();
        while (true) {
            sleep(snakeSpeed);
            if (isGamePaused) {
                continue;
            }
            move();
            if (isGameOver) {
                JOptionPane.showMessageDialog(null, "Game Over");
                int a = JOptionPane.showConfirmDialog(null, "Restart", null, JOptionPane.YES_NO_OPTION);
                if (a == JOptionPane.YES_OPTION) {
                    isGameOver = false;
                    snake.clear();
                    snake.add(new Point(FIELD_WIDTH / 2, FIELD_HEIGHT / 2));
                    moveApple();
                } else {
                    view.close();
                    return;
                }
            }
            generateImage();
        }
    }

    private void drawSquare(Point point, Color color) {
        for (int i = 0; i < SQUARE_SIZE; i++) {
            for (int j = 0; j < SQUARE_SIZE; j++) {
                image.setRGB(i + point.x * SQUARE_SIZE, j + point.y * SQUARE_SIZE, color.getRGB());
            }
        }
    }

    private void drawShoot(Point point) {
        for (int i = 8; i < 12; i++) {
            for (int j = 8; j < 12; j++) {
                image.setRGB(i + point.x * SQUARE_SIZE, j + point.y * SQUARE_SIZE, Color.WHITE.getRGB());
            }
        }
    }

    private void generateImage() {
        image = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);
        for (Point point : snake) {
            drawSquare(point, Color.YELLOW);
        }
        for (Point point : poisonedApples) {
            drawSquare(point, new Color(0x9944FC));

        }
        drawSquare(apple, Color.RED);
        if (shootPoint != null) {
            drawShoot(shootPoint);
        }
        view.setImage(image);
    }

    private void move() {
        Point headPoint = snake.get(snake.size() - 1);
        Point nextPoint = new Point(headPoint);
        if (direction == Direction.RIGHT) {
            nextPoint.x++;
        }
        if (direction == Direction.LEFT) {
            nextPoint.x--;
        }
        if (direction == Direction.UP) {
            nextPoint.y--;
        }
        if (direction == Direction.DOWN) {
            nextPoint.y++;
        }
        if (snake.contains(nextPoint)) {
            isGameOver = true;
            return;
        }
        snake.add(nextPoint);
        lastDirection = direction;
        if (isBorderTouched()) {
            isGameOver = true;
            return;
        }
        if (score % 100 == 0) {
            snakeSpeed = 500;
        }

        if (snake.get(snake.size() - 1).equals(apple)) {
            moveApple();
            snakeSpeed -= 30;
            score += 10;
            for (int i = 0; i < score / 10; i++) {
                createPoisonedApple();
            }

            view.setScores(score);
        } else {

            snake.remove(0);
        }

        if (poisonedApples.contains(snake.get(snake.size() - 1))) {
            isGameOver = true;
        }
    }

    public void handleKeyPress(int keyCode) {
        if (keyCode == 87 && lastDirection != Direction.DOWN) {
            direction = Direction.UP;
        }

        if (keyCode == 83 && lastDirection != Direction.UP) {
            direction = Direction.DOWN;
        }

        if (keyCode == 65 && lastDirection != Direction.RIGHT) {
            direction = Direction.LEFT;
        }

        if (keyCode == 68 && lastDirection != Direction.LEFT) {
            direction = Direction.RIGHT;
        }

        if (keyCode == 27) {
            System.exit(0);
        }

        if (keyCode == 80) {
            isGamePaused = !isGamePaused;
            view.setPauseVisible(isGamePaused);
        }

        if (keyCode == 32) {
            if (shootPoint != null) {
                return;
            }
            shootPoint = new Point(snake.get(snake.size() - 1));
            Direction shootDirection = direction;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (shootPoint.x >= 0 && shootPoint.x < FIELD_WIDTH && shootPoint.y >= 0 && shootPoint.y < FIELD_HEIGHT) {
                        boolean hasRemoved = poisonedApples.remove(shootPoint);
                        generateImage();
                        if (hasRemoved) {
                            break;
                        }
                        sleep(snakeSpeed / 3);

                        switch (shootDirection) {
                            case RIGHT:
                                shootPoint.x++;
                                break;
                            case LEFT:
                                shootPoint.x--;
                                break;
                            case DOWN:
                                shootPoint.y++;
                                break;
                            case UP:
                                shootPoint.y--;
                        }
                    }
                    shootPoint = null;
                }
            }).start();

        }
    }

    private void sleep(int value) {
        try {
            TimeUnit.MILLISECONDS.sleep(value);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private int randomNumber(int value) {
        return (int) (Math.random() * value);
    }

    private void moveApple() {
        do {
            apple = new Point(randomNumber(FIELD_WIDTH), randomNumber(FIELD_HEIGHT));
        } while (snake.contains(apple) || poisonedApples.contains(apple));
    }

    private boolean isBorderTouched() {
        Point head = snake.get(snake.size() - 1);
        return head.x < 0 || head.y < 0 || head.x >= FIELD_WIDTH || head.y >= FIELD_HEIGHT;
    }

    private void createPoisonedApple() {
        Point point;
        do {
            point = new Point(randomNumber(FIELD_WIDTH), randomNumber(FIELD_HEIGHT));
        } while (point.equals(apple) || snake.contains(point) || poisonedApples.contains(point));
        poisonedApples.add(point);

    }
}
