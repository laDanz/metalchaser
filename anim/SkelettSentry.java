package anim;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import javax.swing.JFileChooser;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import main.SuperMain;

import org.lwjgl.opengl.GL11;

import Classes.Geschoss;
import Classes.OGL;
import Classes.Object3D;
import Classes.Vektor3D;
import Classes.myColor;
import Classes.v3;
import Fenster.AnimFenster;

/**
 * Skeleton and animation management for the sentrygun.
 * 
 * @author laDanz
 * 
 */
public class SkelettSentry {

	private Object3D nieten;

	private Object3D fuss;

	// private Object3D weapon2;*/

	// private Object3D hufte;
	protected Object3D drehgestell;

	protected Object3D stuetze;

	// private Object3D rHydra;

	protected Object3D gattling;
	SkelettTeil nietenT, fussT, drehgestellT, stuetzeT, gattlingT;

	/*
	 * private Object3D lOber;
	 * 
	 * private Object3D lUnter;
	 * 
	 * private Object3D lHydra;
	 * 
	 * private Object3D lFoot;
	 */

	private WinkelSet winkelset;

	private WinkelSet animate_ziel;

	private WinkelSet delta;

	private static WinkelSet verschiebWS, rotWS;

	/**
	 * Is the animation running?
	 */
	public boolean animation = false;

	private int animation_count = 0;

	String name;

	int id;

	/**
	 * Default constructor.<br>
	 * Loads the 3D models and the animations.
	 * 
	 * @param id
	 *            The parent id.
	 */
	public SkelettSentry(int id) {
		this.id = id;
		winkelset = new WinkelSet();
		verschiebWS = new WinkelSet();
		rotWS = new WinkelSet();
		this.name = "SentryNeu";
		// Modells laden
		setNieten(new Object3D(main.SuperMain.ordner + "models/data3/002.mco", "img/sentry/nieten.jpg"));
		nietenT = new SkelettTeil(nieten, id);
		drehgestell = new Object3D(main.SuperMain.ordner + "models/data3/000.mco", "img/sentry/drehgestell.jpg");
		drehgestellT = new SkelettTeil(drehgestell, id);
		stuetze = new Object3D(main.SuperMain.ordner + "models/data3/004.mco", "img/sentry/stuetze.jpg");
		stuetzeT = new SkelettTeil(stuetze, id);
		gattling = new Object3D(main.SuperMain.ordner + "models/data3/001.mco", "img/sentry/gettling.jpg");
		gattlingT = new SkelettTeil(gattling, id);
		fuss = new Object3D(main.SuperMain.ordner + "models/data3/003.mco", "img/sentry/standfuss.jpg");
		fussT = new SkelettTeil(fuss, id);
		stuetzeT.addVerbundener(drehgestellT);
		gattlingT.addVerbundener(drehgestellT);
		// kalib file vorhanden??? ---> laden

		File f = new File(SuperMain.ordner + "anim/" + name + "/kalib.xml");
		if (f.exists()) {
			loadAll(f.getAbsolutePath());
		}

		assaignSkelettTeile();
	}

	private void assaignSkelettTeile() {
		fussT.setVerschieb(verschiebWS.getWeapon1());
		fussT.setRot_punkt(rotWS.getWeapon1());
		gattlingT.setVerschieb(verschiebWS.getRFoot());
		gattlingT.setRot_punkt(rotWS.getRFoot());
		stuetzeT.setVerschieb(verschiebWS.getRUnter());
		stuetzeT.setRot_punkt(rotWS.getRUnter());
		nietenT.setVerschieb(verschiebWS.getTorso());
		nietenT.setRot_punkt(rotWS.getTorso());
		drehgestellT.setVerschieb(verschiebWS.getROber());
		drehgestellT.setRot_punkt(rotWS.getROber());

	}

	private Vektor3D x_axis = new v3(1, 0, 0);

	private Vektor3D y_axis = new v3(0, 1, 0);

	private Vektor3D z_axis = new v3(0, 0, 1);

	/**
	 * Rotate the scene by the angels provided by <code>winkels</code>.
	 * 
	 * @param winkels
	 *            The angles to rotate by.
	 */
	public static void rotate3Axis(Vektor3D winkels) {
		rotate3Axis(winkels, null);
	}

