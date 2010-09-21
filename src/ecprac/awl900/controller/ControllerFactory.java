package ecprac.awl900.controller;

import ecprac.awl900.SnorkGenome;

public class ControllerFactory {

	public static SteeringController getSteeringController(
			SnorkGenome snorkGenome) {
		return new BasicSteeringController(snorkGenome);
	}

	public static AcclerationController getAcclerationController(
			SnorkGenome snorkGenome) {
		return new BasicAcclerationController(snorkGenome);
	}

}
