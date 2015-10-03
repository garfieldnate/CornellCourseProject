package cs4620.ray2.accel;

import cs4620.ray2.Ray;
import egl.math.Vector3d;

/**
 * A class representing a node in a bounding volume hierarchy.
 * 
 * @author pramook 
 */
public class BvhNode {

	/** The current bounding box for this tree node.
	 *  The bounding box is described by 
	 *  (minPt.x, minPt.y, minPt.z) - (maxBound.x, maxBound.y, maxBound.z).
	 */
	public final Vector3d minBound, maxBound;
	
	/**
	 * The array of children.
	 * child[0] is the left child.
	 * child[1] is the right child.
	 */
	public final BvhNode child[];

	/**
	 * The index of the first surface under this node. 
	 */
	public int surfaceIndexStart;
	
	/**
	 * The index of the surface next to the last surface under this node.	 
	 */
	public int surfaceIndexEnd; 
	
	/**
	 * Default constructor
	 */
	public BvhNode()
	{
		minBound = new Vector3d();
		maxBound = new Vector3d();
		child = new BvhNode[2];
		child[0] = null;
		child[1] = null;		
		surfaceIndexStart = -1;
		surfaceIndexEnd = -1;
	}
	
	/**
	 * Constructor where the user can specify the fields.
	 * @param minBound
	 * @param maxBound
	 * @param leftChild
	 * @param rightChild
	 * @param start
	 * @param end
	 */
	public BvhNode(Vector3d minBound, Vector3d maxBound, BvhNode leftChild, BvhNode rightChild, int start, int end) 
	{
		this.minBound = new Vector3d();
		this.minBound.set(minBound);
		this.maxBound = new Vector3d();
		this.maxBound.set(maxBound);
		this.child = new BvhNode[2];
		this.child[0] = leftChild;
		this.child[1] = rightChild;		   
		this.surfaceIndexStart = start;
		this.surfaceIndexEnd = end;
	}
	
	/**
	 * @return true if this node is a leaf node
	 */
	public boolean isLeaf()
	{
		return child[0] == null && child[1] == null; 
	}
	
	/** 
	 * Check if the ray intersects the bounding box.
	 * @param ray
	 * @return true if ray intersects the bounding box
	 */
	public boolean intersects(Ray ray) {
		// TODO#A7: fill in this function.
		// Check whether the given ray intersects the AABB of this BvhNode
		double tMin = Double.NEGATIVE_INFINITY;
		double tMax = Double.POSITIVE_INFINITY;
		Vector3d d = ray.direction;
		Vector3d p = ray.origin;
		
		//Bound In z dimension
		if(d.z > 0){
			tMin = (minBound.z - p.z) / d.z;
			tMax = (maxBound.z - p.z) / d.z;
		}
		else if(d.z < 0){
			tMax = (minBound.z - p.z) / d.z;
			tMin = (maxBound.z - p.z) / d.z;
		}
		//Bound in x dimension
		if(d.x > 0){
			tMin = Math.max((minBound.x - p.x) / d.x, tMin);
			tMax = Math.min((maxBound.x - p.x) / d.x, tMax);
		}
		else if(d.x < 0){
			tMin = Math.max((maxBound.x - p.x) / d.x, tMin);
			tMax = Math.min((minBound.x - p.x) / d.x, tMax);
		}
		//Bound in y dimension
		if(d.y > 0){
			tMin = Math.max((minBound.y - p.y) / d.y, tMin);
			tMax = Math.min((maxBound.y - p.y) / d.y, tMax);
		}
		else if(d.y < 0){
			tMin = Math.max((maxBound.y - p.y) / d.y, tMin);
			tMax = Math.min((minBound.y - p.y) / d.y, tMax);
		}
		
		if(tMin > tMax)
			return false;
		return true;
	}
}
