package glmodel;

import java.io.Serializable;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.util.glu.GLU;

/**
 * Holds a mesh object containing triangles and vertices. Verts and Triangles
 * are stored as ArrayLists for flexibility and also as arrays for speedier
 * processing.
 * 
 * The rebuild() function converts the ArrayLists to arrays, assigns neighbor
 * triangles to each vert, and recalculates normals. Handles normal smoothing,
 * and preserves sharp edges.
 * 
 * see projectVerts() for setting screen positions (Zdepth) of verts see
 * sortTriangles() for Z depth sorting see regenerateNormals() to calculate
 * smoothed normals
 */
public class GL_Mesh implements Serializable {

	private static final long serialVersionUID = 1L;

	public ArrayList vertexData = new ArrayList();
	public ArrayList triangleData = new ArrayList();

	public GL_Vertex[] vertices;
	public GL_Triangle[] triangles;

	public int numVertices = 0;
	public int numTriangles = 0;

	public String name = ""; // This object's name

	// calculate the cosine of the smoothing angle (in degrees) (see
	// registerSmoothNeighbors())
	private float smoothingAngle = 89f;
	private float cos_angle = (float) Math.cos(Math.toRadians(smoothingAngle));

	public GL_Mesh() {
	}

	public void setSmoothingAngle(float minimumAngle) {
		smoothingAngle = minimumAngle;
		cos_angle = (float) Math.cos(Math.toRadians(smoothingAngle));
	}

	public GL_Vertex vertex(int id) {
		return (GL_Vertex) vertexData.get(id);
	}

	public GL_Triangle triangle(int id) {
		return (GL_Triangle) triangleData.get(id);
	}

	public void addVertex(GL_Vertex newVertex) {
		vertexData.add(newVertex);
	}

	public void addTriangle(GL_Triangle newTriangle) {
		triangleData.add(newTriangle);
	}

	public void addTriangle(int v1, int v2, int v3) {
		addTriangle(vertex(v1), vertex(v2), vertex(v3));
	}

	public void removeVertex(GL_Vertex v) {
		vertexData.remove(v);
	}

	public void removeTriangle(GL_Triangle t) {
		triangleData.remove(t);
	}

	public void removeVertexAt(int pos) {
		vertexData.remove(pos);
	}

	public void removeTriangleAt(int pos) {
		triangleData.remove(pos);
	}

	/**
	 * Copy vertex and triangle data into arrays for faster access. Find the
	 * neighbor triangles for each vertex. This data should not change once the
	 * mesh is loaded, so we call rebuild() only when the object is imported.
	 */
	public void rebuild() {
		GL_Triangle tri;

		// Generate faster structure for vertices
		numVertices = vertexData.size();
		vertices = new GL_Vertex[numVertices];
		for (int i = 0; i < numVertices; i++) {
			vertices[i] = (GL_Vertex) vertexData.get(i);
		}

		// Generate faster structure for triangles
		numTriangles = triangleData.size();
		triangles = new GL_Triangle[numTriangles];
		for (int i = 0; i < numTriangles; i++) {
			triangles[i] = (GL_Triangle) triangleData.get(i); // enum.nextElement();
			triangles[i].ID = i;
		}

		// //////////////////////////////////////////////
		// find neighboring triangles for each vertex

		// clear the neighbors list for all verts
		for (int i = 0; i < numVertices; i++) {
			vertices[i].ID = i;
			vertices[i].resetNeighbors();
		}

		// register each triangle as a "neighbor" of the triangle's verts
		for (int i = 0; i < numTriangles; i++) {
			tri = triangles[i];
			tri.p1.addNeighborTri(tri);
			tri.p2.addNeighborTri(tri);
			tri.p3.addNeighborTri(tri);
		}
	}

	// MJN!!!!! /////////////////////////////////////////////
	// Since a vertex can be shared by several triangles
	// and each triangle may have a different normal (ie. two triangles
	// form a 90 degree edge), then we need to store the neighboring
	// triangles for each vertex IN EACH TRIANGLE. So when a vertex is
	// shared by several triangles, each triangle can have a different
	// normal for that vert, based on the neighbors of that triangle.

