package cs4620.splines;
import java.util.ArrayList;
import java.util.Collections;

import cs4620.mesh.MeshData;
import egl.NativeMem;
import egl.math.*;

public class BSpline {
	
	private float epsilon;
	
	//BSpline Control Points
	private ArrayList<Vector2> controlPoints;
	
	//Bezier Curves that make up this BSpline
	private ArrayList<CubicBezier> approximationCurves;
	
	//Whether or not this curve is a closed curve
	private boolean isClosed;
	
	public static final float DIST_THRESH = 0.15f;
	public static final int MIN_OPEN_CTRL_POINTS= 4,
			                           MIN_CLOSED_CTRL_POINTS= 3,
			                           MAX_CTRL_POINTS= 1000;

	public BSpline(ArrayList<Vector2> controlPoints, boolean isClosed, float epsilon) throws IllegalArgumentException {
		if(isClosed) {
			if(controlPoints.size() < MIN_CLOSED_CTRL_POINTS)
				throw new IllegalArgumentException("Closed Splines must have at least 3 control points.");
		} else {
			if(controlPoints.size() < MIN_OPEN_CTRL_POINTS)
				throw new IllegalArgumentException("Open Splines must have at least 4 control points.");
		}

		this.controlPoints = controlPoints;
		this.isClosed = isClosed;
		this.epsilon = epsilon;
		setBeziers();
	}
	
	public boolean isClosed() {
		return this.isClosed;
	}
	
	public boolean setClosed(boolean closed) {
		if(this.isClosed && this.controlPoints.size() == 3) {
			System.err.println("You must have at least 4 control points to make an open spline.");
			return false;
		}
		this.isClosed= closed;
		setBeziers();
		return true;
	}
	
	public ArrayList<Vector2> getControlPoints() {
		return this.controlPoints;
	}
	
	public void setControlPoint(int index, Vector2 point) {
		this.controlPoints.set(index, point);
		setBeziers();
	}
	
	public boolean addControlPoint(Vector2 point) {
		if(this.controlPoints.size() == MAX_CTRL_POINTS) {
			System.err.println("You can only have "+BSpline.MAX_CTRL_POINTS+" control points per spline.");
			return false;
		}
		/* point= (x0, y0), prev= (x1, y1), curr= (x2,y2)
		 * 
		 * v= [ (y2-y1), -(x2-x1) ]
		 * 
		 * r= [ (x1-x0), (y1-y0) ]
		 * 
		 * distance between point and line prev -> curr is v . r
		 */
		Vector2 curr, prev;
		Vector2 r= new Vector2(), v= new Vector2();
		float distance= Float.POSITIVE_INFINITY;
		int index= -1;
		for(int i= 0; i < controlPoints.size(); i++) {
			curr= controlPoints.get(i);
			if(i == 0) {
				if(isClosed) {
					// add line between first and last ctrl points
					prev= controlPoints.get(controlPoints.size()-1);
				} else {
					continue;
				}
			} else {
				prev= controlPoints.get(i-1);
			}
			v.set(curr.y-prev.y, -(curr.x-prev.x)); v.normalize();
			r.set(prev.x-point.x, prev.y-point.y);
			float newDist = Math.abs(v.dot(r));
			Vector2 v2 = curr.clone().sub(prev);
			v2.mul(1.0f / v2.lenSq());
			float newParam = -v2.dot(r);
			if(newDist < DIST_THRESH && newDist <= distance && 0 < newParam && newParam < 1) {
				distance= newDist;
				index= i;
			}
		}
		
		if (index >= 0) {
			controlPoints.add(index, point);
			setBeziers();
			return true;
		}
		System.err.println("Invalid location, try selecting a point closer to the spline.");
		return false;
	}
	
	public boolean removeControlPoint(int index) {
		if(this.isClosed) {
			if(this.controlPoints.size() == MIN_CLOSED_CTRL_POINTS) {
				System.err.println("You must have at least "+MIN_CLOSED_CTRL_POINTS+" for a closed Spline.");
				return false;
			}
		} else {
			if(this.controlPoints.size() == MIN_OPEN_CTRL_POINTS) {
				System.err.println("You must have at least "+MIN_OPEN_CTRL_POINTS+" for an open Spline.");
				return false;
			}
		}
		this.controlPoints.remove(index);
		setBeziers();
		return true;
	}
	
	public void modifyEpsilon(float newEps) {
		epsilon = newEps;
		setBeziers();
	}
	
	/**
	 * Returns the sequence of normals on this BSpline specified by the sequence of approximation curves
	 */
	public ArrayList<Vector2> getPoints() {
		ArrayList<Vector2> returnList = new ArrayList<Vector2>();
		for(CubicBezier b : approximationCurves)
			for(Vector2 p : b.getPoints())
				returnList.add(p.clone());
		return returnList;
	}
	
