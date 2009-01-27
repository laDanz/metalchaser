package monster;

import main.SuperMain;
import mechPeck.equipment.HealthPackage;
import mechPeck.munition.MunPack;
import mechPeck.munition.PigShells;
import Classes.Geschoss;
import Classes.OGL;
import Classes.Object3D;
import Classes.OpenAlClip;
import Classes.RadarAble;
import Classes.RotateAble;
import Classes.Vektor3D;
import Classes.v3;
import drops.EquipDrop;
import drops.VerteilbarePunkteDrop;

public class fuckingMonster extends monster.master implements RotateAble, RadarAble {

	v3 move_to;
	static Object3D object;
	int rotation = 0;
	changeWaybyTimeThread ChangeWaybyTimeThread;

	// int health;
	static OpenAlClip mycrit;

	/**
	 * Constructor
	 */
	public fuckingMonster() {
		super();// "models/more_models/pig.obj"
		if (object == null)
			object = new Object3D("models/more_models/pig.obj", "img/textures/skin-pink.jpg");
		ChangeWaybyTimeThread = null;
		Health = 5;

		try {
			if (mycrit == null)
				mycrit = new OpenAlClip("sound/pig.ogg");
		} catch (Exception ex) {
			// Unsupported Conversion!

			// FIXME
		}
	}

	@Override
	public void doCriticalDamage() {
		mycrit.play();
	}

	// @override
	public void setDrehwinkel(int dreh) {
		this.rotation = dreh;
	}

	// @override
	public boolean checkCollisionforObjekt(Vektor3D pos) {

		return Geschoss.checkCollisionforObjektAsEllipse(this, pos);
	}

	public int getRadarAppearance() {
		if (Health > 0)
			return RadarAble.MONSTER;
		else
			return RadarAble.INVISABLE;
	}

	public double getDrehwinkel() {
		// TODO Auto-generated method stub
		return rotation;
	}

	public int hurt(int by) {
		if (Health > 0 && by >= Health) {
			onDeath();
		}
		Health -= by;
		return 0;
	}

	public boolean isAlive() {
		return Health > 0;
	}

	public void onDeath() {
		// SuperMain.addDrop(new SchrottDrop(this));
		// SuperMain.addDrop(new HealthDrop((int) (10), position));
		SuperMain.addDrop(new VerteilbarePunkteDrop(position, 1));
		SuperMain.addDrop(new EquipDrop(position, new MunPack(1 + (int) (100 * Math.random() + 100), new PigShells()),
				new HealthPackage(10)));
		if (mycrit != null)
			mycrit.play();

	}

	public Vektor3D getDimension() {
		if (Health > 0) {
			return object.getDimension();
		}
		return null;
	}

	public String toString() {

		return "fuckingMonster";
	}

	boolean richtung_geandert = false;

	public void render() {
		super.render();
		OGL.verschieb(position);
		OGL.rot(rotation, new v3(0, 1, 0));
		if (isAlive()) {
			object.render(id);
			object.renderSchatten(this);
			richtung_geandert = false;
		}
		OGL.rot(-rotation, new v3(0, 1, 0));
		OGL.verschieb(position.negiere());
	}

	/**
	 * Calculate Waypoints
	 */
	void calcWay() {
		double rand = Math.random() * 5 + 5;
		double rand2 = Math.random() * 5 + 5;
		if (Math.random() > 0.5)
			rand *= -1;
		if (Math.random() > 0.5)
			rand2 *= -1;

		move_to = new v3(position.getX1() + rand, 0, position.getX3() + rand2);
		if (move_to.getX1() < 0 || move_to.getX1() > SuperMain.level.width || move_to.getX3() > 0
				|| move_to.getX3() < -Math.abs(SuperMain.level.depth)) {
			calcWay();
		}

		rotation = (int) Math.round(Math.toDegrees(Math.atan2(move_to.getX1() - position.getX1(), move_to.getX3()
				- position.getX3()))) - 90;
		richtung_geandert = true;
	}

	Vektor3D getV(Vektor3D v) {
		return v.mal(1 / (v.length() * 10));
	}

	public void logic() {
		super.logic();
		if (isAlive()) {
			if (ChangeWaybyTimeThread == null)
				ChangeWaybyTimeThread = new changeWaybyTimeThread(5);
			if (move_to == null || move_to.add(position.mal(-1)).length() < 1 || move_to.equals(position)) {
				if (Health > 0)
					calcWay();
			}
			this.setPosition(position.add(getV(move_to.add(position.mal(-1)))));
		}

	}

	public String getDescription() {

		return "Test Monster";
	}

	/**
	 * Changes the direction after a specified time
	 * 
	 * @author ladanz
	 * 
	 */
	class changeWaybyTimeThread extends Thread {
		int wait;

		public changeWaybyTimeThread(int secs) {
			wait = 1000 * secs;
			start();

		}

		public void run() {
			while (isAlive()) {
				try {
					sleep(wait);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					SuperMain.out(e);
				}
				calcWay();
			}
		}

	}

}
