package Classes;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_NICEST;
import static org.lwjgl.opengl.GL11.GL_PERSPECTIVE_CORRECTION_HINT;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_RENDER;
import static org.lwjgl.opengl.GL11.GL_SELECT;
import static org.lwjgl.opengl.GL11.GL_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_VIEWPORT;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glClearDepth;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL11.glHint;
import static org.lwjgl.opengl.GL11.glInitNames;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glLoadName;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glPushName;
import static org.lwjgl.opengl.GL11.glRenderMode;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glSelectBuffer;
import static org.lwjgl.opengl.GL11.glShadeModel;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex3f;
import static org.lwjgl.util.glu.GLU.gluPerspective;
import static org.lwjgl.util.glu.GLU.gluPickMatrix;

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import main.SuperMain;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Sphere;

import com.sun.opengl.util.BufferUtil;

/**
 * OpenGL render and stuff class.
 * 
 * @author laDanz
 */
public class OGL {
	static int sel = 0;

	static int fps = 0;

	static Zeit_messen m;

	/** Game title */
	public static final String GAME_TITLE = "MetalChaser";

	/** Desired frame time */
	public static int FRAMERATE = 300;

	/** was wurde zuletzt selektiert */
	public static int lastsel = -1;
	/**
	 * Bildschirmdaten.<br>
	 * Weite,Hhe
	 */
	public static Vektor2D screenwh;
	/**
	 * Vollbildmodus?
	 */
	public static boolean fullscreen;

	public static int akt_fps = 60;
	/**
	 * Factor to multiply frame-based durations with, due to different fps on
	 * different machines.
	 * 
	 */
	public static double fps_anpassung = 1;

	public static int viewport[];

	/** To quit the game set this <code>true</code> */
	public static boolean finished;

	/**
	 * Tries to change the fullscreen flag.
	 * 
	 * @param fullscreen
	 * @throws Exception
	 */
	public static void setFullscreen(boolean fullscreen) throws Exception {
		if (OGL.fullscreen == fullscreen)
			return;

		if (false) {
			try {
				Display.setFullscreen(!fullscreen);
			} catch (LWJGLException e) {
				throw new Exception("Unable to set fullscreen=" + fullscreen, e);
			}
		} else {

			setDisplayMode((int) (screenwh.getX1()), (int) (screenwh.getX2()), fullscreen);
		}

	}

	/**
	 * Set the display mode to be used
	 * 
	 * @param width
	 *            The width of the display required
	 * @param height
	 *            The height of the display required
	 * @param fullscreen
	 *            True if we want fullscreen mode
	 * @throws SlickException
	 *             Indicates a failure to initialise the display
	 */
	/** The original display mode before we tampered with things */
	protected static DisplayMode originalDisplayMode;
	/** The display mode we're going to try and use */
	protected static DisplayMode targetDisplayMode;

	private static void setDisplayMode(int width, int height, boolean fullscreen) throws Exception {

		try {
			targetDisplayMode = null;
			if (fullscreen) {
				DisplayMode[] modes = Display.getAvailableDisplayModes();
				int freq = 0;

				for (int i = 0; i < modes.length; i++) {
					DisplayMode current = modes[i];

					if ((current.getWidth() == width) && (current.getHeight() == height)) {
						if ((targetDisplayMode == null) || (current.getFrequency() >= freq)) {
							if ((targetDisplayMode == null)
									|| (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel())) {
								targetDisplayMode = current;
								freq = targetDisplayMode.getFrequency();
							}
						}

						// if we've found a match for bpp and frequence against
						// the
						// original display mode then it's probably best to go
						// for this one
						// since it's most likely compatible with the monitor
						if ((current.getBitsPerPixel() == originalDisplayMode.getBitsPerPixel())
								&& (current.getFrequency() == originalDisplayMode.getFrequency())) {
							targetDisplayMode = current;
							break;
						}
					}
				}
			} else {
				targetDisplayMode = new DisplayMode(width, height);
			}

			if (targetDisplayMode == null) {
				throw new Exception("Failed to find value mode: " + width + "x" + height + " fs=" + fullscreen);
			}

			OGL.screenwh.setX1(width);
			OGL.screenwh.setX2(height);

			Display.setDisplayMode(targetDisplayMode);
			Display.setFullscreen(fullscreen);

			if (Display.isCreated()) {
				// initGL();

			}
		} catch (LWJGLException e) {
			throw new Exception("Unable to setup mode " + width + "x" + height + " fullscreen=" + fullscreen, e);
		}

	}

	public static void line(double width, Vektor3D start, Vektor3D end, int name) {
		line(width, start, end, name, false);
	}

	/**
	 * Draw a line.
	 * 
	 * @param width
	 *            Thickness in pixel.
	 * @param start
	 *            start point
	 * @param end
	 *            end point
	 * @deprecated Isnt real 3D drawing, because it has always the same
	 *             thickness independent from the distance.
	 * 
	 */
	public static void line(double width, Vektor3D start, Vektor3D end, int name, boolean headless) {
		if (!headless) {
			glDisable(GL_TEXTURE_2D);
			glLineWidth((float) width);
			glLoadName(name);
			glBegin(GL_LINES);
		}
		glVertex3f((float) start.getX1(), (float) start.getX2(), (float) start.getX3());
		glVertex3f((float) end.getX1(), (float) end.getX2(), (float) end.getX3());
		if (!headless) {
			glEnd();
		}
	}

	/**
	 * Draw a line.
	 * 
	 * @param width
	 *            Thickness in pixel.
	 * @param start
	 *            start point
	 * @param end
	 *            end point
	 * @deprecated Isnt real 3D drawing, because it has always the same
	 *             thickness independent from the distance.
	 * 
	 */

	public static void line(double width, Vektor3D start, Vektor3D end) {
		line(width, start, end, 0, false);
	}

	public static void wuerfel_streck(Vektor3D pos, Vektor3D spann, Texture tex, int i, double alpha, double streck) {
		wuerfel_streck(pos, spann, tex, i, alpha, streck, streck);
	}

	/**
	 * The texture of this cube is scaled to a given value.
	 * 
	 * @param pos
	 *            position
	 * @param spann
	 *            span vector
	 * @param tex
	 *            Texture
	 * @param i
	 *            ID/Name
	 * @param alpha
	 *            alpha value
	 * @param streckX
	 *            Texture stretch in X-dimension.
	 * @param streckY
	 *            Texture stretch in Y-dimension.
	 */

