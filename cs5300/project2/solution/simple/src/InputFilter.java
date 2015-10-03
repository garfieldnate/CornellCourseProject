import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * Pre-filter the edges.txt file and reject 1% of it.
 */

public class InputFilter{
	private static double fromNetID = 0.638;
	private static double rejectMin = 0.9 * fromNetID;
	private static double rejectLimit = rejectMin + 0.01;
	
	public static void main(String[] args){
		try{
			//Read blocks file into an array
			URL url = new URL("http://edu-cornell-cs-cs5300s15-proj2.s3.amazonaws.com/blocks.txt");
			InputStreamReader r = new InputStreamReader(url.openStream());
			BufferedReader bufferReader = new BufferedReader(r);
				
			String l = "";
			int i = 0;
			long[] blocks = new long[68];
			do{ 
				l = bufferReader.readLine();
				if(l != null && (!l.equals(""))){
					l = l.trim();
					if(i == 0)
						blocks[i] = Long.valueOf(l);
					else
						blocks[i] = Long.valueOf(l) + blocks[i - 1];
					i++;
				}
			}while(l != null);
				
			//Read edges file
			String pathname = "http://edu-cornell-cs-cs5300s15-proj2.s3.amazonaws.com/edges.txt";
			URL filename = new URL(pathname);
			InputStreamReader reader = new InputStreamReader(filename.openStream());
			BufferedReader br = new BufferedReader(reader);
			
			//Write raw filtered file
			File writename = new File("./FilteredInput/edges.txt");
			writename.createNewFile();
			BufferedWriter out = new BufferedWriter(new FileWriter(writename));
			
			//Write input file for simple pagerank
			File writenameSimple = new File("./FilteredInput/SimpleInput.txt");
			writenameSimple.createNewFile();
			BufferedWriter outSimple = new BufferedWriter(new FileWriter(writenameSimple));
			
			String line = "";
			long index = 0;
			String pr = String.valueOf(1.0 / 685230.0);
			String outputSimple = "0 " + pr + " ";
			do{
				line = br.readLine();
				if(line != null && (!line.equals(""))){
					//Perform filter and attach the block id
					line = line.trim();
					String[] words = line.split("\\s+");
					double value = Double.valueOf(words[0]);
					
					if(selectInputLine(value)){
						//Find the source node id and destination node id
						long srcNode = Long.valueOf(words[1]);
						long dstNode = Long.valueOf(words[2]);
						
						//print out for the raw filtered file
						String outString = String.valueOf(srcNode) + "," + String.valueOf(blockIDofNode(srcNode, blocks)) + 
								 " " + String.valueOf(dstNode) + "," + String.valueOf(blockIDofNode(dstNode, blocks)) + "\n";
						//System.out.println(outString);
						out.write(outString);
						
						//format file for simple page rank
						if(index != srcNode){
							outSimple.write(outputSimple.substring(0, outputSimple.length() - 1) + "\n");
							//System.out.println(outputSimple.substring(0, outputSimple.length() - 1) + "\n");
							index += 1;
							outputSimple = String.valueOf(index) + " " + pr + " ";
							//deal with isolated nodes
							while(index != srcNode){
								outSimple.write(outputSimple.substring(0, outputSimple.length() - 1) + "\n");
								//System.out.println(outputSimple.substring(0, outputSimple.length() - 1) + "\n");
								index += 1;
								outputSimple = String.valueOf(index) + " " + pr + " ";
							}
							outputSimple += String.valueOf(dstNode) + ",";
							if(index == 685229)
								outSimple.write(outputSimple.substring(0, outputSimple.length() - 1) + "\n");
						}else{
							outputSimple += String.valueOf(dstNode) + ",";
						}
					}
				}
			}while(line != null);
			
			out.close();
			outSimple.close();
			
			/*File filename = new File("./FilteredInput/edges.txt");
			FileReader r = new FileReader(filename);
			BufferedReader bufferReader = new BufferedReader(r);
			
			//Generate input file for blocked page rank
			File writename = new File("./FilteredInput/BlockedInput.txt");
			writename.createNewFile();
			BufferedWriter out = new BufferedWriter(new FileWriter(writename));
			
			String line = "";
			String previous = "0,0";
			int degree = 0;
			ArrayList<String> ls = new ArrayList<String>();
			
			do{
				line = bufferReader.readLine();
				if(line != null && !(line.equals(""))){
					line = line.trim();
	
					String[] temp = line.split("\\s+");
					if(!temp[0].equals(previous)){
						for(String l: ls){
							String[] t = l.split("\\s+");
							out.write(t[0] + ",-1," + String.valueOf(degree) + "," + t[1] + "\n");
							System.out.println(t[0] + ",-1," + String.valueOf(degree) + "," + t[1] + "\n");
						}
						//Reset values
						degree = 1;
						ls.clear();
						ls.add(line);
						previous = temp[0];
						//System.out.println("previous" + previous);
						
					}else{
						//System.out.println(temp[0]);
						degree++;
						ls.add(line);
					}
				}
			}while(line != null);
			
			out.close();*/
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//Filter the input line
	public static boolean selectInputLine(double x){
		return ( ((x >= rejectMin) && (x < rejectLimit)) ? false : true );
	}
	
	//Find the blockId of each node
	public static long blockIDofNode(long nodeID, long[] blocks){
		long result = 0;
		if(nodeID < blocks[0])
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
		}
		return result;
	}
}