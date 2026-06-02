package mobiarmy.war.Boss.bullet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import mobiarmy.server.GameData;
import mobiarmy.war.Gun;
import mobiarmy.war.Player;
public class MirrorBullet extends Bullet {
    public MirrorBullet(Gun gun, int att, int x, int y, int vx, int vy) {
        super(gun, 49, att, x, y, vx, vy, gun.windX * 40 / 100, gun.windY * 40 / 100, 70);
    }
    @Override
    public void nextXY() {
        super.nextXY();
        if (super.collect) {
            super.frames.get(super.frames.size() - 1).jumpX = 0;
            super.frames.get(super.frames.size() - 1).jumpY = 0;
        } else if(super.vy >= 0) {
            this.frames.add(new Bullet.Frame(this.frame, this.bX, this.bY, this.bX - this.xOld, this.bY - this.yOld));
            int ang = GameData.getArg(super.lastX - super.frames.get(0).fX, super.frames.get(0).fY - super.lastY);
            super.vx = (gun.force * GameData.cos(ang) >> 10);
            super.vy = (gun.force * GameData.sin(ang) >> 10);
            if (super.vx != 0) {
                while (Math.abs(super.vx) < 15) {
                    super.vx += super.vx;
                    super.vy += super.vy;
                }
            }
            int vx0 = super.vx;
            int vy0 = super.vy;
            boolean hit = false;
            while(true) {
                if((super.bX < -100) || (super.bX > super.gun.mapData.width + 100) || (super.bY > super.gun.mapData.height + 100)) {
                    break;
                }
                int nextX = super.bX + super.vx;
                int nextY = super.bY + super.vy;
                int[] collision = super.getCollision(super.bX, super.bY, nextX, nextY);
                if(collision != null) {
                    super.bX = collision[0];
                    super.bY = collision[1];
                    hit = true;
                    break;
                } else {
                    this.xOld = this.bX;
                    this.yOld = this.bY;
                    this.lastX = this.bX = nextX;
                    this.lastY = this.bY = nextY;
                }
            }
            super.frames.add(new Bullet.Frame(super.frame, super.bX, super.bY, vx0, -vy0));
            super.collect = true;
            if(hit && super.isCanCollision) {
                super.collision();
            }
        }
    }
}
