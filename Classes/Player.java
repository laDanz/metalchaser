package Classes;

import java.io.IOException;

import main.LevelPlay;
import main.SuperMain;

import org.lwjgl.input.Keyboard;

import anim.SkelettZ300;

/**
 * Represents the player on the level.
 * 
 * 
 * @author laDanz
 * 
 */
public class Player implements Objekt {
	Texture myico;
	public double blickrichtung = 0;
	public double huftwinkel = 0;
	public double yblick = 0;
	Vektor3D dimension;
	Vektor3D position;
	public SkelettZ300 skelett;
	public int life;
	int id;

	/**
	 * Constructor.
	 */
	public Player(Texture tex) {
		setPosition(new v3(0, 0, 0));
		setDimension(new v3(1.5, 2, 1.5));
		id = SuperMain.genId();
		if (tex == null) {
			try {
				TextureLoader loader = new TextureLoader();
				myico = loader.getTexture("img/test.bmp");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				SuperMain.out(e);
			}
		} else {
			myico = tex;
		}
		object3d = new Object3D("models/teapotT.3DS", "img/marble.jpg");
		life = SuperMain.profil.mecha.getHealth();
		skelett = new SkelettZ300("Z300");

	}

	/**
	 * Default constructor.
	 */
	public Player() {
		this(null);
	}

	public Player(int id) {
		this(null);
		this.id = id;
	}

	/**
	 * Checks whether the player collides with a given point.
	 */
	public boolean checkCollisionforObjekt(Vektor3D pos) {
		// FIXME wenn aus mehreren teilen besteht dann auf alle teile anwenden

		return skelett.checkCollisionforObjekt(pos.add(position.mal(-1)).mal(1 / d));
	}

	/**
	 * Perform some calculations.<br>
	 * 
	 */
	public void logic() {
		// Bewegung in LevelPlay

		// werde ich von einem gegner weggeschubst?
		if (life > 0)
			for (Objekt o : SuperMain.level.objekte) {
				if (o instanceof monster.master) {
					monster.master enemy = (monster.master) o;
					// Stärketest
					int mystreangth = (int) SuperMain.profil.mecha.getStrength();
					if (mystreangth > enemy.Strength || !enemy.isAlive())
						continue;
					// in meiner nähe?
					Vektor3D delta = getPosition().add(o.getPosition().mal(-1));
					double length = delta.length();
					double grenz_abstand = 2.6;
					if (length < grenz_abstand) {// FIXME wertanpassen
						// mich wegpushen
						delta = delta.normierter().mal(grenz_abstand - length);
						// nicht über levelbegrenzung hinaus
						if (main.SuperMain.level.isOutta(getPosition().add(delta))) {
							continue;
						}
						setPosition(getPosition().add(delta));
					}
				}
			}

	}

	/**
	 * Set the Position of the player.<br>
	 * The height information is taken from the level height array.
	 */
	public void setPosition(Vektor3D v) {
		position = new v3(v.getX1(), main.SuperMain.level.getHeight(v), v.getX3());
	}

	/**
	 * Set the position of the player directly. Doesn't take the height
	 * information from the level height array.
	 */
	public void setPositionDirectly(Vektor3D v) {
		position = v;
	}

	/**
	 * Simple Setter.
	 * 
	 * @param v3
	 */
	private void setDimension(v3 v3) {
		this.dimension = v3;

	}

	Object3D object3d;
	double d = 0.4;
	Vektor3D scale = new v3(d, d, d);

	/**
	 * Render the player.
	 */
	public void render() {
		render((int) huftwinkel);
	}

	/**
	 * Render the player.
	 */
	public void render(int huftwinkel) {
		this.huftwinkel = huftwinkel;
		OGL.verschieb(this.getPosition());
		OGL.rot(blickrichtung + 90, new v3(0, 1, 0));
		OGL.skaliere(scale);

		// object3d.render();
		skelett.render(0, huftwinkel);

		OGL.skaliere(scale.reziproke());
		// OGL.wuerfel(new v3(this.getDimension().getX1() / -2, 0, this
		// .getDimension().getX3()
		// / -2), this.getDimension(), this.getIco());
		OGL.rot(-blickrichtung - 90, new v3(0, 1, 0));
		OGL.verschieb(this.getPosition().negiere());

	}

	/**
	 * Simple Getter.
	 */
	public Vektor3D getDimension() {
		// TODO Auto-generated method stub
		return dimension;
	}

	/**
	 * Changes the players health.<br>
	 * 
	 */
	public int hurt(int by) {
		if (by == 0) {
			return 0;
		}
		life -= by;
		if (life <= 0) {
			onPlayerDead();
			return Math.abs(life) + 1;
		}
		// beschränkung nach oben
		if (life > SuperMain.profil.mecha.getHealth()) {
			int d = life - SuperMain.profil.mecha.getHealth();
			life -= d;
			// System.out.println("zu viel!!! neue health:"
			// +SuperMain.profil.mecha.getHealth());
			return d;
		}

		return 0;

	}

	private void onPlayerDead() {
		new Thread() {
			@Override
			public void run() {
				// LevelPlay onDeadAnimation()
				boolean alreaddead = LevelPlay.onDeadAnimation();
				if (alreaddead)
					return;
				life = SuperMain.profil.mecha.getHealth();
				// warte 30 Sekunden
				int seconds = 30;
				for (int i = 0; i < seconds * 10; i++) {
					try {
						this.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (Keyboard.isKeyDown(Keyboard.KEY_RETURN)) {
						break;
					}
					if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
						return;
					}
				}

				// Neues Level laden.

				LevelPlay.setState("LevelPlay," + "StartPkt#" + LevelPlay.startPktNummer + ","
						+ LevelPlay.geladenes_level);
			}
		}.start();

	}

	public Texture getIco() {
		// TODO Auto-generated method stub
		return myico;
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
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
		return position;
	}

	public boolean isInWertebereich(int i, Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	public void setOptionValue(int i, Object value) {
		// TODO Auto-generated method stub

	}

}
