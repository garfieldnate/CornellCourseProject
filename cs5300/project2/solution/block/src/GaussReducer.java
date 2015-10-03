// package blockedPR;

import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.util.*;

public class GaussReducer extends Reducer<IntWritable, NodeOrEdge, LongWritable, Text> {
	private static final double DAMPING_FACTOR = 0.85;
	private static final double RANDOM_SURFER_PROB =  (1 - DAMPING_FACTOR) / ((double)BlockedMapper.NUM_NODES);
	public static final long RESIDUAL_SCALE = 100000000;
	private static final int MAX_REPITATIONS = 20;
	private static final double THRESHOLD = 0.001;
	private HashMap<Long, Node> nodeTable = new HashMap<Long, Node>();
	private HashMap<Long, Double> finalPageRank = new HashMap<Long, Double>();
	private HashMap<Long, Double> boundaryConditions = new HashMap<Long, Double>();
	private HashMap<Long, ArrayList<Long>> srcNodeTable = new HashMap<Long, ArrayList<Long>>();
	
	public void reduce(IntWritable key, Iterable<NodeOrEdge> values, Context context) throws IOException, InterruptedException {
		//Clean the tables
		nodeTable.clear();
		finalPageRank.clear();
		boundaryConditions.clear();
		srcNodeTable.clear();

		Iterator<NodeOrEdge> iter = values.iterator();
		while(iter.hasNext()){
			NodeOrEdge temp = iter.next();
			
			//Construct nodes in this block
			if(temp.isNode()){
				Node n = temp.getNode();
				
				//Put the new node into nodeTable and corresponding table
				nodeTable.put(n.nodeid, n);
				finalPageRank.put(n.nodeid, n.getPageRank());
				
			//Collect srcNode inside the block and boundaryCondition pagerank
			}else{
				Edge e = temp.getEdge();
				long dst = e.vId;

				//If the source node inside this block
				if(key.get() == e.uBlock){
					ArrayList<Long> t;
					if(srcNodeTable.containsKey(dst)){
						t = srcNodeTable.get(dst);
					}else{
						t = new ArrayList<Long>();
					}
					
					t.add(e.uId);
					srcNodeTable.put(dst, t);
				//If the source node outside the block
				}else{
					double t = 0;
					if(boundaryConditions.containsKey(dst)){
						t = boundaryConditions.get(dst);
					}
					t += e.pageRank;
					boundaryConditions.put(dst, t);
				}
			}
		}
		
		// Iterate inside the block
		int passes = 0;
		double residualError = 0.0;
		do{
			residualError = IterateBlockOnce();
			passes++;
			//System.out.println("Block:" + key.get() +"," + "Passes:" + passes + "," + "Error: " + residualError);
		}while(passes < MAX_REPITATIONS && residualError > THRESHOLD);
		
		//Record block number and #iterations in each block
		context.getCounter(BlockedPageRank.COUNTERS.LOCAL_PR_ITERATIONS).increment(passes);
		context.getCounter(BlockedPageRank.COUNTERS.REDUCE_TASKS).increment(1L);
		
		//Calculate the residual error over the entire block
		double blockResidual = 0.0;
		for(long d: nodeTable.keySet()){
			blockResidual += Math.abs(nodeTable.get(d).getPageRank() - finalPageRank.get(d)) / finalPageRank.get(d);
			//emit to mapper
			Node dst = nodeTable.get(d);
			dst.setPageRank(finalPageRank.get(d));
			context.write(new LongWritable(dst.nodeid), new Text(dst.toString()));
		}

		//Record the blockResidual in counter
		long R = (long)(Math.floor(blockResidual * RESIDUAL_SCALE)); // scaled net residual error
		context.getCounter(BlockedPageRank.COUNTERS.RESIDUAL_ERROR).increment(R);	 
		//cleanup(context);
	}
	
	//Implement inner loop of this block

	public double IterateBlockOnce(){
		double residual = 0.0;
		HashMap<Long, Double> newPageRank = new HashMap<Long, Double>();
		for(long d: nodeTable.keySet()){
			newPageRank.put(d, 0.0); 
		}
		for(long d: nodeTable.keySet()){
			double nextPageRank = 0;
			//Deal with source node inside the block
			// x_i(k + 1) = (1-d) + d * sum(a * x_j(k + 1)) + d * sum(a * x_j(k))
			//                      j < i                       j > i
			if(srcNodeTable.containsKey(d)){
				for(long s: srcNodeTable.get(d)){
					if(newPageRank.get(s) > 0)
						nextPageRank += newPageRank.get(s) / (double)nodeTable.get(s).outgoingSize();
					else
						nextPageRank += finalPageRank.get(s) / (double)nodeTable.get(s).outgoingSize();
				}
			}
			//Deal with source node outside the block
			if(boundaryConditions.containsKey(d)){
				nextPageRank += boundaryConditions.get(d);
			}
			
			nextPageRank = DAMPING_FACTOR * nextPageRank + RANDOM_SURFER_PROB;
			
			residual += Math.abs(finalPageRank.get(d) - nextPageRank) / nextPageRank;
			
			newPageRank.put(d, nextPageRank);
		}
		finalPageRank.putAll(newPageRank);
		residual /= nodeTable.size();
		return residual;
	}
	
}
