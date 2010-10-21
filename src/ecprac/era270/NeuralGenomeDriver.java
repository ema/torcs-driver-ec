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

    // very useful
    private static int USE_EA_METERS = 100;

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

        // normalized track pos XXX: is it useful?
        drivingNetworkInput[i] = 200 * sensors.getTrackPosition();
        drivingNetworkInput[i+1] = sensors.getSpeed();
        drivingNetworkInput[i+2] = improvedFrontalSensor(sensors);

        drivingNetwork.changeInputs(drivingNetworkInput);
    }

	public void control(Action action, SensorModel sensors) {
        super.control(action, sensors);

        if (improvedFrontalSensor(sensors) > USE_EA_METERS) 
            // We DO NOT use the ANN. See GenericGenomeDriver.control
            return;

        changeNetworkInputs(sensors);

        double netOutput[] = drivingNetwork.getOutput();

        //action.steering = netOutput[0];

        // let's see what our buddy thinks about accelerating / braking
        double accBrakeAction = netOutput[1];
        if (accBrakeAction > 0) {
            action.accelerate = accBrakeAction;
            action.brake = 0;
        }
        else {
            action.accelerate = 0;
            action.brake = accBrakeAction;
        }
    }

	public String getDriverName() {
		return "Una faccia mia ratsa";
	}
}
