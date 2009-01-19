package mechPeck.weapons;

import main.SuperMain;
import mechPeck.Munition;
import mechPeck.Waffe;
import Classes.Vektor3D;

/**
 * Class that contains methods and variables that all weapons use
 * 
 * @author ladanz
 * 
 */
public class WaffenMaster implements Waffe {

	private String caliber;
	private Munition munition;
	private int munition_count;
	private long abklingzeit = 0;
	private double schoots_per_second = 5;
	private double mechRoF = 0;
	private double mechTRoF = 0;
	private int slot_count, amountperslot;
	private long last_time_feuerd;
	double salve;

	public WaffenMaster() {
		caliber = null;
		munition = null;
		munition_count = -1;
		slot_count = amountperslot = 0;
		abklingzeit = 0;
		schoots_per_second = 10;
		last_time_feuerd = 0;
		salve = 0;
	}

	/**
	 * Sets the delay until the next shoot is ready. This is determined by the
	 * "RateOfFire".<BR>
	 * Decreases the munition for every shot and increases the shot-count of the
	 * statistic for the results.
	 * 
	 * @return is shooting possible?
	 */
	@Override
	public boolean feuer() {
		
		if (getAktuellMunitionCount() <= 0) {
			return false;
		}
		
		// Erkennen von Schuss-Salven
		if(System.currentTimeMillis()-getAbklingzeit() < 100)
		{
			if(salve < 100)
				salve++;
			//System.out.print("S: Zeit-Abkling: "+ (System.currentTimeMillis()-getAbklingzeit())+ "\t Salve:" + salve);
		}
		else
			salve = 0;

			
		
		main.SuperMain.statistics.incShootsFired();
		last_time_feuerd = System.currentTimeMillis();
		setAbklingzeit((long) (System.currentTimeMillis() + 1000 / (getSchoots_per_second() + getSchoots_per_second()
				* SuperMain.profil.mecha.getRateOfFire() * 2 / 100)));
		decMunitionCount();
		return true;
	}

	@Override
	public long getLastTimeFeuered() {
		// TODO Auto-generated method stub
		return last_time_feuerd;
	}

	/**
	 * Is the gun able to shoot again?
	 */
	public boolean isFeuerbereit() {

		return (getAbklingzeit() < System.currentTimeMillis());
	}

	/**
	 * Simple Getter
	 */
	public int getMaxMunition() {

		return getSlotCount() * getAmountperSlot();
	}

	/**
	 * Simple Getter
	 */
	@Override
	public int getAmountperSlot() {

		return amountperslot;
	}

	/**
	 * Simple Getter
	 */
	@Override
	public Vektor3D getAustrittsPkt() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Simple Getter
	 */
	@Override
	public double getGenauigkeit() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Simple Getter
	 */
	@Override
	public double getGewicht() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getPreis() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Simple Getter
	 */
	@Override
	public int getSlotCount() {
		// TODO Auto-generated method stub
		return slot_count;
	}

	/**
	 * Simple Setter
	 */
	public void setSlot_count(int slot_count) {
		this.slot_count = slot_count;
	}

	/**
	 * Simple Setter
	 */
	public void setAmountperslot(int amountperslot) {
		this.amountperslot = amountperslot;
	}

	/**
	 * Simple Getter
	 */
	@Override
	public double getZustand() {
		// TODO Auto-generated method stub
		return 0;
	}

	Munition actualmun = null;

	/**
	 * Simple Getter
	 */
	public Munition getActualMun() {
		if (actualmun == null) {
			actualmun = SuperMain.profil.mecha.bag.getActuelMunition(SuperMain.profil.mecha.isLinkeWaffe(this));
		}
		return actualmun;
	}

	/**
	 * Simple Getter
	 */
	public int getAktuellMunitionCount() {
		// TODO Auto-generated method stub
		return SuperMain.profil.mecha.bag.getAktuellMunitionCount(SuperMain.profil.mecha.isLinkeWaffe(this));
	}

	public void decMunitionCount() {
		SuperMain.profil.mecha.bag.decAktuellMunitionCount(SuperMain.profil.mecha.isLinkeWaffe(this));

	}

	/**
	 * Simple Getter
	 */
	public String getCaliber() {
		return caliber;
	}

	/**
	 * Simple Setter
	 */
	public void setCaliber(String caliber) {
		this.caliber = caliber;
	}

	/**
	 * Simple Getter
	 */
	public Munition getMunition() {
		return munition;
	}

	/**
	 * Simple Setter
	 */
	public void setMunition(Munition munition) {
		this.munition = munition;
	}

	public int getMunition_count() {
		return munition_count;
	}

	/**
	 * Simple Setter
	 */
	public void setMunition_count(int munition_count) {
		this.munition_count = munition_count;
	}

	/**
	 * Simple Getter
	 */
	public long getAbklingzeit() {
		return abklingzeit;
	}

	/**
	 * Simple Setter
	 */
	public void setAbklingzeit(long abklingzeit) {
		this.abklingzeit = abklingzeit;
	}

	/**
	 * Simple Getter
	 */
	public double getSchoots_per_second() {
		return schoots_per_second;
	}

	/**
	 * Simple Setter
	 */
	public void setSchoots_per_second(double schoots_per_second) {
		this.schoots_per_second = schoots_per_second;
	}

	/**
	 * Simple Getter
	 */
	public double getMechRoF() {
		return mechRoF;
	}

	/**
	 * Simple Setter
	 */
	public void setMechRoF(double RoF) {
		this.mechRoF = RoF;
	}

	/**
	 * Simple Getter
	 */
	public double getMechTRoF() {
		return mechTRoF;
	}

	/**
	 * Simple Setter
	 */
	public void setMechTRoF(double RoF) {
		this.mechTRoF = RoF;
	}

}
