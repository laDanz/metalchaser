package mechPeck.weapons;

import main.LevelPlay;
import main.SuperMain;
import mechPeck.munition.Rockets;
import Classes.Geschoss;
import Classes.Level;
import Classes.OGL;
import Classes.OpenAlClip;
import Classes.Vektor3D;
import Classes.v3;

public class RocketLauncher extends WaffenMaster {

	OpenAlClip sound;

	public RocketLauncher() {
		super();
		setMunition(new Rockets());
		setMunition_count(getMaxMunition());
		setCaliber("Rockets");
		setSchoots_per_second(0.2);
		setSlot_count(1);
		setAmountperslot(10);
		sound = new OpenAlClip("sound/rocketLauncherLaunch.ogg");
	}

	// FIXME auslagerungs potenzial
	public boolean feuer() {
		if (!super.feuer())
			return false;

		// Geschoss erzeugen und Genauigkeit bestimmen

		double dx = 0;
		double dy = 0;

		sound.play();

		if (Math.random() * 100 > SuperMain.profil.mecha.getRocketLauncherAccuracy()*2) {

			double offset = 1;

			dx = Math.random() * offset - offset / 2.;
			dy = Math.random() * offset - offset / 2.;
		}

		Vektor3D richtung = new v3(-1 * Math.sin(Math.toRadians(LevelPlay.p.blickrichtung + dx)), -1
				* Math.sin(Math.toRadians(LevelPlay.p.yblick + dy)), -1
				* Math.cos(Math.toRadians(LevelPlay.p.blickrichtung + dx)));
		if (Level.getSelectedPosition() != null) {

			richtung = Level.getSelectedPosition().add(getAustrittsPkt().add(LevelPlay.p.getPosition()).mal(-1))
					.normierter();

		}
		double speed = ((300 / 3.6) / (60)) * OGL.fps_anpassung;
		Geschoss g = new Geschoss(getAustrittsPkt().add(LevelPlay.p.getPosition()), richtung, getActualMun(), speed,
				LevelPlay.p.getID());

		LevelPlay.addBullet(g);

		return true;
	}

	public Vektor3D getAustrittsPkt() {
		// TODO Auto-generated method stub
		int dazu = (SuperMain.profil.mecha.isLinkeWaffe(this) ? 30 : -30);
		return new Vektor3D(-2 * Math.sin(Math.toRadians(LevelPlay.p.blickrichtung + dazu)), 2, -2
				* Math.cos(Math.toRadians(LevelPlay.p.blickrichtung + dazu)));
	}

	public double getGenauigkeit() {
		// TODO Auto-generated method stub
		return 50;
	}

	public double getPreis() {
		// TODO Auto-generated method stub
		return 1000;
	}

	public double getZustand() {
		// TODO Auto-generated method stub
		return 100;
	}

	public double getGewicht() {
		// TODO Auto-generated method stub
		return 100;
	}

}
