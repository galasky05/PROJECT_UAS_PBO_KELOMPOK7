import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SpaceInvaders extends JPanel implements ActionListener, KeyListener {
    // Konstanta untuk dimensi papan permainan
    private final int TILE_SIZE = 32;
    private final int ROWS = 16;
    private final int COLUMNS = 16;
    private final int BOARD_WIDTH = TILE_SIZE * COLUMNS;
    private final int BOARD_HEIGHT = TILE_SIZE * ROWS;

    // Gambar
    private Image shipImg;
    private Image alienImg;
    private Image alienCyanImg;
    private Image alienMagentaImg;
    private Image alienYellowImg;
    private ArrayList<Image> alienImgArray;

    // Kelas Blok untuk objek permainan
    class Block {
        int x, y, width, height;
        Image img;
        boolean alive = true; // Digunakan untuk alien
        boolean used = false; // Digunakan untuk peluru

        Block(int x, int y, int width, int height, Image img) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.img = img;
        }
    }

    // Properti kapal
    private final int SHIP_WIDTH = TILE_SIZE * 2;
    private final int SHIP_HEIGHT = TILE_SIZE;
    private final int SHIP_VELOCITY_X = TILE_SIZE;
    private int shipX = TILE_SIZE * COLUMNS / 2 - TILE_SIZE;
    private int shipY = TILE_SIZE * ROWS - TILE_SIZE * 2;
    private int shipVelocityX = TILE_SIZE;
    private Block ship;

    // Properti alien
    private ArrayList<Block> alienArray;
    private final int ALIEN_WIDTH = TILE_SIZE * 2;
    private final int ALIEN_HEIGHT = TILE_SIZE;
    private final int ALIEN_VELOCITY_X = 1;
    private final int ALIEN_VELOCITY_Y = 1;
    private int alienX = TILE_SIZE;
    private int alienY = TILE_SIZE;
    private int alienRows = 2;
    private int alienColumns = 3;
    private int alienCount = 0;
    private int alienVelocityX = 1;

    // Properti peluru
    private ArrayList<Block> bulletArray;
    private final int BULLET_WIDTH = TILE_SIZE / 8;
    private final int BULLET_HEIGHT = TILE_SIZE / 2;
    private final int BULLET_VELOCITY_Y = -10;

    // Variabel game
    private Timer gameLoop;
    private boolean gameOver = false;
    private int score = 0;

    public SpaceInvaders() {
        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        // Memuat gambar
        try {
        shipImg = new ImageIcon(getClass().getResource("./ship.png")).getImage();
        alienImg = new ImageIcon(getClass().getResource("./alien.png")).getImage();
        alienCyanImg = new ImageIcon(getClass().getResource("./alien-cyan.png")).getImage();
        alienMagentaImg = new ImageIcon(getClass().getResource("./alien-magenta.png")).getImage();
        alienYellowImg = new ImageIcon(getClass().getResource("./alien-yellow.png")).getImage();

        alienImgArray = new ArrayList<>();
        alienImgArray.add(alienImg);
        alienImgArray.add(alienCyanImg);
        alienImgArray.add(alienMagentaImg);
        alienImgArray.add(alienYellowImg);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading images: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        ship = new Block(shipX, shipY, SHIP_WIDTH, SHIP_HEIGHT, shipImg);
        alienArray = new ArrayList<>();
        bulletArray = new ArrayList<>();

        // Timer game
        gameLoop = new Timer(1000 / 60, this);
        createAliens();
        gameLoop.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        // Menggambar kapal
        g.drawImage(ship.img, ship.x, ship.y, ship.width, ship.height, null);

        // Menggambar alien
        for (int i = 0; i < alienArray.size(); i++) {
            Block alien = alienArray.get(i);
            if (alien.alive) {
                g.drawImage(alien.img, alien.x, alien.y, alien.width, alien.height, null);
            }
        }

        // Menggambar peluru
        g.setColor(Color.WHITE);
        for (int i = 0; i < bulletArray.size(); i++) {
            Block bullet = bulletArray.get(i);
            if (!bullet.used) {
                g.drawRect(bullet.x, bullet.y, bullet.width, bullet.height);
                // g.fillRect(bullet.x, bullet.y, bullet.width, bullet.height);
            }
        }

        // Menggambar skor
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf((int) score), 10, 35);
        }
        else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }

    private void move() {
        // Pergerakan alien
        try {
        for (int i = 0; i < alienArray.size(); i++) {
            Block alien = alienArray.get(i);
            if (alien.alive) {
                alien.x += alienVelocityX;
                //if alien touches the borders
                if (alien.x + alien.width >= BOARD_WIDTH || alien.x <= 0) {
                    alienVelocityX *= -1;
                    alien.x += alienVelocityX*2;

                    //move all aliens up by one row
                    for (int j = 0; j < alienArray.size(); j++) {
                        alienArray.get(j).y += ALIEN_HEIGHT;
                    }
                }

                if (alien.y >= ship.y) {
                    gameOver = true;
                }
            }
        }
                 

        // Pergerakan peluru
        for (int i = 0; i < bulletArray.size(); i++) {
            Block bullet = bulletArray.get(i);
            bullet.y += BULLET_VELOCITY_Y;

            //bullet collision with aliens
            for (int j = 0; j < alienArray.size(); j++) {
                Block alien = alienArray.get(j);
                if (!bullet.used && alien.alive && detectCollision(bullet, alien)) {
                    bullet.used = true;
                    alien.alive = false;
                    alienCount--;
                    score += 100;
                }
            }
        }

            //clear bullets
            while (bulletArray.size() > 0 && (bulletArray.get(0).used || bulletArray.get(0).y < 0)) {
                bulletArray.remove(0); //removes the first element of the array
            }
            

       

        // Level berikutnya
        if (alienCount == 0) {
            //increase the number of aliens in columns and rows by 1
            score += alienColumns * alienRows * 100; //bonus points :)
            alienColumns = Math.min(alienColumns + 1, COLUMNS/2 -2); //cap at 16/2 -2 = 6
            alienRows = Math.min(alienRows + 1, ROWS-6);  //cap at 16-6 = 10
            alienArray.clear();
            bulletArray.clear();
            createAliens();
        }
    } catch (Exception e) {
        System.err.println("Error during game logic update: " + e.getMessage());
    }
}

    private void createAliens() {
        Random random = new Random();
        for (int c = 0; c < alienColumns; c++) {
            for (int r = 0; r < alienRows; r++) {
                int randomImgIndex = random.nextInt(alienImgArray.size());
                Block alien = new Block(
                    alienX + c * ALIEN_WIDTH,
                    alienY + r * ALIEN_HEIGHT,
                    ALIEN_WIDTH,
                    ALIEN_HEIGHT,
                    alienImgArray.get(randomImgIndex)
                );
                alienArray.add(alien);
            }
        }
        alienCount = alienArray.size();
    }

    private boolean detectCollision(Block a, Block b) {
        return a.x < b.x + b.width &&
               a.x + a.width > b.x &&
               a.y < b.y + b.height &&
               a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        if (gameOver) { //any key to restart
            ship.x = shipX;
            bulletArray.clear();
            alienArray.clear();
            gameOver = false;
            score = 0;
            alienColumns = 3;
            alienRows = 2;
            alienVelocityX = 1;
            createAliens();
            gameLoop.start();
        }
        else if (e.getKeyCode() == KeyEvent.VK_LEFT  && ship.x - shipVelocityX >= 0) {
            ship.x -= shipVelocityX; //move left one tile
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT  && ship.x + shipVelocityX + ship.width <= BOARD_WIDTH) {
            ship.x += shipVelocityX; //move right one tile
        }
        else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            //shoot bullet
            Block bullet = new Block(ship.x + SHIP_WIDTH*15/32, ship.y, BULLET_WIDTH, BULLET_HEIGHT, null);
            bulletArray.add(bullet);
        }
    }
}