	/**
	 * Translate the scene by <code>verschiebung</code> and then rotate it by
	 * the angels provided by <code>winkels</code>.<br>
	 * 
	 * @param winkels
	 *            The angles to rotate by.
	 * @param verschiebung
	 *            The vector to translate by.
	 */
	public static void rotate3Axis(Vektor3D winkels, Vektor3D verschiebung) {
		if (verschiebung != null)
			OGL.verschieb(verschiebung);
		OGL.rot(winkels.getX1(), v3.x_axis);
		OGL.rot(winkels.getX2(), v3.y_axis);
		OGL.rot(winkels.getX3(), v3.z_axis);
		if (verschiebung != null)
			OGL.verschieb(verschiebung.negiere());
	}

	/**
	 * Undo the rotation.
	 * 
	 */
	public static void rotate3AxisBack(Vektor3D winkels) {
		rotate3AxisBack(winkels, null);
	}

	/**
	 * Undo the rotation and translation.
	 * 
	 */
	public static void rotate3AxisBack(Vektor3D winkels, Vektor3D verschiebung) {
		if (verschiebung != null)
			OGL.verschieb(verschiebung);
		OGL.rot(-winkels.getX3(), v3.z_axis);
		OGL.rot(-winkels.getX2(), v3.y_axis);
		OGL.rot(-winkels.getX1(), v3.x_axis);
		if (verschiebung != null)
			OGL.verschieb(verschiebung.negiere());
	}

	/**
	 * Berechnet die Hohe des Mechas aus den Winkeln der Beine
	 * 
	 * @return
	 */
	private double getHeight() {

		return 2;
	}

	/**
	 * Render the skeleton.
	 */
	public void render() {
		render(0, 0);
	}

	/**
	 * Render the skeleton with a special kalib_state and rotation.
	 * 
	 * @param kalibrate_state
	 *            A number which indicates which part is being kalibrated right
	 *            now.
	 * @param drehwinkel
	 *            A rotation in degree.
	 */
	public void render(int kalibrate_state, int drehwinkel) {
		render(kalibrate_state, drehwinkel, 0);
	}

	/**
	 * Render the skeleton with a special kalib_state and rotation.
	 * 
	 * @param kalibrate_state
	 *            A number which indicates which part is being kalibrated right
	 *            now.
	 * @param drehwinkel
	 *            A rotation in degree.
	 */

	public void render(int kalibrate_state, int drehwinkel, double hoehen_winkel) {
		tick();
		assaignSkelettTeile();

		if (getNieten().getDimension() == null)
			return;

		OGL.setColor(myColor.WHITE);

		fussT.render(new v3());

		nietenT.render(new v3());

		if (kalibrate_state == 0 || kalibrate_state >= 1 || AnimFenster.cb_rechtesbein.isSelected())
			drehgestellT.render(winkelset.getROber().add(
					(kalibrate_state == 2 && !AnimFenster.cb_rechtesbein.isSelected() ? new v3(0, 180, 0) : new v3(0,
							drehwinkel, 0))));

		if (kalibrate_state == 0 || kalibrate_state >= 3 || AnimFenster.cb_rechtesbein.isSelected())
			stuetzeT.render(winkelset.getRUnter().add(
					(kalibrate_state == 4 && !AnimFenster.cb_rechtesbein.isSelected() ? new v3(0, 0, 90) : new v3(0, 0,
							0))));

		if (kalibrate_state == 0 || kalibrate_state >= 5 || AnimFenster.cb_rechtesbein.isSelected())
			gattlingT.render(winkelset.getRFoot().add(
					(kalibrate_state == 6 && !AnimFenster.cb_rechtesbein.isSelected() ? new v3(0, 0, 90) : new v3(0, 0,
							hoehen_winkel))));

	}

	/**
	 * Simple Getter.
	 * 
	 * @return
	 */
	public static WinkelSet getRotWS() {
		return rotWS;
	}

	/**
	 * Simple Setter.
	 * 
	 * @param rotWS
	 */
	public static void setRotWS(WinkelSet rotWS) {
		SkelettSentry.rotWS = rotWS;
	}

