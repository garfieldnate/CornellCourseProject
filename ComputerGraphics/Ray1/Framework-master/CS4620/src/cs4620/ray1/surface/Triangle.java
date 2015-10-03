package cs4620.ray1.surface;

import cs4620.ray1.IntersectionRecord;
import cs4620.ray1.Ray;
import egl.math.Vector3d;
import egl.math.Vector3i;
import cs4620.ray1.shader.Shader;
import egl.math.Vector2d;
/**
 * Represents a single triangle, part of a triangle mesh
 *
 * @author ags
 */
public class Triangle extends Surface {
  /** The normal vector of this triangle, if vertex normals are not specified */
  Vector3d norm;
  
  /** The mesh that contains this triangle */
  Mesh owner;
  
  /** 3 indices to the vertices of this triangle. */
  Vector3i index;
  
  double a, b, c, d, e, f;
  public Triangle(Mesh owner, Vector3i index, Shader shader) {
    this.owner = owner;
    this.index = new Vector3i(index);
    
    Vector3d v0 = owner.getPosition(index.x);
    Vector3d v1 = owner.getPosition(index.y);
    Vector3d v2 = owner.getPosition(index.z);
    
    if (!owner.hasNormals()) {
    	Vector3d e0 = new Vector3d(), e1 = new Vector3d();
    	e0.set(v1).sub(v0);
    	e1.set(v2).sub(v0);
    	norm = new Vector3d();
    	norm.set(e0).cross(e1);
    }
    a = v0.x-v1.x;
    b = v0.y-v1.y;
    c = v0.z-v1.z;
    
    d = v0.x-v2.x;
    e = v0.y-v2.y;
    f = v0.z-v2.z;
    
    this.setShader(shader);
  }

  /**
   * Tests this surface for intersection with ray. If an intersection is found
   * record is filled out with the information about the intersection and the
   * method returns true. It returns false otherwise and the information in
   * outRecord is not modified.
   *
   * @param outRecord the output IntersectionRecord
   * @param rayIn the ray to intersect
   * @return true if the surface intersects the ray
   */
  public boolean intersect(IntersectionRecord outRecord, Ray rayIn) {
    // TODO#A2: fill in this function.
	
	double g,h,i,j,k,l,beta,gama,t,m;
	g = rayIn.direction.x;
	h = rayIn.direction.y;
	i = rayIn.direction.z;
	j = owner.getPosition(index.x).x - rayIn.origin.x;
	k = owner.getPosition(index.x).y - rayIn.origin.y;
	l = owner.getPosition(index.x).z - rayIn.origin.z;
	
	m = a * (e * i - h * f) + b * (g * f - d * i) + c * (d * h - e * g);
	if (m == 0)
		return false;
		t = -(f * (a * k - j * b) + e * (j * c - a * l) + d * (b * l - k * c)) / m;
		if(t < rayIn.start || t > rayIn.end)
			return false;
			gama = (i * (a * k - j * b) + h * (j * c - a * l) + g * (b * l - k * c)) / m;
			if(gama < 0 || gama > 1)
				return false;
				beta = (j * (e * i - h * f) + k * (g * f - d * i) + l * (d * h - e * g)) / m;
			    if(beta < 0 || beta > 1 - gama)
			    	return false;
			    	Vector3d recordLocation = new Vector3d();
					Vector3d recordDirection = new Vector3d();
					recordDirection.set(rayIn.direction).mul(t);
					recordLocation.set(rayIn.origin).add(recordDirection);
					outRecord.t = t;
					outRecord.location.set(recordLocation);
					if (!owner.hasNormals()){
						outRecord.normal.set(norm).normalize();
					}
					else{
						Vector3d recordNormal = new Vector3d();
						recordNormal.x = owner.getNormal(index.x).x 
								         + beta * (owner.getNormal(index.y).x - owner.getNormal(index.x).x)
								         + gama * (owner.getNormal(index.z).x - owner.getNormal(index.x).x);
						recordNormal.y = owner.getNormal(index.x).y
						                 + beta * (owner.getNormal(index.y).y - owner.getNormal(index.x).y)
						                 + gama * (owner.getNormal(index.z).y - owner.getNormal(index.x).y);
						recordNormal.z = owner.getNormal(index.x).z 
						                 + beta * (owner.getNormal(index.y).z - owner.getNormal(index.x).z)
						                 + gama * (owner.getNormal(index.z).z - owner.getNormal(index.x).z);
						outRecord.normal.set(recordNormal).normalize();
					}
					if(owner.hasUVs()){
						Vector2d recordUV = new Vector2d();
						recordUV.x = owner.getUV(index.x).x 
								     + beta * (owner.getUV(index.y).x - owner.getUV(index.x).x) 
								     + gama * (owner.getUV(index.z).x - owner.getUV(index.x).x);
						recordUV.y = owner.getUV(index.x).y 
								     + beta * (owner.getUV(index.y).y - owner.getUV(index.x).y) 
								     + gama * (owner.getUV(index.z).y - owner.getUV(index.x).y);
					    outRecord.texCoords.set(recordUV);
					}
					outRecord.surface = this;
			    	return true;
  }

  /**
   * @see Object#toString()
   */
  public String toString() {
    return "Triangle ";
  }
}