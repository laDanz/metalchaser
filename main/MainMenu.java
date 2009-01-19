package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import mechPeck.Mecha;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import sock.DedicatedServer;
import Classes.GameState;
import Classes.HighscoreEintrag;
import Classes.Key;
import Classes.OGL;
import Classes.OpenAlClip;
import Classes.Profil;
import Classes.Statistics;
import Classes.Steuerung;
import Classes.Texture;
import Classes.Vektor2D;
import Classes.Vektor3D;
import Classes.myButton;
import Classes.myColor;
import Classes.myText;
import Classes.v3;

/**
 * Handels the Main Menue logic
 * 
 * @author paule
 * 
 */
public class MainMenu implements GameState {
	private String state = "0";

	private boolean hitted = false;

	public static int intern_state;

	private Texture[] bg;

	public static final int NEUES_PROFIL = 1;

	public static final int MainMenu = 2;

	public static final int Einstellungen = 3;

	private LinkedList<Profil> profile;

	// int sound = 5;

	static public int keytobeChanged = -1;

	static private boolean first = true;

	private int mecha_rot = 0;

	// myButton links;
	// myButton rechts;
	// myButton auswahlen;

	myButton button_exit;

	myButton button_play;

	myButton button_resume;

	myButton button_profile;

	// PROFIEL
	myButton button_new_profile, button_keyboard;

	myButton accept, zuruck, nextprofil, prevprofil;

	myButton nextBild, prevBild, nextWapp, prevWapp;

	myButton[] changeKey;

	myButton soundp;

	myButton soundm;

	Texture langmetal, soundbar0, soundbar1, laden;

	// MAINMENU
	Texture dropdown;

	Vektor3D reinrollvektor;

	public static Profil temp_profil;

	/**
	 * kleinen trick damit ein esc druck nicht gleich alles nacheinander
	 * schließt
	 */
	private boolean esc_was_down = false;

	static OpenAlClip introO = null, introO2 = null;

	/**
	 * Constructor
	 */
	public MainMenu() {
		esc_was_down = false;
		once();
		changeInternState(NEUES_PROFIL);
		profileLaden();
		Mouse.setGrabbed(false);

		if (introO == null) {
			introO = new OpenAlClip(SuperMain.ordner + "sound/intro.ogg");
			// danach intro2 abspielen
			introO.addEndAction(new Runnable() {
				//@override
				public void run() {
					introO2.play();

				}
			});
		}
		if (introO2 == null) {
			introO2 = new OpenAlClip(SuperMain.ordner + "sound/menutheme.ogg");
			// Loop
			introO2.addEndAction(new Runnable() {
				//@override
				public void run() {
					introO2.play();

				}
			});
		}

		introO.play();

		/*
		 * links = new myButton("img/button/pfeil_links", -7, -2, 4, 1,
		 * onLinks(), "Links"); rechts = new myButton("img/button/pfeil_rechts",
		 * 3, -2, 4, 1, onRechts(), "Rechts"); auswahlen = new
		 * myButton("img/button/button", -2, -2, 4, 1, onAuswahlen(), "");
		 */
		// Einstellbildschirmkomponenten
		nextBild = new myButton("img/button/pfeil_rechts", 5, 4, 1, 1, onNextBild(), "");
		prevBild = new myButton("img/button/pfeil_links", 4, 4, 1, 1, onPrevBild(), "");
		nextprofil = new myButton("img/button/pfeil_rechts", -4.5, 4, 0.3, 0.3, onNextProv(), "");
		prevprofil = new myButton("img/button/pfeil_links", -5, 4, 0.3, 0.3, onPrevProv(), "");
		soundp = new myButton("img/button/pfeil_rechts", 6, -2, 0.5, 0.5, onSoundP(), "");
		soundm = new myButton("img/button/pfeil_links", 3, -2, 0.5, 0.5, onSoundM(), "");
		nextWapp = new myButton("img/button/pfeil_rechts", 0, 4, 1, 1, onNextWappen(), "");
		prevWapp = new myButton("img/button/pfeil_links", -1, 4, 1, 1, onPrevWappen(), "");
		accept = new myButton("img/button/ok", 3, -5, 2, 1, onAccept(), "");
		zuruck = new myButton("img/button/cancel", 5, -5, 2, 1, onZuruck(), "");
		button_keyboard = new myButton("img/button/key", 4, -0.8, 2, 1, onKeyboard(), "");
		button_new_profile = new myButton("img/button/new_profile", 4, 0, 2, 1, onNewProfile(), "");

		makeKeyButtons();

		makeMMbuttons();

		reinrollvektor = new v3();
		// nue wenn profil geladen ist
		if (SuperMain.profil != null)
			changeInternState(MainMenu);

	}

