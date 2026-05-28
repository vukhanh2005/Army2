package mobiarmy.war.Boss.bullet;
import mobiarmy.war.Gun;
public class ItemLandBullet extends Bullet {
    public ItemLandBullet(Gun gun, int att, int x, int y, int vx, int vy) {
        super(gun, 6, att, x, y, vx, vy, gun.windX * 70 / 100, gun.windY * 70 / 100, 90);
    }
    @Override
    public void nextXY() {
        super.nextXY();
    }
}