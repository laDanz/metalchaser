package main;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import Classes.GameState;
import Classes.Level;
import Classes.OGL;
import Classes.Objekt;
import Classes.ParameterAble;
import Classes.Profil;
import Classes.Vektor3D;
import Classes.myColor;
import Classes.quad;
import Classes.v3;
import Fenster.DebugFenster;
import Fenster.GelaendeFenster;
import Fenster.SaveFenster;

/**
 * Cleaners Terrain Editor.
 * 
 * @author laDanz
 * 
 */
public class Editor implements GameState, ParameterAble {
	// public static LinkedList<quad> quads = new LinkedList<quad>();

	public static String state = "0";
	// Vector speichert: <um was;ursprungsgroessen feld>
	static public HashMap<String, Vector<Double>> tohigher;
	final Vektor3D startpos = new Vektor3D(7.6, 10, 16.7);
	Vektor3D pos = startpos;

	AchsenKreuz ak = new AchsenKreuz();
	public static int rot010 = 8;
	public static int rot100 = 18;
	int hohe = 0;
	static int mousedowncount = 0;
	static public boolean begrenzer = false;
	static public boolean allow_selection = true;

	static public LinkedList<Integer> MouseOver = new LinkedList<Integer>();
	static public LinkedList<Integer> MouseClick = new LinkedList<Integer>();

	static public Objekt putter = null;
	public static Objekt selectedObjekt;
	public static boolean draggin = false;
	public static boolean optionchanging = false;

	// color of overall scene lighting
	float ambient[] = { 0f, 0f, 0f, 1f };

	// color of light source
	float lightDiffuse[] = { .9f, .9f, .6f, 1f }; // direct light
	float lightSpecular[] = { .9f, .9f, .6f, 1f }; // highlight
	float lightAmbient[] = { .5f, .0f, .0f, 1f }; // scattered light

	/**
	 * Constructor
	 */
	public Editor() {
		Mouse.setGrabbed(false);
		int wid = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
		wid = (wid - Display.getDisplayMode().getWidth()) / 2 + Display.getDisplayMode().getWidth();
		int hei = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height;
		hei = (hei - Display.getDisplayMode().getHeight()) / 2;
		new DebugFenster(wid + 10, hei, 200, 200);

		new GelaendeFenster(0, 210, 200, 400);
		SuperMain.profil = new Profil("Editor");
		new SaveFenster(0, 0, 200, 200);
		once();
		loadAllTextures();
		System.out.println("EDITOR +#+#+#+#+");
		System.out.println("W , A , S , D  for Movement");
		System.out.println("[Q]/[E]/Arrows for Rotating ");
		state = "0";
	}

	/**
	 * Set Parameters for the Editor. These put the Editor in differnet states
	 */
	public void setParameter(String s) {
		SuperMain.level = new Level();
		SuperMain.level.load(SuperMain.ordner + s);
		System.out.println("got Parameter " + s);
	}

	public String toString() {
		return "Editor";
	}

	//@override
	public void doFinalizeActions() {
		// TODO Auto-generated method stub

	}

	/**
	 * Is "Editor" the current GameState?
	 * @return
	 */
	public static boolean isActualGameState() {
		return SuperMain.gamestate.toString().equals("Editor");
	}

	/**
	 * Start putting the objects
	 * @param o
	 */
	public static void startPut(Objekt o) {
		putter = o;
		System.out.println("Start Putting");
	}

	/**
	 * Loading all Textures into the Editor
	 */
	private void loadAllTextures() {
		File f = new File(SuperMain.ordner + "img");
		for (File f_ : f.listFiles()) {
			String path = "img" + File.separator + f_.getName();
			if (path.endsWith("jpg") || path.endsWith("bmp") || path.endsWith("png")) {
				try {
					SuperMain.loadTex(path);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();SuperMain.out(e);
				}
			}
		}
	}

	/**
	 * Simple Getter
	 */
	public String getState() {
		// TODO Auto-generated method stub
		return state;
	}

