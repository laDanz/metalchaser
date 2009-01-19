package drops;

import java.io.Serializable;

import main.SuperMain;
import Classes.OGL;
import Classes.Object3D;
import Classes.Objekt;
import Classes.OpenAlClip;
import Classes.Vektor3D;
import Classes.v3;

/**
 * Superclass for Enemydrops
 * 
 * @author ladanz
 * 
 */
public class SchrottDrop implements Objekt, Serializable {

	Vektor3D position;
	int id;
	static Object3D batterie = null, schlauch = null, munition = null, metaltrash = null;
	OpenAlClip pickup;

	/**
	 * Item drops on ground
	 * 
	 * @param old
	 *            object that drops the item
	 */
	public SchrottDrop(Objekt old) {
		position = old.getPosition();
		id = SuperMain.genId();
		initModel();
		if (pickup == null)
			pickup = new OpenAlClip("sound/pickup.ogg");

	}

	//@override
	public boolean equals(Object obj) {
		if (obj instanceof SchrottDrop) {
			SchrottDrop new_name = (SchrottDrop) obj;
			return new_name.id == this.id;

		} else
			return false;

	}

	/**
	 * Item drops on the ground
	 * 
	 * @param pos
	 *            3D Position on which the item drops
	 */
	public SchrottDrop(Vektor3D pos) {
		position = pos;
		id = SuperMain.genId();
		initModel();

	}

	/**
	 * generating the model of the drop
	 */
	public static void initModel() {
		if (batterie == null) {
			batterie = new Object3D("models/data1/000.mco", "img/loot/batterie.png");
			schlauch = new Object3D("models/data1/003.mco", "img/loot/schlauch.png");
			metaltrash = new Object3D("models/data1/001.mco", "img/loot/metaltrash.png");
			munition = new Object3D("models/data1/002.mco", "img/loot/munition.png");
		}

	}

	/**
	 * checks if Object collides with another object
	 */
	public boolean checkCollisionforObjekt(Vektor3D pos) {
		return false;
	}

	public String getDescription() {
		return null;
	}

	public Vektor3D getDimension() {
		return null;
	}

	public int getID() {
		return id;
	}

	public int getOptionCount() {
		return 0;
	}

	public String getOptionDescription(int i) {
		return null;
	}

	public int getOptionType(int i) {
		return 0;
	}

	public Object getOptionValue(int i) {
		return null;
	}

	public Vektor3D getPosition() {
		return position;
	}

	public int hurt(int by) {
		return 0;
	}

	public boolean isInWertebereich(int i, Object value) {
		return false;
	}

	public void logic() {

	}

	static double scale = 0.15;
	static Vektor3D skal = new v3(scale, scale, scale);

	public void render() {

		OGL.verschieb(position);
		OGL.skaliere(skal);
		if (batterie != null) {
			/*
			 * OGL.verschieb(new v3(0,-batterie.getDimension().getX2()/2,0));
			 * OGL.rot(-90, v3.x_axis); OGL.verschieb(new
			 * v3(0,0,model.getDimension().getX1()/3)); model.render();
			 * OGL.verschieb(new
			 * v3(0,0,model.getDimension().getX1()/3).negiere()); OGL.rot(90,
			 * v3.x_axis); OGL.verschieb(new
			 * v3(0,-model.getDimension().getX2()/2,0).negiere());
			 */
			batterie.render();
			schlauch.render();
			munition.render();
			metaltrash.render();

		}
		OGL.skaliere(skal.reziproke());
		OGL.verschieb(position.negiere());
	}

	public void setOptionValue(int i, Object value) {

	}

	public void setPosition(Vektor3D v) {

	}

	public void setPositionDirectly(Vektor3D v) {

	}

}
