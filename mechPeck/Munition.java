package mechPeck;

import java.io.Serializable;

/**
 * Munition-Interface
 * 
 * @author danzi
 * 
 */
public interface Munition extends Serializable {
	/**
	 * Kaliber in Stringform.<br>
	 * Wird klassisch ber Munition.getCaliber.equals(Waffe.getCaliber
	 * ermittelt).<br>
	 * Mehrere Caliber mit Kommatrennen.
	 * 
	 * @return
	 */
	String getCaliber();

	/**
	 * ohne verschieben! Rendern
	 */
	void render();

	int getSchaden();

	int getPiercing();

	void setSchaden(int value);

	void setPiercing(int value);

}
