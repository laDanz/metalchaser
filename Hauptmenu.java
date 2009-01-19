import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import Classes.GameState;
import Classes.OGL;
import Classes.Texture;
import Classes.TextureLoader;
import Classes.Vektor3D;

public class Hauptmenu implements GameState {
	TextureLoader loader;
	String state = "0";
	Texture BG, wurfel, weg, stange, ng, fundament, rasen, zaun, holz, baustellenboden, fahrzeug;
	double bewegung = 0;
	int Liste;
	boolean done = false;

	Vektor3D oldD;
	double drehwinkel = 0;

	public Hauptmenu() {
		loader = new TextureLoader();
		once();
	}

	public void doFinalizeActions() {
		// TODO Auto-generated method stub

	}

	public void render() {
		OGL.viereck(new Vektor3D(-50, -40, -82), new Vektor3D(175, 135, 0), BG);

		OGL.verschieb(new Vektor3D(bewegung, 0, 0));
		if (!done) {

			Liste = OGL.startlist();

			// Haus1 - neues spiel
			OGL.wuerfel_streck(new Vektor3D(-50, 0, -10), new Vektor3D(90, 10, -70), baustellenboden, 0, 1, 5);
			OGL.wuerfel_streck(new Vektor3D(-50, 0, 50), new Vektor3D(200, 10, -60), weg, 0, 1, 16, 8);
			OGL.wuerfel(new Vektor3D(10, 0.25, -10), new Vektor3D(10, 10, -20), weg);
			OGL.wuerfel(new Vektor3D(0, 1, -30), new Vektor3D(30, 10, -30), fundament);
			// Fahrzeug
			OGL.viereck(new Vektor3D(-15, 3, -22), new Vektor3D(30, 30, 0), fahrzeug, 0, 1.2);
			// schild
			OGL.wuerfel(new Vektor3D(5, 10, -20), new Vektor3D(3, 10, -1), fundament);
			OGL.wuerfel(new Vektor3D(1, 20, -20), new Vektor3D(11, 10, -1), ng, 0, 2);

			// Vektor3D zaun
			// Haus2 - spiel laden
			OGL.wuerfel_streck(new Vektor3D(40, 0, -10), new Vektor3D(60, 10, -70), rasen, 0, 1, 3);
			OGL.wuerfel(new Vektor3D(60, 0.25, -10), new Vektor3D(10, 10, -20), weg);
			OGL.wuerfel(new Vektor3D(50, 1, -30), new Vektor3D(40, 10, -30), fundament);
			OGL.wuerfel(new Vektor3D(50, 11, -30), new Vektor3D(3, 20, -30), holz);
			OGL.wuerfel_streck(new Vektor3D(50, 11, -60), new Vektor3D(40, 20, 3), holz, 0, 1, 1);
			OGL.wuerfel(new Vektor3D(87, 11, -30), new Vektor3D(3, 20, -30), holz);
			OGL.wuerfel(new Vektor3D(50, 11, -30), new Vektor3D(10, 20, -3), holz);
			OGL.wuerfel(new Vektor3D(70, 11, -30), new Vektor3D(20, 20, -3), holz);
			OGL.wuerfel_streck(new Vektor3D(75, 11, -15), new Vektor3D(10, 5, -5), holz, 0, 1, 2);
			OGL.wuerfel_streck(new Vektor3D(75, 16, -15), new Vektor3D(10, 5, -5), holz, 0, 1, 2);

			// zaun
			OGL.wuerfel_streck(new Vektor3D(40, 10, -80), new Vektor3D(60, 10, 0), zaun, 0, 2, 3);
			OGL.wuerfel_streck(new Vektor3D(40, 10, -10), new Vektor3D(0, 10, -80), zaun, 0, 2, 3);
			OGL.wuerfel_streck(new Vektor3D(100, 10, -10), new Vektor3D(0, 10, -80), zaun, 0, 2, 3);
			OGL.wuerfel_streck(new Vektor3D(40, 10, -10), new Vektor3D(20, 10, 0), zaun, 0, 2, 1);
			OGL.wuerfel_streck(new Vektor3D(70, 10, -10), new Vektor3D(0, 15, -10), zaun, 0, 2, 1);
			OGL.wuerfel_streck(new Vektor3D(70, 10, -10), new Vektor3D(30, 10, 0), zaun, 0, 2, 2, 1);

			OGL.endlist();
			done = true;
		} else {
			OGL.calllist(Liste);
		}

		OGL.verschieb(new Vektor3D(-bewegung, 0, 0));

	}

	public void once() {
		try {
			BG = loader.getTexture("img/bg/bg02.jpg");
			wurfel = loader.getTexture("img/kisten/gras.jpg");
			weg = loader.getTexture("img/kisten/flasterstein.png");
			stange = loader.getTexture("img/kisten/stange.jpg");
			ng = loader.getTexture("img/kisten/ng3.png");
			fundament = loader.getTexture("img/kisten/fundament.png");
			rasen = loader.getTexture("img/kisten/rasen.png");
			zaun = loader.getTexture("img/zaun/hauptseite.png");
			holz = loader.getTexture("img/kisten/holz2.png");
			baustellenboden = loader.getTexture("img/kisten/baustellenboden.png");
			fahrzeug = loader.getTexture("img/baufahrzeuge/gabelstapler.png");

		} catch (Exception e) {
			e.printStackTrace();
		}

		Mouse.setGrabbed(true);
	}

	public String getState() {
		// TODO Auto-generated method stub
		return state;
	}

	public void logic() {
		// TODO Auto-generated method stub
		if (Keyboard.isKeyDown(Keyboard.KEY_Z)) {
			bewegung += 0.5;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_X)) {
			bewegung -= 0.5;
		}
		Vektor3D d = new Vektor3D();

		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			d = d.add(new Vektor3D(-0.5, 0, 0));
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			d = d.add(new Vektor3D(0.5, 0, 0));
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			d = d.add(new Vektor3D(0, 0, -0.5));
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			d = d.add(new Vektor3D(0, 0, 0.5));
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			state = "back";
		}

		// gucken
		if (d.getX1() == 0) {
			drehwinkel = 0;
			if (d.getX3() < 0) {
				drehwinkel = 180;
			}
		} else if (d.getX1() > 0) {
			drehwinkel = 90;
			if (d.getX3() > 0) {
				drehwinkel = 45;
			}
			if (d.getX3() < 0) {
				drehwinkel = 135;
			}
		} else {
			drehwinkel = 270;
			if (d.getX3() > 0) {
				drehwinkel = 315;
			}
			if (d.getX3() < 0) {
				drehwinkel = 225;
			}
		}

		oldD = d;
		// auch schraeg gleichscnell gehen
		d = new Vektor3D(Math.abs(d.getX1()) * Math.sin(Math.toRadians(drehwinkel)), d.getX2(), Math.abs(d.getX3())
				* Math.cos(Math.toRadians(drehwinkel)));
		// TODO ################# Hier : #####################
		// ++++++++++++++ Pr�fen ob Bewegung zul�ssig (Zaun, Ende der Welt,
		// ....)
		// ++++++++++++++ analog zur Pr�fung ob er sich auf dem NeuesSpiel Feld
		// befindet
		// ++++++++++++++ ist die Bewegung nicht zul�ssig einfach ->d<- Null
		// setzen

		// Bildschirm verschieben

		// neus spiel - feld

	}
}
