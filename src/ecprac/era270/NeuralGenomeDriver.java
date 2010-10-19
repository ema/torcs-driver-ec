package ecprac.era270;

import java.util.Arrays;

import ecprac.torcs.client.Action;
import ecprac.torcs.client.SensorModel;
import ecprac.torcs.controller.GenomeDriver;
import ecprac.torcs.genome.IGenome;

import ecprac.utils.DriversUtils;

import java.lang.Math;

public class NeuralGenomeDriver extends GenericGenomeDriver {
    private FeedForward drivingNetwork;

    private int ticksCollision=0, ticksOutside=0;

	public void loadGenome(IGenome genome) {
		if (genome instanceof NeuralGenome) {
			NeuralGenome llgenome = (NeuralGenome) genome;
			drivingNetwork = llgenome.drivingNetwork;
		} else {
			System.err.println("Invalid Genome assigned");
		}
	}

    private double[] trimmedEdgeSensors(SensorModel sensors) {
        double edgeSensors[] = sensors.getTrackEdgeSensors();
        double[] trimmedSensors = new double[5];

        for (int i=3, j=0; i<17; i+=3, j++)
            trimmedSensors[j] = edgeSensors[i];

        return trimmedSensors;
    }

    private double improvedFrontalSensor(SensorModel sensors) {
        double edgeSensors[] = sensors.getTrackEdgeSensors();
        double frontal[] = new double[3];

        for (int i=0; i<3; i++)
            frontal[i] = edgeSensors[7+i];

        Arrays.sort(frontal);

        // maximum value
        return frontal[2];
    }

    private void changeNetworkInputs(SensorModel sensors) {
        double edgeSensors[] = trimmedEdgeSensors(sensors);
        double drivingNetworkInput[] = new double[8];

        /* populating the drivingNetworkInput */
        int i=0;
        for (; i<edgeSensors.length; i++)
            drivingNetworkInput[i] = edgeSensors[i];

        // normalized track pos
        drivingNetworkInput[i] = 200 * sensors.getTrackPosition();
        drivingNetworkInput[i+1] = sensors.getSpeed();
        drivingNetworkInput[i+2] = improvedFrontalSensor(sensors);

        drivingNetwork.changeInputs(drivingNetworkInput);
    }

	public void control(Action action, SensorModel sensors) {
        super.control(action, sensors);

        if (improvedFrontalSensor(sensors) > 190) 
            return;

        changeNetworkInputs(sensors);

        double netOutput[] = drivingNetwork.getOutput();

        // steering
        double steeringAmount = Math.abs(netOutput[1]);

        if (0 < sensors.getTrackPosition())
    		action.steering = -1 * steeringAmount;
        else
    		action.steering = steeringAmount;

        // and wanted speed
        double wantedSpeed = 200 * netOutput[0];

        action.accelerate = getAccel((int)wantedSpeed, sensors.getSpeed());
        action.brake = getBrake((int)wantedSpeed, sensors.getSpeed());

        if (sensors.getDamage() > 0)
            ticksCollision++;

        if (Math.abs(sensors.getTrackPosition()) > 1)
            ticksOutside++;
    }

	public String getDriverName() {
		return "Neural Driver";
	}
}
