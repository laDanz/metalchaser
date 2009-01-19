package main;

import java.util.LinkedList;

import org.lwjgl.input.Keyboard;

import Classes.GameState;
import Classes.KeyListener;
import Classes.OGL;
import Classes.Vektor3D;
import Classes.myColor;
import Classes.v3;
import Fenster.AnimFenster;
import anim.SkelettZ300;
import anim.WinkelSet;

/**
 * Animate Window for animating the Models
 * @author ladanz
 *
 */
public class AnimateIt implements GameState, KeyListener {

	String state = "0";

	Vektor3D backup = new v3(0, -1, -10);

	// hier ändern wenn anderes!
	static SkelettZ300 skelett;

	static boolean kalibrating;

	int rotate_all_y = 0;

	private static WinkelSet[] winkelset_save;

	boolean dauer_ani;

	int ani_state = 0;

	/**
	 * For Managing the calibrations. 0: Nothing will calibrated<br>
	 * ks=1 ROber, ks=2 ROber, ks=3 RUnter, ks=5 RFoot
	 */
	static int kalibrate_state = 0;

	/**
	 * Constructor
	 */
	public AnimateIt() {
		kalibrate_state = 0;

		dauer_ani = false;
		ani_state = 0;
		// skelett = new SkelettSentry(0);
		skelett = new SkelettZ300("Z300");
		winkelset_save = new WinkelSet[10];
		for (int i = 0; i < 10; i++) {
			winkelset_save[i] = new WinkelSet();
		}

		new AnimFenster(0, 0, 200, 400);
		System.out.println("##AnimateIt##");
		System.out.println("QAY -> Torso ; WSX -> Weapon ; EDC -> oSchenkel");
		System.out.println("RFV -> uSchenkel ; TGB -> Foot");
		System.out.println("+lShift -> invers");
		System.out.println("Mecha erhöehen: P");
		System.out.println("1-0 Speicher abrufen ; + lShift Speicher setzten");
		System.out.println("F1-F10 zu Speicherplatz interpolieren");
		System.out.println("F11 daueranimation ; F12 aus");
		System.out.println("i --> invertieren");
	}

	//@override
	public void doFinalizeActions() {
		// TODO Auto-generated method stub

	}

	public String getState() {
		// TODO Auto-generated method stub
		return state;
	}

	/**
	 * Simple Event
	 */
	public void onKeyDown(int key) {

	}

	/**
	 * Simple Event
	 */
	public void onKeyUp(int key) {
		// TODO Auto-generated method stub

	}

	/**
	 * Simple Event
	 */
	public void onMouseDown(int key) {
		// TODO Auto-generated method stub

	}

	/**
	 * Simple Event
	 */
	public void onMouseUp(int key) {
		// TODO Auto-generated method stub

	}

