package ecprac.era270;

import ecprac.torcs.client.Action;
import ecprac.torcs.client.SensorModel;
import ecprac.torcs.controller.GenomeDriver;
import ecprac.torcs.controller.extras.ABS;
import ecprac.torcs.controller.extras.AutomatedClutch;
import ecprac.torcs.controller.extras.AutomatedGearbox;
import ecprac.torcs.controller.extras.AutomatedRecovering;
import ecprac.torcs.genome.IGenome;

abstract class GenericGenomeDriver extends GenomeDriver {

	public abstract void loadGenome(IGenome genome);
	public abstract void control(Action action, SensorModel sensors);
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

	public float[] initAngles() {
		return super.initAngles();
	}
}