	/**
	 * Returns the sequence of normals on this BSpline specified by the sequence of approximation curves
	 */
	public ArrayList<Vector2> getNormals() {
		ArrayList<Vector2> returnList = new ArrayList<Vector2>();
		for(CubicBezier b : approximationCurves)
			for(Vector2 p : b.getNormals())
				returnList.add(p.clone());
		return returnList;
	}
	
	/**
	 * Returns the sequence of normals on this BSpline specified by the sequence of approximation curves
	 */
	public ArrayList<Vector2> getTangents() {
		ArrayList<Vector2> returnList = new ArrayList<Vector2>();
		for(CubicBezier b : approximationCurves)
			for(Vector2 p : b.getTangents())
				returnList.add(p.clone());
		return returnList;
	}
	
	/**
	 * Using this.controlPoints, create the CubicBezier objects that approxmiate this curve and
	 * save them to this.approximationCurves. Assure that the order of the Bezier curves that you
	 * add to approximationCurves is the order in which they approximate the overall BSpline
	 */
	private void setBeziers() {
		//TODO A5
		//placeholder code so this compiles
		approximationCurves = new ArrayList<CubicBezier>();
		//loop through the control points and add to approximation curves
		for(int i = 1; i < controlPoints.size() - 2; i++){
			CubicBezier tmp = new CubicBezier(controlPoints.get(i - 1),controlPoints.get(i),controlPoints.get(i + 1),controlPoints.get(i + 2),epsilon, this);
			approximationCurves.add(tmp);
		}
		//Closed splines
		if(isClosed){
			int n = controlPoints.size();
			CubicBezier tmp1 = new CubicBezier(controlPoints.get(n - 3),controlPoints.get(n - 2),controlPoints.get(n - 1),controlPoints.get(0),epsilon, this);
			approximationCurves.add(tmp1);
			CubicBezier tmp2 = new CubicBezier(controlPoints.get(n - 2),controlPoints.get(n - 1),controlPoints.get(0),controlPoints.get(1),epsilon, this);
			approximationCurves.add(tmp2);
			CubicBezier tmp3 = new CubicBezier(controlPoints.get(n - 1),controlPoints.get(0),controlPoints.get(1),controlPoints.get(2),epsilon, this);
			approximationCurves.add(tmp3);
		}	
	}
	
