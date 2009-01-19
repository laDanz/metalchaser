package mechPeck;

import java.awt.Dimension;
import java.awt.Point;
import java.io.Serializable;

/**
 * Utility-Class for bag handling Location and Dimensions of items
 * @author danzi
 */

public class ItemLocationDimension implements Serializable {

	private static final long serialVersionUID = 1L;

	public Equipable Item;

	public Point Location;

	public Dimension Dimension;

	public ItemLocationDimension(Equipable item) {
		this(item, new Point(0, 0));
	}

	public ItemLocationDimension(Equipable item, Point location) {
		Item = item;
		Location = new Point(location);
		Dimension = item.getEquipDimension();

	}
}
