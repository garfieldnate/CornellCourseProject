package cs4620.ray1.shader;

import cs4620.ray1.IntersectionRecord;
import cs4620.ray1.Ray;
import cs4620.ray1.Scene;
import egl.math.Color;
import egl.math.Colord;
import egl.math.Vector3d;

/**
 * A Phong material.
 *
 * @author ags, pramook
 */
public class Phong extends Shader {

	/** The color of the diffuse reflection. */
	protected final Colord diffuseColor = new Colord(Color.White);
	public void setDiffuseColor(Colord diffuseColor) { this.diffuseColor.set(diffuseColor); }

	/** The color of the specular reflection. */
	protected final Colord specularColor = new Colord(Color.White);
	public void setSpecularColor(Colord specularColor) { this.specularColor.set(specularColor); }

	/** The exponent controlling the sharpness of the specular reflection. */
	protected double exponent = 1.0;
	public void setExponent(double exponent) { this.exponent = exponent; }

	public Phong() { }

	/**
	 * @see Object#toString()
	 */
	public String toString() {    
		return "phong " + diffuseColor + " " + specularColor + " " + exponent + " end";
	}

	/**
	 * Evaluate the intensity for a given intersection using the Phong shading model.
	 *
	 * @param outIntensity The color returned towards the source of the incoming ray.
	 * @param scene The scene in which the surface exists.
	 * @param ray The ray which intersected the surface.
	 * @param record The intersection record of where the ray intersected the surface.
	 * @param depth The recursion depth.
	 */
	@Override
	public void shade(Colord outIntensity, Scene scene, Ray ray, IntersectionRecord record) {
		// TODO#A2: Fill in this function.
		// 1) Loop through each light in the scene.
		// 2) If the intersection point is shadowed, skip the calculation for the light.
		//	  See Shader.java for a useful shadowing function.
		// 3) Compute the incoming direction by subtracting
		//    the intersection point from the light's position.
		// 4) Compute the color of the point using the Phong shading model. Add this value
		//    to the output.
		
		outIntensity.set(0, 0, 0);
		for(int i = 0; i < scene.getLights().size(); i++){
			Ray shadowRay = new Ray();
			if(!isShadowed(scene, scene.getLights().get(i), record, shadowRay)){
				Vector3d wi = new Vector3d();
				wi.set(scene.getLights().get(i).position).sub(record.location);
				double r = wi.lenSq();
				Colord kl = new Colord();
				kl.set(scene.getLights().get(i).intensity);
				
				Colord kd = new Colord();
				if(getTexture() != null){
					kd.set(getTexture().getTexColor(record.texCoords));
				}
				else{
					kd.set(diffuseColor);
				}
				
				Vector3d w0 = new Vector3d();
				w0.set(ray.origin).sub(record.location);
				Vector3d h = new Vector3d();
				Vector3d mirror = new Vector3d();
				mirror.set(wi).add(w0);
				h.set(mirror).div(mirror.len()).normalize();
				Colord ks = new Colord();
				ks.set(specularColor);
				double p = exponent;
				
				//the same with  Lambertian Shading
				Colord recordIntensity = new Colord();
				recordIntensity.set(kd).mul(Math.max(record.normal.normalize().dot(wi.normalize()), 0)).mul(1 / r).mul(kl);
				outIntensity.add(recordIntensity);
				
				//the other part
				recordIntensity.set(ks).mul(Math.pow(Math.max(record.normal.normalize().dot(h), 0), p)).mul(1 / r).mul(kl);
				outIntensity.add(recordIntensity);	
			}
		}		
	}

}