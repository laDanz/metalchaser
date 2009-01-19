package Classes;

import main.SuperMain;

import org.lwjgl.opengl.GL11;

public class ExplodingParticles implements Objekt {

	particle[] particles;
	// gravity
	static double g = 9.81 / (60 * 600);
	int n = 100;
	int dead;
	Vektor3D color;

	public ExplodingParticles(Vektor3D position) {
		this(position, 100, 1, 1, myColor.BLACK);
	}

	public ExplodingParticles(Vektor3D position, int amount, double velocity_fac, double size_fac, Vektor3D color) {
		dead = 0;
		n = amount;
		this.color = color;
		particles = new particle[n];
		for (int i = 0; i < n; i++) {
			particles[i] = new particle(position, velocity_fac, size_fac);
		}
		// System.out.println("generate particel:" + this);
	}

	public void render() {
		GL11.glLoadName(0);
		for (int i = 0; i < n; i++) {
			particles[i].render();
		}

		if ((dead + 0.) / n > 0.8) {
			SuperMain.toRun.add(new Runnable() {
				//@override
				public void run() {
					SuperMain.level.removeObj(ExplodingParticles.this);
					// System.out.println("remove particel:" +
					// ExplodingParticles.this);
				}
			});
		}
	}

	class particle {
		Vektor3D position, // [m]
				spann, velocity;// [m/s]
		boolean dead;

		public particle(Vektor3D pos, double velocity_factor, double size_factor) {
			position = pos;
			dead = false;
			velocity = new Vektor3D(Math.random() - 0.5, Math.random() * 2, Math.random() - 0.5)
					.mal(velocity_factor * 1.5 / 60.);
			spann = new Vektor3D(Math.random(), Math.random(), Math.random()).mal(size_factor * 0.2);
		}

		void logic() {
			double oldy = velocity.getX2();
			velocity.setX2(oldy - g);
			if (position.getX2() < -1) {
				dead = true;
				ExplodingParticles.this.dead++;
			}
			position = position.add(velocity);
		}

		void render() {
			if (dead)
				return;
			logic();

			GL11.glColor3d(velocity.getX1(), velocity.getX2(), velocity.getX3());
			OGL.setColor(ExplodingParticles.this.color);
			GL11.glBegin(GL11.GL_TRIANGLES);
			GL11.glVertex3d(position.getX1(), position.getX2(), position.getX3());
			GL11.glVertex3d(position.getX1() + spann.getX1(), position.getX2(), position.getX3());
			GL11.glVertex3d(position.getX1() + spann.getX1(), position.getX2() + spann.getX2(), position.getX3());
			GL11.glEnd();

		}
	}

	//@override
	public boolean checkCollisionforObjekt(Vektor3D pos) {
		// TODO Auto-generated method stub
		return false;
	}

	//@override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	//@override
	public Vektor3D getDimension() {
		// TODO Auto-generated method stub
		return null;
	}

	//@override
	public int getID() {
		// TODO Auto-generated method stub
		return 0;
	}

	//@override
	public int getOptionCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	//@override
	public String getOptionDescription(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	//@override
	public int getOptionType(int i) {
		// TODO Auto-generated method stub
		return 0;
	}

	//@override
	public Object getOptionValue(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	//@override
	public Vektor3D getPosition() {
		// TODO Auto-generated method stub
		return null;
	}

	//@override
	public int hurt(int by) {
		// TODO Auto-generated method stub
		return 0;
	}

	//@override
	public boolean isInWertebereich(int i, Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	//@override
	public void logic() {
		// TODO Auto-generated method stub

	}

	//@override
	public void setOptionValue(int i, Object value) {
		// TODO Auto-generated method stub

	}

	//@override
	public void setPosition(Vektor3D v) {
		// TODO Auto-generated method stub

	}

	//@override
	public void setPositionDirectly(Vektor3D v) {
		// TODO Auto-generated method stub

	}
}
