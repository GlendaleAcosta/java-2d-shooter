import com.sun.corba.se.impl.orbutil.graph.Graph;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class Game extends Canvas implements Runnable{

    private static final long serialVersionUID = 1L;
    private boolean isRunning = false;
    private Thread thread;
    private Handler handler;
    private BufferedImage level = null;
    private BufferedImage spriteSheetImg = null;
    private BufferedImage floor = null;
    private SpriteSheet sprites;
    private Camera camera;
    public int hp = 100;

    public Game() {
        new Window(1000, 563, "Wizard Game", this);
        start();

        handler  = new Handler();
        camera = new Camera(0,0);
        this.addKeyListener(new KeyInput(handler));

        BufferedImageLoader loader = new BufferedImageLoader();
        BufferedImage imageOfLevel = loader.loadImage("/Wizard_Level2.png");
        spriteSheetImg = loader.loadImage("/sprite_sheet.png");

        sprites = new SpriteSheet(spriteSheetImg);

        floor = sprites.grabImage(4, 2, 32, 32);

        this.addMouseListener(new MouseInput(handler, camera, sprites));
        loadLevel(imageOfLevel);
    }

    private void start() {
        isRunning = true;
        thread = new Thread(this);
        thread.start();
    }

    private void stop() {
        isRunning = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        // Game Loop
        this.requestFocus();
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        while(isRunning) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while(delta >= 1) {
                tick();
                delta--;
            }
            render();
            frames++;
            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                frames = 0;
            }
        }
        stop();
    }

    public void tick() {
        // gets updated 60x a second

        for(int i = 0; i < handler.objects.size(); i++) {
            if (handler.objects.get(i).getId() == ID.Player) {

                camera.tick(handler.objects.get(i));
            }
        }
        handler.tick();
    }

    public void render() {
        // gets updated a couple 1000 times a second

        // Preload 3 frames in every iteration to make game run smoothly
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();
        Graphics2D g2d = (Graphics2D) g;
        /// DRAW HERE ////////////////////////////////////////////

//        g.setColor(Color.BLACK);
//        g.fillRect(0,0,1000, 563);

        g2d.translate(-camera.getX(), -camera.getY());

        for(int x = 0; x < 30 * 72; x+=32) {
            for(int y = 0; y < 30 * 72; y+=32) {
                g.drawImage(floor, x, y, null);
            }
        }
        handler.render(g);

        g2d.translate(camera.getX(), camera.getY());

        g.setColor(Color.gray);
        g.fillRect(5, 5, 200, 32);
        g.setColor(Color.green);
        g.fillRect(5, 5, hp* 2, 32);
        g.setColor(Color.black);
        g.drawRect(5, 5, 200, 32);


        //////////////////////////////////////////////////////////
        g.dispose();
        bs.show();
    }

    private void loadLevel(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();
        for(int x = 0; x < w; x++) {
            for(int y = 0; y < h; y++) {
                int pixel = image.getRGB(x, y);
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;

                if (red == 255) {
                    handler.addObject(new Block(x * 32, y * 32, ID.Block, sprites));
                }
                if (blue == 255) {
s
                    handler.addObject(new Wizard(x * 32, y * 32, ID.Player, handler, this, sprites));
                }
                if (green == 255) {
                    handler.addObject(new Enemy(x * 32, y * 32, ID.Enemy, handler, sprites));
                }
            }
        }
    }

    public static void main(String args[]) {
        new Game();
    }
}
