package glmodel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Describes a triangular face. Holds references to three vertices, their
 * normals and texture coodinates. Vertex normals are stored here and not in the
 * Vertex object because a vertex may be shared between two or more faces, and
 * the faces may have very different normals (ie. if the faces are at a 90
 * degree angle and make a sharp edge).
 * 
 * To smooth normals and preserve sharp edges, the Triangle object holds the
 * neighboring triangles for each vert. Based on the angles between this
 * triangle and its neighbors, the smoothing algorithm can decide whether to
 * smooth the normals across the triangles, or to use the face normal to create
 * a hard edge.
 */
public class GL_Triangle implements Serializable {

	private static final long serialVersionUID = 1L;

	public GL_Vertex p1; // first vertex
	public GL_Vertex p2; // second vertex
	public GL_Vertex p3; // third vertex

	public GL_Vector norm1; // normal at vert 1
	public GL_Vector norm2; // normal at vert 2
	public GL_Vector norm3; // normal at vert 3

	public GL_Vector uvw1 = new GL_Vector();; // texture coord at vert 1
												// MJN!!!
	public GL_Vector uvw2 = new GL_Vector();; // texture coord at vert 2
												// MJN!!!
	public GL_Vector uvw3 = new GL_Vector();; // texture coord at vert 3
												// MJN!!!

	public ArrayList neighborsP1 = new ArrayList(); // Neighbor triangles of
													// vertex1
	public ArrayList neighborsP2 = new ArrayList(); // Neighbor triangles of
													// vertex2
	public ArrayList neighborsP3 = new ArrayList(); // Neighbor triangles of
													// vertex3

	public GL_Vector n = new GL_Vector(); // Normal vector of flat triangle
											// //MJN!!! 'new' the vector here,
											// then we'll reuse it in
											// regenerateNormalsMJN() instead of
											// newing it again
	public GL_Vector wn = new GL_Vector(); // temp work var
	public float Zdepth = 0f; // screen Z depth
	public int ID = 0;

	public GL_Triangle(GL_Vertex a, GL_Vertex b, GL_Vertex c) {
		p1 = a;
		p2 = b;
		p3 = c;
	}

	/**
	 * Calculate the face normal for this triangle
	 */
	public void recalcFaceNormal() {
		GL_Vector.getNormalMJN(p1.pos, p2.pos, p3.pos, n);
	}

	/**
	 * Recalculate the vertex normal, by averagin the normals of the neighboring
	 * triangles. The neighbor list holds only triangles that we want to average
	 * with this vertex.
	 * 
	 * @see GL_Object.registerNeighbors()
	 * @param neighbors
	 *            neighboring triangles for this vert
	 * @return vertex normal
	 */
	public GL_Vector recalcVertexNormal(ArrayList neighbors) // MJN!!!!
																// altered to
																// smooth verts
																// and leave
																// edges
	{
		float nx = 0;
		float ny = 0;
		float nz = 0;
		GL_Triangle tri;
		// for each neighbor triangle, average the normals
		for (int i = 0; i < neighbors.size(); i++) {
			tri = (GL_Triangle) neighbors.get(i);
			tri.getWeightedNormalMJN(wn); // MJN!!!! pass answer vector in
											// here
			nx += wn.x;
			ny += wn.y;
			nz += wn.z;
		}
		GL_Vector vertn = new GL_Vector(nx, ny, nz);
		vertn.normalize();
		return vertn;
	}

	public void getWeightedNormalMJN(GL_Vector result) // MJN!!! pass in a
														// vector to hold answer
	{
		GL_Vector.vectorProductMJN(p1.pos, p2.pos, p3.pos, result);
	}

	public GL_Vector getCenter() {
		float cx = (p1.pos.x + p2.pos.x + p3.pos.x) / 3;
		float cy = (p1.pos.y + p2.pos.y + p3.pos.y) / 3;
		float cz = (p1.pos.z + p2.pos.z + p3.pos.z) / 3;
		return new GL_Vector(cx, cy, cz);
	}

	public void resetNeighbors() {
		neighborsP1.clear();
		neighborsP2.clear();
		neighborsP3.clear();
	}

	/**
	 * Calculate average screen Z depth of this triangle. This function requires
	 * that the vertex screen positions have been set (vertex.posS).
	 * 
	 * @see GL_Mesh.project()
	 */
	public void calcZdepth() {
		Zdepth = (p1.posS.z + p2.posS.z + p3.posS.z) / 3f;
	}

	/**
	 * Return true if two triangles should be smoothed as one surface. cos_angle
	 * is the minumum angle for smoothing. If the angle between the faces is >
	 * cos_angle, then the faces are considered to be a continuous surface. Ie.
	 * 90 degrees is a sharp corner, 180 degrees is a flat surface.
	 */
	public static boolean onSameSurface(GL_Triangle t1, GL_Triangle t2, float cos_angle) {
		float dot = GL_Vector.dotProduct(t1.n, t2.n);
		// System.out.println("surface: compare dot=" +dot + " cos-angle=" +
		// cos_angle + " return " + (dot > cos_angle));
		return (dot > cos_angle);
	}

}