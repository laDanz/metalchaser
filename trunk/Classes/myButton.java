package Classes;

/**
 * A class representing ingame buttons.
 * 
 * @author laDanz
 * 
 */
import java.awt.event.MouseListener;
import java.io.IOException;

import main.SuperMain;

public class myButton {
	private int id;
	private Texture[] state_tex;
	private Runnable action;
	private double x;
	private double y;
	private double width;
	private double height;
	private int state;
	private String text, tooltip_text;
	// private Text text_out;
	private Vektor3D text_color;
	private boolean visible;
	// private static Text text_text;
	private long first_time_on = 0;

	private static Texture tooltip = null;
	private Vektor3D tooltip_spann;

	static OpenAlClip sound;

	MouseListener mlistener;

	/**
	 * Constructor.
	 * 
	 * @param filename
	 *            What images should be used, for example: img/button/editor
	 * @param x
	 *            The x coordinate of the button.
	 * @param y
	 *            The y coordinate of the button.
	 * @param width
	 *            The width of the button.
	 * @param height
	 *            The height of the button.
	 * @param action_
	 *            The action that should run after activating the button.
	 * @param text_
	 *            The displayed text.
	 */
	public myButton(String filename, double x, double y, double width, double height, Runnable action_, String text_) {
		id = SuperMain.genId();
		if (filename != null)
			loadTex(filename);
		this.x = x;
		visible = true;
		if (sound == null) {
			sound = new OpenAlClip("sound/klick.ogg");
		}
		this.y = y;
		this.width = width;
		this.height = height;
		action = action_;
		state = 0;
		this.text = text_;
		this.tooltip_text = "";
		// text_out = new Text();
		text_color = new v3(0, 0, 0);
		// text_text = new Text();

		// new myButtonListener(this); Versuch über MouseListener?
	}

	/**
	 * Simple Setter.
	 * 
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Simple Getter.
	 */
	public String getText() {
		return text;
	}

	/**
	 * Simple Getter.
	 * 
	 */
	public int getId() {
		return id;
	}

	/**
	 * Simple Getter.
	 * 
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * Simple Setter.
	 * 
	 */
	public void setHeight(double height) {
		this.height = height;
	}

	/**
	 * Simple Getter.
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * Simple Setter.
	 * 
	 */
	public void setWidth(double width) {
		this.width = width;
	}

	/**
	 * Simple Getter.
	 * 
	 */
	public double getY() {
		return y;
	}

	/**
	 * Simple Setter.
	 * 
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * Simple Setter.
	 * 
	 */
	public void setX(double x) {
		this.x = x;
	}

	private void loadTex(String filename) {
		state_tex = new Texture[3];
		for (int i = 0; i <= 2; i++) {
			state_tex[i] = null;
			try {
				state_tex[i] = SuperMain.loadTex(filename + i + ".png");
			} catch (IOException e) {
			}
			if (state_tex[i] != null)
				continue;
			try {
				state_tex[i] = SuperMain.loadTex(filename + i + ".jpg");
			} catch (IOException e) {
			}
			if (state_tex[i] != null)
				continue;
			try {
				state_tex[i] = SuperMain.loadTex(filename + i + ".bmp");
			} catch (IOException e) {
			}

			if (state_tex[i] == null) {
				if (i == 0) {
					// Konnte nichtmal die erste finden
					// throw new RuntimeException("Image not Found!");
				}
				// ansonsten einfach die erste bernehmen
				state_tex[i] = state_tex[0];
			}

		}
		if (tooltip == null)
			try {
				tooltip = SuperMain.loadTex("img/button/tooltip.png");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				SuperMain.out(e);
			}

	}

