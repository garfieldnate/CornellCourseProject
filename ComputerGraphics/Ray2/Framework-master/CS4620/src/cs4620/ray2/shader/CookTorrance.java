package cs4620.ray2.shader;

import cs4620.ray2.IntersectionRecord;
import cs4620.ray2.Light;
import cs4620.ray2.Ray;
import cs4620.ray2.Scene;
import egl.math.Color;
import egl.math.Colord;
import egl.math.Vector3d;

public class CookTorrance extends Shader {

	/** The color of the diffuse reflection. */
	protected final Colord diffuseColor = new Colord(Color.White);
	public void setDiffuseColor(Colord diffuseColor) { this.diffuseColor.set(diffuseColor); }

	/** The color of the specular reflection. */
	protected final Colord specularColor = new Colord(Color.White);
	public void setSpecularColor(Colord specularColor) { this.specularColor.set(specularColor); }

	/** The roughness controlling the roughness of the surface. */
	protected double roughness = 1.0;
	public void setRoughness(double roughness) { this.roughness = roughness; }

	/**
	 * The index of refraction of this material. Used when calculating Snell's Law.
	 */
	protected double refractiveIndex;
	public void setRefractiveIndex(double refractiveIndex) { this.refractiveIndex = refractiveIndex; }
	
	public CookTorrance() { }

	/**
	 * @see Object#toString()
	 */
	public String toString() {    
		return "CookTorrance " + diffuseColor + " " + specularColor + " " + roughness + " end";
	}

	/**
	 * Evaluate the intensity for a given intersection using the CookTorrance shading model.
	 *
	 * @param outIntensity The color returned towards the source of the incoming ray.
	 * @param scene The scene in which the surface exists.
	 * @param ray The ray which intersected the surface.
	 * @param record The intersection record of where the ray intersected the surface.
	 * @param depth The recursion depth.
	 */
	@Override
	public void shade(Colord outIntensity, Scene scene, Ray ray, IntersectionRecord record, int depth) {
		// TODO#A7 Fill in this function.
		// 1) Loop through each light in the scene.
		// 2) If the intersection point is shadowed, skip the calculation for the light.
		//	  See Shader.java for a useful shadowing function.
		// 3) Compute the incoming direction by subtracting
		//    the intersection point from the light's position.
		// 4) Compute the color of the point using the CookTorrance shading model. Add this value
		//    to the output.
		Vector3d normal = record.normal.normalize();
		Vector3d view = ray.origin.clone().sub(record.location).normalize();
		Vector3d incoming = new Vector3d();
		Colord color = new Colord();
		Ray shadowRay = new Ray();
		double Fresnel = fresnel(normal, view, refractiveIndex);
		
		outIntensity.setZero();
		
		for(Light light: scene.getLights()){
			if(!isShadowed(scene, light, record, shadowRay)) {
				incoming.set(light.getDirection(record.location)).normalize();
				Vector3d H = view.clone().add(incoming).normalize();
				
				//calculate specular term
				//Microfacet Distribution
				double D = Math.exp((Math.pow(normal.clone().dot(H), 2) - 1) / (Math.pow(roughness, 2) * Math.pow(normal.clone().dot(H), 2))) / (Math.pow(roughness, 2) * Math.pow(normal.clone().dot(H), 4));
				//Geometric Attenuation
				double G = Math.min(1, Math.min(2 * normal.clone().dot(H) * normal.clone().dot(view) / view.clone().dot(H), 2 * normal.clone().dot(H) * normal.clone().dot(incoming) / view.clone().dot(H)));
				double temp = Fresnel / Math.PI * D * G / (normal.clone().dot(view) * normal.clone().dot(incoming));
				
				//Add together
				color.set(specularColor.mul(temp).add(diffuseColor)).mul(Math.max(normal.clone().dot(incoming), 0)).mul(light.intensity).div(light.getRSq(record.location));
				outIntensity.add(color);
			}	
		}
	}
}
