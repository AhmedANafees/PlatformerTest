
import jaco.mp3.player.MP3Player;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JFrame;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.Timer;

/**
 *
 * @author lamonta
 */
public class PlatformerTest extends JComponent implements ActionListener {

    // Height and Width of our game
    static final int WIDTH = 800;
    static final int HEIGHT = 600;

    //Title of the window
    String title = "My Game";

    // sets the framerate and delay for our game
    // this calculates the number of milliseconds per frame
    // you just need to select an approproate framerate
    int desiredFPS = 60;
    int desiredTime = Math.round((1000 / desiredFPS));

    // timer used to run the game loop
    // this is what keeps our time running smoothly :)
    Timer gameTimer;

    // YOUR GAME VARIABLES WOULD GO HERE
    Rectangle player = new Rectangle(40, 500, 80, 133);
    int moveSpeed = 5;

    int ground = 580;
    Rectangle block = new Rectangle(400, 400, 50, 50);

    boolean right = false;
    boolean left = false;
    boolean jump = false;

    int gravity = 1;
    int dy = 0;  // change in the y position
    boolean standing = true; // on the ground or not
    final int JUMP_FORCE = -10;

    final int BLOCK_TIME = 2 * desiredFPS;
    int blockTimer = BLOCK_TIME;

    BufferedImage standRPic;
    BufferedImage[] runRPic = new BufferedImage[3];
    BufferedImage jumpRPic;
    BufferedImage standLPic;
    BufferedImage[] runLPic = new BufferedImage[3];

    int frameCount = 0;
    int frameDelay = 5;
    int runDelay = 5;

    int camX = 0;

