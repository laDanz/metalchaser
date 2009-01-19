package Classes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import main.Editor;
import main.LevelPlay;
import main.SuperMain;

import org.lwjgl.opengl.GL11;

import other.LevelInfo;
import other.RichtZeit;
import sock.Data;
import Fenster.GelaendeFenster;
import drops.SchrottDrop;
import env.SkyKugel;

/**
 * Class for level representation and methods.
 * 
 * @author laDanz
 * 
 */

public class Level implements Serializable {

	private static final long serialVersionUID = 1L;

	private static Vektor3D selectedposition;

	LinkedList<quad> quads;
	public LinkedList<Objekt> objekte;
	public LinkedList<Texture> tex = new LinkedList<Texture>();
	public LinkedList<String> texS = new LinkedList<String>();
	public double width;
	public double depth;

	int list = 0;

	public Float[][] height = null;
	double fac = 1;
	double facz = 1;

	boolean rebuild = true;
	private String filename;

	/**
	 * Initialize an empty level.
	 */
	public Level() {
		quads = new LinkedList<quad>();
		objekte = new LinkedList<Objekt>();
		width = 0;
		depth = 0;
		rebuild = true;
		list = 0;
	}

	/**
	 * Creates a level with a given <code>quad</code> structure.
	 * 
	 * @param quads
	 *            A set of <code>quad</code>s.
	 */
	public Level(LinkedList<quad> quads) {
		this();
		this.addQuads(quads);
	}

	/**
	 * Simple Getter.
	 * 
	 * @return
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * Searches the level objects for <code>LevelInfo</code> objects and
	 * returns their information text.
	 * 
	 * @return level info text.
	 */
	public String getLevelInfo() {
		String res = "";
		for (Objekt o : objekte) {
			if (o instanceof LevelInfo) {
				res = ((LevelInfo) o).getText();
				return res;
			}
		}
		return res;
	}

	/**
	 * Adopts the attributes from the given level.
	 * 
	 * @param l
	 *            Another <code>Level</code>
	 */
	public void copy(Level l) {
		quads = l.getQuads();
		objekte = l.getObjekte();
		this.width = l.width;
		this.depth = l.depth;
		this.filename = l.filename;
		if (l.height == null) {

			l.calc_height();
		}
		height = new Float[1002][1002];
		for (int x = 0; x < 1002; x++) {
			for (int y = 0; y < 1002; y++) {
				height[x][y] = l.height[x][y];
			}
		}
		fac = l.fac;
		facz = l.facz;
		rebuild = true;
	}

	/**
	 * Returns all objects from this level.
	 * 
	 * @return All objects from this Level.
	 */
	public LinkedList<Objekt> getObjekte() {

		return objekte;
	}

	/**
	 * Calculates a height array for the level.
	 */
	public void calc_height() {
		// Hhen vorberechnen
		height = new Float[1002][1002];

		// Auflsung: Weite(Feld)/Anzahl(Speicherpltze)
		fac = width / 1000.;
		facz = Math.abs(depth) / 1000.;
		for (quad q : quads) {
			double minx = Math.ceil(q.getMinX() / fac) * fac;
			double maxx = q.getMaxX();
			double minz = Math.ceil(q.getMinZ() / facz) * facz;
			double maxz = q.getMaxZ();

			for (double i = minx; i <= (maxx + fac / 2); i += fac / 2) {
				for (double j = minz; j <= (maxz + facz / 2); j += facz / 2) {
					int posx = (int) (i / fac);
					int posy = -((int) (j / facz));
					int d;
					if (posx == 119)
						d = 3 * 4;
					Float valu = q.getHeight(i, j);
					if (!valu.isNaN() && (height[posx][posy] == null || (valu > height[posx][posy]))) {
						height[posx][posy] = valu;
					}
				}
			}

		}

	}

	/**
	 * Simple Setter.<br>
	 * Rebuilds the height array.
	 * 
	 * @param quads
	 */
	public void setQuads(LinkedList<quad> quads) {
		this.quads = quads;
		rebuild = true;
	}

	/**
	 * Get the height for a certain x/z point.
	 * 
	 * @param v
	 *            A point.
	 * @return The height at this point as a <code>double</code>.
	 */
	public double getHeight(Vektor3D v) {
		return getHeight(v.getX1(), v.getX3());
	}

	/**
	 * Get the height for a certain x/z point.
	 * 
	 * @param x
	 *            X coordinate of the point.
	 * @param z
	 *            Z coordinate of the point.
	 * @return The height at this point as a <code>double</code>.
	 */
	public double getHeight(double x, double z) {
		if (height == null) {
			calc_height();
		}
		z = Math.abs(z);
		Float d = new Float(0);
		try {
			d = height[(int) (x / fac)][(int) (z / facz)];
		} catch (IndexOutOfBoundsException e) {
			SuperMain.out("DeadLock@ (" + Math.round(x * 100) / 100 + "," + Math.round(z * 100) / 100 + "), ["
					+ (int) (x / fac) + "," + (int) (z / facz) + "]");
			d = new Float(0);
		}
		// FIXME wenn d null is is scheie, warum wirts berhaupt null
		return (d == null ? 0 : d);
	}

