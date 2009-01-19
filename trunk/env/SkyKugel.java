package env;

import java.io.File;
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
 * Implementation of bowl shaped SkyBox
 * 
 * @author paule
 * 
 */
public class SkyKugel implements Objekt {

	// option 1
	int radius = 100;
	// option 2
	boolean show = true;
	// option 3
	File theme;

	Vektor3D position;
	Texture ico, kugel;
	// Texture forward,left,right,back,up;

	int id;

	/**
	 * Constructor
	 */
	public SkyKugel() {
		id = SuperMain.genId();
		position = new v3(-1, 1, 0);

		SuperMain.toRun.add(new Runnable() {
			public void run() {
				try {
					ico = SuperMain.loadTex("img/test.bmp");

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					SuperMain.out(e);
				}

			}
		});
		loadTheme();
	}

	/**
	 * Load the actual theme
	 */
	public void loadTheme() {
		SuperMain.toRun.add(new Runnable() {
			public void run() {
				try {

					if (theme != null)
						kugel = SuperMain.loadTex(theme.getAbsolutePath());
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
		return "SkyKugel";
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
		return 3;
	}

	public String getOptionDescription(int i) {
		switch (i) {
		case 0:
			return "Radius";
		case 1:
			return "Show";
		case 2:
			return "File";

		default:
			return null;
		}
	}

	public int getOptionType(int i) {
		switch (i) {
		case 0:
			return Objekt.INT;
		case 1:
			return Objekt.BOOL;
		case 2:
			return Objekt.FILE;

		default:
			return 0;
		}
	}

	public Object getOptionValue(int i) {
		switch (i) {
		case 0:
			return radius;
		case 1:
			return show;
		case 2:
			return theme;

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

			return (true);
		case 1:

			return (true);
		case 2:
			return ((File) value).exists();

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
		if (show || !Editor.isActualGameState()) {
			// Kugel????
			OGL.kugel(new v3(SuperMain.level.width / 2, SuperMain.level.width / 2, -SuperMain.level.depth / 2), -radius, kugel, 1, 32, getID());

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
			radius = j;
			break;
		case 1:
			show = ((Boolean) value);
			break;
		case 2:
			if (value instanceof String) {
				theme = new File((String) value);
			} else {
				theme = new File(((File) value).getAbsolutePath());
			}
			loadTheme();
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
