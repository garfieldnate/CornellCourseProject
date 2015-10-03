package cs4620.ray2.surface;

import cs4620.ray2.IntersectionRecord;
import cs4620.ray2.Ray;
import egl.math.Vector3d;

/**
 * Represents a sphere as a center and a radius.
 *
 * @author ags
 */
public class Sphere extends Surface {

	/** The center of the sphere. */
	protected final Vector3d center = new Vector3d();

	public void setCenter(Vector3d center) {
		this.center.set(center);
	}

	/** The radius of the sphere. */
	protected double radius = 1.0;

	public void setRadius(double radius) {
		this.radius = radius;
	}

	protected final double M_2PI = 2 * Math.PI;

	public Sphere() {
	}

	/**
	 * Tests this surface for intersection with ray. If an intersection is found
	 * record is filled out with the information about the intersection and the
	 * method returns true. It returns false otherwise and the information in
	 * outRecord is not modified.
	 *
	 * @param outRecord
	 *            the output IntersectionRecord
	 * @param ray
	 *            the ray to intersect
	 * @return true if the surface intersects the ray
	 */
	public boolean intersect(IntersectionRecord outRecord, Ray rayIn) {
	  	//TODO#A7: Modify the intersect method: transform the ray to object space
	  	//transform the resulting intersection point and normal to world space

		//transform the ray into object space
		Ray ray = untransformRay(rayIn);
		
		// Rename the common vectors so I don't have to type so much
		Vector3d d = ray.direction;
		Vector3d c = center;
		Vector3d o = ray.origin;

		// Compute some factors used in computation
		double qx = o.x - c.x;
		double qy = o.y - c.y;
		double qz = o.z - c.z;
		double dd = d.lenSq();
		double qd = qx * d.x + qy * d.y + qz * d.z;
		double qq = qx * qx + qy * qy + qz * qz;

		// solving the quadratic equation for t at the pts of intersection
		// dd*t^2 + (2*qd)*t + (qq-r^2) = 0
		double discriminantsqr = (qd * qd - dd * (qq - radius * radius));

		// If the discriminant is less than zero, there is no intersection
		if (discriminantsqr < 0) {
			return false;
		}

		// Otherwise check and make sure that the intersections occur on the ray
		// (t
		// > 0) and return the closer one
		double discriminant = Math.sqrt(discriminantsqr);
		double t1 = (-qd - discriminant) / dd;
		double t2 = (-qd + discriminant) / dd;
		double t = 0;
		if (t1 > ray.start && t1 < ray.end) {
			t = t1;
		} else if (t2 > ray.start && t2 < ray.end) {
			t = t2;
		} else {
			return false; // Neither intersection was in the ray's half line.
		}

		// There was an intersection, fill out the intersection record
		if (outRecord != null) {
			outRecord.t = t;
			ray.evaluate(outRecord.location, t);
			outRecord.surface = this;
			outRecord.normal.set(outRecord.location).sub(center).normalize();
			double theta = Math.asin(outRecord.normal.y);
			double phi = Math.atan2(outRecord.normal.x, outRecord.normal.z);
			double u = (phi + Math.PI) / (2 * Math.PI);
			double v = (theta - Math.PI / 2) / Math.PI;
			outRecord.texCoords.set(u, v);
		    //Transform the resulting intersection point and normal to world space
		    outRecord.location.set(tMat.mulPos(outRecord.location));
		    outRecord.normal.set(tMatTInv.mulDir(outRecord.normal));
			}

		return true;
	}

	public void computeBoundingBox() {
		// TODO#A7: Compute the bounding box and store the result in
		// averagePosition, minBound, and maxBound.
		//Compute the bound coordinates
		double x1 = center.x - radius;
		double x2 = center.x + radius;
		double y1 = center.y - radius;
		double y2 = center.y + radius;
	    double z1 = center.z - radius;
	    double z2 = center.z + radius;
	    Vector3d tempMin = new Vector3d(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
	    Vector3d tempMax = new Vector3d(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
	    
	    //Find the 8 corner of cylinder
	    Vector3d[] v = new Vector3d[8];
	    v[0] = new Vector3d(x1, y1, z1);
	    v[1] = new Vector3d(x1, y2, z1);
	    v[2] = new Vector3d(x2, y1, z1);
	    v[3] = new Vector3d(x2, y2, z1);
	    v[4] = new Vector3d(x1, y1, z2);
	    v[5] = new Vector3d(x1, y2, z2);
	    v[6] = new Vector3d(x2, y1, z2);
	    v[7] = new Vector3d(x2, y2, z2);
	    
	    //Transform the 8 corner coordinates into world space and find the minbound and maxbound
	    for(int i = 0; i < 8; i++){
	    	Vector3d temp = tMat.clone().mulPos(v[i]);
	    	tempMin.x = Math.min(temp.x, tempMin.x);
	    	tempMin.y = Math.min(temp.y, tempMin.y);
	    	tempMin.z = Math.min(temp.z, tempMin.z);
	    	tempMax.x = Math.max(temp.x, tempMax.x);
	    	tempMax.y = Math.max(temp.y, tempMax.y);
	    	tempMax.z = Math.max(temp.z, tempMax.z);
	    }
	    minBound = tempMin;
	    maxBound = tempMax;
	    averagePosition = tempMin.clone().add(tempMax).div(2.0);  

	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return "sphere " + center + " " + radius + " " + shader + " end";
	}

}