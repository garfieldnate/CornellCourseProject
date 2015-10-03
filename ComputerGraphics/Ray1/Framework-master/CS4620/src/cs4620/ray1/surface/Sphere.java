package cs4620.ray1.surface;

import cs4620.ray1.IntersectionRecord;
import egl.math.Vector2d;
import cs4620.ray1.Ray;
import egl.math.Vector3d;

/**
 * Represents a sphere as a center and a radius.
 *
 * @author ags
 */
public class Sphere extends Surface {
  
  /** The center of the sphere. */
  protected final Vector3d center = new Vector3d();
  public void setCenter(Vector3d center) { this.center.set(center); }
  
  /** The radius of the sphere. */
  protected double radius = 1.0;
  public void setRadius(double radius) { this.radius = radius; }
  
  protected final double M_2PI = 2*Math.PI;
  
  public Sphere() { 
	  this.setShader(shader);
  }
  
  /**
   * Tests this surface for intersection with ray. If an intersection is found
   * record is filled out with the information about the intersection and the
   * method returns true. It returns false otherwise and the information in
   * outRecord is not modified.
   *
   * @param outRecord the output IntersectionRecord
   * @param ray the ray to intersect
   * @return true if the surface intersects the ray
   */
  public boolean intersect(IntersectionRecord outRecord, Ray rayIn) {
    // TODO#A2: fill in this function.
	double t1, t2, t;
	double discriminant;
	Vector3d sub = new Vector3d();
	sub.set(rayIn.origin).sub(center);
	double b = rayIn.direction.dot(sub);
	double a = rayIn.direction.lenSq();
	double c = sub.lenSq() - radius * radius;
	discriminant = b * b - a * c;

	if (discriminant >= 0){
		t1 = (-b + Math.sqrt(discriminant)) / a;
		t2 = (-b - Math.sqrt(discriminant)) / a;
		
		if (t1 > rayIn.end || t1 < rayIn.start){
			if(t2 > rayIn.end || t2 < rayIn.start)
				return false;
			t = t2;
		}
		else{
			if(t2 > rayIn.end || t2 < rayIn.start)
				t = t1;
			else{
				if (t1 > t2)
					t = t2;
				else
					t = t1;
			}
		}
		
			outRecord.t = t;
			//System.out.println(t);
			Vector3d recordLocation = new Vector3d();
			Vector3d recordDirection = new Vector3d();
			recordDirection.set(rayIn.direction).mul(t);
			recordLocation.set(rayIn.origin).add(recordDirection);
			outRecord.location.set(recordLocation);
			outRecord.normal.set(recordLocation).sub(center).normalize();
			
			//set record texture coordinates
            double v = (M_2PI/2 + 2 * Math.asin(recordLocation.y)) / M_2PI;
			//double v = Math.asin(recordLocation.y) * 2 / M_2PI;
            double u = -(Math.atan2(recordLocation.z,recordLocation.x)) / M_2PI;
            Vector2d recordUV = new Vector2d(u,v);	
            outRecord.texCoords.set(recordUV);
            outRecord.surface = this;
			return true;
		
	}
	else  
       return false;
  }
  
  /**
   * @see Object#toString()
   */
  public String toString() {
    return "sphere " + center + " " + radius + " " + shader + " end";
  }

}