	public static void wuerfel_streck(Vektor3D pos, Vektor3D spann, Texture tex, int i, double alpha, double streckX,
			double streckY) {

		float fX = (float) streckX;
		float fY = (float) streckY;

		if (tex != null) {
			glEnable(GL_TEXTURE_2D);
			tex.bind();

		} else {
			glDisable(GL_TEXTURE_2D);
		}

		if (alpha != 1) {
			GL11.glEnable(GL11.GL_BLEND); // enable transparency
			// GL11.glEnable(GL11.GL_TEXTURE_2D); // so texture image will show
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			glColor4f((float) 1, (float) 1, (float) 1, (float) alpha);
		} else {
			GL11.glDisable(GL11.GL_BLEND); // enable transparency
		}
		// if (lastsel == i)
		// glColor3f(1, 0, 0);
		// else
		// glColor3f(1, 1, 1);

		glLoadName(i);

		glBegin(GL_QUADS);
		glTexCoord2f(0, 0);
		glVertex3f((float) pos.getX1(), (float) pos.getX2(), (float) pos.getX3());
		glTexCoord2f(fX, 0);
		glVertex3f((float) pos.getX1() + (float) spann.getX1(), (float) pos.getX2(), (float) pos.getX3());
		glTexCoord2f(fX, fY);
		glVertex3f((float) pos.getX1() + (float) spann.getX1(), (float) pos.getX2() + (float) spann.getX2(),
				(float) pos.getX3());
		glTexCoord2f(0, fY);
		glVertex3f((float) pos.getX1(), (float) pos.getX2() + (float) spann.getX2(), (float) pos.getX3());
		glTexCoord2f(0, 0);
		glVertex3f((float) pos.getX1(), (float) pos.getX2(), (float) pos.getX3() + (float) spann.getX3());
		glTexCoord2f(fX, 0);
		glVertex3f((float) pos.getX1() + (float) spann.getX1(), (float) pos.getX2(), (float) pos.getX3()
				+ (float) spann.getX3());
		glTexCoord2f(fX, fY);
		glVertex3f((float) pos.getX1() + (float) spann.getX1(), (float) pos.getX2() + (float) spann.getX2(),
				(float) pos.getX3() + (float) spann.getX3());
		glTexCoord2f(0, fY);
		glVertex3f((float) pos.getX1(), (float) pos.getX2() + (float) spann.getX2(), (float) pos.getX3()
				+ (float) spann.getX3());
		glTexCoord2f(0, 0);
		glVertex3f((float) pos.getX1(), (float) pos.getX2(), (float) pos.getX3());
		glTexCoord2f(fX, 0);
		glVertex3f((float) pos.getX1(), (float) pos.getX2(), (float) pos.getX3() + (float) spann.getX3());
		glTexCoord2f(fX, fY);
		glVertex3f((float) pos.getX1(), (float) pos.getX2() + (float) spann.getX2(), (float) pos.getX3()
				+ (float) spann.getX3());
		glTexCoord2f(0, fY);
		glVertex3f((float) pos.getX1(), (float) pos.getX2() + (float) spann.getX2(), (float) pos.getX3());
		glTexCoord2f(0, 0);
		glVertex3f((float) pos.getX1() + (float) spann.getX1(), (float) pos.getX2(), (float) pos.getX3());
		glTexCoord2f(fX, 0);
		glVertex3f((float) pos.getX1() + (float) spann.getX1(), (float) pos.getX2(), (float) pos.getX3()
				+ (float) spann.getX3());
		glTexCoord2f(fX, fY);
		glVertex3f((float) pos.getX1() + (float) spann.getX1(), (float) pos.getX2() + (float) spann.getX2(),
				(float) pos.getX3() + (float) spann.getX3());
		glTexCoord2f(0, fY);
		glVertex3f((float) pos.getX1() + (float) spann.getX1(), (float) pos.getX2() + (float) spann.getX2(),
				(float) pos.getX3());
		glTexCoord2f(0, 0);
		glVertex3f((float) pos.getX1(), (float) pos.getX2(), (float) pos.getX3() + (float) spann.getX3());
		glTexCoord2f(fX, 0);
		glVertex3f((float) pos.getX1() + (float) spann.getX1(), (float) pos.getX2(), (float) pos.getX3()
				+ (float) spann.getX3());
		glTexCoord2f(fX, fY);
		glVertex3f((float) pos.getX1() + (float) spann.getX1(), (float) pos.getX2(), (float) pos.getX3());
		glTexCoord2f(0, fY);
		glVertex3f((float) pos.getX1(), (float) pos.getX2(), (float) pos.getX3());
		glTexCoord2f(0, 0);
		glVertex3f((float) pos.getX1(), (float) pos.getX2() + (float) spann.getX2(), (float) pos.getX3()
				+ (float) spann.getX3());
		glTexCoord2f(fX, 0);
		glVertex3f((float) pos.getX1() + (float) spann.getX1(), (float) pos.getX2() + (float) spann.getX2(),
				(float) pos.getX3() + (float) spann.getX3());
		glTexCoord2f(fX, fY);
		glVertex3f((float) pos.getX1() + (float) spann.getX1(), (float) pos.getX2() + (float) spann.getX2(),
				(float) pos.getX3());
		glTexCoord2f(0, fY);
		glVertex3f((float) pos.getX1(), (float) pos.getX2() + (float) spann.getX2(), (float) pos.getX3());
		glEnd();

	}

	public static void viereck(Vektor3D pos, Vektor3D spann, Texture tex, int name, double alpha) {
		viereck(pos, spann, tex, name, alpha, 1, 1, 1);
	}

	public static void viereck(Vektor3D pos, Vektor3D spann, Texture tex, int name, double alpha, double r, double g,
			double b) {
		// Wenn keine Textur, nur Farbe
		if (tex != null) {
			glEnable(GL_TEXTURE_2D);
			tex.bind();
		} else {
			glDisable(GL_TEXTURE_2D);
		}
		// alpha Wert anders als 1 --> Blending
		if (alpha != 1) {
			GL11.glEnable(GL11.GL_BLEND); // enable transparency
			// GL11.glEnable(GL11.GL_TEXTURE_2D); // so texture image will show
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			glColor4f((float) r, (float) g, (float) b, (float) alpha);
		} else {
			GL11.glDisable(GL11.GL_BLEND); // enable transparency
			glColor3f((float) r, (float) g, (float) b);
		}
		// wurde aktuelles Element als letztes getroffen? --> ROT!
		// if (lastsel == i)
		// glColor3f(1, 0, 0);
		// else
		// glColor3f(1, 1, 1);
		glLoadName(name);
		glBegin(GL_QUADS);
		glTexCoord2f(0, 0);
		glVertex3f((float) pos.getX1(), (float) pos.getX2(), (float) pos.getX3());
		glTexCoord2f(1, 0);
		glVertex3f((float) pos.getX1() + (float) spann.getX1(), (float) pos.getX2(), (float) pos.getX3());
		glTexCoord2f(1, 1);
		glVertex3f((float) pos.getX1() + (float) spann.getX1(), (float) pos.getX2() + (float) spann.getX2(),
				(float) pos.getX3() + (float) spann.getX3());
		glTexCoord2f(0, 1);
		glVertex3f((float) pos.getX1(), (float) pos.getX2() + (float) spann.getX2(), (float) pos.getX3()
				+ (float) spann.getX3());
		glEnd();

	}

