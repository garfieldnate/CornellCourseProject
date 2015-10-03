//package blockedPR;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.output.*;
import org.apache.hadoop.util.*;

/**
 * Runs blocked page rank
 * 
 *
 */
public class BlockedPageRank {
	public static final int NUM_BLOCKS = 68;

	public static enum COUNTERS {
		RESIDUAL_ERROR,
		REDUCE_TASKS,
		LOCAL_PR_ITERATIONS
	}
	
	public static void main(String[] args) {
		double superstepAvgResidualError = 100;
		int superstepPasses = 0;
		
		int maxSupersteps = 7;
		double threshold = 1e-3;
		
		try {
			//File writename = new File("./output/blocked_residual.txt");
			//writename.createNewFile();
			//PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(writename)));
			
			String outputPath; // the directory where mapreduce output files are written
			
			// do map reduce
			do {
				Job job = new Job();
				job.setJobName("blocked_map_reduce_" + superstepPasses);
				job.setJar("BlockedPageRank.jar");
				job.setOutputKeyClass(LongWritable.class);
				job.setOutputValueClass(Text.class);
			
				job.setInputFormatClass(TextInputFormat.class);
				job.setOutputFormatClass(TextOutputFormat.class);
			
				//job.setJarByClass(BlockedPageRank.class);
			
				job.setMapOutputKeyClass(IntWritable.class);
				job.setMapOutputValueClass(NodeOrEdge.class);
			
				job.setMapperClass(BlockedMapper.class);
				job.setReducerClass(BlockedReducer.class);
				
				// directory names to pass data between mapreduce super steps
				String inputPath = superstepPasses == 0 ? args[0] : (args[1] + "/superstep" + (superstepPasses - 1));
				outputPath = args[1] + "/superstep" + superstepPasses;
			
				FileInputFormat.addInputPath(job, new Path(inputPath));
				FileOutputFormat.setOutputPath(job, new Path(outputPath));
				
				try {
					job.waitForCompletion(true);
				} catch (Exception e) {
					System.err.println("Error in " + (superstepPasses + 1) + " superstep of mapreduce");
					e.printStackTrace();
					return;
				}
				
				// extract accounting from reducer nodes
				double superstepNetResidualError = (double)(job.getCounters().findCounter(COUNTERS.RESIDUAL_ERROR).getValue()) / BlockedReducer.RESIDUAL_SCALE;

				
				float avgIterationsPerBlock = (float)(job.getCounters().findCounter(COUNTERS.LOCAL_PR_ITERATIONS).getValue());
				avgIterationsPerBlock /= ((float)(job.getCounters().findCounter(COUNTERS.REDUCE_TASKS).getValue()));
				
				superstepAvgResidualError = superstepNetResidualError / BlockedMapper.NUM_NODES;
				
				//out.write("Iteration " + String.valueOf(superstepPasses) + " total residual error " + String.valueOf(superstepNetResidualError) + "\n");
				//out.write("Iteration " + String.valueOf(superstepPasses) + " avg local PageRank iterations " + String.valueOf(avgIterationsPerBlock) + "\n");
				//out.write("Iteration " + String.valueOf(superstepPasses) + " avg residual error " + String.valueOf(superstepAvgResidualError) + "\n");
				
				System.out.println("Iteration " + String.valueOf(superstepPasses) + " total residual error " + String.valueOf(superstepNetResidualError) + "\n");
				System.out.println("Iteration " + String.valueOf(superstepPasses) + " avg local PageRank iterations " + String.valueOf(avgIterationsPerBlock) + "\n");
				System.out.println("Iteration " + String.valueOf(superstepPasses) + " avg residual error " + String.valueOf(superstepAvgResidualError) + "\n");
				
				// reset counters
				job.getCounters().findCounter(COUNTERS.RESIDUAL_ERROR).setValue(0L);
				job.getCounters().findCounter(COUNTERS.LOCAL_PR_ITERATIONS).setValue(0L);
				job.getCounters().findCounter(COUNTERS.REDUCE_TASKS).setValue(0L);
				
				superstepPasses ++;

			} while (superstepAvgResidualError > threshold && superstepPasses < maxSupersteps);
			//out.close();
			
			// print results
			doFinalJob(outputPath, args[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Given the parameter where the last page rank mapreduce output was written
	 * open that directory and read in files 
	 * @param dir
	 * @throws IOException 
	 */
	public static void doFinalJob(String dir, String path) throws IOException {
		Job job = Job.getInstance();
		job.setJobName("pagerank_value_collection");
		job.setJar("BlockedPageRank.jar");
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
	
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
	
		//job.setJarByClass(BlockedPageRank.class);
	
		job.setMapOutputKeyClass(IntWritable.class);
		job.setMapOutputValueClass(NodeOrEdge.class);
	
		job.setMapperClass(FinalMapper.class);
		job.setReducerClass(FinalReducer.class);
		
		FileInputFormat.addInputPath(job, new Path(dir));
		FileOutputFormat.setOutputPath(job, new Path(path + "/final"));
		
		try {
			System.out.println("RUNNING MAP REDUCE TO EXTRACT PAGE RANK OUTPUT");
			System.out.println("using path : " + dir);
			job.waitForCompletion(true);
		} catch (Exception e) {
			System.err.println("Error in final step of mapreduce");
			e.printStackTrace();
			return;
		}
	}
	
	
}
