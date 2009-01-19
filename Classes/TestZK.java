package Classes;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

/**
 * Was for testing Zielkreuz but now it just creates a window.
 * 
 * @author PoggeDeluxe
 * 
 */
class TestZK implements OGLable {

	public TestZK() {

		try {
			OGL.init(true, this, 1024, 768);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		once();
	}

	public void logic() {
		// TODO Auto-generated method stub
		render();
		// Display.update();
		if (Keyboard.isKeyDown(Keyboard.KEY_F10)) {
			OGL.finished = true;

		}
	}

	public void once() {
		// TODO Auto-generated method stub

		logic();
		render();
		Display.update();
	}

	public void render() {

	}

	public static void main(String args[]) {

	}

}
