package Classes;

import static org.lwjgl.opengl.GL11.glLoadName;
import glmodel.FILE;
import glmodel.GLImage;
import glmodel.GL_3DS_Importer;
import glmodel.GL_Mesh;
import glmodel.GL_OBJ_Importer;
import glmodel.GL_Triangle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Vector;

import main.SuperMain;

import org.lwjgl.opengl.GL11;

/**
 * Class for easy handling of 3D objects.
 * 
 */
public class Object3D {

	private Texture textureHandle;// FIXME
	private GL_Mesh obj;
	public int listhandle;
	boolean islisted = false;
	private Vektor3D dimension;
	private Vektor3D mittelPkt;
	// Hier werden die RenderListen bereits geladener Objekte gespeichert
	// zusammen mit den dimensions+MittelPunkt informationen
	static HashMap<Integer, Vector<Object>> loadedLists;

	String filename;
	String texture_path;

	private boolean fur_liste_freigegeben = true;

	// boolean translucent = false;
	/**
	 * Default constructor.<br>
	 * Loads an object out of an file and a texture.
	 * 
	 * @param filename
	 *            The object file location.
	 * @param texture_path
	 *            The texture file location.
	 */
	public Object3D(final String filename, final String texture_path) {
		this.filename = filename;
		this.texture_path = texture_path;
		obj = null;
		// if (loadedObjects == null)
		// loadedObjects = new HashMap<String, GL_Mesh>();
		if (loadedLists == null)
			loadedLists = new HashMap<Integer, Vector<Object>>();
		if (texture_path == null) {
			textureHandle = null;
			fur_liste_freigegeben = true;
		} else {

			try {
				textureHandle = SuperMain.loadTex(texture_path);
				fur_liste_freigegeben = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				SuperMain.out(e);
			}

		}

		if (checkList()) {

		} else if (!main.Editor.isActualGameState()) {

			// loadFromFile();
			// loadedObjects.put(filename, obj);
			// render();
			SuperMain.toRender.add(this);
		} else {
			// kann leider net sofort rendern...
			SuperMain.toRender.add(this);
		}

	}

	/**
	 * Simple Getter.
	 * 
	 * @return
	 */
	public Vektor3D getDimension() {
		return dimension;
	}

	/**
	 * Simple Getter.
	 * 
	 * @return
	 */
	public Vektor3D getMittelPkt() {
		return mittelPkt;
	}