    MP3Player bgMusic = new MP3Player(new File("mario//mario theme.mp3"));
    MP3Player jumpingMusic = new MP3Player(new File("mario//jump sound.mp3"));
    // GAME VARIABLES END HERE    
    public BufferedImage load(String filename) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(filename));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    // Constructor to create the Frame and place the panel in
    // You will learn more about this in Grade 12 :)
    public PlatformerTest() {
        // creates a windows to show my game
        JFrame frame = new JFrame(title);

        // sets the size of my game
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        // adds the game to the window
        frame.add(this);

        // sets some options and size of the window automatically
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        // shows the window to the user
        frame.setVisible(true);

        // add listeners for keyboard and mouse
        frame.addKeyListener(new Keyboard());
        Mouse m = new Mouse();
        this.addMouseMotionListener(m);
        this.addMouseWheelListener(m);
        this.addMouseListener(m);

        // Set things up for the game at startup
        setup();

        // Start the game loop
        gameTimer = new Timer(desiredTime, this);
        gameTimer.setRepeats(true);
        gameTimer.start();

    }

    // drawing of the game happens in here
    // we use the Graphics object, g, to perform the drawing
    // NOTE: This is already double buffered!(helps with framerate/speed)
    public void paintComponent(Graphics g) {
        // always clear the screen first!
        g.clearRect(0, 0, WIDTH, HEIGHT);
        g.clearRect(block.x - camX, block.y, block.width, block.height);
        // GAME DRAWING GOES HERE
        Graphics2D g2d = (Graphics2D) g;

        // draw the ground
        g.setColor(Color.GREEN);
        g.fillRect(0, ground, WIDTH, 120);

        // draw the block
        g.setColor(Color.RED);
        g.fillRect(block.x - camX, block.y, block.width, block.height);
        // draw the player
        g.setColor(Color.BLACK);
        //g2d.fill(player);

        if (right && standing) {
            runDelay--;

            if (runDelay == 0) {
                frameCount++;
                runDelay = frameDelay;
            }
            if (frameCount == runRPic.length) {
                frameCount = 0;
            }
            g.drawImage(runRPic[frameCount], player.x - camX, player.y, this);
        } else if (left && standing) {
            runDelay--;

            if (runDelay == 0) {
                frameCount++;
                runDelay = frameDelay;
            }
            if (frameCount == runLPic.length) {
                frameCount = 0;
            }
            g.drawImage(runLPic[frameCount], player.x - camX, player.y, this);
        } else if (!standing) {
            g.drawImage(jumpRPic, player.x - camX, player.y, this);
        } else {
            g.drawImage(standRPic, player.x - camX, player.y, this);
        }
        // GAME DRAWING ENDS HERE
    }

    public void test(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(100, 100, 100, 100);
    }

    // This method is used to do any pre-setup you might need to do
    // This is run before the game loop begins!
    public void setup() {
        // Any of your pre setup before the loop starts should go here
        standRPic = load("mario//standR.png");
        standLPic = load("mario//standL.png");
        jumpRPic = load("mario//jumpR.png");
        for (int i = 0; i < runRPic.length; i++) {
            runRPic[i] = load("mario//runR" + i + ".png");
        }
        for (int i = 0; i < runLPic.length; i++) {
            runLPic[i] = load("mario//runL" + i + ".png");
        }
        bgMusic.setRepeat(true);
        bgMusic.play();

    }

    public void moveBlock() {
        // random position between 100 and 700
        int x = (int) (Math.random() * 600 + 100);
        block.x = x;
    }

    // The main game loop
    // In here is where all the logic for my game will go
    public void loop() {

        // player movement
        if (right) {
            player.x = player.x + moveSpeed;
        } else if (left) {
            player.x = player.x - moveSpeed;
        }

        // jumping
        if (jump && standing) {
            // make it move upwards quickly
            jumpingMusic.play();
            dy = JUMP_FORCE;
            // no longer standing
            standing = false;
        }

        // add in gravity
        dy = dy + gravity;
        player.y = player.y + dy;

        // ground collision detection
        if (player.y + player.height > ground) {
            player.y = ground - player.height;
            standing = true;
        }

        // block collision detection
        if (player.intersects(block)) {
            // get the collision rectangle
            Rectangle collide = player.intersection(block);
            // fix the smaller of height or width
            if (collide.height < collide.width) {
                // fix up or down
                // on top
                if (player.y < block.y) {
                    standing = true;
                    dy = 0;
                    // move player up on top of block
                    player.y = player.y - collide.height;
                } else {
                    dy = 0;
                    // move player down under the block
                    player.y = player.y + collide.height;
                }
            } else {
                // fix left or right
                // left
                if (player.x < block.x) {
                    // move to the left of the block
                    player.x = player.x - collide.width;
                } else {
                    // move to the right of the block
                    player.x = player.x + collide.width;
                }
            }
        }
        // move the block every 2 seconds
        if (blockTimer == 0) {
            // move block
            moveBlock();
            // reset timer
            blockTimer = BLOCK_TIME;
        } else {
            blockTimer--;
        }
        camX = player.x - WIDTH / 2;
    }

    // Used to implement any of the Mouse Actions
    private class Mouse extends MouseAdapter {

        // if a mouse button has been pressed down
        @Override
        public void mousePressed(MouseEvent e) {

        }

        // if a mouse button has been released
        @Override
        public void mouseReleased(MouseEvent e) {

        }

        // if the scroll wheel has been moved
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {

        }

        // if the mouse has moved positions
        @Override
        public void mouseMoved(MouseEvent e) {

        }
    }

    // Used to implements any of the Keyboard Actions
    private class Keyboard extends KeyAdapter {

        // if a key has been pressed down
        @Override
        public void keyPressed(KeyEvent e) {
            // get the key code
            int key = e.getKeyCode();
            // do my actions
            if (key == KeyEvent.VK_D) {
                right = true;
            } else if (key == KeyEvent.VK_A) {
                left = true;
            } else if (key == KeyEvent.VK_SPACE) {
                jump = true;
            }
        }

        // if a key has been released
        @Override
        public void keyReleased(KeyEvent e) {
            int key = e.getKeyCode();
            // do my actions
            if (key == KeyEvent.VK_D) {
                right = false;
            } else if (key == KeyEvent.VK_A) {
                left = false;
            } else if (key == KeyEvent.VK_SPACE) {
                jump = false;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        loop();
        repaint();

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // creates an instance of my game
        PlatformerTest game = new PlatformerTest();
    }
}