	public void logic() {
		daueraniVerwaltung();
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			OGL.finished = true;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			rotate_all_y++;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_I)) {
			invert();
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			rotate_all_y--;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
			AnimFenster.cb_rechtesbein.setSelected(!AnimFenster.cb_rechtesbein.isSelected());
		}
		double delta = 0.5;
		if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
				backup = backup.add(new v3(delta * Math.cos(Math.toRadians(-rotate_all_y + 0)), 0, delta
						* Math.sin(Math.toRadians(-rotate_all_y + 0))));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
				backup = backup.add(new v3(-delta * Math.cos(Math.toRadians(-rotate_all_y + 0)), 0, -delta
						* Math.sin(Math.toRadians(-rotate_all_y + 0))));
			}
		} else {
			// shift und hoch/runter --> kamera winekl verndern
			if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
				backup = backup.add(new v3(0, 0.2, 0));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
				backup = backup.add(new v3(0, -0.2, 0));
			}
		}
		// kalibrierungs modus
		double change = 1;
		WinkelSet tochange = skelett.getWinkelset();
		if (kalibrate_state > 0) {
			change = 0.05;
			if (kalibrate_state % 2 == 0) {
				tochange = skelett.getRotWS();
			} else {
				tochange = skelett.getVerschiebWS();
			}
		}
		// bewegung ohne shift plus, mit shift minus,LEERTASTE 0 SETZTEN
		// //rechtes schift=linkes bein
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			if (Keyboard.isKeyDown(Keyboard.KEY_O)) {
				tochange.setRHydra_stauch(tochange.getRHydra_stauch() - 0.02 * change);
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_L)) {
				tochange.setLHydra_stauch(tochange.getLHydra_stauch() - 0.02 * change);
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_P)) {
				tochange.setHeight(tochange.getHeight() - 0.02 * change);
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
				tochange.setTorso(tochange.getTorso().add(new v3(-1 * change, 0, 0)));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
				tochange.setTorso(tochange.getTorso().add(new v3(0, -1 * change, 0)));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_Z)) {
				tochange.setTorso(tochange.getTorso().add(new v3(0, 0, -1 * change)));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
				tochange.setWeapon1(tochange.getWeapon1().add(new v3(-1 * change, 0, 0)));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
				tochange.setWeapon1(tochange.getWeapon1().add(new v3(0, -1 * change, 0)));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_X)) {
				tochange.setWeapon1(tochange.getWeapon1().add(new v3(0, 0, -1 * change)));
			}
			if (!AnimFenster.cb_rechtesbein.isSelected()) {
				// EDC fr rOber
				if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
					tochange.setROber(tochange.getROber().add(new v3(-1 * change, 0, 0)));
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
					tochange.setROber(tochange.getROber().add(new v3(0, -1 * change, 0)));
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_C)) {
					tochange.setROber(tochange.getROber().add(new v3(0, 0, -1 * change)));
				}
				// RFV fr rUnter
				if (Keyboard.isKeyDown(Keyboard.KEY_R)) {
					tochange.setRUnter(tochange.getRUnter().add(new v3(-1 * change, 0, 0)));
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_F)) {
					tochange.setRUnter(tochange.getRUnter().add(new v3(0, -1 * change, 0)));
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_V)) {
					tochange.setRUnter(tochange.getRUnter().add(new v3(0, 0, -1 * change)));
				}
				// TGB fr rFoot
				if (Keyboard.isKeyDown(Keyboard.KEY_T)) {
					tochange.setRFoot(tochange.getRFoot().add(new v3(-1 * change, 0, 0)));
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_G)) {
					tochange.setRFoot(tochange.getRFoot().add(new v3(0, -1 * change, 0)));
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_B)) {
					tochange.setRFoot(tochange.getRFoot().add(new v3(0, 0, -1 * change)));
				}
			} else {// linkes
				// EDC fr lOber
				if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
					tochange.setLOber(tochange.getLOber().add(new v3(-1 * change, 0, 0)));
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
					tochange.setLOber(tochange.getLOber().add(new v3(0, -1 * change, 0)));
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_C)) {
					tochange.setLOber(tochange.getLOber().add(new v3(0, 0, -1 * change)));
				}
				// RFV fr lUnter
				if (Keyboard.isKeyDown(Keyboard.KEY_R)) {
					tochange.setLUnter(tochange.getLUnter().add(new v3(-1 * change, 0, 0)));
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_F)) {
					tochange.setLUnter(tochange.getLUnter().add(new v3(0, -1 * change, 0)));
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_V)) {
					tochange.setLUnter(tochange.getLUnter().add(new v3(0, 0, -1 * change)));
				}
				// TGB fr lFoot
				if (Keyboard.isKeyDown(Keyboard.KEY_T)) {
					tochange.setLFoot(tochange.getLFoot().add(new v3(-1 * change, 0, 0)));
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_G)) {
					tochange.setLFoot(tochange.getLFoot().add(new v3(0, -1 * change, 0)));
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_B)) {
					tochange.setLFoot(tochange.getLFoot().add(new v3(0, 0, -1 * change)));
				}

			}
		} else if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			if (Keyboard.isKeyDown(Keyboard.KEY_O)) {
				tochange.setRHydra_stauch(0);
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_L)) {
				tochange.setLHydra_stauch(0);
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_P)) {
				tochange.setHeight(0);
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
				tochange.setTorso((new v3(0, tochange.getTorso().getX2(), skelett.getWinkelset().getTorso().getX3())));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
				tochange.setTorso((new v3(tochange.getTorso().getX1(), 0, skelett.getWinkelset().getTorso().getX3())));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_Z)) {
				tochange.setTorso((new v3(tochange.getTorso().getX1(), tochange.getTorso().getX2(), 0)));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
				tochange.setWeapon1((new v3(0, tochange.getWeapon1().getX2(), skelett.getWinkelset().getWeapon1()
						.getX3())));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
				tochange.setWeapon1((new v3(tochange.getWeapon1().getX1(), 0, skelett.getWinkelset().getWeapon1()
						.getX3())));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_X)) {
				tochange.setWeapon1((new v3(tochange.getWeapon1().getX1(), tochange.getWeapon1().getX2(), 0)));
			}
			if (!AnimFenster.cb_rechtesbein.isSelected()) {
				// EDC fr rOber
				if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
					tochange.setROber(

					new v3(0, tochange.getROber().getX2(), skelett.getWinkelset().getROber().getX3()));
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
					tochange.setROber(

					new v3(tochange.getROber().getX1(), 0, skelett.getWinkelset().getROber().getX3()));
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_C)) {
					tochange.setROber(

					new v3(tochange.getROber().getX1(), skelett.getWinkelset().getROber().getX2(), 0));
				}
				// RFV fr rUnter
				if (Keyboard.isKeyDown(Keyboard.KEY_R)) {
					tochange.setRUnter(

					new v3(0, tochange.getRUnter().getX2(), skelett.getWinkelset().getRUnter().getX3()));
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_F)) {
					tochange.setRUnter(

					new v3(tochange.getRUnter().getX1(), 0, skelett.getWinkelset().getRUnter().getX3()));
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_V)) {
					tochange.setRUnter(

					new v3(tochange.getRUnter().getX1(), skelett.getWinkelset().getRUnter().getX2(), 0));
				}
				// TGB fr rFoot
				if (Keyboard.isKeyDown(Keyboard.KEY_T)) {
					tochange.setRFoot(

					new v3(0, tochange.getRFoot().getX2(), skelett.getWinkelset().getRFoot().getX3()));
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_G)) {
					tochange.setRFoot(

					new v3(tochange.getRFoot().getX1(), 0, skelett.getWinkelset().getRFoot().getX3()));
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_B)) {
					tochange.setRFoot(

					new v3(tochange.getRFoot().getX1(), skelett.getWinkelset().getRFoot().getX2(), 0));
				}
			} else {
				// EDC fr lOber
				if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
					tochange.setLOber(

					new v3(0, tochange.getLOber().getX2(), skelett.getWinkelset().getLOber().getX3()));
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
					tochange.setLOber(

					new v3(tochange.getLOber().getX1(), 0, skelett.getWinkelset().getLOber().getX3()));
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_C)) {
					tochange.setLOber(

					new v3(tochange.getLOber().getX1(), skelett.getWinkelset().getLOber().getX2(), 0));
				}
				// RFV fr lUnter
				if (Keyboard.isKeyDown(Keyboard.KEY_R)) {
					tochange.setLUnter(

					new v3(0, tochange.getLUnter().getX2(), skelett.getWinkelset().getLUnter().getX3()));
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_F)) {
					tochange.setLUnter(

					new v3(tochange.getLUnter().getX1(), 0, skelett.getWinkelset().getLUnter().getX3()));
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_V)) {
					tochange.setLUnter(

					new v3(tochange.getLUnter().getX1(), skelett.getWinkelset().getLUnter().getX2(), 0));
				}
				// TGB fr lFoot
				if (Keyboard.isKeyDown(Keyboard.KEY_T)) {
					tochange.setLFoot(

					new v3(0, tochange.getLFoot().getX2(), skelett.getWinkelset().getLFoot().getX3()));
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_G)) {
					tochange.setLFoot(

					new v3(tochange.getLFoot().getX1(), 0, skelett.getWinkelset().getLFoot().getX3()));
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_B)) {
					tochange.setLFoot(

					new v3(tochange.getLFoot().getX1(), skelett.getWinkelset().getLFoot().getX2(), 0));
				}
			}
		} else if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			if (Keyboard.isKeyDown(Keyboard.KEY_O)) {
				tochange.setRHydra_stauch(tochange.getRHydra_stauch() + 0.02 * change);
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_L)) {
				tochange.setLHydra_stauch(tochange.getLHydra_stauch() + 0.02 * change);
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_P)) {
				tochange.setHeight(tochange.getHeight() + 0.02 * change);
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
				tochange.setTorso(tochange.getTorso().add(new v3(1 * change, 0, 0)));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
				tochange.setTorso(tochange.getTorso().add(new v3(0, 1 * change, 0)));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_Z)) {
				tochange.setTorso(tochange.getTorso().add(new v3(0, 0, 1 * change)));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
				tochange.setWeapon1(tochange.getWeapon1().add(new v3(1 * change, 0, 0)));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
				tochange.setWeapon1(tochange.getWeapon1().add(new v3(0, 1 * change, 0)));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_X)) {
				tochange.setWeapon1(tochange.getWeapon1().add(new v3(0, 0, 1 * change)));
			}
			if (!AnimFenster.cb_rechtesbein.isSelected()) {
				// EDC fr rOber
				if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
					tochange.setROber(tochange.getROber().add(new v3(1 * change, 0, 0)));
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
					tochange.setROber(tochange.getROber().add(new v3(0, 1 * change, 0)));
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_C)) {
					tochange.setROber(tochange.getROber().add(new v3(0, 0, 1 * change)));
				}
				// RFV fr rUnter
				if (Keyboard.isKeyDown(Keyboard.KEY_R)) {
					tochange.setRUnter(tochange.getRUnter().add(new v3(1 * change, 0, 0)));
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_F)) {
					tochange.setRUnter(tochange.getRUnter().add(new v3(0, 1 * change, 0)));
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_V)) {
					tochange.setRUnter(tochange.getRUnter().add(new v3(0, 0, 1 * change)));
				}
				// TGB fr rFoot
				if (Keyboard.isKeyDown(Keyboard.KEY_T)) {
					tochange.setRFoot(tochange.getRFoot().add(new v3(1 * change, 0, 0)));
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_G)) {
					tochange.setRFoot(tochange.getRFoot().add(new v3(0, 1 * change, 0)));
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_B)) {
					tochange.setRFoot(tochange.getRFoot().add(new v3(0, 0, 1 * change)));
				}
			} else {
				// EDC fr lOber
				if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
					tochange.setLOber(tochange.getLOber().add(new v3(1 * change, 0, 0)));
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
					tochange.setLOber(tochange.getLOber().add(new v3(0, 1 * change, 0)));
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_C)) {
					tochange.setLOber(tochange.getLOber().add(new v3(0, 0, 1 * change)));
				}
				// RFV fr rUnter
				if (Keyboard.isKeyDown(Keyboard.KEY_R)) {
					tochange.setLUnter(tochange.getLUnter().add(new v3(1 * change, 0, 0)));
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_F)) {
					tochange.setLUnter(tochange.getLUnter().add(new v3(0, 1 * change, 0)));
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_V)) {
					tochange.setLUnter(tochange.getLUnter().add(new v3(0, 0, 1 * change)));
				}
				// TGB fr rFoot
				if (Keyboard.isKeyDown(Keyboard.KEY_T)) {
					tochange.setLFoot(tochange.getLFoot().add(new v3(1 * change, 0, 0)));
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_G)) {
					tochange.setLFoot(tochange.getLFoot().add(new v3(0, 1 * change, 0)));
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_B)) {
					tochange.setLFoot(tochange.getLFoot().add(new v3(0, 0, 1 * change)));
				}
			}
		}

		winkelsetVerwaltung();
	}

	private void daueraniVerwaltung() {
		if (dauer_ani) {
			if (!skelett.animation) {
				skelett.animateTo(winkelset_save[ani_state++]);
				if (ani_state > 9)
					ani_state = 0;
			}
		}

	}

	private void invert() {
		skelett.invert();
	}

	private void winkelsetVerwaltung() {
		// WINKELSET VERWALTUNG
		// SHIFT SETZTEN, ZAHL ABFRAGE
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))) {
			if (Keyboard.next()) {
				int key = Keyboard.getEventKey();
				if (key >= Keyboard.KEY_1 && key <= Keyboard.KEY_0) {
					winkelset_save[key - Keyboard.KEY_1] = new WinkelSet(skelett.getWinkelset());
				}
			}
		} else {
			if (Keyboard.next()) {
				int key = Keyboard.getEventKey();
				if (key >= Keyboard.KEY_1 && key <= Keyboard.KEY_0) {
					skelett.setWinkelset(winkelset_save[key - Keyboard.KEY_1]);
				}
				// F Tasten --> Animation
				if (key >= Keyboard.KEY_F1 && key <= Keyboard.KEY_F10) {
					skelett.animateTo(winkelset_save[key - Keyboard.KEY_F1]);
				}
				if (key == Keyboard.KEY_F11) {
					dauer_ani = true;
				}
				if (key == Keyboard.KEY_F12) {
					dauer_ani = false;
				}

			}
		}

	}

	public void once() {
		// TODO Auto-generated method stub

	}

	public void render() {

		OGL.verschieb(backup);

		OGL.rot(rotate_all_y, new v3(0, 1, 0));
		skelett.render(kalibrate_state, 0);
		OGL.rot(-rotate_all_y, new v3(0, 1, 0));
		OGL.setColor(myColor.GREEN);
		OGL.viereck(new Vektor3D(-10, 0, -10), new Vektor3D(20, 0, 20));
		OGL.setColor(myColor.WHITE);
		OGL.verschieb(backup.negiere());

	}

	/**
	 * Save the current angleset
	 */
	public static void save() {
		skelett.saveAll(winkelset_save);
	}

	/**
	 * Load angleset
	 */
	public static void load() {
		LinkedList<WinkelSet> res = new LinkedList<WinkelSet>();
		for (WinkelSet ws : skelett.loadAll())
			res.add(ws);

		while (res.size() < 10) {
			res.add(new WinkelSet());
		}
		winkelset_save = res.toArray(new WinkelSet[10]);
	}

	/**
	 * Initializing calibration
	 */
	public static void kalibrate() {
		kalibrate_state++;
		if (kalibrate_state == 7) {
			kalibrate_state = 0;
			// FIXME daten fürs andere bein verwenden?
		}
		AnimFenster.bu_kalib.setText(getKalibText());

	}

	private static String getKalibText() {

		switch (kalibrate_state) {
		case 0:
			return "Kalibrierung";
		case 1:
		case 2:
			return "rOber";
		case 3:
		case 4:
			return "rUnter";
		case 5:
		case 6:
			return "rFoot";
		default:
			return "fehler";

		}
	}

}
