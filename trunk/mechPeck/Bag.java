package mechPeck;

import java.awt.Dimension;
import java.awt.Point;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;

import main.LevelPlay;
import main.SuperMain;
import mechPeck.equipment.HealthPackage;
import mechPeck.munition.Ammo;
import mechPeck.munition.MunPack;
import mechPeck.munition.Rockets;

import org.lwjgl.input.Mouse;

import Classes.OGL;
import Classes.OpenAlClip;
import Classes.Texture;
import Classes.Vektor3D;
import Classes.myButton;
import Classes.myColor;
import Classes.myText;
import Classes.v3;
import drops.EquipDrop;

/**
 * Repräsentiert das Inventar des Mechas...
 * 
 * @author ladanz
 * 
 */
public class Bag implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	LinkedList<ItemLocationDimension> items;

	int width = 6;

	int height = 3;

	int mun_plus = 4;

	int equip_line = height + 1;

	transient Texture leer, muell, ammo, rockets, upgrades;

	public String bagtext = "";

	transient Texture nopic;

	static final int item_start = 10000;

	static final int place_start = 100;

	static final Point drop_feld = new Point(-1, 0);

	transient LinkedList<myButton> buttons;

	transient ItemLocationDimension selected_item = null;

	static OpenAlClip drag, drop;

	public Bag() {
		this(5, 3);
	}

	/**
	 * Bag Constructor<BR>
	 * 
	 * @param width
	 * @param height
	 */
	public Bag(int width, int height) {
		this.width = width;
		this.height = height;
		equip_line = height + 1;

		if (drag == null)
			drag = new OpenAlClip(SuperMain.ordner + "sound/equipmentDrag.ogg");
		if (drop == null)
			drop = new OpenAlClip(SuperMain.ordner + "sound/equipmentDrop.ogg");

		items = new LinkedList<ItemLocationDimension>();
		buttons = new LinkedList<myButton>();
		SuperMain.toRun.add(new Runnable() {
			public void run() {
				try {
					leer = SuperMain.loadTex("img/hud/equip/leer.png");
					nopic = SuperMain.loadTex("img/hud/equip/no-pic.png");
					muell = SuperMain.loadTex("img/hud/equip/trash.jpg");
					ammo = SuperMain.loadTex("img/hud/equip/ammo.jpg");
					rockets = SuperMain.loadTex("img/hud/equip/rockets.jpg");
					upgrades = SuperMain.loadTex("img/hud/equip/upgrade.jpg");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					SuperMain.out(e);
				}

			}
		});

		put(new MunPack(500, new Ammo()));
		put(new MunPack(20, new Rockets()));
		// put(new MunPack(20, new PigShells()));

	}

	/**
	 * Tries to place Item in Bag.<br>
	 * 
	 * 
	 * @param item_
	 * @return true/false
	 */
	public boolean put(Equipable item_) {
		// stapeln
		// wenns mun ist, ist die sorte schon vorhanden?
		// wird ueber Munition.equals() abgefragt!
		if (item_ instanceof MunPack) {
			Munition mun_to_pack = ((MunPack) item_).mun;
			for (ItemLocationDimension ite : items) {
				// nur im normalen bag gucken
				if (ite.Location.x >= width || ite.Location.y >= height)
					continue;

				if (ite.Item instanceof MunPack) {
					Munition mun_from_bag = ((MunPack) ite.Item).mun;
					if (mun_from_bag.equals(mun_to_pack)) {
						((MunPack) ite.Item).amount += ((MunPack) item_).amount;
						refreshButtons();
						return true;
					}
				}
			}
		}
		if (item_ instanceof HealthPackage) {

			for (ItemLocationDimension ite : items) {
				// nur im normalen bag gucken
				if (ite.Location.x >= width || ite.Location.y >= height)
					continue;

				if (ite.Item instanceof HealthPackage) {

					((HealthPackage) ite.Item).setValue(((HealthPackage) item_).getValue()
							+ ((HealthPackage) ite.Item).getValue());
					refreshButtons();
					return true;

				}
			}
		}
		ItemLocationDimension item = new ItemLocationDimension(item_);
		Point p = getFirstPlaceForItem(item.Dimension);
		if (p == null)
			return false;
		item.Location = p;
		items.add(item);
		// boolean[][] b=createBesetztMatrix();
		// for(boolean [] b_:b)
		// System.out.println(Arrays.toString(b_));
		refreshButtons();
		return true;
	}

	/**
	 * Compares free Room in Bag with Item-Dimension (d).
	 * 
	 * @param d
	 * @return true/false
	 */
	public boolean hasRoomForItem(Dimension d) {
		return (getFirstPlaceForItem(d) != null);
	}

	/**
	 * Returns first fitting Place in Bag for Item;
	 * 
	 * @param dimension
	 *            Dimension of the Item
	 * @return (free Space? Fitting Place = 0)
	 */
	private Point getFirstPlaceForItem(Dimension dimension) {
		// besetzmatrix erstellen die wo besetzt ist eine 1 hat, sonst 0;
		boolean[][] besetztMatrix = createBesetztMatrix();
		Point res = new Point(0, 0);
		boolean passt;
		do {
			passt = true;
			first: for (int i = res.x; i < res.x + dimension.width; i++) {
				for (int j = res.y; j < res.y + dimension.height; j++) {

					if (i >= width || j >= height || besetztMatrix[i][j]) {
						res.x++;
						passt = false;
						if (res.x == width) {
							res.x = 0;
							res.y++;
						}
						break first;
					}

				}
			}
			if (passt || (res.x >= width || res.y >= height))
				break;
		} while (true);

		return (res.x == width || res.y == height ? null : res);
	}

	private boolean[][] createBesetztMatrix() {
		return createBesetztMatrix(null);
	}

	private boolean[][] createBesetztMatrix(ItemLocationDimension without) {
		boolean[][] res = new boolean[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				res[i][j] = false;
			}
		}
		for (ItemLocationDimension item : items) {
			if (item.Dimension.height == 0 || item.Dimension.width == 0 || item.Location.y >= height
					|| item.Location.x >= width) {
				continue;
			}
			if (without != null && item == without) {
				continue;
			}
			for (int i = (int) item.Location.x; i < item.Location.x + item.Dimension.width; i++) {
				for (int j = (int) item.Location.y; j < item.Location.y + item.Dimension.height; j++) {
					res[i][j] = true;
				}

			}
		}
		return res;
	}

	Vektor3D um = new v3(-1, 1, 0);

	/**
	 * Renders the Bag with buttons and items
	 * 
	 * @param scale
	 */
	public void render(double scale) {
		OGL.verschieb(um);
		boolean build_buttons = buttons.size() == 0;

		// dropfeld
		// Point p=getPositionbyName(drop_feld);
		OGL.viereck(new Vektor3D(drop_feld.x * scale, drop_feld.y * scale, 0), new v3(1 * scale, 1 * scale, 0), muell,
				getNamebyPosition(drop_feld));

		if (build_buttons) {
			myButton b = new myButton(null, drop_feld.x * scale, drop_feld.y * scale, 1 * scale, 1 * scale, null, "");
			b.setId(getNamebyPosition(drop_feld));
			buttons.add(b);
		}

		// auf alle leeren felder --> leer pic
		boolean[][] bm = createBesetztMatrix();

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (!bm[i][j]) {

					OGL.viereck(new Vektor3D(i * scale, j * scale, 0), new v3(1 * scale, 1 * scale, 0), leer,
							getNamebyPosition(new Point(i, j)));

				}
				if (build_buttons) {
					myButton b = new myButton(null, i * scale, j * scale, 1 * scale, 1 * scale, null, "");
					b.setId(getNamebyPosition(new Point(i, j)));
					buttons.add(b);
				}
			}
		}
		render_mun1(scale, build_buttons);

		// dann items

		int i = item_start;
		for (ItemLocationDimension item : items) {
			Texture tex = item.Item.getEquipTexture();
			if (tex == null)
				tex = nopic;
			OGL.viereck(new Vektor3D(item.Location.x * scale, item.Location.y * scale, 0), new v3(item.Dimension.width
					* scale, item.Dimension.height * scale, 0), tex, i);
			if (build_buttons) {
				myButton b = new myButton(null, item.Location.x * scale, item.Location.y * scale, item.Dimension
						.getWidth()
						* scale, item.Dimension.getHeight() * scale, null, "");
				b.setId(i);
				buttons.add(b);
			}
			i++;
		}
		int selection = LevelPlay.orthoselection(Mouse.getX(), Mouse.getY());
		if (selection >= item_start && selection < item_start + items.size()) {
			bagtext = items.get(selection - item_start).Item.getMouseOverText();

		}
		Point p = getPositionbyName(selection);
		if (p.equals(drop_feld)) {
			bagtext = "drop it";
		}

		if (bagtext != null && !bagtext.equals("")) {
			Vektor3D farbe = myColor.BGREY;
			myText.setSelected_big_text(SuperMain.TEXT_CONSOLE_BIG);
			myText.setSelected_text(SuperMain.TEXT_CONSOLE_SMALL);
			// wenn im bag && req nicht erfüllt --> rot
			ItemLocationDimension it = null;

			try {
				if (selection >= item_start)
					it = items.get(selection - item_start);
			} catch (IndexOutOfBoundsException e) {
				it = null;
			}
			// mun --> grun
			// if (it != null && it.Item instanceof MunPack)
			// farbe = new v3(0, 1, 0);

			// equip->blau
			// if (it != null && it.Item instanceof Equipment)
			// farbe = new v3(0, 0, 1);

			// nicht einsetzbares equip->DunkelGrau
			if (it != null && it.Item instanceof Equipment && !((Equipment) it.Item).RequirementsFullfiled()
					&& it.Location.x < width && it.Location.y < height)
				farbe = myColor.DGREY;

			double j = 0;
			for (String s : bagtext.split("/n")) {
				myText.out(s, new v3(-8 * scale, (height - 1 - j) * scale, 0), new v3(0.3 * scale, 0.3 * scale,
						0.3 * scale), farbe, 1.1, 0);
				j += 0.5;
			}
			bagtext = "";
		}
		// move item
		if (selected_item != null) {
			if (selection >= place_start && SuperMain.selection < item_start) {

				Point selected_place = getPositionbyName(selection);
				Texture tex = selected_item.Item.getEquipTexture();
				int x = selected_place.x;
				int y = selected_place.y;
				OGL
						.viereck(new Vektor3D(x * scale, y * scale, 0), new v3(1 * scale, 1 * scale, 0), tex,
								selection, 0.5);

			}
		}

		OGL.verschieb(um.negiere());

	}

	private void render_mun1(double scale) {
		render_mun1(scale, false);
	}

	private void render_mun1(double scale, boolean build_buttons) {
		// linke mun felder
		OGL.setColor(myColor.GREEN);
		for (int i = height + mun_plus; i < height + mun_plus + SuperMain.profil.mecha.linkeWaffe.getSlotCount(); i++) {
			int name = getNamebyPosition(new Point(0, i));
			// feld leer
			{

				OGL.viereck(new Vektor3D(0, i * scale, -0.01), new v3(1 * scale, 1 * scale, 0), rockets, name);
				if (build_buttons) {
					myButton b = new myButton(null, 0 * scale, i * scale, 1 * scale, 1 * scale, null, "");
					b.setId(name);
					buttons.add(b);
				}
			}
		}
		// rechjte mun felder
		for (int i = height + mun_plus; i < height + mun_plus + SuperMain.profil.mecha.rechteWaffe.getSlotCount(); i++) {
			int name = getNamebyPosition(new Point(width - 1, i));
			// feld leer
			{

				OGL.viereck(new Vektor3D((width - 1) * scale, i * scale, -0.01), new v3(1 * scale, 1 * scale, 0), ammo,
						name);
				if (build_buttons) {
					myButton b = new myButton(null, (width - 1) * scale, i * scale, 1 * scale, 1 * scale, null, "");
					b.setId(name);
					buttons.add(b);
				}
			}
		}

		// equipline
		OGL.setColor(myColor.BLUE);
		for (int i = 0; i < SuperMain.profil.mecha.getEquipSlots(); i++) {
			Point p = new Point(1 + (i) % (width - 2), (i) / (width - 2) + equip_line);
			int name = getNamebyPosition(p);
			// feld leer
			{

				OGL.viereck(new Vektor3D(p.x * scale, p.y * scale, -0.01), new v3(1 * scale, 1 * scale, 0), upgrades,
						name);
				if (build_buttons) {
					myButton b = new myButton(null, p.x * scale, p.y * scale, 1 * scale, 1 * scale, null, "");
					b.setId(name);
					buttons.add(b);
				}
			}
		}
		OGL.setColor(myColor.WHITE);
	}

	private int getNamebyPosition(Point p) {
		return (p.x * 1000 + p.y + place_start);
	}

	private Point getPositionbyName(int name) {
		name -= place_start;
		return new Point(name / 1000, name % 1000);
	}

	/**
	 * Catches mouse-clicks and processes them
	 * 
	 * @param key
	 */
	public void onMouseDown(int key) {
		int selection = LevelPlay.orthoselection(Mouse.getX(), Mouse.getY());
		if (key == 0) {
			// ist ein item selected?

			if (selection >= item_start) {
				try {
					selected_item = items.get(selection - item_start);
				} catch (IndexOutOfBoundsException e) {
					// Anscheinend doch kein Item ?!?
					System.err.println("onMouseDown() IndexOutOfBounds Exception- in Bag Z 459");
					return;
				}
				// System.out.println("Item selected: " + SuperMain.selection);
				drag.play();
			} else {
				selected_item = null;
			}
		} else if (key == 1) {
			if (selection >= item_start) {
				selected_item = items.get(selection - item_start);
				if (selected_item.Item instanceof RechtsKlickAble) {
					((RechtsKlickAble) selected_item.Item).doRechtsKlickAction();
				}
			}
			refreshButtons();
		}

	}

	/**
	 * Veranlasst die Neuberechnung der Buttons, die zum Auswaehlen von
	 * Elementen benoetigt werden.
	 */
	private void refreshButtons() {
		buttons.clear();
	}

	/**
	 * Catches mouse-click-releases and processes them
	 * 
	 * @param key
	 */
	public void onMouseUp(int key) {
		int selection = LevelPlay.orthoselection(Mouse.getX(), Mouse.getY());
		if (key == 0) {

			// Item auf einen anderen Platz legen
			if (selected_item != null && selection >= place_start && selection < item_start) {
				Point selected_place = getPositionbyName(selection);
				tryToPutAtPlace(selected_item, selected_place);
				reorganizeWeaponSlots();
				drop.play();
			}
			// versuch eine item auf ein anders zu legen
			if (selected_item != null && selection >= item_start) {
				ItemLocationDimension item;
				item = items.get(selection - item_start);
				// nicht das selbe auf einander legen
				if (item == selected_item) {
					selected_item = null;
					refreshButtons();
					return;
				}
				// mun auf mun???
				if (item.Item instanceof MunPack && selected_item.Item instanceof MunPack) {
					// gleiche Art ???
					if (((MunPack) item.Item).mun.equals(((MunPack) selected_item.Item).mun)) {
						// Ist Ziel in mun-feld???
						if (item.Location.y > height && (item.Location.x == 0 || item.Location.x == width - 1)) {
							int slot_max = (item.Location.x == 0 ? SuperMain.profil.mecha.linkeWaffe.getAmountperSlot()
									: SuperMain.profil.mecha.rechteWaffe.getAmountperSlot());

							int d = Math.min(slot_max - ((MunPack) item.Item).amount,
									((MunPack) selected_item.Item).amount);
							((MunPack) item.Item).amount += d;
							((MunPack) selected_item.Item).amount -= d;
							if (((MunPack) selected_item.Item).amount <= 0) {
								remove(selected_item.Item);
							}
						} else {
							// keine beschraenkung
							((MunPack) item.Item).amount += ((MunPack) selected_item.Item).amount;
							remove(selected_item.Item);
						}
					}
				}
			}// ENDE versuch eins auf ein anderes zu legen

			// Spezialfall Dropfeld
			Point selected_place = getPositionbyName(selection);
			if ((selected_place.equals(drop_feld))) {
				// drop item
				dropItem(selected_item);

			}

		}
		// Item nicht mehr Selektieren
		if (key == 0 || key == 1) {
			selected_item = null;
			refreshButtons();

		}

	}

	private void dropItem(ItemLocationDimension item) {
		if (item == null || item.Item == null) {
			return;
		}
		double x = Math.cos(Math.toRadians(-LevelPlay.p.blickrichtung + 90)) * 6.;
		double z = Math.sin(Math.toRadians(-LevelPlay.p.blickrichtung + 90)) * 6.;
		Vektor3D infront = new Vektor3D(-x, 0, -z);

		SuperMain.addDrop(new EquipDrop(LevelPlay.p.getPosition().add(infront), item.Item));
		items.remove(item);
		reorganizeWeaponSlots();
		refreshButtons();
		// LevelPlay.InGameConsole.addText("Item "+item.Item+" gedroppt");
	}

	private void tryToPutAtPlace(ItemLocationDimension selected_item, Point selected_place) {

		if (selected_place.x < width && selected_place.y < height) {
			// ein objekt in den bag verschieben

			int x = selected_place.x;
			int y = selected_place.y;

			Dimension dimension = (selected_item).Dimension;
			boolean[][] besetztMatrix = createBesetztMatrix(selected_item);
			boolean passt = true;
			// versuch nach oben rechts auszurichten
			first: for (int i = x; i < x + dimension.width; i++) {
				for (int j = y; j < y + dimension.height; j++) {

					if (i >= width || j >= height || besetztMatrix[i][j]) {

						passt = false;

						break first;
					}
				}
			}
			if (!passt) {
				// versuch nach unten links auszurichten
				passt = true;
				first: for (int i = x - dimension.width + 1; i < x + 1; i++) {
					for (int j = y - dimension.height + 1; j < y + 1; j++) {

						if (i < 0 || j < 0 || i >= width || j >= height || besetztMatrix[i][j]) {

							passt = false;

							break first;
						}
					}
				}
				if (passt) {
					x = x - dimension.width + 1;
					y = y - dimension.height + 1;
				}

			}
			if (!passt) {
				// versuch nach unten rechts auszurichten
				passt = true;
				first: for (int i = x; i < x + dimension.width; i++) {
					for (int j = y - dimension.height + 1; j < y + 1; j++) {

						if (i < 0 || j < 0 || i >= width || j >= height || besetztMatrix[i][j]) {

							passt = false;

							break first;
						}
					}
				}
				if (passt) {
					x = x;
					y = y - dimension.height + 1;
				}

			}
			if (!passt) {
				// versuch nach oben links auszurichten
				passt = true;
				first: for (int i = x - dimension.width + 1; i < x + 1; i++) {
					for (int j = y; j < y + dimension.height; j++) {

						if (i < 0 || j < 0 || i >= width || j >= height || besetztMatrix[i][j]) {

							passt = false;

							break first;
						}
					}
				}
				if (passt) {
					x = x - dimension.width + 1;
					y = y;
				}

			}
			if (passt) {
				(selected_item).Location.x = x;
				(selected_item).Location.y = y;
			}
		} else if (selected_place.x == 0 && selected_place.y >= height) {

			// es soll in linke waffe
			// ist es mun?
			Equipable equip = (selected_item).Item;
			if (!(equip instanceof MunPack)) {
				return;
			}
			// richtiges Kaliber??
			if (!((MunPack) equip).mun.getCaliber().equals(SuperMain.profil.mecha.linkeWaffe.getCaliber())) {
				return;
			}
			// slot leer --> reinpacken
			if (getItembyPoint(selected_place) == null) {

				int amount_ = Math.min(SuperMain.profil.mecha.linkeWaffe.getAmountperSlot(), ((MunPack) equip).amount);
				try {
					Munition newmun = ((MunPack) equip).mun.getClass().newInstance();
					newmun.setSchaden(((MunPack) equip).mun.getSchaden());
					newmun.setPiercing(((MunPack) equip).mun.getPiercing());
					items.add(new ItemLocationDimension(new MunPack(amount_, newmun), selected_place));

				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					SuperMain.out(e);
					return;
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					SuperMain.out(e);
					return;
				}
				((MunPack) equip).amount -= amount_;
				if (((MunPack) equip).amount <= 0) {
					items.remove(selected_item);
				}
				return;

			} else {
				// slot nicht leer
			}

		} else if (selected_place.x == width - 1 && selected_place.y >= height) {

			// es soll in rechte waffe
			// ist es mun?
			Equipable equip = (selected_item).Item;
			if (!(equip instanceof MunPack)) {
				return;
			}
			// richtiges Kaliber??
			if (!((MunPack) equip).mun.getCaliber().equals(SuperMain.profil.mecha.rechteWaffe.getCaliber())) {
				return;
			}
			// slot leer --> reinpacken
			if (getItembyPoint(selected_place) == null) {

				int amount_ = Math.min(SuperMain.profil.mecha.rechteWaffe.getAmountperSlot(), ((MunPack) equip).amount);
				try {
					Munition newmun = ((MunPack) equip).mun.getClass().newInstance();
					newmun.setSchaden(((MunPack) equip).mun.getSchaden());
					newmun.setPiercing(((MunPack) equip).mun.getPiercing());
					items.add(new ItemLocationDimension(new MunPack(amount_, newmun), selected_place));

				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					SuperMain.out(e);
					return;
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					SuperMain.out(e);
					return;
				}
				((MunPack) equip).amount -= amount_;
				if (((MunPack) equip).amount <= 0) {
					items.remove(selected_item);
				}
				return;

			} else {
				// slot nicht leer
			}

		} else if (selected_place.x >= 1 && selected_place.x < width - 1 && selected_place.y >= equip_line) {

			// es soll in equip slot
			// ist es Equipment
			Equipable equip_ = (selected_item).Item;
			if (!(equip_ instanceof Equipment)) {
				return;
			}
			Equipment equip = (Equipment) equip_;
			// Vorraussetzungen erfüllt???
			if (!(equip.RequirementsFullfiled())) {
				return;
			}
			// slot leer --> reinpacken
			if (getItembyPoint(selected_place) == null) {

				selected_item.Location = selected_place;

				return;

			} else {
				// slot nicht leer
			}

		}

	}

	private void reorganizeWeaponSlots() {
		// vllt noch ein kleinerer waffenslot frei?
		// von vorne alle slotz durchlaufen
		for (int i = height + mun_plus; i < height + mun_plus + SuperMain.profil.mecha.linkeWaffe.getSlotCount(); i++) {
			Point pp = new Point(0, i);
			if (getItembyPoint(pp) == null) {
				// slot is leer
				// dahinter was drin ?
				for (int j = i + 1; j < height + mun_plus + SuperMain.profil.mecha.linkeWaffe.getSlotCount(); j++) {
					Point ppp = new Point(0, j);
					if (getItembyPoint(ppp) != null) {
						// noch einer dahinter--> den nach vorne holen
						getItembyPoint(ppp).Location.y = i;
						// noch was schief gelaufen?
						break;

					}
				}

			}
		}

		// rechtz

		for (int i = height + mun_plus; i < height + mun_plus + SuperMain.profil.mecha.rechteWaffe.getSlotCount(); i++) {
			Point pp = new Point(width - 1, i);
			if (getItembyPoint(pp) == null) {
				// slot is leer
				// dahinter was drin ?
				for (int j = i + 1; j < height + mun_plus + SuperMain.profil.mecha.rechteWaffe.getSlotCount(); j++) {
					Point ppp = new Point(width - 1, j);
					if (getItembyPoint(ppp) != null) {
						// noch einer dahinter--> den nach vorne holen
						getItembyPoint(ppp).Location.y = i;
						// noch was schief gelaufen?
						break;

					}
				}

			}
		}

	}

	private ItemLocationDimension getItembyPoint(Point selected_place) {
		int i = 0;
		for (ItemLocationDimension item : items) {
			if (item.Location.equals(selected_place))
				return items.get(i);
			i++;
		}
		return null;
	}

	/**
	 * decreases Ammo of left or right weapon
	 * 
	 * @param linke
	 *            <BR>
	 *            if linke is true the rocketlauncher ammo will be decreased
	 *            <BR>
	 *            if false the gatling ammo will be decreased
	 */
	public void decAktuellMunitionCount(boolean linke) {
		if (linke) {
			if (getItembyPoint(new Point(0, height + mun_plus)) == null)
				return;
			MunPack munpack = (MunPack) getItembyPoint(new Point(0, height + mun_plus)).Item;
			munpack.amount--;
			if (munpack.amount <= 0) {
				items.remove(getItembyPoint(new Point(0, height + mun_plus)));
				reorganizeWeaponSlots();
				refreshButtons();
			}
		} else {
			if (getItembyPoint(new Point(width - 1, height + mun_plus)) == null)
				return;
			MunPack munpack = (MunPack) getItembyPoint(new Point(width - 1, height + mun_plus)).Item;
			munpack.amount--;
			if (munpack.amount <= 0) {
				items.remove(getItembyPoint(new Point(width - 1, height + mun_plus)));
				reorganizeWeaponSlots();
				refreshButtons();
			}
		}

	}

	/**
	 * Returns a copie of all items placed in the bag. Equipped items will not
	 * be returned.
	 * 
	 * @return copie of all bagged items
	 */
	public Equipable[] getItems() {
		LinkedList<Equipable> res = new LinkedList<Equipable>();
		for (ItemLocationDimension item : items) {
			if (item.Location.y < height && item.Location.x < width) {
				res.add(item.Item);
			}
		}
		return res.toArray(new Equipable[0]);
	}

	/**
	 * Returns a LinkedList of all items
	 * 
	 * @return all items
	 */
	public LinkedList<ItemLocationDimension> getAllItems() {
		return items;
	}

	/**
	 * Sets equipped items and items in Bag to given items
	 * 
	 * @param items
	 */
	public void setItems(LinkedList<ItemLocationDimension> items) {
		this.items = items;
	}

	/**
	 * Returns copie of all equipped items (no munition)
	 * 
	 * @return copie of all equipped items
	 */
	public Equipment[] getEquippedItems() {
		LinkedList<Equipment> res = new LinkedList<Equipment>();
		for (ItemLocationDimension item : items) {
			if (item.Location.y >= height && item.Item instanceof Equipment) {
				res.add((Equipment) item.Item);
			}
		}
		return res.toArray(new Equipment[0]);
	}

	/**
	 * Returns number of bullets for left or right weapon
	 * 
	 * @param linke
	 *            <BR>
	 *            if linke is true the rocketlauncher ammo will be decreased
	 *            <BR>
	 *            if false the gatling ammo will be decreased
	 * @return number of bullets
	 */
	public int getAktuellMunitionCount(boolean linke) {
		int amount = 0;
		if (linke) {
			for (int i = height + mun_plus; i < height + mun_plus + SuperMain.profil.mecha.linkeWaffe.getSlotCount(); i++) {
				Point pp = new Point(0, i);
				if (getItembyPoint(pp) != null) {
					amount += ((MunPack) getItembyPoint(pp).Item).amount;

				}

			}
		} else {
			for (int i = height + mun_plus; i < height + mun_plus + SuperMain.profil.mecha.rechteWaffe.getSlotCount(); i++) {
				Point pp = new Point(width - 1, i);
				if (getItembyPoint(pp) != null) {
					amount += ((MunPack) getItembyPoint(pp).Item).amount;

				}

			}
		}

		return amount;
	}

	/**
	 * Returns bullets for left or right weapon
	 * 
	 * @param linke
	 *            <BR>
	 *            if linke is true the rocketlauncher ammo will be decreased
	 *            <BR>
	 *            if false the gatling ammo will be decreased
	 * @return bullets
	 */
	public Munition getActuelMunition(boolean linke) {
		if (linke) {
			if (getItembyPoint(new Point(0, height + mun_plus)) == null)
				return null;
			MunPack munpack = (MunPack) getItembyPoint(new Point(0, height + mun_plus)).Item;
			return munpack.mun;
		} else {
			if (getItembyPoint(new Point(width - 1, height + mun_plus)) == null)
				return null;
			MunPack munpack = (MunPack) getItembyPoint(new Point(width - 1, height + mun_plus)).Item;
			return munpack.mun;
		}

	}

	/**
	 * Removes specified item
	 * 
	 * @param package1
	 */
	public void remove(Equipable package1) {
		int i = 0;
		for (ItemLocationDimension item : items) {
			if (item.Item == package1) {
				items.remove(i);
				return;
			}
			i++;
		}

	}

	/**
	 * Checks all equipment-requirements and unequips items, that don't fullfill
	 * them
	 */
	public void checkAllRequirementsAgain() {
		boolean etwas_entfernt = false;

		for (Equipment equip : SuperMain.profil.mecha.bag.getEquippedItems()) {
			if (!equip.RequirementsFullfiled()) {
				etwas_entfernt = true;
				Point p = getFirstPlaceForItem(equip.getEquipDimension());
				// versuch in bag zu packen
				ItemLocationDimension item = null;
				for (ItemLocationDimension item_ : items) {
					if (item_.Item == equip) {
						item = item_;
						break;
					}

				}

				if (p != null) {
					item.Location = p;
				} else {
					// dropp it
					dropItem(item);
				}
				break;
			}

		}
		if (etwas_entfernt)
			checkAllRequirementsAgain();
	}

	/**
	 * Returns a collection of all buttons
	 * 
	 * @return all buttons
	 */
	public Collection<myButton> getAllButtons() {
		// TODO Auto-generated method stub
		return buttons;
	}

	public void save(BufferedWriter bw) {
		throw new RuntimeException("not yet implemented");

	}

	/**
	 * Loads saved bag of current profile
	 * 
	 * @return bag
	 */
	public Bag loadAsObject() {
		File f = new File(SuperMain.ordner + "profile/" + SuperMain.profil.getName() + "/" + "bag.cfgo");
		ObjectInputStream bw;
		try {
			if (!f.exists())
				f.createNewFile();
			bw = new ObjectInputStream(new FileInputStream(f));
			Bag bag = (Bag) bw.readObject();
			bw.close();
			return bag;
		} catch (IOException e) {
			System.err.println("Bag konfig laden(Obj) fehlgeschlagen: " + e.getMessage());
			return null;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			SuperMain.out(e);
			return null;
		}
	}

	/**
	 * Saves current bag in profile folder
	 * 
	 * @return bag saved?
	 */
	public boolean saveAsObject() {
		File f = new File(SuperMain.ordner + "profile/" + SuperMain.profil.getName() + "/" + "bag.cfgo");
		ObjectOutputStream bw;
		try {
			if (!f.exists())
				f.createNewFile();
			bw = new ObjectOutputStream(new FileOutputStream(f));
			bw.writeObject(this);
			bw.close();

		} catch (IOException e) {
			System.err.println("Bag konfigc (Obj) speichern fehlgeschlagen: " + e.getMessage());
			return false;
		}
		return true;

	}

	public void tryToPutInWeaponSlot(Equipable mun) {
		LinkedList<ItemLocationDimension> toput = new LinkedList<ItemLocationDimension>();
		for (ItemLocationDimension d : items) {
			if (d.Item == mun) {
				toput.add(d);
			}
		}

		for (ItemLocationDimension d : toput.toArray(new ItemLocationDimension[0])) {
			tryToPutAtPlace(d, new Point(width - 1, height + 2 + mun_plus));
			tryToPutAtPlace(d, new Point(0, height + 4));
			reorganizeWeaponSlots();
			refreshButtons();
		}

	}

}
