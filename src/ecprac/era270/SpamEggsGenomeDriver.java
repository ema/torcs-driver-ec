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

	private int[] speed;

	public void init() {
		enableExtras(new AutomatedClutch());
		enableExtras(new AutomatedGearbox());
		enableExtras(new AutomatedRecovering());
		enableExtras(new ABS());
	}

	public void loadGenome(IGenome genome) {

		if (genome instanceof SpamEggsGenome) {
			SpamEggsGenome llgenome = (SpamEggsGenome) genome;
			speed = llgenome.speed;
		} else {
			System.err.println("Invalid Genome assigned");
		}

	}

    private double getAccel(int targetSpeed, double curSpeed) {
        double accel = 0;

        if (targetSpeed - curSpeed > 0)
            accel = (targetSpeed - curSpeed) / 20;

        if (targetSpeed - curSpeed > 20)
            accel = 1;

        return accel;
    }

    private double getBrake(int targetSpeed, double curSpeed) {
        double brake = 0;
        
        if (targetSpeed - curSpeed < 0)
            brake = - (targetSpeed - curSpeed) / 20;

        if (targetSpeed - curSpeed < - 20)
            brake = 1;

        return brake;
    }

	public void control(Action action, SensorModel sensors) {
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

	public float[] initAngles() {
		return super.initAngles();

	}
}
