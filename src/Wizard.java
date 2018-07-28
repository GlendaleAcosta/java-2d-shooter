import java.awt.*;
import java.awt.image.BufferedImage;

public class Wizard extends GameObject {

    private Handler handler;
    private BufferedImage wizardImg;
    private Game game;

    public Wizard(int x, int y, ID id, Handler handler, Game game, SpriteSheet sprites) {
        super(x, y, id, sprites);
        this.handler = handler;
        this.game = game;
        wizardImg = sprites.grabImage(1, 1, 32, 48);
    }

    @Override
    public void tick() {
        x += velX;
        y += velY;

        collision();

        // Movement
        if (handler.isUp()) {
            velY = -5;
        } else if (!handler.isDown()) velY = 0;

        if (handler.isDown()) {
            velY = 5;
        } else if (!handler.isUp()) velY = 0;

        if (handler.isRight()) {
            velX = 5;
        } else if (!handler.isLeft()) velX = 0;

        if (handler.isLeft()) {
            velX = -5;
        } else if (!handler.isRight()) velX = 0;
    }

    private void collision() {
        for(int i = 0; i < handler.objects.size(); i++) {
            GameObject tempObject = handler.objects.get(i);
            if (tempObject.getId() == ID.Block) {
                if (getBounds().intersects(tempObject.getBounds())) {
                    x += velX * -1;
                    y += velY * -1;
                }
            }
            if (tempObject.getId() == ID.Enemy) {
                if (getBounds().intersects(tempObject.getBounds())) {
                    game.hp--;
                }
            }

        }
    }


    @Override
    public void render(Graphics g) {
        g.drawImage(wizardImg, x, y, null);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, 32, 48);
    }
}
