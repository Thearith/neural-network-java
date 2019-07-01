import java.util.ArrayList;

import Features.FeatureExtraction;
import FileReaderWriter.FileReaderWriter;
import Helper.Constants;
import Model.SensorInstance;
import Model.SensorList;
import Model.SensorPackage;


public class Main {

	public static void main(String[] args) {
		
		FeatureExtraction featureExtraction = new FeatureExtraction();
		
		for(int i=1; i<=Constants.NUM_RAW_FILES; i++) {
			
			System.out.println("File " + String.valueOf(i));
		
			SensorList sensorList = new SensorList();
			String location = Constants.RAW_FILE_LOCATION + "/" + String.valueOf(i) + "/";
			
			ArrayList<SensorInstance> accelList = FileReaderWriter.getSensorInstanceList(location + Constants.ACCEL_FILE);
			ArrayList<SensorInstance> gyroList = FileReaderWriter.getSensorInstanceList(location + Constants.GYRO_FILE);
			ArrayList<SensorInstance> compassList = FileReaderWriter.getSensorInstanceList(location + Constants.COMPASS_FILE);
		
			for(int j=0; j<accelList.size(); j++) {
				SensorPackage sensorPackage = new SensorPackage(accelList.get(j), gyroList.get(j), compassList.get(j));
				sensorList.addSensorPackage(sensorPackage);
			}
			
			ArrayList<Integer> heelStrikes = FileReaderWriter.getHeelStrike(location + Constants.HEEL_STRIKE_FILE);
			
			featureExtraction.extractFeatures(sensorList, heelStrikes);
		}
		
		featureExtraction.normalize();
		featureExtraction.shuffle();
		
		ArrayList<double[]> featuresList = featureExtraction.getFeatures();
		double[] maxFeatures = featureExtraction.getMaxFeatures();
		double[] minFeatures = featureExtraction.getMinFeatures();
		
		System.out.println(featuresList.size() + " " + featuresList.get(0).length);
		
		String location = Constants.FEATUREFILE_LOCATION + "/";
		
		FileReaderWriter.writeFeaturesToFile(location + Constants.FEAUTRE_FILE, featuresList);
		FileReaderWriter.writeNormalizerToFile(location + Constants.MAX_FEATURE_FILE, maxFeatures);
		FileReaderWriter.writeNormalizerToFile(location + Constants.MIN_FEATURE_FILE, minFeatures);
	}
}
