package monster;

import java.io.IOException;

import main.SuperMain;
import Classes.Geschoss;
import Classes.OGL;
import Classes.Object3D;
import Classes.Objekt;
import Classes.Texture;
import Classes.Vektor3D;
import Classes.v3;

/**
 * SchiessWand Monster
 * @author ladanz
 *
 */
public class SchiessWand implements Objekt {

	int id;
	Vektor3D pos;
	Texture scheibe;
	Object3D scheibe3d;

	/**
	 * Constructor
	 */
	public SchiessWand() {
		id = SuperMain.genId();
		pos = new v3(0, 1, 0);
		scheibe3d = new Object3D("models/more_models/pig.obj",
				"img/Zielscheibe.jpg");// "images/marble.jpg"

		try {
			scheibe = SuperMain.loadTex("img/Zielscheibe.jpg");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();SuperMain.out(e);
		}
	}

	public int hurt(int by) {
		return 0;

	}

	public boolean checkCollisionforObjekt(Vektor3D pos) {
		return Geschoss.checkCollisionforObjektAsEllipse(this, pos);
	}

	public Vektor3D getDimension() {

		return scheibe3d.getDimension();
	}

	public void logic() {
		// TODO Auto-generated method stub

	}

	public void render() {
		OGL.verschieb(pos);
		scheibe3d.render(id);
		OGL.verschieb(pos.negiere());
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return "zum einschieen";
	}

	public int getID() {
		// TODO Auto-generated method stub
		return id;
	}

	public int getOptionCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getOptionDescription(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getOptionType(int i) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Object getOptionValue(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	public Vektor3D getPosition() {
		// TODO Auto-generated method stub
		return pos;
	}

	public boolean isInWertebereich(int i, Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	public void setOptionValue(int i, Object value) {
		// TODO Auto-generated method stub

	}

	public void setPosition(Vektor3D v) {
		pos = new v3(v.getX1(), 1, v.getX3());
		System.out.println("Pig dimension: " + scheibe3d.getDimension());
	}

	public void setPositionDirectly(Vektor3D v) {
		pos = new v3(v);

	}

}
