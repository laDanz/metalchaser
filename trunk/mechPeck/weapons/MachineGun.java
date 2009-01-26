package mechPeck.weapons;

import java.util.Random;

import main.LevelPlay;
import main.SuperMain;
import mechPeck.munition.NineMilimeters;
import Classes.Geschoss;
import Classes.Level;
import Classes.OGL;
import Classes.OpenAlClip;
import Classes.Vektor3D;

public class MachineGun extends WaffenMaster {

	static OpenAlClip sound;

	public MachineGun() {
		super();
		setMunition(new NineMilimeters());
		setMunition_count(getMaxMunition());
		setCaliber("9mm");
		setSchoots_per_second(4);
		setSlot_count(3);
		setAmountperslot(100);
		if (sound == null)
			sound = new OpenAlClip("sound/shoot1.ogg");
	}

	// FIXME auslagerungs potenzial
	public boolean feuer() {
		if (!super.feuer())
			return false;

		// Normalverteilte Zufallszahlen generieren
		Random random = new Random();

		// Geschoss erzeugen und Genauigkeit bestimmen

		sound.play();

		double dx = salve / 20 + random.nextGaussian() * 2 / (1 + SuperMain.profil.mecha.getGatlingAccuracy() / 20);
		double dy = salve / 20 + random.nextGaussian() * 2 / (1 + SuperMain.profil.mecha.getGatlingAccuracy() / 20);

		// System.out.print("\t offset: "+salve/100+"\t dx= "+dx+"\t dy= "+dy+"\n");

		Vektor3D richtung = new Vektor3D(-1 * Math.sin(Math.toRadians(LevelPlay.p.blickrichtung + dx)), -1
				* Math.sin(Math.toRadians(LevelPlay.p.yblick + dy)), -1
				* Math.cos(Math.toRadians(LevelPlay.p.blickrichtung + dx)));

		if (Level.getSelectedPosition() != null) {

			richtung = Level.getSelectedPosition().add(getAustrittsPkt().add(LevelPlay.p.getPosition()).mal(-1))
					.normierter();

			double xb = Math.atan2(richtung.getX1(), richtung.getX3());
			double yb = Math.atan2(richtung.getX2(), 1);

			richtung = new Vektor3D(Math.sin(xb + Math.toRadians(dx)), Math.sin(yb + Math.toRadians(dy)), Math.cos(xb
					+ Math.toRadians(dx)));

		}

		double speed = ((1000 / 3.6) / (60)) * OGL.fps_anpassung;
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
