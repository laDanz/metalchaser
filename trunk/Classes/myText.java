package Classes;

import static org.lwjgl.opengl.GL11.glColor4f;

import java.util.HashMap;
import java.util.LinkedList;

import main.SuperMain;

import org.lwjgl.opengl.GL11;

/**
 * A class which provides writing with texture fonts.
 * 
 * @author laDanz
 * 
 */
public class myText {

	static LinkedList<HashMap<Character, Integer>> lists;

	static String font_location = "img/hud/font0.png";

	static char[] zeile2 = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.', ',', '-', ':', ';', '_', '!', '"',
			Character.PARAGRAPH_SEPARATOR, '$', '%', '&', '/', '(', ')', '=' };
	// 'ö' platzhalter für nicht funktionierende
	static char[] zeile3 = { '?', '{', '[', ']', '}', '\\', '^', 'x', '+', '*', '~', '#', '\'', '<', '>', '|', '<',
			'@', '<', '<', '<', '<', '<', '<', '<', '<' };

	// FIXME Paragraph GradZeichen und Euro Symbol geht net unter win+linux
	static Texture font_tex;

	static private int selected_text = 0;

	static private int selected_big_text = 0;

	private static int font_count = 0;

	/**
	 * Constructor.
	 */
	public myText() {
		this(null);
	}

	/**
	 * Default constructor.<br>
	 * 
	 * @param font_location_
	 *            The location of the texture-font file.
	 */
	public myText(String font_location_) {
		if (lists == null) {
			lists = new LinkedList<HashMap<Character, Integer>>();

			font_tex = null;
			try {
				font_tex = SuperMain.loadTex((font_location_ == null ? font_location : font_location_));
			} catch (Exception e) {

			}
			buildFont();
		}

	}

	/**
	 * Adds another Font to this class.
	 * 
	 * @param font_location
	 *            The location of the texture-font file.
	 * @return
	 */
	public static int addNewFont(final String font_location) {
		int res = ++font_count;
		SuperMain.toRun.add(new Runnable() {
			public void run() {
				try {
					font_tex = SuperMain.loadTex((font_location));
				} catch (Exception e) {
					// TODO: handle exception
				}
				buildFont();

			}
		});
		return res;
	}

	/**
	 * Simple Getter.
	 * 
	 * @return
	 */
	public static int getSelected_big_text() {
		return selected_big_text;
	}

	/**
	 * Simple Setter.
	 * 
	 * @return
	 */
	public static void setSelected_big_text(int selected_big_text) {
		myText.selected_big_text = selected_big_text;
	}

	/**
	 * Simple Getter.
	 * 
	 * @return
	 */
	public static int getSelected_text() {
		return selected_text;
	}

	/**
	 * Simple Setter.
	 * 
	 * @return
	 */
	public static void setSelected_text(int selected_text) {
		myText.selected_text = selected_text;
	}

	/**
	 * Extract the single letters out of the texture-font.
	 */
	private static void buildFont() {
		int size = lists.size();
		lists.add(new HashMap<Character, Integer>());
		// jeden buchstaben rendern
		for (int i = 'a'; i <= 'z'; i++) {
			// erste zeile
			int list = OGL.startlist();
			drawChar((char) i);
			OGL.endlist();
			lists.get(size).put((char) i, list);
			// zweite zeile
			list = OGL.startlist();
			drawChar(i - 'a', 1);
			OGL.endlist();
			lists.get(size).put(zeile2[i - 'a'], list);
			// dritte zeile
			list = OGL.startlist();
			drawChar(i - 'a', 2);
			OGL.endlist();
			lists.get(size).put(zeile3[i - 'a'], list);
		}
	}

	private static void drawChar(int i, int j) {

		double linker_rand = -00.00;// 4;
		double rechter_rand = linker_rand;
		double diff = (1 - linker_rand - rechter_rand) / 26.;

		OGL.viereck_texCoord_light(new v3(), new v3(0.75, 1, 0), font_tex, new Vektor2D(linker_rand + diff * (i), 2
				/ 3. - (1 / 3.) * j), new Vektor2D(linker_rand + diff * (i + 1), 1 - (1 / 3.) * j));

	}

	/**
	 * Write something on the screen.
	 * 
	 * @param text
	 *            The text to be written.
	 * @param alpha
	 *            The alpha value of the text.(0-1)
	 */
	public static void out(String text, double alpha) {
		out(text, new v3(0, 0, 0), new v3(1, 1, 1), new v3(1, 1, 1), alpha, 0);
	}

	private static void drawChar(char c) {
		drawChar(c - 'a', 0);
	}

	/**
	 * Write something on the screen.
	 * 
	 * @param text
	 *            The text to be written.
	 * @param wo
	 *            The point where to write.
	 * @param scale_
	 *            The scaling for the text.
	 * @param text_color
	 *            The color of the text.
	 * @param alpha
	 *            The alpha value for the text.
	 * @param id
	 *            The name for the text.
	 */
	public static void out(String text, Vektor3D wo, Vektor3D scale_, Vektor3D text_color, double alpha, int id) {
		OGL.verschieb(wo);
		OGL.skaliere(scale_);
		GL11.glColor3f((float) text_color.getX1(), (float) text_color.getX2(), (float) text_color.getX3());
		int i = 0;
		if (alpha != 1) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			glColor4f((float) text_color.getX1(), (float) text_color.getX2(), (float) text_color.getX3(), (float) alpha);
		} else
			GL11.glDisable(GL11.GL_BLEND);
		for (char c : text.toCharArray()) {
			OGL.verschieb(new v3(.75 * i, 0, 0));
			Integer list;

			if (c >= 'a' && c <= 'z') {
				list = lists.get(selected_text).get(c);
			} else {
				list = lists.get(selected_big_text).get(((c + "").toLowerCase().charAt(0)));
			}
			if (list != null)
				OGL.calllist(list);
			// drawChar(c);
			OGL.verschieb(new v3(.75 * i, 0, 0).negiere());
			i++;
		}
		if (alpha != 1)
			GL11.glDisable(GL11.GL_BLEND);

		OGL.skaliere(scale_.reziproke());
		OGL.verschieb(wo.negiere());
	}

}
