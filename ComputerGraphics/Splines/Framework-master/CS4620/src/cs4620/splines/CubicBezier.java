package cs4620.splines;

import java.util.ArrayList;
import egl.math.*;
/*
 * Cubic Bezier class for the splines assignment
 */

public class CubicBezier {
	
	//This Bezier's control points
	Vector2 p0, p1, p2, p3;
	
	//Control parameter for curve smoothness
	float epsilon;
	
	//The points on the curve represented by this Bezier
	private ArrayList<Vector2> curvePoints;
	
	//The normals associated with curvePoints
	private ArrayList<Vector2> curveNormals;
	
	//The tangent vectors of this bezier
	private ArrayList<Vector2> curveTangents;
	
	//This bezier's owner
	private BSpline owner;
	
	/**
	 * 
	 * Cubic Bezier Constructor
	 * 
	 * Given 2-D BSpline Control Points correctly set self.{p0, p1, p2, p3},
	 * self.uVals, self.curvePoints, and self.curveNormals
	 * 
	 * @param bs0 First BSpline Control Point
	 * @param bs1 Second BSpline Control Point
	 * @param bs2 Third BSpline Control Point
	 * @param bs3 Fourth BSpline Control Point
	 * @param eps Maximum angle between line segments
	 */
	public CubicBezier(Vector2 bs0, Vector2 bs1, Vector2 bs2, Vector2 bs3, float eps, BSpline own) {
		curvePoints = new ArrayList<Vector2>();
		curveTangents = new ArrayList<Vector2>();
		curveNormals = new ArrayList<Vector2>();
		epsilon = eps;
		owner = own;
		//TODO A5
		p0 = bs0.clone().addMultiple(4.f, bs1).addMultiple(1.f, bs2).div(6.f);
		p1 = bs1.clone().mul(4.f).addMultiple(2.f, bs2).div(6.f);
		p2 = bs1.clone().mul(2.f).addMultiple(4.f, bs2).div(6.f);
		p3 = bs1.clone().addMultiple(4.f, bs2).addMultiple(1.f, bs3).div(6.f);
		tessellate();
	}
	

    /**
     * Approximate a Bezier segment with a number of vertices, according to an appropriate
     * smoothness criterion for how many are needed.  The points on the curve are written into the
     * array self.curvePoints, the tangents into self.curveTangents, and the normals into self.curveNormals.
     * The final point, p3, is not included, because cubic beziers will be "strung together"
     */
    private void tessellate() {
    	 // TODO A5
    	//placeholder code
    	this.tessellateRecursive(this.p0, this.p1, this.p2, this.p3, 0);
    }
    
	//Recursively divide beziers till enough smooth
	private void tessellateRecursive(Vector2 p0, Vector2 p1, Vector2 p2, Vector2 p3, int level){
		//If the angles between the edges of control polygon are both less than the half the angle tolerance 
		//or recursion level larger than 10, 
		//terminate recursion and return first points parameters
		if(p1.clone().sub(p0).angle(p2.clone().sub(p1)) < this.epsilon / 2 
				&& p1.clone().sub(p2).angle(p2.clone().sub(p3)) < this.epsilon / 2 
				|| level == 10){
			curvePoints.add(p0);

			//Compute the tangent of this middle point, we set it as a normalized vector from p2 to p1
			Vector2 tangent = p2.clone().sub(p1.clone()).normalize();
			curveTangents.add(tangent);
			
			//The normal of this middle point, which is the rotation of tangent with 90 degrees in CW 
			Vector2 normal = new Vector2(tangent.y, -tangent.x);
			curveNormals.add(normal); 
			return;
		}
		Vector2 L1 = p0;
		Vector2 L2 = p0.clone().add(p1.clone()).mul(0.5f);
		Vector2 H = p1.clone().add(p2.clone()).mul(0.5f);
		Vector2 R3 = p2.clone().add(p3.clone()).mul(0.5f);
		Vector2 R4 = p3;
		Vector2 L3 = L2.clone().add(H.clone()).mul(0.5f);
		Vector2 R2 = R3.clone().add(H.clone()).mul(0.5f);
		Vector2 L4 = L3.clone().add(R2.clone()).mul(0.5f);
		Vector2 R1 = L4;
		tessellateRecursive(L1, L2, L3, L4, level + 1);
		tessellateRecursive(R1, R2, R3, R4, level + 1);
	}
    
    /**
     * @return The points on this cubic bezier
     */
    public ArrayList<Vector2> getPoints() {
    	ArrayList<Vector2> returnList = new ArrayList<Vector2>();
    	for(Vector2 p : curvePoints) returnList.add(p.clone());
    	return returnList;
    }
    
    /**
     * @return The tangents on this cubic bezier
     */
    public ArrayList<Vector2> getTangents() {
    	ArrayList<Vector2> returnList = new ArrayList<Vector2>();
    	for(Vector2 p : curveTangents) returnList.add(p.clone());
    	return returnList;
    }
    
    /**
     * @return The normals on this cubic bezier
     */
    public ArrayList<Vector2> getNormals() {
    	ArrayList<Vector2> returnList = new ArrayList<Vector2>();
    	for(Vector2 p : curveNormals) returnList.add(p.clone());
    	return returnList;
    }
    
    
    /**
     * @return The references to points on this cubic bezier
     */
    public ArrayList<Vector2> getPointReferences() {
    	ArrayList<Vector2> returnList = new ArrayList<Vector2>();
    	for(Vector2 p : curvePoints) returnList.add(p);
    	return returnList;
    }
    
    /**
     * @return The references to tangents on this cubic bezier
     */
    public ArrayList<Vector2> getTangentReferences() {
    	ArrayList<Vector2> returnList = new ArrayList<Vector2>();
    	for(Vector2 p : curveTangents) returnList.add(p);
    	return returnList;
    }
    
    /**
     * @return The references to normals on this cubic bezier
     */
    public ArrayList<Vector2> getNormalReferences() {
    	ArrayList<Vector2> returnList = new ArrayList<Vector2>();
    	for(Vector2 p : curveNormals) returnList.add(p);
    	return returnList;
    }
    /*public static void main(String[] args){
    	CubicBezier bz = new CubicBezier(new Vector2(0, 0), new Vector2(1, 1), new Vector2(2, 3), new Vector2(6, -2), 0.f);
    	bz.tessellate();
    }*/
}
