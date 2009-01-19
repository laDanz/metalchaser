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
 * Masterclasse, which handles Position and ID-Handling<br>
 * Realises also a simple display
 * 
 * @author ladanz
 * 
 */
public class master implements Objekt {

	int id;
	Vektor3D position;
	Texture tex;

	/**
	 * Comstructor
	 */
	public master() {
		id = SuperMain.genId();
		position = new v3();

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
	public int getId() {
		return id;
	}

	@Override
	public boolean checkCollisionforObjekt(Vektor3D pos) {

		return false;
	}

	@Override
	public String getDescription() {
		return "Master";
	}

	@Override
	public Vektor3D getDimension() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	public int getOptionCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getOptionDescription(int i) {
		return "master";
	}

	@Override
	public int getOptionType(int i) {

		return 0;

	}

	@Override
	public Object getOptionValue(int i) {

		return null;

	}

	@Override
	public Vektor3D getPosition() {
		return position;
	}

	@Override
	public int hurt(int by) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isInWertebereich(int i, Object value) {

		return true;
	}

	@Override
	public void logic() {

	}

	@Override
	public void render() {
		if (Editor.isActualGameState()) {
			OGL.verschieb(position);
			Classes.OGL.wuerfel(new v3(-1.5, 0, -1.5), new Vektor3D(3, 3, 3), tex, id);
			OGL.verschieb(position.negiere());
		}

		OGL.setColor(myColor.WHITE);
	}

	@Override
	public void setOptionValue(int i, Object value) {

	}

	@Override
	public void setPosition(Vektor3D v) {
		position = new v3(v.getX1(), SuperMain.level.getHeight(v), v.getX3());
	}

	@Override
	public void setPositionDirectly(Vektor3D v) {
		position = new v3(v);
	}

	@Override
	public String toString() {
		return "other.Master";
	}
}
