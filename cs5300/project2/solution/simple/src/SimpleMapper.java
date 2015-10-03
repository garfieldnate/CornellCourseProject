//package simple;

import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.util.*;

public class SimpleMapper extends Mapper<LongWritable, Text, LongWritable, NodeOrDouble>{
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException{
		//value is formatted as "nodeID PageRank {outgoing0, outgoing1, ...}"
		String line = value.toString();
		line = line.trim();
		
		String[] words = line.split("\\s+");
		
		//Construct the node
		long nid = Long.valueOf(words[0]);
		double pr = Double.valueOf(words[1]);
		Node n;
		
		//If the node has outgoing edges
		if(words.length == 3 && words[2] != ""){
			String[] outLinks = words[2].split(",");
			long[] outs = new long[outLinks.length];
			for(int i = 0; i < outLinks.length; i++){
				outs[i] = Long.valueOf(outLinks[i]);
			}
			n = new Node(nid, outs);
		}else{
			n = new Node(nid);
		}
		n.setPageRank(pr);
		context.write(new LongWritable(nid), new NodeOrDouble(n));
		
		//Perform uniform distribution of the pagerank value to its outgoings
		if(n.outgoingSize() != 0){
			double p = n.getPageRank() / (double) n.outgoingSize();
	   		for(long l: n.outgoing){
	     		context.write(new LongWritable(l), new NodeOrDouble(p));
	    	}
		}
	}
}
