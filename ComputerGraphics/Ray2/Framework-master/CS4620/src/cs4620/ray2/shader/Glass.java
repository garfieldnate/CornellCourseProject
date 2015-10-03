package cs4620.ray2.shader;

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
public class Glass extends Shader {

	/**
	 * The index of refraction of this material. Used when calculating Snell's Law.
	 */
	protected double refractiveIndex;
	public void setRefractiveIndex(double refractiveIndex) { this.refractiveIndex = refractiveIndex; }


	public Glass() { 
		refractiveIndex = 1.0;
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
		if(depth > RayTracer.MAX_DEPTH)
			return;
		outIntensity.setZero();
		Vector3d p = record.location;
		Vector3d n = record.normal.normalize();
		Vector3d d = ray.direction.negate().normalize();
		double R = 1;
		
		//The ray is going from air into glass
		if(d.dot(n) > 0){
			//Generate reflection ray
			Vector3d refDir = n.clone().mul(2 * d.clone().dot(n)).sub(d).normalize();
			Ray reflect = new Ray(record.location, refDir);
			reflect.makeOffsetRay();
			
			//Compute reflect color
			Colord rColor = new Colord();
			RayTracer.shadeRay(rColor, scene, reflect, depth + 1);
			
			//Total internal reflection
			if(Math.sin(d.angle(n)) >= refractiveIndex){
				outIntensity.add(rColor);
				return;
			}
			
			//No total internal reflection
			R = fresnel(n, d, refractiveIndex);
			rColor.mul(R);
			outIntensity.add(rColor);
			
			//Generate refracted ray
			double theta1 = d.angle(n);
			double theta2 = Math.asin(Math.sin(theta1) / refractiveIndex);
			Vector3d refractive1 = n.clone().mul(Math.cos(theta1)).sub(d).div(refractiveIndex);
			Vector3d refractive2 = n.clone().negate().mul(Math.cos(theta2));
			Vector3d refractiveDir = refractive1.clone().add(refractive2).normalize();
			Ray refractive = new Ray(record.location, refractiveDir);
			refractive.makeOffsetRay();
			
			//Compute refracted color
			Colord rftColor = new Colord();
			RayTracer.shadeRay(rftColor, scene, refractive, depth + 1);
			rftColor.mul(1 - R);
			outIntensity.add(rftColor);
		}
		//The ray is going from glass into air
		else if(d.dot(n) < 0){
			n.negate();
			//Generate reflection ray
			Vector3d refDir = n.clone().mul(2 * d.clone().dot(n)).sub(d).normalize();
			Ray reflect = new Ray(record.location, refDir);
			reflect.makeOffsetRay();
			
			//Compute reflect color
			Colord rColor = new Colord();
			RayTracer.shadeRay(rColor, scene, reflect, depth + 1);
			
			//Total internal reflection
			if(Math.sin(d.angle(n)) >= 1 / refractiveIndex){
				outIntensity.add(rColor);
				return;
			}
			//Non total internal reflection
			R = fresnel(n.clone().negate(), d, refractiveIndex);
			rColor.mul(R);
			outIntensity.add(rColor);
			double theta1 = d.angle(n);
			double theta2 = Math.asin(Math.sin(theta1) * refractiveIndex);
			//Generate refracted ray
			Vector3d refractive1 = n.clone().mul(Math.cos(theta1)).sub(d).mul(refractiveIndex);
			Vector3d refractive2 = n.clone().negate().mul(Math.cos(theta2));
			Vector3d refractiveDir = refractive1.clone().add(refractive2).normalize();
			Ray refractive = new Ray(record.location, refractiveDir);
			refractive.makeOffsetRay();
			
			//Compute refracted color
			Colord rftColor = new Colord();
			RayTracer.shadeRay(rftColor, scene, refractive, depth + 1);
			rftColor.mul(1 - R);
			outIntensity.add(rftColor);
		}
	}
	

}