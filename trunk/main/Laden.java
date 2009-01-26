package main;

import java.io.IOException;
import java.util.LinkedList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import Classes.GameState;
import Classes.KeyListener;
import Classes.OGL;
import Classes.ParameterAble;
import Classes.Texture;
import Classes.Vektor3D;
import Classes.myColor;
import Classes.myText;
import Classes.v3;

/**
 * Loading window that occurs when changing a GameState
 * 
 * @author laDanz
 * 
 */
public class Laden implements GameState, KeyListener {
	// static Text text;
	String s = "...";
	GameState gs;
	static Texture tex = null;
	static Texture background = null;
	String state = "0";
	static Laden last_instance = null;
	private static boolean ready;
	LinkedList<String> text;

	private boolean render_hit_space = false;

	/**
	 * Constructor
	 */
	public Laden() {
		this.once();
		text = new LinkedList<String>();
		ready = false;
		if (tex == null) {
			SuperMain.toRun.add(new Runnable() {

				public void run() {

					try {
						tex = SuperMain.loadTex("img/menu/laden.png");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						SuperMain.out(e);
					}

				}

			});

		}

		last_instance = this;

	}

	// @override
	public void onKeyDown(int key) {
		ready = (key == Keyboard.KEY_SPACE);

	}

	// @override
	public void onKeyUp(int key) {
		// TODO Auto-generated method stub

	}

	// @override
	public void onMouseDown(int key) {
		// TODO Auto-generated method stub

	}

	// @override
	public void onMouseUp(int key) {
		// TODO Auto-generated method stub

	}

