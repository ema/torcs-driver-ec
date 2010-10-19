package ecprac.era270;

import ecprac.torcs.client.Action;
import ecprac.torcs.client.SensorModel;
import ecprac.torcs.controller.GenomeDriver;
import ecprac.torcs.controller.extras.ABS;
import ecprac.torcs.controller.extras.AutomatedClutch;
import ecprac.torcs.controller.extras.AutomatedGearbox;
import ecprac.torcs.controller.extras.AutomatedRecovering;
import ecprac.torcs.genome.IGenome;

import ecprac.utils.DriversUtils;

import java.lang.Math;

abstract class GenericGenomeDriver extends GenomeDriver {

	public abstract void loadGenome(IGenome genome);
	public abstract String getDriverName();

	public void init() {
		enableExtras(new AutomatedClutch());
		enableExtras(new AutomatedGearbox());
		enableExtras(new AutomatedRecovering());
		enableExtras(new ABS());
	}

    protected double getAccel(int targetSpeed, double curSpeed) {
        double accel = 0;

        if (targetSpeed - curSpeed > 0)
            accel = (targetSpeed - curSpeed) / 20;

        if (targetSpeed - curSpeed > 20)
            accel = 1;

        return accel;
    }

    protected double getBrake(int targetSpeed, double curSpeed) {
        double brake = 0;
        
        if (targetSpeed - curSpeed < 0)
            brake = - (targetSpeed - curSpeed) / 20;

        if (targetSpeed - curSpeed < - 20)
            brake = 1;

        return brake;
    }

    protected int headingTowards(SensorModel sensors) {
        int max = 1;
        double edgeSensors[] = sensors.getTrackEdgeSensors();

        /* finding out the position of the largest distance vector */
        for (int i=1; i < edgeSensors.length - 1; i++)
            if (edgeSensors[i] >= edgeSensors[max]) 
                max = i;

        return max;
    }

    protected boolean opponentToOverTake(SensorModel sensors) {
        double opponents[] = sensors.getOpponentSensors();
        return opponents[17] < 20 || opponents[18] < 20 || opponents[19] < 20;
    }

    public void control(Action action, SensorModel sensors) {
        /* basic driving skills */
        int curDirection = headingTowards(sensors);
        
        action.steering = 0.1 * curDirection - 0.9;

        Double nextCorner = Math.abs(DriversUtils.sharpnessOfNextCorner(sensors));

        if (nextCorner.isNaN() || nextCorner == 0) {
            action.accelerate = 1;
            action.brake = 0;
        }
        else {
            double curSpeed = sensors.getSpeed();
            int targetSpeed = (int)curSpeed;

            if (nextCorner >= 20)
                targetSpeed = 110;

            if (nextCorner >= 50)
                targetSpeed = 70;

            if (nextCorner >= 80)
                targetSpeed = 40;

            action.accelerate = getAccel(targetSpeed, curSpeed);
            action.brake = getBrake(targetSpeed, curSpeed);
        }
    }

	public float[] initAngles() {
		return super.initAngles();
	}
}
