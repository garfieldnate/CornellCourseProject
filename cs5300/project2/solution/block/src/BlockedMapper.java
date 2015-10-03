// package blockedPR;


import java.io.IOException;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.util.*;

/**
 * maps text file lines to output KV pair
 * <nodeID(u), pr(u), [nodeID(v)]>
 * 
 *
 */
public class BlockedMapper extends Mapper<LongWritable, Text, IntWritable, NodeOrEdge> {
	public static final long NUM_NODES = 685230; // total number of nodes in the graph
	public static long[] blockBoundaries = {0, 10328, 20373, 30629, 40645,
			50462, 60841, 70591, 80118, 90497, 100501, 110567, 120945,
			130999, 140574, 150953, 161332, 171154, 181514, 191625, 202004,
			212383, 222762, 232593, 242878, 252938, 263149, 273210, 283473,
			293255, 303043, 313370, 323522, 333883, 343663, 353645, 363929,
			374236, 384554, 394929, 404712, 414617, 424747, 434707, 444489,
			454285, 464398, 474196, 484050, 493968, 503752, 514131, 524510,
			534709, 545088, 555467, 565846, 576225, 586604, 596585, 606367,
			616148, 626448, 636240, 646022, 655804, 665666, 675448, 685230 };
	
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		String line = value.toString().trim();
		String[] temp = line.split("\\s+");
		
		//construct a node
		Node src;
		if(temp.length == 2){
			src = new Node(Long.valueOf(temp[0]));
		}else{
			String[] words = temp[2].split(",");
			long[] outs = new long[words.length];
			for(int i = 0; i < words.length; i++){
				outs[i] = Long.valueOf(words[i]);
			}
			src = new Node(Long.valueOf(temp[0]), outs);
		}

		src.setPageRank(Double.valueOf(temp[1]));
		
		int srcBlock = blockIDofNode(src.nodeid);
		
		context.write(new IntWritable(srcBlock), new NodeOrEdge(src));
		//System.out.println("Node " + temp[0] + " " +  src.toString());
		if(src.outgoingSize() != 0){
			for(int i = 0; i < src.outgoingSize(); i++){
				Edge e = new Edge(src.nodeid, srcBlock, src.getPageRank() / (double) src.outgoingSize(), src.outgoingSize(), src.outgoing[i], blockIDofNode(src.outgoing[i]));
				//System.out.println(v);
				context.write(new IntWritable(e.vBlock), new NodeOrEdge(e));
			}
		}
	}
	
	//Find the blockId of each node
	public static int blockIDofNode(long nodeID){
		int result = 0;
		/*if(nodeID < blocks[0])
			return result;
		int min = 0;
		int max = blocks.length - 1;
		
		while(min <= max){
			int mid = (min + max) / 2;
			if(blocks[mid] > nodeID && blocks[mid - 1] <= nodeID)
				return mid;
			else if(blocks[mid] < nodeID)
				min = mid + 1;
			else
				max = mid - 1;
		}*/
		result = (int) Math.floor(nodeID / 10000);
		long testBoundary = blockBoundaries[result];
		if (nodeID < testBoundary) {
			result--;
		}
		return result;
	}

}
