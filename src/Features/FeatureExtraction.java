package Features;

import java.util.ArrayList;
import java.util.Arrays;

import Helper.Constants;
import Model.SensorList;
import Model.SensorPackage;

public class FeatureExtraction {
	
	ArrayList<double[]> featureList;
	double[] maxFeatures;
	double[] minFeatures;
	
	public FeatureExtraction() {
		featureList = new ArrayList<>();
	}
	
	public ArrayList<double[]> getFeatures() {
		return this.featureList;
	}
	
	
	/*
	 * Normalization
	 * */
	
	public void normalize() {
		int length = featureList.get(0).length;
		maxFeatures = new double[length];
		minFeatures = new double[length];
		
		for(int i=0; i<maxFeatures.length; i++) {
			maxFeatures[i] = Double.MIN_VALUE;
			minFeatures[i] = Double.MAX_VALUE;
		}
		
		for(int index=0; index<featureList.size(); index++) {
			for(int i=0; i<maxFeatures.length; i++) {
				if(maxFeatures[i] < featureList.get(index)[i]) 
					maxFeatures[i] = featureList.get(index)[i];
				
				if(minFeatures[i] > featureList.get(index)[i]) 
					minFeatures[i] = featureList.get(index)[i];
			}
		}
		
		for(int index=0; index<featureList.size(); index++) {
			for(int i=0; i<maxFeatures.length; i++) {
				featureList.get(index)[i] = (featureList.get(index)[i] - minFeatures[i]) / 
						(maxFeatures[i] - minFeatures[i]);
			}
		}
	}
	
	public double[] getMaxFeatures() {
		return maxFeatures;
	}
	
	public double[] getMinFeatures() {
		return minFeatures;
	}
	
	
	
	/*
	 * Feature extraction
	 * */
	
	public void extractFeatures(SensorList sensorList, ArrayList<Integer> heelStrikePeaks){
		ArrayList<Integer> nonHeelStrikePeaks = extractPeaks(sensorList);
		extractFeatureList(sensorList, nonHeelStrikePeaks, Constants.NON_HEEL_STRIKE_CLASS);
		extractFeatureList(sensorList, heelStrikePeaks, Constants.HEEL_STRIKE_CLASS);
	}
	
	private void extractFeatureList(SensorList sensorList, ArrayList<Integer> indexList, int output) {
		for(int i=0; i<indexList.size(); i++) {
			int start = indexList.get(i);
			int end = indexList.get(i+1);
			double[] feature = extractFeature(sensorList, start, end, output);
			featureList.add(feature);
		}
	}
	
	private double[] extractFeature(SensorList sensorList, int start, int end, int output) {
		double[] accelFeatures = extractSensorFeature(sensorList, Constants.ACCEL, start, end);
		double[] gyroFeatures = extractSensorFeature(sensorList, Constants.GYRO, start, end);
		double[] compassFeatures = extractSensorFeature(sensorList, Constants.COMPASS, start, end);
		
		double[] feature = concat(accelFeatures, gyroFeatures, compassFeatures);
		double[] outputs = extractOutput(output);
		
		return concat(feature, outputs);
	}
	
	private double[] extractOutput(int output) {
		double[] outputs = new double[Constants.NUM_CLASSES];
		outputs[output] = 1;
		return outputs;
	}
	
	private double[] extractSensorFeature(SensorList sensorList, int sensorType, int start, int end) {
		double[] xAxisFeatures = extractAxisFeature(sensorList, sensorType, Constants.AXIS_X, start, end);
		double[] yAxisFeatures = extractAxisFeature(sensorList, sensorType, Constants.AXIS_Y, start, end);
		double[] zAxisFeatures = extractAxisFeature(sensorList, sensorType, Constants.AXIS_Z, start, end);
		
		return concat(xAxisFeatures, yAxisFeatures, zAxisFeatures);
	}
	
