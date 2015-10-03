import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;

/**
 * this class is used to grab the final output of mapreduce
 * selecting only the nodes we're interested in
 * and printing their pagerank
 * where the pagerank is stored in an edge's data fields
 * 
 *
 */
public class FinalReducer extends Reducer<IntWritable, NodeOrEdge, Text, Text> {
	
	public void reduce(IntWritable key, Iterable<NodeOrEdge> values, Context context) throws IOException, InterruptedException {
		System.out.println("STARTING FINAL REDUCER");
		HashMap<Long, Double> pageranks = new HashMap<Long, Double>();
		long minNode = Long.MAX_VALUE;

		//File writename = new File("./output/blocked_residual.txt");
		//writename.createNewFile();
		//PrintWriter out = new PrintWriter( new BufferedWriter(new FileWriter(writename, true)) );
		
		//out.println("Selected Answers:");
		
		Iterator<NodeOrEdge> iter = values.iterator();
		while (iter.hasNext()) {
			NodeOrEdge temp = iter.next();
			
			Node n = null;
			if(temp.isNode()){
				n = temp.getNode();
				pageranks.put(n.nodeid, n.getPageRank());
				if(n.nodeid < minNode)
					minNode = n.nodeid;
			}
		}
		
		// write the pageranks of two least # nodes to file:
		//  node[0] PR (1.067450e+01) 1.557798e-05 
		//out.printf("Node[%d] PR %2.8e\n", minNode, pageranks.get(minNode));
		System.out.println("Node["+ minNode + "] PR " + pageranks.get(minNode));
		//out.printf("Node[%d] PR %2.8e\n", minNode + 1, pageranks.get(minNode + 1));
		System.out.println("Node["+ (minNode+1) + "] PR " + pageranks.get(minNode + 1));
		
		//out.close();
	}

}
