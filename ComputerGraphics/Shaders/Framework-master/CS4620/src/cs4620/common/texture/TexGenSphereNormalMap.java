package cs4620.common.texture;

import egl.math.Color;
import egl.math.Colord;
import egl.math.Vector2;
import egl.math.Vector2i;
import egl.math.Vector3d;

public class TexGenSphereNormalMap extends ACTextureGenerator {
	// 0.5f means that the discs are tangent to each other
	// For greater values discs intersect
	// For smaller values there is a "planar" area between the discs
	private float bumpRadius;
	// The number of rows and columns
	// There should be one disc in each row and column
	private int resolution;
	
	public TexGenSphereNormalMap() {
		this.bumpRadius = 0.5f;
		this.resolution = 10;
		this.setSize(new Vector2i(256));
	}
	
	public void setBumpRadius(float bumpRadius) {
		this.bumpRadius = bumpRadius;
	}
	
	public void setResolution(int resolution) {
		this.resolution = resolution;
	}
	
	@Override
	public void getColor(float u, float v, Color outColor) {
		// TODO A4: Implement the sphere-disk normal map generation
		//get normal of the point
		Vector3d normal = getNormal(u,v);
		//compute nearest center of a disk
		float centeru = (float)Math.round(u * resolution) / (float)resolution;
		float centerv = (float)Math.round(v * resolution) / (float)resolution;
		Vector3d center = getNormal(centeru, centerv);
		Vector2 texture = new Vector2(u,v);
		Vector2 centerText = new Vector2(centeru, centerv);
		
		//judge whether the point is on a disk
		if(texture.mul(resolution).dist(centerText.mul(resolution)) < bumpRadius){
			normal.set(center);
		}
		//set outColor value
		Colord tem =new Colord(normal.add(1.0).div(2.0));
		outColor.set(tem);
	}
	
	//compute normal according to texture coordinates
	private Vector3d getNormal(float u, float v){
		double phi = (v - 0.5) * Math.PI;
		double theta = 2 * (u - 0.5)* Math.PI;
		float y = (float) Math.sin(phi);
		float x = (float) (Math.cos(phi) * (Math.sin(theta)));
		float z = (float) (Math.cos(phi) * (Math.cos(theta)));
		Vector3d result = new Vector3d(x,y,z);
		return(result);
	}
}