	private void makeMMbuttons() {
		double i = 2;
		double minus_pro = 0.8;

		button_play = new myButton("img/button/play", -1, i, 2, 1, null, "");
		button_play.setText("");
		button_play.setAction(onPlay());
		button_play.setTooltip("Instant Action1");
		i -= minus_pro;
		int next_level = checkWichLevelWouldBeNext();
		if (next_level > 0 && next_level < 5) {
			button_resume = new myButton("img/button/resume", -1, i, 2, 1, null, "");
			button_resume.setText("");
			button_resume.setAction(onResume());
			button_resume.setTooltip("continue last journey");
			i -= minus_pro;
		} else if (button_resume != null) {
			button_resume.setVisible(false);
		}
		button_profile = new myButton("img/button/profile", -1, i, 2, 1, null, "");
		button_profile.setText("");
		button_profile.setAction(onEinstellungen());
		i -= minus_pro;
		button_exit = new myButton("img/button/exit", -1, i, 2, 1, null, "");
		button_exit.setText("");
		button_exit.setAction(onBeenden());
		i -= minus_pro;

	}

	private Runnable onPrevProv() {
		return new Runnable() {
			//@override
			public void run() {
				// index des gerade ausgewaehlten profils
				int old = profile.indexOf(SuperMain.profil);
				int neu = old - 1;
				if (neu < 0)
					neu = profile.size() - 1;

				loadProfil(profile.get(neu));

			}
		};
	}

	private Runnable onNextProv() {

		return new Runnable() {
			//@override
			public void run() {
				// index des gerade ausgewaehlten profils
				int old = profile.indexOf(SuperMain.profil);
				int neu = old + 1;
				if (neu >= profile.size())
					neu = 0;

				loadProfil(profile.get(neu));

			}
		};
	}

	// FIXME umbauen!
	private Runnable onResume() {
		return new Runnable() {
			//@override
			public void run() {
				// Aus den gespeicherten scores rausfinden bei welchem level wir
				// gerade sind.
				int level = checkWichLevelWouldBeNext();
				if (level == 0) {
					// Neu Anfangen
					Mecha.deleteMechaSave();
				}

				if (level == 5) {
					// für alle schon eine score erzielt!
					level = 0;
				}

				SuperMain.out("Starte mit Level " + level);

				state = "LevelPlay,level/level" + level + ".xml";

				// Spezialfall level 4, vllt anderer StartPkt
				if (level == 4) {
					Mecha.loadAsObject();
					state = Mecha.new_state;
				}

				if (SuperMain.open_server)
					SuperMain.server = new DedicatedServer();

				// introO2.fadeOut(9);

			}
		};
	}

	//@override
	public void doFinalizeActions() {
		// TODO Auto-generated method stub

	}

	public static void stopIntroMusic() {
		if (introO2 != null)
			introO2.fadeOut(2);
	}

	private Runnable onSoundM() {
		// TODO Auto-generated method stub
		return new Runnable() {

			public void run() {
				if (temp_profil.getSound_gain() > 0)
					temp_profil.setSound_gain(temp_profil.getSound_gain() - 1);

			}

		};
	}

	private Runnable onSoundP() {
		// TODO Auto-generated method stub
		return new Runnable() {

			public void run() {
				if (temp_profil.getSound_gain() < 10)
					temp_profil.setSound_gain(temp_profil.getSound_gain() + 1);

			}

		};
	}

	private Runnable onNewProfile() {
		return new Runnable() {
			public void run() {
				changeInternState(NEUES_PROFIL);

			}
		};

	}

