package main;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import Classes.GameState;
import Classes.KeyListener;
import Classes.OGL;
import Classes.Texture;
import Classes.Vektor3D;
import Classes.myColor;
import Classes.v3;

/**
 * Last state, shuttinng down the Game, Credits
 * 
 * @author ladanz
 * 
 */
public class EndState implements GameState, KeyListener {

	Texture creditslauf, mc, bg;
	String state = "0";
	// Verschiebung in Pixel
	Vektor3D verschieb, spann;

	boolean esc_was_down = true;
	long start;

	/**
	 * Constructor
	 */
	public EndState() {
		once();
		verschieb = new v3(0, -700, 0);
		spann = new v3(1024, 2048, 0);
		double scale = SuperMain.cm * 2;
		start = System.currentTimeMillis();

		// Keybuffer leeren, damit er nicht schon wegen eines vorher gedrückten
		// Esc aussteigt
		while (Keyboard.getNumKeyboardEvents() > 0) {
			Keyboard.next();
		}

	}

	//@override
	public void doFinalizeActions() {
		// TODO Auto-generated method stub

	}

	public String getState() {

		return state;
	}

	public void logic() {
		if (!((System.currentTimeMillis() - start) > 1000)) {
			return;
		}
		verschieb = verschieb.add(new v3(0, SuperMain.cm / 60 * OGL.fps_anpassung, 0));

		if (verschieb.getX2() > SuperMain.cm * (31))
			state = "exit";

	}

	//@override
	public void onKeyDown(int key) {
		if (key == Keyboard.KEY_ESCAPE) {

			state = "ente";
			esc_was_down = true;
		} else {
			esc_was_down = false;
		}

	}

	//@override
	public void onKeyUp(int key) {
		if (key == Keyboard.KEY_ESCAPE) {

			esc_was_down = false;
		}

	}

	//@override
	public void onMouseDown(int key) {
		// TODO Auto-generated method stub

	}

	//@override
	public void onMouseUp(int key) {
		// TODO Auto-generated method stub

	}

	/**
	 * Single configurations
	 */
	public void once() {
		try {
			creditslauf = SuperMain.loadTex("img/menu/credits.png");
			mc = SuperMain.loadTex("img/menu/credits1.png");
			bg = SuperMain.loadTex("img/menu/mainmenu.jpg");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			SuperMain.out(e);
		}

	}

	public void render() {
		OGL.setColor(myColor.WHITE);
		OGL.Hintergrund(bg);
		OGL.verschieb(verschieb);
		// OGL.skaliere(skalier);
		OGL.setOrthoOn();

		//OGL.viereck(verschieb.add(new v3(0, 956, 0)), new v3(1024, 512, 0), mc, 0, 1.1);
		//OGL.viereck(verschieb.add(new v3(0, -1156, 0)), spann, creditslauf, 0, 1.1);
		
		OGL.viereck(verschieb.add(new v3(256, 956, 0)), new v3(512, 256, 0), mc, 0, 1.1);
		//OGL.viereck(verschieb.add(new v3(1024, -578, 0)), new v3(512, 1024, 0), creditslauf, 0, 1.1);
		OGL.viereck(verschieb.add(new v3(256, 0, 0)), new v3(512, 1024, 0), creditslauf, 0, 1.1);
		
		OGL.setOrthoOff();
		// OGL.skaliere(skalier.reziproke());
		OGL.verschieb(verschieb.negiere());

	}

}
