package Classes;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex3f;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;

import main.Editor;
import main.SuperMain;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Renderable;

import Fenster.GelaendeFenster;

/**
 * Square which builds the ground of each level.
 * 
 * @author laDanz
 * 
 */
public class quad implements Renderable {
	Vektor3D ul;
	Vektor3D ur;
	Vektor3D ol;
	Vektor3D or;
	int tex;

	Mantel mantel = null;

	Vektor3D farbe = myColor.BLUE;
	static int ecke_or = 1;
	static int ecke_ol = 2;
	static int ecke_ur = 4;
	static int ecke_ul = 8;
	static int ecke_all = 15;
	// fr begrenzer, falls schon angefasst wurde
	boolean isdone = false;
	private boolean button2wasdown = false;
	private int splittcount = 0;

	quad parent;
	int id;

	Vektor2D texCoord, texCoord2;

	/**
	 * Constructor.<br>
	 * Creates a square with a dimension of 0.
	 */
	public quad() {
		this(new Vektor3D(0, 0, 0), new Vektor3D(0, 0, 0));
	}

	/**
	 * Creates a square.
	 * 
	 * @param pos
	 *            Position of a corner of the <code>quad</code>.
	 * @param dim
	 *            Stretch vector of the <code>quad</code>.
	 */
	public quad(Vektor3D pos, Vektor3D dim) {

		this(pos, pos.add(new Vektor3D(dim.getX1(), 0, 0)), pos.add(new Vektor3D(dim.getX1(), 0, dim.getX3())), pos
				.add(new Vektor3D(0, 0, dim.getX3())));
	}

	/**
	 * Creates a square.
	 * 
	 * @param ul
	 *            Lower left corner.
	 * @param ur
	 *            Lower right corner.
	 * @param or
	 *            Upper right corner.
	 * @param ol
	 *            Upper left corner.
	 */
	public quad(Vektor3D ul, Vektor3D ur, Vektor3D or, Vektor3D ol) {
		if (Editor.tohigher == null)
			Editor.tohigher = new HashMap<String, Vector<Double>>();

		this.id = SuperMain.genId();

		this.parent = null;
		this.tex = -1;
		this.ul = new Vektor3D(ul);
		this.ur = new Vektor3D(ur);
		this.ol = new Vektor3D(ol);
		this.or = new Vektor3D(or);

		texCoord = new Vektor2D(0, 0);
		texCoord2 = new Vektor2D(1, 1);

	}

	/**
	 * Simple Getter.
	 * 
	 * @return The texture identifier.
	 */
	public int getTex() {
		return tex;
	}

	/**
	 * Simple Setter.
	 * 
	 * @param tex
	 *            A texture identifier.
	 */
	public void setTex(int tex) {
		this.tex = tex;
	}

	/**
	 * Calculates the center of the <code>quad</code>.
	 * 
	 * @return The position vector of the center.
	 */
	public Vektor3D getMittelPkt() {
		double newx = (getMaxX() - getMinX()) / 2 + getMinX();
		double newz = (getMaxZ() - getMinZ()) / 2 + getMinZ();

		v3 res = new v3(newx, getHeight(newx, newz), newz);

		return res;
	}

