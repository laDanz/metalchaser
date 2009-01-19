package LevelZiel;

import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glLoadName;

import java.io.IOException;

import main.Editor;
import main.LevelPlay;
import main.SuperMain;

import org.lwjgl.opengl.GL11;

import Classes.OGL;
import Classes.Objekt;
import Classes.RadarAble;
import Classes.Texture;
import Classes.Vektor3D;
import Classes.myColor;
import Classes.v3;

/**
 * Marks the Target area of every level. If the target area is reached by the
 * player the level is solved successfully
 * 
 * @author ladanz
 * 
 */
public class ZielArea implements Objekt, RadarAble {
	int id;

	Vektor3D position;
	Texture ico;
	private Vektor3D streck = new Vektor3D(1, 1, 1);

	boolean liste = false;
	int list;

	// option 1: gre
	int groesse = 5;
	// option 2: eckig
	boolean eckig = true;
	// option 3:level
	String newlevel = "";
	// option 4:Results Bildschirm?
	boolean show_results;

	private float alpha;

	/**
	 * Constructor
	 */
	public ZielArea() {
		id = SuperMain.genId();
		position = new Vektor3D(0, 0, 0);
		show_results = false;
		try {
			ico = SuperMain.loadTex("img/test.bmp");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			SuperMain.out(e);
		}
		alpha = 1f;
	}

	public boolean checkCollisionforObjekt(Vektor3D pos) {
		// TODO Auto-generated method stub
		return false;
	}

	public int getRadarAppearance() {
		// TODO Auto-generated method stub
		return RadarAble.GOAL;
	}

	public Vektor3D getDimension() {
		// TODO Auto-generated method stub
		return null;
	}

	public int hurt(int by) {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return "Wird ausgelst wenn Player sich darin befindet.";
	}

	public int getID() {
		// TODO Auto-generated method stub
		return id;
	}

