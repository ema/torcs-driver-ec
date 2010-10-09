package ecprac.era270;

import ecprac.torcs.client.Action;
import ecprac.torcs.client.SensorModel;
import ecprac.torcs.controller.GenomeDriver;
import ecprac.torcs.genome.IGenome;

public class NeuralGenomeDriver extends GenericGenomeDriver {
    private FeedForward network;

	public void loadGenome(IGenome genome) {
		if (genome instanceof NeuralGenome) {
			NeuralGenome llgenome = (NeuralGenome) genome;
			network = llgenome.network;
		} else {
			System.err.println("Invalid Genome assigned");
		}
	}

	public void control(Action action, SensorModel sensors) {
        int max = 1;
        double edgeSensors[] = sensors.getTrackEdgeSensors();

        /* finding out the position of the largest distance vector */
        for (int i=1; i < edgeSensors.length - 1; i++)
            if (edgeSensors[i] >= edgeSensors[max]) 
                max = i;

        double steeringAmount = 0.1 * max - 0.9;
		action.steering = steeringAmount;

        network.changeInputs(edgeSensors);
        
        double wantedSpeed = 200 * network.getOutput()[0];

        action.accelerate = getAccel((int)wantedSpeed, sensors.getSpeed());
        action.brake = getBrake((int)wantedSpeed, sensors.getSpeed());
    }

	public String getDriverName() {
		return "Neural Driver";
	}
}
