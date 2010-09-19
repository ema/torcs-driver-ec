package ecprac.era270;

import ecprac.torcs.client.Action;
import ecprac.torcs.client.SensorModel;
import ecprac.torcs.controller.GenomeDriver;
import ecprac.torcs.controller.extras.ABS;
import ecprac.torcs.controller.extras.AutomatedClutch;
import ecprac.torcs.controller.extras.AutomatedGearbox;
import ecprac.torcs.controller.extras.AutomatedRecovering;
import ecprac.torcs.genome.IGenome;

public class SpamEggsGenomeDriver extends GenomeDriver {

	private int maxSpeed;
	private double steering;
	private double trackpos;


	public void init() {
		enableExtras(new AutomatedClutch());
		enableExtras(new AutomatedGearbox());
		enableExtras(new AutomatedRecovering());
		enableExtras(new ABS());
	}

	public void loadGenome(IGenome genome) {

		if (genome instanceof SpamEggsGenome) {
			SpamEggsGenome llgenome = (SpamEggsGenome) genome;
			maxSpeed = llgenome.speed;
			steering = llgenome.steering;
			trackpos = llgenome.trackpos;
		} else {
			System.err.println("Invalid Genome assigned");
		}

	}

	public void control(Action action, SensorModel sensors) {
		if (sensors.getSpeed() < maxSpeed) {
			action.accelerate = 1;
		}

        int max = 1;
        double edgeSensors[] = sensors.getTrackEdgeSensors();

        /* finding out the position of the largest distance vector */
        for (int i=1; i < edgeSensors.length - 1; i++)
            if (edgeSensors[i] >= edgeSensors[max]) 
                max = i;
        /*
        double d = edgeSensors[max];
        double dl = edgeSensors[max + 1];
        double dr = edgeSensors[max - 1];
        */

        double steeringAmount = 0.1 * max - 0.9;
		action.steering = steeringAmount;
	}

	public String getDriverName() {
		return "Spam Eggs";
	}

	public float[] initAngles() {
		return super.initAngles();

	}
}
