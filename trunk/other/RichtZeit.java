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
 * Defines a Indicator-Time of each level
 * @author ladanz
 *
 */
public class RichtZeit implements Objekt {

	int id;
	Vektor3D position;
	Texture tex;
	// Option 1
	int richtzeit;

	/**
	 * Constructor
	 */
	public RichtZeit() {
		id = SuperMain.genId();
		position = new v3();
		richtzeit = 0;

		TextureLoader loader = SuperMain.loader;
		try {
			tex = loader.getTexture("img/test.bmp");
		} catch (IOException e) {
			e.printStackTrace();SuperMain.out(e);
		}

	}

	/**
	 * Simple Getter
	 * @return
	 */
	public int getRichtzeit() {
		return richtzeit;
	}

	/**
	 * Simple Getter
	 * @return
	 */
	public int getId() {
		return id;
	}

	//@override
	public boolean checkCollisionforObjekt(Vektor3D pos) {

		return false;
	}

	//@override
	public String getDescription() {
		return "Bestimmt die Richtzeit des levels, in Sekunden.";
	}

	//@override
	public Vektor3D getDimension() {
		// TODO Auto-generated method stub
		return null;
	}

	//@override
	public int getID() {
		// TODO Auto-generated method stub
		return id;
	}

	//@override
	public int getOptionCount() {
		// TODO Auto-generated method stub
		return 1;
	}

	//@override
	public String getOptionDescription(int i) {
		switch (i) {
		case 0:
			return "RichtZeit";

		default:
			return "fehler";
		}
	}

	//@override
	public int getOptionType(int i) {
		switch (i) {
		case 0:
			return Objekt.INT;

		default:
			return 0;
		}
	}

	//@override
	public Object getOptionValue(int i) {
		switch (i) {
		case 0:
			return richtzeit;

		default:
			return null;
		}
	}

	//@override
	public Vektor3D getPosition() {
		return position;
	}

	//@override
	public int hurt(int by) {
		// TODO Auto-generated method stub
		return 0;
	}

	//@override
	public boolean isInWertebereich(int i, Object value) {

		return true;
	}

	//@override
	public void logic() {

	}

	//@override
	public void render() {
		if (Editor.isActualGameState()) {
			OGL.verschieb(position);
			Classes.OGL.wuerfel(new v3(-1.5, 0, -1.5), new Vektor3D(3, 3, 3), tex, id);
			OGL.verschieb(position.negiere());
		}

		OGL.setColor(myColor.WHITE);
	}

	//@override
	public void setOptionValue(int i, Object value) {
		switch (i) {
		case 0:
			richtzeit = (Integer) value;
			break;

		default:
			break;
		}
	}

	//@override
	public void setPosition(Vektor3D v) {
		position = new v3(v.getX1(), SuperMain.level.getHeight(v), v.getX3());
	}

	//@override
	public void setPositionDirectly(Vektor3D v) {
		position = new v3(v);
	}

	//@override
	public String toString() {
		return "LevelRichtZeitObjekt";
	}
}
