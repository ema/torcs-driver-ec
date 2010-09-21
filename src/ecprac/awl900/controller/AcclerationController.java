package ecprac.awl900.controller;

import ecprac.torcs.client.SensorModel;

public interface AcclerationController {
	
	public double calculateAccleration(SensorModel sensorModel, double steering);
}
