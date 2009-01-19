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
 * Skeleton and animations management for the mecha.
 * 
 * @author laDanz
 * 
 */
public class SkelettZ300 {

	private Object3D torso;

	private Object3D hufte;

	private Object3D weapon1;

	private Object3D weapon2;

	private Object3D rOber;

	private Object3D rUnter;

	// private Object3D rHydra;

	private Object3D rFoot;

	private Object3D lOber;

	private Object3D lUnter;

	// private Object3D lHydra;

	private Object3D lFoot;

	private WinkelSet winkelset;

	private WinkelSet animate_ziel;

	private WinkelSet delta;

	private static WinkelSet verschiebWS, rotWS;

	/**
	 * Is the animation running?
	 */
	public boolean animation = false;

	private int animation_count = 0;

	private String name;

	/**
	 * Default constructor.<br>
	 * Loads model files and animation files for the mecha. <br>
	 * 
	 * @param name
	 *            Name des Modells.
	 */
	public SkelettZ300(String name) {
		winkelset = new WinkelSet();
		verschiebWS = new WinkelSet();
		rotWS = new WinkelSet();
		this.name = name;
		String lname = "data5";
		// Modells laden
		setTorso(new Object3D(main.SuperMain.ordner + "models/" + lname + "/Data/007.mco", "img/" + name
				+ "/Texturen/torso.png"));
		hufte = new Object3D(main.SuperMain.ordner + "models/" + lname + "/Data/000.mco", "img/" + name
				+ "/Texturen/hufte.png");

		weapon1 = new Object3D(main.SuperMain.ordner + "models/" + lname + "/Data/008.mco", "img/" + name
				+ "/Texturen/weapon1.png");

		weapon2 = new Object3D(main.SuperMain.ordner + "models/" + lname + "/Data/009.mco", "img/" + name
				+ "/Texturen/weapon2.png");

		rOber = new Object3D(main.SuperMain.ordner + "models/" + lname + "/Data/005.mco", "img/" + name
				+ "/Texturen/Ober.png");
		rUnter = new Object3D(main.SuperMain.ordner + "models/" + lname + "/Data/006.mco", "img/" + name
				+ "/Texturen/Unter.png");
		// rHydra = new Object3D(main.SuperMain.ordner + "models/" + name +
		// "/rHydra.mco", "img\\test.png");
		rFoot = new Object3D(main.SuperMain.ordner + "models/" + lname + "/Data/004.mco", "img/" + name
				+ "/Texturen/Foot.png");

		lOber = new Object3D(main.SuperMain.ordner + "models/" + lname + "/Data/002.mco", "img/" + name
				+ "/Texturen/Ober.png");
		lUnter = new Object3D(main.SuperMain.ordner + "models/" + lname + "/Data/003.mco", "img/" + name
				+ "/Texturen/Unter.png");
		// lHydra = new Object3D(main.SuperMain.ordner + "models/" + name +
		// "/lHydra.mco", "img\\cement_stony.png");
		lFoot = new Object3D(main.SuperMain.ordner + "models/" + lname + "/Data/001.mco", "img/" + name
				+ "/Texturen/Foot.png");

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
	 * Berechnet die Hohe des Mechas aus den Winkeln der Beine
	 * 
	 * @return
	 */
	private double getHeight() {

		return 2.7;
	}

	/**
	 * Rotates a point by the angles from <code>winkel</code>. <br>
	 * <b>Probably a little buggy.</b>
	 * 
	 * @param point
	 *            A point.
	 * @param winkel
	 *            Angles for each dimension.
	 * @return The rotated Point.
	 */
	public static Vektor3D getHeightUnderRot(Vektor3D point, Vektor3D winkel) {
		// um x:
		Vektor3D dimension = new v3(point);
		double newx1 = dimension.getX1();
		double newx2 = Math.cos(Math.toRadians(winkel.getX1())) * dimension.getX2()
				+ Math.sin(Math.toRadians(winkel.getX1())) * dimension.getX3();
		double newx3 = -Math.sin(Math.toRadians(winkel.getX1())) * dimension.getX2()
				+ Math.cos(Math.toRadians(winkel.getX1())) * dimension.getX3();
		dimension = new v3(newx1, newx2, newx3);
		// um y
		newx1 = Math.cos(Math.toRadians(winkel.getX2())) * dimension.getX1() - Math.sin(Math.toRadians(winkel.getX2()))
				* dimension.getX3();
		newx2 = dimension.getX2();
		newx3 = +Math.sin(Math.toRadians(winkel.getX2())) * dimension.getX1()
				+ Math.cos(Math.toRadians(winkel.getX2())) * dimension.getX3();
		dimension = new v3(newx1, newx2, newx3);
		// um z
		newx1 = Math.cos(Math.toRadians(winkel.getX3())) * dimension.getX1() + Math.sin(Math.toRadians(winkel.getX3()))
				* dimension.getX2();
		newx2 = -Math.sin(Math.toRadians(winkel.getX3())) * dimension.getX1()
				+ Math.cos(Math.toRadians(winkel.getX3())) * dimension.getX2();
		newx3 = dimension.getX3();
		dimension = new v3(newx1, newx2, newx3);
		return dimension;
	}

	Vektor3D huftenhohe;
	Vektor3D tor_ver;
	Vektor3D beinverschieb;

	/**
	 * Renders the skeleton with kalib_state 0 and hip-angle 0.
	 */
	public void render() {
		render(0, 0);
	}

	/**
	 * Renders the skeleton with a certain kalib_state and hip-angle 0.
	 * 
	 * @param kalibrate_state
	 *            A number which indicates which part is being kalibrated right
	 *            now.
	 */
	public void render(int kalibrate_state) {
		render(kalibrate_state, 0);
	}

	/**
	 * Renders the skeleton with a certain kalib_state and hip-angle.
	 * 
	 * @param kalibrate_state
	 *            A number which indicates which part is being kalibrated right
	 *            now.
	 * @param huftwinkel
	 *            The hip is rotated by this angle.
	 */
	public void render(int kalibrate_state, int huftwinkel) {
		tick();

		huftenhohe = new v3(0, getHeight() + winkelset.getHeight(), 0);
		if (getTorso().getDimension() == null)
			return;
		tor_ver = new v3(0, getTorso().getDimension().getX2() - hufte.getDimension().getX2() / 2, 0);

		Vektor3D huft_dreh = new v3(0, huftwinkel, 0);
		Vektor3D huft_dreh_verschieb = new v3(+0.3, 0, 0);

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

			OGL.verschieb(tor_ver);
			{
				rotate3Axis(winkelset.getTorso());
				getTorso().render();
				OGL.verschieb(new v3(0, weapon1.getMittelPkt().getX2(), 0));
				{
					rotate3Axis(winkelset.getWeapon1());
					OGL.verschieb(new v3(0, weapon1.getMittelPkt().getX2(), 0).negiere());
				}
				weapon1.render();
				weapon2.render();
				OGL.verschieb(new v3(0, weapon1.getMittelPkt().getX2(), 0));
				{
					rotate3AxisBack(winkelset.getWeapon1());
					OGL.verschieb(new v3(0, weapon1.getMittelPkt().getX2(), 0).negiere());
				}
				rotate3AxisBack(winkelset.getTorso());
				OGL.verschieb(tor_ver.negiere());
			}
			rotate3Axis(huft_dreh, huft_dreh_verschieb);
			hufte.render();

			// rechtes bein
			beinverschieb = new Vektor3D(0, 0, hufte.getDimension().getX3() / 2 - 0.3);
			OGL.verschieb(beinverschieb);
			{
				// rechter oberschenkel

				OGL.verschieb(verschiebWS.getROber());
				{

					Vektor3D ober_rot = rotWS.getROber();
					rotate3Axis(winkelset.getROber().add(
							(kalibrate_state == 2 && !AnimFenster.cb_rechtesbein.isSelected() ? new v3(0, 0, 90)
									: new v3())), ober_rot);

					OGL.verschieb(rOber.getMittelPkt().negiere());
					if (kalibrate_state == 0 || kalibrate_state >= 1 || AnimFenster.cb_rechtesbein.isSelected())
						rOber.render();
					OGL.verschieb(rOber.getMittelPkt());

					// rechter uschenkel
					Vektor3D unter_verschieb = verschiebWS.getRUnter();
					OGL.verschieb(unter_verschieb);
					{

						Vektor3D unter_rot = rotWS.getRUnter();

						rotate3Axis(winkelset.getRUnter().add(
								(kalibrate_state == 4 && !AnimFenster.cb_rechtesbein.isSelected() ? new v3(0, 0, 90)
										: new v3())), unter_rot);

						OGL.verschieb(rUnter.getMittelPkt().negiere());
						if (kalibrate_state == 0 || kalibrate_state >= 3 || AnimFenster.cb_rechtesbein.isSelected())
							rUnter.render();
						OGL.verschieb(rUnter.getMittelPkt());

						// rechter hydra+fuss
						double anpassung_hydra = 0.6 + winkelset.getRHydra_stauch();
						Vektor3D hydra_verschieb = new v3(+/* rHydra.getDimension().getX1() */0 / 2 - anpassung_hydra
								* (0.45), -/*
						 * rHydra.getDimension() .getX2()
						 */0 + anpassung_hydra, 0.0);
						OGL.verschieb(hydra_verschieb);
						{

							// rHydra.render();

							Vektor3D fuss_verschieb = verschiebWS.getRFoot();
							OGL.verschieb(fuss_verschieb);
							{

								Vektor3D fuss_rot = rotWS.getRFoot();
								rotate3Axis(winkelset.getRFoot().add(
										(kalibrate_state == 6 && !AnimFenster.cb_rechtesbein.isSelected() ? new v3(0,
												0, 90) : new v3())), fuss_rot);

								OGL.verschieb(rFoot.getMittelPkt().negiere());
								if (kalibrate_state == 0 || kalibrate_state >= 5
										|| AnimFenster.cb_rechtesbein.isSelected())
									rFoot.render();
								OGL.verschieb(rFoot.getMittelPkt());

								rotate3AxisBack(winkelset.getRFoot().add(
										(kalibrate_state == 6 && !AnimFenster.cb_rechtesbein.isSelected() ? new v3(0,
												0, 90) : new v3())), fuss_rot);

								OGL.verschieb(fuss_verschieb.negiere());
							}
							OGL.verschieb(hydra_verschieb.negiere());
						}
						rotate3AxisBack(winkelset.getRUnter().add(
								(kalibrate_state == 4 && !AnimFenster.cb_rechtesbein.isSelected() ? new v3(0, 0, 90)
										: new v3())), unter_rot);
						OGL.verschieb(unter_verschieb.negiere());
					}

					rotate3AxisBack(winkelset.getROber().add(
							(kalibrate_state == 2 && !AnimFenster.cb_rechtesbein.isSelected() ? new v3(0, 0, 90)
									: new v3())), ober_rot);

					OGL.verschieb(verschiebWS.getROber().negiere());
				}

				OGL.verschieb(beinverschieb.negiere());
			}

			// rechtes bein fertig

			linkesbein(kalibrate_state);

			rotate3AxisBack(huft_dreh, huft_dreh_verschieb);
			OGL.verschieb(huftenhohe.negiere());

		}

	}

