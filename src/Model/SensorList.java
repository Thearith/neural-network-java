package Model;

import java.util.ArrayList;

public class SensorList {
	
	private ArrayList<SensorPackage> sensors;
	
	public SensorList() {
		sensors = new ArrayList<>();
	}
	
	public void addSensorPackage(SensorPackage sensorPackage) {
		sensors.add(sensorPackage);
	}
	
	public ArrayList<SensorPackage> getSensorList() {
		return sensors;
	}
	
	public SensorPackage getSensorPackage(int index) {
		return sensors.get(index);
	}
}