	private Runnable onKeyboard() {
		// TODO Auto-generated method stub
		return new Runnable() {

			public void run() {
				boolean b = !changeKey[0].isVisible();
				for (myButton bu : changeKey) {
					bu.setVisible(b);
				}

			}

		};
	}

	private Runnable onChangeKey(final int id_, final int count_) {
		// TODO Auto-generated method stub
		return new Runnable() {
			int id = id_;

			int count = count_;

			public void run() {
				changeKey[count].setText("change");
				keytobeChanged = id_;
			}

		};
	}

	private Runnable onZuruck() {
		// TODO Auto-generated method stub
		return new Runnable() {

			public void run() {
				changeInternState(MainMenu);
				temp_profil = null;
			}

		};
	}

	private Runnable onAccept() {
		// TODO Auto-generated method stub
		return new Runnable() {

			public void run() {
				temp_profil.save();
				profileLaden();
				loadProfil(temp_profil);
			}

		};
	}

	private Runnable onPrevWappen() {
		// TODO Auto-generated method stub
		return new Runnable() {

			public void run() {
				temp_profil.prevWappen();

			}

		};
	}

	private Runnable onNextWappen() {
		// TODO Auto-generated method stub
		return new Runnable() {

			public void run() {
				temp_profil.nextWappen();

			}

		};
	}

	private Runnable onPrevBild() {
		// TODO Auto-generated method stub
		return new Runnable() {

			public void run() {
				temp_profil.prevBild();

			}

		};
	}

	private Runnable onNextBild() {
		// TODO Auto-generated method stub
		return new Runnable() {

			public void run() {
				temp_profil.nextBild();

			}

		};
	}

	private Runnable onEinstellungen() {
		// TODO Auto-generated method stub
		return new Runnable() {

			public void run() {

				temp_profil = new Profil(SuperMain.profil);
				updateKeyButtons();
				changeInternState(Einstellungen);
			}

		};
	}

	private Runnable onProfilverwaltung() {
		// TODO Auto-generated method stub
		return new Runnable() {

			public void run() {
				SuperMain.profil = new Profil("null");
				profileLaden();
				changeInternState(NEUES_PROFIL);

			}

		};
	}

	private Runnable onPlay() {

		return new Runnable() {

			public void run() {
				// immer mit level 1 anfangen
				// -> scores löschen mecha löschen
				boolean b = Mecha.deleteMechaSave();
				// SuperMain.out("Mecha " + (b ? "" : "nicht") + " erfolgreich
				// gelöscht");
				LevelPlay.deleteAllStatistics();

				state = "LevelPlay,level/level0.xml";
				SuperMain.toRun.add(new Runnable() {
					//@override
					public void run() {
						Profil.mecha.nachladen();

					}
				});
				// introO2.fadeOut(9);
			}

		};
	}

	private Runnable onLastSave() {
		// TODO Auto-generated method stub
		return null;
	}

	private int checkWichLevelWouldBeNext() {
		int level = 0;
		int scorefurlevel = Statistics.getAllForLevel(SuperMain.ordner + "level/level" + level + ".xml").length;
		while ((scorefurlevel) > 0) {
			level++;
			// wenn mi level2 weiter gemacht->danach level 4
			if (level == 3) {
				level++;
			}

			if (level == 2) {
				// Mit level 2 oder 3 weitergemacht?
				if (Statistics.getAllForLevel(SuperMain.ordner + "level/level1.xml")[0].getState_on_Exit().endsWith(
						"3.xml")) {
					level++;
				}
			}

			scorefurlevel = Statistics.getAllForLevel(SuperMain.ordner + "level/level" + level + ".xml").length;
		}
		return level;
	}

	private Runnable onBeenden() {

		return new Runnable() {

			public void run() {
				state = "EndState";
			}

		};
	}

	private Runnable onAuswahlen() {

		return new Runnable() {

			public void run() {
				// neues profil erstellen
				if (profile.getFirst().getName().equals("create_new_profil")) {
					String name = "Player";
					name = JOptionPane.showInputDialog("Name: ");
					if (name == null || name.equals("create_new_profil") || name.length() < 1) {
						return;
					}
					// profil bereits vorhanden?
					for (Profil p_ : profile) {
						if (p_.getName().equalsIgnoreCase(name))
							return;
					}
					Profil p = new Profil(name);
					p.save();
					loadProfil(p);
					return;
				}

				// profil laden
				loadProfil(profile.getFirst());
			}

		};
	}

