package anim;

import java.util.LinkedList;

import Classes.OGL;
import Classes.Object3D;
import Classes.Vektor3D;
import Classes.v3;

/**
 * A utility class for the skeletons.<br>
 * Represents one movable part.
 * 
 * @author laDanz
 * 
 */
public class SkelettTeil {
	private LinkedList<SkelettTeil> verbundene;
	private Object3D object;
	private Vektor3D verschieb, rot_punkt, rotation;
	private int id;

	/**
	 * Default constructor.<br>
	 * 
	 * @param object
	 *            An 3D object file.
	 * @param id
	 *            The parent id.
	 */
	public SkelettTeil(Object3D object, int id) {
		this.object = object;
		verschieb = new v3();
		rot_punkt = new v3();
		rotation = new v3();
		verbundene = new LinkedList<SkelettTeil>();
		this.id = id;
	}

	/**
	 * Simple Getter.
	 * 
	 * @return
	 */
	public Vektor3D getRotation() {
		return rotation;
	}

	/**
	 * Simple Setter.
	 * 
	 * @param rotation
	 */
	public void setRotation(Vektor3D rotation) {
		this.rotation = rotation;
	}

	/**
	 * Renders the part.
	 */
	void render() {
		render(rotation);
	}

	/**
	 * Renders the part with a special rotation-angle for each dimension.
	 * 
	 * @param rotation
	 */
	void render(Vektor3D rotation) {
		rotVerbundene();
		this.rotation = rotation;
		OGL.verschieb(verschieb);
		SkelettSentry.rotate3Axis(rotation, rot_punkt);

		OGL.verschieb(object.getMittelPkt().negiere());
		object.render(id);
		OGL.verschieb(object.getMittelPkt());
		SkelettSentry.rotate3AxisBack(rotation, rot_punkt);

		OGL.verschieb(verschieb.negiere());
		rotVerbundeneBack();
	}

	private void rotVerbundeneBack() {
		SkelettTeil[] ver = verbundene.toArray(new SkelettTeil[0]);

		for (int i = ver.length - 1; i >= 0; i--) {
			SkelettSentry.rotate3AxisBack(ver[i].getRotation(), ver[i].getRot_punkt());
		}
	}

	private void rotVerbundene() {
		SkelettTeil[] ver = verbundene.toArray(new SkelettTeil[0]);

		for (int i = 0; i < ver.length; i++) {
			SkelettSentry.rotate3Axis(ver[i].getRotation(), ver[i].getRot_punkt());
		}
	}

	/**
	 * Simple Getter.
	 * 
	 * @return
	 */
	public Object3D getObject() {
		return object;
	}

	/**
	 * Simple Getter.
	 * 
	 * @return
	 */
	public int getId() {
		return id;
	}

	/**
	 * Simple Getter.
	 * 
	 * @return
	 */
	public Vektor3D getVerschieb() {
		return verschieb;
	}

	/**
	 * Simple Setter.
	 * 
	 * @param verschieb
	 */
	public void setVerschieb(Vektor3D verschieb) {
		this.verschieb = verschieb;
	}

	/**
	 * Simple Getter.
	 * 
	 * @return
	 */
	public Vektor3D getRot_punkt() {
		return rot_punkt;
	}

	/**
	 * Simple Setter.
	 * 
	 * @param rot_punkt
	 */
	public void setRot_punkt(Vektor3D rot_punkt) {
		this.rot_punkt = rot_punkt;
	}

	/**
	 * Adds a bounded <code>SkelettTeil</code> to this one.<br>
	 * Bounded parts will rotate together.
	 * 
	 * @param verbund
	 *            The part to bind with.
	 */
	void addVerbundener(SkelettTeil verbund) {
		verbundene.add(verbund);
	}

	/**
	 * Removes a bounded part.
	 * 
	 * @param verbund
	 *            The part to remove.
	 */
	void removeVerbundener(SkelettTeil verbund) {
		verbundene.remove(verbund);
	}

}