	/**
	 * Simple Getter.
	 * 
	 * @return
	 */
	public static WinkelSet getVerschiebWS() {
		return verschiebWS;
	}

	/**
	 * Simple Setter.
	 * 
	 * @param verschiebWS
	 */
	public static void setVerschiebWS(WinkelSet verschiebWS) {
		SkelettSentry.verschiebWS = verschiebWS;
	}

	private void tick() {
		if (animation && animate_ziel != null) {
			setWinkelset(getWinkelset().minus(delta.teilenauf(-1)));
			animation_count--;
			animation = animation_count > 0;
		}

	}

	/**
	 * Simple Getter.
	 */
	public WinkelSet getWinkelset() {
		return winkelset;
	}

	/**
	 * Simple Setter.
	 * 
	 * @param winkelset
	 */
	public void setWinkelset(WinkelSet winkelset) {
		this.winkelset = new WinkelSet(winkelset);
	}

	/**
	 * Animate to a <code>WinkelSet</code>.
	 * 
	 * @param winkelSet
	 *            The <code>WinkelSet</code> to animate to.
	 */
	public void animateTo(WinkelSet winkelSet) {
		animateTo(winkelSet, 100);
	}

	/**
	 * Animate to a <code>WinkelSet</code>.
	 * 
	 * @param winkelSet
	 *            The <code>WinkelSet</code> to animate to.
	 * @param steps
	 *            The number of steps.
	 */
	public void animateTo(WinkelSet winkelSet, int steps) {
		animate_ziel = winkelSet;
		animation = true;
		calcAnimation(steps);
	}

	private void calcAnimation(int steps) {
		// Delta rauskriegen und auf 100 Tiks aufteilen
		animation_count = steps;
		delta = animate_ziel.minus(winkelset).teilenauf(animation_count);

	}

	/**
	 * Simple Setter.
	 * 
	 * @param torso
	 */
	public void setNieten(Object3D torso) {
		this.nieten = torso;
	}

	/**
	 * Simple Getter.
	 * 
	 * @return
	 */
	public Object3D getNieten() {
		return nieten;
	}

	/**
	 * Save all key-frames from the animation.
	 * 
	 * @param winkelset_save
	 *            The keyframeset.
	 */
	public void saveAll(WinkelSet[] winkelset_save) {

		// eventuell ordner erstellen
		File f = new File(main.SuperMain.ordner + "anim/" + name);
		if (!(f.exists() && f.isDirectory())) {
			f.mkdir();
		}
		// save dialog
		final File startvz = new File(SuperMain.ordner + "anim/" + name);
		final JFileChooser f_ = new JFileChooser(startvz) {

			public boolean accept(File f) {
				String s = SuperMain.ordner + "anim/" + name;
				if (!f.getAbsolutePath().toLowerCase().startsWith(s.toLowerCase())) {
					this.setCurrentDirectory(startvz);
				}

				return (f.getAbsolutePath().toLowerCase().endsWith("xml"));
			}

		};
		File savefile;
		if (f_.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			savefile = f_.getSelectedFile();
			if (!savefile.getAbsolutePath().toLowerCase().endsWith(".xml")) {
				savefile = new File(savefile.getAbsolutePath() + ".xml");
			}
		} else {
			return;
		}

		try {
			FileWriter fw = new FileWriter(savefile);
			BufferedWriter f1 = new BufferedWriter(fw);
			f1.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			f1.newLine();
			f1.write("<Animation>");
			f1.newLine();

			f1.write("<VerschiebWS>");
			f1.newLine();
			verschiebWS.save(f1);
			f1.write("</VerschiebWS>");
			f1.newLine();

			f1.write("<RotWS>");
			f1.newLine();
			rotWS.save(f1);
			f1.write("</RotWS>");
			f1.newLine();

			for (WinkelSet ws : winkelset_save) {
				f1.write("<WinkelSet>");
				f1.newLine();
				ws.save(f1);

				f1.write("</WinkelSet>");
				f1.newLine();
			}
			f1.write("</Animation>");
			f1.close();
		} catch (IOException e) {
			org.lwjgl.Sys.alert("", "Speichern fehlgeschlagen");
		}

	}

