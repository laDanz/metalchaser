package monster;

import Classes.OGL;
import Classes.Vektor3D;
import anim.SkelettSentry;

public class CollisionTest extends master {

	SkelettSentry skelett;

	public CollisionTest() {
		super();
		// master übernimmt id + position
		skelett = new SkelettSentry(getID());
	}

	@Override
	public boolean checkCollisionforObjekt(Vektor3D pos) {
		// TODO Auto-generated method stub
		return skelett.checkCollisionforObjekt(pos, getPosition());
	}

	@Override
	public void render() {
		OGL.verschieb(position);
		skelett.render(0, 45);
		OGL.verschieb(position.negiere());
	}

}
