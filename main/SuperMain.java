package main;

import java.awt.AWTError;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Hashtable;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import sock.DedicatedServer;
import sock.ObjRemover;
import Classes.GameState;
import Classes.KeyListener;
import Classes.Level;
import Classes.OGL;
import Classes.OGLable;
import Classes.Object3D;
import Classes.Objekt;
import Classes.OpenAlClip;
import Classes.ParameterAble;
import Classes.Profil;
import Classes.Statistics;
import Classes.Texture;
import Classes.TextureLoader;
import Classes.TimeAnaliser;
import Classes.Vektor3D;
import Classes.myText;
import Fenster.GelaendeFenster;
import drops.SchrottDrop;

/**
 * Main Game Class<br>
 * Manages statechanges of Gamestates and more
 * 
 * @author laDanz
 * 
 */
public class SuperMain implements OGLable {
	protected static final boolean open_server = false;

	public static int data_send = 0;
	public static int data_rec = 0;

	static public GameState gamestate;

	public static TextureLoader loader = new TextureLoader();

	static public boolean gitter = false;

	static public boolean farbe = false;

	static public boolean schatten = false;

	static public boolean texture_anzeigen = true;

	static public char seperator;

	// static public Auftrag[] auftrag = null;

	static public LinkedList<String> toLoad = new LinkedList<String>();

	static public LinkedList<Object3D> toRender = new LinkedList<Object3D>();

	static public LinkedList<Runnable> toRun = new LinkedList<Runnable>();

	public static Level level;

	public static Statistics statistics;

	static public DedicatedServer server;

	/**
	 * Utilitivar for counting the Frames per Second
	 */
	static public int fps, oldfps = 60;

	static private String[] arg;

	static public double cm = 1;

	/**
	 * Developermode switched on/off
	 */
	static public boolean dev = false;

	// private static Text text;
	/**
	 * root Folder from where the game was started
	 */
	static public String ordner;

	private static int lastId = 1;

	public static LinkedList<String> toSend = new LinkedList<String>();

	static public int selection;

	/**
	 * Current profile
	 */
	public static Profil profil = null;

	public static int TEXT_METAL = 0;
	public static int TEXT_CONSOLE_SMALL = 1;
	public static int TEXT_CONSOLE_BIG = 2;

	public static boolean licht = true;
	public static boolean nofilecreation = false;

	public static File logFile;
	static PrintStream logFileStream;

	public static boolean fullscreenfailed;

	public static Integer instances;

	static boolean windowsmode;

	@SuppressWarnings("deprecation")
	public SuperMain() {
		if (arg == null) {
			arg = new String[0];
		}
		if (instances == null) {
			instances = 0;
		}
		instances++;
		if (instances > 1) {
			System.exit(0);
		}

		fullscreenfailed = false;
		// Aktuellen Ordner auslesen
		getOrdner();

		setIcon();

		for (String s : arg) {
			if (s.equals("nofilecreation")) {
				nofilecreation = true;
			}

		}

		// Openal initialisieren
		OpenAlClip.initOpenAL();

		if (!new File(SuperMain.ordner + "errors/").exists()) {
			new File(SuperMain.ordner + "errors/").mkdir();
		}
		logFile = new File(SuperMain.ordner + "errors/Trace" + System.currentTimeMillis() + ".log");
		try {
			if (!nofilecreation) {
				logFile.createNewFile();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			SuperMain.out(e);
		}
		try {
			if (!nofilecreation) {
				logFileStream = new PrintStream(logFile);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SuperMain.out("StartOrdner: " + ordner);

		// Linux oder Windows
		SuperMain.out("running on " + System.getProperties().get("os.arch") + " "
				+ System.getProperties().get("os.name"));

		level = new Level();

		// #############################
		// OpenGL und Display initialisiern
		if (dev || windowsmode) {
			initAsWindow();
		} else {
			try {
				OGL.init(true, this, 1024, 768);
			} catch (Exception e) {
				SuperMain.out(e);
				// Fullscreen failed.
				// Try Window
				fullscreenfailed = true;
				initAsWindow();

			}
		}

	}

	private void initAsWindow() {
		try {
			OGL.init(false, this);
		} catch (Exception e) {
			SuperMain.out(e);

		}
	}

	private void setIcon() {
		String f = SuperMain.ordner + "img/icon_bunt.png";
		BufferedImage b1, b2, b3;
		try {
			b1 = ImageIO.read(new BufferedInputStream(new FileInputStream(f)));
			f = SuperMain.ordner + "img/icon_bunt32.png";
			b2 = ImageIO.read(new BufferedInputStream(new FileInputStream(f)));
			f = SuperMain.ordner + "img/icon_bunt128.png";
			b3 = ImageIO.read(new BufferedInputStream(new FileInputStream(f)));
			ByteBuffer[] icon = new ByteBuffer[] { convertImageData(b1), convertImageData(b2), convertImageData(b3) };
			Display.setIcon(icon);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AWTError e) {
			// Not the right java Version
			e.printStackTrace();
			Sys
					.alert(
							"AWTError (#SM250)",
							"Failed to load some java librarys.\nPlease make sure you have the latest java Version installed.\nhttp://www.java.com/download/");
		}

	}

	private ByteBuffer convertImageData(BufferedImage bufferedImage) {
		ColorModel glAlphaColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] {
				8, 8, 8, 8 }, true, false, ComponentColorModel.TRANSLUCENT, DataBuffer.TYPE_BYTE);

		ColorModel glColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] { 8, 8,
				8, 0 }, false, false, ComponentColorModel.OPAQUE, DataBuffer.TYPE_BYTE);

