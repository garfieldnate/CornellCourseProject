// package blockedPR;


import java.io.IOException;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.util.*;

/**
 * maps text file lines to output KV pair
 * <nodeID(u), node(u)>
 * 
 *
 */
public class FinalMapper extends Mapper<LongWritable, Text, IntWritable, NodeOrEdge> {
	
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
		
		int srcBlock = BlockedMapper.blockIDofNode(src.nodeid);
		
		context.write(new IntWritable(srcBlock), new NodeOrEdge(src));
	}
}
