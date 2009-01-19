package Classes;

import main.SuperMain;

/**
 * Little utility to calculate time differences.
 * 
 * @author laDanz
 * 
 */
public class Zeit_messen {
	long start, ende, gesamt;

	/**
	 * Set the start time.
	 */
	public void start() {
		java.util.Date dat = new java.util.Date();
		start = dat.getTime();
	}

	/**
	 * Set the end time and return the difference.
	 * 
	 * @return difference
	 */
	public long ende() {
		java.util.Date dat2 = new java.util.Date();
		ende = dat2.getTime();
		gesamt += res();
		return (ende - start);
	}

	/**
	 * writes the difference to the console.
	 */
	public void ausgabe() {
		SuperMain.out("Das Programm brauch " + (ende - start) + " mSek");
	}

	/**
	 * returns the difference
	 * 
	 * @return the difference.
	 */
	public long res() {
		return (ende - start);
	}

	public long gesamt() {
		return (gesamt);
	}

	public Zeit_messen() {
		this.start = 0;
		this.ende = 0;
		this.gesamt = 0;
	}

}
