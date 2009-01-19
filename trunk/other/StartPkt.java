package other;

import java.io.IOException;

import main.Editor;
import main.SuperMain;
import Classes.OGL;
import Classes.Objekt;
import Classes.Texture;
import Classes.TextureLoader;
import Classes.Vektor3D;
import Classes.myColor;
import Classes.v3;

/**
 * Point where the Player starts in each level
 * @author paule
 *
 */
public class StartPkt implements Objekt {
	Vektor3D position;
	Texture tex;
	int id;

	int blick_richtg = 0;

	/**
	 * Constructor
	 */
	public StartPkt() {
		TextureLoader loader = SuperMain.loader;
		this.id = SuperMain.genId();
		try {
			tex = loader.getTexture("img/test.bmp");
		} catch (IOException e) {
			e.printStackTrace();SuperMain.out(e);
		}
		position = new Vektor3D(0, 0, 0);
		blick_richtg = 0;
	}

	public int hurt(int by) {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean checkCollisionforObjekt(Vektor3D pos) {
		// TODO Auto-generated method stub
		return false;
	}

	public Vektor3D getDimension() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setPositionDirectly(Vektor3D v) {
		this.position = v;

	}

	public String getDescription() {

		return "Legt den Startpkt des Spielers fest";
	}

	public int getOptionCount() {
		// TODO Auto-generated method stub
		return 1;
	}

	public String getOptionDescription(int i) {

		switch (i) {
		case 0:
			return "Blickwinkel";

		default:
			break;
		}
		return null;
	}

	public int getOptionType(int i) {
		switch (i) {
		case 0:
			return INT;

		default:
			break;
		}
		return 0;
	}

	public Vektor3D getPosition() {
		// TODO Auto-generated method stub
		return position;
	}

	public Object getOptionValue(int i) {
		switch (i) {
		case 0:
			return blick_richtg;

		default:
			break;
		}
		return 0;
	}

	public void setOptionValue(int i, Object value) {
		if (i == 0) {
			blick_richtg = (Integer) value;
		}

	}

	public void setPosition(Vektor3D v) {
		v3 neu = new v3(v.getX1(), SuperMain.level.getHeight(v), v.getX3());
		this.position = neu;

	}

	public String toString() {
		return "StartPkt";
	}

	public void logic() {
		// TODO Auto-generated method stub

	}

	public void render() {

		if (Editor.isActualGameState()) {
			OGL.verschieb(position);
			OGL.rot(blick_richtg, new v3(0, 1, 0));

			Classes.OGL.wuerfel(new v3(-1.5, 0, -1.5), new Vektor3D(3, 3, 3), tex, id);
			OGL.rot(-blick_richtg, new v3(0, 1, 0));
			OGL.verschieb(position.negiere());
		}

		OGL.setColor(myColor.WHITE);
	}

	public boolean isInWertebereich(int i, Object value) {
		if (i == 0) {
			int blick = (Integer) value;
			return (blick >= 0 && blick <= 360);
		}
		return false;
	}

	public int getID() {
		// TODO Auto-generated method stub
		return id;
	}

	/**
	 * Numerate the starting points if there are more than one starting point.
	 * @return
	 */
	public int getReihenfolgePlatz() {
		int i = 0;
		for (Objekt o : SuperMain.level.objekte) {
			if (o.getID() == this.getID())
				return i;
			if (o instanceof StartPkt)
				i++;
		}
		return -1;
	}

}
