package cs4620.ray2.shader;

import cs4620.ray2.shader.Shader;
import cs4620.ray2.RayTracer;
import cs4620.ray2.IntersectionRecord;
import cs4620.ray2.Ray;
import cs4620.ray2.Scene;
import egl.math.Colord;
import egl.math.Vector3d;

/**
 * A Phong material.
 *
 * @author ags, pramook
 */
public class Glazed extends Shader {

	/**
	 * The index of refraction of this material. Used when calculating Snell's Law.
	 */
	protected double refractiveIndex;
	public void setRefractiveIndex(double refractiveIndex) { this.refractiveIndex = refractiveIndex; }

	/**
	 * The underlying material beneath the glaze.
	 */
	protected Shader substrate;
	public void setSubstrate(Shader substrate) {
		this.substrate = substrate; 
	}
	
	public Glazed() { 
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {    
		return "glass " + refractiveIndex + " end";
	}

	/**
	 * Evaluate the intensity for a given intersection using the Glass shading model.
	 *
	 * @param outIntensity The color returned towards the source of the incoming ray.
	 * @param scene The scene in which the surface exists.
	 * @param ray The ray which intersected the surface.
	 * @param record The intersection record of where the ray intersected the surface.
	 * @param depth The recursion depth.
	 */
	@Override
	public void shade(Colord outIntensity, Scene scene, Ray ray, IntersectionRecord record, int depth) {
		// TODO#A7: fill in this function.
		//get the scaling factor
		if(depth > RayTracer.MAX_DEPTH)
			return;
		outIntensity.setZero();
		Vector3d normal = record.normal.normalize();
		//Outgoing direction
		Vector3d d = ray.direction.negate().normalize();
		//Generate the new reflection ray
		if(normal.clone().dot(d) < 0)
			return;
		double R = fresnel(normal, d, refractiveIndex);
		if(R > 1){
			System.out.println("haha");
		}
		Vector3d refDir = normal.clone().mul(2 * d.clone().dot(normal)).sub(d);
		Ray reflect = new Ray(record.location, refDir.normalize());
		reflect.makeOffsetRay();
		
		//Compute reflect color
		Colord rColor = new Colord();		
		RayTracer.shadeRay(rColor, scene, reflect, depth + 1);
		rColor.mul(R);

		//Compute substrate color
		Colord subColor = new Colord();
		substrate.shade(subColor, scene, ray, record, 10);
		subColor.mul(1.0 - R);
		
		outIntensity.add(subColor).add(rColor);
	
	}
}