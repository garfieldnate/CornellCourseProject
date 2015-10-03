//package simple;

import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.util.*;

public class SimpleReducer extends Reducer<LongWritable, NodeOrDouble, Text, Text>{
	public void reduce(LongWritable key, Iterable<NodeOrDouble> values, Context context) throws IOException, InterruptedException{
		
		Iterator<NodeOrDouble> itr = values.iterator();
		double dampingFactor = 0.85;
		double oldPageRank = 0.0;
		
		//Compute the new page rank value and record the old one
		Node M = null;
		double newPageRank = 0.0;
		while(itr.hasNext()){
			NodeOrDouble p = itr.next();
			if (p.isNode()){
				M = p.getNode();
				oldPageRank = M.getPageRank();
			}
			else
				newPageRank += p.getDouble();
		}
		newPageRank = dampingFactor * newPageRank + (1.0 - dampingFactor) / (double)SimplePageRank.NODE_NUMBER;
		M.setPageRank(newPageRank);
		
		//Compute residual error and put it into the counter
		double r = Math.abs(newPageRank - oldPageRank) / newPageRank;
		long R = (long)(r * 100000000);
		context.getCounter(ResidualCounter.RESIDUALS).increment(R);
		
		context.write(new Text(String.valueOf(M.nodeid)), new Text(M.toString()));
	}
	
}