	public static void viereck_freeform(Vektor3D ul, Vektor3D ur, Vektor3D or, Vektor3D ol, Texture tex, int name,
			double alpha) {
		viereck_freeform(ul, ur, or, ol, tex, new Vektor2D(0, 0), new Vektor2D(1, 1), name, alpha);
	}

	public static void viereck_freeform(Vektor3D ul, Vektor3D ur, Vektor3D or, Vektor3D ol, Texture tex,
			Vektor2D texcoord, Vektor2D texcoord2, int name, double alpha) {
		viereck_freeform(ul, ur, or, ol, tex, texcoord, texcoord2, name, alpha, false);
	}

	public static void viereck_freeform(Vektor3D ul, Vektor3D ur, Vektor3D or, Vektor3D ol, Texture tex,
			Vektor2D texcoord, Vektor2D texcoord2, int name, double alpha, boolean headless) {
		// Wenn keine Textur, nur Farbe
		if (!headless) {
			if (tex != null) {
				glEnable(GL_TEXTURE_2D);
				tex.bind();
			} else {
				glDisable(GL_TEXTURE_2D);
			}
		}
		// alpha Wert anders als 1 --> Blending
		if (!headless) {
			if (alpha != 1) {
				GL11.glEnable(GL11.GL_BLEND); // enable transparency
				// GL11.glEnable(GL11.GL_TEXTURE_2D); // so texture image will
				// show
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				glColor4f((float) 1, (float) 1, (float) 1, (float) alpha);
			} else {
				GL11.glDisable(GL11.GL_BLEND); // enable transparency
			}
		}

		if (!headless) {
			glLoadName(name);
		}
		Vektor3D n = normale(ul, ur, or, ol);
		if (!headless) {
			glBegin(GL_TRIANGLES);
		}
		glTexCoord2f((float) texcoord.getX1(), (float) texcoord.getX2());
		glVertex3f((float) ul.getX1(), (float) ul.getX2(), (float) ul.getX3());
		// GL11.glNormal3f( (float) n.getX1(), (float) n.getX2(), (float)
		// n.getX3());

		glTexCoord2f((float) texcoord2.getX1(), (float) texcoord.getX2());
		glVertex3f((float) ur.getX1(), (float) ur.getX2(), (float) ur.getX3());
		// GL11.glNormal3f( (float) n.getX1(), (float) n.getX2(), (float)
		// n.getX3());

		glTexCoord2f((float) texcoord2.getX1(), (float) texcoord2.getX2());
		glVertex3f((float) or.getX1(), (float) or.getX2(), (float) or.getX3());
		GL11.glNormal3f((float) n.getX1(), (float) n.getX2(), (float) n.getX3());
		if (!headless) {
			glEnd();
		}

		// Dreieck 2
		if (!headless) {
			glBegin(GL_TRIANGLES);
		}
		glTexCoord2f((float) texcoord2.getX1(), (float) texcoord2.getX2());
		glVertex3f((float) or.getX1(), (float) or.getX2(), (float) or.getX3());
		// GL11.glNormal3f( (float) n.getX1(), (float) n.getX2(), (float)
		// n.getX3());

		glTexCoord2f((float) texcoord.getX1(), (float) texcoord2.getX2());
		glVertex3f((float) ol.getX1(), (float) ol.getX2(), (float) ol.getX3());
		// GL11.glNormal3f( (float) n.getX1(), (float) n.getX2(), (float)
		// n.getX3());

		glTexCoord2f((float) texcoord.getX1(), (float) texcoord.getX2());
		glVertex3f((float) ul.getX1(), (float) ul.getX2(), (float) ul.getX3());
		GL11.glNormal3f((float) n.getX1(), (float) n.getX2(), (float) n.getX3());
		if (!headless) {
			glEnd();
		}

	}

	private static Vektor3D normale(Vektor3D ul, Vektor3D ur, Vektor3D or, Vektor3D ol) {
		Vektor3D normale;
		Vektor3D d1;
		Vektor3D d2;

		d1 = ol.sub(ur);
		d2 = or.sub(ul);

		normale = new Vektor3D((d2.getX2() * d1.getX3() - d1.getX2() * d2.getX3()), (d2.getX3() * d1.getX1() - d1
				.getX3()
				* d2.getX1()), (d2.getX1() * d1.getX2() - d1.getX1() * d2.getX2()));

		normale = normale.normierter();

		// if(normale.getX2()<0)
		// normale = normale.negiere();
		//		
		return normale;
	}

	public static void viereck_texCoord(Vektor3D pos, Vektor3D spann, Texture tex, int name, double alpha,
			Vektor2D von, Vektor2D bis) {
		// Wenn keine Textur, nur Farbe
		float x1 = (float) von.getX1();
		float x2 = (float) bis.getX1();
		float y1 = (float) von.getX2();
		float y2 = (float) bis.getX2();

		if (tex != null) {
			glEnable(GL_TEXTURE_2D);
			tex.bind();
		} else {
			glDisable(GL_TEXTURE_2D);
		}
		// alpha Wert anders als 1 --> Blending
		if (alpha != 1) {
			GL11.glEnable(GL11.GL_BLEND); // enable transparency
			// GL11.glEnable(GL11.GL_TEXTURE_2D); // so texture image will show
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			glColor4f((float) 1, (float) 1, (float) 1, (float) alpha);
		} else {
			GL11.glDisable(GL11.GL_BLEND); // enable transparency
		}
		// wurde aktuelles Element als letztes getroffen? --> ROT!
		// if (lastsel == i)
		// glColor3f(1, 0, 0);
		// else
		// glColor3f(1, 1, 1);
		glLoadName(name);
		glBegin(GL_QUADS);
		glTexCoord2f(x1, y1);
		glVertex3f((float) pos.getX1(), (float) pos.getX2(), (float) pos.getX3());
		glTexCoord2f(x2, y1);
		glVertex3f((float) pos.getX1() + (float) spann.getX1(), (float) pos.getX2(), (float) pos.getX3());
		glTexCoord2f(x2, y2);
		glVertex3f((float) pos.getX1() + (float) spann.getX1(), (float) pos.getX2() + (float) spann.getX2(),
				(float) pos.getX3() + (float) spann.getX3());
		glTexCoord2f(x1, y2);
		glVertex3f((float) pos.getX1(), (float) pos.getX2() + (float) spann.getX2(), (float) pos.getX3()
				+ (float) spann.getX3());
		glEnd();

	}