	private double[] extractAxisFeature(SensorList sensorList, int sensorType, int axis, int start, int end) {
		ArrayList<SensorPackage> sensors = sensorList.getSensorList();
		
		double mean = mean(sensors, sensorType, axis, start, end);
		double max = max(sensors, sensorType, axis, start, end);
		double min = min(sensors, sensorType, axis, start, end);
		double median = median(sensors, sensorType, axis, start, end);
		double range = range(sensors, sensorType, axis, start, end);
		double variance = variance(sensors, sensorType, axis, start, end);
		double std = standardDerivation(sensors, sensorType, axis, start, end);
		
		double[] diffList = diff(sensors, sensorType, axis, start, end);
		double meanDiff = mean(diffList);
        double medianDiff = median(diffList);
        double rangeDiff = range(diffList);
        double varianceDiff = variance(diffList);
        double stdDiff = standardDerivation(diffList);

        double peakToPeak = max - min;
        double thres1 = min + peakToPeak * 0.3f;
        double thres2 = min + peakToPeak * 0.5f;
        double thres3 = min + peakToPeak * 0.7f;
        double meanThres1 = mean(sensors, sensorType, axis, start, end, thres1);
        double meanThres2 = mean(sensors, sensorType, axis, start, end, thres2);
        double meanThres3 = mean(sensors, sensorType, axis, start, end, thres3);

        double timestamp = diffTimestamp(sensors, start, end);
		
        return new double[] {
                mean, max, min, median, range, variance, std,
                meanDiff, medianDiff, rangeDiff, varianceDiff, stdDiff,
                peakToPeak, meanThres1, meanThres2, meanThres3,
                timestamp
        };
	}
	
	
	/*
	 * Feature extraction helper method
	 * */
	
	private double mean(ArrayList<SensorPackage> sensors, int sensorType, int axis, int start, int end) {
        double sum = 0.0f;
        for(int index = start; index<end+1; index++) {
        	SensorPackage instance = sensors.get(index);
            sum += instance.getSensorData(sensorType, axis);
        }

        return sum / (end-start+1);
    }

    private double mean(ArrayList<SensorPackage> sensors, int sensorType, int axis, int start, int end, double threshold) {
        double sum = 0.0f;
        for(int index = start; index<end+1; index++) {
        	SensorPackage instance = sensors.get(index);
            double value = instance.getSensorData(sensorType, axis);
            if(value >= threshold) {
                sum += value;
            }
        }

        return sum / (end-start+1);
    }

    private double mean(double[] list) {
        double sum = 0.0f;
        for(double val : list) {
            sum += val;
        }

        return sum / list.length;
    }

    private double median(ArrayList<SensorPackage> sensors, int sensorType, int axis, int start, int end) {
        double[] list = extractAxisListFromSensorList(sensors, sensorType, axis, start, end);
        return median(list);
    }

    private double median(double[] list) {
        int size = list.length;
        Arrays.sort(list);

        if(size % 2 == 0) {
            return list[size/2];
        } else {
            return (list[size/2-1] + list[size/2]) / 2.0f;
        }
    }

    private double max(ArrayList<SensorPackage> sensors, int sensorType, int axis, int start, int end) {
        double max = Double.MIN_VALUE;
        for(int index = start; index<end+1; index++) {
        	SensorPackage instance = sensors.get(index);
            double value = instance.getSensorData(sensorType, axis);
            if(max < value) {
                max = value;
            }
        }

        return max;
    }

    private double max(double[] list) {
        double max = Double.MIN_VALUE;
        for(double val : list) {
            if(max < val) {
                max = val;
            }
        }

        return max;
    }

    private double min(ArrayList<SensorPackage> sensors, int sensorType, int axis, int start, int end) {
        double min = Double.MAX_VALUE;
        for(int index = start; index<end+1; index++) {
        	SensorPackage instance = sensors.get(index);
            double value = instance.getSensorData(sensorType, axis);
            if(min > value) {
                min = value;
            }
        }

        return min;
    }

    private double min(double[] list) {
        double min = Double.MAX_VALUE;
        for(double val : list) {
            if(min > val) {
                min = val;
            }
        }

        return min;
    }

