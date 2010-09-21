package ecprac.awl900.controller;

import ecprac.awl900.SnorkGenome;
import ecprac.torcs.client.SensorModel;
import ecprac.utils.DriversUtils;

public class BasicAcclerationController implements AcclerationController {
	
	private SnorkGenome genome;

	BasicAcclerationController(SnorkGenome snorkGenome) {
		genome = snorkGenome;
	}
	
	public double calculateAccleration(SensorModel sensorModel, double steering) {
		double ret = 0.0;
		
		if (DriversUtils.isOffTrack(sensorModel)) {
			ret = 0.1;
		} else {
			double front = sensorModel.getTrackEdgeSensors()[9]; // no need to divide as
			                                                 // the steering ctrl did it
			                                                 // edge sensor values are not
			                                                 // copied :-(
			double speed = sensorModel.getSpeed();
			ret = front * genome._speedFactors[0] - (genome._speedFactors[2] - front) * speed *
				genome._speedFactors[1];
			System.out.println("accl: front=" + front);
			System.out.println("accl: speed=" + speed);
		}
		System.out.println("Accl Before clamping: " + ret);
		ret = clamp(ret);
		System.out.println("Returning accl " + ret);
		return ret;
	}

	private double clamp(double ret) {
		if (ret > 1.0) {
			return 1.0;
		} else if (ret < -1.0) {
			return -1.0;
		} else {
			return ret;
		}
	}
}