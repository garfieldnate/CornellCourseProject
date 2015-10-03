package cs4620.mesh;

import java.util.ArrayList;

import org.lwjgl.BufferUtils;

import egl.math.Vector3;
import egl.math.Vector3i;

/**
 * Performs Normals Reconstruction Upon A Mesh Of Positions
 * @author Cristian
 *
 */
public class MeshConverter {
	/**
	 * Reconstruct a mesh's normals so that it appears to have sharp creases
	 * @param positions List of positions
	 * @param tris List of triangles (Each is a group of three indices into the positions list)
	 * @return A mesh with all faces separated and normals at vertices that lie normal to faces
	 */
	public static MeshData convertToFaceNormals(ArrayList<Vector3> positions, ArrayList<Vector3i> tris) {
		MeshData data = new MeshData();

		// Notice
		System.out.println("Face normals are not implemented");
		
		// No need to implement this function, not part of the Mesh assignment.
		
		return data;
	}
	/**
	 * Reconstruct a mesh's normals so that it appears to be smooth
	 * @param positions List of positions
	 * @param tris List of triangles (Each is a group of three indices into the positions list)
	 * @return A mesh with normals at vertices
	 */
	public static MeshData convertToVertexNormals(ArrayList<Vector3> positions, ArrayList<Vector3i> tris) {
		MeshData data = new MeshData();

		// TODO#A1: Allocate mesh data and create mesh positions, normals, and indices (Remember to set mesh Vertex/Index counts)
		// Note that the vertex data has been supplied as a list of egl.math.Vector3 objects.  Take a
		// look at that class, which contains methods that are very helpful for performing vector
		// math.
		
		//Calculate Vertex And Index Count
		data.vertexCount = positions.size();
		data.indexCount = tris.size() * 3;
		
		// Create Storage Spaces
		data.positions = BufferUtils.createFloatBuffer(data.vertexCount * 3);
		data.normals = BufferUtils.createFloatBuffer(data.vertexCount * 3);
		data.indices = BufferUtils.createIntBuffer(data.indexCount);
		
		//Create mesh positions
		for(int i = 0; i < data.vertexCount; i++){
			float x = positions.get(i).get(0);
			float y = positions.get(i).get(1);
			float z = positions.get(i).get(2);
			//System.out.println(x);
			//System.out.println(y);
			//System.out.println(z);
			data.positions.put(x); data.positions.put(y); data.positions.put(z);
		}
		
		//Compute Vertex Normals
		float normalarray[][] = new float [positions.size()][3];
		for(int i = 0; i < data.vertexCount; i++){
			normalarray[i][0] = 0; normalarray[i][1] = 0; normalarray[i][2] = 0; 
		}
		for(int j = 0; j < tris.size(); j++){
			
			//Compute The Normal of a Triangle
			 int a = tris.get(j).get(0);
			 int b = tris.get(j).get(1);
			 int c = tris.get(j).get(2);
	         Vector3 ab = new Vector3((positions.get(a).get(0) - positions.get(b).get(0)),(positions.get(a).get(1) - positions.get(b).get(1)),(positions.get(a).get(2) - positions.get(b).get(2)));
	         Vector3 cb = new Vector3((positions.get(c).get(0) - positions.get(b).get(0)),(positions.get(c).get(1) - positions.get(b).get(1)),(positions.get(c).get(2) - positions.get(b).get(2))); 
	         Vector3 n = new Vector3();
	         n.x = cb.y * ab.z - ab.y * cb.z;
	         n.y = cb.z * ab.x - ab.z * cb.x;
	         n.z = cb.x * ab.y - ab.x * cb.y;
	         Vector3 nl = new Vector3();
	         nl = n.normalize();
	         
	         
	        //Add The Normal of a Triangle to its Vertex Normals
	         normalarray[a][0] += nl.x;
	         //System.out.println(normalarray[a][0]);
	         normalarray[a][1] += nl.y;
	         //System.out.println(normalarray[a][1]);
	         normalarray[a][2] += nl.z;
	         //System.out.println(normalarray[a][2]);
	         normalarray[b][0] += nl.x;
	         normalarray[b][1] += nl.y;
	         normalarray[b][2] += nl.z;
	         normalarray[c][0] += nl.x;
	         normalarray[c][1] += nl.y;
	         normalarray[c][2] += nl.z;
	         
		}
		for (int i = 0; i < data.vertexCount; i++){
			double x = normalarray[i][0]; 
			double y = normalarray[i][1]; 
			double z = normalarray[i][2];
			double norml = Math.sqrt( x * x + y * y + z * z);
			data.normals.put((float)(x / norml));
			data.normals.put((float)(y / norml));
			data.normals.put((float)(z / norml));
		}

		//Create Indices
		for(int i = 0; i < tris.size(); i++){
			int x = tris.get(i).get(0);
			int y = tris.get(i).get(1);
			int z = tris.get(i).get(2);
			//System.out.println(x);
			//System.out.println(y);
			//System.out.println(z);
			data.indices.put(x); data.indices.put(y); data.indices.put(z);
		}
		return data;
	}
}