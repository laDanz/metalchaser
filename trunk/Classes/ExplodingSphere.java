package Classes;

import java.io.IOException;

import main.SuperMain;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Sphere;

public class ExplodingSphere implements Objekt {

	Vektor3D dimension, position, start_dimension;
	int id;
	static Texture tex;
	Sphere s;
	int duration_fps;
	int time_alive;
	double scalar;

	public ExplodingSphere(Vektor3D starting_size, Vektor3D position, int duration_seconds, double size_factor) {
		id = SuperMain.genId();
		dimension = starting_size;
		start_dimension = dimension;
		this.position = position;
		scalar = size_factor;
		duration_fps = (int) (duration_seconds * 60 * OGL.fps_anpassung);
		time_alive = 0;
		s = new Sphere();
		s.setTextureFlag(true);
		if (tex == null) {
			SuperMain.toRun.add(new Runnable() {
				@Override
				public void run() {
					try {
						tex = SuperMain.loadTex(SuperMain.ordner + "img/env/explosion.png");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});
		}
	}

	@Override
	public void logic() {
		time_alive++;
		dimension = start_dimension.mal(1 + ((time_alive + .0) / duration_fps) * scalar);
		if (time_alive > duration_fps) {
			SuperMain.toRun.add(new Runnable() {
				@Override
				public void run() {
					SuperMain.level.removeObj(ExplodingSphere.this);

				}
			});
		}
	}

	@Override
	public void render() {
		if (tex == null)
			return;
		logic();

		OGL.verschieb(position);

		OGL.skaliere(dimension);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		tex.bind();

		GL11.glEnable(GL11.GL_BLEND); // enable transparency

		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(1, 1, 1, 1 - (float) ((time_alive + .0) / duration_fps));

		s.draw(1, 16, 16);
		GL11.glColor4f(1, 1, 1, 1);
		OGL.skaliere(dimension.reziproke());

		OGL.verschieb(position.negiere());
	}

	@Override
	public void setOptionValue(int i, Object value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPosition(Vektor3D v) {
		position = v;

	}

	@Override
	public void setPositionDirectly(Vektor3D v) {
		position = v;

	}

	@Override
	public boolean checkCollisionforObjekt(Vektor3D pos) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vektor3D getDimension() {
		// TODO Auto-generated method stub
		return dimension;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getOptionType(int i) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getOptionValue(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vektor3D getPosition() {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public int hurt(int by) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isInWertebereich(int i, Object value) {
		// TODO Auto-generated method stub
		return false;
	}

}
