// package blockedPR;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

/**
 * This class is the output from the Mapper
 * For a given edge <u,v> it resembles
 * <u, block#u, PR(u), outDegree(u), v, block#v>
 *
 */
public class Edge implements Writable {
	// the source node page rank must always be known (even if its just the
	// approximation in the first phase of pagerank() algorithm
	double pageRank; // u's current page rank
	long uId, vId; // nodeID of source/dest node of edge
	long uDegree; // u's outDegree
	int uBlock, vBlock;
	
	// constructor for Hadoop's internal purposes
	public Edge() { }
	
	public Edge(long u, int uB, double PR, long uD, long v, int vB) {
		this.uId = u;
		this.uBlock = uB;
		this.pageRank = PR;
		this.uDegree = uD;
		this.vId = v;
		this.vBlock = vB;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		uId = in.readLong();
		uBlock = in.readInt();
		pageRank = in.readDouble();
		uDegree = in.readLong();
		vId = in.readLong();
		vBlock = in.readInt();

	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeLong(uId);
		out.writeInt(uBlock);
		out.writeDouble(pageRank);
		out.writeLong(uDegree);
		out.writeLong(vId);
		out.writeInt(vBlock);

	}
	
	public Edge clone() {
		Edge k = new Edge(uId, uBlock, pageRank, uDegree, vId, vBlock);
		return k;
	}
	
	public void setNewPageRank(double pr) {
		this.pageRank = pr;
	}
	
	public long getSourceNode() { return this.uId; }
	public long getDestNode()	{ return this.vId; }
	public double getPR()		{ return this.pageRank; }
	public int getSourceBlock()	{ return this.uBlock; }
	public int getDestBlock()	{ return this.vBlock; }
	public double getUDegree()	{ return (double)this.uDegree; }
	
	@Override
	public String toString() {
		//	"nodeID(u),block#u,PR(u),outdegree(u),nodeID(v),block#v"
		String s = "%d,%d,%.7f,%d,%d,%d";
		s = String.format(s, uId, uBlock, pageRank, uDegree, vId, vBlock);
		return s;
		
	}


}
