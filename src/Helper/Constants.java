package Helper;

public class Constants {
	
	public static final int NUM_FILES				= 5;
	
	public static final String RAW_FILE_LOCATION	= "raw";
	public static final String ACCEL_FILE			= "Accel.txt";
	public static final String GYRO_FILE			= "Gyro.txt";
	public static final String COMPASS_FILE			= "Compass.txt";
	public static final String HEEL_STRIKE_FILE		= "Ground Truth.txt";
	
	public static final String FEAUTRE_FILE			= "FeatureList.txt";
	public static final String MAX_FEATURE_FILE		= "MaxFeatures.txt";
	public static final String MIN_FEATURE_FILE		= "MinFeatures.txt";
	
	public static final int NUM_CLASSES				= 2;
	public static final int HEEL_STRIKE_CLASS		= 1;
	public static final int NON_HEEL_STRIKE_CLASS	= 0;

	public static final int ACCEL					= 0;
	public static final int GYRO					= 1;
	public static final int COMPASS		 			= 2;

    public static final int AXIS_X          		= 0;
    public static final int AXIS_Y         			= 1;
    public static final int AXIS_Z          		= 2;
    
    public static final double ACCEL_THRESHOLD		= 0.45f;
}