	/**
	 * Simple getter.
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
		SkelettZ300.rotWS = rotWS;
	}

	/**
	 * Simple getter.
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
		SkelettZ300.verschiebWS = verschiebWS;
	}

	/**
	 * Swap the angles of the legs.
	 */
	public void invert() {
		Vektor3D save = winkelset.getLFoot();
		winkelset.setLFoot(winkelset.getRFoot());
		winkelset.setRFoot(save);

		save = winkelset.getLOber();
		winkelset.setLOber(winkelset.getROber());
		winkelset.setROber(save);

		save = winkelset.getLUnter();
		winkelset.setLUnter(winkelset.getRUnter());
		winkelset.setRUnter(save);
	}

	private void linkesbein(int kalibrate_state) {
		// lechtes bein
		Vektor3D beinvelschieb = new Vektor3D(0, 0, hufte.getDimension().getX3() / 2 - 0.3).negiere();
		OGL.verschieb(beinvelschieb);
		// lechtel obelschenkel

		Vektor3D obel_velschieb = verschiebWS.getLOber();
		OGL.verschieb(obel_velschieb);

		Vektor3D obel_lot = rotWS.getLOber();
		rotate3Axis(winkelset.getLOber().add(
				(kalibrate_state == 2 && AnimFenster.cb_rechtesbein.isSelected() ? new v3(0, 0, 90) : new v3())),
				obel_lot);
		// OGL.rot(180, v3.y_axis);
		OGL.verschieb(lOber.getMittelPkt().negiere());
		if (kalibrate_state == 0 || kalibrate_state >= 1 || !AnimFenster.cb_rechtesbein.isSelected())
			lOber.render();
		OGL.verschieb(lOber.getMittelPkt());
		// OGL.rot(-180, v3.y_axis);

		// lechtel uschenkel
		Vektor3D untel_velschieb = verschiebWS.getLUnter();
		OGL.verschieb(untel_velschieb);

		Vektor3D untel_lot = rotWS.getLUnter();

		rotate3Axis(winkelset.getLUnter().add(
				(kalibrate_state == 4 && AnimFenster.cb_rechtesbein.isSelected() ? new v3(0, 0, 90) : new v3())),
				untel_lot);

		OGL.verschieb(lUnter.getMittelPkt().negiere());
		if (kalibrate_state == 0 || kalibrate_state >= 3 || !AnimFenster.cb_rechtesbein.isSelected())
			lUnter.render();
		OGL.verschieb(lUnter.getMittelPkt());

		// lechtel hydla+fuss
		double anpassung_hydla = 0.6 + winkelset.getLHydra_stauch();
		Vektor3D hydla_velschieb = new v3(+/* lHydra.getDimension().getX1() */0 / 2 - anpassung_hydla * (0.45) - 0.3,
				-/*
				 * lHydra
				 * .getDimension().getX2()
				 */0 + anpassung_hydla, 0.);
		OGL.verschieb(hydla_velschieb);

		// lHydra.render();

		Vektor3D fuss_velschieb = verschiebWS.getLFoot();
		OGL.verschieb(fuss_velschieb);

		Vektor3D fuss_lot = rotWS.getLFoot();
		rotate3Axis(winkelset.getLFoot().add(
				(kalibrate_state == 6 && AnimFenster.cb_rechtesbein.isSelected() ? new v3(0, 0, 90) : new v3())),
				fuss_lot);

		OGL.verschieb(lFoot.getMittelPkt().negiere());
		if (kalibrate_state == 0 || kalibrate_state >= 5 || !AnimFenster.cb_rechtesbein.isSelected())
			lFoot.render();
		OGL.verschieb(lFoot.getMittelPkt());

		rotate3AxisBack(winkelset.getLFoot().add(
				(kalibrate_state == 6 && AnimFenster.cb_rechtesbein.isSelected() ? new v3(0, 0, 90) : new v3())),
				fuss_lot);

		OGL.verschieb(fuss_velschieb.negiere());
		OGL.verschieb(hydla_velschieb.negiere());
		rotate3AxisBack(winkelset.getLUnter().add(
				(kalibrate_state == 4 && AnimFenster.cb_rechtesbein.isSelected() ? new v3(0, 0, 90) : new v3())),
				untel_lot);
		OGL.verschieb(untel_velschieb.negiere());

		rotate3AxisBack(winkelset.getLOber().add(
				(kalibrate_state == 2 && AnimFenster.cb_rechtesbein.isSelected() ? new v3(0, 0, 90) : new v3())),
				obel_lot);

		OGL.verschieb(obel_velschieb.negiere());

		OGL.verschieb(beinvelschieb.negiere());
		// lechtes bein feltig

	}

