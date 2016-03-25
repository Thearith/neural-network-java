package FileReaderWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import Model.SensorInstance;


public class FileReaderWriter {

	public static ArrayList<String> readFromFile(String fileName) {
		BufferedReader br = null;
		ArrayList<String> lines = new ArrayList<>();
		
		try {
            br = new BufferedReader(new FileReader(fileName));
            
            String line;
            while ( (line = br.readLine()) != null ) {
                lines.add(line);
            } 
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
        	try {
        		if(br != null) {
        			br.close();
        		}
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
		
		return lines;
	}
	
	public static ArrayList<SensorInstance> getSensorInstanceList(String fileName) {
		ArrayList<SensorInstance> list = new ArrayList<>();
		ArrayList<String> lines = readFromFile(fileName);
		
		for(String line : lines) {
			String[] words = line.split("\t");
			int sensorType = Integer.parseInt(words[0]);
			double dataX = Double.parseDouble(words[1]);
			double dataY = Double.parseDouble(words[2]);
			double dataZ = Double.parseDouble(words[3]);
			long timestamp = Long.parseLong(words[4]);
			SensorInstance instance = new SensorInstance(sensorType, dataX, dataY, dataZ, timestamp);
			list.add(instance);
		}
		
		return list;
	}
	
	public static ArrayList<Integer> getHeelStrike(String fileName) {
		ArrayList<Integer> list = new ArrayList<>();
		ArrayList<String> lines = readFromFile(fileName);
		
		for(String line : lines) {
			String[] words = line.split(" ");
			int index = Integer.parseInt(words[0]);
			list.add(index);
		}
		
		return list;
	}
	
	public static void writeToFile(String fileName, ArrayList<String> outputs) {
		BufferedWriter bw = null;
		
		try{
           File file = new File(fileName);
           if (!file.exists()) {
               file.createNewFile();
           }
           
           bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
           
           for(String output : outputs)
        	   bw.write(output);
           
         } catch(Exception e){
             System.err.println(e);
         } finally {
        	 try {
	    		 if(bw != null) {
					bw.close();
	    		 }
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
	}
	
	public static void writeFeaturesToFile(String fileName, ArrayList<double[]> featuresList) {
		ArrayList<String> outputs = new ArrayList<>();
		for(double[] features : featuresList) {
			String output = "";
			for(double val : features)
				output += String.valueOf(val) + "\t";
			outputs.add(output);
		}
		
		writeToFile(fileName, outputs);
	}
	
	public static void writeNormalizerToFile(String fileName, double[] normalizerFeatures){
		ArrayList<String> outputs = new ArrayList<>();
		String output = "";
		for(double val : normalizerFeatures)
			output += String.valueOf(val) + "\t";
		outputs.add(output);
		
		writeToFile(fileName, outputs);
	}
}
