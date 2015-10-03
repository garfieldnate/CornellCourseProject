package cs4620.anim;

import java.util.HashMap;
import java.util.TreeSet;

import cs4620.common.Scene;
import cs4620.common.SceneObject;
import cs4620.common.event.SceneTransformationEvent;
import egl.math.Matrix4;
import egl.math.Vector3;
import egl.math.Vector4;
import egl.math.Matrix3;
import egl.math.Quat;

/**
 * A Component Resting Upon Scene That Gives
 * Animation Capabilities
 * @author Cristian
 *
 */
public class AnimationEngine {
	/**
	 * The First Frame In The Global Timeline
	 */
	private int frameStart = 0;
	/**
	 * The Last Frame In The Global Timeline
	 */
	private int frameEnd = 100;
	/**
	 * The Current Frame In The Global Timeline
	 */
	private int curFrame = 0;
	/**
	 * Scene Reference
	 */
	private final Scene scene;
	/**
	 * Animation Timelines That Map To Object Names
	 */
	public final HashMap<String, AnimTimeline> timelines = new HashMap<>();

	/**
	 * An Animation Engine That Works Only On A Certain Scene
	 * @param s The Working Scene
	 */
	public AnimationEngine(Scene s) {
		scene = s;
	}
	
	/**
	 * Set The First And Last Frame Of The Global Timeline
	 * @param start First Frame
	 * @param end Last Frame (Must Be Greater Than The First
	 */
	public void setTimelineBounds(int start, int end) {
		// Make Sure Our End Is Greater Than Our Start
		if(end < start) {
			int buf = end;
			end = start;
			start = buf;
		}
		
		frameStart = start;
		frameEnd = end;
		moveToFrame(curFrame);
	}
	/**
	 * Add An Animating Object
	 * @param oName Object Name
	 * @param o Object
	 */
	public void addObject(String oName, SceneObject o) {
		timelines.put(oName, new AnimTimeline(o));
	}
	/**
	 * Remove An Animating Object
	 * @param oName Object Name
	 */
	public void removeObject(String oName) {
		timelines.remove(oName);
	}

	/**
	 * Set The Frame Pointer To A Desired Frame (Will Be Bounded By The Global Timeline)
	 * @param f Desired Frame
	 */
	public void moveToFrame(int f) {
		if(f < frameStart) f = frameStart;
		else if(f > frameEnd) f = frameEnd;
		curFrame = f;
	}
	/**
	 * Looping Forwards Play
	 * @param n Number Of Frames To Move Forwards
	 */
	public void advance(int n) {
		curFrame += n;
		if(curFrame > frameEnd) curFrame = frameStart + (curFrame - frameEnd - 1);
	}
	/**
	 * Looping Backwards Play
	 * @param n Number Of Frames To Move Backwards
	 */
	public void rewind(int n) {
		curFrame -= n;
		if(curFrame < frameStart) curFrame = frameEnd - (frameStart - curFrame - 1);
	}

	public int getCurrentFrame() {
		return curFrame;
	}
	public int getFirstFrame() {
		return frameStart;
	}
	public int getLastFrame() {
		return frameEnd;
	}
	public int getNumFrames() {
		return frameEnd - frameStart + 1;
	}

	/**
	 * Adds A Keyframe For An Object At The Current Frame
	 * Using The Object's Transformation - (CONVENIENCE METHOD)
	 * @param oName Object Name
	 */
	public void addKeyframe(String oName) {
		AnimTimeline tl = timelines.get(oName);
		if(tl == null) return;
		tl.addKeyFrame(getCurrentFrame(), tl.object.transformation);
	}
	/**
	 * Removes A Keyframe For An Object At The Current Frame
	 * Using The Object's Transformation - (CONVENIENCE METHOD)
	 * @param oName Object Name
	 */
	public void removeKeyframe(String oName) {
		AnimTimeline tl = timelines.get(oName);
		if(tl == null) return;
		tl.removeKeyFrame(getCurrentFrame(), tl.object.transformation);
	}
	