	/**
	 * Load the Object out of a .obj or .3ds file.
	 */
	public void loadFromFile() {
		if (checkList())
			return;
		// SuperMain.out("Importiere " + filename);
		if (filename.toLowerCase().endsWith("3ds")) {
			GL_3DS_Importer import3DS = new GL_3DS_Importer();
			InputStream in3ds = FILE.getInputStream(filename);
			obj = import3DS.importFromStream(in3ds);
		} else if (filename.toLowerCase().endsWith("obj") || filename.toLowerCase().endsWith("mco")) {
			// check for precompiled version
			String precompiled_path = filename.replace(".mco", ".cmco").replace(".obj", ".cmco");
			File f = new File(precompiled_path);
			if (f.exists()) {
				try {
					ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
					obj = (GL_Mesh) ois.readObject();
					ois.close();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {

				// load it from raw data
				try {
					GL_OBJ_Importer importOBJ = new GL_OBJ_Importer();
					InputStream in = FILE.getInputStream(filename);
					obj = importOBJ.importFromStream(in);
					// and save it to precompiled but only mco files!!!
					if (!filename.toLowerCase().endsWith("obj")) {

						f.createNewFile();
						ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
						oos.writeObject(obj);
						oos.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
					SuperMain.out(e);
					org.lwjgl.Sys.alert("Fehler",
							"Fehler beim obj importieren...\n knnte ein texturkoordinatenproblem sein");
				}
			}
		} else {
			throw new RuntimeException("Format wird nicht Untersttzt!");
		}
		saveDimension();
	}

	private boolean checkList() {
		// nochmal prfen obs vorhanden ist!
		Vector v = loadedLists.get(filename.hashCode() + (texture_path == null ? 0 : texture_path.hashCode()));
		if (v != null) {
			int i = (Integer) v.get(0);
			islisted = true;
			listhandle = i;
			Vektor3D dim = new v3((Double) v.get(1), (Double) v.get(2), (Double) v.get(3));
			Vektor3D mit = new v3((Double) v.get(4), (Double) v.get(5), (Double) v.get(6));
			this.dimension = dim;
			this.mittelPkt = mit;
			obj = (GL_Mesh) v.get(7);

			return true;
		}
		return false;
	}

	private void saveDimension() {
		dimension = new v3(obj.getDimension().x, obj.getDimension().y, obj.getDimension().z);
		SuperMain.out("Dimension: " + dimension);
		mittelPkt = new v3(obj.getCenter().x, obj.getCenter().y, obj.getCenter().z);

	}

	/**
	 * Render the Object.
	 */
	public void render() {
		render(0);
	}

	/**
	 * Render the object.
	 * 
	 * @param name
	 *            A name for the object.
	 */
	public void render(int name) {
		if (getDimension() != null) {
			// etwas nach oben schieben
			OGL.verschieb(new v3(0, getDimension().getX2() / 2., 0));
			OGL.verschieb(new v3(0, -getMittelPkt().getX2(), 0));
		}

		if (textureHandle != null)
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		glLoadName(name);
		if (islisted) {
			OGL.calllist(listhandle);
		} else if (obj != null) {
			if (fur_liste_freigegeben) {
				islisted = true;
				listhandle = OGL.startlist();

				renderMesh(obj, textureHandle);

				OGL.endlist();
				zurListeAdden();
			}
		}
		if (getDimension() != null) {
			// etwas nach oben schieben
			OGL.verschieb(new v3(0, -getMittelPkt().getX2(), 0).negiere());
			OGL.verschieb(new v3(0, getDimension().getX2() / 2., 0).negiere());
		}
	}

	private void zurListeAdden() {
		Vector<Object> v = new Vector<Object>();
		v.add(0, listhandle);
		v.add(1, getDimension().getX1());
		v.add(2, getDimension().getX2());
		v.add(3, getDimension().getX3());
		v.add(4, getMittelPkt().getX1());
		v.add(5, getMittelPkt().getX2());
		v.add(6, getMittelPkt().getX3());
		v.add(7, obj);
		loadedLists.put(this.filename.hashCode() + (texture_path == null ? 0 : this.texture_path.hashCode()), v);
		// obj file brauch ich an dieser stelle nicht mehr
		// obj = null;
		System.gc();

	}

	private float tileFactorHoriz = 1f;
	private float tileFactorVert = 1f;

	/**
	 * Render mesh with normals and texture coordinates. Loops through all
	 * triangles in the mesh object.
	 * 
	 * Several triangles may refer to the same vertex, but each face can have
	 * different normals for that vertex. This allows for sharp edges between
	 * faces.
	 * 
	 * @param o
	 *            mesh object to render
	 */
	private void renderMesh(GL_Mesh o, Texture textureHandle) {// FIXME
		GL_Triangle t;
		if (textureHandle != null)
			// FIXMEGL11.glBindTexture(GL11.GL_TEXTURE_2D, textureHandle);
			textureHandle.bind();
		GL11.glBegin(GL11.GL_TRIANGLES);
		for (int j = 0; j < o.triangles.length; j++) { // draw all triangles in
			// object
			t = o.triangles[j];

			GL11.glTexCoord2f(tileFactorHoriz * t.uvw1.x, tileFactorVert * t.uvw1.y);
			GL11.glNormal3f(t.norm1.x, t.norm1.y, t.norm1.z);
			GL11.glVertex3f((float) t.p1.pos.x, (float) t.p1.pos.y, (float) t.p1.pos.z);

			GL11.glTexCoord2f(tileFactorHoriz * t.uvw2.x, tileFactorVert * t.uvw2.y);
			GL11.glNormal3f(t.norm2.x, t.norm2.y, t.norm2.z);
			GL11.glVertex3f((float) t.p2.pos.x, (float) t.p2.pos.y, (float) t.p2.pos.z);

			GL11.glTexCoord2f(tileFactorHoriz * t.uvw3.x, tileFactorVert * t.uvw3.y);
			GL11.glNormal3f(t.norm3.x, t.norm3.y, t.norm3.z);
			GL11.glVertex3f((float) t.p3.pos.x, (float) t.p3.pos.y, (float) t.p3.pos.z);
		}
		GL11.glEnd();
	}

	/**
	 * Create a texture from the given image.
	 */
	private static int makeTexture(GLImage textureImg) {
		if (textureImg == null) {
			return 0;
		} else {
			return makeTexture(textureImg.pixelBuffer, textureImg.w, textureImg.h);
		}
	}

	private static int makeTexture(ByteBuffer pixels, int w, int h) {
		// get a new empty texture
		int textureHandle = allocateTexture();
		// 'select' the new texture by it's handle
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureHandle);
		// set texture parameters
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR); // GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR); // GL11.GL_NEAREST);
		// Create the texture from pixels
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, w, h, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);
		return textureHandle;
	}

