package mobiarmy.war;
import java.util.ArrayList;
import java.util.HashSet;
import mobiarmy.server.GameData;
import mobiarmy.war.Boss.bullet.*;
public class Gun {
    public MapData mapData;
    public int index;
    public int typeshoot;
    public int isPow;
    public int bulletId;
    public int gunX;
    public int gunY;
    public int ang;
    public int force;
    public int force2;
    public int nshoot;
    public ArrayList<Bullet> bullets;
    public int att;
    public byte isSuper;
    public HashSet<Player> setEff;
    public int radius;
    public int windX;
    public int windY;
    public boolean test;
    public Gun(MapData mapData, int index, boolean isPow) {
        this.mapData = mapData;
        this.index = index;
        this.bullets = new ArrayList<>();
        this.isSuper = 0;
        this.setEff = new HashSet();
        this.isPow = isPow ? 1 : 0;
        this.windX = mapData.windX;
        this.windY = mapData.windY;
        this.test = false;
    }
    public void shootBullet(int bulletId, int x, int y, int width, int height, int ang, int force, int force2, int nshoot, int att, int radius) {
        this.bulletId = bulletId;
        this.gunX = x;
        this.gunY = y;
        this.ang = ang;
        this.force = force;
        this.force2 = force2;
        this.nshoot = nshoot;
        this.radius = radius;
        this.bullets.clear();
        int bx = this.gunX + ((width - 4) * GameData.cos(ang) >> 10);
        int by = this.gunY - (height / 2) - ((height - 4) * GameData.sin(ang) >> 10);
        int vx = force * GameData.cos(ang) >> 10;
        int vy = -(force * GameData.sin(ang) >> 10);
        for (int num = 0; num < this.nshoot; num++) {
            switch (bulletId) {
                case 0 ->
                    this.bullets.add(new Bullet(this, 0, this.isPow == 1 ? att * 2 : att, bx, by, vx, vy, this.windX * 80 / 100, this.windY * 80 / 100, 100));
                case 1 -> {
                    for (int i = 0; i < (this.isPow == 1 ? 5 : 2); i++) {
                        this.bullets.add(new Bullet(this, 1, att / (this.isPow == 1 ? 3 : 2), bx, by, vx, vy, this.windX * 50 / 100, this.windY * 50 / 100, 50));
                    }
                }
                case 2 -> {
                    int lent = this.isPow == 1 ? 6 : 3;
                    int startAngle = this.ang - (lent / 2) * 5;
                    for (int i = 0; i < lent; i++) {
                        int currAng = startAngle + i * 5;
                        int bxNew = this.gunX + ((width - 4) * GameData.cos(currAng) >> 10);
                        int byNew = this.gunY - (height / 2) - ((height - 4) * GameData.sin(currAng) >> 10);
                        int vxNew = force * GameData.cos(currAng) >> 10;
                        int vyNew = -(force * GameData.sin(currAng) >> 10);
                        this.bullets.add(new Bullet(this, 2, att / 3, bxNew, byNew, vxNew, vyNew, this.windX * 80 / 100, this.windY * 80 / 100, 60));
                    }
                }
                case 4 ->
                    this.bullets.add(new ItemB52Bullet(this, 100 + (att * 2), bx, by, vx, vy));
                case 5 -> {
                    this.mapData.disableLuck = true;
                    this.bullets.add(new ItemTeleportBullet(this, 0, bx, by, vx, vy));
                }
                case 6 -> {
                    for (int i = 0; i < 3; i++) {
                        this.bullets.add(new ItemLandBullet(this, att / 3 - i, bx, by, vx, vy));
                    }
                }
                case 7 ->
                    this.bullets.add(new Bullet(this, 7, att * 2, bx, by, vx, vy, this.windX * 70 / 100, this.windY * 70 / 100, 80));
                case 8 ->
                    this.bullets.add(new ItemSilkBullet(this, att, bx, by, vx, vy));
                case 9 -> {
                    for (int i = this.ang - 6, j = 0; j < 4; i += 4, j++) {
                        int bxNew = this.gunX + ((width - 4) * GameData.cos(i) >> 10);
                        int byNew = this.gunY - (height / 2) - ((height - 4) * GameData.sin(i) >> 10);
                        int vxNew = force * GameData.cos(i) >> 10;
                        int vyNew = -(force * GameData.sin(i) >> 10);
                        this.bullets.add(new Bullet(this, 9, att / 4 * (this.isPow == 1 ? 2 : 1), bxNew, byNew, vxNew, vyNew, this.windX * 40 / 100, this.windY * 40 / 100, 90));
                    }
                }
                case 10 -> {
                    for (int i = 0; i < 3; i++) {
                        this.bullets.add(new Bullet(this, 10, att / 3 * (this.isPow == 1 ? 2 : 1), bx, by, vx, vy, this.windX * 50 / 100, this.windY * 50 / 100, 80));
                    }
                }
                case 11 -> {
                    for (int i = 0; i < 5; i++) {
                        this.bullets.add(new Bullet(this, 11, att / 5 * (this.isPow == 1 ? 2 : 1), bx, by, vx, vy, this.windX * 30 / 100, this.windY * 30 / 100, 90));
                    }
                }
                case 13 -> {
                    this.mapData.disableLuck = true;
                    this.bullets.add(new ItemTornadoBullet(this, bx, by, vx, vy));
                }
                case 14 ->
                    this.bullets.add(new ItemLaserBullet(this, att, bx, by, vx, vy));
                case 16 ->
                    this.bullets.add(new ItemBomBullet(this, att, bx, by, vx, vy));
                case 17 ->
                    this.bullets.add(new ApacheBullet(this, att / 4 * (this.isPow == 1 ? 2 : 1), bx, by, vx, vy, x, y));
                case 19 ->
                    this.bullets.add(new ChickyBullet(this, 19, att / 2 * (this.isPow == 1 ? 2 : 1), bx, by, vx, vy));
                case 21 ->
                    this.bullets.add(new TarzanBullet(this, att * (this.isPow == 1 ? 2 : 1), bx, by, vx, vy));
                case 22 ->
                    this.bullets.add(new ItemMouseBullet(this, 100 + att + (att / 2), bx, this.gunY - (height / 2), vx, vy));
                case 23 ->
                    this.bullets.add(new ItemMeteorBullet(this, att, bx, by, vx, vy));
                case 25 ->
                    this.bullets.add(new ItemNoCollisionMap(this, (int) (att * 1.5F), bx, by, vx, vy));
                case 26 ->
                    this.bullets.add(new ItemRocketBullet(this, att, bx, by, vx, vy));
                case 28 ->
                    this.bullets.add(new ItemRainBullet(this, att, bx, by, vx, vy));
                case 30 -> {
                    this.mapData.disableLuck = true;
                    this.bullets.add(new ItemEarthHoleBullet(this, this.gunX, this.gunY));
                }
                case 31 ->
                    this.bullets.add(new BigBoomBullet(this, att, this.gunX, this.gunY));
                case 32 ->
                    this.bullets.add(new SmallBoomBullet(this, att, this.gunX, this.gunY));
                case 33 -> {
                    int lent = 5;
                    for (int i = 0; i < lent; i++) {
                        this.bullets.add(new Bullet(this, bulletId, att / lent, bx, by, vx, vy, this.windX * 80 / 100, this.windY * 80 / 100, 40));
                    }
                }
                case 34 -> {
                    this.mapData.disableLuck = true;
                    this.bullets.add(new AddBoomBullet(this, 0, bx, by, vx, vy));
                }
                case 35 ->
                    this.bullets.add(new BicycleBullet(this, att, this.gunX, this.gunY));
                case 36 -> {
                    this.mapData.disableLuck = true;
                    this.bullets.add(new ItemTeleportBullet2(this, 0, bx, by, vx, vy));
                }
                case 37 ->
                    this.bullets.add(new BigRocketBullet(this, att, bx, by, force2));
                case 40 ->
                    this.bullets.add(new BigLaserBullet(this, att, bx, by, vx, vy));
                case 42 ->
                    this.bullets.add(new UfoLaserBullet(this, att * 2, this.gunX, this.gunY));
                case 49 -> {
                    vx = (force + 5) * GameData.cos(ang) >> 10;
                    vy = -((force + 5) * GameData.sin(ang) >> 10);
                    this.bullets.add(new MirrorBullet(this, att * (this.isPow == 1 ? 2 : 1), bx, by, vx, vy));
                }
                case 50 ->
                    this.bullets.add(new ItemSuicideBullet(this, 1500, this.gunX, this.gunY));
                case 51 ->
                    this.bullets.add(new ItemBlindBullet(this, att, bx, by, vx, vy));
                case 52 ->
                    this.bullets.add(new ItemEarthHole2Bullet(this, 100 + att + (att / 2), bx, by, vx, vy));
                case 53 -> {
                    this.mapData.disableLuck = true;
                    this.bullets.add(new ItemUfoBullet(this, att, this.mapData.players[this.index].hp, this.mapData.players[this.index].team, 100, 100));
                }
                case 54 ->
                    this.bullets.add(new ItemFreezeBullet(this, att, bx, by, vx, vy));
                case 55 ->
                    this.bullets.add(new ItemPoisonBullet(this, att, bx, by, vx, vy));
                case 56 -> {
                    for (int i = this.ang - 5, j = 0; j < 3; i += 5, j++) {
                        int bxNew = this.gunX + ((width - 4) * GameData.cos(i) >> 10);
                        int byNew = this.gunY - (height / 2) - ((height - 4) * GameData.sin(i) >> 10);
                        int vxNew = force * GameData.cos(i) >> 10;
                        int vyNew = -(force * GameData.sin(i) >> 10);
                        this.bullets.add(new ItemSilkBullet2(this, att, bxNew, byNew, vxNew, vyNew));
                    }
                }
                case 57 -> {
                    this.mapData.disableLuck = true;
                    this.bullets.add(new ItemTimeBombBullet(this, att * 2, bx, by, vx, vy));
                }
                default -> this.bullets.add(new Bullet(this, bulletId, this.isPow == 1 ? att * 2 : att, bx, by, vx, vy, this.windX * 80 / 100, this.windY * 80 / 100, 100));
            }
        }
    }
    public void fillXY() {
        boolean hasNext;
        do {
            hasNext = false;
            Bullet[] bs = this.bullets.toArray(Bullet[]::new);
            for (Bullet bullet : bs) {
                if (bullet.collect) {
                    continue;
                }
                hasNext = true;
                bullet.nextXY();
            }
        } while(hasNext);
    }
}