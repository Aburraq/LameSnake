import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;


public class GamePanel extends JPanel implements ActionListener {

    BufferedImage headImage;
    BufferedImage [] appleImage;

    static final int SCREEN_WIDTH = 900;
    static final int SCREEN_HEIGHT = 900;

    static final int UNIT_SIZE = 25;

    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;

    static final int DELAY = 75;

    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];

    int bodyParts = 6;

    int applesEaten;

    int appleX;

    int appleY;

    char direction = 'R';

    boolean running = false;

    Timer timer;

    Random random;

    int currentAppleIndex;




    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());

        try {
            headImage = ImageIO.read(new File("src/main/resources/cropped.jpg")); // Replace "your_head_image.png" with the actual path or URL
        } catch (IOException e) {
            e.getMessage();
        }

        appleImage = new BufferedImage[3];
        for (int i = 0; i<appleImage.length; i++){
            try {
                appleImage[i] = ImageIO.read(new File("src/main/resources/apple" + (i +1) + ".png"));
            } catch (IOException e){
                e.getMessage();
            }
        }

        startGame();
        this.requestFocusInWindow();

    }

    public void startGame() {
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();

    }

    public void resetGame(){
        bodyParts = 6;
        applesEaten = 0;
        direction = 'R';
        running = false;
        currentAppleIndex = 0;

        for (int i = 0; i < bodyParts; i++) {
            x[i] = 0;
            y[i] = 0;
        }

        startGame();
    }

    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        draw(g);

    }

    public void draw(Graphics g) {

        BufferedImage image = null;

        if (running) {
            g.setColor(Color.black);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {



                if (i == 0) {
                    g.drawImage(headImage, x[i], y[i], UNIT_SIZE, UNIT_SIZE, null);
                } else {
                    g.setColor(new Color(45, 180, 0));
                    g.setColor(new Color(random.nextInt(25), random.nextInt(255), random.nextInt(5)));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }

            g.setColor(Color.red);
            g.setFont(new Font("Blackadder ITC", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString(
                    "Score: " + applesEaten,
                    (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2,
                    g.getFont().getSize());

            BufferedImage randomAppleImage = appleImage[currentAppleIndex];
            g.drawImage(randomAppleImage, appleX, appleY, UNIT_SIZE, UNIT_SIZE, null);
        } else {
            gameOver(g);
        }



    }

    public void newApple() {

        boolean appleOnSnake = true;

        while (appleOnSnake) {


            appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
            appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;

            appleOnSnake = false;
            for (int i = 0; i< bodyParts;i++){
                if (appleX == x[i] && appleY== y[i]){
                    appleOnSnake = true;
                    break;
                }
            }


            currentAppleIndex = random.nextInt(appleImage.length);
        }
    }

    public void move() {

        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];

        }

        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;

        }

    }

    public void checkApple() {

        if ((x[0] == appleX) && (y[0] == appleY)){
            bodyParts++;
            applesEaten++;
            newApple();
        }

    }

    public void checkCollisions() {

        // Checks if head collides with body.
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }
        // Checks if head touches left border.

        if (x[0] < 0) {
            running = false;
        }

        if (x[0] > SCREEN_WIDTH) {
            running = false;
        }

        if (y[0] < 0) {
            running = false;
        }

        if (y[0] > SCREEN_HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }

    }

    public void gameOver(Graphics g) {

        g.setColor(Color.red);
        g.setFont(new Font("Blackadder ITC", Font.BOLD, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over",
                (SCREEN_WIDTH - metrics.stringWidth("Game Over"))/2,
                SCREEN_HEIGHT/2);

        g.setColor(Color.red);
        g.setFont(new Font("Blackadder ITC", Font.BOLD, 40));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString(
                "Final Score: "+applesEaten,
                (SCREEN_WIDTH - metrics2.stringWidth(" Final Score: "+applesEaten))/2,
                g.getFont().getSize());

        g.setColor(Color.white);
        g.setFont(new Font("Blackadder ITC", Font.BOLD, 30));
        FontMetrics metrics3 = getFontMetrics(g.getFont());
        g.drawString("Press Space to Eat More",
                (SCREEN_WIDTH - metrics3.stringWidth("Press Space to Eat More")) / 2,
                SCREEN_HEIGHT / 2 + 50);


    }


    /*

    class MyKeyAdapter extends KeyAdapter {
        LinkedList<Character> directionQueue = new LinkedList<>();

        @Override
        public void keyPressed(KeyEvent e) {
            char newDirection = 0;

            switch (e.getKeyCode()) {
                case KeyEvent.VK_DOWN:
                    newDirection = 'D';
                    break;
                case KeyEvent.VK_UP:
                    newDirection = 'U';
                    break;
                case KeyEvent.VK_LEFT:
                    newDirection = 'L';
                    break;
                case KeyEvent.VK_RIGHT:
                    newDirection = 'R';
                    break;
                case KeyEvent.VK_SPACE:
                    if (!running) {
                        resetGame();
                    }
                    break;
            }

            if (newDirection != 0 && isValidDirection(newDirection)) {
                directionQueue.clear();
                directionQueue.add(newDirection);
            }
        }

        private boolean isValidDirection(char newDirection) {
            if (directionQueue.size() > 1) {
                char lastDirection = directionQueue.get(directionQueue.size() - 1);
                return !(newDirection == 'L' && lastDirection == 'R' ||
                        newDirection == 'R' && lastDirection == 'L' ||
                        newDirection == 'D' && lastDirection == 'U' ||
                        newDirection == 'U' && lastDirection == 'D');
            } else {
                return true;
            }
        }
    }

*/


    /*

    class MyKeyAdapter extends KeyAdapter {
        LinkedList<Character> directionQueue = new LinkedList<>();
        private boolean isProcessingDirection = false;

        @Override
        public void keyPressed(KeyEvent e) {
            char newDirection = 0;

            switch (e.getKeyCode()) {
                case KeyEvent.VK_DOWN:
                    if (newDirection  != 'U') {
                        newDirection = 'D';}
                    break;
                case KeyEvent.VK_UP:
                    if (newDirection  != 'D') {
                        newDirection = 'U';}
                    break;
                case KeyEvent.VK_LEFT:
                    if (newDirection  != 'R') {
                        newDirection = 'L';}
                    break;
                case KeyEvent.VK_RIGHT:
                    if (newDirection  != 'L') {
                        newDirection = 'R';}
                    break;
                case KeyEvent.VK_SPACE:
                    if (!running) {
                        resetGame();
                    }
                    break;
            }

            if (newDirection != 0) {
                directionQueue.add(newDirection);
            }

            processDirectionQueue();
        }



        private void processDirectionQueue() {
            if (!isProcessingDirection && !directionQueue.isEmpty()) {
                isProcessingDirection = true;
                char newDirection = directionQueue.remove();
                if (isValidDirection(newDirection)) {
                    direction = newDirection;
                }
                isProcessingDirection = false;
            }
        }

        private boolean isValidDirection(char newDirection) {

            if (directionQueue.size() > 1) {
                return !(newDirection == 'L' && direction == 'R' ||
                        newDirection == 'R' && direction == 'L' ||
                        newDirection == 'D' && direction == 'U' ||
                        newDirection == 'U' && direction == 'D');
            } else {
                return true;
            }
        }
    }

/*
          class MyKeyAdapter extends KeyAdapter {

              LinkedList<Character> directionQueue = new LinkedList<>();


            @Override
            public void keyPressed(KeyEvent e) {

                char newDirection = 0;

                switch (e.getKeyCode()){
                    case KeyEvent.VK_DOWN:
                        newDirection = 'D';
                        break;
                    case KeyEvent.VK_UP:
                        newDirection = 'U';
                        break;
                    case KeyEvent.VK_LEFT:
                        newDirection = 'L';
                        break;
                    case KeyEvent.VK_RIGHT:
                        newDirection = 'R';
                        break;
                    case KeyEvent.VK_SPACE:
                        if (!running){
                            resetGame();
                        }
                        break;
                }

                if (newDirection != 0){
                    directionQueue.add(newDirection);
                } processDirectionQueue();




                /*
                switch (e.getKeyCode()){
                    case KeyEvent.VK_LEFT:
                        if (direction != 'R') {
                            direction = 'L';
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (direction  != 'L') {
                            direction = 'R';
                        }
                        break;
                    case KeyEvent.VK_UP:
                        if (direction  != 'D') {
                            direction = 'U';
                        }
                        break;

                    case KeyEvent.VK_DOWN:
                        if (direction  != 'U') {
                            direction = 'D';
                        }
                        break;
                    case KeyEvent.VK_SPACE:
                        if (!running){
                            resetGame();
                        }
                        break;

                }



            }
        }



        */

    class MyKeyAdapter extends KeyAdapter {


        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;

                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
                case KeyEvent.VK_SPACE:
                    if (!running) {
                        resetGame();
                    }
                    break;

            }

        }
    }
        @Override
    public void actionPerformed(ActionEvent e) {

        if (running) {
            move();
            checkApple();
            checkCollisions();

        }
        repaint();
    }



}