	private static int allocateTexture() {
		IntBuffer textureHandle = allocInts(1);
		GL11.glGenTextures(textureHandle);
		return textureHandle.get(0);
	}

	private static final int SIZE_INT = 4;

	private static IntBuffer allocInts(int howmany) {
		return ByteBuffer.allocateDirect(howmany * SIZE_INT).order(ByteOrder.nativeOrder()).asIntBuffer();
	}

	boolean SchattenisListed = false;
	int schatten_list;

	/**
	 * Tries to render a shadow for the object.
	 * 
	 * @param o
	 *            The Object.
	 */
	public void renderSchatten(Objekt o) {
		renderSchatten(o, false);
	}

	/**
	 * Tries to render a shadow for the object.
	 * 
	 * @param o
	 *            The object.
	 * @param force_render
	 *            Should rendering be forced, or should a list be used.
	 */
	public void renderSchatten(Objekt o, boolean force_render) {
		if (SchattenisListed && !force_render) {
			OGL.calllist(schatten_list);
		} else {
			if (obj == null)
				return;
			schatten_list = OGL.startlist();
			renderSchatten((o instanceof RotateAble ? ((RotateAble) o).getDrehwinkel() : 0), o.getPosition());
			OGL.endlist();
			SchattenisListed = true;
		}

	}

	private void renderSchatten(double wink, Vektor3D position) {
		GL_Triangle t;
		float x;
		float z;

		// OGL.rot(-wink, v3.y_axis);
		OGL.verschieb(position.negiere());
		float h0 = obj.min().y;
		float dh = 0.1f - (float) position.getX2();
		GL11.glColor4f(0, 0, 0, 1.5f);
		GL11.glBegin(GL11.GL_TRIANGLES);
		for (int j = 0; j < obj.triangles.length; j++) { // draw all
			// triangles in
			// object

			t = obj.triangles[j];

			x = (float) (t.p1.pos.x + Math.cos(Math.toRadians(wink)) * (t.p1.pos.y - h0) + position.getX1());
			z = (float) (t.p1.pos.z + Math.sin(Math.toRadians(wink)) * (t.p1.pos.y - h0) + position.getX3());
			// GL11.glTexCoord2f(tileFactorHoriz * t.uvw1.x, tileFactorVert *
			// t.uvw1.y);
			// GL11.glNormal3f(t.norm1.x, t.norm1.y, t.norm1.z);
			GL11.glVertex3f(x, (float) SuperMain.level.getHeight(x, z) + dh, z);

			x = (float) (t.p2.pos.x + Math.cos(Math.toRadians(wink)) * (t.p2.pos.y - h0) + position.getX1());
			z = (float) (t.p2.pos.z + Math.sin(Math.toRadians(wink)) * (t.p2.pos.y - h0) + position.getX3());
			// GL11.glTexCoord2f(tileFactorHoriz * t.uvw2.x, tileFactorVert *
			// t.uvw2.y);
			// GL11.glNormal3f(t.norm2.x, t.norm2.y, t.norm2.z);
			GL11.glVertex3f(x, (float) SuperMain.level.getHeight(x, z) + dh, z);

			x = (float) (t.p3.pos.x + Math.cos(Math.toRadians(wink)) * (t.p3.pos.y - h0) + position.getX1());
			z = (float) (t.p3.pos.z + Math.sin(Math.toRadians(wink)) * (t.p3.pos.y - h0) + position.getX3());
			// GL11.glTexCoord2f(tileFactorHoriz * t.uvw3.x, tileFactorVert *
			// t.uvw3.y);
			// GL11.glNormal3f(t.norm3.x, t.norm3.y, t.norm3.z);
			GL11.glVertex3f(x, (float) SuperMain.level.getHeight(x, z) + dh, z);
		}
		GL11.glEnd();
		GL11.glColor4f(1f, 1f, 1f, 1f);
		OGL.verschieb(position);
		// OGL.rot(wink, v3.y_axis);
	}

}