	/**
	 * Add some <code>quad</code>s to this level.
	 * 
	 * @param quads
	 *            A set of <code>quad</code>s.
	 */
	public void addQuads(LinkedList<quad> quads) {
		this.quads.addAll(quads);
		rebuild = true;
	}

	/**
	 * Add one <code>quad</code> to this level.
	 * 
	 * @param quad_
	 *            A <code>quad</code>.
	 */
	public void addQuad(quad quad_) {
		this.quads.add(quad_);
		rebuild = true;
	}

	/**
	 * Write this level to a file.
	 * 
	 * @param filename
	 *            The destination filename.
	 */
	public void save(String filename) {
		try {
			FileWriter fw = new FileWriter(filename);
			BufferedWriter f1 = new BufferedWriter(fw);
			f1.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			f1.newLine();
			f1.write("<Cleaners>");
			f1.newLine();

			for (String s : texS) {
				f1.write("<texture>");
				f1.write(s);
				f1.write("</texture>");
				f1.newLine();
			}

			for (quad q : quads) {
				q.save(f1);

			}
			for (Objekt o : objekte) {
				f1.write("<Objekt>");
				f1.newLine();
				f1.write("<Klasse>");
				f1.write("class " + o.getClass().getName());
				f1.write("</Klasse>");
				f1.newLine();
				f1.write("<Position>");
				f1.write(o.getPosition().toString());
				f1.write("</Position>");
				f1.newLine();
				for (int i = 0; i < o.getOptionCount(); i++) {
					f1.write("<option>");
					try {
						f1.write("" + o.getOptionValue(i).toString().replace('\\', '/'));
					} catch (NullPointerException e) {
						f1.write("null");
					}

					f1.write("</option>");
					f1.newLine();

				}
				f1.write("</Objekt>");
				f1.newLine();
			}

			f1.write("</Cleaners>");
			f1.close();
		} catch (IOException e) {
			SuperMain.out("Fehler beim Level speichern: " + e);

		}
		org.lwjgl.Sys.alert("Speichern", "Erfolgreich!");
	}

	/**
	 * Simple Getter.
	 * 
	 * @return
	 */
	public LinkedList<quad> getQuads() {
		return quads;
	}

	/**
	 * Loads a level from a file.
	 * 
	 * @param filename
	 *            The source filename.
	 */
	public void load(String filename) {

		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = null;
		this.filename = filename;
		try {
			saxParser = factory.newSAXParser();
		} catch (Exception e) {
			SuperMain.out("XML-Init Fehler beim Laden im Modul LEVEL: " + e);
			throw new RuntimeException("XML-Init Fehler beim Laden im Modul LEVEL: " + e);
		}

		Levelhandler handler = new Levelhandler(this);
		try {
			saxParser.parse(new File(filename), handler);

			if (this == null)
				throw new Exception("Datei ist leer/wird nicht unterstzt");

			Level l;
			l = handler.getLevel();
			l.filename = filename;
			this.copy(l);

		} catch (Exception e) {
			SuperMain.out("Fehler beim Laden im Modul LEVEL: " + e);
			e.printStackTrace();
			SuperMain.out(e);

		}
		return;
	}

	/**
	 * Causes the rebuild of the height array.
	 */
	public void doRebuild() {
		rebuild = true;

	}

	/**
	 * Renders this level.
	 */
	public void render() {
		render(false);
		// list=0;
	}

