package main;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.Vector;

import lights.PositionalLight;
import lights.SunLight;
import mechPeck.Bag;
import mechPeck.Equipment;
import mechPeck.ItemLocationDimension;
import mechPeck.Mecha;
import mechPeck.StatsView;
import mechPeck.equipment.HealthPackage;
import mechPeck.equipment.Loot;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import other.LevelMusik;
import other.Object3Dputter;
import sock.Data;
import sock.MCClient;
import Classes.GameState;
import Classes.Geschoss;
import Classes.HighscoreEintrag;
import Classes.KeyListener;
import Classes.Level;
import Classes.OGL;
import Classes.Objekt;
import Classes.OpenAlClip;
import Classes.ParameterAble;
import Classes.Player;
import Classes.Profil;
import Classes.Radar;
import Classes.RotateAble;
import Classes.Statistics;
import Classes.Steuerung;
import Classes.Texture;
import Classes.TimeAnaliser;
import Classes.Vektor2D;
import Classes.Vektor3D;
import Classes.Zielkreuz;
import Classes.inGameConsole;
import Classes.myButton;
import Classes.myColor;
import Classes.myText;
import Classes.v3;
import Fenster.DebugFenster;
import anim.WinkelSet;
import drops.EquipDrop;
import drops.HealthDrop;
import drops.SchrottDrop;
import drops.VerteilbarePunkteDrop;

/**
 * Handels the Game Routines
 * 
 * @author ladanz
 * 
 */
public class LevelPlay implements GameState, ParameterAble, KeyListener {

	boolean timeanalyse = false;

	// static public Level l;
	public static Player p;

	static public LinkedList<Player> enemy;

	static boolean noclip = false;

	static int backupcount = 5;

	static JetPack jetty;

	private static String state = "0";

	public static String state_on_exit = "";

	static StatsView statsView;

	Texture hud, radarnadel, bullet, health, deadTexture, explosion;

	double dead_alpha_value;

	int radarnadel_count;

	static double zielkreuzfalt = 0;

	static public inGameConsole InGameConsole;

	// IGM Buttons
	static myButton igm_weiter, igm_beenden, igm_restart, next_level, start_level, show_highscores;

	// reiter
	static myButton reiter_button_stats, reiter_button_statistics, reiter_button_levelinfo;

	/**
	 * intern state management
	 */
	static int show_bag_count = 0;

	static int show_menu_count = 0;

	static boolean show_level_time = true;

	/**
	 * How many ticks should it take to show the bag
	 */
	static double show_bag_time = 40;

	boolean show_geh_anim;

	LinkedList<WinkelSet> gehanim = new LinkedList<WinkelSet>();

	public static Vektor3D startPkt;

	static public LinkedList<Geschoss> bullets;

	static public Radar radar;

	static int parametercount;

	static public boolean ziel_erreicht = false;

	/* LIGHT VARIABLES */
	float ambient[] = { 0f, 0f, 0f, 1f }; // color of overall scene lighting
	float lightDiffuse[] = { .9f, .9f, .6f, 1f }; // color of direct light
	float lightSpecular[] = { .9f, .9f, .6f, 1f }; // color of highlight
	float lightAmbient[] = { .5f, .0f, .0f, 1f }; // color of scattered light

	double cm = 1;

	static public String geladenes_level = null;

	private boolean mouse_grab;

	private int anim_frame;

	private int hueftwinkel;

	private int gehrichtung;

	static public int startPktNummer;

	static int reiter = 0;

	static final int REITER_STATS = 0;

	static final int REITER_STATISTICS = 1;

	static final int REITER_LEVELINFO = 2;

	static final int REITER_HIGHSCORE = 3;

	static Vektor3D stats_v, bag_v;

	static Vektor3D delta = new v3();

	static OpenAlClip musik, walk0, walk1, pickup, dead, zielerreicht;

	static public MCClient client;

	private static boolean deadanimation;

	v3 backup;

	private double deaddrehung;
	public double actual_mecha_velocity = 0;
	Zielkreuz zielkreuz;

	public static String deadtext = " = you are dead =       press return to retry      press ESC to quit      ";

	static boolean space_hitted;

	@SuppressWarnings("deprecation")
	/**
	 * Constructor
	 */
	public LevelPlay() {
		zielkreuz = new Zielkreuz();
		Mouse.setGrabbed(false);
		enemy = new LinkedList<Player>();
		hueftwinkel = gehrichtung = 0;
		startPktNummer = 0;
		dead_alpha_value = 0;
		deaddrehung = 0;
		deadanimation = false;
		mouse_grab = false;
		Laden.addText("LevelPlay()");
		if (SuperMain.profil == null) {
			SuperMain.profil = new Profil("LevelPlay");
			SuperMain.farbe = true;
			SuperMain.dev = true;
			SuperMain.gitter = true;
		}

		if (walk0 == null) {

			walk0 = new OpenAlClip("sound/walk/walk1.ogg");
			walk1 = new OpenAlClip("sound/walk/walk2.ogg");
		}
		
		if (pickup == null) 
			pickup = new OpenAlClip("sound/pickup.ogg");
		
		if (dead == null) 
			dead = new OpenAlClip("sound/dead.ogg");
		
		if (zielerreicht == null) 
			zielerreicht = new OpenAlClip("sound/finished.ogg");


		Laden.addText("init Models");
		// Models initialisieren
		SchrottDrop.initModel();
		EquipDrop.initModel();
		HealthDrop.initModel();
		VerteilbarePunkteDrop.initModel();
		SuperMain.profil.initMecha();

		// Versuche Mecha zu laden
		Mecha m = Mecha.loadAsObject();
		if (m != null) {
			Profil.mecha = m;
			LinkedList<ItemLocationDimension> temp = new LinkedList<ItemLocationDimension>();
			temp.addAll(m.bag.getAllItems());
			for (ItemLocationDimension i : temp) {
				if (i.Item instanceof Loot) {
					((Loot) i.Item).initTexture();
				}
				if (i.Item instanceof HealthPackage) {
					((HealthPackage) i.Item).initTexture();
				}
			}
			Profil.mecha.bag = new Bag();
			Profil.mecha.bag.setItems(temp);
			Profil.mecha.initNonLoadedObjects();
			SuperMain.profil.mecha.setVerteilbare_punkte(SuperMain.profil.mecha.getVerteilbare_punkte() + 10);
			SuperMain.out("Mecha erfolgreich geladen.");
		} else {
			Profil.mecha.bag = new Bag();

			Profil.mecha.initStats();
			Profil.mecha.deleteTempValues();
		}

		Laden.addText("start func");

		InGameConsole = new inGameConsole();
		InGameConsole.addText("mecha rdy");
		radar = new Radar();
		p = new Player();
		jetty = new JetPack();
		cm = OGL.screenwh.getX1() * 1 / 21;
		statsView = new StatsView(cm);
		stats_v = new v3(8 * cm, -1.5 * cm, 0);
		delta = new v3();
		state_on_exit = "";
		ziel_erreicht = false;
		bag_v = new v3(OGL.screenwh.getX1() / 2 + 7.5 * cm, -OGL.screenwh.getX2() + 1 * cm, 0);
		show_bag_count = 0;
		show_menu_count = 0;

		Laden.addText("init HUD");
		igm_weiter = new myButton("img/hud/reiter/neu", OGL.screenwh.getX1() + 0.5 * cm, 2.3 * cm, 3 * cm, 1 * cm,
				onIgmWeiter(), "resume");
		igm_restart = new myButton("img/hud/reiter/neu", OGL.screenwh.getX1() + 0.5 * cm, 1.3 * cm, 3 * cm, 1 * cm,
				onIgmRestart(), "restart");
		igm_beenden = new myButton("img/hud/reiter/neu", OGL.screenwh.getX1() + 0.5 * cm, 0.3 * cm, 3 * cm, 1 * cm,
				onIgmEnde(), "exit");
		next_level = new myButton("img/hud/reiter/neu", 15.1 * cm, 4.6 * cm - OGL.screenwh.getX2(), 1.2 * cm, 1 * cm,
				onNextLevel(), "GO");
		start_level = new myButton("img/hud/reiter/neu", 15.1 * cm, 4.6 * cm - OGL.screenwh.getX2(), 1.2 * cm, 1 * cm,
				onStartLevel(), "GO");

		reiter_button_stats = new myButton("img/hud/reiter/neu", 6.7 * cm, 4.6 * cm - OGL.screenwh.getX2(), 2.5 * cm,
				1 * cm, onReiter(REITER_STATS), "stats");
		reiter_button_statistics = new myButton("img/hud/reiter/neu", 9.5 * cm, 4.6 * cm - OGL.screenwh.getX2(),
				2.5 * cm, 1 * cm, onReiter(REITER_STATISTICS), "score");
		reiter_button_levelinfo = new myButton("img/hud/reiter/neu", 12.3 * cm, 4.6 * cm - OGL.screenwh.getX2(),
				2.5 * cm, 1 * cm, onReiter(REITER_LEVELINFO), "info");

		show_highscores = new myButton("img/hud/reiter/neu", 9.3 * cm, 6.5 * cm - OGL.screenwh.getX2(), 4.5 * cm,
				1 * cm, onReiter(REITER_HIGHSCORE), "highscore");

		// Werte initialisieren
		radarnadel_count = 0;
		startPkt = new v3();
		show_geh_anim = false;
		bullets = new LinkedList<Geschoss>();
		parametercount = 0;
		state = "0";

		// Fenster ausrichten
		int wid = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
		wid = (wid - Display.getDisplayMode().getWidth()) / 2 + Display.getDisplayMode().getWidth();
		int hei = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
		hei = (hei - Display.getDisplayMode().getHeight()) / 2;
		if (SuperMain.dev) {
			new DebugFenster(wid + 10, hei, 200, 200);
			// new Netzwerkfenster(wid + 10, hei + 210, 200, 200);
		}

		Laden.addText("ld texture");
		once();

		SuperMain.level = new Level();

	}

