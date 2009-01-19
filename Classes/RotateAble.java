package Classes;

/**
 * Rotatable objects must implement this.<br>
 * Otherwise the hitbox is not correctly calculated.
 * 
 * @author laDanz
 * 
 */
public interface RotateAble {

	double getDrehwinkel();

	void setDrehwinkel(int dreh);

}
