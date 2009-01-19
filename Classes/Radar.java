package Classes;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Vector;

import main.LevelPlay;
import main.SuperMain;

/**
 * implements the radar
 * 
 * Once the objects are added to the radar their position can be updated any
 * 
 * @author laDanz
 * 
 */
public class Radar {

	LinkedList<Objekt> objekte = new LinkedList<Objekt>();
	// Speichert fr alle Objekte (POSITION,APPEARANCE)
	LinkedList<Vector> positions = new LinkedList<Vector>();
	Vektor3D playerpos = new v3();
	public Texture radarbg;
	Texture monster, goal;

	double max_sichtbereich = 50 + 10 * SuperMain.profil.mecha.getSensors();
	double increase = 0;

	/**
	 * Constructor
	 */
	public Radar() {
		objekte = new LinkedList<Objekt>();
		positions = new LinkedList<Vector>();
		playerpos = new v3();
		increase = 0;
		try {
			radarbg = SuperMain.loadTex("img/hud/radar.jpg");
			monster = SuperMain.loadTex("img/hud/monster.png");
			goal = SuperMain.loadTex("img/hud/goal.png");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			SuperMain.out(e);
		}

	}

	/**
	 * Increases view range for one tick
	 * 
	 * @param by
	 */
	public void increase_SB(double by) {
		increase += by;
	}

	/**
	 * adds a new object to Radar
	 * 
	 * @param o
	 */
	public void addObjektOfInterest(Objekt o) {
		if (o instanceof RadarAble) {
			objekte.add(o);
			Vector v = new Vector();
			v.add(o.getPosition());
			v.add(((RadarAble) o).getRadarAppearance());
			positions.add(v);
		} else {
			// System.err.println("Objekt " + o.toString()
			// + " ist nicht RadarAble");
		}
	}

	/**
	 * refresh object postions
	 */
	public void actualizeObjectPositions() {
		positions = new LinkedList<Vector>();
		for (Objekt o : objekte) {
			Vector v = new Vector();
			v.add(o.getPosition());
			v.add(((RadarAble) o).getRadarAppearance());
			positions.add(v);
		}

	}

	/**
	 * refresh Player position
	 */
	public void actualizePlayerPosition() {
		playerpos = LevelPlay.p.getPosition();

	}

	/**
	 * Renders the radar.
	 * 
	 * @param showback
	 *            Should a background be rendered too?
	 * @param groesse
	 *            The size of the radar.
	 * @param alpha
	 */
	public void render(boolean showback, double groesse, double alpha) {
		max_sichtbereich = 50 + 10 * SuperMain.profil.mecha.getSensors();
		// Grner scheiben BG
		if (increase > 0)
			max_sichtbereich += increase;
		double radargroesse = groesse;
		OGL.rot(-LevelPlay.p.blickrichtung, new v3(0, 0, 1));
		if (showback)
			OGL.viereck(new Vektor3D(-radargroesse / 2, -radargroesse / 2, 0),
					new v3(radargroesse, radargroesse, 0.01), radarbg);
		for (Vector v : positions) {
			Vektor3D delta = ((Vektor3D) v.get(0)).add(playerpos.mal(-1));
			int appearance = (Integer) v.get(1);

			// Ziel immer sichtbar machen
			if (appearance == RadarAble.GOAL && delta.length() >= max_sichtbereich / 2) {
				delta = delta.normierter().mal(max_sichtbereich / 2. - 1);
			}
			if (delta.length() > max_sichtbereich / 2) {
				continue;
			}
			double radarx = delta.getX1() / max_sichtbereich * radargroesse;
			double radary = -delta.getX3() / max_sichtbereich * radargroesse;
			Vektor3D pos = new Vektor3D(radarx, radary, 0.001);
			v3 spann = new v3(radargroesse / 13., radargroesse / 13., 0);

			switch (appearance) {
			case RadarAble.MONSTER:
				OGL.viereck(pos.add(spann.mal(-0.5)), spann, monster, 0, alpha);
				break;
			case RadarAble.GOAL:
				OGL.viereck(pos.add(spann.mal(-0.5)), spann, goal, 0, alpha);
				break;

			default:
				break;
			}

		}
		OGL.rot(LevelPlay.p.blickrichtung, new v3(0, 0, 1));
		max_sichtbereich -= increase;
		increase = 0;
	}

}
