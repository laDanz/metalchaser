package mechPeck;

import java.awt.Dimension;
import java.io.Serializable;

import Classes.Texture;

/**
 * Interface for equipable stuff
 * @author danzi
 *
 */
public interface Equipable extends Serializable {

	Dimension getEquipDimension();

	Texture getEquipTexture();

	String getMouseOverText();

	int getType();

}