    private double range(ArrayList<SensorPackage> sensors, int sensorType, int axis, int start, int end) {
        double max = max(sensors, sensorType, axis, start, end);
        double min = min(sensors, sensorType, axis, start, end);

        return max - min;
    }

    private double range(double[] list) {
        double max = max(list);
        double min = min(list);

        return max - min;
    }

    private double variance(ArrayList<SensorPackage> sensors, int sensorType, int axis, int start, int end) {
        double variance = 0.0f;
        double mean = mean(sensors, sensorType, axis, start, end);
        for(int index = start; index<end+1; index++) {
        	SensorPackage instance = sensors.get(index);
            double value = instance.getSensorData(sensorType, axis);
            variance += (value - mean) * (value - mean);
        }

        return variance / (end-start+1);
    }

    private double variance(double[] list) {
        double variance = 0.0f;
        double mean = mean(list);
        for(double val : list) {
            variance += (val - mean) * (val - mean);
        }

        return variance / list.length;
    }

    private double standardDerivation(ArrayList<SensorPackage> sensors, int sensorType, int axis, int start, int end) {
        return Math.sqrt(variance(sensors, sensorType, axis, start, end));
    }

    private double standardDerivation(double[] list) {
        return Math.sqrt(variance(list));
    }

    private double[] diff(ArrayList<SensorPackage> sensors, int sensorType, int axis, int start, int end) {
        double[] diffList = new double[(end-start+1)];
        for(int index = start; index<end; index++) {
            double value = sensors.get(index+1).getSensorData(sensorType, axis);
            double prevValue = sensors.get(index).getSensorData(sensorType, axis);
            diffList[index] = value - prevValue;
        }

        return diffList;
    }

    private long diffTimestamp(ArrayList<SensorPackage> sensors, int start, int end) {
        SensorPackage firstElement = sensors.get(0);
        SensorPackage lastElement = sensors.get(sensors.size()-1);
        return lastElement.getTimestamp() - firstElement.getTimestamp();
    }
	
	
	
	/*
	 * private methods
	 * */
	
	private ArrayList<Integer> extractPeaks(SensorList sensorList) {
		ArrayList<SensorPackage> sensors = sensorList.getSensorList();
		
		ArrayList<Integer> peaksIndex = new ArrayList<>();
		peaksIndex.add(0);
		
		ArrayList<Integer> filterList = new ArrayList<>();
		SensorPackage sampleVal = sensors.get(0);
		for(int i=1; i<sensors.size(); i++) {
			SensorPackage sensorPackage = sensors.get(i);
			double accelY = sampleVal.getSensorData(Constants.ACCEL, Constants.AXIS_Y);
			double accelY1 = sensorPackage.getSensorData(Constants.ACCEL, Constants.AXIS_Y);
			
			if(Math.abs(accelY - accelY1) <= Constants.ACCEL_THRESHOLD) {
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
	
	/*
	 * Helper method
	 * */
	
	private double[] extractAxisListFromSensorList(ArrayList<SensorPackage>sensors, int sensorType, int axis, 
			int start, int end) {
		double[] list = new double[sensors.size()];
		int index = 0;
        for(int i=start; i<end+1; i++) {
            list[index++] = sensors.get(i).getSensorData(sensorType, axis);
        }

        return list;
	}
	
	private double[] concat(double[] array1, double[] array2) {
		double[] concat = new double[array1.length + array2.length];
        System.arraycopy(array1, 0, concat, 0, array1.length);
        System.arraycopy(array2, 0, concat, array1.length, array2.length);
        
        return concat;
	}
	
	private double[] concat(double[] array1, double[] array2, double[] array3) {
        double[] concat = new double[array1.length + array2.length + array3.length];
        System.arraycopy(array1, 0, concat, 0, array1.length);
        System.arraycopy(array2, 0, concat, array1.length, array2.length);
        System.arraycopy(array3, 0, concat, array1.length + array2.length, array3.length);

        return concat;
    }
	
}
