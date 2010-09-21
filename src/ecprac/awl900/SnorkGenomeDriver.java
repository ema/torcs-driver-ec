package ecprac.awl900;

import ecprac.awl900.controller.AcclerationController;
import ecprac.awl900.controller.ControllerFactory;
import ecprac.awl900.controller.SteeringController;
import ecprac.torcs.client.Action;
import ecprac.torcs.client.SensorModel;
import ecprac.torcs.controller.GenomeDriver;
import ecprac.torcs.controller.extras.ABS;
import ecprac.torcs.controller.extras.AutomatedClutch;
import ecprac.torcs.controller.extras.AutomatedGearbox;
import ecprac.torcs.controller.extras.AutomatedRecovering;
import ecprac.torcs.genome.IGenome;

public class SnorkGenomeDriver extends GenomeDriver {
	
	public static final String DRIVER_NAME = "Snork";
	
	private float[] internalAngles;
	private final int EDGE_SENSOR_COUNT = 19;
	private final float EDGE_SENSOR_MIN = -90.0f;
	private final float EDGE_SENSOR_STEP = 10.0f;
	
	private SteeringController steerCtrl;
	private AcclerationController acclCtrl;
	private SnorkGenome genome;
	
	@Override
	public void control(Action act, SensorModel sens) {
		
	//	printTrackPosition(sens);
	//	printAngle(sens);
	//	printTrackEdgeSensors(sens);
	//	printFocusSensors(sens);
		
		act.steering = steerCtrl.calculateSteering(sens);
		
		double accl = acclCtrl.calculateAccleration(sens, act.steering);
		if (accl < 0.0f) {
			act.brake = -accl;
		} else {
			act.accelerate = accl;
		}
	}
		
	public void init() {
		System.out.println("init");
		enableExtras(new AutomatedClutch());
		enableExtras(new AutomatedGearbox());
		enableExtras(new AutomatedRecovering());
		enableExtras(new ABS());
	}

	@Override
	public void loadGenome(IGenome genome) {
		if (!(genome instanceof SnorkGenome)) {
			System.err.println(DRIVER_NAME + ": BAD GENOME TYPE");
			return;
		}
		SnorkGenome snorkGenome = (SnorkGenome) genome;
		this.genome = snorkGenome;
		
		steerCtrl = ControllerFactory.getSteeringController(snorkGenome);
		acclCtrl = ControllerFactory.getAcclerationController(snorkGenome);
	}

	@Override
	public String getDriverName() {
		return DRIVER_NAME;
	}
	
	
	public float[] initAngles() {
		internalAngles = new float[19];
		float next = EDGE_SENSOR_MIN;
		for (int i = 0; i < EDGE_SENSOR_COUNT; i++, next += EDGE_SENSOR_STEP) {
			internalAngles[i] = next;
		}
		return internalAngles;
	}
	
	
	private void printTrackEdgeSensors(SensorModel sens) {
		System.out.println("TrackEdgeSensors");
		double trackEdgeSensors[] = sens.getTrackEdgeSensors();
		for (int i = 0; i < EDGE_SENSOR_COUNT; i++) {
			System.out.println(internalAngles[i] + " = " + trackEdgeSensors[i]);
		}
		System.out.println("================");
	}
	
	private void printTrackPosition(SensorModel sens) {
		System.out.println("TrackPosition");
		System.out.println(sens.getTrackPosition());
		System.out.println("================");
		
	}
	
	private void printFocusSensors(SensorModel sens) {
		double focusSensors[] = sens.getFocusSensors();
		System.out.println("FocusSensors");
		for (double d : focusSensors) {
			System.out.println("  " + d);
		}
		System.out.println("================");
	}
	
	private void printAngle(SensorModel sens) {
		System.out.println("Angle");
		System.out.println(sens.getAngleToTrackAxis());
		System.out.println("================");
	}
	
	public SnorkGenome getGenome() {
		return this.genome;
	}
}