	public static void loadBackgroundTexture(final String short_filename) {
		// SuperMain.toRun.add(new Runnable() {
		//
		// public void run() {
		//
		try {
			last_instance.background = SuperMain.loadTex(short_filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			SuperMain.out(e);
		}
		//
		// }
		//
		// });
	}

	/**
	 * Loading the loading-Texture
	 * 
	 * @param tex
	 */
	public Laden(Texture tex) {
		this();
		this.tex = tex;

	}

	// @override
	public void doFinalizeActions() {
		last_instance = null;
	}

	/**
	 * @deprecated
	 */
	public Laden(String gamestate) {
		if (true)
			throw new RuntimeException("NotSupported!!! Laden#130");
		this.once();

		t2 T2 = new t2(gamestate);

		T2.start();

	}

	public String getState() {
		// TODO Auto-generated method stub
		return state;
	}

	public void logic() {
		// TODO Auto-generated method stub

	}

	public void once() {
		// TODO Auto-generated method stub
		// text = new Text();
	}

	/**
	 * Add Text to the loading Console
	 * 
	 * @param s
	 */
	public static void addText(String s) {
		last_instance.insertText(s);
	}

	/**
	 * Insert Text to the loading Console
	 * 
	 * @param s2
	 */
	private void insertText(String s2) {
		// TODO Auto-generated method stub
		text.add(s2);
		if (text.size() > 4)
			text.removeFirst();
		// Display.update();
		render();
		Display.sync(OGL.FRAMERATE);
		Display.update();
	}

	/**
	 * Insert a dot to the last line of the loading Console.
	 * 
	 * @param s2
	 */
	public static void insertDot() {
		try {
			last_instance.insertDotA();
		} catch (Exception e) {

		}
	}

	/**
	 * Insert a dot to the last line of the loading Console.
	 * 
	 * @param s2
	 */
	private void insertDotA() {

		String s = text.removeLast();
		s = s.concat(".");
		text.add(s);
		if (s.length() > 12 && s.indexOf('.') > 0) {
			s = text.removeLast();
			s = s.substring(0, s.indexOf('.'));
			text.add(s);
		}
		// System.out.println("Insert dot @" + System.currentTimeMillis() + " :
		// " + s);
		render();
		Display.sync(OGL.FRAMERATE);
		Display.update();
	}

	public void render() {
		OGL.setColor(myColor.WHITE);

		if (background != null) {
			OGL.Hintergrund(background);
		}

		myText.setSelected_text(SuperMain.TEXT_CONSOLE_SMALL);
		myText.setSelected_big_text(SuperMain.TEXT_CONSOLE_BIG);
		OGL.setOrthoOn();
		double cm = OGL.screenwh.getX1() / 21;
		Vektor3D scale = new Vektor3D(0.5 * cm, 0.5 * cm, 1);
		if (background == null) {
			// Normales Lade Fenster

			OGL.viereck(new v3(0, 0, 0), new v3(22 * cm, 16 * cm, 0), null, 0, 0.75, 0, 0, 0);
			OGL.setColor(myColor.WHITE);
			OGL.viereck(new v3(1 * cm, 3 * cm, 0), new v3(20 * cm, 10 * cm, 0), tex, 0, 1.1);

			Vektor3D wo = new Vektor3D(4.5 * cm, 5.8 * cm, 0);

			for (String s_ : text) {
				myText.out(s_, wo.add(new Vektor3D(0, 0, -0.2)), scale, new Vektor3D(1, 1, 1), 1.1, 0);
				wo = wo.add(new v3(0, -0.6 * cm, 0));
			}
			if (text.size() > 0 && text.getLast().startsWith("-Hit S")) {
				// automatisch space drücken
				LevelPlay.space_hitted = true;
				SuperMain.toRun.add(new Runnable() {
					// @override
					public void run() {
						Laden.last_instance = null;

					}
				});
			}

		} else {
			// Wenn Hintergrund ist
			{
				if (text != null && text.size() > 0) {
					String s_ = text.getLast();
					if (!((s_.startsWith("-Hit S") && SuperMain.fps < 40 && SuperMain.fps > 10))) {
						myText.out(s_, new Vektor3D(8.5 * cm, 1.7 * cm, 0), scale, new Vektor3D(1, 1, 1), 1.1, 0);
					}
				}

			}
		}

		OGL.setColor(new Vektor3D(1, 1, 1));
		OGL.setOrthoOff();
	}

	/**
	 * Experimental Class. No Use for this in the current Build
	 * 
	 * @author ladanz
	 * 
	 */
	class t2 extends Thread {
		String gamestate;

		public t2(String gamestate) {
			this.gamestate = gamestate;
		}

		// @override
		public void run() {

			System.out.println("gs");
			try {
				String[] s = gamestate.split(",");
				gamestate = s[0];
				gamestate = "main." + gamestate;
				// for(String ss:s)
				// System.out.println("----->"+ss);

				ClassLoader cl = ClassLoader.getSystemClassLoader();
				Class c3 = cl.loadClass(gamestate);
				gs = (GameState) c3.newInstance();
				// System.out.println("-> neue Klasse "+s[0]+" geladen");
				int i = 1;
				while (gs instanceof ParameterAble && s.length > i) {
					((ParameterAble) gs).setParameter(s[i]);
					i++;
				}
			} catch (ClassNotFoundException e) {
				System.out.println("Wird aufgrund der Aussage beendet: " + gamestate);
				OGL.finished = true;
			} catch (InstantiationException e) {
				e.printStackTrace();
				SuperMain.out(e);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				SuperMain.out(e);
			}
		}

	}

	public static boolean HasHitAnyKey() {
		return ready;
	}

	public static void renderHitSpace() {
		if (last_instance.render_hit_space == false) {
			SuperMain.toRun.add(new Runnable() {
				// @override
				public void run() {
					Laden.addText("-Hit Space-");
				}
			});

		}
		last_instance.render_hit_space = true;

	}

}

// Punkte reinsetzten
// insertdotthread = new Thread() {
// //@override
// public void run() {
//
// while (true) {
// // super.run();
// try {
// sleep(1000);
// } catch (InterruptedException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// }
// try {
// Laden.this.insertDotA();
// } catch (NullPointerException e) {
// // Wirft beim rendern in glcolor eine Exc.
// }
// }
// }
// };
// insertdotthread.start();
