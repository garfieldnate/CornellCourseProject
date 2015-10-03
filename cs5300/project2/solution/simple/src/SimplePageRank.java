//package simple;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.output.*;
import org.apache.hadoop.util.*;

public class SimplePageRank {
	private static double threshold = 0.001;
	public static long NODE_NUMBER = 685230;
	
	public static void main(String[] args) {
		//int numRepititions = 8;
		double residual = Double.MAX_VALUE;
		int i = 0;

		//Generate residual error output txt file
		try{
			/*File writename = new File(args[1] + "/simple_residual.txt");
			writename.createNewFile();
			BufferedWriter out = new BufferedWriter(new FileWriter(writename));*/
		
			do{	
				String in = args[0];
				String ot = args[1];
				Job job = new Job();
				
				job.setJobName("pagerank_" + i);
				job.setJar("SimplePageRank.jar");
				job.setOutputKeyClass(Text.class);
				job.setOutputValueClass(Text.class);
			
				//job.setInputFormatClass(TextInputFormat.class);
				//job.setOutputFormatClass(TextOutputFormat.class);
				//job.setJarByClass(SimplePageRank.class);
			
				job.setMapOutputKeyClass(LongWritable.class);
				job.setMapOutputValueClass(NodeOrDouble.class);
				
				job.setMapperClass(SimpleMapper.class);
				job.setReducerClass(SimpleReducer.class);
				
				String inputPath = (i == 0) ? in : (ot + "/stage" + (i - 1));
				String outputPath = ot + "/stage" + i;
			
				FileInputFormat.addInputPath(job, new Path(inputPath));
				FileOutputFormat.setOutputPath(job, new Path(outputPath));
			
				try{
					job.waitForCompletion(true);
				}catch(Exception e){
					System.err.println("ERROR IN JOB: " + e);
					return;
				}
				//Process the long value got from counters into double type
				long RESIDUAL = job.getCounters().findCounter(ResidualCounter.RESIDUALS).getValue();
				residual = ((double)RESIDUAL / 100000000.0) / (double)NODE_NUMBER;
			
				//print out the avg residual error
				System.out.println("Iteration " + String.valueOf(i) + " avg error " + String.valueOf(residual));
			
				//reset residual counter
				job.getCounters().findCounter(ResidualCounter.RESIDUALS).setValue(0L);
				i++;
			}while(residual > threshold);
		
			//out.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
