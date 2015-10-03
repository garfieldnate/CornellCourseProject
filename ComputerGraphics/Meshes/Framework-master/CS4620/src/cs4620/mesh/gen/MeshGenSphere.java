package cs4620.mesh.gen;

import org.lwjgl.BufferUtils;

import cs4620.mesh.MeshData;

/**
 * Generates A Sphere Mesh
 * @author Cristian
 *
 */
public class MeshGenSphere extends MeshGenerator {
	@Override
	public void generate(MeshData outData, MeshGenOptions opt) {
		// TODO#A1: Create A Sphere Mesh

		// Calculate Vertex And Index Count
		outData.vertexCount = (opt.divisionsLongitude) * (opt.divisionsLatitude) * 4;
		int tris = (opt.divisionsLongitude * 2) * (opt.divisionsLatitude);
		outData.indexCount = tris * 3;
		
		// Create Storage Spaces
		outData.positions = BufferUtils.createFloatBuffer(outData.vertexCount * 3);
		outData.indices = BufferUtils.createIntBuffer(outData.indexCount);
		outData.uvs = BufferUtils.createFloatBuffer(outData.vertexCount * 2);
		outData.normals = BufferUtils.createFloatBuffer(outData.vertexCount * 3);
		
		//Create The Vertices for North Pole
		//outData.positions.put(0); outData.positions.put(1); outData.positions.put(0);
		
		//Create The UV Coordinates for North Pole
		//outData.uvs.put(1);outData.uvs.put(1);
		
		//Create The Normals For North Pole;
		//outData.normals.put(0); outData.normals.put(1); outData.normals.put(0);
		
		// Create The Vertices & UV Coordinates for Tube
		for(int i = 0; i < opt.divisionsLatitude; i++){
			double alpha = (double)i * Math.PI / (opt.divisionsLatitude);
			double alpha1 = (double)(i + 1) * Math.PI / (opt.divisionsLatitude);
			//double alphan = (double)(i + 0.5) * Math.PI / (opt.divisionsLatitude);
			for(int j = 0; j < opt.divisionsLongitude; j++){
				double theta = (double)(j * 2) * Math.PI / (opt.divisionsLongitude);
				double theta1 = (double)((j + 1) * 2) * Math.PI / (opt.divisionsLongitude);
				//double thetan = (double)(j * 2 + 1) * Math.PI / (opt.divisionsLongitude);
				
				//Calculate Positions
				float y = (float) Math.cos(alpha);
				float x = (float) (Math.sin(alpha) * Math.sin(theta));
				float z = (float) (Math.sin(alpha) * Math.cos(theta));
				float y1 = (float) Math.cos(alpha1);
				float x1 = (float) (Math.sin(alpha1) * Math.sin(theta));
				float z1 = (float) (Math.sin(alpha1) * Math.cos(theta));
				float y2 = (float) Math.cos(alpha);
				float x2 = (float) (Math.sin(alpha) * Math.sin(theta1));
				float z2 = (float) (Math.sin(alpha) * Math.cos(theta1));
				float y3 = (float) Math.cos(alpha1);
				float x3 = (float) (Math.sin(alpha1) * Math.sin(theta1));
				float z3 = (float) (Math.sin(alpha1) * Math.cos(theta1));
				
				//Calculate Normals
				//float yn = (float) Math.cos(alphan);
				//float xn = (float) (Math.sin(alphan) * Math.sin(thetan));
				//float zn = (float) (Math.sin(alphan) * Math.cos(thetan));
				
				//Calculate UV Coordinates
				float u = (float) j / (opt.divisionsLongitude);
				float u1 = (float) (j + 1) / (opt.divisionsLongitude);
				float v = (float) ((opt.divisionsLatitude) - i) / (opt.divisionsLatitude);
				float v1 = (float) ((opt.divisionsLatitude) - i - 1) / (opt.divisionsLatitude);
				
				//Vertices & Normals
				
				//Left Top
				outData.positions.put(x); outData.positions.put(y); outData.positions.put(z);
				outData.normals.put(x);outData.normals.put(y);outData.normals.put(z);
				
				//Left Bottom
				outData.positions.put(x1); outData.positions.put(y1); outData.positions.put(z1);
				outData.normals.put(x1);outData.normals.put(y1);outData.normals.put(z1);
				
				//Right Top
				outData.positions.put(x2); outData.positions.put(y2); outData.positions.put(z2);
				outData.normals.put(x2);outData.normals.put(y2);outData.normals.put(z2);
				
				//Right Bottom
				outData.positions.put(x3); outData.positions.put(y3); outData.positions.put(z3);
				outData.normals.put(x3);outData.normals.put(y3);outData.normals.put(z3);
				
				//UV Coordinates
				outData.uvs.put(u); outData.uvs.put(v);
				outData.uvs.put(u); outData.uvs.put(v1);
				outData.uvs.put(u1); outData.uvs.put(v);
				outData.uvs.put(u1); outData.uvs.put(v1);
				
				//Normals
				//for(int k = 0; k < 4; k++){outData.normals.put(xn);outData.normals.put(yn);outData.normals.put(zn);}
		   }
		}
		
		// Create The Indices For The Tube
		for(int i = 0; i < opt.divisionsLatitude; i++){
			for(int j = 0 ; j < opt.divisionsLongitude; j++){
				int si = i * 4 * opt.divisionsLongitude + j * 4;
				outData.indices.put(si);
				outData.indices.put(si + 1);
				outData.indices.put(si + 2);
				outData.indices.put(si + 2);
				outData.indices.put(si + 1);
				outData.indices.put(si + 3);
			}	
		}
	}
}