	/**
	 * For the given Vert in the given Triangle, make a list of triangles that
	 * are neighbors to the given Triangle. Only count as neighbors those
	 * triangles that form a smooth surface with this triangle, meaning the
	 * angle between this triangle and the neighbor triangle is > 90 degrees
	 * (the actual min degrees value is in cos_angle).
	 * 
	 * Requires that rebuild() has been run so that the vertex has a list of
	 * neighbor triangles populated (see addNeighborTri()), and the triangle
	 * face normals have been calculated. (see GL_Triangle.regenerateNormal()).
	 */
	public void registerSmoothNeighbors(ArrayList neighborTris, GL_Vertex v, GL_Triangle t) {
		GL_Triangle neighborTri;
		// any triangle that is neighbor to the vert, is neighbor to that verts
		// parent triangle
		for (int i = 0; i < v.neighborTris.size(); i++) {
			neighborTri = (GL_Triangle) v.neighborTris.get(i);
			// Test for > 90 degree angle between triangle t and this triangle
			if (GL_Triangle.onSameSurface(t, neighborTri, cos_angle)) {
				if (!neighborTris.contains(neighborTri)) {
					neighborTris.add(neighborTri);
				}
			}
		}
		if (neighborTris.size() == 0) {
			// no neighbors found. Use the verts parent triangle
			neighborTris.add(t);
		}
	}

	public void addVertex(float x, float y, float z) {
		addVertex(new GL_Vertex(x, y, z));
	}

	public void addVertex(float x, float y, float z, float u, float v) {
		GL_Vertex vert = new GL_Vertex(x, y, z);
		// vert.setUV(u,v); // see triangle
		addVertex(vert);
	}

	public void addTriangle(GL_Vertex a, GL_Vertex b, GL_Vertex c) {
		addTriangle(new GL_Triangle(a, b, c));
	}

	/**
	 * Recalculate normals for each vertex in each triangle. This allows a
	 * vertex to have a different normal for each triangle it's in (so we can
	 * have sharp edges or smooth surfaces).
	 * 
	 * Requires that neighoring triangles have already been set. See rebuild()
	 * and registerNeighbors().
	 */
	public void regenerateNormals() {
		GL_Triangle tri;
		// first calculate all triangle (face) normals
		for (int i = 0; i < numTriangles; i++) {
			triangles[i].recalcFaceNormal();
		}
		// Register the "smooth" neighbor triangles for each vert in
		// each triangle. Triangles that share verts are neighbors.
		// A "smooth" neighbor is a triangle that will be treated
		// as part of the same surface as this triangle.
		// See setSmoothingAngle() to set the angle at which triangles
		// are treated as two separate surfaces.
		for (int i = 0; i < numTriangles; i++) {
			tri = triangles[i];
			tri.resetNeighbors();
			registerSmoothNeighbors(tri.neighborsP1, tri.p1, tri);
			registerSmoothNeighbors(tri.neighborsP2, tri.p2, tri);
			registerSmoothNeighbors(tri.neighborsP3, tri.p3, tri);
		}
		// next calculate normals for each vert in each triangle.
		// the vert normal is the average of it's neighbor triangle normals.
		for (int i = 0; i < numTriangles; i++) {
			tri = triangles[i];
			tri.norm1 = tri.recalcVertexNormal(tri.neighborsP1);
			tri.norm2 = tri.recalcVertexNormal(tri.neighborsP2);
			tri.norm3 = tri.recalcVertexNormal(tri.neighborsP3);
		}
	}

	/**
	 * Return minimum point in the mesh.
	 */
	public GL_Vector min() {
		if (numVertices == 0)
			return new GL_Vector(0f, 0f, 0f);
		float minX = vertices[0].pos.x;
		float minY = vertices[0].pos.y;
		float minZ = vertices[0].pos.z;
		for (int i = 0; i < numVertices; i++) {
			if (vertices[i].pos.x < minX)
				minX = vertices[i].pos.x;
			if (vertices[i].pos.y < minY)
				minY = vertices[i].pos.y;
			if (vertices[i].pos.z < minZ)
				minZ = vertices[i].pos.z;
		}
		return new GL_Vector(minX, minY, minZ);
	}

