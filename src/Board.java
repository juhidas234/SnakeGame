import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class Board extends JPanel implements ActionListener {
    
    private Image apple;
    private Image dot;
    private Image head;
    
    private final int ALL_DOTS = 900;
    private final int DOT_SIZE = 25;
    private final int GAME_WIDTH = 300;
    private final int GAME_HEIGHT = 300;
    
    private int apple_x;
    private int apple_y;
    private int score = 0;
    
    private final int[] x = new int[ALL_DOTS];
    private final int[] y = new int[ALL_DOTS];
    
    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;
    
    private boolean inGame = true;
    
    private int dots;
    private Timer timer;
    
    public Board() {
        addKeyListener(new TAdapter());
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        setFocusable(true);
        loadImages();
        initGame();
    }
    
    public void loadImages() {
        try {
            apple = new ImageIcon(getClass().getResource("/icons/apple_icon.png")).getImage();
            dot = new ImageIcon(getClass().getResource("/icons/green_dot.png")).getImage();
            head = new ImageIcon(getClass().getResource("/icons/red_dot.png")).getImage();
        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
            // Create placeholder images
            apple = createColorImage(Color.RED);
            dot = createColorImage(Color.GREEN);
            head = createColorImage(Color.ORANGE);
        }
    }
    
    private Image createColorImage(Color color) {
        BufferedImage image = new BufferedImage(DOT_SIZE, DOT_SIZE, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(color);
        g2d.fillRect(0, 0, DOT_SIZE, DOT_SIZE);
        g2d.dispose();
        return image;
    }
    
    public void initGame() {
        dots = 3;
        score = 0;
        inGame = true;
        
        // Initialize snake position (centered)
        for (int i = 0; i < dots; i++) {
            x[i] = 100 - i * DOT_SIZE;
            y[i] = 100;
        }
        
        // Initialize directions
        leftDirection = false;
        rightDirection = true;
        upDirection = false;
        downDirection = false;
        
        locateApple();
        
        if (timer != null) {
            timer.stop();
        }
        timer = new Timer(150, this); // Slightly slower for better control
        timer.start();
    }
    
    public void locateApple() {
        int maxX = (GAME_WIDTH / DOT_SIZE) - 1;
        int maxY = (GAME_HEIGHT / DOT_SIZE) - 1;
        apple_x = (int)(Math.random() * maxX) * DOT_SIZE;
        apple_y = (int)(Math.random() * maxY) * DOT_SIZE;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (inGame) {
            drawGame(g);
        } else {
            gameOver(g);
        }
    }
    
    public void drawGame(Graphics g) {
        // Draw apple
        g.drawImage(apple, apple_x, apple_y, this);
        
        // Draw snake
        for (int i = 0; i < dots; i++) {
            if (i == 0) {
                g.drawImage(head, x[i], y[i], this);
            } else {
                g.drawImage(dot, x[i], y[i], this);
            }
        }
        
        // Draw score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("Score: " + score, 10, 15);
    }
    
    public void gameOver(Graphics g) {
        String msg = "Game Over! Score: " + score;
        String restartMsg = "Press R to restart";
        Font font = new Font("Arial", Font.BOLD, 14);
        FontMetrics metrics = getFontMetrics(font);
        
        g.setColor(Color.WHITE);
        g.setFont(font);
        g.drawString(msg, (GAME_WIDTH - metrics.stringWidth(msg)) / 2, GAME_HEIGHT/2);
        g.drawString(restartMsg, (GAME_WIDTH - metrics.stringWidth(restartMsg)) / 2, GAME_HEIGHT/2 + 25);
    }
    
    public void move() {
        // Move body parts
        for (int i = dots; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        
        // Move head based on direction
        if (leftDirection) {
            x[0] -= DOT_SIZE;
        }
        if (rightDirection) {
            x[0] += DOT_SIZE;
        }
        if (upDirection) {
            y[0] -= DOT_SIZE;
        }
        if (downDirection) {
            y[0] += DOT_SIZE;
        }
    }
    
    public void checkApple() {
        if ((x[0] == apple_x) && (y[0] == apple_y)) {
            dots++;
            score++;
            locateApple();
        }
    }
    
    public void checkCollision() {
        // Check collision with body
        for (int i = dots; i > 0; i--) {
            if ((i > 4) && (x[0] == x[i]) && (y[0] == y[i])) {
                inGame = false;
            }
        }
        
        // Check wall collision
        if (x[0] >= GAME_WIDTH || x[0] < 0 || y[0] >= GAME_HEIGHT || y[0] < 0) {
            inGame = false;
        }
        
        if (!inGame) {
            timer.stop();
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (inGame) {
            checkApple();
            move();
            checkCollision();
        }
        repaint();
    }
    
    private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            
            if (inGame) {
                if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
                    leftDirection = true;
                    upDirection = false;
                    downDirection = false;
                }
                
                if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
                    rightDirection = true;
                    upDirection = false;
                    downDirection = false;
                }
                
                if ((key == KeyEvent.VK_UP) && (!downDirection)) {
                    upDirection = true;
                    leftDirection = false;
                    rightDirection = false;
                }
                
                if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
                    downDirection = true;
                    leftDirection = false;
                    rightDirection = false;
                }
            }
            
            // Restart game
            if ((key == KeyEvent.VK_R) && !inGame) {
                initGame();
            }
        }
    }
}