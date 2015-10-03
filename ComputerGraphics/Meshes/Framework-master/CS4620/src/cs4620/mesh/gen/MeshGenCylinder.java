package cs4620.mesh.gen;

import org.lwjgl.BufferUtils;

import cs4620.mesh.MeshData;

/**
 * Generates A Cylinder Mesh
 * @author Cristian
 *
 */
public class MeshGenCylinder extends MeshGenerator {
	@Override
	public void generate(MeshData outData, MeshGenOptions opt) {
		// TODO#A1: Add Normals And Texture Coordinates Into The Mesh

		// Calculate Vertex And Index Count
		outData.vertexCount = (opt.divisionsLongitude) * 4 + 2;
		int tris = (opt.divisionsLongitude * 2) + (2 * (opt.divisionsLongitude - 2));
		outData.indexCount = tris * 3;

		// Create Storage Spaces
		outData.positions = BufferUtils.createFloatBuffer(outData.vertexCount * 3);
		outData.indices = BufferUtils.createIntBuffer(outData.indexCount);
		outData.uvs = BufferUtils.createFloatBuffer(outData.vertexCount * 2);
		outData.normals = BufferUtils.createFloatBuffer(outData.vertexCount * 3 * 3);
		
		// Create The Vertices & The UV Coordinates
		for(int i = 0;i < opt.divisionsLongitude;i++) {
			// Calculate XZ-Plane Position
			float u = (float)i / (float)opt.divisionsLongitude;
			double theta = u * Math.PI * 2.0;
			float z = (float)-Math.cos(theta);
			float x = (float)-Math.sin(theta);
			float v = ((float)1) / ((float)2);
			
			// Middle Tube Top
			 outData.positions.put(x); outData.positions.put(1); outData.positions.put(z);
            //UV Coordinates
			outData.uvs.put(u); outData.uvs.put(v);
			
			// Middle Tube Bottom
			outData.positions.put(x); outData.positions.put(-1); outData.positions.put(z);
			//UV Coordinates
			outData.uvs.put(u); outData.uvs.put(0);

			// Top Cap
			outData.positions.put(x); outData.positions.put(1); outData.positions.put(z);
			//UV Coordinates
			outData.uvs.put((x+1)/4);outData.uvs.put(Math.abs((z-3)/4));

			// Bottom Cap
			outData.positions.put(x); outData.positions.put(-1); outData.positions.put(z);
			//UV Coordinates
			outData.uvs.put((x+3)/4);outData.uvs.put(Math.abs((z-3)/4));
		}
		// Extra Vertices For U = 1
		outData.positions.put(0); outData.positions.put(1); outData.positions.put(-1);
		
		outData.positions.put(0); outData.positions.put(-1); outData.positions.put(-1);
		
		// Extra UV Coordinates For U = 1
		outData.uvs.put(1); outData.uvs.put(((float)1) / ((float)2));		
		outData.uvs.put(1); outData.uvs.put(0);
		
		//Create the Normals for The Tube
		for(int i = 0;i < opt.divisionsLongitude;i++){
			float p = (float)(i + 1 / 2) / (float)opt.divisionsLongitude;
			double theta = p * Math.PI * 2.0;
			float z = (float)-Math.cos(theta);
			float x = (float)-Math.sin(theta);
			
			//Middle Tube Top
			for(int j = 0;j < 2; j++) {outData.normals.put(x); outData.normals.put(0); outData.normals.put(z);}
			outData.normals.put(0); outData.normals.put(1); outData.normals.put(0);
			outData.normals.put(0); outData.normals.put(-1); outData.normals.put(0);

		}
				
		// Create The Indices For The Tube
		for(int i = 0;i < opt.divisionsLongitude;i++) {
			int si = i * 4;
			outData.indices.put(si);
			outData.indices.put(si + 1);
			outData.indices.put(si + 4);
			outData.indices.put(si + 4);
			outData.indices.put(si + 1);
			outData.indices.put(si + 5);
		}
		
		// Create The Indices For The Caps
		for(int i = 0;i < opt.divisionsLongitude - 2;i++) {
			int si = (i + 1) * 4 + 2;
			
			// Top Fan Piece
			outData.indices.put(2);
			outData.indices.put(si);
			outData.indices.put(si + 4);

			// Bottom Fan Piece
			outData.indices.put(3);
			outData.indices.put(si + 5);
			outData.indices.put(si + 1);
		}
	}

}
