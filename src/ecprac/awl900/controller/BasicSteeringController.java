package ecprac.awl900.controller;

import ecprac.awl900.SnorkGenome;
import ecprac.torcs.client.SensorModel;
import ecprac.utils.DriversUtils;

public class BasicSteeringController implements SteeringController {

	private SnorkGenome genome;

	BasicSteeringController(SnorkGenome snorkGenome) {
		genome = snorkGenome;
	}

	@Override
	public double calculateSteering(SensorModel sensorModel) {
		
		double ret = 0.0;
		System.out.println("angle=" + sensorModel.getAngleToTrackAxis());
		System.out.println("trackpos=" + sensorModel.getTrackPosition());
		if (DriversUtils.isOffTrack(sensorModel)) {
			System.out.println("OFF THE TRACK!");
			if (DriversUtils.isLeftOfAxis(sensorModel)) {
				ret = 0.8;
			} else {
				ret = -0.8;
			}
		} else {
			double[] edgeSensors = sensorModel.getTrackEdgeSensors();
			// Normalize to [0.0, 1.0]
			for (int i = 0; i < edgeSensors.length; i++) {
				edgeSensors[i] = edgeSensors[i] / 200.0;
			}
			double leftSums[] = calcLeftSums(edgeSensors);
			double rightSums[] = calcRightSums(edgeSensors);
			
			double sumLeft = leftSums[0] + leftSums[1] + leftSums[2];
			double sumRight = rightSums[0] + rightSums[1] + rightSums[2];
			double sumDiff = Math.abs(sumLeft - sumRight);
			System.out.println("sumRight=" + String.format("%.3f", sumRight));
			System.out.println("sumLeft=" + String.format("%.3f", sumLeft));
			System.out.println("sumDiff=" + String.format("%.3f", sumDiff));
			System.out.println("genome._steerAreaDiff=" + String.format("%.3f",
					genome._steerAreaDiff));
		
			if (sumDiff < genome._steerAreaDiff) {
				ret = 0.0;
			} else {
				if (sumLeft > sumRight) {
					ret = -calcSteering(leftSums, edgeSensors[9]);
				} else {
					ret = calcSteering(rightSums, edgeSensors[9]);
				}
			}
		}
		System.out.println("Returning steering: " + ret);
		return ret;
	}

	private double calcSteering(double[] sums, double frontDist) {
		double ret = 0.0;
		for (int i = 0; i < sums.length; i++) {
			System.out.println("sum_" + i + "=" + sums[i]);
			ret += sums[i] * genome._steerFactors[i];
		}
		System.out.println("front=" + frontDist);
		System.out.println("Steering Before correction: " + ret);
		ret -= frontDist* genome._steerFactors[genome._steerFactors.length - 1];
		System.out.println("Steering Before clamping: " + ret);
		return clamp(ret);
	}

	private double[] calcRightSums(double[] edgeSensors) {
		double[] ret = {0.0, 0.0, 0.0};
	
		for (int i = 10; i < 12; i++)
			ret[2] += edgeSensors[i];
		
		for (int i = 12; i < 15; i++)
			ret[1] += edgeSensors[i];
		
		for (int i = 15; i < 19; i++)
			ret[0] += edgeSensors[i];
		
		return ret;
	}

	private double[] calcLeftSums(double[] edgeSensors) {
		double[] ret = {0.0, 0.0, 0.0};
	
		for (int i = 0; i < 3; i++)
			ret[0] += edgeSensors[i];
		
		for (int i = 3; i < 7; i++)
			ret[1] += edgeSensors[i];
		
		for (int i = 7; i < 9; i++)
			ret[2] += edgeSensors[i];
		
		return ret;
	}

	private double clamp(double ret) {
		if (ret > 1.0) {
			return 1.0;
		} else if (ret < -1.0) {
			return -1.0;
		} else {
			return ret;
		}
	}
}
