package Classes;

import main.SuperMain;

/**
 * Class for creating our new awesome crossair.
 * 
 * @author PoggeDeluxe
 * 
 */
public class Zielkreuz implements OGLable {

	Vektor3D color = new Vektor3D(1, 0, 0);

	public Zielkreuz() {
	}

	public void logic() {
	}

	public void once() {
	}

	/**
	 * Default render method. Renders fully defalted crossair.
	 */
	public void render() {
		render(1);
	}

	/**
	 * Simple Setter.
	 * 
	 * @param c
	 */
	public void setColor(v3 c) {
		color = new v3(c);
	}

	/**
	 * Simple Getter.
	 * 
	 * @return
	 */
	public Vektor3D getColor() {
		return color;
	}

	/**
	 * Setter to change the color of the crossair.
	 * 
	 * @param c1
	 *            Color before change.
	 * @param c2
	 *            Color o be changed to.
	 * @param falt
	 *            Degree of change. (0 = 0%, 1 = 100%)
	 */
	public void setFarbverlauf(Vektor3D c1, Vektor3D c2, double falt) {
		color = (c1.mal(falt)).add(c2.mal(1 - falt));
	}

	double cm = SuperMain.cm;
	v3 verschieb = new v3(10.5 * cm, 7.55 * cm, 0);
	v3 v1 = new v3(-2 * cm, -1 * cm, 0), v2 = new v3(0, 2.5 * cm, 0), v3 = new v3(2 * cm, -1 * cm, 0);
	v3 scale = new v3(1. / 3, 1. / 3, 1. / 3);

	/**
	 * Renders the crossair.
	 * 
	 * @param falt
	 *            Explains how much crossair is defalted (1 = 100%, 0 = 0%).
	 * 
	 */
	public void render(double falt) {
		double i;
		i = 150 * falt;

		OGL.setColor(color);

		OGL.verschieb(verschieb);
		OGL.skaliere(scale);
		OGL.line(2, new Vektor3D(0, -0.2 * cm, 0), new Vektor3D(0, 0.2 * cm, 0), 0);
		OGL.line(2, new Vektor3D(-0.2 * cm, 0, 0), new Vektor3D(0.2 * cm, 0, 0), 0);

		OGL.verschieb(v1);
		OGL.rot(i, v3.z_axis);
		OGL.line(2, new Vektor3D(0, 0, 0), new Vektor3D(2 * cm, 3.5 * cm, 0), 0);
		OGL.rot(-i, v3.z_axis);
		OGL.verschieb(v1.negiere());

		OGL.verschieb(v2);
		OGL.rot(i, v3.z_axis);
		OGL.line(2, new Vektor3D(0, 0, 0), new Vektor3D(2 * cm, -3.5 * cm, 0), 0);
		OGL.rot(-i, v3.z_axis);
		OGL.verschieb(v2.negiere());

		OGL.verschieb(v3);
		OGL.rot(i, v3.z_axis);
		OGL.line(2, new Vektor3D(0, 0, 0), new Vektor3D(-4 * cm, 0, 0), 0);
		OGL.rot(-i, v3.z_axis);
		OGL.verschieb(v3.negiere());
		OGL.skaliere(scale.reziproke());
		OGL.verschieb(verschieb.negiere());
		OGL.setColor(myColor.WHITE);
	}
}
