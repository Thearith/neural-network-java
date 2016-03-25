package Model;
import Helper.Constants;


/**
 * Created by thearith on 3/10/15.
 */
public class SensorInstance {

    /*
    * Constants
    * */

    private int sensorType;
    private double dataX;
    private double dataY;
    private double dataZ;
    private long timestamp;

    public SensorInstance(int sensorType, double dataX, double dataY, double dataZ, long timestamp) {
    	this.sensorType = sensorType;
        this.dataX = dataX;
        this.dataY = dataY;
        this.dataZ = dataZ;
        this.timestamp = timestamp;
    }

    /* public setter and getter methods */

    public double getDataX() {
        return this.dataX;
    }

    public double getDataY() {
        return this.dataY;
    }

    public double getDataZ() {
        return this.dataZ;
    }

    public int getSensorType() {
        return this.sensorType;
    }
    
    public long getTimestamp() {
    	return this.timestamp;
    }

    public double getData(int axis) {
        switch(axis) {
            case Constants.AXIS_X:
                return this.dataX;
            case Constants.AXIS_Y:
                return this.dataY;
            case Constants.AXIS_Z:
                return this.dataZ;
        }

        return this.dataX;
    }

    public SensorInstance clone() {
        return new SensorInstance(this.sensorType, this.dataX, this.dataY, this.dataZ, this.timestamp);
    }
}