	/**
	 * Wechselt das Profil, indem es in SuperMain.profil geladen wird und
	 * speichert es zusätzlich als letztes geaeähltes ab.<br>
	 * Anschliessend wird ins mainMenu gewechselt.
	 * 
	 * @param p
	 */
	protected void loadProfil(Profil p) {
		changeInternState(MainMenu);
		loadProfilNoStateChange(p);
	}

	/**
	 * Wechselt das Profil, indem es in SuperMain.profil geladen wird und
	 * speichert es zusätzlich als letztes geaeähltes ab.
	 * 
	 * @param p
	 */
	protected void loadProfilNoStateChange(Profil p) {
		SuperMain.profil = p;
		makeMMbuttons();
		saveLastProfile();
	}

	private void saveLastProfile() {
		File f = new File(SuperMain.ordner + "conf.ini");
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				SuperMain.out(e);
			}
		}
		try {
			FileWriter fw = new FileWriter(f);
			fw.write("lastprofile=" + SuperMain.profil.getName());
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			SuperMain.out(e);
		}

	}

	double swing_till;
	String new_profil_name;

	private String new_profile_state;

	private int new_profile_state_show_count;

	private void changeInternState(int to) {
		intern_state = to;
		esc_was_down = false;
		if (intern_state == this.MainMenu) {
			reinrollvektor = new v3(0, 5.75, 0);
			swing_till = -1.2;
		}
		if (intern_state == NEUES_PROFIL) {
			new_profil_name = "player";
			new_profile_state = "";
			new_profile_state_show_count = 0;
		}
		if (intern_state == this.Einstellungen) {
			SuperMain.toRun.add(new Runnable() {
				public void run() {
					// Nur wenn mecha geladen werden muss laden anzeigen
					Laden l;
					if (SuperMain.profil.mecha == null) {
						l = new Laden();
						l.addText("Lade Mecha");
						l.render();
						Display.update();
					}
					try {
						temp_profil.initMecha();
					} catch (NullPointerException e) {
						// wird vermutlich durch eingabe eines ""Profilnames
						// ausgelöst!
						// nochmal auffordern zur eingabe!
						changeInternState(NEUES_PROFIL);
					}
					l = null;
					Laden.last_instance = null;
				}
			});

		}
	}

	private void profileLaden() {
		profile = new LinkedList<Profil>();
		File f = new File(SuperMain.ordner + "profile");
		for (File f_ : f.listFiles()) {
			if (f_.getAbsolutePath().endsWith(".svn"))
				continue;
			if (f_.isDirectory() && f_.exists()) {
				Profil p = Profil.load(f_.getAbsolutePath() + "/conf.cfg");
				profile.add(p);
			}
		}
		// profile.add(new Profil("create_new_profil"));
		if (profile.size() == 0) {
			// Keine Profile Vorhanden!
			changeInternState(NEUES_PROFIL);
			return;
		}
		// ansonsten letztes Profil laden
		File ff = new File(SuperMain.ordner + "conf.ini");
		if (ff.exists()) {
			try {
				BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(ff)));
				String s = bf.readLine();
				while (s != null) {
					if (s.startsWith("lastprofile")) {
						String name = s.split("=")[1];
						for (Profil p : profile) {
							if (p.getName().equals(name)) {
								loadProfil(p);
								return;
							}
						}
						// das letzte profil wirde nicht gefunden
						loadProfil(profile.getFirst());
					}
					s = bf.readLine();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				SuperMain.out(e);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				SuperMain.out(e);
			}
		} else {
			loadProfil(profile.getFirst());
		}
	}

	private Runnable onLinks() {
		// TODO Auto-generated method stub
		return new Runnable() {

			public void run() {
				if (profile != null && profile.size() > 1) {
					Profil first = profile.removeFirst();
					profile.addLast(first);
				}

			}

		};
	}

	private Runnable onRechts() {
		return new Runnable() {

			public void run() {
				if (profile != null && profile.size() > 1) {
					Profil last = profile.removeLast();
					profile.addFirst(last);
				}

			}

		};
	}

	public String getState() {
		return state;
	}

	public void logic() {

		switch (intern_state) {
		case NEUES_PROFIL:
			while (Keyboard.next()) {
				// nur auf KeyDown reagieren
				if (!Keyboard.getEventKeyState())
					break;
				char c = Keyboard.getEventCharacter();
				int key = Keyboard.getEventKey();
				if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
					// Buchstabe gedrückt
					new_profil_name += c;

				}
				if (key == Keyboard.KEY_DELETE || key == Keyboard.KEY_BACK) {
					new_profil_name = new_profil_name.substring(0, Math.max(0, new_profil_name.length() - 1));
				}
				if (key == Keyboard.KEY_ESCAPE) {
					hitted = true;
					changeInternState(Einstellungen);
				}
				if (key == Keyboard.KEY_RETURN) {
					newProfile(new_profil_name);
				}
			}
			break;
		case MainMenu:
			if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) && esc_was_down) {
				state = "EndState";
			}
			if (!Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
				esc_was_down = true;
			}

			double oldy = reinrollvektor.getX2();
			double newy = oldy;
			if (oldy != 0) {
				if (oldy > 5.5) {
					newy -= 0.13;// zu anfang eventuel etwas langsamer
				} else if (oldy > swing_till) {
					newy -= 0.13;
				} else {
					newy += Math.min(0.01, -oldy + swing_till);
					swing_till = -1;
				}
				reinrollvektor.setX2(newy);
			}
			break;
		case Einstellungen:
			if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
				if (keytobeChanged == -1) {
					if (!hitted) {
						changeInternState(MainMenu);
						hitted = true;
					}
				} else {
					keytobeChanged = -1;
					updateKeyButtons();
					hitted = true;
				}
			} else if (Keyboard.next()) {
				if (keytobeChanged > -1) {
					temp_profil.steuerung.setBelegung(keytobeChanged, new Key(true, Keyboard.getEventKey()));
					keytobeChanged = -1;
					updateKeyButtons();
				}
			} else if (Mouse.next()) {
				if (keytobeChanged > -1 && Mouse.getEventButtonState()) {
					temp_profil.steuerung.setBelegung(keytobeChanged, new Key(false, Mouse.getEventButton()));
					keytobeChanged = -1;
					updateKeyButtons();
				}
			} else {
				hitted = false;
			}
			int dx = Mouse.getDX();
			int dy = Mouse.getDY();
			if (dx != 0 && Mouse.isButtonDown(0) && Mouse.getX() < OGL.screenwh.getX1() / 2) {
				mecha_rot += dx;
			}
			if (!Mouse.isButtonDown(0) || Mouse.getX() > OGL.screenwh.getX1() / 2) {
				mecha_rot--;
			}
			break;

		default:
			break;
		}

		main.SuperMain.selection = OGL.selection(Mouse.getX(), Mouse.getY(), this);

	}

	/**
	 * prueft ob der name zulaessig ist, und ob das Profil bereits existiert.<br>
	 * Anschiessend wird es aktiviert und in den einstellungen bildschirm
	 * gewechselt.
	 * 
	 * @param name
	 * @return
	 */
	private boolean newProfile(String name) {
		if (name == null || name.length() < 1) {
			changeInternState(Einstellungen);
			return false;
		}
		// profil bereits vorhanden?
		for (Profil p_ : profile) {
			if (p_.getName().equalsIgnoreCase(name)) {
				new_profil_name = "";
				new_profile_state = "exists!";
				new_profile_state_show_count = 5;
				return false;
			}
		}
		Profil p = new Profil(name);
		p.save();
		profile.add(p);
		loadProfilNoStateChange(p);
		onEinstellungen().run();
		return true;

	}

	private void makeKeyButtons() {
		changeKey = new myButton[8];
		int count = 0;
		for (int i : new Integer[] { Steuerung.MOVE_VOR, Steuerung.MOVE_ZURUCK, Steuerung.STRAVE_LINKS,
				Steuerung.STRAVE_RECHTS, Steuerung.DREH_LINKS, Steuerung.DREH_RECHTS, Steuerung.ROCKETL,
				Steuerung.GATLING }) {
			changeKey[count] = new myButton("img/button/button", -6, 4 - count, 5, 1, onChangeKey(i, count), Steuerung
					.getDescription(i)
					+ " ");
			changeKey[count].setVisible(false);
			count++;
		}
	}

	private void updateKeyButtons() {
		// changeKey = new myButton[8];
		int count = 0;
		for (int i : new Integer[] { Steuerung.MOVE_VOR, Steuerung.MOVE_ZURUCK, Steuerung.STRAVE_LINKS,
				Steuerung.STRAVE_RECHTS, Steuerung.DREH_LINKS, Steuerung.DREH_RECHTS, Steuerung.ROCKETL,
				Steuerung.GATLING }) {
			changeKey[count].setText(Steuerung.getDescription(i) + " " + temp_profil.steuerung.getBelegung(i));

			count++;
		}
	}

	/**
	 * Single Configurations
	 */
	public void once() {

		new Thread() {
			//@override
			public void run() {
				HighscoreEintrag.writeLocaltoOnline();
			}
		}.start();

		bg = new Texture[4];
		try {
			bg[NEUES_PROFIL] = SuperMain.loadTex("img/menu/mainmenu.jpg");
			bg[MainMenu] = SuperMain.loadTex("img/menu/mainmenu.jpg");
			bg[Einstellungen] = SuperMain.loadTex("img/menu/mainmenu.jpg");

			dropdown = SuperMain.loadTex("img/menu/dropdown.png");
			langmetal = SuperMain.loadTex("img/menu/metal_lang.png");
			soundbar0 = SuperMain.loadTex("img/button/sbe.png");
			laden = SuperMain.loadTex("img/menu/laden.png");
			soundbar1 = SuperMain.loadTex("img/button/sbf.png");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			SuperMain.out(e);
		}

	}

	public void render() {
		OGL.setColor(myColor.WHITE);

		// if(intern_state!=NEUES_PROFIL)
		OGL.Hintergrund(bg[intern_state]);

		if (first) {
			try {
				if (!SuperMain.windowsmode) {
					OGL.setFullscreen(!SuperMain.dev && !SuperMain.fullscreenfailed);
				}
				first = false;
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				SuperMain.out(e1);
			}
		}

		// etwas weg
		Vektor3D versch = new v3(0, 0, -14);
		OGL.verschieb(versch);
		// Buttons
		switch (intern_state) {
		case NEUES_PROFIL:
		case Einstellungen:
			if (temp_profil != null)
				OGL.viereck(new Vektor2D(4, 2), new Vektor2D(2, 2), temp_profil.getBild(), 0);
			if (temp_profil != null)
				OGL.viereck(new Vektor2D(-1, 2), new Vektor2D(2, 2), temp_profil.getWappen(), 0, 1.1);
			OGL.viereck(new v3(3, -5, -0.1), new v3(4, 8, 0), langmetal, 0, 1.1);
			OGL.viereck(new v3(4, -2, -0.05), new v3(2, 1, 0), soundbar0, 0, 1.1);
			if (temp_profil != null)
				OGL.viereck_texCoord(new v3(4.05, -1.95, -0.03), new v3(1.9 * (temp_profil.getSound_gain() / 10.), 0.9,
						0), soundbar1, 0, 1.1, new Vektor2D(0, 0), new Vektor2D(
						1 * (temp_profil.getSound_gain() / 10.), 1));
			myText.setSelected_text(SuperMain.TEXT_CONSOLE_SMALL);
			myText.setSelected_big_text(SuperMain.TEXT_CONSOLE_BIG);
			if (temp_profil != null)
				myText.out(temp_profil.getName(), new v3(4, 1.5, 0.1), new v3(0.5, 0.5, 1), myColor.GREEN, 1.1, 0);
			myText.setSelected_text(SuperMain.TEXT_METAL);
			myText.setSelected_big_text(SuperMain.TEXT_METAL);
			for (myButton b : new myButton[] { nextWapp, prevWapp, nextBild, prevBild, accept, zuruck, button_keyboard,
					button_new_profile, soundp, soundm }) {
				b.render();
			}

			Vektor3D mecha_ver = new Vektor3D(-3, -6, -5);
			OGL.verschieb(mecha_ver);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_BLEND);
			OGL.rot(mecha_rot, v3.y_axis);
			if (temp_profil != null)
				if (temp_profil.mecha != null && temp_profil.mecha.skellet != null
						&& temp_profil.mecha.skellet.getTorso() != null
						&& temp_profil.mecha.skellet.getTorso().listhandle != 0)
					temp_profil.mecha.render();
			OGL.rot(-mecha_rot, v3.y_axis);
			OGL.verschieb(mecha_ver.negiere());

			for (myButton b : changeKey) {
				if (b.isVisible())
					b.render();
			}
			if (intern_state != NEUES_PROFIL)
				break;
			// neues profil bereich##############
			OGL.setOrthoOn();
			double cm = OGL.screenwh.getX1() / 21;

			OGL.viereck(new v3(1 * cm, 3 * cm, 0), new v3(20 * cm, 10 * cm, 0), laden, 0, 1.1);

			Vektor3D scaleT = new Vektor3D(0.4 * cm, 0.4 * cm, 1);
			Vektor3D wo = new Vektor3D(4.5 * cm, 5.8 * cm, 0);

			myText.setSelected_big_text(SuperMain.TEXT_CONSOLE_BIG);
			myText.setSelected_text(SuperMain.TEXT_CONSOLE_SMALL);

			myText.out("profile name:", wo.add(new Vektor3D(0, 0, -0.2)), scaleT, new Vektor3D(1, 1, 1), 1.1, 0);
			wo = wo.add(new v3(0, -0.5 * cm, 0));
			if (SuperMain.fps <= 30) {
				myText.out(new_profil_name + "|", wo.add(new Vektor3D(0, 0, -0.2)), scaleT, new Vektor3D(1, 1, 1), 1.1,
						0);
			}
			myText.out(new_profil_name, wo.add(new Vektor3D(0, 0, -0.2)), scaleT, new Vektor3D(1, 1, 1), 1.1, 0);

			if (new_profile_state_show_count > 0) {
				if (SuperMain.fps == 1)
					new_profile_state_show_count--;
				wo = wo.add(new v3(0, -0.5 * cm, 0));
				myText.out(new_profile_state, wo.add(new Vektor3D(0, 0, -0.2)), scaleT, new Vektor3D(1, 1, 1), 1.1, 0);
			}

			OGL.setOrthoOff();
			// OGL.viereck(new Vektor2D(0, 0), new Vektor2D(2, 2),
			// profile.getFirst().getBild(), 0);
			// OGL.viereck(new Vektor2D(1, 2), new Vektor2D(2, 2),
			// profile.getFirst().getWappen(), 0);
			break;
		// ENDE NEUES_PROFIL BEREICH
		case MainMenu:
			Vektor3D scale = new v3(1.5, 1.5, 1.5);
			OGL.verschieb(reinrollvektor);
			OGL.skaliere(scale);
			OGL.viereck(new v3(-1.5, -2, -0.2), new v3(3, 6, 0), dropdown, 0, 1.1);
			for (myButton b : new myButton[] { button_play, button_profile, button_exit, button_resume, nextprofil,
					prevprofil }) {
				if (b != null && b.isVisible())
					b.render();
			}

			OGL.skaliere(scale.reziproke());

			// Profilname
			double scale_t = 0.5;
			myText.setSelected_text(SuperMain.TEXT_CONSOLE_SMALL);
			myText.setSelected_big_text(SuperMain.TEXT_CONSOLE_BIG);
			if (SuperMain.profil != null)
				myText.out(SuperMain.profil.getName(), new v3(-6, 6, 0.1), new v3(scale_t, scale_t, 1), myColor.BLACK,
						1.1, 0);
			myText.setSelected_text(SuperMain.TEXT_METAL);
			myText.setSelected_big_text(SuperMain.TEXT_METAL);

			OGL.verschieb(reinrollvektor.negiere());

			break;

		default:
			break;
		}

		OGL.verschieb(versch.negiere());
	}
}