	private Runnable onStartLevel() {
		return new Runnable() {
			@Override
			public void run() {
				SuperMain.statistics.setStartTime();
				// Health nochmal updaten
				p.life = SuperMain.profil.mecha.getHealth();
				show_bag_count++;
			}
		};
	}

	private Runnable onReiter(final int reiter_stats2) {

		return new Runnable() {
			public void run() {
				reiter = reiter_stats2;

				if (reiter_stats2 == REITER_HIGHSCORE) {
					HighscoreEintrag.rebuildHighscores();
				}

			}
		};
	}

	private Runnable onNextLevel() {

		return new Runnable() {
			public void run() {
				state = state_on_exit;
				// Mecha nohcmal wegen eventuellen Änderungen speichern
				Profil.mecha.saveAsObject(state);
			}
		};
	}

	private Runnable onIgmEnde() {

		return new Runnable() {
			public void run() {
				if (state_on_exit != null && !state_on_exit.equals(""))
					state = state_on_exit;
				else
					state = "MainMenu";

			}
		};
	}

	private Runnable onIgmRestart() {

		return new Runnable() {
			public void run() {

				state = "LevelPlay," + geladenes_level;

			}
		};
	}

	private Runnable onIgmWeiter() {

		return new Runnable() {
			public void run() {
				onKeyDown(Keyboard.KEY_ESCAPE);

			}
		};
	}

	/**
	 * Load the stated Level
	 * 
	 * @param s
	 */
	void loadLevel(final String s) {

		geladenes_level = s + ",StartPkt#" + startPktNummer;
		Laden.loadBackgroundTexture("img/levelhints" + s.replace("\\", "/").substring(s.indexOf("/"), s.length())
				+ ".png");

		Laden.addText("ld level");
		// Level laden
		SuperMain.level.load(SuperMain.ordner + s);
		SuperMain.statistics = new Statistics(SuperMain.level);
		Statistics.printForLevel(s);

		// Startpkt filtern
		int startPkt_count = 0;
		boolean startpkt_zugewiesen = false;
		while (!startpkt_zugewiesen && startPktNummer >= 0) {
			for (Objekt o : SuperMain.level.getObjekte()) {
				if (o.getClass().toString().equalsIgnoreCase("class other.StartPkt")) {
					if (startPkt_count != startPktNummer) {
						startPkt_count++;
						continue;
					}
					startpkt_zugewiesen = true;
					p.setPosition(o.getPosition());
					startPkt = o.getPosition();
					try {
						p.blickrichtung = (Integer) o.getOptionValue(0);

					} catch (Exception e) {
						p.blickrichtung = 0;
						System.err.println("Fehler mit dem Startpkt #001");
					}
					if (SuperMain.dev) {
						DebugFenster.setPos(p.getPosition());
					}
					// StartPkt gefunden--> aussteigen
					break;
				}
			}
			if (!startpkt_zugewiesen)
				startPktNummer--;
		}
		for (Objekt o : SuperMain.level.getObjekte()) {
			radar.addObjektOfInterest(o);
			if (o.getClass().toString().equals("class other.LevelMusik")) {
				musik = new OpenAlClip(SuperMain.ordner + ((LevelMusik) o).getMusikFile());
				musik.addEndAction(new Runnable() {
					@Override
					public void run() {
						musik.play();

					}
				});
				// musik.play();
			}
		}
		System.out.println("LevelPlay +#+#+#+#+");
		System.out.println("W , A , S , D  for Movement");
		System.out.println("Arrows for Rotating ");
		System.out.println("[F]arbe, [G]itter");
		reiter = REITER_LEVELINFO;
		show_bag_count = 1;
		InGameConsole.addText("radar init");

		space_hitted = false;
		Laden.renderHitSpace();
		MainMenu.stopIntroMusic();
		musik.play();
	}

	@Override
	public void doFinalizeActions() {
		if (musik != null)
			musik.stop();

		// Alle lichter loeschen
		SunLight.global_light_count = 1;
		PositionalLight.global_light_count = 1;
	}

	public String getState() {

		return state;
	}

	public static void setState(String newstate) {
		state = newstate;
		GL11.glDisable(GL11.GL_LIGHTING);
	}

	/**
	 * Set parameters for LevelPlay
	 */
	public void setParameter(String s) {
		s = s.replace('\\', '/');
		System.out.println("Parameter gekriegt: " + s);
		if (s.startsWith("StartPkt")) {
			startPktNummer = Integer.parseInt(s.split("#")[1]);

			return;
		}
		if (parametercount == 0) {
			if (!s.startsWith("NETZ")) {
				if (s != null && s.length() > 0) {
					if (!s.startsWith("level/")) {
						s = "level/" + s;

					}

					loadLevel(s);
				}
			} else {
				// NETZWERK
				String serv = s.substring(4);
				client = new MCClient(serv);
				Laden.addText("kontakte server");
				String level = client.getLevel();
				client.client.startListening();
				System.out.println("temporäres level: " + level);
				loadLevel(level);

			}
		} else {
			if (state_on_exit.length() > 0) {
				state_on_exit += ",";
			}
			state_on_exit += s;
		}

		parametercount++;
	}

	private void move(Vektor3D delta) {
		if (noclip)
			return;
		Vektor3D old_pos = p.getPosition();
		// unzulaessige bewegung ???
		boolean geht = true;

		// x kleiner 0 oder z groessr 0
		if ((old_pos.getX1() + delta.getX1()) < 0 || (old_pos.getX3() + delta.getX3()) > 0) {
			geht = false;
		}
		// hintere level grenzen
		if ((old_pos.getX1() + delta.getX1()) > SuperMain.level.width
				|| (old_pos.getX3() + delta.getX3()) < -Math.abs(SuperMain.level.depth)) {
			geht = false;
		}

		// neue soll hoehe ausrechnen
		double righth;
		if (jetty.isActive()) {
			righth = jetty.getHeight();
		} else {
			righth = SuperMain.level.getHeight(p.getPosition().add(delta));
		}

		double dh = old_pos.getX2() - righth;

		// FIXME
		// Speed veringern bei groesseren hohen??? bei abstiegen
		// beschleunigen???
		{
			double fac = dh / 0.15;
			// max Wert 1 für fac
			if (Math.abs(fac) > 0.5) {
				fac = (fac / Math.abs(fac)) * 0.5;
			}
			if (Math.abs(fac) < -0.6) {
				fac = (fac / Math.abs(fac)) * 0.6;
			}
			delta = delta.mal(1 + fac);
			righth = SuperMain.level.getHeight(p.getPosition().add(delta));

			dh = old_pos.getX2() - righth;

		}

		// Grenze für hoch gehen
		if (dh < -0.25 * OGL.fps_anpassung && !jetty.isActive()) {
			geht = false;
		}

		// collision mit Objekt???
		double my_needed_platz = 0.3;// Math.max(Math.abs(p.getDimension().getX1()),
		// Math.abs(p.getDimension().getX3()));
		for (Objekt o : SuperMain.level.objekte) {
			if (o.getDimension() == null || o == this)
				continue;
			if (o instanceof Object3Dputter) {
				if (o.checkCollisionforObjekt(p.getPosition().add(delta))) {
					geht = false;
					if (o instanceof RotateAble && SuperMain.dev) {
						System.out.println("Winkel: "
								+ ((RotateAble) o).getDrehwinkel()
								+ " v: "
								+ Geschoss.getDimensionUnderDrehung(o.getDimension(), new v3(0, ((RotateAble) o)
										.getDrehwinkel(), 0)));
					}
				}
				continue;
			}

			Vektor3D abstand = o.getPosition().add(p.getPosition().mal(-1));

			// beweg ich mich drauf zu oder weg?
			double myangle = (Math.atan2(delta.getX1(), delta.getX3()));

			double hisangle = (Math.atan2(abstand.getX1(), abstand.getX3()));
			double delta_angel = Math.abs(Math.toDegrees(myangle - hisangle));

			if (delta_angel > 90 && delta_angel < 270)
				continue;

			double neededplatz = my_needed_platz
					+ Math.max(Math.abs(o.getDimension().getX1()), Math.abs(o.getDimension().getX3()));

			if (abstand.length() < neededplatz) {
				geht = false;
			}
		}

		// Spieler tatschlich auf neue Position setzten
		if (geht) {
			p.setPosition((new Vektor3D(delta.getX1() + p.getPosition().getX1(), righth, delta.getX3()
					+ p.getPosition().getX3())));

			show_geh_anim = true;

		} else {
			// if (show_geh_anim) {
			p.skelett.animateTo(new WinkelSet(), 10);
			// }
			show_geh_anim = false;

		}

		if (SuperMain.dev) {
			DebugFenster.setPos(LevelPlay.p.getPosition());
		}
	}

