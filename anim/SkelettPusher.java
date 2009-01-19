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
import Classes.Geschoss;
import Classes.OGL;
import Classes.Object3D;
import Classes.Vektor3D;
import Classes.myColor;
import Classes.v3;
import Fenster.AnimFenster;

/**
 * Skeleton and animation management.
 * 
 * @author laDanz
 * 
 */
public class SkelettPusher {

	private Object3D nieten;

	protected Object3D body;

	protected Object3D propellor;

	protected Object3D lufter;

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

	int propeller_rot = 0;

	int id;

	/**
	 * Default constructor.<br>
	 * Loads the 3D models and the animations.
	 * 
	 * @param id
	 *            The parent id.
	 */
	public SkelettPusher(int id) {
		this.id = id;
		winkelset = new WinkelSet();
		verschiebWS = new WinkelSet();
		rotWS = new WinkelSet();
		this.name = "Pusher";
		// Modells laden
		setNieten(new Object3D(main.SuperMain.ordner + "models/data4/pub0/001.mco", "img/pusher/nieten.jpg"));

		body = new Object3D(main.SuperMain.ordner + "models/data4/pub0/003.mco", "img/pusher/ysmile.jpg");

		propellor = new Object3D(main.SuperMain.ordner + "models/data4/pub0/002.mco", "img/pusher/propeller.jpg");

		lufter = new Object3D(main.SuperMain.ordner + "models/data4/pub0/000.mco", "img/pusher/lufter.jpg");

		// kalib file vorhanden??? ---> laden

		File f = new File(SuperMain.ordner + "anim/" + name + "/kalib.xml");
		if (f.exists()) {
			loadAll(f.getAbsolutePath());
		}
	}

	private Vektor3D x_axis = v3.x_axis;

	private Vektor3D y_axis = v3.y_axis;

	private Vektor3D z_axis = v3.z_axis;

	private void rotate3Axis(Vektor3D winkels) {
		rotate3Axis(winkels, null);
	}

	private void rotate3Axis(Vektor3D winkels, Vektor3D verschiebung) {
		if (verschiebung != null)
			OGL.verschieb(verschiebung);
		OGL.rot(winkels.getX1(), x_axis);
		OGL.rot(winkels.getX2(), y_axis);
		OGL.rot(winkels.getX3(), z_axis);
		if (verschiebung != null)
			OGL.verschieb(verschiebung.negiere());
	}

	private void rotate3AxisBack(Vektor3D winkels) {
		rotate3AxisBack(winkels, null);
	}

	private void rotate3AxisBack(Vektor3D winkels, Vektor3D verschiebung) {
		if (verschiebung != null)
			OGL.verschieb(verschiebung);
		OGL.rot(-winkels.getX3(), z_axis);
		OGL.rot(-winkels.getX2(), y_axis);
		OGL.rot(-winkels.getX1(), x_axis);
		if (verschiebung != null)
			OGL.verschieb(verschiebung.negiere());
	}

	/**
	 * Calculates the height of the pusher.
	 * 
	 * @return the height.
	 */
	public double getHeight() {

		return 2 + 0.3 * Math.sin(SuperMain.fps / 15. + id);// rbein;
	}

	Vektor3D huftenhohe;

	/**
	 * Renders the pusher.
	 */
	public void render() {
		render(0);
	}