	/**
	 * Loops Through All The Animating Objects And Updates Their Transformations To
	 * The Current Frame - For Each Updated Transformation, An Event Has To Be 
	 * Sent Through The Scene Notifying Everyone Of The Change
	 */
	public void updateTransformations() {
		// TODO: Loop Through All The Timelines
		// And Update Transformations Accordingly
		// (You WILL Need To Use this.scene)
		for(AnimTimeline tl : timelines.values()){
			//Set the transformation of frames beyond the extent of key frames as the first or the last of key frame transformation;
			AnimKeyframe current = new AnimKeyframe(curFrame);
			if(AnimKeyframe.COMPARATOR.compare(tl.frames.first(), current) > 0)
				tl.object.transformation.set(tl.frames.first().transformation);
			else if(AnimKeyframe.COMPARATOR.compare(tl.frames.last(), current) < 0)
				tl.object.transformation.set(tl.frames.last().transformation);
			
			//Compute the transformation when current frame is within the key frames
			else{
				AnimKeyframe f1 = tl.frames.floor(current);
				AnimKeyframe f2 = tl.frames.ceiling(current);
				Matrix4 interTransform = InterpolationHelper(curFrame, f1, f2);
				tl.object.transformation.set(interTransform);
			}
			
			//EXTRA CREDIT
			/*else{
				//Compute the four neighbor key frames
				AnimKeyframe f1 = tl.frames.floor(current);
				AnimKeyframe f2 = tl.frames.ceiling(current);
				AnimKeyframe f0 = tl.frames.floor(f1);
				AnimKeyframe f3 = tl.frames.ceiling(f2);
				//If f1 is the first key frame, set f0 as f1
				if(AnimKeyframe.COMPARATOR.compare(tl.frames.first(), f1) == 0)
					f0 = f1;
				//If f2 is the last key frame, set f3 as f2
				if(AnimKeyframe.COMPARATOR.compare(tl.frames.last(), f2) == 0)
					f3 = f2;
				Matrix4 interTransform = CatmullRomInter(curFrame, f0, f1, f2, f3);
				tl.object.transformation.set(interTransform);				
			}*/
			
			//Always keep this line!
			this.scene.sendEvent(new SceneTransformationEvent(tl.object));
		}
		
		
	}
	
	/**
	 * Given two key frames and the current frame between them, compute the transformation of the
	 * current frame via interpolation*/
	private Matrix4 InterpolationHelper(int curtFrame, AnimKeyframe former, AnimKeyframe latter){
		Matrix4 transf1 = former.transformation;
		Matrix4 transf2 = latter.transformation;
		int f1 = former.frame;
		int f2 = latter.frame;
		//If the two key frames are the same, return key frame transformation
		if(f1 == f2)
			return transf1;
		float t = (float)(curtFrame - f1) / (float)(f2 - f1);
		
		//Interpolate translation linearly
		Vector3 T1 = transf1.getTrans();
		Vector3 T2 = transf2.getTrans();
		Vector3 T = new Vector3();
		T.set(T1.clone().mul(1 - t).add(T2.clone().mul(t)));

		//Decomposing Transformation
		Matrix3 RS1 = transf1.getAxes();
		Matrix3 RS2 = transf2.getAxes();
		Matrix3 R1 = new Matrix3();
		Matrix3 R2 = new Matrix3();
		Matrix3 S1 = new Matrix3();
		Matrix3 S2 = new Matrix3();
		RS1.polar_decomp(R1, S1);
		RS2.polar_decomp(R2, S2);
		
		//Interpolate scale lineraly
		Matrix3 S = new Matrix3();
		S.interpolate(S1, S2, t);
		
		//Interpolate rotation
		Matrix3 R = new Matrix3();
		//Convert from a rotation matrix to a quaternion
		Quat Q1 = new Quat(R1);
		Quat Q2 = new Quat(R2);
		//Spherical Linear Interpolation
		double omg = Math.acos(Q1.w * Q2.w + Q1.y * Q2.y + Q1.x * Q2.x + Q1.z * Q2.z);
		if(omg != 0){
			Quat Q = new Quat();
			float a = (float)(Math.sin(omg * (1 - t)) / Math.sin(omg));
			float b = (float)(Math.sin(omg * t) / Math.sin(omg));
			Q.set(Q1.clone().setScaled(a, Q1).addScaled(b, Q2));
			//Convert from quaternion to rotation matrix
			Q.toRotationMatrix(R);
		}
		
		//Recompose the constituents
		Matrix3 RS = new Matrix3(R.clone().mulBefore(S));
		Matrix4 result = new Matrix4(RS.get(0, 0), RS.get(0, 1), RS.get(0, 2), T.x,
									 RS.get(1, 0), RS.get(1, 1), RS.get(1, 2), T.y,
									 RS.get(2, 0), RS.get(2, 1), RS.get(2, 2), T.z,
									 0, 0, 0, 1);
		return result;
	}
	
