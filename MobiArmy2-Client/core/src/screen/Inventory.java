package screen;
import CLib.mGraphics;
import Equipment.Equip;
import coreLG.CCanvas;
import coreLG.TerrainMidlet;
import java.util.Vector;
import model.CRes;
import model.Font;
import model.IAction;
import model.Language;
import model.PlayerInfo;
import model.Position;
import network.Command;
import network.GameService;
public class Inventory extends TabScreen {
   int wTab;
   static int cmtoYI;
   static int cmyI;
   static int cmdyI;
   static int cmvyI;
   static int cmyILim;
   int size = 0;
   int nLine = 8;
   private int wXp;
   private int wYp;
   private Vector equips = new Vector();
   Command cmdXacnhan;
   Command menu;
   int dem;
   boolean isCombine = false;
   Position transText1 = new Position(0, 1);
   int combineSelect;
   public int select2;
   Equip eSelect;
   String equipDetail;
   String equipName;
   String date;
   int numCombine = 1;
   boolean isCombineNum;
   int hLine;
   int pa = 0;
   boolean trans = false;
   Command cmdCombine;
   boolean isItemPopup;
   boolean isGemPicker;
   Equip popupEquip;
   Equip socketEquip;
   Vector gemOptions = new Vector();
   int gemSelect;
   int gemScroll;
   int gemScrollTo;
   int gemScrollLim;
   int popupButtonSelect;
   boolean gemQuantityMode;
   int gemQuantity;
   public Inventory() {
      this.xPaint = CCanvas.width / 2 - 85;
      this.yPaint = (CCanvas.hieght - CScreen.cmdH) / 2 - 85;
      this.hTabScreen = 180;
      this.title = Language.ruongdo();
      this.getW();
      if (CCanvas.isTouch) {
         this.nLine = 4;
         this.wXp = this.wBlank / 4;
         this.wYp = 5;
         this.wTab = 40;
      } else {
         this.nLine = 8;
         this.wXp = 0;
         this.wYp = 0;
         this.wTab = 20;
      }
      this.cmdCombine = new Command(Language.select(), new IAction() {
         public void perform() {
            if (Inventory.this.isGemPicker) {
               Inventory.this.confirmSelectedGem();
            } else if (Inventory.this.isItemPopup) {
               Inventory.this.performSelectedPopupButton();
            } else if (!Inventory.this.isCombineNum) {
               Inventory.this.showItemPopup(Inventory.this.getEquipSelect());
            } else {
               Inventory.this.isCombineNum = false;
            }
         }
      });
      this.nameCScreen = "Inventory screen!";
   }
   public void show(CScreen lastScreen) {
      super.show(lastScreen);
      this.init();
   }
   public void init() {
      this.select2 = 0;
      this.menuScroll = false;
      if (this.size == 0) {
         cmtoYI = 0;
      }
      this.size = EquipScreen.inventory.size();
      this.hLine = this.size / this.nLine;
      if (this.size % this.nLine != 0) {
         ++this.hLine;
      }
      this.select = 0;
      this.getCommand();
      if (this.size != 0) {
         this.getDetail();
      }
   }
   public void getCommand() {
      this.center = new Command(Language.select(), new IAction() {
         public void perform() {
         }
      });
      this.center = this.cmdCombine;
      new Command(Language.xacnhan(), new IAction() {
         public void perform() {
            Inventory.this.doCombine();
         }
      });
      this.menu = new Command("Menu", new IAction() {
         public void perform() {
            Vector<Command> menu = new Vector();
            menu.addElement(Inventory.this.cmdXacnhan);
            menu.addElement(new Command(Language.detail(), new IAction() {
               public void perform() {
                  if (!Inventory.this.getEquipSelect().isMaterial) {
                     CCanvas.startOKDlg(Inventory.this.getEquipSelect().getStrInvDetail());
                  } else if (Inventory.this.getEquipSelect().strDetail.startsWith(Language.fomula())) {
                     CCanvas.startOKDlg(Language.pleaseWait());
                     GameService.gI().getFomula((byte)Inventory.this.getEquipSelect().id, (byte)1, (byte)-1);
                  } else {
                     CCanvas.startOKDlg(Inventory.this.getEquipSelect().strDetail);
                  }
               }
            }));
            CCanvas.menu.startAt(menu, 0);
         }
      });
      this.cmdXacnhan = new Command(Language.use(), new IAction() {
         public void perform() {
            Inventory.this.doCombine();
         }
      });
      this.right = new Command(Language.back(), new IAction() {
         public void perform() {
            if (Inventory.this.isItemPopup) {
               Inventory.this.hideItemPopup();
            } else if (Inventory.this.isGemPicker) {
               if (Inventory.this.gemQuantityMode) {
                  Inventory.this.gemQuantityMode = false;
               } else {
                  Inventory.this.resetGemPickerState();
               }
            } else if (!Inventory.this.isCombineNum) {
               CCanvas.equipScreen.isClose = false;
               CCanvas.equipScreen.show(CCanvas.menuScr);
            } else {
               Inventory.this.isCombineNum = false;
               Equip e = Inventory.this.getEquipSelect();
               if (e != null) {
                  e.isSelect = false;
               }
            }
         }
      });
   }
   public void doUse() {
      if (this.size != 0) {
         int[] ind = new int[this.dem];
         int a = 0;
         this.size = EquipScreen.inventory.size();
         for(int i = 0; i < this.size; ++i) {
            Equip e = (Equip)EquipScreen.inventory.elementAt(i);
            if (e.isSelect) {
               ind[a] = e.dbKey;
               ++a;
            }
         }
      }
   }
   public Equip getEquipSelect() {
      Equip eS = null;
      this.size = EquipScreen.inventory.size();
      if (this.size > 0) {
         eS = (Equip)EquipScreen.inventory.elementAt(this.select2);
      }
      return eS;
   }
   public void showItemPopup(Equip e) {
      if (e == null) {
         return;
      }
      this.resetGemPickerState();
      this.popupEquip = e;
      this.isItemPopup = true;
      this.isCombineNum = false;
      this.popupButtonSelect = 0;
   }
   public void hideItemPopup() {
      this.isItemPopup = false;
      this.popupEquip = null;
   }
   public boolean isSocketGem(Equip e) {
      return e != null && e.isMaterial && e.id >= 0 && e.id < 50 && e.num > 0;
   }
   public void resetGemPickerState() {
      this.isGemPicker = false;
      this.gemQuantityMode = false;
      this.socketEquip = null;
      this.gemOptions.removeAllElements();
      this.gemSelect = 0;
      this.gemScroll = 0;
      this.gemScrollTo = 0;
      this.gemQuantity = 1;
   }
   public void showGemPicker(Equip e) {
      this.socketEquip = e;
      this.gemOptions.removeAllElements();
      for(int i = 0; i < EquipScreen.inventory.size(); ++i) {
         Equip item = (Equip)EquipScreen.inventory.elementAt(i);
         if (this.isSocketGem(item)) {
            this.gemOptions.addElement(item);
         }
      }
      if (this.gemOptions.size() == 0) {
         CCanvas.startOKDlg("Không có ngọc có thể ghép.");
         return;
      }
      this.gemSelect = 0;
      this.gemScroll = 0;
      this.gemScrollTo = 0;
      this.gemQuantityMode = false;
      this.gemQuantity = 1;
      this.isItemPopup = false;
      this.isGemPicker = true;
   }
   public int getPopupButtonCount() {
      return this.popupEquip != null && !this.popupEquip.isMaterial ? 3 : 2;
   }
   public void performSelectedPopupButton() {
      if (this.popupEquip == null) {
         return;
      }
      if (this.popupButtonSelect == 0) {
         this.usePopupItem();
      } else if (this.popupButtonSelect == 1) {
         this.sellPopupItem();
      } else if (!this.popupEquip.isMaterial) {
         if (this.popupEquip.slot <= 0) {
            CCanvas.startOKDlg("Trang bị đã hết slot ghép ngọc.");
         } else {
            this.showGemPicker(this.popupEquip);
         }
      }
   }
   public void confirmSelectedGem() {
      if (!this.isGemPicker || this.socketEquip == null || this.gemSelect < 0 || this.gemSelect >= this.gemOptions.size()) {
         return;
      }
      Equip gem = (Equip)this.gemOptions.elementAt(this.gemSelect);
      this.requestSocketGem(this.socketEquip, gem, this.gemQuantity);
      this.resetGemPickerState();
   }
   public void requestSocketGem(Equip equip, Equip gem, int num) {
      if (equip == null || gem == null) {
         return;
      }
      if (num <= 0) {
         num = 1;
      }
      int[] ids = new int[]{equip.dbKey | 0x10000, gem.id};
      byte[] nums = new byte[]{1, (byte)num};
      GameService.gI().imbue((byte)0, (byte)2, ids, nums);
   }
   public int getMaxGemQuantity() {
      if (this.socketEquip == null || this.gemSelect < 0 || this.gemSelect >= this.gemOptions.size()) {
         return 1;
      }
      Equip gem = (Equip)this.gemOptions.elementAt(this.gemSelect);
      int max = gem.num;
      if (this.socketEquip.slot < max) {
         max = this.socketEquip.slot;
      }
      return max <= 0 ? 1 : max;
   }
   public void startGemQuantityMode() {
      this.gemQuantityMode = true;
      this.gemQuantity = 1;
      int max = this.getMaxGemQuantity();
      if (this.gemQuantity > max) {
         this.gemQuantity = max;
      }
   }
   public void sellPopupItem() {
      if (this.popupEquip == null) {
         return;
      }
      Equip e = this.popupEquip;
      this.unSelectEquip();
      e.isSelect = true;
      e.numSelected = 1;
      this.doCombine();
      this.hideItemPopup();
   }
   public void usePopupItem() {
      if (this.popupEquip == null) {
         return;
      }
      Equip e = this.popupEquip;
      this.unSelectEquip();
      if (e.isMaterial) {
         e.isSelect = true;
         e.numSelected = 1;
         this.doCombine();
      } else {
         CCanvas.startOKDlg("Hãy vào màn Trang bị để mặc trang bị này.");
      }
      this.hideItemPopup();
   }
   public void unSelectEquip() {
      this.size = EquipScreen.inventory.size();
      for(int i = 0; i < this.size; ++i) {
         ((Equip)EquipScreen.inventory.elementAt(i)).isSelect = false;
         ((Equip)EquipScreen.inventory.elementAt(i)).numSelected = 0;
      }
      this.dem = 0;
   }
   public void combineYesNo(String info) {
      CCanvas.startYesNoDlg(info, new IAction() {
         public void perform() {
            GameService.gI().imbue((byte)1, (byte)-1, (int[])null, (byte[])null);
            Inventory.this.unSelectEquip();
            CCanvas.endDlg();
         }
      }, new IAction() {
         public void perform() {
            CCanvas.endDlg();
            Inventory.this.unSelectEquip();
         }
      });
   }
   public void doCombine() {
      this.size = EquipScreen.inventory.size();
      if (this.size > 0) {
         this.isCombine = false;
         this.size = EquipScreen.inventory.size();
         for(int i = 0; i < EquipScreen.inventory.size(); ++i) {
            Equip e = (Equip)EquipScreen.inventory.elementAt(i);
            if (e.isSelect) {
               ++this.dem;
            }
         }
         int[] ind = new int[this.dem];
         byte[] numSl = new byte[this.dem];
         int a = 0;
         int i;
         for(i = 0; i < this.size; ++i) {
            Equip e = (Equip)EquipScreen.inventory.elementAt(i);
            if (e.isSelect) {
               if (e.isMaterial) {
                  this.isCombine = true;
                  ind[a] = e.id;
               } else {
                  ind[a] = e.dbKey | 0x10000;
               }
               numSl[a] = (byte)e.numSelected;
               ++a;
               e.isSelect = false;
            }
         }
         if (this.isCombine) {
            GameService.gI().imbue((byte)0, (byte)this.dem, ind, numSl);
         } else {
            for(i = 0; i < ind.length; ++i) {
               GameService.gI().buy_sell_Equip((byte)1, ind, (short)-1, (byte)-1);
            }
         }
         this.dem = 0;
      }
   }
   public void doCombineSelect() {
      this.size = EquipScreen.inventory.size();
      if (this.size != 0) {
         Equip e = this.getEquipSelect();
         if (e != null) {
            if (e.num > 1) {
               e.numSelected = 1;
               this.isCombineNum = true;
               this.numCombine = 1;
               e.isSelect = true;
            } else {
               e.numSelected = 1;
               e.isSelect = !e.isSelect;
            }
         }
      }
   }
   public void paint(mGraphics g) {
      super.paint(g);
      g.setColor(3832504);
      g.fillRoundRect(CCanvas.width / 2 - 85, this.yPaint + 23, 170, 115, 6, 6, true);
      PlayerInfo m = TerrainMidlet.myInfo;
      String myMoney = Language.money() + ": " + m.xu + Language.xu() + "-" + m.luong + Language.luong();
      Font.normalFont.drawString(g, myMoney, CCanvas.width / 2, this.yPaint + 160, 3);
      Font.normalFont.drawString(g, this.equipName, CCanvas.width / 2, this.yPaint + 142, 3);
      Font.borderFont.drawString(g, " ", this.xPaint, this.yPaint + 12, 2, false);
      this.paintMaterial(g, CCanvas.width / 2 - 78, this.yPaint + 29);
      if (this.isCombineNum) {
         this.paintCombineSelect(this.combineSelect, CCanvas.width / 2, CCanvas.hieght / 2, g);
      }
      if (this.isItemPopup) {
         this.paintItemPopup(g);
      }
      if (this.isGemPicker) {
         this.paintGemPicker(g);
      }
      this.paintSuper(g);
   }
   public void paintItemPopup(mGraphics g) {
      if (this.popupEquip == null) {
         return;
      }
      int w = Math.min(CCanvas.width - 20, 196);
      int h = 112;
      int x = CCanvas.width / 2 - w / 2;
      int y = CCanvas.hieght / 2 - h / 2;
      paintDefaultPopup(x, y, w, h, g);
      this.popupEquip.drawIcon(g, x + 18, y + 18, false);
      Font.normalGFont.drawString(g, this.popupEquip.name, x + 34, y + 12, 0, false);
      String detail = this.popupEquip.isMaterial ? this.popupEquip.strDetail : this.popupEquip.getStrInvDetail();
      String[] lines = Font.normalFont.splitFontBStrInLine(detail == null ? "" : detail, w - 24);
      int lineCount = Math.min(lines.length, 2);
      for(int i = 0; i < lineCount; ++i) {
         Font.normalFont.drawString(g, lines[i], x + 12, y + 38 + i * 14, 0, false);
      }
      int buttonY = y + h - 30;
      int count = this.getPopupButtonCount();
      int gap = 4;
      int buttonW = (w - 24 - gap * (count - 1)) / count;
      this.paintPopupButton(g, x + 12, buttonY, buttonW, 18, "Dùng", this.popupButtonSelect == 0);
      this.paintPopupButton(g, x + 12 + buttonW + gap, buttonY, buttonW, 18, "Bán", this.popupButtonSelect == 1);
      if (!this.popupEquip.isMaterial) {
         this.paintPopupButton(g, x + 12 + (buttonW + gap) * 2, buttonY, buttonW, 18, "Ngọc", this.popupButtonSelect == 2);
      }
   }
   public void paintPopupButton(mGraphics g, int x, int y, int w, int h, String text, boolean selected) {
      g.setColor(selected ? 4819660 : 2378093);
      g.fillRoundRect(x, y, w, h, 5, 5, false);
      g.setColor(selected ? 16774532 : 6457531);
      g.drawRect(x, y, w, h, false);
      if (selected) {
         Font.normalGFont.drawString(g, text, x + w / 2, y + 3, 2, false);
      } else {
         Font.normalYFont.drawString(g, text, x + w / 2, y + 3, 2, false);
      }
   }
   public void paintGemPicker(mGraphics g) {
      int w = Math.min(CCanvas.width - 16, 236);
      int h = Math.min(CCanvas.hieght - 34, 184);
      int x = CCanvas.width / 2 - w / 2;
      int y = CCanvas.hieght / 2 - h / 2;
      paintDefaultPopup(x, y, w, h, g);
      Font.normalGFont.drawString(g, "Chọn ngọc ghép", CCanvas.width / 2, y + 10, 2, false);
      if (this.socketEquip != null) {
         Font.normalFont.drawString(g, this.socketEquip.name, CCanvas.width / 2, y + 25, 2, false);
      }
      int gridX = x + 14;
      int gridY = y + 42;
      int col = Math.max(1, (w - 30) / 32);
      int visibleH = h - (this.gemQuantityMode ? 94 : 52);
      int rows = (this.gemOptions.size() + col - 1) / col;
      this.gemScrollLim = Math.max(0, rows * 32 - visibleH);
      if (this.gemScrollTo > this.gemScrollLim) {
         this.gemScrollTo = this.gemScrollLim;
      }
      g.setClip(gridX - 4, gridY - 4, w - 24, visibleH + 8);
      g.translate(0, -this.gemScroll);
      for(int i = 0; i < this.gemOptions.size(); ++i) {
         Equip gem = (Equip)this.gemOptions.elementAt(i);
         int gx = gridX + i % col * 32;
         int gy = gridY + i / col * 32;
         if (i == this.gemSelect) {
            g.setColor(16767817);
            g.fillRect(gx - 4, gy - 4, 24, 24, true);
         }
         gem.drawIcon(g, gx, gy, true);
      }
      g.translate(0, -g.getTranslateY());
      g.setClip(0, 0, 1000, 1000);
      if (this.gemQuantityMode && this.gemSelect >= 0 && this.gemSelect < this.gemOptions.size()) {
         this.paintGemQuantity(g, x, y + h - 48, w);
      }
   }
   public void paintGemQuantity(mGraphics g, int x, int y, int w) {
      Equip gem = (Equip)this.gemOptions.elementAt(this.gemSelect);
      g.setColor(2378093);
      g.fillRoundRect(x + 10, y, w - 20, 38, 5, 5, false);
      g.setColor(6457531);
      g.drawRect(x + 10, y, w - 20, 38, false);
      Font.normalGFont.drawString(g, gem.name, CCanvas.width / 2, y + 4, 2, false);
      Font.normalYFont.drawString(g, "<  " + this.gemQuantity + "/" + this.getMaxGemQuantity() + "  >", CCanvas.width / 2, y + 20, 2, false);
   }
   public void paintMaterial(mGraphics g, int X, int Y) {
      int a = 0;
      int b = 0;
      g.setClip(X - 2, Y - 2, 170, 105);
      g.translate(0, -cmyI);
      g.setColor(16767817);
      this.size = EquipScreen.inventory.size();
      for(int i = 0; i < this.size; ++i) {
         Equip e = (Equip)EquipScreen.inventory.elementAt(i);
         int xIcon = X + a * this.wTab + this.wXp;
         int yIcon = Y + b * this.wTab + this.wYp;
         if (i == this.select2) {
            g.fillRect(xIcon - (CCanvas.isTouch ? 12 : 2), yIcon - (CCanvas.isTouch ? 12 : 2), CCanvas.isTouch ? 40 : 20, CCanvas.isTouch ? 40 : 20, true);
            if (!CCanvas.isTouch) {
               cmtoYI = yIcon - (Y + 55);
            }
         }
         if (e.isSelect) {
            g.setColor(5612786);
            g.fillRect(xIcon, yIcon, 16, 16, true);
         }
         e.drawIcon(g, xIcon, yIcon, true);
         if (!e.isMaterial) {
            for(int z = 0; z < 3 - e.slot; ++z) {
               if (i != this.select2) {
                  g.setColor(16377901);
               } else {
                  g.setColor(0);
               }
               g.fillRect(xIcon + z * 4, yIcon, 2, 2, true);
            }
         }
         ++a;
         if (a == this.nLine) {
            a = 0;
            ++b;
         }
      }
      g.setClip(0, 0, 1000, 1000);
      g.translate(0, -g.getTranslateY());
   }
   public void paintDetail(mGraphics g, int X, int Y) {
      PlayerInfo m = TerrainMidlet.myInfo;
      String myMoney = Language.money() + ": " + m.xu + Language.xu() + "-" + m.luong + Language.luong();
      int bb = Font.normalFont.getWidth(this.equipDetail);
      if (bb > 155) {
         CRes.transTextLimit(this.transText1, bb - 155);
      }
      int cc = this.transText1.x;
      Font.borderFont.drawString(g, " ", this.xPaint, this.yPaint + 2, 2, false);
      Font.normalFont.drawString(g, myMoney, CCanvas.width / 2, Y - 1, 3, false);
      g.setColor(2378093);
      g.fillRoundRect(X, Y + 14, 170, 16, 6, 6, false);
      g.fillRoundRect(X, Y + 34, 170, 16, 6, 6, false);
      g.fillRoundRect(X, Y + 54, 170, 16, 6, 6, false);
      Font.borderFont.drawString(g, " ", this.xPaint, this.yPaint + 2, 2, false);
      Font.normalGFont.drawString(g, this.equipName, X + 6, Y + 15, 0, false);
      Font.normalYFont.drawString(g, this.date, X + 6, Y + 35, 0, false);
      Font.borderFont.drawString(g, " ", this.xPaint, this.yPaint + 2, 2, false);
      Font.normalYFont.drawString(g, this.equipDetail, X + 6 + cc, Y + 55, 0, false);
   }
   public void requestServer(String info) {
      CCanvas.startYesNoDlg(info, new IAction() {
         public void perform() {
            GameService.gI().buy_sell_Equip((byte)2, (int[])null, (short)-1, (byte)-1);
            CCanvas.startWaitDlgWithoutCancel(Language.pleaseWait(), 19);
            Inventory.this.select2 = 0;
            Inventory.this.dem = 0;
         }
      }, new IAction() {
         public void perform() {
            CCanvas.endDlg();
            Inventory.this.select2 = 0;
         }
      });
   }
   public void getDetail() {
      this.eSelect = (Equip)EquipScreen.inventory.elementAt(this.select2);
      this.equipDetail = this.eSelect.getStrShopDetail();
      this.equipName = this.eSelect.name;
      this.date = Language.expr() + ": " + this.eSelect.date;
      this.transText1.x = 0;
   }
   public void itemCamera() {
      if (cmyI != cmtoYI) {
         cmvyI = cmtoYI - cmyI << 2;
         cmdyI += cmvyI;
         cmyI += cmdyI >> 4;
         cmdyI &= 15;
      }
      if (cmyI > cmyILim) {
         cmyI = cmyILim;
      }
      if (cmyI < 0) {
         cmyI = 0;
      }
   }
   public void paintCombineSelect(int Select, int x, int y, mGraphics g) {
      paintDefaultPopup(x - 75, y - 30, 150, 60, g);
      Font.normalFont.drawString(g, Language.nhapsoluong(), CCanvas.hw, y - 15, 2);
      Font.normalFont.drawString(g, this.numCombine + " " + Language.per(), x, y + 18 - 15, 2);
      g.drawRegion(PrepareScr.imgReady[3], 0, 0, 13, 11, 4, x - 40 + CCanvas.gameTick % 3, y + 20 - 15, 0, true);
      g.drawRegion(PrepareScr.imgReady[3], 0, 0, 13, 11, 7, x + 30 - CCanvas.gameTick % 3, y + 20 - 15, 0, true);
   }
   public void update() {
      super.update();
      cmyILim = this.hLine * this.wTab - 110;
      this.itemCamera();
      if (this.isGemPicker && this.gemScroll != this.gemScrollTo) {
         int dy = this.gemScrollTo - this.gemScroll << 2;
         this.gemScroll += dy >> 4;
         if (Math.abs(this.gemScrollTo - this.gemScroll) < 2) {
            this.gemScroll = this.gemScrollTo;
         }
      }
      if (this.isCombineNum || this.isItemPopup || this.isGemPicker) {
         this.left = null;
      } else {
         this.left = this.menu;
      }
   }
   public void removeEquip(int dbKey, int nDelete) {
      this.size = EquipScreen.inventory.size();
      for(int i = 0; i < this.size; ++i) {
         Equip e = (Equip)EquipScreen.inventory.elementAt(i);
         if (!e.isMaterial) {
            if (e.dbKey == dbKey) {
               e.num -= nDelete;
               if (e.num <= 0) {
                  e.num = 0;
                  EquipScreen.inventory.removeElement(e);
               }
               return;
            }
         } else if (e.id == dbKey) {
            e.num -= nDelete;
            if (e.num <= 0) {
               e.num = 0;
               EquipScreen.inventory.removeElement(e);
            }
            return;
         }
      }
   }
   public Equip getEquip(int dbKey) {
      this.size = EquipScreen.inventory.size();
      for(int i = 0; i < this.size; ++i) {
         Equip e = (Equip)EquipScreen.inventory.elementAt(i);
         CRes.out("DB KEY E= " + e.dbKey);
         if (e.dbKey == dbKey) {
            return e;
         }
      }
      return null;
   }
   public void onPointerDragged(int xDragged, int yDragged, int index) {
      super.onPointerDragged(xDragged, yDragged, index);
      if (this.isGemPicker) {
         if (!this.trans) {
            this.pa = this.gemScroll;
            this.trans = true;
         }
         this.gemScrollTo = this.pa + (CCanvas.pyFirst[index] - yDragged);
         if (this.gemScrollTo < 0) {
            this.gemScrollTo = 0;
         }
         if (this.gemScrollTo > this.gemScrollLim) {
            this.gemScrollTo = this.gemScrollLim;
         }
         return;
      }
      if (this.isItemPopup) {
         return;
      }
      if (!CCanvas.isPointer(xDragged, yDragged, 150, 60, index)) {
         this.isCombineNum = false;
      }
      if (!this.isCombineNum) {
         if (!this.trans) {
            this.pa = cmyI;
            this.trans = true;
         }
         cmtoYI = this.pa + (CCanvas.pyFirst[index] - yDragged);
         if (cmtoYI < 0) {
            cmtoYI = 0;
         }
         if (cmtoYI > this.hLine * 40 - 40) {
            cmtoYI = this.hLine * 40 - 40;
         }
      }
   }
   public void onPointerPressed(int xScreen, int yScreen, int index) {
      super.onPointerPressed(xScreen, yScreen, index);
        if (this.isItemPopup) {
            this.handleItemPopupKeys();
            return;
        }
        if (this.isGemPicker) {
            this.handleGemPickerKeys();
            return;
        }
        if (this.isCombineNum) {
            if (CCanvas.keyPressed[2] || CCanvas.keyPressed[4] || CCanvas.keyPressed[6] || CCanvas.keyPressed[8]) {
                Equip e = this.getEquipSelect();
                if (CCanvas.keyPressed[4] || CCanvas.keyPressed[8]) {
                   if (e.num > 5) {
                      this.numCombine -= 5;
                   } else {
                      --this.numCombine;
                   }
                   if (this.numCombine <= 0) {
                      e.isSelect = false;
                      this.numCombine = 0;
                   }
                   e.numSelected = this.numCombine;
                }
                if (CCanvas.keyPressed[6] || CCanvas.keyPressed[2]) {
                   if (e.num > 5) {
                      this.numCombine += this.numCombine == 1 ? 4 : 5;
                      if (this.numCombine > e.num) {
                         this.numCombine -= 5;
                      }
                   } else if (this.numCombine >= e.num) {
                      this.numCombine = e.num;
                   } else {
                      ++this.numCombine;
                   }
                   e.numSelected = this.numCombine;
                   e.isSelect = true;
                }
                CScreen.clearKey();
            }
        } else
      if (CCanvas.keyPressed[2] || CCanvas.keyPressed[4] || CCanvas.keyPressed[6] || CCanvas.keyPressed[8]) {
            if (CCanvas.keyPressed[2]) {
                this.select2 -= this.nLine;
            }
            if (CCanvas.keyPressed[8]) {
                this.select2 += this.nLine;
            }
            if (CCanvas.keyPressed[4]) {
                this.select2--;
            }
            if (CCanvas.keyPressed[6]) {
                this.select2++;
            }
            if (select2 > EquipScreen.inventory.size() - 1) {
                select2 = 0;
            }
            if (select2 < 0) {
                select2 = EquipScreen.inventory.size() - 1;
            }
            cmtoYI = (select2 / this.nLine) * 40 - 20;
            if (cmtoYI < 0) {
                cmtoYI = 0;
            }
            CScreen.clearKey();
       }
   }
   public void handleItemPopupKeys() {
      if (CCanvas.keyPressed[4] || CCanvas.keyPressed[2]) {
         --this.popupButtonSelect;
         if (this.popupButtonSelect < 0) {
            this.popupButtonSelect = this.getPopupButtonCount() - 1;
         }
         CScreen.clearKey();
      } else if (CCanvas.keyPressed[6] || CCanvas.keyPressed[8]) {
         ++this.popupButtonSelect;
         if (this.popupButtonSelect >= this.getPopupButtonCount()) {
            this.popupButtonSelect = 0;
         }
         CScreen.clearKey();
      } else if (CCanvas.keyPressed[5]) {
         this.performSelectedPopupButton();
         CScreen.clearKey();
      }
   }
   public void handleGemPickerKeys() {
      if (this.gemQuantityMode) {
         if (CCanvas.keyPressed[4] || CCanvas.keyPressed[8]) {
            --this.gemQuantity;
            if (this.gemQuantity < 1) {
               this.gemQuantity = 1;
            }
            CScreen.clearKey();
         } else if (CCanvas.keyPressed[6] || CCanvas.keyPressed[2]) {
            ++this.gemQuantity;
            int max = this.getMaxGemQuantity();
            if (this.gemQuantity > max) {
               this.gemQuantity = max;
            }
            CScreen.clearKey();
         } else if (CCanvas.keyPressed[5]) {
            this.confirmSelectedGem();
            CScreen.clearKey();
         }
         return;
      }
      int w = Math.min(CCanvas.width - 16, 236);
      int col = Math.max(1, (w - 30) / 32);
      int oldSelect = this.gemSelect;
      if (CCanvas.keyPressed[4]) {
         --this.gemSelect;
      } else if (CCanvas.keyPressed[6]) {
         ++this.gemSelect;
      } else if (CCanvas.keyPressed[2]) {
         this.gemSelect -= col;
      } else if (CCanvas.keyPressed[8]) {
         this.gemSelect += col;
      } else if (CCanvas.keyPressed[5]) {
         this.startGemQuantityMode();
         CScreen.clearKey();
         return;
      } else {
         return;
      }
      if (this.gemSelect < 0) {
         this.gemSelect = this.gemOptions.size() - 1;
      }
      if (this.gemSelect >= this.gemOptions.size()) {
         this.gemSelect = 0;
      }
      if (oldSelect != this.gemSelect) {
         this.keepSelectedGemVisible(col);
      }
      CScreen.clearKey();
   }
   public void keepSelectedGemVisible(int col) {
      int h = Math.min(CCanvas.hieght - 34, 184);
      int visibleH = h - 52;
      int rowY = this.gemSelect / col * 32;
      if (rowY - this.gemScroll > visibleH - 32) {
         this.gemScrollTo = rowY - visibleH + 32;
      }
      if (rowY < this.gemScroll) {
         this.gemScrollTo = rowY;
      }
      if (this.gemScrollTo < 0) {
         this.gemScrollTo = 0;
      }
      if (this.gemScrollTo > this.gemScrollLim) {
         this.gemScrollTo = this.gemScrollLim;
      }
   }
   public void onPointerReleased(int xReleased, int yReleased, int index) {
      this.trans = false;
      if (this.isItemPopup) {
         this.onItemPopupReleased(xReleased, yReleased, index);
         CScreen.clearKey();
         return;
      }
      if (this.isGemPicker) {
         this.onGemPickerReleased(xReleased, yReleased, index);
         CScreen.clearKey();
         return;
      }
      super.onPointerReleased(xReleased, yReleased, index);
      if (!CCanvas.isPointer(xReleased, yReleased, 150, 60, index)) {
         this.isCombineNum = false;
      }
      if (this.isCombineNum) {
         if (!CCanvas.isPointer(xReleased - 75, yReleased - 30, 150, 60, index)) {
            this.isCombineNum = false;
         } else {
            Equip e = this.getEquipSelect();
            if (CCanvas.isPointer(CCanvas.width / 2 - 100, CCanvas.hieght / 2 - 100, 100, 200, index)) {
               if (e.num > 5) {
                  this.numCombine -= 5;
               } else {
                  --this.numCombine;
               }
               if (this.numCombine <= 0) {
                  e.isSelect = false;
                  this.numCombine = 0;
               }
               e.numSelected = this.numCombine;
            }
            if (CCanvas.isPointer(CCanvas.width / 2, CCanvas.hieght / 2 - 100, 100, 200, index)) {
               if (e.num > 5) {
                  this.numCombine += this.numCombine == 1 ? 4 : 5;
                  if (this.numCombine > e.num) {
                     this.numCombine -= 5;
                  }
               } else if (this.numCombine >= e.num) {
                  this.numCombine = e.num;
               } else {
                  ++this.numCombine;
               }
               e.numSelected = this.numCombine;
               e.isSelect = true;
            }
         }
      } else {
         if (CCanvas.isPointer(this.xPaint, this.yPaint, this.wTabScreen, this.hTabScreen, index)) {
            int paintX = CCanvas.width / 2 - 78;
            int paintY = this.yPaint + 29;
            if (CCanvas.isPointer(paintX, paintY, 160, 120, index)) {
               int aa = (cmtoYI + yReleased - paintY) / this.wTab * this.nLine + (xReleased - paintX - 8) / this.wTab;
               if (aa < 0 || aa >= this.size) {
                  return;
               }
               this.select2 = aa;
               this.getDetail();
               this.showItemPopup((Equip)EquipScreen.inventory.elementAt(this.select2));
            }
         }
      }
   }
   public void onItemPopupReleased(int xReleased, int yReleased, int index) {
      if (CCanvas.keyPressed[5] || CCanvas.keyPressed[12] || CCanvas.keyPressed[13]) {
         return;
      }
      if (this.popupEquip == null) {
         this.hideItemPopup();
         return;
      }
      int w = Math.min(CCanvas.width - 20, 196);
      int h = 112;
      int x = CCanvas.width / 2 - w / 2;
      int y = CCanvas.hieght / 2 - h / 2;
      int buttonY = y + h - 30;
      int count = this.popupEquip.isMaterial ? 2 : 3;
      int gap = 4;
      int buttonW = (w - 24 - gap * (count - 1)) / count;
      if (CCanvas.isPointer(x + 12, buttonY, buttonW, 18, index)) {
         this.usePopupItem();
         return;
      }
      if (CCanvas.isPointer(x + 12 + buttonW + gap, buttonY, buttonW, 18, index)) {
         this.sellPopupItem();
         return;
      }
      if (!this.popupEquip.isMaterial && CCanvas.isPointer(x + 12 + (buttonW + gap) * 2, buttonY, buttonW, 18, index)) {
         if (this.popupEquip.slot <= 0) {
            CCanvas.startOKDlg("Trang bị đã hết slot ghép ngọc.");
         } else {
            this.showGemPicker(this.popupEquip);
         }
         return;
      }
      if (!CCanvas.isPointer(x, y, w, h, index)) {
         this.hideItemPopup();
      }
   }
   public void onGemPickerReleased(int xReleased, int yReleased, int index) {
      if (CCanvas.keyPressed[5] || CCanvas.keyPressed[12] || CCanvas.keyPressed[13]) {
         return;
      }
      if (Math.abs(CCanvas.pyFirst[index] - yReleased) > 8) {
         return;
      }
      int w = Math.min(CCanvas.width - 16, 236);
      int h = Math.min(CCanvas.hieght - 34, 184);
      int x = CCanvas.width / 2 - w / 2;
      int y = CCanvas.hieght / 2 - h / 2;
      if (!CCanvas.isPointer(x, y, w, h, index)) {
         this.isGemPicker = false;
         this.gemQuantityMode = false;
         return;
      }
      if (this.gemQuantityMode) {
         int qx = x + 10;
         int qy = y + h - 48;
         int qw = w - 20;
         if (CCanvas.isPointer(qx, qy, qw / 3, 38, index)) {
            --this.gemQuantity;
            if (this.gemQuantity < 1) {
               this.gemQuantity = 1;
            }
            return;
         }
         if (CCanvas.isPointer(qx + qw * 2 / 3, qy, qw / 3, 38, index)) {
            ++this.gemQuantity;
            int max = this.getMaxGemQuantity();
            if (this.gemQuantity > max) {
               this.gemQuantity = max;
            }
            return;
         }
         if (CCanvas.isPointer(qx + qw / 3, qy, qw / 3, 38, index)) {
            this.confirmSelectedGem();
            return;
         }
      }
      int gridX = x + 14;
      int gridY = y + 42;
      int col = Math.max(1, (w - 30) / 32);
      int visibleH = h - (this.gemQuantityMode ? 94 : 52);
      if (CCanvas.isPointer(gridX - 4, gridY - 4, w - 24, visibleH + 8, index)) {
         int aa = (this.gemScroll + yReleased - gridY) / 32 * col + (xReleased - gridX) / 32;
         if (aa >= 0 && aa < this.gemOptions.size()) {
            this.gemSelect = aa;
            this.startGemQuantityMode();
         }
      }
   }
}