	/**
	 * Reverses the tangents and normals associated with this BSpline
	 */
	public void reverseNormalsAndTangents() {
		for(CubicBezier b : approximationCurves) {
			for(Vector2 p : b.getNormalReferences())
				p.mul(-1);
			for(Vector2 p : b.getTangentReferences())
				p.mul(-1);
		}
	}
	
	
	/**
	 * Given a closed curve and a sweep curve, fill the three GLBuffer objects appropriately. Here, we sweep the
	 * closed curve along the sweep curve
	 * @param crossSection, the BSpline cross section
	 * @param sweepCurve, the BSpline we are sweeping along
	 * @param data, a MeshData where we will output our triangle mesh
	 * @param scale > 0, parameter that controls how big the closed curve with respect to the sweep curve
	 */
	public static void build3DSweep(BSpline crossSection, BSpline sweepCurve, MeshData data, float scale) {
		//TODO A5
		//Our goal is to fill these arrays. Then we can put them in the buffers properly.
		
		
		/* Initialize the buffers for data.positions, data.normals, data.indices, and data.uvs as
		 * you did for A1.  Although you will not be using uv's, you DO need to initialize the
		 * buffer with space.  Don't forget to initialize data.indexCount and data.vertexCount.
		 * 
		 * Then set the data of positions / normals / indices with what you have calculated.
		 */
		
		//Calculate vertexCount and indexCount
		data.vertexCount = (crossSection.getPoints().size() + 1) * (sweepCurve.getPoints().size() + 1);
		int tris = 2 * crossSection.getPoints().size() * sweepCurve.getPoints().size();
		data.indexCount = 3 * tris;
		
		// Create Storage Spaces
		data.positions = NativeMem.createFloatBuffer(data.vertexCount * 3);
		data.uvs = NativeMem.createFloatBuffer(data.vertexCount * 2);
		data.normals = NativeMem.createFloatBuffer(data.vertexCount * 3);
		data.indices = NativeMem.createIntBuffer(data.indexCount);
		
		//Compute the data of positions, normals
		for(int j = 0; j < sweepCurve.getPoints().size(); j++){
			
			//Compute the new coordinate matrix based on t, n, b
			Vector3 points = new Vector3(sweepCurve.getPoints().get(j).x, 0, sweepCurve.getPoints().get(j).y);
			Vector3 t = new Vector3(sweepCurve.getTangents().get(j).x, 0, sweepCurve.getTangents().get(j).y);
			Vector3 n = new Vector3(sweepCurve.getNormals().get(j).x, 0, sweepCurve.getNormals().get(j).y);
			Vector3 b = t.clone().cross(n).normalize();
			Matrix4 tnb = new Matrix4(b, n.normalize(), t.negate().normalize(), points);
			
			//Transform each of the cross section points to tnb based coordinates
			for(int i = 0; i < crossSection.getPoints().size(); i++){
				//Do the coordinates transformation 
				Vector3 position = tnb.mulPos(new Vector3(crossSection.getPoints().get(i).x * scale, crossSection.getPoints().get(i).y * scale,0));
				Vector3 normal = tnb.mulDir(new Vector3(crossSection.getNormals().get(i).x, crossSection.getNormals().get(i).y, 0));
				
				//put the positions and normals in the buffer
				data.positions.put(position.x); data.positions.put(position.y); data.positions.put(position.z);
				data.normals.put(normal.x); data.normals.put(normal.y); data.normals.put(normal.z);
			}
			
			//If cross sections are closed, put the seam positions, normals in buffer
			if(crossSection.isClosed){
				//Do the coordinates transformation 
				Vector3 position = tnb.mulPos(new Vector3(crossSection.getPoints().get(0).x * scale, crossSection.getPoints().get(0).y * scale,0));
				Vector3 normal = tnb.mulDir(new Vector3(crossSection.getNormals().get(0).x, crossSection.getNormals().get(0).y, 0));
				
				//put the positions and normals in the buffer
				data.positions.put(position.x); data.positions.put(position.y); data.positions.put(position.z);
				data.normals.put(normal.x); data.normals.put(normal.y); data.normals.put(normal.z);
			}
		}
		
		//If sweep splines are closed, put the seam positions, normals in buffer
		if(sweepCurve.isClosed){
			//Compute the new coordinate matrix based on t, n, b
			Vector3 points = new Vector3(sweepCurve.getPoints().get(0).x, 0, sweepCurve.getPoints().get(0).y);
			Vector3 t = new Vector3(sweepCurve.getTangents().get(0).x, 0, sweepCurve.getTangents().get(0).y);
			Vector3 n = new Vector3(sweepCurve.getNormals().get(0).x, 0, sweepCurve.getNormals().get(0).y);
			Vector3 b = t.clone().cross(n).normalize();
			Matrix4 tnb = new Matrix4(b, n.normalize(), t.negate().normalize(), points);
			
            for(int i = 0; i < crossSection.getPoints().size(); i++){
				//Do the coordinates transformation 
				Vector3 position = tnb.mulPos(new Vector3(crossSection.getPoints().get(i).x * scale, crossSection.getPoints().get(i).y * scale,0));
				Vector3 normal = tnb.mulDir(new Vector3(crossSection.getNormals().get(i).x, crossSection.getNormals().get(i).y, 0));
				
				//put the positions and normals in the buffer
				data.positions.put(position.x); data.positions.put(position.y); data.positions.put(position.z);
				data.normals.put(normal.x); data.normals.put(normal.y); data.normals.put(normal.z);
			}
           
            //If cross sections are closed, put the seam positions, normals in buffer
			if(crossSection.isClosed){
				//Do the coordinates transformation 
				Vector3 position = tnb.mulPos(new Vector3(crossSection.getPoints().get(0).x * scale, crossSection.getPoints().get(0).y * scale,0));
				Vector3 normal = tnb.mulDir(new Vector3(crossSection.getNormals().get(0).x, crossSection.getNormals().get(0).y, 0));
				
				//put the positions and normals in the buffer
				data.positions.put(position.x); data.positions.put(position.y); data.positions.put(position.z);
				data.normals.put(normal.x); data.normals.put(normal.y); data.normals.put(normal.z);
			}
		}
		
		// Create The Indices, need to take the seams into consideration
		int sweepSize = sweepCurve.getPoints().size();
		if(!sweepCurve.isClosed)
			sweepSize--;
		int crossSize = crossSection.getPoints().size();
		if(crossSection.isClosed)
			crossSize++;
		for(int j = 0; j < sweepSize; j++) {
			int si = j * crossSize;
			for(int i = 0; i < crossSize - 1; i++) {
				data.indices.put(si);
				data.indices.put(si + crossSize);
				data.indices.put(si + 1);
				data.indices.put(si + 1);
				data.indices.put(si + crossSize);
				data.indices.put(si + crossSize + 1);
				si++;
			}
		}
	}

	public float getEpsilon() {
		return epsilon;
	}
}
