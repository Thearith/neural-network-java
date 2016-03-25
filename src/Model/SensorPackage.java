package Model;
import Helper.Constants;


public class SensorPackage {
	
	private long timestamp;
	private SensorInstance accel;
	private SensorInstance gyro;
	private SensorInstance compass;
	
	public SensorPackage(SensorInstance accel, SensorInstance gyro, SensorInstance compass) {
		syncSensorDatas(accel, gyro, compass);
	}
	
	public void syncSensorDatas(SensorInstance accel, SensorInstance gyro, SensorInstance compass) {
		long accelTimestamp = accel.getTimestamp();
		long gyroTimestamp = gyro.getTimestamp();
		long compassTimestamp = compass.getTimestamp();
		
		if(accelTimestamp == gyroTimestamp && gyroTimestamp == compassTimestamp) {
			this.accel = accel;
			this.gyro = gyro;
			this.compass = compass;
			this.timestamp = accelTimestamp;
		}
	}
	
	public long getTimestamp() {
		return this.timestamp;
	}
	
	public SensorInstance getSensorInstance(int sensorType) {
		switch(sensorType) {
			case Constants.ACCEL:
				return accel;
			case Constants.GYRO:
				return gyro;
			case Constants.COMPASS:
				return compass;
		}
		
		return null;
	}
	
	public double getSensorData(int sensorType, int axis) {
		return getSensorInstance(sensorType).getData(axis);
	}
}
