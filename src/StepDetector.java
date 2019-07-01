import java.util.ArrayList;

import org.neuroph.core.NeuralNetwork;

import Features.FeatureExtraction;
import FileReaderWriter.FileReaderWriter;
import Helper.Constants;
import Model.SensorInstance;
import Model.SensorList;
import Model.SensorPackage;


public class StepDetector {
	public static void main(String args[]) {
		
		String minMaxlocation = Constants.FEATUREFILE_LOCATION + "/";
		ArrayList<Double> max = FileReaderWriter.getMaxMinFeatures(minMaxlocation + Constants.MAX_FEATURE_FILE);
		ArrayList<Double> min = FileReaderWriter.getMaxMinFeatures(minMaxlocation + Constants.MIN_FEATURE_FILE);
		
		
		
		for(int i=1; i<=Constants.NUM_TEST_FILES; i++) {
			System.out.println("File " + String.valueOf(i));
			SensorList sensorList = new SensorList();
			String location = Constants.TEST_FILE_LOCATION + "/" + String.valueOf(i) + "/";
			
			ArrayList<SensorInstance> accelList = FileReaderWriter.getSensorInstanceList(location + Constants.ACCEL_FILE);
			ArrayList<SensorInstance> gyroList = FileReaderWriter.getSensorInstanceList(location + Constants.GYRO_FILE);
			ArrayList<SensorInstance> compassList = FileReaderWriter.getSensorInstanceList(location + Constants.COMPASS_FILE);
		
			for(int j=0; j<accelList.size(); j++) {
				SensorPackage sensorPackage = new SensorPackage(accelList.get(j), gyroList.get(j), compassList.get(j));
				sensorList.addSensorPackage(sensorPackage);
			}
			
			ArrayList<Integer> peaks = extractPeaks(sensorList);
			System.out.println("Found " + getStepCount(nn, sensorList, peaks, max, min));
		}
	}
	
	private static int getStepCount(NeuralNetwork nn, SensorList sensorList, ArrayList<Integer> peaks, ArrayList<Double> max, ArrayList<Double> min) {
		int stepCount = 0;
		int currStepIndex = 0;
		
		while(currStepIndex < peaks.size()-1) {
			for(int j=currStepIndex+1; j<peaks.size(); j++) {
				FeatureExtraction fe = new FeatureExtraction();
				double[] features = fe.extractFeature(sensorList, currStepIndex, j);
				normalize(features, max, min);
				nn.setInput(features);
				double[] output = nn.getOutput();
				if(output[1] > output[0]) {
					System.out.println("Step " + stepCount + " " + currStepIndex + ", " + j);
					stepCount++;
					currStepIndex = j+1;
				}
					
			}
		
		}
		
		return stepCount;
	}
	
	private static void normalize(double[] features, ArrayList<Double> max, ArrayList<Double> min) {
		for(int i=0; i<features.length; i++) {
			features[i] = (features[i] - min.get(i)) / (max.get(i) - min.get(i));
		}
	}
	
	private static ArrayList<Integer> extractPeaks(SensorList sensorList) {
		ArrayList<SensorPackage> sensors = sensorList.getSensorList();
		
		ArrayList<Integer> peaksIndex = new ArrayList<>();
		peaksIndex.add(0);
		
		ArrayList<Integer> filterList = new ArrayList<>();
		SensorPackage sampleVal = sensors.get(0);
		for(int i=1; i<sensors.size(); i++) {
			SensorPackage sensorPackage = sensors.get(i);
			double accelY = sampleVal.getSensorData(Constants.ACCEL, Constants.AXIS_Y);
			double accelY1 = sensorPackage.getSensorData(Constants.ACCEL, Constants.AXIS_Y);
			
			if(Math.abs(accelY - accelY1) >= Constants.ACCEL_THRESHOLD) {
				filterList.add(i);
				sampleVal = sensorPackage;
			}
		}
		
		for(int i=1; i<filterList.size()-1; i++) {
			int prev = filterList.get(i-1);
			int curr = filterList.get(i);
			int next = filterList.get(i+1);
			
			double prevValue = sensors.get(prev).getSensorData(Constants.ACCEL, Constants.AXIS_Y);
			double currValue = sensors.get(curr).getSensorData(Constants.ACCEL, Constants.AXIS_Y);
			double nextValue = sensors.get(next).getSensorData(Constants.ACCEL, Constants.AXIS_Y);
			
			if(prevValue < currValue && currValue > nextValue) {
				peaksIndex.add(curr);
			}
		}
		
		return peaksIndex;
	}
}
