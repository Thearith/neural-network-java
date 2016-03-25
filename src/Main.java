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
		
		for(int i=0; i<Constants.NUM_FILES; i++) {
		
			SensorList sensorList = new SensorList();
			
			ArrayList<SensorInstance> accelList = FileReaderWriter.getSensorInstanceList(Constants.ACCEL_FILE);
			ArrayList<SensorInstance> gyroList = FileReaderWriter.getSensorInstanceList(Constants.GYRO_FILE);
			ArrayList<SensorInstance> compassList = FileReaderWriter.getSensorInstanceList(Constants.COMPASS_FILE);
		
			for(int j=0; j<accelList.size(); j++) {
				SensorPackage sensorPackage = new SensorPackage(accelList.get(j), gyroList.get(j), compassList.get(j));
				sensorList.addSensorPackage(sensorPackage);
			}
			
			ArrayList<Integer> heelStrikes = FileReaderWriter.getHeelStrike(Constants.HEEL_STRIKE_FILE);
			
			featureExtraction.extractFeatures(sensorList, heelStrikes);
		}
		
		featureExtraction.normalize();
		
		ArrayList<double[]> featuresList = featureExtraction.getFeatures();
		double[] maxFeatures = featureExtraction.getMaxFeatures();
		double[] minFeatures = featureExtraction.getMinFeatures();
		
		for(double val : featuresList.get(0))
			System.out.print(val + " ");
		
		
		FileReaderWriter.writeFeaturesToFile(Constants.FEAUTRE_FILE, featuresList);
		FileReaderWriter.writeNormalizerToFile(Constants.MAX_FEATURE_FILE, maxFeatures);
		FileReaderWriter.writeNormalizerToFile(Constants.MIN_FEATURE_FILE, minFeatures);
	}
}