	public boolean isInWertebereich(int i, Object value) {
		try {
			switch (i) {
			case 0:
				int in = (Integer) value;
				return (in > 0);
			case 1:
				boolean in_ = (Boolean) value;
				return true;
			case 2:
				String i_ = (String) value;
				return true;
			case 3:
				in_ = (Boolean) value;
				return true;
			default:
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	public int getOptionCount() {
		// TODO Auto-generated method stub
		return 4;
	}

	public String getOptionDescription(int i) {
		String s = "";
		switch (i) {
		case 0:
			s = "Gre";

			break;
		case 1:
			s = "Eckig";

			break;
		case 2:
			s = "Neuer Level";
			break;
		case 3:
			s = "Results zeigen";
			break;

		default:
			break;
		}
		return s;
	}

	public int getOptionType(int i) {
		int type = -1;
		switch (i) {
		case 0:
			type = INT;
			break;
		case 1:
			type = BOOL;
			break;
		case 2:
			type = STRING;
			break;
		case 3:
			type = BOOL;
			break;
		default:
			break;
		}
		return type;
	}

	public Object getOptionValue(int i) {
		switch (i) {
		case 0:
			return groesse;

		case 1:
			return eckig;
		case 2:
			return newlevel.replace('\\', '/');
		case 3:
			return show_results;

		default:
			break;
		}
		return null;
	}

	public void setOptionValue(int i, Object value) {
		switch (i) {
		case 0:
			groesse = (Integer) value;
			break;
		case 1:
			eckig = (Boolean) value;
			break;
		case 2:
			newlevel = (String) value;
			break;
		case 3:
			show_results = (Boolean) value;
			break;

		default:
			break;
		}
	}

	public Vektor3D getPosition() {
		// TODO Auto-generated method stub
		return position;
	}

	public void logic() {

		// wenn der p dinne is denn neues level laden
		Vektor3D ppos = main.LevelPlay.p.getPosition();
		if (!eckig) {
			if (ppos.add(position.mal(-1)).length() < groesse) {
				String s = "LevelPlay," + newlevel;
				/*
				 * if (show_results) { s = "Results," + s; }
				 * main.LevelPlay.setState(s);
				 */
				LevelPlay.ZielErreicht(s);

			}
		} else {
			if (ppos.getX1() > position.getX1() - groesse && ppos.getX1() < position.getX1() + groesse
					&& ppos.getX3() > position.getX3() - groesse && ppos.getX3() < position.getX3() + groesse) {
				String s = "LevelPlay," + newlevel;
				/*
				 * if (show_results) { s = "Results," + s; }
				 * main.LevelPlay.setState(s);
				 */
				LevelPlay.ZielErreicht(s);
			}
		}

		alpha -= 0.05f;
		if (alpha < 0) {
			alpha = 1f;
		}
	}

	public void render() {

		// Wrfel in der Mitte nur im Editor zum schieben und so
		if (Editor.isActualGameState()) {
			OGL.wuerfel(position.add(new v3(-streck.getX1() / 2, 0, -streck.getX3() / 2)), streck, ico, id);

		}

		if (liste) {
			OGL.enableBlend();
			OGL.setAlpha(alpha);
			OGL.calllist(list);
			OGL.setAlpha(1);
			OGL.disableBlend();
		} else {
			if (!Editor.isActualGameState()) {
				liste = true;
				list = OGL.startlist();
			}
			if (eckig) {
				double z1 = position.getX3() + groesse;
				double z2 = position.getX3() - groesse;
				double x1 = position.getX1() + groesse;
				double x2 = position.getX1() - groesse;
				OGL.setColor(myColor.RED);
				glDisable(GL_TEXTURE_2D);
				GL11.glDisable(GL11.GL_LIGHTING);
				glLineWidth((float) 1);
				glLoadName(0);
				glBegin(GL_LINES);
				// Untere/Obere Linie
				for (double x = position.getX1() - groesse; x < position.getX1() + groesse; x += 0.05) {
					v3 start = new v3(x, SuperMain.level.getHeight(x, z1) + 0.01, z1);
					v3 end = new v3(x + 0.1, SuperMain.level.getHeight(x + 0.1, z1) + 0.01, z1);
					OGL.line(1, start, end, 0, true);
					v3 start2 = new v3(x, SuperMain.level.getHeight(x, z2) + 0.01, z2);
					v3 end2 = new v3(x + 0.1, SuperMain.level.getHeight(x + 0.1, z2) + 0.01, z2);
					OGL.line(1, start2, end2, 0, true);
				}
				// Linke/Rechte Linie
				for (double z = position.getX3() - groesse; z < position.getX3() + groesse; z += 0.05) {
					v3 start = new v3(x1, SuperMain.level.getHeight(x1, z) + 0.01, z);
					v3 end = new v3(x1, SuperMain.level.getHeight(x1, z + 0.1) + 0.01, z + 0.1);
					OGL.line(1, start, end, 0, true);
					v3 start2 = new v3(x2, SuperMain.level.getHeight(x2, z) + 0.01, z);
					v3 end2 = new v3(x2, SuperMain.level.getHeight(x2, z + 0.1) + 0.01, z + 0.1);
					OGL.line(1, start2, end2, 0, true);
				}
				GL11.glEnd();
				GL11.glEnable(GL11.GL_LIGHTING);
				OGL.setColor(myColor.WHITE);
			} else {
				OGL.setColor(myColor.RED);
				GL11.glDisable(GL11.GL_LIGHTING);
				// rund
				Vektor3D start = position.add(new v3(groesse, 0, 0));
				Vektor3D end = null;
				glDisable(GL_TEXTURE_2D);
				glLineWidth((float) 1);
				glLoadName(0);
				glBegin(GL_LINES);
				for (int a = 0; a < 360; a++) {
					end = position.add(new v3(groesse * Math.cos(Math.toRadians(a)), 0, groesse
							* Math.sin(Math.toRadians(a))));
					end = end.add(new v3(0, SuperMain.level.getHeight(end), 0));
					OGL.line(1, start, end, 0, true);
					start = end;
				}
				GL11.glEnd();
				GL11.glEnable(GL11.GL_LIGHTING);
				OGL.setColor(myColor.WHITE);
			}
			if (!Editor.isActualGameState()) {
				OGL.endlist();
			}
		}

	}

	public void setPosition(Vektor3D v) {
		this.position = new v3(v.getX1(), SuperMain.level.getHeight(v), v.getX3());

	}

	public void setPositionDirectly(Vektor3D v) {
		position = v;

	}

}