	/**
	 * Do some calculations like higher or lower the <code>quad</code>.<br>
	 * Only called by the editor.
	 */
	public void logic() {
		int max = (int) Math.max(ul.getX2(), Math.max(ur.getX2(), Math.max(or.getX2(), ol.getX2())));

		double peek = max / 10.;

		Vektor3D farbe = new Vektor3D(Math.max(0, -peek), Math.max(0, peek), 0);
		this.farbe = myColor.BLUE.add(farbe);

		if (SuperMain.selection == this.id || (this.parent != null && this.parent.id == SuperMain.selection)
				|| (this.parent != null && this.parent.mantel.containsID(SuperMain.selection))
				|| (this.mantel != null && this.mantel.containsID(SuperMain.selection))) {

			if (Editor.putter != null) {
				Editor.putter.setPosition(getMittelPkt());
				GelaendeFenster.postObjektData(Editor.putter);
			}
			if (Editor.draggin) {
				Editor.selectedObjekt.setPosition(getMittelPkt());
				GelaendeFenster.postObjektData(Editor.selectedObjekt);
			}
			this.farbe = (myColor.RED);

			// first mouse button--> higher
			if (Mouse.isButtonDown(0) && !isdone) {
				if (Editor.begrenzer && GelaendeFenster.click_move.isSelected()) {
					isdone = true;
					if (this.mantel != null) {
						this.mantel.setAllDone();

					}
					if (this.parent != null) {
						this.parent.isdone = true;
					}
				}
				if (button2wasdown) {
					splittcount++;
					System.out.println("splitt by " + splittcount);
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_CAPITAL)) {
					if (GelaendeFenster.click_move.isSelected() && !button2wasdown) {

						higher(quad.ecke_all, -0.3);
						SuperMain.level.doRebuild();
					}

				} else {
					if (GelaendeFenster.click_move.isSelected() && !button2wasdown) {
						higher(quad.ecke_all, 0.3);
						SuperMain.level.doRebuild();
					}
					if (GelaendeFenster.click_tex.isSelected() && SuperMain.selection == this.id) {
						this.tex = (GelaendeFenster.tex_area.getSelectedRow());
						SuperMain.level.doRebuild();
					}
				}
			}
			if (Mouse.isButtonDown(1) && !isdone) {
				if (!button2wasdown)
					splittcount = 1;
				button2wasdown = true;

				if (GelaendeFenster.click_tex.isSelected() && SuperMain.selection == this.id) {
					this.tex = -1;

				}
			}
			if (!Mouse.isButtonDown(1)) {
				if (button2wasdown && GelaendeFenster.click_move.isSelected()) {
					split(splittcount);
				}
				button2wasdown = false;
			}

		}
		if (!Mouse.isButtonDown(0)) {
			isdone = false;
		}
		// muss ich ne eke erhhen???
		if (Editor.tohigher.size() > 0)
			for (Vektor3D v : new Vektor3D[] { ol, or, ur, ul }) {
				String key = v.toString();
				if (Editor.tohigher.containsKey(key)) {
					Double value = Editor.tohigher.get(key).firstElement();
					Double ur_groesse = Editor.tohigher.get(key).lastElement();
					if (Math.abs(ur_groesse) < Math.abs((getMaxX() - getMinX()))) {
						// urspruengliche war kleiner -> mich splitten
						System.out.println(Math.abs(ur_groesse) + " kleiner als " + Math.abs((getMaxX() - getMinX()))
								+ " also splitten");
						split(1);
						// und aussteigen
						continue;
					}

					Vektor3D neu = v.add(new Vektor3D(0, (value == null ? 0 : value), 0));
					v.setX1(neu.getX1());
					v.setX2(neu.getX2());
					v.setX3(neu.getX3());

					// tohigher.remove(key);
					// nicht ntig, wird durch mouserelease gelst!
				}
			}

	}

	/**
	 * Simple Getter.
	 * 
	 */
	public Vektor2D getTexCoord() {
		return texCoord;
	}

	/**
	 * Simple Setter.
	 * 
	 */
	public void setTexCoord(Vektor2D texCoord) {
		this.texCoord = texCoord;
	}

	/**
	 * Simple Getter.
	 * 
	 */
	public Vektor2D getTexCoord2() {
		return texCoord2;
	}

	/**
	 * Simple Setter.
	 * 
	 */
	public void setTexCoord2(Vektor2D texCoord2) {
		this.texCoord2 = texCoord2;
	}

	/**
	 * Splits the <code>quad</code> in new <code>quad</code>s. <br>
	 * Recursive use when i>1;
	 * 
	 * @param i
	 *            How often should be split.
	 */
	// TODO mantel richtig splitten....
	public void split(int i) {
		if (i == 0)
			return;
		i--;
		// Altes loeschen
		quad altes = this;
		SuperMain.level.quads.remove(altes);
		Vektor3D mitPkt = getMittelPkt();
		Vektor3D um = ul.add(ur.mal(-1)).mal(0.5).add(ur);
		Vektor3D lm = ul.add(ol.mal(-1)).mal(0.5).add(ol);
		Vektor3D om = ol.add(or.mal(-1)).mal(0.5).add(or);
		Vektor3D rm = ur.add(or.mal(-1)).mal(0.5).add(or);
		quad neu1 = new quad(ul, um, mitPkt, lm);
		neu1.setTex(getTex());
		// tex teilen
		Vektor2D streck = altes.getTexCoord2().add(altes.texCoord.mal(-1));
		neu1.setTexCoords(altes.getTexCoord(), altes.getTexCoord().add(streck.mal(0.5)));
		quad neu2 = new quad(mitPkt, rm, or, om);
		neu2.setTex(getTex());

		// tex teilen
		neu2.setTexCoords(altes.getTexCoord().add(streck.mal(new Vektor2D(0.5, 0.5))), altes.getTexCoord2());
		quad neu3 = new quad(um, ur, rm, mitPkt);
		neu3.setTex(getTex());
		// tex teilen
		neu3.setTexCoords(altes.getTexCoord().add(streck.mal(new Vektor2D(0.5, 0))), altes.getTexCoord().add(
				streck.mal(new Vektor2D(0.5, 0))).add(streck.mal(0.5)));
		quad neu4 = new quad(lm, mitPkt, om, ol);
		neu4.setTex(getTex());
		// tex teilen
		neu4.setTexCoords(altes.getTexCoord().add(streck.mal(new Vektor2D(0, 0.5))), altes.getTexCoord().add(
				streck.mal(new Vektor2D(0, 0.5))).add(streck.mal(0.5)));
		SuperMain.level.quads.add(neu1);
		neu1.split(i);
		SuperMain.level.quads.add(neu2);
		neu2.split(i);
		SuperMain.level.quads.add(neu3);
		neu3.split(i);
		SuperMain.level.quads.add(neu4);
		neu4.split(i);
		if (altes.mantel != null) {
			neu1.higher_alone(quad.ecke_all, 0.);
			neu2.higher_alone(quad.ecke_all, 0.);
			neu3.higher_alone(quad.ecke_all, 0.);
			neu4.higher_alone(quad.ecke_all, 0.);
		}

	}

	private void setTexCoords(Vektor2D vektor2D, Vektor2D vektor2D2) {
		texCoord = vektor2D;
		texCoord2 = vektor2D2;
	}

	/**
	 * Das quad drehen. I-mal. im Uhrzeigersinn.
	 * 
	 * @param i
	 *            wie oft gedreht wird.
	 */
	private void dreh(int i) {
		if (i == 0)
			return;
		Vektor3D oldul = ul;
		ul = ol;
		ol = or;
		or = ur;
		ur = oldul;
		dreh(--i);

	}

	private void spiegeln() {
		Vektor3D alto = or;
		Vektor3D altu = ur;
		or = ol;
		ur = ul;
		ol = alto;
		ul = altu;
	}

	/**
	 * Renders the quad.
	 */
	public void render() {
		render(false);
	}

	/**
	 * Renders the quad.
	 * 
	 * @param headless
	 *            Should the OpenGL GL_BEGIN block be neglected?
	 */
	public void render(boolean headless) {
		if (Editor.isActualGameState())
			logic();

		if (!headless) {
			OGL.setColor(myColor.WHITE);
			if (SuperMain.farbe)
				OGL.setColor(farbe);
		}
		OGL.viereck_freeform(ul, ur, or, ol, (SuperMain.texture_anzeigen ? (tex < 0
				|| tex > SuperMain.level.tex.size() - 1 ? null : SuperMain.level.tex.get(tex)) : null), texCoord,
				texCoord2, id, 1, headless);

		if (SuperMain.gitter) {
			// GL11.glDisable(GL11.GL_LIGHTING);
			OGL.setColor(myColor.GREEN);
			Vektor3D up = new Vektor3D(0, 0.01, 0);
			OGL.line(1, ul.add(up), ur.add(up));

			OGL.line(1, ur.add(up), or.add(up));
			// GL11.glEnable(GL11.GL_LIGHTING);
		}
		if (mantel != null) {
			mantel.render(headless);
		}

	}

	/**
	 * Tries to render a shadow for the quad.<br>
	 * <b>buggy.</b>
	 */
	public void renderSchatten() {
		// if(true)
		// return;
		float x, z, y;
		OGL.setColor(myColor.BLACK);

		glDisable(GL_TEXTURE_2D);
		// GL11.glEnable(GL11.GL_BLEND);
		// GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		glDisable(GL11.GL_LIGHTING);
		// OGL.disableDephTest();
		glColor4f((float) 0, (float) 0, (float) 0, (float) 0.5);
		glBegin(GL11.GL_QUADS);
		int wink = 0;

		for (Vektor3D v : new Vektor3D[] { ul, ur, or, ol }) {
			x = (float) (v.getX1() + v.getX2() * Math.cos(Math.toRadians(wink)));
			z = (float) (v.getX3() + v.getX2() * Math.sin(Math.toRadians(wink)));
			y = (float) Math.min(SuperMain.level.getHeight(x, z) + 0.021, v.getX2()) - 0.02f;

			// y=0;
			glVertex3f(x, y, z);
		}

		glEnd();
		// OGL.enableDephTest();
		glEnable(GL_TEXTURE_2D);
		glEnable(GL11.GL_LIGHTING);
		glDisable(GL11.GL_BLEND);

	}

	/**
	 * Calculates the height for a given point on this quad.
	 * 
	 * @param x
	 *            The x coordinate of the point.
	 * @param z
	 *            The z coordinate of the point.
	 * @return The height as a <code>float</code>.
	 */
	public Float getHeight(double x, double z) {
		Float res = new Float(0);
		if (getMaxY() == getMinY())
			return new Float(getMaxY());
		// sind 2 gleich???
		if (ol.getX2() == or.getX2() && ul.getX2() == ur.getX2()) {
			double d = ol.getX2() + (ul.getX2() - ol.getX2()) * ((z - ol.getX3()) / (ul.getX3() - ol.getX3()));
			return new Float(Math.min(getMaxY(), Math.max(getMinY(), d)));
		}
		if (ol.getX2() == ul.getX2() && or.getX2() == ur.getX2()) {
			double d = ul.getX2() + (ur.getX2() - ul.getX2()) * ((x - ul.getX1()) / (ur.getX1() - ul.getX1()));
			return new Float(Math.min(getMaxY(), Math.max(getMinY(), d)));
		}

		// auf welchem halbdreieck bin ich
		Vektor3D e3;
		if (Math.abs(z - ul.getX3()) > Math.abs(x - ul.getX1())) {
			e3 = ol;
		} else {
			e3 = ur;
		}
		return new Float(getDurchstpktH(new Vektor3D(x, 0, z), new Vektor3D(x, 1, z), ul, or, e3));
	}

	/**
	 * A <b>buggy</b> version of height calculating.
	 * 
	 * @param x
	 * @param z
	 * @return
	 */
	public Float getHeight2(double x, double z) {
		Float height;

		// berechnen der Ebenen aufspannenden Vektoren v1, v2
		Vektor3D v1 = ur.sub(ul); // Vektor in X-Richtung
		Vektor3D v2 = ol.sub(ul); // Vektor in Z-Richtung

		// berechnen der skalare mit denen der Punkt (x,y) erreicht wird auf der
		// Ebene
		double p = (x - ul.getX1()) / v1.getX1();
		double q = (z - ul.getX3()) / v2.getX3();

		height = new Float((p * v1.getX2()) + (q * v2.getX2()) + ul.getX2());

		return height;
	}

	/**
	 * A method which calculates a break through point of a line through a
	 * plane.<br>
	 * Is used for height calculations.
	 * 
	 * @param g1
	 *            One point of the line.
	 * @param g2
	 *            Another point of the line.
	 * @param e1
	 *            One point of the plane.
	 * @param e2
	 *            A second point of the plane.
	 * @param e3
	 *            A third point of the plane.
	 */
	public static double getDurchstpktH(Vektor3D g1, Vektor3D g2, Vektor3D e1, Vektor3D e2, Vektor3D e3) {
		// Fr den Durchstopunkt berechnet man erst die Parameter darstellung
		// Gerade x=g1+r*v;
		// Ebene x=e1+s*w+t*u
		// Dann muss man die gleichsetzten
		// g1+r*v = e1+s*w+t*u
		// und man erhlt den durchstopkt
		// die zeilen vektoren reprsentieren g11-e11, s koeffizienten, t
		// koeeffizienten
		Double r = null, s = null, t = null;
		Vektor3D zeile1 = new Vektor3D(g1.getX1() - e1.getX1(), -e3.getX1() + e1.getX1(), -e2.getX1() + e1.getX1());// =0
		Vektor3D zeile3 = new Vektor3D(g1.getX3() - e1.getX3(), -e3.getX3() + e1.getX3(), -e2.getX3() + e1.getX3());// =0
		boolean dev = false;
		if (dev)
			System.out.println("erste zeile: " + zeile1);
		if (dev)
			System.out.println("dritte zeile: " + zeile3);
		// wenn einer der s/t koeffizienten 0 ist, dann hab ich schon gewonnen
		if (zeile1.getX2() == 0 && zeile1.getX3() != 0) {
			t = new Double(-zeile1.getX1() / zeile1.getX3());
			if (dev)
				System.out.println("T ist " + t);
		}
		if (zeile3.getX2() == 0 && zeile3.getX3() != 0) {
			t = new Double(-zeile3.getX1() / zeile3.getX3());
			if (dev)
				System.out.println("T ist " + t);
		}
		if (zeile1.getX3() == 0 && zeile1.getX2() != 0) {
			s = new Double(-zeile1.getX1() / zeile1.getX2());
			if (dev)
				System.out.println("s ist " + s);
		}
		if (zeile3.getX3() == 0 && zeile3.getX2() != 0) {
			s = new Double(-zeile3.getX1() / zeile3.getX2());
			if (dev)
				System.out.println("s ist " + s);
		}
		// hat das nicht geklappt --> Addition
		if (t == null && s == null) {
			// ich versuche s zu eleminieren
			Vektor3D zeile11 = zeile1.mal(1 / zeile1.getX2());
			Vektor3D zeile31 = zeile3.mal(-1 / zeile3.getX2());
			if (dev)
				System.out.println("neue zeile 1 " + zeile11);
			if (dev)
				System.out.println("neue zeile 3 " + zeile31);
			Vektor3D zeile111 = zeile11.add(zeile31);
			if (dev)
				System.out.println("zusammen: " + zeile111);
			// daraus t
			t = new Double(-zeile111.getX1() / zeile111.getX3());
			if (dev)
				System.out.println("t ist " + t);
			// daraus s
			s = new Double(-(zeile11.getX1() + zeile11.getX3() * t));
			if (dev)
				System.out.println("s ist " + s);
		}
		if (t == null) {

			if (zeile3.getX3() == 0) {
				t = new Double((zeile1.getX1() + zeile1.getX2() * s) / zeile1.getX3());
			} else {
				t = new Double((zeile3.getX1() + zeile3.getX2() * s) / zeile3.getX3());
			}
			if (dev)
				System.out.println("t ist " + t);
		}
		if (s == null) {
			if (zeile1.getX2() == 0) {
				s = new Double((zeile3.getX1() + zeile3.getX3() * t) / -zeile3.getX2());
			} else {
				s = new Double((zeile1.getX1() + zeile1.getX3() * t) / -zeile1.getX2());
			}

			if (dev)
				System.out.println("s ist " + s);
		}
		// nur noch r
		r = new Double(e1.getX2() - g1.getX2() + s * (e3.getX2() - e1.getX2()) + t * (e2.getX2() - e1.getX2()));
		if (dev)
			System.out.println("r ist " + r);

		Vektor3D d = new Vektor3D(g1.getX1(), g1.getX2() + r, g1.getX3());
		if (dev)
			System.out.println("Durchstopkt: " + d);
		return d.getX2();
	}

	/**
	 * Simple Getter.
	 * 
	 * @return
	 */
	public double getMinY() {
		return Math.min(ol.getX2(), Math.min(or.getX2(), Math.min(ul.getX2(), ur.getX2())));
	}

	/**
	 * Simple Getter.
	 * 
	 * @return
	 */
	public double getMaxY() {
		return Math.max(ol.getX2(), Math.max(or.getX2(), Math.max(ul.getX2(), ur.getX2())));
	}

	/**
	 * Simple Getter.
	 * 
	 * @return
	 */
	public double getMinX() {
		return Math.min(ol.getX1(), Math.min(or.getX1(), Math.min(ul.getX1(), ur.getX1())));
	}

	/**
	 * Simple Getter.
	 * 
	 * @return
	 */
	public double getMaxX() {
		return Math.max(ol.getX1(), Math.max(or.getX1(), Math.max(ul.getX1(), ur.getX1())));
	}

	/**
	 * Simple Getter.
	 * 
	 * @return
	 */
	public double getMinZ() {
		return Math.min(ol.getX3(), Math.min(or.getX3(), Math.min(ul.getX3(), ur.getX3())));
	}

	/**
	 * Simple Getter.
	 * 
	 * @return
	 */
	public double getMaxZ() {
		return Math.max(ol.getX3(), Math.max(or.getX3(), Math.max(ul.getX3(), ur.getX3())));
	}

	void higher_alone(int ecken, double by) {
		if (this.parent != null) {
			this.parent.higher_alone(ecken, by);
			return;
		}

		Vektor3D anderung = new Vektor3D(0, by, 0);

		if (ecken >= 8) {

			ul = ul.add(anderung);
			ecken = ecken - 8;
		}
		if (ecken >= 4) {

			ur = ur.add(anderung);
			ecken = ecken - 4;
		}
		if (ecken >= 2) {

			ol = ol.add(anderung);
			ecken = ecken - 2;
		}
		if (ecken == 1) {

			or = or.add(anderung);

		}

		mantel = new Mantel(this);
		if (Editor.begrenzer) {
			isdone = true;
			mantel.setAllDone();
		}
	}

	private void higher(int ecken, double by) {
		if (!GelaendeFenster.cb_andereMitZiehen.isSelected()) {
			higher_alone(ecken, by);
			return;
		}
		// Achtung ringsrum alle gleiche groesse wie ich??

		Vektor3D anderung = new Vektor3D(0, by, 0);
		Vector<Double> put;
		put = new Vector<Double>();
		put.add(by);
		put.add(Math.abs(getMaxX() - getMinX()));
		if (ecken >= 8) {
			Editor.tohigher.put(ul.toString(), put);
			ul = ul.add(anderung);
			ecken = ecken - 8;
		}
		if (ecken >= 4) {
			Editor.tohigher.put(ur.toString(), put);
			ur = ur.add(anderung);
			ecken = ecken - 4;
		}
		if (ecken >= 2) {
			Editor.tohigher.put(ol.toString(), put);
			ol = ol.add(anderung);
			ecken = ecken - 2;
		}
		if (ecken == 1) {
			Editor.tohigher.put(or.toString(), put);
			or = or.add(anderung);

		}

	}

	/**
	 * Save this <code>quad</code> to a file.
	 * 
	 * @param bwriter
	 *            A BufferedWriter from a file.
	 * @throws IOException
	 */
	public void save(BufferedWriter bwriter) throws IOException {
		bwriter.write("<quad>");
		bwriter.newLine();
		bwriter.write("<ol>");
		bwriter.write(this.ol.toString());
		bwriter.write("</ol>");
		bwriter.newLine();
		bwriter.write("<ul>");
		bwriter.write(this.ul.toString());
		bwriter.write("</ul>");
		bwriter.newLine();
		bwriter.write("<or>");
		bwriter.write(this.or.toString());
		bwriter.write("</or>");
		bwriter.newLine();
		bwriter.write("<ur>");
		bwriter.write(this.ur.toString());
		bwriter.write("</ur>");
		bwriter.newLine();
		bwriter.write("<tex>");
		bwriter.write(tex + "");
		bwriter.write("</tex>");
		bwriter.newLine();

		bwriter.write("<TexCoord1>");
		bwriter.write(getTexCoord().toString());
		bwriter.write("</TexCoord1>");
		bwriter.newLine();
		bwriter.write("<TexCoord2>");
		bwriter.write(getTexCoord2().toString());
		bwriter.write("</TexCoord2>");
		bwriter.newLine();

		bwriter.write("</quad>");
		bwriter.newLine();
		if (this.mantel != null) {
			bwriter.write("<MANTEL>");
			this.mantel.save(bwriter);
			bwriter.write("</MANTEL>");
		}

	}

	public void renderOnly() {
		OGL.viereck_freeform(ul, ur, or, ol, (SuperMain.texture_anzeigen ? (tex < 0
				|| tex > SuperMain.level.tex.size() - 1 ? null : SuperMain.level.tex.get(tex)) : null), texCoord,
				texCoord2, id, 1, true);
	}

	public static Comparator<? super quad> TextureComparator() {

		return new Comparator<quad>() {
			//@override
			public int compare(quad o1, quad o2) {

				return o1.getTex() - o2.getTex();
			}
		};
	}

}