	/**
	 * Return maximum point in the mesh.
	 */
	public GL_Vector max() {
		if (numVertices == 0)
			return new GL_Vector(0f, 0f, 0f);
		float maxX = vertices[0].pos.x;
		float maxY = vertices[0].pos.y;
		float maxZ = vertices[0].pos.z;
		for (int i = 0; i < numVertices; i++) {
			if (vertices[i].pos.x > maxX)
				maxX = vertices[i].pos.x;
			if (vertices[i].pos.y > maxY)
				maxY = vertices[i].pos.y;
			if (vertices[i].pos.z > maxZ)
				maxZ = vertices[i].pos.z;
		}
		return new GL_Vector(maxX, maxY, maxZ);
	}

	/**
	 * Return the center point of the mesh.
	 */
	public GL_Vector getCenter() {
		GL_Vector max = max();
		GL_Vector min = min();
		return new GL_Vector((max.x + min.x) / 2f, (max.y + min.y) / 2f, (max.z + min.z) / 2f);
	}

	/**
	 * Returns the dimensions of this mesh.
	 */
	public GL_Vector getDimension() {
		GL_Vector max = max();
		GL_Vector min = min();
		return new GL_Vector(max.x - min.x, max.y - min.y, max.z - min.z);
	}

	/**
	 * return the vertex array
	 */
	public GL_Vertex[] getVertexArray() {
		return vertices;
	}

	/**
	 * "Project" the verts to find their screen coords. Store these screen
	 * coords in the vertex.posS vector. GLU.gluProject() will create screen x,y
	 * coords and a z depth value between 0 and 1.
	 * 
	 * @see sortTriangles()
	 */
	public void projectVerts(GL_Mesh obj, FloatBuffer modelMatrix, FloatBuffer projectionMatrix, int[] viewport) {
		float[] projectedVert = new float[3];
		GL_Vertex v;
		// project verts to screen space and store x,y,z into vertex
		for (int i = 0; i < obj.vertices.length; i++) {
			v = obj.vertices[i];
			GLU.gluProject(v.pos.x, v.pos.y, v.pos.z, (modelMatrix), (projectionMatrix), IntBuffer.wrap(viewport),
					FloatBuffer.wrap(projectedVert));
			v.posS.x = projectedVert[0]; // screen x
			v.posS.y = projectedVert[1]; // screen y
			v.posS.z = projectedVert[2]; // z depth value is 0.0 - 1.0
		}
		// set average screen Z depth of each triangle
		calcZDepths();
	}

	/**
	 * Calculate the average Z depth of each triangle. Used by sortTriangles()
	 * below.
	 */
	public void calcZDepths() {
		// update screen Z depth of triangles
		for (int i = 0; i < triangles.length; i++) {
			triangles[i].calcZdepth();
		}
	}

	/**
	 * Z sort the triangles of this mesh. Call projectVerts() and calcZDepths()
	 * first to set correct Z depth of all verts and triangles.
	 */
	public void sortTriangles() {
		if (triangles != null) {
			triangles = sortTriangles(triangles, 0, triangles.length - 1);
		}
	}

	/**
	 * Z sort the given triangle array. Call projectVerts() first to set correct
	 * Z depth of all verts and triangles.
	 */
	public GL_Triangle[] sortTriangles(GL_Triangle[] tri, int L, int R) {
		float m = (tri[L].Zdepth + tri[R].Zdepth) / 2;
		int i = L;
		int j = R;
		GL_Triangle temp;

		do {
			while (tri[i].Zdepth > m)
				i++;
			while (tri[j].Zdepth < m)
				j--;

			if (i <= j) {
				temp = tri[i];
				tri[i] = tri[j];
				tri[j] = temp;
				i++;
				j--;
			}
		} while (j >= i);

		if (L < j)
			sortTriangles(tri, L, j);
		if (R > i)
			sortTriangles(tri, i, R);
		return tri;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<object id=" + name + ">\r\n");
		for (int i = 0; i < numVertices; i++) {
			buffer.append(vertices[i].toString());
		}
		return buffer.toString();
	}
}