	/**
	 * KeyDown Action
	 */
	public void onKeyDown(int key) {
		if (key == Keyboard.KEY_SPACE || key == Keyboard.KEY_RETURN) {
			if (!space_hitted) {
				// Laden hart beenden
				Laden.last_instance.doFinalizeActions();
				Laden.last_instance = null;

			}
			space_hitted = true;
		}

		// bei go button(levelstart) mit enter starten
		if (key == Keyboard.KEY_RETURN && SuperMain.statistics.getSt_time() == 0) {
			onStartLevel().run();
		}
		// bei go button(levelstart) mit esc -> mainmenu
		if (key == Keyboard.KEY_ESCAPE && SuperMain.statistics.getSt_time() == 0) {
			state = "MainMenu";

		}

		// bei go button(nextlevel) mit enter starten
		if (key == Keyboard.KEY_RETURN && ziel_erreicht) {
			onNextLevel().run();
		}
		// bei go button(nextlevel) mit esc -> mainmenu
		if (key == Keyboard.KEY_ESCAPE && ziel_erreicht) {
			state = "MainMenu";
		}
		// bei deathscreen mit esc -> mainmenu
		if (key == Keyboard.KEY_ESCAPE && deadanimation) {
			state = "MainMenu";
		}
		// bei deathscreen mit enter nochmalversuchen
		if (key == Keyboard.KEY_RETURN && deadanimation) {
			// key = Keyboard.KEY_ESCAPE;
		}

		if (false && SuperMain.dev && key == Keyboard.KEY_H) {
			ZielErreicht("LevelPlay,level/level4.xml");
		}
		// nachdem das zieerreichtwurde UF KEINE TASTENANSCHKLAEGE MEHR
		// REAAGIEREN
		// bzw wenn noch nicht gestartet wurde
		// oder wenn man tot ist nur esc
		if (ziel_erreicht || SuperMain.statistics.getSt_time() == 0 || (deadanimation && key != (Keyboard.KEY_ESCAPE)))
			return;
		if (key == Keyboard.KEY_B || key == Keyboard.KEY_I) {
			show_bag_count++;
		}
		if (SuperMain.dev && key == Keyboard.KEY_F9) {
			p.hurt(10);
		}
		if (key == Keyboard.KEY_M) {
			show_menu_count++;
		}
		if (key == Keyboard.KEY_Z) {
			SuperMain.schatten = !SuperMain.schatten;
		}
		if (key == Keyboard.KEY_T) {
			reiter = REITER_STATISTICS;
		}
		if (key == Keyboard.KEY_R) {
			reiter = REITER_STATS;
		}
		if (key == (Keyboard.KEY_ESCAPE)) {
			if (show_bag_count > 0 || show_menu_count > 0) {
				if (show_bag_count > 0) {
					if (show_bag_count % 2 == 1) {
						show_bag_count++;
					} else {
						show_bag_count = 0;
					}

				}
				if (show_menu_count > 0) {
					if (show_menu_count % 2 == 1) {
						show_menu_count++;
					} else {
						show_menu_count = 0;
					}

				}

			} else {
				show_menu_count++;
			}
		}

		if (key == (Keyboard.KEY_F) && SuperMain.dev) {
			SuperMain.farbe = !SuperMain.farbe;
		}
		if (key == (Keyboard.KEY_L) && SuperMain.dev) {
			SuperMain.licht = !SuperMain.licht;
		}
		if (key == (Keyboard.KEY_G) && SuperMain.dev) {
			SuperMain.gitter = !SuperMain.gitter;
		}
		if (key == (Keyboard.KEY_T) && SuperMain.dev) {
			SuperMain.texture_anzeigen = !SuperMain.texture_anzeigen;
		}

	}

	/**
	 * KeyUp Action
	 */
	public void onKeyUp(int key) {
		// nachdem das zieerreichtwurde UF KEINE TASTENANSCHKLAEGE MEHR
		// REAAGIEREN
		if (ziel_erreicht)
			return;
	}

	/**
	 * Action Handlers
	 */
	public void onMouseDown(int key) {
		if (show_bag_count > 0)
			SuperMain.profil.mecha.bag.onMouseDown(key);

	}

	/**
	 * Action Handlers
	 */
	public void onMouseUp(int key) {
		if (show_bag_count > 0)
			SuperMain.profil.mecha.bag.onMouseUp(key);

	}

	byte last_dir = 0;

	public void logic() {

		selection = OGL.selection(512, 368, this);
		if (timeanalyse) {
			TimeAnaliser.startNewRound();
		}
		if (SuperMain.fps % 14 == 0 && deadanimation) {
			char cut = deadtext.charAt(0);
			deadtext = deadtext.substring(1) + cut;
		}
		// temporäre wertemwegen dauer+ zurücksetzten
		SuperMain.profil.mecha.deleteTempValues();
		// Equip effekte anwenden
		for (Equipment equip : SuperMain.profil.mecha.bag.getEquippedItems()) {

			equip.doEffekt();
		}
		checkAllRequirementsAgain();
		if (timeanalyse) {
			TimeAnaliser.addPoint("Equipment checken");
		}
		if (deadanimation) {
			if (deaddrehung > 90 || dead_alpha_value > 0)
				if (dead_alpha_value < 1) {
					dead_alpha_value += 0.03;
				}
			deaddrehung++;

		}
		double min_per_fps = -0.05;
		if (Level.getSelectedPosition() == null) {
			zielkreuzfalt -= min_per_fps;
			if (zielkreuzfalt > 1) {
				zielkreuzfalt = 1;
			}

		} else {
			zielkreuzfalt += min_per_fps;
			if (zielkreuzfalt < 0) {
				zielkreuzfalt = 0;
			}
		}

		if (SuperMain.server != null && SuperMain.fps % 4 == 0)
			SuperMain.server.sendDataAboutAllObjects();

		if (timeanalyse) {
			TimeAnaliser.addPoint("Server datensenden");
		}
		if (show_bag_count > 0) {
			if (show_bag_count % 2 == 1) {
				if (show_bag_count < show_bag_time) {
					show_bag_count += 2;
				}
			} else {
				show_bag_count -= 2;
				if (show_bag_count < 0)
					show_bag_count = 0;
			}

		}

		if (show_menu_count > 0) {
			if (show_menu_count % 2 == 1) {
				if (show_menu_count < show_bag_time) {
					show_menu_count += 2;
				}
			} else {
				show_menu_count -= 2;
				if (show_menu_count < 0)
					show_menu_count = 0;
			}

		}
		if (timeanalyse) {
			TimeAnaliser.addPoint("bag animieren");
		}
		if (SuperMain.dev) {
			DebugFenster.setframes(SuperMain.oldfps);
		}
		if (gehanim.size() == 0 && p.skelett != null) {
			gehanim.addAll(java.util.Arrays.asList(SuperMain.profil.mecha.skellet.loadAll("anim/Z300/walk.xml")));
		}
		if (show_geh_anim) {
			if (!p.skelett.animation) {
				anim_frame++;
				if (anim_frame == gehanim.size()) {
					anim_frame = 0;
				}
				if (anim_frame == 0) {
					walk1.stop();
					walk0.play();

				}
				if (anim_frame == gehanim.size() / 2) {
					walk0.stop();
					walk1.play();

				}

				p.skelett.animateTo(new WinkelSet(gehanim.getFirst()), 10);
				gehanim.addLast(gehanim.removeFirst());

			}
			show_geh_anim = false;
		}

		if (timeanalyse) {
			TimeAnaliser.addPoint("gehanimation");
		}
		if (jetty.isActive()) {
			jetty.tick(p);
			move(new v3());
		}

		radar.actualizeObjectPositions();
		radar.actualizePlayerPosition();
		if (timeanalyse) {
			TimeAnaliser.addPoint("radar aktualisieren");
		}
		// TODO hier die geschwindigkeitsberechnung einstellen in Metern Pro
		// sekunde
		// maximale geschwindigkeit
		double geschwindigkeit = 1 + SuperMain.profil.mecha.getSpeed() / 20;
		// Vorne und seitwärts gleichzeitig?
		if ((SuperMain.profil.steuerung.isDown(Steuerung.MOVE_VOR) || SuperMain.profil.steuerung
				.isDown(Steuerung.MOVE_ZURUCK))
				&& (SuperMain.profil.steuerung.isDown(Steuerung.STRAVE_LINKS) || SuperMain.profil.steuerung
						.isDown(Steuerung.STRAVE_RECHTS))) {
			geschwindigkeit = geschwindigkeit * Math.sin(Math.PI / 4);
		}
		// beschleunigung
		double acceleration_per_tick = 0.1;

		boolean is_accerlating = Keyboard.isKeyDown(SuperMain.profil.steuerung.getBelegung(Steuerung.MOVE_ZURUCK)
				.getId())
				|| Keyboard.isKeyDown(SuperMain.profil.steuerung.getBelegung(Steuerung.STRAVE_RECHTS).getId())
				|| Keyboard.isKeyDown(SuperMain.profil.steuerung.getBelegung(Steuerung.STRAVE_LINKS).getId())
				|| Keyboard.isKeyDown(SuperMain.profil.steuerung.getBelegung(Steuerung.MOVE_VOR).getId());

		actual_mecha_velocity += acceleration_per_tick * (is_accerlating ? 1 : -1);
		if (actual_mecha_velocity > geschwindigkeit) {
			actual_mecha_velocity = geschwindigkeit;
		}
		if (actual_mecha_velocity < 0) {
			actual_mecha_velocity = 0;
		}

		double x = actual_mecha_velocity * Math.cos(Math.toRadians(-p.blickrichtung + 0)) * OGL.fps_anpassung / 10.;
		double z = actual_mecha_velocity * Math.sin(Math.toRadians(-p.blickrichtung + 0)) * OGL.fps_anpassung / 10.;

		// steuerung
		// drehung mit Mausbewegung
		// sichtbarkeit der maus
		double dx = Mouse.getDX() / 15.;
		double dy = Mouse.getDY() / 15.;
		if (SuperMain.dev || show_menu_count > 0 || show_bag_count > 0 || deadanimation) {
			if (mouse_grab) {
				Mouse.setGrabbed(false);
				mouse_grab = false;
			}
			dy = dx = 0;
		} else {
			if (!mouse_grab) {
				Mouse.setGrabbed(true);
				mouse_grab = true;
			}
		}

		// Tastatur
		if (SuperMain.profil.steuerung.isDown(Steuerung.HEALTH_ANWENDEN)) {
			SuperMain.profil.mecha.health_anwenden();

		}
		if (SuperMain.profil.steuerung.isDown(Steuerung.NACHLADEN)) {
			SuperMain.profil.mecha.nachladen();
		}

		if (show_bag_count == 0 && show_menu_count == 0 && !deadanimation) {
			Vektor3D pos_alt = p.getPosition();
			if ((last_dir == 0 && actual_mecha_velocity > 0)
					|| Keyboard.isKeyDown(SuperMain.profil.steuerung.getBelegung(Steuerung.STRAVE_RECHTS).getId())) {
				move(new Vektor3D(x, 0, z));
				last_dir = 0;
			}
			if ((last_dir == 1 && actual_mecha_velocity > 0)
					|| Keyboard.isKeyDown(SuperMain.profil.steuerung.getBelegung(Steuerung.STRAVE_LINKS).getId())) {
				move(new Vektor3D(-x, 0, -z));
				last_dir = 1;
			}
			x = actual_mecha_velocity * Math.cos(Math.toRadians(-p.blickrichtung + 90)) * OGL.fps_anpassung / 10.;
			z = actual_mecha_velocity * Math.sin(Math.toRadians(-p.blickrichtung + 90)) * OGL.fps_anpassung / 10.;
			if ((last_dir == 2 && actual_mecha_velocity > 0)
					|| Keyboard.isKeyDown(SuperMain.profil.steuerung.getBelegung(Steuerung.MOVE_VOR).getId())) {
				move(new Vektor3D(-x, 0, -z));
				last_dir = 2;
			}
			if ((last_dir == 3 && actual_mecha_velocity > 0)
					|| Keyboard.isKeyDown(SuperMain.profil.steuerung.getBelegung(Steuerung.MOVE_ZURUCK).getId())) {
				move(new Vektor3D(x, 0, z));
				last_dir = 3;
			}
			Vektor3D delta = p.getPosition().sub(pos_alt);

			if (!(delta.getX1() == 0 && delta.getX3() == 0))
				gehrichtung = 180 + (int) Math.toDegrees(Math.atan2(delta.getX1(), delta.getX3()));
			hueftwinkel = gehrichtung - (int) p.blickrichtung;

			if (SuperMain.profil.steuerung.isDown(Steuerung.ROCKETL)) {
				if (SuperMain.profil.mecha.linkeWaffe.isFeuerbereit())
					SuperMain.profil.mecha.linkeWaffe.feuer();
			}
			if (SuperMain.profil.steuerung.isDown(Steuerung.GATLING)) {
				if (SuperMain.profil.mecha.rechteWaffe.isFeuerbereit())
					SuperMain.profil.mecha.rechteWaffe.feuer();
			}

			if (Keyboard.isKeyDown(SuperMain.profil.steuerung.getBelegung(Steuerung.DREH_RECHTS).getId()) || dx > 0) {
				if (dx > 0)
					p.blickrichtung -= dx;
				else
					p.blickrichtung -= 1;
			}
			if (Keyboard.isKeyDown(SuperMain.profil.steuerung.getBelegung(Steuerung.DREH_LINKS).getId()) || dx < 0) {
				if (dx < 0)
					p.blickrichtung -= dx;
				else
					p.blickrichtung += 1;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_UP) || dy > 0) {
				if (dy > 0) {
					p.yblick -= dy;
				} else {
					p.yblick -= 1;
				}
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_DOWN) || dy < 0) {
				if (dy < 0) {
					p.yblick -= dy;
				} else {
					p.yblick += 1;
				}
			}

