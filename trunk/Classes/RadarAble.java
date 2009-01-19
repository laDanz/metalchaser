package Classes;

/**
 * Implement of Object is visible on the radar
 * 
 * @author laDanz
 * 
 */
public interface RadarAble {

	static final int INVISABLE = 0;
	static final int MONSTER = 1;
	static final int GOAL = 2;
	static final int OTHER = 3;

	int getRadarAppearance();

}
