//package simple;

import java.util.*;
import java.io.*;
import org.apache.hadoop.io.Writable;

public class Node implements Writable {
	long nodeid;
	double pageRank;
	long[] outgoing;
	
    //Here for internal Hadoop purposes only. Don't use this constructor!
    public Node() {
    	nodeid = -1;
		outgoing = new long[0];
    }

    //Construct a node with no outgoing links.
    public Node(long nid) {
    	nodeid = nid;
    	outgoing = new long[0];
    }

    //Construct a node where the outgoing links are have nodeids in outs.
    public Node(long nid, long[] outs) {
    	nodeid = nid;
		outgoing = outs;
    }
    
    //Get the number of outgoing edges
    public int outgoingSize() {
    	return outgoing.length;
    }
    
    //Set the outgoing edges to be a new array
    public void setOutgoing(long[] outs) {
    	outgoing = outs;
    }
    
    //Get the PageRank of this node.
    public double getPageRank() {
    	return pageRank;
    }
    
    //Set the PageRank of this node
    public void setPageRank(double pr) {
    	pageRank = pr;
    }
    //Used for internal Hadoop purposes.
    //Describes how to write this node across a network
    public void write(DataOutput out) throws IOException {
	out.writeLong(nodeid);
	out.writeDouble(pageRank);
	for(long n : outgoing) {
	    out.writeLong(n);
	}
	out.writeLong(-1);
    }

    //Used for internal Hadoop purposes
    //Describes how to read this node from across a network
    public void readFields(DataInput in) throws IOException {
	nodeid = in.readLong();
	pageRank = in.readDouble();
	long next = in.readLong();
	ArrayList<Long> ins = new ArrayList<Long>();
	while (next != -1) {
	    ins.add(next);
	    next = in.readLong();
	}
	outgoing = new long[ins.size()];
	for(int i = 0; i < ins.size(); i++) {
	    outgoing[i] = ins.get(i);
	}
    }
    //Gives a human-readable representaton of the node.
    public String toString() {
		//String retv = nodeid + " ";
		String retv = pageRank + " ";
		String out = "";
		for(long n : outgoing) out += "" + n + ",";
		if(!out.equals("")) out = out.substring(0, out.length() - 1);
		retv += out;
		return retv;
    }
}
