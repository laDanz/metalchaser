package glmodel;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Read an OBJ file and load it into a GL_Object. GL_Object is a basic mesh
 * object that holds vertex and triangle data. The GL_OBJ_Reader loads data
 * pretty much as it is in the OBJ, and this Importer converts the
 * idiosyncracies of the OBJ format into a straightforward vert/triangle
 * structure.
 * 
 * NOTE: OBJ files can contain polygon faces. The GL_Object holds only
 * triangles, so all faces will be converted to triangles.
 * 
 * NOTE: The importer ignores groups, and treats the entire model as one group.
 */
public class GL_OBJ_Importer {
	private GL_OBJ_Reader reader = null;
	private GL_Mesh mesh = null;

	public GL_OBJ_Importer() {
	}

	public GL_Mesh importFromStream(InputStream inStream) {
		// System.out.println("importFromStream(): Load object from OBJ
		// stream...");
		reader = new GL_OBJ_Reader(inStream);
		// System.out.println("importFromStream(): models has "
		// + reader.faces.size() + " faces and " + reader.vertices.size()
		// + " vertices.");
		return makeMeshObject(reader.vertices, reader.textureCoords, reader.normals, reader.faces);
	}

	/**
	 * create a GL_Object (mesh object) from the data read by a 3DS_Reader
	 * 
	 * @param verts
	 *            ArrayList of vertices
	 * @param txtrs
	 *            ArrayList of texture coordinates
	 * @param norms
	 *            ArrayList of normal
	 * @param faces
	 *            ArrayList of Face objects (triangles)
	 * @return
	 */
	public GL_Mesh makeMeshObject(ArrayList verts, ArrayList txtrs, ArrayList norms, ArrayList faces) {

		// grab the loaded data
		mesh = new GL_Mesh(); // mesh object
		mesh.name = "OBJ";

		// add verts to GL_object
		for (int i = 0; i < verts.size(); i++) {
			float[] coords = (float[]) verts.get(i);
			mesh.addVertex(coords[0], coords[1], coords[2]);
		}

		// add triangles to GL_object. OBJ "face" may be a triangle,
		// quad or polygon. Convert all faces to triangles.
		for (int i = 0; i < faces.size(); i++) {
			Face face = (Face) faces.get(i);
			// put verts, normals, texture coords into triangle(s)
			if (face.vertexIDs.length == 3) {
				addTriangle(mesh, face, txtrs, norms, 0, 1, 2);
			} else if (face.vertexIDs.length == 4) {
				// convert quad to two triangles
				addTriangle(mesh, face, txtrs, norms, 0, 1, 2);
				addTriangle(mesh, face, txtrs, norms, 0, 2, 3);
			} else {
				// convert polygon to triangle fan, with first vertex (0)
				// at center: 0,1,2 0,2,3 0,3,4 0,4,5
				for (int n = 0; n < face.vertexIDs.length - 2; n++) {
					addTriangle(mesh, face, txtrs, norms, 0, n + 1, n + 2);
				}
			}
		}

		// optimize the GL_Object
		mesh.rebuild();

		// if no normals were loaded, generate some
		if (norms.size() == 0) {
			mesh.regenerateNormals();
		}

		return mesh;
	}

	/**
	 * Add a new triangle to the GL_Object. This assumes that the vertices have
	 * already been added to the GL_Object, in the same order that they were in
	 * the OBJ.
	 * 
	 * @param obj
	 *            GL_Object
	 * @param face
	 *            a face from the OBJ file
	 * @param txtrs
	 *            ArrayList of texture coords from the OBJ file
	 * @param norms
	 *            ArrayList of normals from the OBJ file
	 * @param v1
	 *            vertices to use for the triangle (face may have >3 verts)
	 * @param v2
	 * @param v3
	 * @return
	 */
	public GL_Triangle addTriangle(GL_Mesh obj, Face face, ArrayList txtrs, ArrayList norms, int v1, int v2, int v3) {
		// An OBJ face may have many vertices (can be a polygon).
		// Make a new triangle with the specified three verts.
		GL_Triangle t = new GL_Triangle(obj.vertex(face.vertexIDs[v1]), obj.vertex(face.vertexIDs[v2]), obj
				.vertex(face.vertexIDs[v3]));

		// put texture coords into triangle
		if (txtrs.size() > 0) { // if texture coords were loaded
			float[] uvw;
			uvw = (float[]) txtrs.get(face.textureIDs[v1]); // txtr coord for
			// vert 1
			t.uvw1 = new GL_Vector(uvw[0], uvw[1], uvw[2]);
			uvw = (float[]) txtrs.get(face.textureIDs[v2]); // txtr coord for
			// vert 2
			t.uvw2 = new GL_Vector(uvw[0], uvw[1], uvw[2]);
			uvw = (float[]) txtrs.get(face.textureIDs[v3]); // txtr coord for
			// vert 3
			t.uvw3 = new GL_Vector(uvw[0], uvw[1], uvw[2]);
		}

		// put normals into triangle (NOTE: normalID can be -1!!! could barf
		// here!!!)
		if (norms.size() > 0) { // if normals were loaded
			float[] norm;
			norm = (float[]) norms.get(face.normalIDs[v1]); // normal for vert 1
			t.norm1 = new GL_Vector(norm[0], norm[1], norm[2]);
			norm = (float[]) norms.get(face.normalIDs[v2]); // normal for vert 2
			t.norm2 = new GL_Vector(norm[0], norm[1], norm[2]);
			norm = (float[]) norms.get(face.normalIDs[v3]); // normal for vert 3
			t.norm3 = new GL_Vector(norm[0], norm[1], norm[2]);
		}

		// add triangle to GL_object
		mesh.addTriangle(t);

		return t;
	}
}