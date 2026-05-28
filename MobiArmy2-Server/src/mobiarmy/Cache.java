package mobiarmy;
import com.google.gson.Gson;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import mobiarmy.server.DBManager;
import mobiarmy.server.GameData;
public class Cache {
    public static void main(String[] args) {
            DBManager army = new DBManager("jdbc:mysql://localhost:3306/army", "root", "vantu");
            DBManager dbarmy2 = new DBManager("jdbc:mysql://localhost:3306/dbarmy2", "root", "vantu");
            try {
                if (false) {
                    HashMap<String, Integer> map = new HashMap<>();
                    DataInputStream msg = new DataInputStream(new ByteArrayInputStream(GameData.getCache("equipdata2.rs")));
                    for (int i = 0; i < 36; i++) {
                        byte b = msg.readByte();
                        if (b == 10) {
                        }
                    }
                    int nglass = msg.readByte();
                    System.out.println("nglass: " + nglass);
                    for (int i = 0; i < nglass; i++) {
                        int glass = msg.readByte();
                        int maxDame = msg.readShort();
                        System.out.println("glass: " + glass + ", maxDame: " + maxDame);
                        HashMap<String, Object> glassData = new HashMap<>();
                        glassData.put("id", glass);
                        glassData.put("att", maxDame);
                        army.updateWithMap("UPDATE glass _SET_ WHERE id = ?", glassData, glass);
                        int ntype = msg.readByte();
                        System.out.println("ntype: " + ntype);
                        for (int j = 0; j < ntype; j++) {
                            int type = msg.readByte();
                            byte nid = msg.readByte();
                            System.out.println("type: " + type + ", nid: " + nid);
                            for (int k = 0; k < nid; k++) {
                                int equipId = msg.readShort();
                                int bullet = type == 0 ? msg.readByte() : -1;
                                int icon = msg.readShort();
                                int level = msg.readByte();
                                System.out.println("equipId: " + equipId + ", icon: " + icon + ", level: " + level);
                                short x[] = new short[6];
                                short y[] = new short[6];
                                byte w[] = new byte[6];
                                byte h[] = new byte[6];
                                byte dx[] = new byte[6];
                                byte dy[] = new byte[6];
                                for (int l = 0; l < 6; l++) {
                                    x[l] = msg.readShort();
                                    y[l] = msg.readShort();
                                    w[l] = msg.readByte();
                                    h[l] = msg.readByte();
                                    dx[l] = msg.readByte();
                                    dy[l] = msg.readByte();
                                }
                                byte[] inv_ability = new byte[5];
                                byte[] inv_percen = new byte[5];
                                for (int m = 0; m < 5; m++) {
                                    inv_ability[m] = msg.readByte();
                                    inv_percen[m] = msg.readByte();
                                }
                                HashMap<String, Object> equipData = new HashMap<>();
                                equipData.put("glassId", glass);
                                equipData.put("id", equipId);
                                equipData.put("type", type);
                                equipData.put("bullet", bullet);
                                equipData.put("icon", icon);
                                equipData.put("level", level);
                                equipData.put("x", new Gson().toJson(x));
                                equipData.put("y", new Gson().toJson(y));
                                equipData.put("w", new Gson().toJson(w));
                                equipData.put("h", new Gson().toJson(h));
                                equipData.put("dx", new Gson().toJson(dx));
                                equipData.put("dy", new Gson().toJson(dy));
                                equipData.put("inv_ability", new Gson().toJson(inv_ability));
                                equipData.put("inv_percen", new Gson().toJson(inv_percen));
                                equipData.put("vip", 0);
                                equipData.put("date", 30);
                                equipData.put("luong", -1);
                                equipData.put("xu", -1);
                                equipData.put("name", null);
                                army.insertWithMap("equip", equipData);
                                if (map.containsKey(glass+" "+equipId)) {
                                    System.err.println("Không có dòng nào được tác động\n"+ equipData.toString());
                                    System.exit(0);
                                }
                                map.put(glass+" "+equipId, glass);
                            }
                        }
                    }
                } else {
                    ArrayList<DBManager.DataRow> rows;
                    rows = dbarmy2.selectColumnName("SELECT * FROM equip WHERE onSale = 1");
                    for (DBManager.DataRow row : rows) {
                        army.update("UPDATE equip SET name = ?, date = ?, luong = ?, xu = ?, vip = ? WHERE glassID  = ? AND type = ? AND id = ? ",
                                row.getString("name"),
                                row.getByte("hanSD"),
                                row.getInt("giaLuong"),
                                row.getInt("giaXu"),
                                row.getBoolean("isSet"),
                                row.getByte("nv"),
                                row.getByte("equipType"),
                                row.getShort("equipId"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}