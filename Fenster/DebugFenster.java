package Fenster;

import javax.swing.JLabel;

import Classes.Vektor3D;

/**
 * Utility window for developer gimmicks like Frames-per-Second etc..
 * 
 * @author laDanz
 * 
 */
public class DebugFenster extends EditorFenster {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static JLabel frames;
	static JLabel timetoload;
	static JLabel pos;

	/**
	 * Constructor
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public DebugFenster(int x, int y, int width, int height) {
		super(x, y, width, height);
		this.setTitle("Debug V1.1");
		initcomp();
	}

	/**
	 * Initiate the FPS HUD
	 */
	private void initcomp() {
		frames = new JLabel();
		timetoload = new JLabel();
		frames.setBounds(20, 5, 100, 20);
		frames.setText("FPS: N%A");
		add(frames);
		timetoload.setBounds(20, 25, 150, 20);
		timetoload.setText("timetoload: N%A");
		add(timetoload);
		pos = new JLabel("Pos: ");
		pos.setBounds(20, 45, 150, 20);
		add(pos);
	}

	/**
	 * Output for the actual frames
	 * @param frames_ ammount of frames to be displayed
	 */
	public static void setframes(int frames_) {
		frames.setText("FPS: " + frames_);
	}

	/**
	 * Set output position
	 * @param pos_ Vektor3D position
	 */
	public static void setPos(Vektor3D pos_) {
		pos.setText("pos: " + pos_.round(1).toString());
	}

	/**
	 * display time to load
	 * @param ttl
	 */
	public static void settimetoload(int ttl) {
		if (timetoload != null)
			timetoload.setText("timetoload: " + (ttl) + " ms");
	}

}