	public static void viereck_texCoord_light(Vektor3D pos, Vektor3D spann, Texture tex, Vektor2D von, Vektor2D bis) {
		// Wenn keine Textur, nur Farbe
		float x1 = (float) von.getX1();
		float x2 = (float) bis.getX1();
		float y1 = (float) von.getX2();
		float y2 = (float) bis.getX2();

		if (tex != null) {
			glEnable(GL_TEXTURE_2D);
			tex.bind();
		} else {
			glDisable(GL_TEXTURE_2D);
		}
		// alpha Wert anders als 1 --> Blending

		// wurde aktuelles Element als letztes getroffen? --> ROT!
		// if (lastsel == i)
		// glColor3f(1, 0, 0);
		// else
		// glColor3f(1, 1, 1);

		glBegin(GL_QUADS);
		glTexCoord2f(x1, y1);
		glVertex3f((float) pos.getX1(), (float) pos.getX2(), (float) pos.getX3());
		glTexCoord2f(x2, y1);
		glVertex3f((float) pos.getX1() + (float) spann.getX1(), (float) pos.getX2(), (float) pos.getX3());
		glTexCoord2f(x2, y2);
		glVertex3f((float) pos.getX1() + (float) spann.getX1(), (float) pos.getX2() + (float) spann.getX2(),
				(float) pos.getX3() + (float) spann.getX3());
		glTexCoord2f(x1, y2);
		glVertex3f((float) pos.getX1(), (float) pos.getX2() + (float) spann.getX2(), (float) pos.getX3()
				+ (float) spann.getX3());
		glEnd();

	}

	public static void viereck(Vektor2D pos, Vektor2D spann, Texture tex, int i) {
		viereck(new Vektor3D(pos, 0), new Vektor3D(spann, 0), tex, i);
	}

	public static void viereck(Vektor2D pos, Vektor2D spann) {
		viereck(new Vektor3D(pos, 0), new Vektor3D(spann, 0), null, 0);
	}

	public static void viereck(Vektor3D pos, Vektor3D spann) {
		viereck(pos, spann, null, 0);
	}

	public static void viereck(Vektor3D pos, Vektor3D spann, Texture tex, int i) {
		viereck(pos, spann, tex, i, 1);
	}

	public static void viereck(Vektor3D pos, Vektor3D spann, Texture tex) {
		viereck(pos, spann, tex, 0, 1);
	}

	public static void wuerfel(Vektor3D pos, Vektor3D spann, Texture tex) {
		wuerfel(pos, spann, tex, 0, 1);
	}

	public static void wuerfel(Vektor3D pos, Vektor3D spann) {
		wuerfel(pos, spann, null, 0, 1);
	}

	public static void wuerfel(Vektor3D pos, Vektor3D spann, Texture tex, int i) {
		wuerfel(pos, spann, tex, i, 1);
	}

	public static void wuerfel(Vektor3D pos, Vektor3D spann, Texture tex, int i, double alpha) {

		if (tex != null) {
			glEnable(GL_TEXTURE_2D);
			tex.bind();

		} else {
			glDisable(GL_TEXTURE_2D);
		}

		if (alpha != 1) {
			GL11.glEnable(GL11.GL_BLEND); // enable transparency
			// GL11.glEnable(GL11.GL_TEXTURE_2D); // so texture image will show
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			glColor4f((float) 1, (float) 1, (float) 1, (float) alpha);
		} else {
			GL11.glDisable(GL11.GL_BLEND); // enable transparency
		}
		// if (lastsel == i)
		// glColor3f(1, 0, 0);
		// else
		// glColor3f(1, 1, 1);

		glLoadName(i);

		glBegin(GL_QUADS);
		glTexCoord2f(0, 0);
		glVertex3f((float) pos.getX1(), (float) pos.getX2(), (float) pos.getX3());
		glTexCoord2f(1, 0);
		glVertex3f((float) pos.getX1() + (float) spann.getX1(), (float) pos.getX2(), (float) pos.getX3());
		glTexCoord2f(1, 1);
		glVertex3f((float) pos.getX1() + (float) spann.getX1(), (float) pos.getX2() + (float) spann.getX2(),
				(float) pos.getX3());
		glTexCoord2f(0, 1);
		glVertex3f((float) pos.getX1(), (float) pos.getX2() + (float) spann.getX2(), (float) pos.getX3());
		glTexCoord2f(0, 0);
		glVertex3f((float) pos.getX1(), (float) pos.getX2(), (float) pos.getX3() + (float) spann.getX3());
		glTexCoord2f(1, 0);
		glVertex3f((float) pos.getX1() + (float) spann.getX1(), (float) pos.getX2(), (float) pos.getX3()
				+ (float) spann.getX3());
		glTexCoord2f(1, 1);
		glVertex3f((float) pos.getX1() + (float) spann.getX1(), (float) pos.getX2() + (float) spann.getX2(),
				(float) pos.getX3() + (float) spann.getX3());
		glTexCoord2f(0, 1);
		glVertex3f((float) pos.getX1(), (float) pos.getX2() + (float) spann.getX2(), (float) pos.getX3()
				+ (float) spann.getX3());
		glTexCoord2f(0, 0);
		glVertex3f((float) pos.getX1(), (float) pos.getX2(), (float) pos.getX3());
		glTexCoord2f(1, 0);
		glVertex3f((float) pos.getX1(), (float) pos.getX2(), (float) pos.getX3() + (float) spann.getX3());
		glTexCoord2f(1, 1);
		glVertex3f((float) pos.getX1(), (float) pos.getX2() + (float) spann.getX2(), (float) pos.getX3()
				+ (float) spann.getX3());
		glTexCoord2f(0, 1);
		glVertex3f((float) pos.getX1(), (float) pos.getX2() + (float) spann.getX2(), (float) pos.getX3());
		glTexCoord2f(0, 0);
		glVertex3f((float) pos.getX1() + (float) spann.getX1(), (float) pos.getX2(), (float) pos.getX3());
		glTexCoord2f(1, 0);
		glVertex3f((float) pos.getX1() + (float) spann.getX1(), (float) pos.getX2(), (float) pos.getX3()
				+ (float) spann.getX3());
		glTexCoord2f(1, 1);
		glVertex3f((float) pos.getX1() + (float) spann.getX1(), (float) pos.getX2() + (float) spann.getX2(),
				(float) pos.getX3() + (float) spann.getX3());
		glTexCoord2f(0, 1);
		glVertex3f((float) pos.getX1() + (float) spann.getX1(), (float) pos.getX2() + (float) spann.getX2(),
				(float) pos.getX3());
		glTexCoord2f(0, 0);
		glVertex3f((float) pos.getX1(), (float) pos.getX2(), (float) pos.getX3() + (float) spann.getX3());
		glTexCoord2f(1, 0);
		glVertex3f((float) pos.getX1() + (float) spann.getX1(), (float) pos.getX2(), (float) pos.getX3()
				+ (float) spann.getX3());
		glTexCoord2f(1, 1);
		glVertex3f((float) pos.getX1() + (float) spann.getX1(), (float) pos.getX2(), (float) pos.getX3());
		glTexCoord2f(0, 1);
		glVertex3f((float) pos.getX1(), (float) pos.getX2(), (float) pos.getX3());
		glTexCoord2f(0, 0);
		glVertex3f((float) pos.getX1(), (float) pos.getX2() + (float) spann.getX2(), (float) pos.getX3()
				+ (float) spann.getX3());
		glTexCoord2f(1, 0);
		glVertex3f((float) pos.getX1() + (float) spann.getX1(), (float) pos.getX2() + (float) spann.getX2(),
				(float) pos.getX3() + (float) spann.getX3());
		glTexCoord2f(1, 1);
		glVertex3f((float) pos.getX1() + (float) spann.getX1(), (float) pos.getX2() + (float) spann.getX2(),
				(float) pos.getX3());
		glTexCoord2f(0, 1);
		glVertex3f((float) pos.getX1(), (float) pos.getX2() + (float) spann.getX2(), (float) pos.getX3());
		glEnd();

	}