	/**
	 * Load all keyframes from a file.<br>
	 * Prompts for the file location.
	 * 
	 * @return The loaded keyframes.
	 */
	public WinkelSet[] loadAll() {
		return loadAll(null);
	}

	/**
	 * Load all keyframes from a specified file.<br>
	 * 
	 * @param filename
	 *            The filename of the file containing the keyframes.
	 * @return The loaded keyframes.
	 */
	public WinkelSet[] loadAll(String filename) {
		LinkedList<WinkelSet> res = new LinkedList<WinkelSet>();
		File savefile;

		if (filename == null) {
			// open dialog
			final File startvz = new File(SuperMain.ordner + "anim/" + name);
			final JFileChooser f_ = new JFileChooser(startvz) {

				public boolean accept(File f) {
					String s = SuperMain.ordner + "anim/" + name;
					if (!f.getAbsolutePath().toLowerCase().startsWith(s.toLowerCase())) {
						this.setCurrentDirectory(startvz);
					}

					return (f.getAbsolutePath().toLowerCase().endsWith("xml"));
				}

			};

			if (f_.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				savefile = f_.getSelectedFile();

			} else {
				return null;
			}
		} else {
			// Filename übergeben
			if (filename.startsWith("anim/"))
				filename = SuperMain.ordner + filename;

			savefile = new File(filename);
		}

		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = null;

		try {
			saxParser = factory.newSAXParser();
		} catch (Exception e) {
			e.printStackTrace();
			SuperMain.out(e);
			throw new RuntimeException("XML-Init Fehler beim Laden im Modul Anim: " + e);
		}

		AnimHandler handler = new AnimHandler();
		try {
			saxParser.parse(new File(savefile.getAbsolutePath()), handler);

			res = handler.getRes();

			verschiebWS = handler.getVerschiebWS();
			rotWS = handler.getRotWS();
			if (verschiebWS == null) {
				verschiebWS = new WinkelSet();
			}
			if (rotWS == null) {
				rotWS = new WinkelSet();
			}

		} catch (Exception e) {
			SuperMain.out("Fehler beim Laden im Modul Profil: " + e);
			e.printStackTrace();
			SuperMain.out(e);

		}

		return res.toArray(new WinkelSet[0]);

	}

	/**
	 * Checks whether the sentry collides with the given point.
	 * 
	 * @param pos_reltative
	 *            Position of the bullet.
	 * @param position
	 *            Position of the object.
	 * @return
	 */

	public boolean checkCollisionforObjekt(Vektor3D pos_reltative, Vektor3D position) {

		boolean result = false;
		// Kugelposition realativ zum Objekt machen
		pos_reltative = pos_reltative.sub(position);

		for (SkelettTeil sk : new SkelettTeil[] { drehgestellT, gattlingT, stuetzeT }) {
			if (!result)
				result = Geschoss.checkCollisionforSkelettTeilAsKasten(sk, pos_reltative.sub(sk.getVerschieb()).add(
						sk.getObject().getMittelPkt()));
			if (!SuperMain.dev)
				continue;
			OGL.setColor(myColor.RED);
			GL11.glDisable(GL11.GL_LIGHTING);
			OGL.disableDephTest();
			Vektor3D mtlpkt = position.add(sk.getVerschieb()).sub(sk.getObject().getMittelPkt());
			Vektor3D x = new v3(sk.getObject().getDimension().getX1(), 0, 0).mal(0.5);
			OGL.line(1, mtlpkt.add(x), mtlpkt.sub(x));
			Vektor3D y = (new v3(0, sk.getObject().getDimension().getX2(), 0)).mal(0.5);
			OGL.line(1, mtlpkt.add(y), mtlpkt.sub(y));
			Vektor3D z = (new v3(0, 0, sk.getObject().getDimension().getX3())).mal(0.5);
			OGL.line(1, mtlpkt.add(z), mtlpkt.sub(z));
			OGL.setColor(myColor.WHITE);
			OGL.enableDephTest();
			GL11.glEnable(GL11.GL_LIGHTING);

		}

		return result;
	}
}
