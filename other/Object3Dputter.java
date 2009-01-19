package other;

import java.io.File;
import java.io.IOException;

import main.SuperMain;
import Classes.Geschoss;
import Classes.OGL;
import Classes.Object3D;
import Classes.Objekt;
import Classes.RotateAble;
import Classes.Texture;
import Classes.Vektor3D;
import Classes.v3;

/**
 * Puts 3D Objects into the scene
 * @author paule
 *
 */
public class Object3Dputter implements Objekt, RotateAble {

	/**
	 * No Collision at all for this object.
	 */
	public static final int COLLISION_NONE = 0;
	/**
	 * ellipsoid interpolation
	 */
	public static final int COLLISION_ELLIPSOID = 1;
	/**
	 * simple square (hitbox) collision
	 */
	public static final int COLLISION_SQUARE = 2;

	Texture ico;
	int id;
	// Option 1
	int rotation;
	// Option2
	File objectFile;
	// Option 3
	File texFile;
	// Option 4
	double scale = 1;
	// Option 5
	int kollision_type = COLLISION_NONE;
	Vektor3D position;
	Vektor3D dimension;
	Object3D object = null;

	/**
	 * Constructor
	 */
	public Object3Dputter() {
		id = SuperMain.genId();
		position = new v3();
		texFile = new File(SuperMain.ordner + "img/marble.jpg");
		if (objectFile != null)
			object = new Object3D(objectFile.getAbsolutePath(), texFile.getAbsolutePath());
		try {
			ico = SuperMain.loadTex("img/test.bmp");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();SuperMain.out(e);
		}
	}

	public boolean checkCollisionforObjekt(Vektor3D pos) {
		switch (kollision_type) {
		case 0:
			return false;
		case 1:
			return Geschoss.checkCollisionforObjektAsEllipse(this, pos, object.getMittelPkt());
		case 2:
			return Geschoss.checkCollisionforObjektAsKasten(this, pos, object.getMittelPkt());
		default:
			return false;

		}

	}

	//@override
	public void setDrehwinkel(int dreh) {
		this.rotation = dreh;
	}

	public double getDrehwinkel() {
		// TODO Auto-generated method stub
		return rotation;
	}

	public int hurt(int by) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Vektor3D getDimension() {
		// TODO Auto-generated method stub
		return object.getDimension().mal(scale);
	}

	/**
	 * Simple Getter
	 */
	public String getDescription() {

		return "Um dem Level 3D Objekte hinzuzufgen!";
	}

	public int getID() {

		return id;
	}

	public int getOptionCount() {

		return 5;
	}

	public String getOptionDescription(int i) {
		switch (i) {
		case 0:
			return "Rotation";
		case 1:
			return "ObjectFile";
		case 2:
			return "TextureFile";
		case 3:
			return "Skalierung";
		case 4:
			return "Collision (0=NONE;1=ELLIPS;2=SQUARE)";
		default:
			return null;
		}
	}

	public int getOptionType(int i) {
		switch (i) {
		case 0:
			return Objekt.INT;
		case 1:
			return Objekt.FILE;
		case 2:
			return Objekt.FILE;
		case 3:
			return Objekt.DOUBLE;
		case 4:
			return Objekt.INT;
		default:
			return 0;
		}
	}

	public Object getOptionValue(int i) {
		switch (i) {
		case 0:
			return rotation;
		case 1:
			return objectFile;
		case 2:
			return texFile;
		case 3:
			return scale;
		case 4:
			return kollision_type;
		default:
			return null;
		}
	}

	public Vektor3D getPosition() {

		return position;
	}

	public boolean isInWertebereich(int i, Object value) {
		switch (i) {
		case 0:
			int j = (Integer) value;
			return (j >= 0 && j <= 360);
		case 1:
			File f = (File) value;
			String s = f.getAbsolutePath().substring(SuperMain.ordner.length()).toLowerCase();
			return (f.isDirectory() || (s.startsWith("model") && (s.endsWith("obj") || s.endsWith("3ds"))));
		case 2:
			f = (File) value;
			s = f.getAbsolutePath().substring(SuperMain.ordner.length()).toLowerCase();
			return (f.isDirectory() || (s.startsWith("img") && (s.endsWith("jpg") || s.endsWith("bmp") || s
					.endsWith("png"))));
		case 3:
			double d = (Double) value;
			return (d >= 0 && d <= 360);
		case 4:
			int val = (Integer) value;
			return (val >= 0 && val <= 3);
		default:
			return false;
		}
	}

	public void logic() {

	}

	public void render() {
		if (object != null) {
			OGL.verschieb(position);
			OGL.rot(rotation, new v3(0, 1, 0));
			Vektor3D um = new v3(scale, scale, scale);
			OGL.skaliere(um);
			object.render(id);
			OGL.skaliere(um.reziproke());
			OGL.rot(-rotation, new v3(0, 1, 0));
			OGL.verschieb(position.negiere());
		} else {
			OGL.wuerfel(position, new v3(0.5, 0.5, 0.5), ico, id);

		}

	}

	public void setOptionValue(int i, Object value) {
		switch (i) {
		case 0:
			int j = (Integer) value;
			rotation = j;
			break;
		case 1:
			if (value instanceof String) {
				objectFile = new File((String) value);
			} else {
				objectFile = new File(((File) value).getAbsolutePath());
			}
			object = new Object3D(objectFile.getAbsolutePath(), texFile.getAbsolutePath());
			break;
		case 2:
			if (value instanceof String) {
				texFile = new File((String) value);
			} else {
				texFile = new File(((File) value).getAbsolutePath());
			}

			object = new Object3D(objectFile.getAbsolutePath(), texFile.getAbsolutePath());
			break;
		case 3:
			double d = (Double) value;
			scale = d;
			break;
		case 4:
			int in = (Integer) value;
			kollision_type = in;
			break;
		default:
			break;
		}
	}

	public void setPosition(Vektor3D v) {
		this.position = new Vektor3D(v.getX1(), SuperMain.level.getHeight(v), v.getX3());

	}

	public void setPositionDirectly(Vektor3D v) {
		this.position = new Vektor3D(v);

	}

}