	public static void wuerfelTexO(Vektor3D pos, Vektor3D spann, Texture texOben, Texture texSonst) {
		// OBEN
		OGL.viereck(pos.add(new Vektor3D(0, spann.getX2(), 0)), new Vektor3D(spann.getX1(), 0, spann.getX3()), texOben);
		// Unten
		OGL.viereck(pos.add(new Vektor3D(0, 0, 0)), new Vektor3D(spann.getX1(), 0, spann.getX3()), texSonst);
		// VORNE
		OGL.viereck(pos.add(new Vektor3D(0, 0, 0)), new Vektor3D(spann.getX1(), spann.getX2(), 0), texSonst);
		// Hinten
		OGL
				.viereck(pos.add(new Vektor3D(0, 0, spann.getX3())), new Vektor3D(spann.getX1(), spann.getX2(), 0),
						texSonst);

		OGL.verschieb(pos);
		OGL.rot(90, new Vektor3D(0, 1, 0));
		// Links
		OGL.viereck((new Vektor3D(0, 0, 0)), new Vektor3D(-spann.getX3(), spann.getX2(), 0), texSonst);
		OGL.rot(-90, new Vektor3D(0, 1, 0));

		OGL.verschieb(new Vektor3D(spann.getX1(), 0, 0));
		// rechts
		OGL.rot(-90, new Vektor3D(0, 1, 0));
		OGL.viereck((new Vektor3D(0, 0, 0)), new Vektor3D(spann.getX3(), spann.getX2(), 0), texSonst);
		OGL.rot(90, new Vektor3D(0, 1, 0));
		OGL.verschieb(new Vektor3D(spann.getX1(), 0, 0).negiere());
		OGL.verschieb(pos.negiere());
	}

	public static void wuerfel_freeform(Vektor3D vul, Vektor3D vur, Vektor3D vor, Vektor3D vol, Vektor3D hul,
			Vektor3D hur, Vektor3D hor, Vektor3D hol, Texture texOben, Texture texFront, Texture texLinks,
			Texture texRechts, Texture texUnten, Texture texHinten) {

		// Hinten
		OGL.viereck_freeform(hul, hur, hor, hol, texHinten, 0, 1);
		// Unten
		OGL.viereck_freeform(vul, vur, hur, hul, texUnten, 0, 1);

		// Links
		OGL.viereck_freeform(vul, hul, hol, vol, texLinks, 0, 1);

		// rechts
		OGL.viereck_freeform(vur, hur, hor, vor, texRechts, 0, 1);

		// OBEN
		OGL.viereck_freeform(vol, vor, hor, hol, texOben, 0, 1);

		// VORNE
		OGL.viereck_freeform(vul, vur, vor, vol, texFront, 0, 1);

	}

	/**
	 * Rotate the scene.
	 * 
	 * @param winkel
	 * @param achse
	 */
	public static void rot(double winkel, Vektor3D achse) {
		glRotatef((float) winkel, (float) achse.getX1(), (float) achse.getX2(), (float) achse.getX3());
	}

	/**
	 * Set the color of the scene.
	 * 
	 * @param color
	 */
	public static void setColor(Vektor3D color) {
		glColor3f((float) color.getX1(), (float) color.getX2(), (float) color.getX3());
	}

	/**
	 * Set the alpha value.
	 * 
	 * @param a
	 */
	public static void setAlpha(float a) {
		glColor4f((float) 1, (float) 1, (float) 1, a);
	}

	/**
	 * Translate the scene.
	 * 
	 * @param um
	 */
	public static void verschieb(Vektor3D um) {

		glTranslatef((float) um.getX1(), (float) um.getX2(), (float) um.getX3());
	}

	/**
	 * Scale the scene.
	 * 
	 * @param um
	 */
	public static void skaliere(Vektor3D um) {

		GL11.glScalef((float) um.getX1(), (float) um.getX2(), (float) um.getX3());
	}

	/**
	 * Start a render list.
	 * 
	 * @return The list identifier.
	 */
	public static int startlist() {
		int identifier;
		identifier = GL11.glGenLists(1); // Create a Display List

		GL11.glNewList(identifier, GL11.GL_COMPILE); // Start building a list

		return identifier;
	}

