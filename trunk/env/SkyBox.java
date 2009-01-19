package env;

import java.io.IOException;

import main.Editor;
import main.SuperMain;

import org.lwjgl.opengl.GL11;

import Classes.OGL;
import Classes.Objekt;
import Classes.Texture;
import Classes.Vektor3D;
import Classes.v3;
import Fenster.GelaendeFenster;

/**
 * Skybox environment class (box)
 * 
 * @author ladanz
 * 
 */
public class SkyBox implements Objekt {

	// option 1
	int height = 10;
	// option 2
	int abstand = 0;
	// option 3
	boolean show = true;
	// option 4
	boolean cube = true;
	// option 5
	String theme;

	Vektor3D position;
	Texture ico, kugel;
	Texture forward, left, right, back, up;

	int id;

	/**
	 * Constructor for SkyBox
	 */
	public SkyBox() {
		id = SuperMain.genId();
		position = new v3(-1, 1, 0);

		loadTheme("wuste");
		SuperMain.toRun.add(new Runnable() {
			public void run() {
				try {
					ico = SuperMain.loadTex("img/test.bmp");
					kugel = SuperMain.loadTex("img/skybox/alien/alien.jpg");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					SuperMain.out(e);
				}

			}
		});
	}

	/**
	 * Load a Skybox theme
	 * 
	 * @param string
	 *            Path to theme
	 */
	private void loadTheme(String string) {
		theme = string;
		SuperMain.toRun.add(new Runnable() {
			public void run() {
				try {
					forward = SuperMain.loadTex("img/skybox/" + theme + "/forward.jpg");
					up = SuperMain.loadTex("img/skybox/" + theme + "/up.jpg");
					right = SuperMain.loadTex("img/skybox/" + theme + "/right.jpg");
					left = SuperMain.loadTex("img/skybox/" + theme + "/left.jpg");
					back = SuperMain.loadTex("img/skybox/" + theme + "/back.jpg");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					SuperMain.out(e);
				}

			}
		});

	}

	public boolean checkCollisionforObjekt(Vektor3D pos) {
		// TODO Auto-generated method stub
		return false;
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return "Himmel";
	}

	public Vektor3D getDimension() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getID() {
		// TODO Auto-generated method stub
		return id;
	}

	public int getOptionCount() {
		// TODO Auto-generated method stub
		return 4;
	}

	public String getOptionDescription(int i) {
		switch (i) {
		case 0:
			return "Hoehe";
		case 1:
			return "Abstand";
		case 2:
			return "Show";
		case 3:
			return "Cube";

		default:
			return null;
		}
	}

	public int getOptionType(int i) {
		switch (i) {
		case 0:
			return Objekt.INT;
		case 1:
			return Objekt.INT;
		case 2:
			return Objekt.BOOL;
		case 3:
			return Objekt.BOOL;
		default:
			return 0;
		}
	}

	public Object getOptionValue(int i) {
		switch (i) {
		case 0:
			return height;
		case 1:
			return abstand;
		case 2:
			return show;
		case 3:
			return cube;

		default:
			return null;
		}
	}

	public Vektor3D getPosition() {
		// TODO Auto-generated method stub
		return position;
	}

	public int hurt(int by) {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isInWertebereich(int i, Object value) {
		switch (i) {
		case 0:
			int j = (Integer) value;
			return (j > 0);
		case 1:
			j = (Integer) value;
			return (true);
		case 2:
			return true;
		case 3:
			return true;
		default:
			return false;
		}
	}

	public void logic() {
		// TODO Auto-generated method stub

	}

	public void render() {
		if (main.Editor.isActualGameState()) {
			OGL.wuerfel(position, new v3(0.5, 0.5, 0.5), ico, id);
		}
		GL11.glDisable(GL11.GL_LIGHTING);
		if (show && forward != null) {
			if (cube) {
				Vektor3D wo = new v3(+SuperMain.level.width + 1 * abstand, 0, 0 + abstand);
				Vektor3D scale = new v3(-SuperMain.level.width - 2 * abstand, height, 0);
				OGL.viereck(wo, scale, back, id);

				wo = new v3(+SuperMain.level.width + 1 * abstand, height, -SuperMain.level.depth - 1 * abstand);
				scale = new v3(-SuperMain.level.width - 2 * abstand, 0, +SuperMain.level.depth + 2 * abstand);
				OGL.viereck(wo, scale, up, id);

				wo = new v3(0 - abstand, 0, +abstand);
				OGL.verschieb(wo);
				OGL.rot(90, v3.y_axis);
				scale = new v3(SuperMain.level.depth + 2 * abstand, height, 0);
				OGL.viereck(new v3(), scale, left, id);
				OGL.rot(-90, v3.y_axis);
				OGL.verschieb(wo.negiere());

				wo = new v3(0 - abstand, 0, +abstand);
				Vektor3D um = new v3((SuperMain.level.depth) / 2. + abstand, 0, +(SuperMain.level.width) / 2. + abstand);
				OGL.verschieb(wo);
				OGL.rot(90, v3.y_axis);
				OGL.verschieb(um);
				OGL.rot(180, v3.y_axis);
				OGL.verschieb(um.negiere());
				scale = new v3(SuperMain.level.depth + 2 * abstand, height, 0);
				OGL.viereck(new v3(), scale, right, id);
				OGL.verschieb(um);
				OGL.rot(-180, v3.y_axis);
				OGL.verschieb(um.negiere());
				OGL.rot(-90, v3.y_axis);
				OGL.verschieb(wo.negiere());

				wo = new v3(0 - abstand, 0, -SuperMain.level.depth - 1 * abstand);
				scale = new v3(SuperMain.level.width + 2 * abstand, height, 0);
				OGL.viereck(wo, scale, forward, id);

				wo = new v3(SuperMain.level.width + 1 * abstand, 0, 0 + abstand);
				scale = new v3(0, height, -SuperMain.level.depth - 2 * abstand);
				OGL.viereck(wo, scale, right, id);
			} else {
				// Kugel????
				OGL.kugel(new v3(), 150 + abstand, kugel, 1, 32, getID());

			}
		}
		if (!Editor.isActualGameState()) {
			GL11.glEnable(GL11.GL_LIGHTING);
		} else {
			if (GelaendeFenster.cb_light.isSelected()) {
				GL11.glEnable(GL11.GL_LIGHTING);
			}
		}

	}

	public void setOptionValue(int i, Object value) {
		switch (i) {
		case 0:
			int j = (Integer) value;
			height = j;
			break;
		case 1:
			j = (Integer) value;
			abstand = j;
			break;
		case 2:
			show = (Boolean) value;
		case 3:
			cube = (Boolean) value;

		default:
			return;
		}

	}

	public void setPosition(Vektor3D v) {
		position = new v3(v.getX1(), SuperMain.level.getHeight(v), v.getX3());

	}

	public void setPositionDirectly(Vektor3D v) {
		position = new v3(v);

	}

}