/**
 * A help class for <code>quad</code>s.
 * 
 * @author ladanz
 * 
 */
class Mantel {
	quad h;
	quad l;
	quad v;
	quad r;

	public Mantel(quad q) {

		Vektor3D von = q.ol;
		Vektor3D nach = q.or;
		h = new quad(new Vektor3D(von.getX1(), 0, von.getX3()), new Vektor3D(nach.getX1(), 0, nach.getX3()),
				new Vektor3D(nach.getX1(), nach.getX2(), nach.getX3()), new Vektor3D(von.getX1(), von.getX2(), von
						.getX3()));
		h.setTex(q.tex);
		von = q.or;
		nach = q.ur;
		l = new quad(new Vektor3D(von.getX1(), 0, von.getX3()), new Vektor3D(nach.getX1(), 0, nach.getX3()),
				new Vektor3D(nach.getX1(), nach.getX2(), nach.getX3()), new Vektor3D(von.getX1(), von.getX2(), von
						.getX3()));
		l.setTex(q.tex);
		von = q.ur;
		nach = q.ul;
		v = new quad(new Vektor3D(von.getX1(), 0, von.getX3()), new Vektor3D(nach.getX1(), 0, nach.getX3()),
				new Vektor3D(nach.getX1(), nach.getX2(), nach.getX3()), new Vektor3D(von.getX1(), von.getX2(), von
						.getX3()));
		v.setTex(q.tex);
		von = q.ul;
		nach = q.ol;
		r = new quad(new Vektor3D(von.getX1(), 0, von.getX3()), new Vektor3D(nach.getX1(), 0, nach.getX3()),
				new Vektor3D(nach.getX1(), nach.getX2(), nach.getX3()), new Vektor3D(von.getX1(), von.getX2(), von
						.getX3()));
		r.setTex(q.tex);
		for (quad q_ : new quad[] { h, v, l, r }) {
			q_.parent = q;

		}

	}

	public void setAllDone() {
		for (quad q_ : new quad[] { h, v, l, r }) {
			q_.isdone = true;

		}
	}

	public boolean containsID(int selection) {
		int[] ids = { h.id, v.id, r.id, l.id };
		for (int i : ids) {
			if (i == selection)
				return true;
		}
		return false;
	}

	public void save(BufferedWriter f1) throws IOException {
		h.save(f1);
		v.save(f1);
		l.save(f1);
		r.save(f1);

	}

	public void render() {
		render(false);
	}

	public void render(boolean headless) {
		h.render(headless);
		v.render(headless);
		r.render(headless);
		l.render(headless);

	}
}