	/**
	 * End the current list.
	 */
	public static void endlist() {

		GL11.glEndList(); // Done building the display list
	}

	/**
	 * Call a list by its identifier.
	 * 
	 * @param identifier
	 *            The lists identifier.
	 */
	public static void calllist(int identifier) {

		GL11.glCallList(identifier);
	}

	/**
	 * Draw a sphere.
	 */
	public static void kugel(Vektor3D pos, double radius, double alpha, int teile) {
		makeSphere(pos, radius, null, alpha, teile, 0);
	}

	/**
	 * Draw a sphere.
	 */
	public static void kugel(Vektor3D pos, double radius, Texture tex, double alpha, int teile, int name) {
		makeSphere(pos, radius, tex, alpha, teile, name);
	}

	/**
	 * Draw a sphere.
	 */
	public static void kugel(Vektor3D pos, double radius) {
		makeSphere(pos, radius, null, 1, 32, 0);
	}

	/**
	 * Draw a sphere.
	 */
	private static void makeSphere(Vektor3D pos, double radius, Texture tex, double alpha, int teile, int name) {
		glLoadName(name);
		Sphere s = new Sphere(); // an LWJGL class for drawing sphere
		if (tex != null) {
			GL11.glEnable(GL11.GL_TEXTURE_2D); // so texture image will show
			tex.bind();
		} else
			GL11.glDisable(GL11.GL_TEXTURE_2D); // so texture image will not
		// show

		if (alpha != 1) {
			GL11.glEnable(GL11.GL_BLEND); // enable transparency

			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			glColor4f((float) 1, (float) 1, (float) 1, (float) alpha);
		} else {
			GL11.glDisable(GL11.GL_BLEND); // enable transparency
		}
		s.setTextureFlag(true); // generate texture coords
		OGL.verschieb(pos);
		OGL.rot(-90, new Vektor3D(1, 0, 0));
		if (alpha != 1)
			OGL.rot(180, new Vektor3D(0, 1, 0));
		s.draw((float) radius, teile, teile); // run GL commands to draw
		// sphere
		if (alpha != 1)
			OGL.rot(-180, new Vektor3D(0, 1, 0));
		OGL.rot(90, new Vektor3D(1, 0, 0));
		OGL.verschieb(pos.negiere());
	}

	public static void init(boolean fullscreen, OGLable o) throws Exception {
		init(fullscreen, o, 0, 0);
	}

	/**
	 * Initialisiert das Display und startet es.
	 * 
	 * @param fullscreen
	 *            Soll Vollbild geschaltet werden?
	 * @param o
	 *            Eine Instanz des aufrufenden Programms.
	 * @throws Exception
	 *             Throws only this Exception, when initializing the Display
	 *             failed.
	 */
	public static void init(boolean fullscreen, OGLable o, int width, int height) throws Exception {
		init1(fullscreen, width, height);
		try {

			o.once();
			run(o);

		} catch (Exception e) {
			// Catches Errors inside the game.
			e.printStackTrace(System.err);
			SuperMain.out(e);
			File fehlerfile = SuperMain.logFile;
			try {
				// PrintStream ps = new PrintStream(fehlerfile);
				// e.printStackTrace(ps);
				// ps.close();
				Scanner sc = new Scanner(fehlerfile);
				LinkedList<String> res = new LinkedList<String>();
				while (sc.hasNext()) {
					res.add(sc.nextLine());
				}

				SuperMain.generateEmail(res.toArray(new String[0]));

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				SuperMain.out(e);
			}

			Sys.alert(GAME_TITLE, "An error occured and the game will exit.");
		} finally {
			cleanup();

		}
		System.exit(0);
	}

	/**
	 * Initialise the game
	 * 
	 * @throws Exception
	 *             if init fails
	 */

	private static DisplayMode findDisplayMode(int width, int height, int bpp) throws LWJGLException {
		DisplayMode[] modes = Display.getAvailableDisplayModes();
		DisplayMode mode = null;

		for (int i = 0; i < modes.length; i++) {
			if ((modes[i].getBitsPerPixel() == bpp) || (mode == null)) {
				if ((modes[i].getWidth() == width) && (modes[i].getHeight() == height)) {
					mode = modes[i];
				}
			}
		}

		return mode;
	}

	public static void setOrtho() {
		// select projection matrix (controls view on screen)
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		// set ortho to same size as viewport, positioned at 0,0
		GL11.glOrtho(0, screenwh.getX1(), 0, screenwh.getX2(), -1, 1);
	}

	public static void showMsg(String msg) {

		JOptionPane jop = new JOptionPane();

		jop.setMessage(msg);

		JDialog dialog = jop.createDialog(null, "Message");

		dialog.setAlwaysOnTop(true);

		dialog.setModal(false);
		dialog.requestFocus();
		dialog.requestFocusInWindow();
		dialog.setVisible(true);
	}

	/**
	 * Set OpenGL to render in flat 2D (no perspective) on top of current scene.
	 * Preserve current projection and model views, and disable depth testing.
	 * Once Ortho is On, glTranslate() will take pixel coords as arguments, with
	 * the lower left corner 0,0 and the upper right corner 1024,768 (or
	 * whatever your screen size is).
	 * 
	 * @see setOrthoOff()
	 */
	public static void setOrthoOn() {
		// prepare to render in 2D
		GL11.glDisable(GL11.GL_DEPTH_TEST); // so text stays on top of scene
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix(); // preserve perspective view
		GL11.glLoadIdentity(); // clear the perspective matrix
		GL11.glOrtho(0, 0 + screenwh.getX1(), 0, 0 + screenwh.getX2(), -1, 1); // turn
		// on
		// 2D
		// mode
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix(); // Preserve the Modelview Matrix
		GL11.glLoadIdentity(); // clear the Modelview Matrix
	}