	/**
	 * Renders the pusher with a certain kalib_state.
	 * 
	 * @param kalibrate_state
	 *            A number which indicates which part is being kalibrated right
	 *            now.
	 */
	public void render(int kalibrate_state) {
		tick();
		propeller_rot++;
		if (propeller_rot > 360)
			propeller_rot -= 360;

		huftenhohe = new v3(0, getHeight(), 0);
		if (getNieten().getDimension() == null)
			return;
		// tor_ver = new v3(0, getNieten().getDimension().getX2(), 0);

		OGL.setColor(myColor.WHITE);
		rotate3Axis(winkelset.getTorso());
		// torso.render();
		// OGL.verschieb(new v3(0, weapon1.getMittelPkt().getX2(), 0));
		rotate3Axis(winkelset.getWeapon1());
		// OGL.verschieb(new v3(0, weapon1.getMittelPkt().getX2(),
		// 0).negiere());
		// weapon1.render();
		// OGL.verschieb(new v3(0, weapon1.getMittelPkt().getX2(), 0));
		rotate3AxisBack(winkelset.getWeapon1());
		// OGL.verschieb(new v3(0, weapon1.getMittelPkt().getX2(),
		// 0).negiere());
		rotate3AxisBack(winkelset.getTorso());
		// hufte.render();

		OGL.verschieb(huftenhohe);
		{

			{

			}

			// rechtes bein

			{
				// rechter oberschenkel

				OGL.verschieb(verschiebWS.getROber());
				{

					Vektor3D ober_rot = rotWS.getROber();
					rotate3Axis(winkelset.getROber().add(
							(kalibrate_state == 2 && !AnimFenster.cb_rechtesbein.isSelected() ? new v3(0, 0, 90)
									: new v3())), ober_rot);

					OGL.verschieb(body.getMittelPkt().negiere());
					if (kalibrate_state == 0 || kalibrate_state >= 1 || AnimFenster.cb_rechtesbein.isSelected())
						body.render(id);

					OGL.verschieb(body.getMittelPkt());

					OGL.verschieb(verschiebWS.getTorso());
					OGL.verschieb(getNieten().getMittelPkt().negiere());
					getNieten().render(id);
					OGL.verschieb(getNieten().getMittelPkt());
					OGL.verschieb(verschiebWS.getTorso().negiere());

					// rechter uschenkel
					Vektor3D unter_verschieb = verschiebWS.getRUnter();
					OGL.verschieb(unter_verschieb);
					{

						Vektor3D unter_rot = rotWS.getRUnter();

						rotate3Axis(winkelset.getRUnter().add(
								(kalibrate_state == 4 && !AnimFenster.cb_rechtesbein.isSelected() ? new v3(0, 0, 90)
										: new v3(0, propeller_rot, 0))), unter_rot);

						OGL.verschieb(propellor.getMittelPkt().negiere());
						if (kalibrate_state == 0 || kalibrate_state >= 3 || AnimFenster.cb_rechtesbein.isSelected())
							propellor.render(id);
						OGL.verschieb(propellor.getMittelPkt());
						rotate3AxisBack(winkelset.getRUnter().add(
								(kalibrate_state == 4 && !AnimFenster.cb_rechtesbein.isSelected() ? new v3(0, 0, 90)
										: new v3(0, propeller_rot, 0))), unter_rot);
						// rechter hydra+fuss
						double anpassung_hydra = 0.6 + winkelset.getRHydra_stauch();

						{

							Vektor3D fuss_verschieb = verschiebWS.getRFoot();
							OGL.verschieb(fuss_verschieb);
							{

								Vektor3D fuss_rot = rotWS.getRFoot();
								rotate3Axis(winkelset.getRFoot().add(
										(kalibrate_state == 6 && !AnimFenster.cb_rechtesbein.isSelected() ? new v3(0,
												0, 90) : new v3())), fuss_rot);

								OGL.verschieb(lufter.getMittelPkt().negiere());
								if (kalibrate_state == 0 || kalibrate_state >= 5
										|| AnimFenster.cb_rechtesbein.isSelected())
									lufter.render(id);
								OGL.verschieb(lufter.getMittelPkt());

								rotate3AxisBack(winkelset.getRFoot().add(
										(kalibrate_state == 6 && !AnimFenster.cb_rechtesbein.isSelected() ? new v3(0,
												0, 90) : new v3())), fuss_rot);

								OGL.verschieb(fuss_verschieb.negiere());
							}

						}

						OGL.verschieb(unter_verschieb.negiere());
					}

					rotate3AxisBack(winkelset.getROber().add(
							(kalibrate_state == 2 && !AnimFenster.cb_rechtesbein.isSelected() ? new v3(0, 0, 90)
									: new v3())), ober_rot);

					OGL.verschieb(verschiebWS.getROber().negiere());
				}

			}

			// rechtes bein fertig

			OGL.verschieb(huftenhohe.negiere());
		}

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
		SkelettPusher.rotWS = rotWS;
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
		SkelettPusher.verschiebWS = verschiebWS;
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
	 * 
	 * @return
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
	 * @param pos
	 *            A point.
	 * @return Does it collide?
	 */

	public boolean checkCollisionforObjekt(Vektor3D pos) {

		boolean result = false;

		result = Geschoss.checkCollisionforObjektAsEllipse(nieten.getDimension(), new v3().add(huftenhohe).add(
				verschiebWS.getROber()), pos, nieten.getMittelPkt());
		if (result)
			return result;
		result = Geschoss.checkCollisionforObjektAsEllipse(body.getDimension(), new v3().add(huftenhohe).add(
				verschiebWS.getROber()), pos, body.getMittelPkt());

		return result;
	}
}
