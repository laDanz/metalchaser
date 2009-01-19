package glmodel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Vertex contains an xyz position, and a list of neighboring triangles. Normals
 * and texture coordinates are stored in the GL_Triangle object (except for u,v
 * which are stored here till I optimize the 3DS loader).
 * 
 * The "neighbor" triangle list holds all triangles that contains this vertex.
 * Not to be confused with the GL_Triangle.neighborsP1, GL_Triangle.neighborsP2,
 * etc. that contain only neighbors that should be smoothed into the given
 * triangle.
 */
public class GL_Vertex implements Serializable {

	private static final long serialVersionUID = 1L;

	public GL_Vector pos = new GL_Vector(); // xyz coordinate of vertex
	public GL_Vector posS = new GL_Vector(); // xyz Screen coords of
												// projected vertex

	// public float u = 0; // Texture x-coordinate (used by 3DS loader, not OBJ)
	// public float v = 0; // Texture y-coordinate (used by 3DS loader, not OBJ)

	public int ID; // index into parent objects vertexData vector
	public ArrayList neighborTris = new ArrayList(); // Neighbor triangles of
														// this vertex

	public GL_Vertex() {
		pos = new GL_Vector(0f, 0f, 0f);
	}

	public GL_Vertex(float xpos, float ypos, float zpos) {
		pos = new GL_Vector(xpos, ypos, zpos);
	}

	public GL_Vertex(float xpos, float ypos, float zpos, float u, float v) {
		pos = new GL_Vector(xpos, ypos, zpos);
	}

	public GL_Vertex(GL_Vector ppos) {
		pos = ppos.getClone();
	}

	public GL_Vertex(GL_Vector ppos, float u, float v) {
		pos = ppos.getClone();
		// this.u = u;
		// this.v = v;
	}

	/**
	 * add a neighbor triangle to this vertex
	 */
	void addNeighborTri(GL_Triangle triangle) {
		if (!neighborTris.contains(triangle)) {
			neighborTris.add(triangle);
		}
	}

	/**
	 * clear the neighbor triangle list
	 */
	void resetNeighbors() {
		neighborTris.clear();
	}

	public String toString() {
		return new String("<vertex  x=" + pos.x + " y=" + pos.y + " z=" + pos.z + ">\r\n");
	}

}