	/**
	 * Turn 2D mode off. Return the projection and model views to their
	 * preserved state that was saved when setOrthoOn() was called, and enable
	 * depth testing.
	 * 
	 * @see setOrthoOn()
	 */
	public static void setOrthoOff() {
		// restore the original positions and views
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_DEPTH_TEST); // turn Depth Testing back on
	}

	public static void enableDephTest() {
		GL11.glEnable(GL11.GL_DEPTH_TEST); // turn Depth Testing back on
	}

	public static void disableDephTest() {
		GL11.glDisable(GL11.GL_DEPTH_TEST); // turn Depth Testing back on
	}

	public static void init1(boolean fullscreen1) throws Exception {
		init1(fullscreen1, 0, 0);
	}

	public static void init1(boolean fullscreen1, int width, int height) throws Exception {
		// Create a fullscreen window with 1:1 orthographic 2D projection
		// (default)
		if (Display.isCreated())
			(Display.class).newInstance();
		Display.setTitle(GAME_TITLE);
		fullscreen = fullscreen1;
		int currentBpp = Display.getDisplayMode().getBitsPerPixel();
		if (SuperMain.nofilecreation) {
			currentBpp = 16;
		}
		// originalDisplayMode
		if (!fullscreen1) {
			originalDisplayMode = findDisplayMode(1024, 768, currentBpp);
			screenwh = new Vektor2D(1024, 768);
		} else {
			int screenSizew = (width == 0 ? (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() : width);
			int screenSizeh = (height == 0 ? (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() : height);
			originalDisplayMode = findDisplayMode(screenSizew, screenSizeh, currentBpp);
			screenwh = new Vektor2D(screenSizew, screenSizeh);

		}
		SuperMain.cm = OGL.screenwh.getX1() * 1 / 21;
		Display.setDisplayMode(originalDisplayMode);
		Display.setFullscreen(fullscreen1);

		// Enable vsync if we can (due to how OpenGL works, it cannot be
		// guarenteed to always work)

		Display.setVSyncEnabled(true);

		// Create default display of 640x480
		//
		try {
			Display.create();
		} catch (LWJGLException e) {
			// throws Exception when wrong drivers
			SuperMain.out(e);
			Sys.alert("Display Creating Exception (#OGL1098)",
					"Could not initialize display.\nPlease install lates graphic-card drivers.");

		}
		GL11.glInitNames(); // Initializes The Name Stack
		GL11.glPushName(0);
		glEnable(GL_TEXTURE_2D); // Enable Texture Mapping
		glShadeModel(GL_SMOOTH); // Enable Smooth Shading
		glClearColor(1.0f, 1.0f, 1.0f, 0.0f); // Black Background
		glClearDepth(1.0f); // Depth Buffer Setup
		glEnable(GL_DEPTH_TEST); // Enables Depth Testing
		glDepthFunc(GL_LEQUAL); // The Type Of Depth Testing To Do

		glMatrixMode(GL_PROJECTION); // Select The Projection Matrix
		glLoadIdentity(); // Reset The Projection Matrix

		// Calculate The Aspect Ratio Of The Window
		gluPerspective(45.0f, (float) screenwh.getX1() / (float) screenwh.getX2(), 0.1f, 1000.0f);
		glMatrixMode(GL_MODELVIEW); // Select The Modelview Matrix

		// Really Nice Perspective Calculations
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);

		viewport = new int[4];

		// This Sets The Array <viewport> To The Size And Location Of The Screen
		// Relative To The Window
		IntBuffer temp = ByteBuffer.allocateDirect(64).order(ByteOrder.nativeOrder()).asIntBuffer();
		temp.order();
		glGetInteger(GL_VIEWPORT, temp);
		temp.get(viewport);
	}

	/**
	 * Runs the game (the "main loop")
	 */
	private static void render1(OGLable o) {
		if (sel == 2) {
			sel = 0;
			return;
		} else if (sel == 1) {
			sel++;
		}
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // Clear The Screen
		// And The Depth
		// Buffer

		glLoadIdentity(); // Reset The Current Modelview Matrix
		// center square according to screen size
		glPushMatrix();
		GL11.glInitNames(); // Initializes The Name Stack
		GL11.glPushName(-1);
		o.render();
		glPopMatrix();
	}

	private static void run(OGLable o) {

		while (!finished) {
			// Always call Window.update(), all the time - it does some behind
			// the
			// scenes work, and also displays the rendered output
			Display.update();

			// Check for close requests
			if (Display.isCloseRequested()) {
				finished = true;
			}

			// The window is in the foreground, so we should play the game
			else if (Display.isActive()) {
				o.logic();
				// clear the screen
				render1(o);

				Display.sync(FRAMERATE);
			}

			// The window is not in the foreground, so we can allow other stuff
			// to run and
			// infrequently update
			else {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
				}
				o.logic();

				// Only bother rendering if the window is visible or dirty
				if (Display.isVisible() || Display.isDirty()) {
					render1(o);
				}
			}
		}
	}

	/**
	 * Do any game-specific cleanup
	 */
	private static void cleanup() {
		// Close the window
		Display.destroy();
	}

	public static void rauszoom() {
		// perfect!!!!!!!!!!!!!!!
		OGL.verschieb(new Vektor3D(-40.00, -30.00, -72.50));// 80 x 60 Felder
		// Verschiebung

	}

	public static void resetzoom() {
		// perfect!!!!!!!!!!!!!!!
		OGL.verschieb(new Vektor3D(40.00, 30.00, 72.50));// 80 x 60 Felder
		// Verschiebung

	}

	public static void Hintergrund(Texture tex) {
		Vektor3D pos = new Vektor3D(0, 0, -1);
		Vektor3D spann = new Vektor3D(screenwh.getX1(), screenwh.getX2(), 0);
		Hintergrund(tex, pos, spann);
	}

	public static void Hintergrund(Texture tex, Vektor3D pos, Vektor3D spann) {
		OGL.setOrthoOn();
		OGL.viereck(pos, spann, tex, 0);
		OGL.setOrthoOff();
	}

	public static int fpscount() {
		// im Thread schreiben
		int old = fps + 1;
		fps++;
		m.ende();
		if (m.res() % 1000 == 0) {

			fps = 0;
			return old;
		}
		return 0;
	}

	public static void enableBlend() {
		GL11.glEnable(GL11.GL_BLEND); // enable transparency
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	public static void disableBlend() {
		GL11.glDisable(GL11.GL_BLEND); // enable transparency

	}

	public static void fpsinit() {
		m = new Zeit_messen();
		m.start();
	}

	public static int selection(int mouse_x, int mouse_y, OGLable o) { // This
		if (mouse_x < 0 || mouse_y < 0)
			return -1;
		int buffer[] = new int[512]; // Set Up A Selection Buffer
		int hits; // The Number Of Objects That We Selected

		IntBuffer temp = BufferUtil.newIntBuffer(512);
		glSelectBuffer(temp); // Tell OpenGL To Use Our Array For Selection

		// Puts OpenGL In Selection Mode. Nothing Will Be Drawn. Object ID's and
		// Extents Are Stored In The Buffer.
		glRenderMode(GL_SELECT);

		glInitNames(); // Initializes The Name Stack
		glPushName(0); // Push 0 (At Least One Entry) Onto The Stack

		glMatrixMode(GL_PROJECTION); // Selects The Projection Matrix
		glPushMatrix(); // Push The Projection Matrix
		glLoadIdentity(); // Resets The Matrix

		// This Creates A Matrix That Will Zoom Up To A Small Portion Of The
		// Screen, Where The Mouse Is.
		gluPickMatrix((float) mouse_x, (float) (/* viewport[3] - */mouse_y), 1.0f, 1.0f, IntBuffer.wrap(viewport));

		// Apply The Perspective Matrix
		gluPerspective(45.0f, (float) (viewport[2] - viewport[0]) / (float) (viewport[3] - viewport[1]), 0.001f, 300.0f);
		glMatrixMode(GL_MODELVIEW); // Select The Modelview Matrix
		glLoadIdentity();
		// if (o instanceof GameState)
		// rauszoom();

		o.render();

		// Render The Targets To The Selection Buffer
		glMatrixMode(GL_PROJECTION); // Select The Projection Matrix
		glPopMatrix(); // Pop The Projection Matrix
		glMatrixMode(GL_MODELVIEW); // Select The Modelview Matrix
		hits = glRenderMode(GL_RENDER); // Switch To Render Mode, Find Out How
		// Many
		temp.get(buffer); // Objects Were Drawn Where The Mouse Was
		if (hits > 0) { // If There Were More Than 0 Hits
			int choose = buffer[3]; // Make Our Selection The First Object
			int depth = buffer[1]; // Store How Far Away It Is

			for (int i = 1; i < hits; i++) { // Loop Through All The Detected
				// Hits
				// If This Object Is Closer To Us Than The One We Have Selected
				if (buffer[i * 4 + 1] < (int) depth) {
					choose = buffer[i * 4 + 3]; // Select The Closer Object
					depth = buffer[i * 4 + 1]; // Store How Far Away It Is
				}
			}
			lastsel = choose;
			return choose;

		}
		return -1;
	}
}

