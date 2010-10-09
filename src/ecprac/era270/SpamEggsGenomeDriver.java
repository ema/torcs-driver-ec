package ecprac.era270;

import ecprac.torcs.client.Action;
import ecprac.torcs.client.SensorModel;
import ecprac.torcs.controller.GenomeDriver;
import ecprac.torcs.genome.IGenome;

public class SpamEggsGenomeDriver extends GenericGenomeDriver {

	private int[] speed;

	public void loadGenome(IGenome genome) {
		if (genome instanceof SpamEggsGenome) {
			SpamEggsGenome llgenome = (SpamEggsGenome) genome;
			speed = llgenome.speed;
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

        // 9 - (max % 9)
        int speedIdx = max;
        if (speedIdx >= speed.length) {
            speedIdx = 9 - (speedIdx % 9);
        }

        action.accelerate = getAccel(speed[speedIdx], sensors.getSpeed());
        action.brake = getBrake(speed[speedIdx], sensors.getSpeed());
	}

	public String getDriverName() {
		return "Spam Eggs";
	}
}