	/**
	 * Renders the level.
	 * 
	 * @param uselist
	 *            Should a render-list be used? <b>buggy</b>
	 */
	public void render(boolean uselist) {
		boolean editor = Editor.isActualGameState();

		if (uselist) {
			if (list == 0) {
				// Laden.addText("smoothe Level");
				// smoothen();
				list = OGL.startlist();

				// Array nach texturen sortieren
				quad[] quad_ = quads.toArray(new quad[0]);
				Arrays.sort(quad_, quad.TextureComparator());
				int last_texture = -1;

				for (quad q : quad_) {
					if (last_texture != q.getTex()) {
						if (last_texture != -1) {
							GL11.glEnd();
						}
						last_texture = q.getTex();
						SuperMain.level.tex.get(last_texture).bind();
						GL11.glBegin(GL11.GL_TRIANGLES);
					}

					q.renderOnly();

				}
				GL11.glEnd();

				if (SuperMain.schatten)
					for (quad q : quads.toArray(new quad[0])) {

						q.renderSchatten();

					}

				OGL.endlist();
			} else {
				OGL.calllist(list);

			}
		} else {
			for (quad q : quads.toArray(new quad[0])) {

				q.render();
			}
			if (SuperMain.schatten)
				for (quad q : quads.toArray(new quad[0])) {

					q.renderSchatten();

				}

		}

		if (editor) {
			for (quad q : quads.toArray(new quad[0])) {

				q.logic();
			}
		}

		OGL.setColor(myColor.WHITE);
		int selection = LevelPlay.selection;
		selectedposition = null;
		boolean skykugel_selected = false;
		for (Objekt o : objekte.toArray(new Objekt[0])) {
			if (selection == o.getID()) {
				double y = 0;
				try {
					y = o.getDimension().getX2() / 2;
				} catch (NullPointerException e) {
					y = 1;
				}
				if (o.getPosition() != null && o instanceof monster.master)
					selectedposition = o.getPosition().add(new v3(0, y, 0));
				if (o instanceof SkyKugel)
					skykugel_selected = true;
			}
			if (skykugel_selected) {
				selectedposition = null;
			}

			if (editor) {
				if (main.Editor.MouseOver.contains(o.getID())) {
					OGL.setColor(myColor.RED);
				}
				if (main.Editor.MouseClick.contains(o.getID())) {

					GelaendeFenster.postObjektData(o);
					Editor.selectedObjekt = o;
					main.Editor.draggin = true;
				}
				if (Editor.selectedObjekt == o)
					OGL.setColor(myColor.RED);
			} else {
				// selektiert?
				// nur wenn nicht client oder noch nicht zielerreicht
				// aber drops beim client schon!
				if ((LevelPlay.client == null && !LevelPlay.ziel_erreicht)
						|| ((o instanceof SchrottDrop) && LevelPlay.client != null && !LevelPlay.ziel_erreicht))
					o.logic();
				if (LevelPlay.client != null) {
					Data data = LevelPlay.client.receivedData.get(o.getID());
					if (data != null) {

						o.setPositionDirectly(data.pos);
						if (o instanceof RotateAble)
							((RotateAble) o).setDrehwinkel((int) data.drehwinkel);
						if (o instanceof monster.master)
							((monster.master) o).Health = data.life;
					}
				}
			}

			o.render();
			OGL.setColor(myColor.WHITE);
		}
		if (LevelPlay.client != null) {
			LevelPlay.client.receivedData.clear();
		}
		main.Editor.MouseOver.clear();
		main.Editor.MouseClick.clear();
	}

	/**
	 * Adds a texture to this level.
	 * 
	 * @param string
	 *            The filename of the texture.
	 * @param tex_
	 *            The texture itself.
	 */
	public void addTexture(String string, Texture tex_) {

		if (tex_ != null) {
			tex.add(tex_);
			texS.add(string);
		}
	}

	/**
	 * Adds an object to this level.
	 * 
	 * @param o
	 *            A object.
	 */
	public void addObjekt(Objekt o) {
		objekte.add(o);

	}

	/**
	 * Removes an object from this level.
	 * 
	 * @param selectedObjekt
	 *            A object.
	 */
	public void removeObj(Objekt selectedObjekt) {
		objekte.remove(selectedObjekt);

	}

	/**
	 * Checks whether a given point is into the level bounds.
	 * 
	 * @param pos
	 *            A point.
	 * @return true if the point is out of bounds.
	 */
	public boolean isOutta(Vektor3D pos) {
		double x = pos.getX1();
		double y = (pos.getX3());
		return (x < 0 || y > 0 || x > width || -y > Math.abs(depth));
	}

	/**
	 * Scans for the smallest <code>quad</code> and splits all other quads
	 * until they all have the same size.
	 * 
	 */
	public void smoothen() {
		// kleinstes quad ermitteln
		double kleinstes = Double.MAX_VALUE;
		for (quad q : quads) {
			double size = Math.abs(q.getMaxX() - q.getMinX());
			if (size < kleinstes)
				kleinstes = size;
		}
		boolean splittet_some = false;
		// alle sooft splitten bis groesse des kleinsten quads
		for (quad q : quads.toArray(new quad[0])) {
			double size = Math.abs(q.getMaxX() - q.getMinX());
			int fac = (int) (Math.log(size / kleinstes) / Math.log(2));
			if (fac > 1) {
				q.split(1);
				splittet_some = true;
			}

		}
		if (splittet_some)
			smoothen();

	}

	/**
	 * Returns the target time for this level in seconds.<br>
	 * Can be provided by a special object.
	 * 
	 * @return time in seconds.
	 */
	public int getRichtZeit() {
		int res = 0;
		for (Objekt o : objekte) {
			if (o instanceof RichtZeit) {
				res = ((RichtZeit) o).getRichtzeit();
				return res;
			}
		}
		return res;
	}

	public static Vektor3D getSelectedPosition() {
		// TODO Auto-generated method stub
		return selectedposition;
	}
}
