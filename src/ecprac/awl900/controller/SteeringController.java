package ecprac.awl900.controller;

import ecprac.torcs.client.SensorModel;

public interface SteeringController {
	
	public double calculateSteering(SensorModel sensorModel);

}