	public void logic() {
		DebugFenster.setframes(SuperMain.oldfps);

		double fac = OGL.fps_anpassung / 3;
		if (Keyboard.isKeyDown(Keyboard.KEY_DELETE)) {
			if (Editor.selectedObjekt != null) {
				SuperMain.level.removeObj(Editor.selectedObjekt);
				Editor.selectedObjekt = null;
				GelaendeFenster.postObjektData(null);
			}
		}
		double dx = 5 * fac * Math.cos(Math.toRadians(rot010));
		double dy = 5 * fac * Math.sin(Math.toRadians(rot010));
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			pos = pos.add(new Vektor3D(dx, 0, dy));
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			pos = pos.add(new Vektor3D(-dx, 0, -dy));
		}
		dx = 5 * fac * Math.cos(Math.toRadians(rot010 + 90));
		dy = 5 * fac * Math.sin(Math.toRadians(rot010 + 90));
		if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			pos = pos.add(new Vektor3D(dx, 0, dy));
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			pos = pos.add(new Vektor3D(-dx, 0, -dy));
		}
		int d = Mouse.getDWheel();
		if (d != 0) {
			d = d / Math.abs(d);
			if ((!(hohe == -50 && d < 0) && !(d > 0 && hohe == 200))) {
				hohe += d;
			}
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			state = "exit";
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
			rot010++;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
			rot010--;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			if (Editor.selectedObjekt != null) {

				Editor.selectedObjekt.setPosition(Editor.selectedObjekt.getPosition().add(
						new v3(0, 0, 0.2 * OGL.fps_anpassung)));
			} else
				rot100++;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			if (Editor.selectedObjekt != null) {

				Editor.selectedObjekt.setPosition(Editor.selectedObjekt.getPosition().add(
						new v3(-0.2 * OGL.fps_anpassung, 0, 0)));
			}
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			if (Editor.selectedObjekt != null) {

				Editor.selectedObjekt.setPosition(Editor.selectedObjekt.getPosition().add(
						new v3(0.2 * OGL.fps_anpassung, 0, 0)));
			}
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			if (Editor.selectedObjekt != null) {
				Editor.selectedObjekt.setPosition(Editor.selectedObjekt.getPosition().add(
						new v3(0, 0, -0.2 * OGL.fps_anpassung)));
			} else
				rot100--;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			pos = startpos;
			rot010 = 27;
			rot100 = 3;

		}

		if (!Mouse.isButtonDown(0)) {
			draggin = false;
			if (mousedowncount == 0) {
				if (Editor.tohigher != null && Editor.tohigher.size() > 0)
					Editor.tohigher.clear();
			} else {
				if (mousedowncount > 0)
					mousedowncount--;
			}
		}

		if (Mouse.isButtonDown(0)) {
			mousedowncount = 3;
			if (!draggin && Editor.selectedObjekt != null && !Editor.optionchanging) {
				Editor.selectedObjekt = null;
				GelaendeFenster.postObjektData(null);
			}
			if (GelaendeFenster.menu != null)
				GelaendeFenster.menu.setVisible(false);
			if (Editor.putter != null) {
				Objekt o = Editor.putter;
				Editor.putter = null;
				if (Editor.allow_selection) {

					SuperMain.level.addObjekt(o);
				}
			}
			if (allow_selection) {
				MouseClick.add(SuperMain.selection);
			}
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_P)) {
			System.out.println("printingKamera Position");
			System.out.println("Pos: " + pos.toString());
			System.out.println("Rot010: " + rot010);
			System.out.println("Rot100: " + rot100);
		}

		begrenzer = GelaendeFenster.cb_Begrenzer.isSelected();
		SuperMain.farbe = GelaendeFenster.cb_Farbe.isSelected();
		SuperMain.gitter = GelaendeFenster.cb_Gitter.isSelected();
		SuperMain.texture_anzeigen = GelaendeFenster.cb_texture.isSelected();
		if (Mouse.getDX() > 0 || Mouse.getDY() > 0) {
			allow_selection = true;
		}
		if (allow_selection) {
			SuperMain.selection = OGL.selection(Mouse.getX(), Mouse.getY(), this);
			MouseOver.add(SuperMain.selection);
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_C)) {
			if (selectedObjekt != null) {
				Objekt newone;
				try {
					newone = selectedObjekt.getClass().newInstance();
					for (int i = 0; i < newone.getOptionCount(); i++) {
						newone.setOptionValue(i, selectedObjekt.getOptionValue(i));
					}
					newone.setPosition(selectedObjekt.getPosition().add(new v3(5, 0, -5)));
					Editor.startPut((Objekt) newone);
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();SuperMain.out(e);
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();SuperMain.out(e);
				}

			}
		}

	}

	/**
	 * Unique settings for initialising the Editor
	 */
	public void once() {

		initQuads();

		// Create a light
		// diffuse is the color of direct light from this light source
		// specular is the hightlight color
		// ambient is the color of scattered light from this source
		// position is where the light is, or it's direction
		// setLight(GL11.GL_LIGHT1, lightDiffuse, lightAmbient, lightSpecular,
		// new v3(50, 20, -50));

		// no overall scene lighting
		setAmbientLight(ambient);
	}

	/**
	 * Set the OpenGL Light configuration
	 * @param GLLightHandle
	 * @param diffuseLightColor
	 * @param ambientLightColor
	 * @param specularLightColor
	 * @param position
	 */
	public static void setLight(int GLLightHandle, float[] diffuseLightColor, float[] ambientLightColor,
			float[] specularLightColor, v3 position) {
		FloatBuffer ltDiffuse = allocFloats(diffuseLightColor);
		FloatBuffer ltAmbient = allocFloats(ambientLightColor);
		FloatBuffer ltSpecular = allocFloats(specularLightColor);
		float[] position_ = new float[] { (float) position.getX1(), (float) position.getX2(), (float) position.getX3(),
				1 };
		FloatBuffer ltPosition = allocFloats(position_);
		GL11.glLight(GLLightHandle, GL11.GL_DIFFUSE, ltDiffuse); // color
		// of
		// the
		// direct
		// illumination
		GL11.glLight(GLLightHandle, GL11.GL_AMBIENT, ltAmbient); // color
		// of
		// the
		// reflected
		// light
		GL11.glLight(GLLightHandle, GL11.GL_SPECULAR, ltSpecular); // color
		// of
		// the
		// highlight
		// (same
		// as
		// direct
		// light)
		GL11.glLight(GLLightHandle, GL11.GL_POSITION, ltPosition);
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

	public static final int SIZE_FLOAT = 4;

	public static FloatBuffer allocFloats(float[] floatarray) {
		FloatBuffer fb = ByteBuffer.allocateDirect(floatarray.length * SIZE_FLOAT).order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		fb.put(floatarray).flip();
		return fb;
	}

	/**
	 * Initialize the Quads
	 */
	public static void initQuads() {
		SuperMain.level = new Level();
		LinkedList<quad> quads = new LinkedList<quad>();
		for (int x = 0; x < 50; x++) {
			for (int y = 0; y < 50; y++) {
				quad q = new quad(new v3(x, 0, -y), new v3(1, 0, -1));
				quads.add(q);
			}

		}
		SuperMain.level.depth = 50;
		SuperMain.level.width = 50;
		SuperMain.level.addQuads(quads);
	}

	public void render() {

		// Vektor3D versch = new Vektor3D(0, -20, 30);
		// OGL.verschieb(versch);

		OGL.rot(rot100, new v3(1, 0, 0));
		OGL.rot(rot010, new Vektor3D(0, 1, 0));
		if (GelaendeFenster.cb_light.isSelected()) {
			GL11.glEnable(GL11.GL_LIGHTING);
		} else {
			GL11.glDisable(GL11.GL_LIGHTING);
		}

		OGL.verschieb(pos.add(new v3(0, hohe, 0)).negiere());

		ak.render();
		SuperMain.level.render();
		if (Editor.putter != null)
			Editor.putter.render();
		// berechnete hhenpunkte anzeigen
		if (false) {
			for (double x = 0; x < 50; x += 0.1) {
				for (double z = 0; z < 50; z += 0.1) {
					v3 v = new v3(x, SuperMain.level.getHeight(x, z), z);
					OGL.setColor(myColor.GREEN);
					OGL.line(1, v, v.add(new v3(0.01, 0.01, 0.01)));
				}
			}

		}

		OGL.verschieb(pos.add(new v3(0, hohe, 0)));

		OGL.rot(-rot010, new Vektor3D(0, 1, 0));
		OGL.rot(-rot100, new v3(1, 0, 0));

		// OGL.verschieb(versch.negiere());
	}

}
