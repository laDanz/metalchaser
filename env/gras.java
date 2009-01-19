// DELETE
package env;

import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;

import main.SuperMain;
import Classes.OGL;
import Classes.Objekt;
import Classes.Texture;
import Classes.Vektor3D;
import Classes.myColor;
import Classes.v3;
/**
 * 
 * @author ladanz
 * @deprecated viel zu unperformant!
 */
public class gras implements Objekt {
	int id;
	// option 3
	double hoehe = 0.3;
	// option 2
	double groesse = 1;
	// option 1
	double dichte = 0.5;
	Vektor3D position;

	boolean rendered = false;
	int list;

	Texture ico;
	LinkedList<Grashalm> halme;

	/**
	 * environment is gras
	 */
	public gras() {
		id = SuperMain.genId();
		position = new v3();

		calcHalme();
		try {
			ico = SuperMain.loadTex("img/test.bmp");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();SuperMain.out(e);
		}
	}

	public boolean checkCollisionforObjekt(Vektor3D pos) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * 
	 */
	public Vektor3D getDimension() {
		// TODO Auto-generated method stub
		return null;
	}

	public int hurt(int by) {
		// TODO Auto-generated method stub
return 0;
	}

	private void calcHalme() {
		rendered = false;
		halme = new LinkedList<Grashalm>();
		for (double x = position.getX1() - groesse; x < position.getX1()
				+ groesse; x += 0.05) {
			for (double y = position.getX3() - groesse; y < position.getX3()
					+ groesse; y += 0.05) {
				if (Math.random() + dichte >= 1) {
					Grashalm halm = new Grashalm(new v3(x, SuperMain.level
							.getHeight(x, y), y), Math.random() * hoehe, id);
					halme.add(halm);
				}
			}
		}
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return "Wuestengraeser";
	}

	public int getID() {
		// TODO Auto-generated method stub
		return id;
	}

	public int getOptionCount() {
		// TODO Auto-generated method stub
		return 3;
	}

	public String getOptionDescription(int i) {
		switch (i) {
		case 2:
			return "Hoehe";
		case 1:
			return "Groesse";
		case 0:
			return "Dichte";
		default:
			return null;
		}
	}

	public int getOptionType(int i) {
		switch (i) {
		case 2:
			return Objekt.DOUBLE;
		case 1:
			return Objekt.DOUBLE;
		case 0:
			return Objekt.DOUBLE;
		default:
			return 0;
		}
	}

	public Object getOptionValue(int i) {
		switch (i) {
		case 2:
			return hoehe;
		case 1:
			return groesse;
		case 0:
			return dichte;
		default:
			return null;
		}
	}

	public Vektor3D getPosition() {

		return position;
	}

	public boolean isInWertebereich(int i, Object value) {
		switch (i) {
		case 2:
			double d = (Double) value;
			return (d > 0 && d < 100);
		case 1:
			d = (Double) value;
			return (d > 0 && d < 100);
		case 0:
			d = (Double) value;
			return (d > 0 && d <= 1);

		default:
			return false;
		}
	}

	public void logic() {
		// wehende grser ?

	}

	public void render() {
		if (rendered) {
			OGL.calllist(list);
		} else {
			if (!main.Editor.isActualGameState()) {
				rendered = true;

				list = OGL.startlist();
			}
			// grser rendern
			if (main.Editor.isActualGameState()) {
				OGL.wuerfel(position, new v3(0.5, 0.5, 0.5), ico, id);
			}
			try {
				for (Grashalm g : halme) {
					g.render();
				}
			} catch (ConcurrentModificationException e) {
				// denn nich!
				// gras halme neu berechnen
			}
			if (!main.Editor.isActualGameState()) {
				OGL.endlist();
				halme = null;
				System.gc();
			}
		}

		OGL.setColor(myColor.WHITE);

	}

	public void setOptionValue(int i, Object value) {
		switch (i) {
		case 2:

			hoehe = (Double) value;
			calcHalme();
			break;
		case 1:
			groesse = (Double) value;
			calcHalme();
			break;
		case 0:
			dichte = (Double) value;
			calcHalme();
			break;
		default:
			return;
		}
	}

	public void setPosition(Vektor3D v) {
		position = new v3(v.getX1(), SuperMain.level.getHeight(v), v.getX3());
		calcHalme();
	}

	public void setPositionDirectly(Vektor3D v) {
		position = new v3(v);
		calcHalme();
	}

}

class Grashalm {
	Vektor3D pos;
	double hoehe;
	LinkedList<Vektor3D> halm;
	int id;

	public Grashalm(Vektor3D pos, double hoehe, int id) {
		this.pos = pos;
		this.hoehe = hoehe;
		this.id = id;
		calcHalm();
	}

	private void calcHalm() {
		halm = new LinkedList<Vektor3D>();
		double dh = 0.02;
		double d = 0.01;
		for (double hohecount = 0; hohecount < hoehe; hohecount += dh) {
			Vektor3D to = (new v3(Math.random() * d, dh, Math.random() * d));
			halm.add(to);
		}

	}

	public void render() {
		// OGL.verschieb(pos);
		Vektor3D old = new v3(pos);
		for (Vektor3D v : halm) {
			OGL.setColor(myColor.GREEN);
			OGL.line(1, old, old.add(v), id);
			old = old.add(v);
		}
		// OGL.verschieb(pos.negiere());

	}
}