	/**
	 * Given four key frames, implement Catmull-Rom Spline Interpolation*/
	private Matrix4 CatmullRomInter(int curtFrame, AnimKeyframe first, AnimKeyframe second, AnimKeyframe third, AnimKeyframe forth){
		Matrix4 transf0 = first.transformation;
		Matrix4 transf1 = second.transformation;
		Matrix4 transf2 = third.transformation;
		Matrix4 transf3 = forth.transformation;
		int f1 = second.frame;
		int f2 = third.frame;
		//If the two key frames are the same, return key frame transformation
		if(f1 == f2)
			return transf1;
		float t = (float)(curtFrame - f1) / (float)(f2 - f1);
		Vector4 temp = new Vector4((float)(-0.5 * t * t * t + t * t - 0.5 * t),
				  					(float)(1.5 * t * t * t - 2.5 * t * t + 1),
				  					(float)(-1.5 * t * t * t + 2 * t * t + 0.5 * t),
				  					(float)(0.5 * t * t * t - 0.5 * t * t));
		
		//Interpolate translation linearly
		Vector3 T0 = transf0.getTrans();
		Vector3 T1 = transf1.getTrans();
		Vector3 T2 = transf2.getTrans();
		Vector3 T3 = transf3.getTrans();
		Vector3 T = new Vector3();
		T.set(T0.clone().mul(temp.x).addMultiple(temp.y, T1).addMultiple(temp.z, T2).addMultiple(temp.w, T3));
		
		//Decomposing Transformation
		Matrix3 RS0 = transf0.getAxes();
		Matrix3 RS1 = transf1.getAxes();
		Matrix3 RS2 = transf2.getAxes();
		Matrix3 RS3 = transf3.getAxes();
		Matrix3 R0 = new Matrix3();
		Matrix3 R1 = new Matrix3();
		Matrix3 R2 = new Matrix3();
		Matrix3 R3 = new Matrix3();
		Matrix3 S0 = new Matrix3();
		Matrix3 S1 = new Matrix3();
		Matrix3 S2 = new Matrix3();
		Matrix3 S3 = new Matrix3();
		RS0.polar_decomp(R0, S0);
		RS1.polar_decomp(R1, S1);
		RS2.polar_decomp(R2, S2);
		RS3.polar_decomp(R3, S3);
				
		//Interpolate scale lineraly
		Matrix3 S = new Matrix3();
		S.set(S0.clone().setScale(temp.x).add(S1.clone().setScale(temp.y)).add(S2.clone().setScale(temp.z)).add(S3.clone().setScale(temp.w)));
		
		//Interpolate rotation
		Matrix3 R = new Matrix3();
		//Convert from a rotation matrix to a quaternion
		Quat Q0 = new Quat(R0);
		Quat Q1 = new Quat(R1);
		Quat Q2 = new Quat(R2);
		Quat Q3 = new Quat(R3);
		//Spherical Linear Interpolation
		double omg = Math.acos(Q1.w * Q2.w + Q1.y * Q2.y + Q1.x * Q2.x + Q1.z * Q2.z);
		if(omg != 0){
			Quat Q = new Quat();
			float a0 = (float)(Math.sin(omg * (temp.x)) / Math.sin(omg));
			float a1 = (float)(Math.sin(omg * (temp.y)) / Math.sin(omg));
			float a2 = (float)(Math.sin(omg * (temp.z)) / Math.sin(omg));
			float a3 = (float)(Math.sin(omg * (temp.w)) / Math.sin(omg));
			Q.set(Q0.clone().setScaled(a0, Q0).addScaled(a1, Q1).addScaled(a2, Q2).addScaled(a3, Q3));
			//Convert from quaternion to rotation matrix
			Q.toRotationMatrix(R);
		}
		
		//Recompose the constituents
		Matrix3 RS = new Matrix3(R.clone().mulBefore(S));
		Matrix4 result = new Matrix4(RS.get(0, 0), RS.get(0, 1), RS.get(0, 2), T.x,
									 RS.get(1, 0), RS.get(1, 1), RS.get(1, 2), T.y,
									 RS.get(2, 0), RS.get(2, 1), RS.get(2, 2), T.z,
									 0, 0, 0, 1);
		return result;
	}
}
