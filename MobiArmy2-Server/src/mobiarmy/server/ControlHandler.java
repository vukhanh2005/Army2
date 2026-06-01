package mobiarmy.server;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import mobiarmy.Util;
import mobiarmy.io.Message;
import mobiarmy.server.DBManager.DataRow;
import static mobiarmy.server.Text.__;
import org.mindrot.jbcrypt.BCrypt;
public class ControlHandler {
    private Session session;
    private static final String PASSWORD_SECRET = "NguyenVuKhanhEni";
    public ControlHandler(Session session) {
        this.session = session;
    }
    private boolean isValidPassword(String password, String storedPassword) {
        if (password == null || storedPassword == null || storedPassword.isEmpty()) {
            return false;
        }
        try {
            return BCrypt.checkpw(passwordWithSecret(password), normalizeBCryptHash(storedPassword))
                    || BCrypt.checkpw(password, normalizeBCryptHash(storedPassword));
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }   
    private static String hashPassword(String password) {
        return BCrypt.hashpw(passwordWithSecret(password), BCrypt.gensalt(10));
    }
    private static String passwordWithSecret(String password) {
        return PASSWORD_SECRET + ":" + password;
    }
    private static String normalizeBCryptHash(String hash) {
        if (hash.startsWith("$2y$")) {
            return "$2a$" + hash.substring(4);
        }
        if (hash.startsWith("$2b$")) {
            return "$2a$" + hash.substring(4);
        }
        return hash;
    }
    private static boolean isValidUsername(String username) {
        return username != null && username.matches("^[a-zA-Z0-9_]{3,50}$");
    }
    private static boolean isValidRegisterPassword(String password) {
        return password != null && password.length() >= 3 && password.length() <= 72;
    }
    private void sendRegisterResult(boolean success, String reason) {
        try {
            Message message = new Message(121);
            message.writer().writeBoolean(success);
            if (!success) {
                message.writer().writeUTF(reason);
            }
            this.session.sendMessage(message);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public static final HashSet<Integer> syncCommands = new HashSet<>();
    static {
        syncCommands.add(-25);
        syncCommands.add(-23);
        syncCommands.add(-18);
        syncCommands.add(-4);
        syncCommands.add(-3);
        syncCommands.add(-2);
        syncCommands.add(-28);
        syncCommands.add(6);
        syncCommands.add(7);
        syncCommands.add(8);
        syncCommands.add(9);
        syncCommands.add(11);
        syncCommands.add(15);
        syncCommands.add(16);
        syncCommands.add(19);
        syncCommands.add(20);
        syncCommands.add(21);
        syncCommands.add(22);
        syncCommands.add(26);
        syncCommands.add(28);
        syncCommands.add(42);
        syncCommands.add(49);
        syncCommands.add(53);
        syncCommands.add(71);
        syncCommands.add(72);
        syncCommands.add(74);
        syncCommands.add(75);
        syncCommands.add(79);
        syncCommands.add(102);
        syncCommands.add(104);
        syncCommands.add(110);
    }
    public void handleControlMessage(Message msg) throws IOException {
        switch (msg.getCommand()) {
            case -28 -> {
                if (this.session.user != null && this.session.user.roomWait == null) {
                    this.session.sessionHandler.loadRoomInfo();
                    this.session.user.hasBeenInvited = false;
                }
            }
            case -27 -> {
                this.session.messageHandler.setKey();
            }
            case -25 -> {
                if (this.session.user != null && this.session.user.roomWait == null) {
                    byte action = msg.reader().readByte();
                    int dbKey = msg.reader().readInt()&0xFFFF;
                    if (action == 0) {
                        Equip equip = this.session.user.getEquip(dbKey);
                        if (equip != null) {
                            this.session.sessionHandler.requestRenewal(dbKey, String.format(__("Bạn có muốn gia hạn trang bị này với giá %d xu?"), equip.renewalPrice()));
                        }
                    }
                    if (action == 1) {
                        this.session.user.renewalEquip(dbKey);
                    }
                }
            }
            case -23 -> {
                if (this.session.user != null && this.session.user.roomWait == null) {
                    byte action = msg.reader().readByte();
                    if (action == 0) {
                        this.session.user.loadMission();
                    }
                    if (action == 1) {
                        this.session.user.actionMission(msg.reader().readByte());
                    }
                }
            }
            case -19 -> {
                if (this.session.user != null && this.session.user.roomWait == null) {
                    this.session.sessionHandler.loadRoomInfoName();
                }
            }
            case -14 -> {
                if (this.session.user != null && this.session.user.roomWait == null) {
                    this.session.sessionHandler.topInfo();
                }
            }
            case -4 -> {
                this.session.disconnect();
            }
            case -3 -> {
                if (this.session.user != null && this.session.user.roomWait == null) {
                    byte type = msg.reader().readByte();
                    if (type == 0) {
                        this.session.sessionHandler.shopLinhTinh(ShopLinhTinh.entrys);
                    } else {
                        this.session.user.buyLinhTinh(msg.reader().readByte(), msg.reader().readByte(), msg.reader().readByte());
                    }
                }
            }
            case -2 -> {
                if (this.session.user != null && this.session.user.roomWait == null) {
                    this.session.user.setEquipVip(msg.reader().readByte(), msg.reader().readInt()&0xFFFF);
                }
            }
            case 1 -> {
                if (this.session.user == null) {
                    String name = msg.reader().readUTF();
                    String pass = msg.reader().readUTF();
                    this.session.version = msg.reader().readUTF();
                    try {
                        ArrayList<DataRow> rows = Server.dbManager.selectColumnName("SELECT * FROM user WHERE LOWER(username) = LOWER(?)", name);
                        if (rows.isEmpty() || !isValidPassword(pass, rows.get(0).getString("password"))) {
                            this.session.sessionHandler.log(__("Thông tin tài khoản hoặc mật khẩu không chính xác."));
                        } else {
                            if (!BCrypt.checkpw(passwordWithSecret(pass), normalizeBCryptHash(rows.get(0).getString("password")))) {
                                Server.dbManager.update("UPDATE user SET password = ? WHERE id = ?", hashPassword(pass), rows.get(0).getInt("id"));
                            }
                            User user = SessionManager.findUserById(rows.get(0).getInt("id"));
                            if (user == null) {
                                user = new User(rows.get(0).getInt("id"), rows.get(0).getString("username"));
                                SessionManager.addUser(user);
                            }
                            if (user.connect(this.session)) {
                                this.session.user.glass().updateAll();
                                this.session.sessionHandler.loadInfoAll();
                                this.session.sessionHandler.layerData();
                                this.session.sessionHandler.idNotColision();
                            } else {
                                this.session.sessionHandler.log(__("Tài khoản đang được đăng nhập trên máy khác hãy thử lại sau."));
                                user.session.requestDisconnect();
                            }
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            case 121 -> {
                if (this.session.user == null) {
                    String username = msg.reader().readUTF().toLowerCase().trim();
                    msg.reader().readUTF();
                    String pass = msg.reader().readUTF();
                    try {
                        if (WebApiClient.isEnabled()) {
                            WebApiClient.RegisterResult result = WebApiClient.register(username, pass);
                            sendRegisterResult(result.success, result.message);
                        } else if (!isValidUsername(username)) {
                            sendRegisterResult(false, __("Tên tài khoản chỉ được gồm chữ, số, dấu _ và dài 3-50 ký tự."));
                        } else if (!isValidRegisterPassword(pass)) {
                            sendRegisterResult(false, __("Mật khẩu phải dài từ 3 đến 72 ký tự."));
                        } else {
                            ArrayList<DataRow> rows = Server.dbManager.selectColumnName("SELECT id FROM user WHERE LOWER(username) = LOWER(?)", username);
                            if (!rows.isEmpty()) {
                                sendRegisterResult(false, __("Tên tài khoản đã tồn tại."));
                            } else {
                                java.util.HashMap<String, Object> userValues = new java.util.HashMap<>();
                                userValues.put("username", username);
                                userValues.put("password", hashPassword(pass));
                                int userId = Server.dbManager.insertWithMap("user", userValues);
                                if (userId <= 0) {
                                    sendRegisterResult(false, __("Không thể tạo tài khoản, vui lòng thử lại."));
                                } else {
                                    User user = new User(userId, username);
                                    java.util.HashMap<String, Object> profileValues = new java.util.HashMap<>();
                                    profileValues.put("user_id", user.id);
                                    profileValues.put("name", username);
                                    profileValues.put("xu", user.xu);
                                    profileValues.put("luong", user.luong);
                                    profileValues.put("cup", user.cup);
                                    profileValues.put("glass", user.selectGlass);
                                    Server.dbManager.insertWithMap("user_", profileValues);
                                    SessionManager.addUser(user);
                                    sendRegisterResult(true, null);
                                }
                            }
                        }
                    } catch (SQLException | IOException ex) {
                        ex.printStackTrace();
                        sendRegisterResult(false, __("Không thể tạo tài khoản, vui lòng thử lại."));
                    }
                }
            }
            case 5 -> {
                if (this.session.user != null) {
                    this.session.user.sendMessage(msg.reader().readInt(), msg.reader().readUTF());
                }
            }
            case 6 -> {
                if (this.session.user != null && this.session.user.roomWait == null) {
                    this.session.sessionHandler.loadRoomInfo();
                    this.session.user.hasBeenInvited = false;
                }
            }
            case 7 -> {
                if (this.session.user != null && this.session.user.roomWait == null) {
                    this.session.user.loadRoomWaits(msg.reader().readByte());
                }
            }
            case 8 -> {
                if (this.session.user != null && this.session.user.roomWait == null) {
                    this.session.user.joinRoomWait(msg.reader().readByte(), msg.reader().readByte(), msg.reader().readUTF());
                }
            }
            case 9 -> {
                if (this.session.user != null && this.session.user.roomWait != null) {
                    this.session.user.chat(msg.reader().readUTF());
                }
            }
            case 11 -> {
                if (this.session.user != null && this.session.user.roomWait != null && !this.session.user.roomWait.started) {
                    this.session.user.kickRoomWait(msg.reader().readInt());
                }
            }
            case 15 -> {
                if (this.session.user != null && this.session.user.roomWait != null) {
                    this.session.user.leaveRoomWait();
                }
            }
            case 16 -> {
                if (this.session.user != null && this.session.user.roomWait != null && !this.session.user.roomWait.started) {
                    this.session.user.ready();
                }
            }
            case 17 -> {
                if (this.session.user != null && this.session.user.roomWait == null) {
                    byte action = msg.reader().readByte();
                    if (action == 0) {
                        this.session.user.getSelect().reset();
                        byte lent = msg.reader().readByte();
                        for (int i = 0; i < lent; i++) {
                            int id = msg.reader().readInt();
                            int num = msg.reader().readUnsignedByte();
                            if((id & 0x10000) > 0) {
                                int dbKey = id&0xFFFF;
                                this.session.user.getSelect().addElement(0, dbKey, num);
                            } else {
                                this.session.user.getSelect().addElement(1, id, num);
                            }
                        }
                        this.session.user.getSelect().make();
                    }
                    if (action == 1) {
                        this.session.user.getConfirm().confirm();
                    }
                }
            }
            case 18 -> {
                if (this.session.user != null && this.session.user.roomWait != null && !this.session.user.roomWait.started) {
                    this.session.user.changePassRoomWait(msg.reader().readUTF());
                }
            }
            case 19 -> {
                if (this.session.user != null && this.session.user.roomWait != null && !this.session.user.roomWait.started) {
                    this.session.user.changeMoneyRoomWait(msg.reader().readInt());
                }
            }
            case 20 -> {
                if (this.session.user != null && this.session.user.roomWait != null && !this.session.user.roomWait.started) {
                    this.session.user.startGame();
                }
            }
            case 21 -> {
                if (this.session.user != null && this.session.user.roomWait != null && this.session.user.roomWait.started) {
                    this.session.user.moveLocation(msg.reader().readShort(), msg.reader().readShort());
                }
            }
            case 22 -> {
                if (this.session.user != null && this.session.user.roomWait != null && this.session.user.roomWait.started) {
                    byte bulletId = msg.reader().readByte();
                    short gunX = msg.reader().readShort();
                    short gunY = msg.reader().readShort();
                    short ang = msg.reader().readShort();
                    byte force = msg.reader().readByte();
                    byte force2 = 0;
                    if(bulletId == 17 || bulletId == 19) {
                        force2 = msg.reader().readByte();
                    }
                    byte nshoot = msg.reader().readByte();
                    this.session.user.shoot(bulletId, gunX, gunY, ang, force, force2, nshoot);
                }
            }
            case 26 -> {
                if (this.session.user != null && this.session.user.roomWait != null && this.session.user.roomWait.started) {
                    this.session.user.useItem(msg.reader().readByte());
                }
            }
            case 28-> {
                if (this.session.user != null && this.session.user.roomWait == null) {
                    this.session.user.joinAnyBoard(msg.reader().readByte());
                }
            }
            case 29 -> {
                if (this.session.user != null) {
                    this.session.user.loadFriend();
                }
            }
            case 32 -> {
                if (this.session.user != null) {
                    this.session.user.addFriend(msg.reader().readInt());
                }
            }
            case 33 -> {
                if (this.session.user != null) {
                    this.session.user.deleteFriend(msg.reader().readInt());
                }
            }
            case 34 -> {
                if (this.session.user != null) {
                    int id = msg.reader().readInt();
                    if(id < -1) {
                        User user = Bot.findById(id);
                        if (user != null) {
                            this.session.sessionHandler.loadInnfo(user);
                        }
                    } else {
                        User user = SessionManager.findUserById(id);
                        if (user != null) {
                            this.session.sessionHandler.loadInnfo(user);
                        }
                    }
                }
            }
            case 36 -> {
                if (this.session.user != null) {
                    this.session.user.findUser(msg.reader().readUTF());
                }
            }
            case 42 -> {
                if (this.session.user != null) {
                    int gameTick = this.session.isGE("2.4.0") ? msg.reader().readInt() : msg.reader().readByte();
                    this.session.sessionHandler.pingPaint();
                }
            }
            case 49 -> {
                if (this.session.user != null && this.session.user.roomWait != null && this.session.user.roomWait.started) {
                    this.session.user.skipTurn();
                }
            }
            case 54 -> {
                if (this.session.user != null && this.session.user.roomWait != null && !this.session.user.roomWait.started) {
                    this.session.user.changeNameRoomWait(msg.reader().readUTF());
                }
            }
            case 56 -> {
                if (this.session.user != null && this.session.user.roomWait != null && !this.session.user.roomWait.started) {
                    this.session.user.setPlayerLimitRoomWait(msg.reader().readByte());
                }
            }
            case 58 -> {
                msg.reader().readByte();
            }
            case 68 -> {
                if (this.session.user != null && this.session.user.roomWait != null && !this.session.user.roomWait.started) {
                    byte[] array = new byte[msg.reader().available()];
                    msg.reader().read(array);
                    this.session.user.setItem(array);
                }
            }
            case 69 -> {
                if (this.session.user != null && this.session.user.roomWait == null) {
                    this.session.user.selectGlass(msg.reader().readByte());
                }
            }
            case 71 -> {
                if (this.session.user != null && this.session.user.roomWait != null && !this.session.user.roomWait.started) {
                    this.session.user.changeTeam();
                }
            }
            case 72 -> {
                if (this.session.user != null && this.session.user.roomWait == null) {
                    this.session.user.buyItem(msg.reader().readByte(), msg.reader().readByte(), msg.reader().readByte());
                }
            }
            case 74 -> {
                if (this.session.user != null && this.session.user.roomWait == null) {
                    this.session.user.buyGlass(msg.reader().readByte() + 3, msg.reader().readByte());
                }
            }
            case 75 -> {
                if (this.session.user != null && this.session.user.roomWait != null && !this.session.user.roomWait.started) {
                    this.session.user.changeMapRoomWait(msg.reader().readByte());
                }
            }
            case 78 -> {
                if (this.session.user != null && this.session.user.roomWait != null && !this.session.user.roomWait.started) {
                    this.session.user.findPlayerToRoomWait(msg.reader().readBoolean(), msg.reader().readInt());
                }
            }
            case 90 -> {
                if (this.session.user != null && this.session.user.roomWait == null) {
                    byte type = msg.reader().readByte();
                    byte version = msg.reader().readByte();
                    switch (type) {
                        case 1 -> this.session.sessionHandler.iconInfo(GameData.resourceVersion("res/map/icon"), version);
                        case 2 -> this.session.sessionHandler.mapInfo(GameData.resourceVersion("res/map"), version);
                        case 3 -> this.session.sessionHandler.layerInfo(GameData.resourceVersion("res/player"), version);
                        case 4 -> this.session.sessionHandler.dataInfo(GameData.equipmentDataVersion(), version);
                        case 5 -> this.session.sessionHandler.captionInfo(GameData.resourceVersion("cache/levelCData2"), version);
                        case 6 -> {
                            this.session.sessionHandler.loadInfo();
                            this.session.sessionHandler.updateRuong();
                            this.session.sessionHandler.loadRoomName();
                        }
                    }
                }
            }
            case 98 -> {
                if (this.session.user != null && this.session.user.roomWait == null) {
                    int[] pointAdd = new int[this.session.user.glass().ability.length];
                    for (int i = 0; i < pointAdd.length; i++) {
                        pointAdd[i] = msg.reader().readShort();
                    }
                    this.session.user.glass().upadtePoint(pointAdd);
                }
            }
            case 99 -> {
                if (this.session.user != null && this.session.user.roomWait == null) {
                    this.session.sessionHandler.loadInfo();
                }
            }
            case 102 -> {
                if (this.session.user != null && this.session.user.roomWait == null) {
                    int[] dbKey = new int[this.session.user.glass().dbKey.length];
                    for(int i = 0; i < dbKey.length; i++) {
                        dbKey[i] = msg.reader().readInt()&0xFFFF;
                    }
                    this.session.user.setEquip(dbKey);
                }
            }
            case 103 -> {
                if (this.session.user != null && this.session.user.roomWait == null) {
                    this.session.sessionHandler.equipShop(this.session.user.glass().id);
                }
            }
            case 104 -> {
                if (this.session.user != null && this.session.user.roomWait == null) {
                    byte type = msg.reader().readByte();
                    switch (type) {
                        case 0 -> this.session.user.buyEquip(msg.reader().readShort(), msg.reader().readByte());
                        case 1 -> {
                            int dbKey[] = new int[msg.reader().readByte()];
                            for (int i = 0; i < dbKey.length; i++) {
                                dbKey[i] = msg.reader().readInt() & 0xFFFF;
                            }
                            this.session.user.actionEquip(dbKey);
                        }
                        case 2 -> this.session.user.getConfirm().confirm();
                    }
                }
            }
            case 114 -> {
                msg.reader().readUTF();
            }
            case 120 -> {
                if (this.session.user != null) {
                    this.session.sessionHandler.getBigIcon(msg.reader().readByte());
                }
            }
            case 126 -> {
                if (this.session.user != null) {
                    byte typeIcon = msg.reader().readByte();
                    byte idIcon = msg.reader().readByte();
                    byte indexIcon = -1;
                    if (typeIcon == 3 || typeIcon == 4) {
                        indexIcon = msg.reader().readByte();
                    }
                    this.session.sessionHandler.getMaterialIconMessage(typeIcon, idIcon, indexIcon);
                }
            }
            case 127 -> {
                msg.reader().readUTF();
            }
            default-> {
            }
        }
        System.out.println("cmd: "+ msg.getCommand());
    }
}
