package mobiarmy.war.Boss.bullet;
import mobiarmy.war.Gun;
public class RocketExpBullet extends Bullet {
    private final int d;
    private int nD;
    public RocketExpBullet(Gun gun, int att, int x, int y, int vx, int vy,int d) {
        super(gun, 27, att, x, y, vx, vy, 0, 0, 0);
        super.isCanCollisionMap = false;
        super.isCanCollisionPlayer = false;
        this.d = d;
        this.nD = 0;
    }
    @Override
    public void nextXY() {
        double x1 = 0, y1 = 0, x2 = 0, y2 = 0;
        super.nextXY();
        if (Math.abs(super.bX - super.x0) >= 50) {
            super.vy = -super.vy;
        }
        int distance = (int) Math.sqrt(Math.pow(super.bX - super.x0, 2) + Math.pow(super.bY - super.y0, 2));
        if (d <= distance) {
            super.collect = true;
            super.frames.add(new Bullet.Frame(super.frame, super.bX, super.bY, super.bX - super.xOld, super.bY - super.yOld));
            if(this.isCanCollision) {
                this.collision();
            }
        }
    }
}