			// Mouse wieder aufn miitlpkt setzen
			// if (dx == 0 && dy == 0 && Mouse.isButtonDown(1)){
			// Mouse.setCursorPosition((int) OGL.screenwh.getX1() / 2,
			// (int) OGL.screenwh.getX2() / 2);
			// }

			if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
				if (!jetty.isActive()) {
					jetty.start(p);
					move(new v3());
				}
			} else {
				if (jetty.isActive()) {
					jetty.stop();
					move(new v3());
				}
			}
			int d = -Mouse.getDWheel();
			if (d != 0) {
				d = d / Math.abs(d);
				backupcount = Math.min(10, Math.max(2, backupcount + d));

			}
		}// \steuerung
		if (timeanalyse) {
			TimeAnaliser.addPoint("tastatur abfrage");
		}
		p.logic();
		if (client != null && SuperMain.fps % 4 == 0) {
			Data d = new Data(p, client.id);
			client.client.send(d);
		}
		if (timeanalyse) {
			TimeAnaliser.addPoint("player logic");
		}
	}

	private void checkAllRequirementsAgain() {
		SuperMain.profil.mecha.bag.checkAllRequirementsAgain();
		if (LevelPlay.p.life > Profil.mecha.getHealth())
			LevelPlay.p.life = Profil.mecha.getHealth();
	}

	/**
	 * Initial settings
	 */
	public void once() {

		try {
			hud = SuperMain.loadTex("img/hud/hud.png");
			radarnadel = SuperMain.loadTex("img/hud/radarnadel.png");
			bullet = SuperMain.loadTex("img/hud/bullet.png");
			health = SuperMain.loadTex("img/hud/health.png");
			deadTexture = SuperMain.loadTex("img/dead.png");
			explosion = SuperMain.loadTex("img/env/explosion.png");
		} catch (IOException e) {
			e.printStackTrace();
			SuperMain.out(e);
		}

		// no overall scene lighting
		// setAmbientLight(ambient);
	}

	/**
	 * Set the color of a 'positional' light (a light that has a specific
	 * position within the scene). <BR>
	 * <BR>
	 * Params:<BR>
	 * an OpenGL light number (GL11.GL_LIGHT1),<BR>
	 * 'Diffuse': color of direct light from this source,<BR>
	 * 'Ambient': color of scattered light from this source <BR>
	 * 'Specular': color of this light reflected off a surface,<BR>
	 * position.<BR>
	 */
	public static void setLight(int GLLightHandle, float[] diffuseLightColor, float[] ambientLightColor,
			float[] specularLightColor, v3 position, v3 lightDirection_, float coneDegree) {

		FloatBuffer ltDiffuse = allocFloats(diffuseLightColor);
		FloatBuffer ltAmbient = allocFloats(ambientLightColor);
		FloatBuffer ltSpecular = allocFloats(specularLightColor);

		float[] position_ = new float[] { (float) position.getX1(), (float) position.getX2(), (float) position.getX3(),
				1 };
		float[] lightDirection = new float[] { (float) lightDirection_.getX1(), (float) lightDirection_.getX2(),
				(float) lightDirection_.getX3(), 1 };

		FloatBuffer ltPosition = allocFloats(position_);
		FloatBuffer ltDirection = allocFloats(lightDirection);

		GL11.glLight(GLLightHandle, GL11.GL_DIFFUSE, ltDiffuse); // color of
		// the
		// direct
		// light
		GL11.glLight(GLLightHandle, GL11.GL_AMBIENT, ltAmbient); // color of
		// the
		// reflected
		// light
		GL11.glLight(GLLightHandle, GL11.GL_SPECULAR, ltSpecular); // color of
		// the
		// highlight
		// (same as
		// direct)

		GL11.glLight(GLLightHandle, GL11.GL_POSITION, ltPosition);
		GL11.glLight(GLLightHandle, GL11.GL_SPOT_DIRECTION, ltDirection);
		GL11.glLightf(GLLightHandle, GL11.GL_SPOT_CUTOFF, coneDegree);

		GL11.glEnable(GLLightHandle); // Enable the light (GL_LIGHT1 - 7)
		// GL11.glLightf(GLLightHandle, GL11.GL_QUADRATIC_ATTENUATION,
		// .005F); // how light beam drops off
	}

	/**
	 * Set the color of the Global Ambient Light. Affects all objects in scene
	 * regardless of their placement.
	 */
	public static void setAmbientLight(float[] ambientLightColor) {
		FloatBuffer ltAmbient = allocFloats(ambientLightColor);
		GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, ltAmbient);
	}

	/**
	 * Set the position of a light to the given xyz. NOTE: Positional light
	 * only, not directional.
	 */
	public static void setLightPos(int GLLightHandle, float x, float y, float z) {
		float[] position = new float[] { x, y, z, 1 };
		GL11.glLight(GLLightHandle, GL11.GL_POSITION, allocFloats(position));

	}

	public static void setLightDir(int GLLightHandle, Vektor3D v) {

		setLightPos(GLLightHandle, (float) v.getX1(), (float) v.getX2(), (float) v.getX3());
	}

	public static void setLightDir(int GLLightHandle, float x, float y, float z) {
		float[] lightDirection = new float[] { (float) x, (float) y, (float) z, 1 };

		FloatBuffer ltDirection = allocFloats(lightDirection);
		GL11.glLight(GLLightHandle, GL11.GL_SPOT_DIRECTION, ltDirection);
	}

	/**
	 * Wurde getestet ob online geht?
	 */
	static boolean checked_online = false;

	/**
	 * Geht online?
	 */
	static boolean online_avaiable = false;

	public static int selection;

	private static Laden laden;

	public void render() {
		if (!space_hitted) {

			Laden.last_instance.render();
			return;
		}
		// Cameraposition initialisieren.
		double right = -2.2;
		double up = 2.3;
		double back = 1.5;
		double tilt = 3.9;

		backup = new v3(right, -backupcount / tilt - up, -backupcount - back);

		OGL.rot(p.yblick, new v3(1, 0, 0));
		OGL.verschieb(backup);
		OGL.rot(-p.blickrichtung - deaddrehung, new v3(0, 1, 0));

		Vektor3D um = p.getPosition().negiere();

		OGL.verschieb(um);

		// Es werde licht
		if (SuperMain.licht)
			GL11.glEnable(GL11.GL_LIGHTING);
		else
			GL11.glDisable(GL11.GL_LIGHTING);
		
		GL11.glEnable(GL11.GL_CULL_FACE);
		
		// setLightPos(GL11.GL_LIGHT1, 50, 20, -50);
		// taschenlampe
		// setLightPos(GL11.GL_LIGHT0, (float) p.getPosition().getX1(), (float)
		// p
		// .getPosition().getX2() + 2, (float) p.getPosition().getX3());
		// GL11.glPushMatrix();???

		// ####### HOHENLINIEN
		SuperMain.level.render(true);
		if (timeanalyse) {
			TimeAnaliser.addPoint("level rendern");
		}
		OGL.setColor(myColor.GREEN);
		if (Keyboard.isKeyDown(Keyboard.KEY_H)) {
			for (double x = p.getPosition().getX1() - 5; x < p.getPosition().getX1() + 5; x += 0.1) {
				for (double z = p.getPosition().getX3() - 5; z < p.getPosition().getX3() + 5; z += 0.1) {

					Vektor3D v = new Vektor3D(x, SuperMain.level.getHeight(x, z), z);
					OGL.line(1, v, v.add(new v3(0.01, 0.1, 0.01)));
				}
			}

		}
		if (timeanalyse) {
			TimeAnaliser.addPoint("hoehen linien");
		}
		GL11.glDisable(GL11.GL_LIGHTING);
		// ######## GSCHOSSE
		for (Geschoss g : bullets.toArray(new Geschoss[0])) {
			if (g.isAlive()) {
				g.render();
			} else {
				bullets.remove(g);
			}
		}
		if (timeanalyse) {
			TimeAnaliser.addPoint("geschosse");
		}
		GL11.glEnable(GL11.GL_LIGHTING);

		// ######## PLAYER
		p.render(hueftwinkel);

		for (Player enem : enemy) {
			enem.render();
		}

		if (timeanalyse) {
			TimeAnaliser.addPoint("player rendern");
		}
		/*
		 * Laserpointer { Vektor3D endpoint = Level.getSelectedPosition();
		 * Vektor3D richtung = new v3(-1 *
		 * Math.sin(Math.toRadians(LevelPlay.p.blickrichtung + 0)), -1
		 * Math.sin(Math.toRadians(LevelPlay.p.yblick)), -1
		 * Math.cos(Math.toRadians(LevelPlay.p.blickrichtung))).normierter(); if
		 * (endpoint == null) { OGL.setColor(myColor.RED);
		 * GL11.glDisable(GL11.GL_LIGHTING); OGL.line(1,
		 * Profil.mecha.linkeWaffe.getAustrittsPkt().add(p.getPosition()),
		 * Profil.mecha.linkeWaffe
		 * .getAustrittsPkt().add(richtung.mal(100)).add(p.getPosition()));
		 * OGL.line(1,
		 * Profil.mecha.rechteWaffe.getAustrittsPkt().add(p.getPosition()),
		 * Profil.mecha.rechteWaffe
		 * .getAustrittsPkt().add(richtung.mal(100)).add(p.getPosition())); }
		 * OGL.setColor(myColor.BLUE);
		 * 
		 * if (endpoint != null) { OGL.line(1,
		 * Profil.mecha.rechteWaffe.getAustrittsPkt().add(p.getPosition()),
		 * endpoint);
		 * 
		 * OGL.line(1,
		 * Profil.mecha.linkeWaffe.getAustrittsPkt().add(p.getPosition()),
		 * endpoint); }
		 * 
		 * OGL.setColor(myColor.WHITE); GL11.glEnable(GL11.GL_LIGHTING); }
		 * 
		 * if (timeanalyse) { TimeAnaliser.addPoint("laser pointer"); }
		 * 
		 * OGL.verschieb(um.negiere());
		 * 
		 * OGL.rot(p.blickrichtung, new v3(0, 1, 0));
		 * 
		 * OGL.verschieb(backup.negiere()); OGL.rot(-p.yblick, new v3(1, 0, 0));
		 */

		GL11.glDisable(GL11.GL_LIGHTING);

		// Muendungsfeuer
		{
			OGL.verschieb(Profil.mecha.rechteWaffe.getAustrittsPkt().add(p.getPosition()));

			double fac = (100. - (System.currentTimeMillis() - Profil.mecha.rechteWaffe.getLastTimeFeuered())) / 100;
			if (fac > 1) {
				fac = 1;
			}
			if (fac < 0) {
				fac = 1;
			}
			Vektor3D spann = new v3(-0.5, -0.5, 0).mal(1 - fac);
			OGL.rot(p.blickrichtung, v3.y_axis);
			OGL.viereck(spann, spann.mal(-2), explosion, 0, 0.5 * (1 - fac));
			OGL.rot(-p.blickrichtung, v3.y_axis);
			OGL.verschieb(Profil.mecha.rechteWaffe.getAustrittsPkt().add(p.getPosition()).negiere());

		}

		OGL.setOrthoOn();
		int swidth = (int) OGL.screenwh.getX1();
		int sheight = (int) OGL.screenwh.getX2();
		Vektor3D hud_pos = new Vektor3D(swidth * 6 / 21, -sheight * 0.97, 0);
		double fac = show_bag_count / show_bag_time;
		if (fac > 1)
			fac = 1;
		if (fac < 0)
			fac = 0;
		double fac1 = show_menu_count / show_bag_time;
		if (fac1 > 1)
			fac1 = 1;
		if (fac1 < 0)
			fac1 = 0;

		Vektor3D facv = new v3(Math.min(fac + fac1, 1), fac, 0);

		delta = new v3(-hud_pos.getX1(), -hud_pos.getX2(), 0).mal(facv);

		SuperMain.selection = orthoselection(Mouse.getX(), Mouse.getY());
		if (timeanalyse) {
			TimeAnaliser.addPoint("ortho selection");
		}
		Vektor3D health_pos = new Vektor3D(swidth * 6.6 / 21, sheight * 0.8 / 21, 0);
		Vektor3D console_v = new Vektor3D(hud_pos.getX1() + swidth * 5.25 / 21, sheight * 4 / 15.5, 0);
		Vektor3D radar_v = new Vektor3D(hud_pos.getX1() + swidth * 12.3 / 21, sheight * 2.7 / 15.5, 0);
		Vektor3D cons_sk = new v3(swidth * 0.25 / 21, sheight * 0.5 / 15.5, 0);

		// runter laufende zeit
		if (show_level_time && !deadanimation && show_bag_count == 0) {
			Vektor3D color = myColor.WHITE;
			if (SuperMain.statistics.getTimeLeftSeks() < 0) {
				color = myColor.ORANGE;
			} else if (SuperMain.statistics.getTimeLeftSeks() < 30) {
				if (SuperMain.fps < 30) {
					color = myColor.ORANGE;
				}
			}
			myText.out(SuperMain.statistics.getTimeLeftString(), new v3(17.8 * cm, 15 * cm, 0), new v3(0.5 * cm,
					0.5 * cm, 1 * cm), color, 1.1, 0);
		}

		// #######+++++++++++
		// DuBistTotMessage
		if (deadanimation) {
			OGL.viereck(new v3(7 * cm, 5 * cm, 0), new v3(8 * cm, 8 * cm, 0), deadTexture, 0, dead_alpha_value);
			myText.out(deadtext.substring(0, 27), new v3(8 * cm, 5.85 * cm, 0), new v3(0.3 * cm, 0.3 * cm, 0.3 * cm),
					myColor.WHITE, dead_alpha_value, 0);

		}

		// Fadenkreuz
		if (!deadanimation) {
			/*
			 * OGL.setColor(myColor.GREEN); OGL.line(1, new v3(10. * cm, 7.59 *
			 * cm, 0), new v3(11 * cm, 7.59 * cm, 0)); OGL.line(1, new v3(10.5 *
			 * cm, 7.59 * cm, 0), new v3(10.5 * cm, 7.09 * cm, 0));
			 * OGL.setColor(myColor.WHITE);
			 */
			zielkreuz.setFarbverlauf(myColor.GREEN, myColor.RED, zielkreuzfalt);
			zielkreuz.render(zielkreuzfalt);
		}

		OGL.verschieb(delta);
		{
			OGL.viereck(hud_pos, new Vektor3D(swidth, swidth, 0), hud, 0, 1.01 - dead_alpha_value);
			double health_fac = (0.0 + LevelPlay.p.life) / SuperMain.profil.mecha.getHealth();
			if (deadanimation) {
				health_fac = 0;
			}
			OGL.viereck_texCoord(health_pos, new Vektor3D(health_fac * swidth * 3.2 / 21, swidth * 3.2 / 21, 0),
					health, 0, 1.01 - dead_alpha_value, new Vektor2D(0, 0), new Vektor2D(health_fac, 1));

			OGL.verschieb(health_pos.add(new v3(1 * cm, 0 * cm, 0)));
			Vektor3D umm = new v3(0.5 * cm, 0.5 * cm, 1);
			OGL.skaliere(umm);
			myText.out((deadanimation ? "0" : LevelPlay.p.life) + "/" + SuperMain.profil.mecha.getHealth(),
					1.01 - dead_alpha_value);
			OGL.skaliere(umm.reziproke());
			OGL.verschieb(health_pos.add(new v3(1 * cm, 0 * cm, 0)).negiere());

			if (timeanalyse) {
				TimeAnaliser.addPoint("health+hud");
			}
			// ingame buttons

			for (myButton b : new myButton[] { igm_weiter, igm_beenden, igm_restart }) {
				if (b == null)
					continue;
				b.render();
				Vektor3D color = myColor.BLACK;
				if (b.getId() == SuperMain.selection)
					color = myColor.BLACK;

				myText.setSelected_big_text(SuperMain.TEXT_CONSOLE_BIG);
				myText.setSelected_text(SuperMain.TEXT_CONSOLE_SMALL);

				double DX = 0.3 * cm;
				if (b.equals(igm_restart))
					DX = 0.2 * cm;
				if (b.equals(igm_beenden))
					DX = 0.7 * cm;
				myText.out(b.getText(), new v3(b.getX() + DX, b.getY() + 0.3 * cm, 0), new v3(0.5 * cm, 0.5 * cm,
						0.5 * cm), color, 1.01 - dead_alpha_value, b.getId());
			}
			if (timeanalyse) {
				TimeAnaliser.addPoint("buttons");
			}
			if (!false) {
				// rechte bullets
				int bis = (int) (9.5 - (SuperMain.profil.mecha.rechteWaffe.getAktuellMunitionCount() / (SuperMain.profil.mecha.rechteWaffe
						.getMaxMunition() + 0.0)) * 10);
				if (SuperMain.profil.mecha.rechteWaffe.getAktuellMunitionCount() <= 0) {
					bis = 10;
				}
				// Ladepkt
				if (!Profil.mecha.rechteWaffe.isFeuerbereit()) {
					OGL.viereck(new v3(hud_pos.getX1() + swidth * 8.45 / 21, sheight * 3.6 / 15.5 - (-1) * sheight
							* 0.335 / 15.5, 0), new v3(swidth * 1 / 21 / 4, swidth * 1 / 21 / 4, 0), bullet, 0,
							1.01 - dead_alpha_value);
				}
				for (int i = 9; i >= bis; i--) {
					OGL.viereck(new v3(hud_pos.getX1() + swidth * 8.45 / 21, sheight * 3.6 / 15.5 - i * sheight * 0.335
							/ 15.5, 0), new v3(swidth * 1 / 21, swidth * 1 / 21 / 4, 0), bullet, 0,
							1.01 - dead_alpha_value);
				}
				// linke bullets
				bis = (int) (10 - (SuperMain.profil.mecha.linkeWaffe.getAktuellMunitionCount() / (SuperMain.profil.mecha.linkeWaffe
						.getMaxMunition() + 0.0)) * 10);

				if (SuperMain.profil.mecha.linkeWaffe.getAktuellMunitionCount() <= 0) {
					bis = 10;
				}
				// Ladepkt
				if (!Profil.mecha.linkeWaffe.isFeuerbereit()) {
					OGL.viereck(new v3(hud_pos.getX1() + swidth * 3.95 / 21, sheight * 3.6 / 15.5 - (-1) * sheight
							* 0.335 / 15.5, 0), new v3(swidth * 1 / 21 / 4, swidth * 1 / 21 / 4, 0), bullet, 0,
							1.01 - dead_alpha_value);
				}

				for (int i = 9; i >= bis; i--) {
					OGL.viereck(new v3(hud_pos.getX1() + swidth * 3.95 / 21, sheight * 3.6 / 15.5 - i * sheight * 0.335
							/ 15.5, 0), new v3(swidth * 1 / 21, swidth * 1 / 21 / 4, 0), bullet, 0,
							1.01 - dead_alpha_value);
				}
			}
			if (timeanalyse) {
				TimeAnaliser.addPoint("bullets");
			}
			// radar
			{
				OGL.verschieb(radar_v);
				radar.render(false, 5 * cm, 1.01 - dead_alpha_value);

				radarnadel_count--;
				OGL.rot(radarnadel_count, v3.z_axis);
				double size = 5 * cm;
				OGL.viereck(new v3(-size / 2, -size / 2, 0), new v3(size, size, 0), radarnadel, 0,
						1.01 - dead_alpha_value);
				OGL.rot(-radarnadel_count, v3.z_axis);
				OGL.verschieb(radar_v.negiere());
			}
			if (timeanalyse) {
				TimeAnaliser.addPoint("radar");
			}
			if (show_bag_count > 0) {
				OGL.verschieb(bag_v);
				SuperMain.profil.mecha.bag.render(1.3 * cm);
				OGL.verschieb(bag_v.negiere());

				myText.setSelected_text(SuperMain.TEXT_METAL);
				myText.setSelected_big_text(SuperMain.TEXT_METAL);
				if (timeanalyse) {
					TimeAnaliser.addPoint("bag");
				}
				// Reiter Button + Weiter/Ende Button
				for (myButton b : new myButton[] { reiter_button_levelinfo, reiter_button_statistics,
						reiter_button_stats, (ziel_erreicht ? next_level : null),
						(reiter == REITER_STATISTICS ? show_highscores : null),
						(SuperMain.statistics.getSt_time() == 0 ? start_level : null) }) {
					double scale = cm / 2.;
					// wenn button null-> nichts weiter machen
					if (b == null)
						continue;
					// button rendern(für click-action)
					myText.setSelected_big_text(SuperMain.TEXT_CONSOLE_BIG);
					myText.setSelected_text(SuperMain.TEXT_CONSOLE_SMALL);

					b.render();
					// Farbe Setzten
					Vektor3D col = (SuperMain.selection == b.getId() ? myColor.BLACK : ((b.getId() == next_level
							.getId() || b.getId() == start_level.getId()) ? myColor.WHITE : myColor.BLACK));

					// // Bei neuen Stats-> verfärben
					if (b.getId() == reiter_button_stats.getId() && Profil.mecha.getVerteilbare_punkte() > 0)
						col = myColor.WHITE;
					double dx = 0.5 * cm;
					double dy = 0.25 * cm;
					if (b.equals(start_level) || b.equals(next_level))
						dx = 0.175 * cm;
					if (b.equals(reiter_button_statistics) || b.equals(reiter_button_stats))
						dx = 0.25 * cm;
					myText.out(b.getText(), new v3(b.getX() + dx, b.getY() + dy, 0), new v3(scale, scale, scale), col,
							1.1, b.getId());
				}
				if (timeanalyse) {
					TimeAnaliser.addPoint("andere buttons");
				}
				if (reiter == REITER_STATS) {

					// OGL.verschieb(stats_v);
					statsView.render(cm);
					// OGL.verschieb(stats_v.negiere());

				} else if (reiter == REITER_STATISTICS) {
					myText.setSelected_text(SuperMain.TEXT_CONSOLE_SMALL);
					myText.setSelected_big_text(SuperMain.TEXT_CONSOLE_BIG);
					OGL.verschieb(new v3(6 * cm, -1.5 * cm, 0));

					myText.out("LEVEL SCORE", new v3(1 * cm, 1 * cm, 0), new v3(0.4 * cm, 0.5 * cm, 0.5 * cm),
							myColor.BGREY, 1.1, 0);

					String s = SuperMain.statistics.getTimeLeftSeks() + "";
					if (s.startsWith("-")) {

					} else {
						s = "+" + s;
					}
					myText.out("Time  : " + s + " (" + SuperMain.statistics.getTimeUsedString() + ")", new v3(1 * cm,
							-1 * cm, 0), new v3(0.4 * cm, 0.5 * cm, 0.5 * cm), myColor.WHITE, 1.1, 0);

					myText.out("Aim   : +" + SuperMain.statistics.getAim() + " (" + SuperMain.statistics.getHitsGiven()
							+ " hits/" + (SuperMain.statistics.getShootsFired()) + " shots)",
							new v3(1 * cm, -2 * cm, 0), new v3(0.4 * cm, 0.5 * cm, 0.5 * cm), myColor.WHITE, 1.1, 0);

					myText.out("Killed: +" + SuperMain.statistics.getTargets_killed() * 10 + " ("
							+ SuperMain.statistics.getTargets_killed() + " sent to hell)", new v3(1 * cm, -3 * cm, 0),
							new v3(0.4 * cm, 0.5 * cm, 0.5 * cm), myColor.WHITE, 1.1, 0);

					/*
					 * Geht nicht in die Score ein... myText.out("Damage
					 * (dealt/taken): " + SuperMain.statistics.getDamageGiven() +
					 * "/" + SuperMain.statistics.getDamageTaken(), new v3(1 *
					 * cm, -4 * cm, 0), new v3(0.4 * cm,0.5 * cm, 0.5 * cm),
					 * myColor.WHITE, 1.1, 0);
					 */

					if (ziel_erreicht) {
						// int score = SuperMain.statistics.getScore();
						myText.out("------------------------", new v3(1 * cm, -3.55 * cm, 0), new v3(0.4 * cm,
								0.5 * cm, 0.5 * cm), myColor.WHITE, 1.1, 0);
						myText.out("Score : " + SuperMain.statistics.getScore(), new v3(1 * cm, -4 * cm, 0), new v3(
								0.4 * cm, 0.5 * cm, 0.5 * cm), myColor.WHITE, 1.1, 0);
						myText.out("Total : " + SuperMain.profil.mecha.gesamt_punkt_zahl, new v3(1 * cm, -5 * cm, 0),
								new v3(0.4 * cm, 0.5 * cm, 0.5 * cm), myColor.WHITE, 1.1, 0);
					}

					OGL.verschieb(new v3(6 * cm, -1.5 * cm, 0).negiere());
				} else if (reiter == REITER_HIGHSCORE) {
					// local highscore data
					if (checked_online && !online_avaiable) {
						myText.setSelected_text(SuperMain.TEXT_CONSOLE_SMALL);
						myText.setSelected_big_text(SuperMain.TEXT_CONSOLE_BIG);
						OGL.verschieb(new v3(6 * cm, -1.5 * cm, 0));
						myText.out("HIGHSCORES - OFFLINE", new v3(1 * cm, 1 * cm, 0), new v3(0.4 * cm, 0.5 * cm,
								0.5 * cm), myColor.BGREY, 1.1, 0);

						int i = 2;
						int place = 0;
						for (Vector v : HighscoreEintrag.loadAllLocalHighscores()) {
							place++;
							String out = place + ". " + v.get(0).toString() + "  " + v.get(1).toString();
							myText.out(out, new v3(1 * cm, 1 * cm - (1 * cm) * i, 0), new v3(0.4 * cm, 0.5 * cm,
									0.5 * cm), myColor.WHITE, 1.1, 0);
							i++;
							if (place == 8) {
								break;
							}

						}

						OGL.verschieb(new v3(6 * cm, -1.5 * cm, 0).negiere());
					} else {
						// try to load online highscore
						myText.setSelected_text(SuperMain.TEXT_CONSOLE_SMALL);
						myText.setSelected_big_text(SuperMain.TEXT_CONSOLE_BIG);
						OGL.verschieb(new v3(6 * cm, -1.5 * cm, 0));
						myText.out("HIGHSCORES", new v3(1 * cm, 1 * cm, 0), new v3(0.4 * cm, 0.5 * cm, 0.5 * cm),
								myColor.BGREY, 1.1, 0);

						int i = 1;
						int place = 0;
						Vektor3D color = myColor.WHITE;
						for (String s : HighscoreEintrag.loadAllOnlineHighscores()) {
							place++;
							String out = place + ". " + s;
							if (s.endsWith(SuperMain.profil.getName())) {
								color = myColor.ORANGE;
							} else {
								color = myColor.WHITE;
							}
							myText.out(out, new v3(1 * cm, 1 * cm - (1 * cm) * i, 0), new v3(0.4 * cm, 0.5 * cm,
									0.5 * cm), color, 1.1, 0);
							i++;
							// show only the first 9 places
							if (place > 7) {
								break;
							}

						}
						myText.out("your current score : " + (SuperMain.profil.mecha.gesamt_punkt_zahl), new v3(1 * cm,
								-8 * cm, 0), new v3(0.4 * cm, 0.5 * cm, 0.5 * cm), myColor.WHITE, 1.1, 0);

						// Error-Bit der onkine highscore auswerten
						if (!checked_online) {
							checked_online = true;
							int err = HighscoreEintrag.getERROR();
							switch (err) {
							case HighscoreEintrag.ER_NONE:
								online_avaiable = true;
								break;
							case HighscoreEintrag.ER_WRONGHIGHSCOREFILE:
							case HighscoreEintrag.ER_WRONGSERVER_NOINTERNET:
								// Sys
								// .alert("Online Highscore",
								// "Cannot establish connection to highscore
								// server.\nCheck your internet connection.");
								online_avaiable = false;
								HighscoreEintrag.rebuildHighscores();
								break;
							default:

								break;
							}
						}

						OGL.verschieb(new v3(6 * cm, -1.5 * cm, 0).negiere());
					}
				} else if (reiter == REITER_LEVELINFO) {
					myText.setSelected_text(SuperMain.TEXT_CONSOLE_SMALL);
					myText.setSelected_big_text(SuperMain.TEXT_CONSOLE_BIG);
					OGL.verschieb(new v3(6 * cm, -1.5 * cm, 0));
					myText.out("LEVEL INFO", new v3(1 * cm, 1 * cm, 0), new v3(0.4 * cm, 0.5 * cm, 0.5 * cm),
							myColor.BGREY, 1.1, 0);
					myText.out("LEVEL TIME: " + SuperMain.level.getRichtZeit() + " sek", new v3(1 * cm, -0.5 * cm, 0),
							new v3(0.4 * cm, 0.5 * cm, 0.5 * cm), myColor.BGREY, 1.1, 0);

					int i = 0;
					for (String s : SuperMain.level.getLevelInfo().split("/n")) {
						myText.out(s, new v3(1 * cm, -2 * cm - 1 * cm * i, 0), new v3(0.4 * cm, 0.5 * cm, 0.5 * cm),
								myColor.WHITE, 1.1, 0);
						i++;
					}

					OGL.verschieb(new v3(6 * cm, -1.5 * cm, 0).negiere());
				}
			}

			// ##### LADE BILDSCHIRM FUER HIGHSCORE UEBERTRAGUNG
			if (laden != null) {
				laden.render();
			}

			if (!false) {
				// Console ## Verursacht nichtanzeige von folgenden sachen,
				// warum
				// weiß keiner
				OGL.verschieb(console_v);
				OGL.skaliere(cons_sk);
				InGameConsole.render(1.01 - dead_alpha_value);
				OGL.skaliere(cons_sk.reziproke());
				OGL.verschieb(console_v.negiere());
			}
			if (timeanalyse) {
				TimeAnaliser.addPoint("console+");
			}
		}
		OGL.verschieb(delta.negiere());
		OGL.setOrthoOff();

		if (timeanalyse) {
			TimeAnaliser.addPoint("all over");
		}
		// renderHUDabsolute(null);
	}

	/**
	 * Checks whether a Button is clicked and if so, it returns which Button
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static int orthoselection(int x, int y) {
		int result = 0;
		if (stats_v == null)
			return 0;

		x -= delta.getX1();
		y -= delta.getX2();
		for (myButton b : new myButton[] { igm_beenden, igm_restart, igm_weiter, (ziel_erreicht ? next_level : null),
				reiter_button_statistics, reiter_button_stats, reiter_button_levelinfo,
				(reiter == REITER_STATISTICS ? show_highscores : null),
				(SuperMain.statistics.getSt_time() == 0 ? start_level : null) }) {
			if (b == null)
				continue;
			if (x > b.getX() && x < b.getX() + b.getWidth() && y > b.getY() && y < b.getY() + b.getHeight()) {
				result = b.getId();
			}
		}

		x -= stats_v.getX1();
		y -= stats_v.getX2();
		if (Mouse.isButtonDown(1))
			x += 0;
		for (myButton b : statsView.getAllButtons()) {
			if (x > b.getX() && x < b.getX() + b.getWidth() && y > b.getY() && y < b.getY() + b.getHeight()) {
				result = b.getId();
			}
		}
		x -= -stats_v.getX1() + bag_v.getX1();
		y -= -stats_v.getX2() + bag_v.getX2();
		if (Mouse.isButtonDown(1))
			x += 0;
		for (myButton b : Profil.mecha.bag.getAllButtons()) {
			if (x > b.getX() && x < b.getX() + b.getWidth() && y > b.getY() && y < b.getY() + b.getHeight()) {
				result = b.getId();
			}
		}
		return result;
	}

	/**
	 * Renders the HUD
	 * 
	 * @param delta
	 */
	private void renderHUDabsolute(Vektor3D delta) {

		delta = new v3(-0.035, 0.09, 0).mal(show_bag_count / show_bag_time);

		Vektor3D verschieb_radar = new v3(0.044, -0.0305, -0.11).add(delta);
		double scale = 0.006;
		Vektor3D skalier_radar = new v3(scale, scale, scale);

		Vektor3D verschieb_hud = new v3(-0.026, -0.133, -0.11).add(delta);
		scale = 0.12;
		Vektor3D skalier_hud = new v3(scale, scale, scale);
		OGL.verschieb(verschieb_hud);
		OGL.skaliere(skalier_hud);
		// OGL.viereck(new v3(), new v3(1, 1, 0), hud, 0, 1.1);

		{
			OGL.skaliere(skalier_hud.reziproke());
			OGL.verschieb(verschieb_hud.negiere());
			OGL.verschieb(verschieb_radar);

			OGL.skaliere(skalier_radar);

			// radar.render(false);

			OGL.skaliere(skalier_radar.reziproke());

			OGL.verschieb(verschieb_radar.negiere());
			OGL.verschieb(verschieb_hud);
			OGL.skaliere(skalier_hud);
		}

		{
			scale = 0.01;
			Vektor3D ingame_console_skalier = new v3(scale, scale * 2, scale);
			Vektor3D verschieb_ingamec = new v3(0.03, 0.112, 0.005);
			OGL.skaliere(skalier_hud.reziproke());
			OGL.verschieb(verschieb_ingamec);
			OGL.skaliere(skalier_hud);
			OGL.skaliere(ingame_console_skalier);
			// InGameConsole.render(true);
			OGL.skaliere(ingame_console_skalier.reziproke());
			OGL.skaliere(skalier_hud.reziproke());
			OGL.verschieb(verschieb_ingamec.negiere());
			OGL.skaliere(skalier_hud);
		}

		if (show_bag_count > 0) {
			scale = 0.05;
			Vektor3D bag_skalier = new v3(scale, scale, scale);
			Vektor3D verschieb_bag = new v3(0.08, 0.112 - 0.105, 0.006);
			OGL.skaliere(skalier_hud.reziproke());
			OGL.verschieb(verschieb_bag);
			OGL.skaliere(skalier_hud);
			OGL.skaliere(bag_skalier);
			SuperMain.profil.mecha.bag.render(1);
			OGL.skaliere(bag_skalier.reziproke());
			OGL.skaliere(skalier_hud.reziproke());
			OGL.verschieb(verschieb_bag.negiere());
			OGL.skaliere(skalier_hud);
		}

		if (show_bag_count > 0) {
			scale = 0.05;
			Vektor3D stats_skalier = new v3(scale, scale, scale);
			Vektor3D verschieb_stats = new v3(0.08, 0.112 - 0.105, 0.006);
			OGL.skaliere(skalier_hud.reziproke());
			OGL.verschieb(verschieb_stats);
			OGL.skaliere(skalier_hud);
			OGL.skaliere(stats_skalier);
			// changestats.render();
			OGL.skaliere(stats_skalier.reziproke());
			OGL.skaliere(skalier_hud.reziproke());
			OGL.verschieb(verschieb_stats.negiere());
			OGL.skaliere(skalier_hud);
		}

		{
			scale = 0.01;
			Vektor3D ingame_console_skalier = new v3(scale, scale * 2, scale);
			Vektor3D verschieb_ingamec = new v3(0.01, 0.092, 0.005);
			OGL.skaliere(skalier_hud.reziproke());
			OGL.verschieb(verschieb_ingamec);
			OGL.skaliere(skalier_hud);
			OGL.skaliere(ingame_console_skalier);
			// myText.out(LevelPlay.p.life + "%", 1.1);
			OGL.skaliere(ingame_console_skalier.reziproke());
			OGL.skaliere(skalier_hud.reziproke());
			OGL.verschieb(verschieb_ingamec.negiere());
			OGL.skaliere(skalier_hud);
		}

		Vektor3D radarnadel_v = new v3(0.0684, 0.1044, 0.005);
		OGL.skaliere(skalier_hud.reziproke());
		OGL.verschieb(radarnadel_v);
		OGL.skaliere(skalier_hud);
		radarnadel_count--;
		OGL.rot(radarnadel_count, v3.z_axis);
		double size = 0.225;
		// OGL.viereck(new v3(-size / 2, -size / 2, 0), new v3(size, size, 0),
		// radarnadel, 0, 1.1);
		OGL.rot(-radarnadel_count, v3.z_axis);
		OGL.skaliere(skalier_hud.reziproke());
		OGL.verschieb(radarnadel_v.negiere());

		Vektor3D bullet_v = new v3(0.0689, 0.104, 0.005);

		OGL.verschieb(bullet_v);

		OGL.skaliere(skalier_hud);

		int bis = (int) (9.5 - (SuperMain.profil.mecha.rechteWaffe.getAktuellMunitionCount() / (SuperMain.profil.mecha.rechteWaffe
				.getMaxMunition() + 0.0)) * 10);
		if (SuperMain.profil.mecha.rechteWaffe.getAktuellMunitionCount() <= 0) {
			bis = 10;
		}
		for (int i = 9; i >= bis; i--) {
			// OGL.viereck(new v3(-0.1799, 0.045 - i * 0.015, 0), new v3(0.042,
			// 0.042 / 4, 0), bullet, 0, 1.1);
		}

		bis = (int) (9.5 - (SuperMain.profil.mecha.linkeWaffe.getAktuellMunitionCount() / (SuperMain.profil.mecha.linkeWaffe
				.getMaxMunition() + 0.0)) * 10);
		if (SuperMain.profil.mecha.linkeWaffe.getAktuellMunitionCount() <= 0) {
			bis = 10;
		}
		for (int i = 9; i >= bis; i--) {
			// OGL.viereck(new v3(-0.383, 0.045 - i * 0.015, 0), new v3(0.042,
			// 0.042 / 4, 0), bullet, 0, 1.1);
		}

		OGL.skaliere(skalier_hud.reziproke());
		OGL.verschieb(bullet_v.negiere());
		OGL.skaliere(skalier_hud);

		OGL.skaliere(skalier_hud.reziproke());
		OGL.verschieb(verschieb_hud.negiere());

	}

	public static final int SIZE_FLOAT = 4;

	public static FloatBuffer allocFloats(float[] floatarray) {
		FloatBuffer fb = ByteBuffer.allocateDirect(floatarray.length * SIZE_FLOAT).order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		fb.put(floatarray).flip();
		return fb;
	}

	/**
	 * Logic that handels the reaction when the Player enters the Target Area
	 * 
	 * @param newstate
	 *            new State of the Game
	 */
	public static void ZielErreicht(String newstate) {
		if (!ziel_erreicht) {
			if (newstate != null) {
				state_on_exit = newstate;
			} else {
				state_on_exit = "MainMenu";
			}
			ziel_erreicht = true;
			zielerreicht.play();

			SuperMain.statistics.setEndTime();
			SuperMain.statistics.setEndState(state_on_exit);
			SuperMain.statistics.save();
			SuperMain.profil.mecha.gesamt_punkt_zahl += SuperMain.statistics.getScore();
			Profil.mecha.saveAsObject(newstate);
			reiter = REITER_STATISTICS;
			if (SuperMain.statistics.getLevel().endsWith("4.xml")) {
				// war das letzte level--> Highscore eintrag!
				new Thread() {
					@Override
					public void run() {
						super.run();
						laden = new Laden();
						try {
							Laden.addText("submit score");
						} catch (NullPointerException e) {
							// wirft beim erstenmal bei laden-render eine nullPE
							// wegen myColor.white
						}
						writeScore();
						laden = null;
						Laden.last_instance = null;
					}

				}.start();
				// Gespeicherten Mecha löschen

				reiter = REITER_HIGHSCORE;
			}

			show_bag_count++;

		}

	}

	private static void writeScore() {
		// für jedes level eine statistik laden
		Statistics[] stat = new Statistics[5];
		long[] dates = new long[5];
		for (Statistics st : Statistics.loadAllStatistics()) {
			int levelnr = Integer
					.parseInt(st.getLevel().split("/")[st.getLevel().split("/").length - 1].split("[.]")[0]
							.split("level")[1]);
			stat[levelnr] = st;
		}

		// Highscore eintrag erstellen und speichern
		HighscoreEintrag hseintrag = new HighscoreEintrag(stat, Profil.mecha);
		hseintrag.save();
		// alle statistiken löschen

		deleteAllStatistics();

	}

	/**
	 * Delete Statistic File
	 */
	public static void deleteAllStatistics() {
		File f = new File(SuperMain.ordner + "profile/" + SuperMain.profil.getName());

		for (File file : f.listFiles()) {
			if (file.getAbsolutePath().endsWith(".hscore")) {

				boolean b = file.delete();
				SuperMain.out("lösche " + file.getAbsolutePath() + " " + (b ? "" : "nicht") + " erfolgerich!");
			}
		}

	}

	public static void addBullet(Geschoss g) {

		if (LevelPlay.client != null) {
			LevelPlay.client.client.send(g);
		} else {
			LevelPlay.bullets.add(g);
		}
	}

	/**
	 * Is called when the player dies.<br>
	 * Initializes the Death cam.
	 * 
	 * @return The old state of the death animation.
	 */
	public static boolean onDeadAnimation() {
		//dead.play();
		boolean old = deadanimation;
		deadanimation = true;
		// hud einziehen
		if (show_bag_count > 0 && show_bag_count % 2 == 1)
			show_bag_count++;
		if (show_menu_count > 0 && show_menu_count % 2 == 1)
			show_menu_count++;

		return old;

	}
}

/**
 * Not in Use
 * 
 * @author ladanz
 * 
 */
class JetPack {
	int fuel;

	private double height;

	private Player p;

	private double start_height;

	private boolean activity;

	private boolean up;

	private final double maxh = 3;

	private final double up_speed = 90;

	private final double down_speed = 400;

	private boolean rapid;

	public JetPack() {
		fuel = 200;
	}

	public void start(Player player) {
		activity = true;
		start_height = player.getPosition().getX2();
		p = player;
		height = 0;
		up = true;
		rapid = false;
		fuel = 300;
	}

	public double getHeight() {
		return height + start_height;
	}

	public void tick(Player p_) {
		if (fuel > 0) {
			fuel--;
		} else {
			rapid = true;
		}
		if (up) {
			height += maxh / up_speed;
			if (height > maxh) {
				height = maxh;
				up = false;
			}
		} else {
			if (rapid) {
				height -= maxh / up_speed;
			} else {
				height -= maxh / down_speed;
			}
			if (getHeight() < SuperMain.level.getHeight(p_.getPosition())) {
				// Flug abbrechen
				activity = false;
			}
		}
	}

	public void stop() {
		rapid = true;
		up = false;
	}

	public boolean isActive() {
		return (activity);
	}

}