class Kreis {
	Vektor3D position, dimension;
	TextureLoader loader;
	Texture texture;
	int teile = 32;
	boolean Tex_entspannen = true;

	public Kreis(Vektor3D position, Vektor3D dimension, int teile) {
		this(position, dimension, null, teile);
	}

	public Kreis(Vektor3D position, Vektor3D dimension) {
		this(position, dimension, null, 32);
	}

	public Kreis(Vektor3D position, Vektor3D dimension, Texture texture, int teile) {
		this.position = position;
		this.dimension = dimension;
		this.texture = texture;
		this.teile = teile;
	}

	public Kreis(Vektor3D position, Vektor3D dimension, Texture texture) {
		this(position, dimension, texture, 32);
	}

	public boolean isTex_entspannen() {
		return Tex_entspannen;
	}

	public void setTex_entspannen(boolean tex_entspannen) {
		Tex_entspannen = tex_entspannen;
	}

	public Vektor3D getDimension() {
		return dimension;
	}

	public void setDimension(Vektor3D dimension) {
		this.dimension = dimension;
	}

	public Vektor3D getPosition() {
		return position;
	}

	public void setPosition(Vektor3D position) {
		this.position = position;
	}

	public int getTeile() {
		return teile;
	}

	public void setTeile(int teile) {
		this.teile = teile;
	}

	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}

	public void render() {

		OGL.verschieb(this.getPosition());
		for (int x = 0; x < teile; x++) {
			for (int y = 0; y < 1; y++) {
				float x1 = (float) (x + 0.0) / teile;
				float x2 = (float) (x + 1.0) / teile;

				double angle2 = 360 / (teile + 0.0) * x;
				double angle = 360 / (teile + 0.0) * (x + 1);
				double korrek = 180. / teile;

				OGL.rot(angle, new Vektor3D(0, 1, 0));
				float y1 = (float) angle / 181;
				float y2 = (float) angle / 181;

				float r = (float) this.getDimension().getX1();
				double d = r * 2 * Math.PI;

				double width = d / (teile - 1.0);
				// double height = this.getDimension().getX2();

				x1 = 0.5f + (float) (Math.sin(Math.toRadians(angle + korrek)) / 2.);
				x2 = 0.5f + (float) (Math.sin(Math.toRadians(angle2 + korrek)) / 2.);
				y1 = 0.5f + (float) (Math.cos(Math.toRadians(angle + korrek)) / 2.);
				y2 = 0.5f + (float) (Math.cos(Math.toRadians(angle2 + korrek)) / 2.);

				if (!this.Tex_entspannen) {
					int quadrant = 0;
					if (angle2 < 45 || angle2 >= 315) {
						quadrant = 1;
						y1 = 1;
						y2 = 1;
						x1 = 0.5f + (float) (angle / 90);
						x2 = 0.5f + (float) (angle2 / 90);
						if (angle2 >= 315) {
							x1 = (float) ((angle - 315) / 90);
							x2 = (float) ((angle2 - 315) / 90);
						}
					} else if (angle2 < 135) {
						quadrant = 2;
						y1 = 1f - (float) ((angle - 45) / 90);
						y2 = 1f - (float) ((angle2 - 45) / 90);
						x1 = 1;
						x2 = 1;
					} else if (angle2 < 225) {
						quadrant = 2;
						x1 = 1f - (float) ((angle - 135) / 90);
						x2 = 1f - (float) ((angle2 - 135) / 90);
						y1 = 0;
						y2 = 0;
					} else if (angle2 < 315) {
						quadrant = 2;
						x1 = 0;
						x2 = 0;
						y1 = (float) ((angle - 225) / 90);
						y2 = (float) ((angle2 - 225) / 90);
					}
				}

				if (this.texture != null) {
					glEnable(GL_TEXTURE_2D);
					this.texture.bind();
				} else {
					glDisable(GL_TEXTURE_2D);
				}
				glBegin(GL_QUADS);
				glTexCoord2f(0.5f, 0.5f);
				// unten links
				glVertex3f(0, 0, 0);
				glTexCoord2f(x1, y1);
				// unten rechts
				glVertex3f((float) (width / 2.), 0, r);
				glTexCoord2f(x2, y2);
				// oben rechts
				glVertex3f(-(float) (width / 2.), 0, r);
				glTexCoord2f(0.5f, 0.5f);
				// oben links
				glVertex3f(0, 0, 0);
				glEnd();
				OGL.rot(-angle, new Vektor3D(0, 1, 0));
			}
		}
		OGL.verschieb(this.getPosition().negiere());

	}

}