	/**
	 * Check whether the button is clicked.
	 * 
	 * @param selection
	 *            The selection value.
	 * @param buttonDown
	 *            The button state.
	 */
	public void tick(int selection, boolean buttonDown) {
		if (selection == this.id) {
			this.state = 1;
			if (first_time_on == 0) {
				first_time_on = System.currentTimeMillis();
				if (mlistener != null)
					mlistener.mouseEntered(null);
			}
		} else {
			if (first_time_on > 0) {
				if (mlistener != null)
					mlistener.mouseExited(null);
			}
			state = 0;
			first_time_on = 0;
		}
		if (selection == this.id && buttonDown && this.action != null) {
			this.state = 2;
			this.action.run();
			if (isVisible()) {
				// sound.play();
			}
		}

	}

	/**
	 * Simple Getter.
	 * 
	 */
	public String getTooltip() {
		// TODO Auto-generated method stub
		return tooltip_text;
	}

	/**
	 * Adds a MouseListener for event listening.
	 * 
	 * @param ml
	 *            A MouseListener.
	 */
	public void addMouseListener(MouseListener ml) {
		mlistener = ml;
	}

	/**
	 * Simple Setter.
	 * 
	 */
	public void setTooltip(String s) {
		// ausstellen:
		if (true)
			return;
		tooltip_text = s;

		// spann machen
		int line_count = 1;
		int longestline = 0;
		for (String ss : s.split("\n")) {
			line_count++;
			if ((longestline - 4) < ss.length())
				longestline = ss.length() + 4;
		}
		tooltip_spann = new v3(longestline * 0.32, Math.max(0.35 + (line_count - 1) * 0.5, 0.5), 0);
	}

	boolean was_down = false;

	/**
	 * Renders this button.
	 */
	public void render() {
		Vektor3D wo = new v3(x, y, 0);

		tick(SuperMain.selection, (!org.lwjgl.input.Mouse.isButtonDown(0) && was_down));
		was_down = org.lwjgl.input.Mouse.isButtonDown(0);

		OGL.viereck(wo, new v3(width, height, 0), (state_tex == null ? null : state_tex[state]), id,
				(state_tex == null ? 0 : 1.1));
		if (text != null && text.length() > 0) {
			wo = wo.add(new v3(Math.max(0.25, (width - text.length() * 0.5) / 2), (height) / 4., 0));
			myText.out(text, wo, new v3(0.5, 0.5, 0.1), text_color, 1.1, id);
			OGL.setColor(myColor.WHITE);
		}

		// Tooltip
		// 3 sek warten

		if (first_time_on > 0 && getTooltip().length() > 0) {
			// er ist on
			double alpha = 0;

			if (System.currentTimeMillis() > first_time_on + 3000) {
				// er ist 3 sekunden on
				alpha = (System.currentTimeMillis() - first_time_on - 3000) / 3000.;
				if (alpha > 1)
					alpha = 1.1;
			}
			OGL.viereck(wo.add(new v3(width - 1, height - 1, 0)), tooltip_spann, tooltip, 0, alpha);
			wo = wo.add(new v3(width - 1 + tooltip_spann.getX1() * 0.15, height - 1 + tooltip_spann.getX2(), 0));
			for (String s : getTooltip().split("\n")) {
				wo = wo.add(new v3(0, -0.5, 0));
				Vektor3D scale = new v3(0.3, 0.3, 1);
				myText.out(s, wo, scale, myColor.BLUE, alpha, 0);
			}
		}
	}

	/**
	 * Simple Setter.
	 * 
	 */
	public void setText(String name) {
		this.text = name;
	}

	/**
	 * Simple Setter.
	 * 
	 */
	public void setAction(Runnable action) {
		this.action = action;
	}

	/**
	 * Simulates a button click.
	 */
	public void DoClick() {
		this.state = 2;
		this.action.run();
	}

	/**
	 * Simple Setter.
	 * 
	 */
	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;

	}

	/**
	 * Simple Setter.
	 * 
	 */
	public void setVisible(boolean b) {
		visible = b;

	}

	/**
	 * Simple Getter.
	 * 
	 */
	public boolean isVisible() {
		return visible;

	}

	/**
	 * Simple Getter.
	 * 
	 */
	public double getX() {
		// TODO Auto-generated method stub
		return x;
	}

}