	private void tick() {
		if (animation && animate_ziel != null) {
			setWinkelset(getWinkelset().minus(delta.teilenauf(-1)));
			animation_count--;
			animation = animation_count > 0;
		}

	}

	/**
	 * Simple getter.
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
	 * Animate to a certain <code>WinkelSet</code>.<br>
	 * The animation is divided into 100 steps.
	 * 
	 * @param winkelSet
	 *            The <code>WinkelSet</code> to animate to.
	 */
	public void animateTo(WinkelSet winkelSet) {
		animateTo(winkelSet, 100);
	}

	/**
	 * Animate to a certain <code>WinkelSet</code>.<br>
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
	public void setTorso(Object3D torso) {
		this.torso = torso;
	}

	/**
	 * Simple getter.
	 * 
	 * @return
	 */
	public Object3D getTorso() {
		return torso;
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
	 * Checks whether the mecha collides with the given point.
	 * 
	 * @param pos
	 *            A point.
	 * @return Does it collide?
	 */
	public boolean checkCollisionforObjekt(Vektor3D pos) {

		boolean result = false;

		result = Geschoss.checkCollisionforObjektAsEllipse(weapon1.getDimension(), new v3().add(tor_ver)
				.add(huftenhohe), pos, torso.getMittelPkt());
		if (result)
			return result;
		result = Geschoss.checkCollisionforObjektAsEllipse(torso.getDimension(), new v3().add(tor_ver).add(huftenhohe),
				pos, torso.getMittelPkt());
		if (result)
			return result;
		result = Geschoss.checkCollisionforObjektAsEllipse(hufte.getDimension(), new v3().add(huftenhohe), pos, hufte
				.getMittelPkt());
		if (result)
			return result;
		result = Geschoss.checkCollisionforObjektAsEllipse(rOber.getDimension(), new v3().add(huftenhohe).add(
				beinverschieb).add(verschiebWS.getROber()), pos, rOber.getMittelPkt());

		return result;
	}
}
