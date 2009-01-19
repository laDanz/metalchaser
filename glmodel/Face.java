package glmodel;

/**
 * The Face class describes one triangle, quad or polygon in the model.
 * For each point in the face, this class holds references to a vertex,
 * texture coord, and normal.  A "reference" is an index into the vertex,
 * texture coord, or normal arrays.
 * <P>
 * The int arrays will hold one element for each point in the
 * face, ie. a triangle face will have int[3] for each, a quad face
 * will have int[4].
 */
class Face { 
	int[] vertexIDs;
	int[] textureIDs;
	int[] normalIDs;

	Face(int[] vertIDs, int[] txtrIDs, int[] normIDs) {
		vertexIDs = new int[vertIDs.length];
		textureIDs = new int[vertIDs.length];
		normalIDs = new int[vertIDs.length];
		if (vertIDs != null)
			System.arraycopy(vertIDs, 0, vertexIDs, 0, vertIDs.length);
		if (txtrIDs != null)
			System.arraycopy(txtrIDs, 0, textureIDs, 0, txtrIDs.length);
		if (normIDs != null)
			System.arraycopy(normIDs, 0, normalIDs, 0, normIDs.length);
	}
}
