package lights;

import static org.lwjgl.opengl.GL11.glLoadName;
import main.Editor;
import main.LevelPlay;
import main.SuperMain;

import org.lwjgl.opengl.GL11;

import Classes.OGL;
import Classes.Objekt;
import Classes.Vektor3D;
import Classes.v3;
import anim.SkelettZ300;

/**
 * Lightsource with fixed position
 * 
 * @author ladanz
 * 
 */
public class PositionalLight implements Objekt, Runnable {
	Vektor3D position;
	int id;
	// option 1
	double hohe;

	private boolean isSetUp = false;
	private int lightid;
	/**
	 * number if lights used:
	 */
	public static int global_light_count = 1;
	private int[] lights = { GL11.GL_LIGHT0, GL11.GL_LIGHT1, GL11.GL_LIGHT2, GL11.GL_LIGHT3, GL11.GL_LIGHT4,
			GL11.GL_LIGHT5, GL11.GL_LIGHT6, GL11.GL_LIGHT7 };

	// color of overall scene lighting
	float ambient[] = { 0f, 0f, 0f, 1f };

	// color of light source
	float lightDiffuse[] = { .9f, .9f, .6f, 1f }; // direct light
	float lightSpecular[] = { .9f, .9f, .6f, 1f }; // highlight
	float lightAmbient[] = { .5f, .0f, .0f, 1f }; // scattered light

	/**
	 * Constructor
	 */
	public PositionalLight() {
		id = SuperMain.genId();
		position = new v3();
		hohe = 5;
		lightid = lights[Math.min(lights.length - 1, global_light_count)];
		System.out.println("LightID: " + lightid);
		global_light_count++;
		if (global_light_count > 8 && Editor.isActualGameState()) {
			org.lwjgl.Sys.alert("Lights", "Du kannst hchstens 8 Lichter haben!");
		}
		SuperMain.toRun.add(this);

	}

	public boolean checkCollisionforObjekt(Vektor3D pos) {
		// TODO Auto-generated method stub
		return false;
	}

	public Vektor3D getDimension() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setUp() {
		LevelPlay.setLight(lightid, lightDiffuse, lightAmbient, lightSpecular, new v3(position), new v3(0, -1, 0), 60);
		isSetUp = true;
	}

	public int hurt(int by) {
		return 0;

	}

	public void run() {
		setUp();
	}

	public String getDescription() {

		return "Der erste Versuch eines Lichtes";
	}

	public int getID() {

		return id;
	}

	public int getOptionCount() {

		return 1;
	}

	public String getOptionDescription(int i) {
		switch (i) {
		case 0:
			return "Hoehe ber dem Boden";

		default:
			return "";
		}
	}

	public int getOptionType(int i) {
		switch (i) {
		case 0:
			return Objekt.DOUBLE;

		default:
			return 0;
		}
	}

	public Object getOptionValue(int i) {
		switch (i) {
		case 0:
			return hohe;

		default:
			return null;
		}
	}

	public Vektor3D getPosition() {
		// TODO Auto-generated method stub
		return position;
	}

	public boolean isInWertebereich(int i, Object value) {
		switch (i) {
		case 0:
			double d = (Double) value;
			return (d >= 0 && d < 100);

		default:
			return false;
		}
	}

	public void logic() {
		// TODO Auto-generated method stub

	}

	public void render() {

		if (Editor.isActualGameState()) {
			glLoadName(id);
			OGL.kugel(position.add(new v3(0, hohe, 0)), 0.5);

			// world drehung rückgängig machen
			OGL.rot(-Editor.rot100, new v3(1, 0, 0));

			LevelPlay.setLightDir(lightid, SkelettZ300.getHeightUnderRot(new v3(0, -1, 0), new v3(Editor.rot100,
					Editor.rot010, 0)));
		}
		if (isSetUp) {
			OGL.verschieb(position);

			LevelPlay.setLightPos(lightid, (float) 0, (float) (hohe), (float) 0);

			OGL.verschieb(position.negiere());

		}
		if (Editor.isActualGameState()) {
			// world drehung rückgängig machen
			OGL.rot(Editor.rot100, new v3(1, 0, 0));
		}

	}

	public void setOptionValue(int i, Object value) {
		switch (i) {
		case 0:
			double d = (Double) value;
			hohe = d;
			break;
		default:
			return;
		}
	}

	public void setPosition(Vektor3D v) {
		this.position = new v3(v.getX1(), SuperMain.level.getHeight(v), v.getX3());
	}

	public void setPositionDirectly(Vektor3D v) {
		this.position = new v3(v);
	}

}