		ByteBuffer imageBuffer = null;
		WritableRaster raster;
		BufferedImage texImage;

		int texWidth = 2;
		int texHeight = 2;

		// find the closest power of 2 for the width and height
		// of the produced texture
		while (texWidth < bufferedImage.getWidth()) {
			texWidth *= 2;
		}
		while (texHeight < bufferedImage.getHeight()) {
			texHeight *= 2;
		}

		// create a raster that can be used by OpenGL as a source
		// for a texture
		if (bufferedImage.getColorModel().hasAlpha()) {
			raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, texWidth, texHeight, 4, null);

			texImage = new BufferedImage(glAlphaColorModel, raster, false, new Hashtable());

		} else {
			raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, texWidth, texHeight, 3, null);
			texImage = new BufferedImage(glColorModel, raster, false, new Hashtable());
		}

		// copy the source image into the produced image
		Graphics g = texImage.getGraphics();
		g.setColor(new Color(0f, 0f, 0f, 0f));
		g.fillRect(0, 0, texWidth, texHeight);
		g.drawImage(bufferedImage, 0, 0, null);

		// build a byte buffer from the temporary image
		// that be used by OpenGL to produce a texture.
		byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer()).getData();

		imageBuffer = ByteBuffer.allocateDirect(data.length);
		imageBuffer.order(ByteOrder.nativeOrder());
		imageBuffer.put(data, 0, data.length);
		imageBuffer.flip();

		return imageBuffer;
	}

	/**
	 * logs in a Logfile and the Console
	 * 
	 * @param s
	 */
	public static void out(String s) {

		System.out.println(s);

		if (nofilecreation) {
			return;
		}
		logFileStream.append(s + "\n");
		logFileStream.flush();

	}

	/**
	 * Log Stacktrace in Logfile
	 * 
	 * @param s
	 */
	public static void out(Exception s) {
		s.printStackTrace();
		if (nofilecreation) {
			return;
		}
		s.printStackTrace(logFileStream);

	}

	public static boolean generateEmail(String... args) {
		String text = "";
		for (String ss : args) {
			text += ss.replace("\\", "/").replace("<", "(").replace(">", ")").replace("\t", "    ").replace(" ", "%20")
					.replace("/n", "%0A").replace("\n", "%0A")
					+ "%0A";
		}
		try {
			String s = "mailto:bugs@metalchaser.de?subject=MetalChaser%20Konsolen%20Inhalt&body=Persoenliche%20Anmerkungen:%0A%0A%0AAutomatisch%20generierter%20Konsolenoutput:%0A"
					+ text;
			// System.out.println(s.substring(500));
			Desktop.getDesktop().mail(new URI(s));

		} catch (IOException e) {
			// Kein MailProgramm installiert
			e.printStackTrace();

			Sys
					.alert(
							OGL.GAME_TITLE,
							"Konnte Debug eMail nicht generieren.\nBitte schicken sie manuell die neueste Datei\naus dem Verzeichnis 'errors' an bugs@metalchaser.de");

			SuperMain.out(e);
			return false;
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			SuperMain.out(e);
			return false;
		}
		return true;
	}

	/**
	 * Read Rootfolder
	 */
	public static void getOrdner() {
		ordner = new java.io.File("hehe").getAbsolutePath();
		char sep = System.getProperties().get("file.separator").toString().charAt(0);
		seperator = sep;
		ordner = ordner.substring(0, ordner.lastIndexOf(sep) + 1);
		String s = "";
		int c = -10;
		for (int i = 0; i < ordner.length(); i++) {

			if (ordner.charAt(i) == sep) {
				if (c > 0) {
					s += "/";
				} else {
					s += ordner.charAt(i);
				}
				c++;
			} else {
				s += ordner.charAt(i);
			}

		}
		ordner = s;
	}

	/**
	 * Get Max Size for Images supported of the operating system
	 * 
	 * @return
	 */
	public static final int getMaxSingleImageSize() {
		IntBuffer buffer = BufferUtils.createIntBuffer(16);
		GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE, buffer);

		return buffer.get(0);
	}

	/**
	 * Entry point for executeable Jar
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		LinkedList<String> res = new LinkedList<String>();
		windowsmode = false;
		for (String s : args) {
			if (s.equals("dev")) {
				dev = true;
			} else if (s.equals("w")) {
				windowsmode = true;
			} else {
				res.add(s);
			}
		}
		arg = res.toArray(new String[0]);

		new SuperMain();
	}

	/**
	 * Loading a Texture
	 * 
	 * @param ress
	 * @return
	 * @throws IOException
	 */
	static public Texture loadTex(String ress) throws IOException {
		if (ress == null)
			return null;
		if (!(ress.toLowerCase().startsWith("img\\") || ress.toLowerCase().startsWith("img/"))) {

			ress = ress.substring(ress.indexOf("img" + java.io.File.separator));
		}
		return SuperMain.loader.getTexture(ress);
	}

	/**
	 * Analyze Gamestate and optionally loading a new Gamestate
	 */
	public void logic() {
		// System.out.println("Supermain: LOGIC");
		// Frames erhoehen
		fps++;

		// selecten
		main.SuperMain.selection = OGL.selection(Mouse.getX(), Mouse.getY(), this);

		// ############################
		// GameState Status auswerten
		String state = gamestate.getState();
		// wenn GS Status 0 --> witermachen im aktuellen Status
		if (state == "0")
			gamestate.logic();
		else {
			// state finalisieren lassen
			gamestate.doFinalizeActions();
			// erst Lade Bildschirm und dann versuchen den state zu laden,
			// ansonsten ende
			gamestate = new Laden();
			render();
			Display.update();
			try {
				String[] s = state.split(",");
				state = s[0];
				state = "main." + state;
				// for(String ss:s)
				// System.out.println("----->"+ss);

				ClassLoader cl = ClassLoader.getSystemClassLoader();
				Class c3 = cl.loadClass(state);
				gamestate = (GameState) c3.newInstance();
				// System.out.println("-> neue Klasse "+s[0]+" geladen");
				int i = 1;
				while (gamestate instanceof ParameterAble && s.length > i) {
					((ParameterAble) gamestate).setParameter(s[i]);
					i++;
				}
			} catch (ClassNotFoundException e) {
				SuperMain.out("Wird aufgrund der Aussage beendet: " + state);
				OGL.finished = true;
			} catch (InstantiationException e) {
				e.printStackTrace();
				SuperMain.out(e);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				SuperMain.out(e);
			}
		}

		// ###########################
		// Taste F10 --> schnell Beenden
		if (Keyboard.isKeyDown(Keyboard.KEY_F10)) {
			OGL.finished = true;
			TimeAnaliser.analyse();
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_F5)) {
			try {
				OGL.setFullscreen(!OGL.fullscreen);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Texturen nachladen
		if (toLoad.size() > 0) {
			String[] tex = toLoad.toArray(new String[0]);
			for (String s : tex) {
				toLoad.removeFirstOccurrence(s);
				Texture tex_ = null;
				String string = s.replace("\\", "/");
				try {
					tex_ = loadTex(string);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					SuperMain.out(e);
				}
				if (tex_ != null) {
					level.addTexture(string, tex_);
					SuperMain.out("neue Tex: " + string);

					if (GelaendeFenster.tex_area != null)
						GelaendeFenster.actTex();
				}
			}
		}

		// Objekte nachladen
		while (toRender.size() > 0) {
			Object3D obj = toRender.removeFirst();
			obj.loadFromFile();
			obj.render();

		}

		long last_dot = 0;
		// xxx nachladen
		while (toRun.size() > 0) {

			if (System.currentTimeMillis() - last_dot > 1000) {
				last_dot = System.currentTimeMillis();
				Laden.insertDot();
			}
			Runnable obj = toRun.removeFirst();
			obj.run();

		}

		// Tasten auswetrten
		if (gamestate instanceof KeyListener) {
			KeyListener gs_key = (KeyListener) gamestate;
			while (Keyboard.next()) {
				if (Keyboard.getEventKeyState()) {
					gs_key.onKeyDown(Keyboard.getEventKey());
				} else {
					gs_key.onKeyUp(Keyboard.getEventKey());
				}
			}
			while (Mouse.next()) {
				if (Mouse.getEventButtonState()) {
					gs_key.onMouseDown(Mouse.getEventButton());
				} else {
					gs_key.onMouseUp(Mouse.getEventButton());
				}
			}
		}

	}

	/**
	 * Singular Settings
	 */
	public void once() {
		// System.out.println("Supermain: ONCE");
		// Variablen initiieren
		// text = new Text();
		SuperMain.out("Maximum Texture Size Supported: " + getMaxSingleImageSize());

		new myText();
		TEXT_CONSOLE_SMALL = myText.addNewFont("img/hud/font_console1.png");
		TEXT_CONSOLE_BIG = myText.addNewFont("img/hud/font_console0.png");

		// mit dem Ladebildschirm beginnen
		gamestate = new Laden();

		logic();
		render();
		Display.update();
		Laden.addText("ld Mainmenu");

		// danach wird Menue oder Vorspann angezeigt
		if (arg.length > 0)
			gamestate = new Laden(arg[0]);
		else {
			// Ich beginne mit dem StartMenue
			if (dev)
				gamestate = new Starter();
			else
				gamestate = new MainMenu();
			// gamestate = new Editor();
		}

		// ##############################
		// FPS Zaehler Thread Starten
		fps = 0;
		Fps fpscount = new Fps();
		fpscount.start();

	}

	public void render() {
		// System.out.println("Supermain: RENDER");
		// Kamera Platzieren

		// aktuellen GS rendern
		gamestate.render();
		if (dev) {
			OGL.setColor(new Vektor3D(0, 0, 1));
		}

	}

	/**
	 * THread which resets the FPS/Frames per second
	 * 
	 * @author laDanz
	 * 
	 */
	class Fps extends Thread {

		//@override
		public void run() {
			while (true) {
				// Maximal 10% unterschied je sek zu lassen
				/*
				 * if (fps > (int) (oldfps * 1.3) || fps < (int) (oldfps * 0.9))
				 * if (fps > oldfps) fps = (int) (oldfps * 1.3); else fps =
				 * (int) (oldfps * 0.9);
				 */

				oldfps = fps;
				OGL.akt_fps = fps;
				OGL.fps_anpassung = 60. / fps;
				if (SuperMain.data_rec > 0)
					System.out.println("data received this second: " + data_rec);

				if (SuperMain.data_send > 0)
					System.out.println("data send this second: " + data_send);
				data_rec = data_send = 0;
				fps = 0;
				try {
					Thread.sleep(1000);

					// Fps fpscount = new Fps();
					// fpscount.start();
				} catch (Exception e) {

					interrupt();

				}
			}
		}
	}

	/**
	 * Loading a new State
	 * 
	 * @author ladanz
	 * 
	 */
	class LoadState extends Thread {
		String gamestates;

		public LoadState(String gamestate) {
			this.gamestates = gamestate;

			this.start();

		}

		//@override
		public void run() {

			System.out.println("gs");
			try {
				ClassLoader cl = ClassLoader.getSystemClassLoader();
				Class c3 = cl.loadClass(gamestates);
				gamestate = (GameState) c3.newInstance();

			} catch (Exception e) {
				e.printStackTrace();
				SuperMain.out(e);
			}

		}

	}

	/**
	 * Generate new ID
	 * 
	 * @return new ID
	 */
	public static int genId() {
		// TODO Auto-generated method stub
		return lastId++;
	}

	public static void initAuftrage() {
		// auftrag = AuftragsEditor.load(SuperMain.ordner + "auf.xml");
	}

	/**
	 * Add a new Drop to the Level
	 * 
	 * @param drop
	 * @return
	 */
	public static boolean addDrop(Objekt drop) {
		if (level == null || level.objekte == null)
			return false;
		level.objekte.add(drop);
		if (server != null) {
			server.server.broadCast(drop);
			System.out.println("Submitted drop: " + drop);
		}
		return true;
	}

	/**
	 * Removes a Drop from the Level
	 * 
	 * @param healthDrop
	 */
	public static void removeDrop(final SchrottDrop healthDrop) {
		SuperMain.toRun.add(new Runnable() {
			public void run() {
				SuperMain.level.objekte.remove(healthDrop);

			}
		});
		if (LevelPlay.client != null) {
			// send the server the picked up drop so he can remove it
			LevelPlay.client.client.send(healthDrop);

		}
		if (server != null) {
			// remove this drop from the clients too
			server.server.broadCast(new ObjRemover(healthDrop));
		}
	}

}

class AchsenKreuz implements OGLable {

	public void logic() {
		// TODO Auto-generated method stub

	}

	public void once() {
		// TODO Auto-generated method stub

	}

	public void render() {
		OGL.setColor(new Vektor3D(0, 0, 1));
		OGL.line(5, new Vektor3D(0, 0, 0), new Vektor3D(1, 0, 0));
		OGL.setColor(new Vektor3D(0, 1, 0));
		OGL.line(5, new Vektor3D(0, 0, 0), new Vektor3D(0, 1, 0));
		OGL.setColor(new Vektor3D(1, 0, 0));
		OGL.line(5, new Vektor3D(0, 0, 0), new Vektor3D(0, 0, 1));

	}

}