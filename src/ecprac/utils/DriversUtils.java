package ecprac.utils;

import ecprac.torcs.client.SensorModel;

public class DriversUtils {

	public static Double directionOfTrackAxis(SensorModel model) {

		if (Math.abs(model.getAngleToTrackAxis()) <= 0.1
				&& Math.abs(model.getTrackPosition()) <= 0.1) {
			return -1 * Math.toDegrees(model.getAngleToTrackAxis());
		}
		if (model.getAngleToTrackAxis() <= 0 && model.getTrackPosition() <= 0) {
			return -1 * (-45 + Math.toDegrees(model.getAngleToTrackAxis()));
		}
		if (model.getAngleToTrackAxis() >= 0 && model.getTrackPosition() >= 0) {
			return -1 * (45 - Math.toDegrees(model.getAngleToTrackAxis()));
		}
		if (model.getAngleToTrackAxis() <= 0 && model.getTrackPosition() >= 0) {
			return (Math.toDegrees(model.getAngleToTrackAxis()) - 45);
		}
		if (model.getAngleToTrackAxis() >= 0 && model.getTrackPosition() <= 0) {
			return -1 * (Math.toDegrees(model.getAngleToTrackAxis()) - 45);
		}
		return Double.NaN;

	}

	
	public static Double distanceToNextCorner(SensorModel model) {
		if (Math.abs(model.getTrackPosition()) < 0.90) {

			double[] sensors = model.getTrackEdgeSensors();
			float[] angles = model.getInitialAngles();
			
			int furthest = 0;
			for (int i = 1; i < sensors.length; i++) {
				if (sensors[i] > sensors[furthest]) {
					furthest = i;
				}
				if (sensors[i] == sensors[furthest]) {
					if (i <= 9) {
						furthest = i;
					}
				}
			}
			if (furthest == 9) {
				return sensors[furthest];
			}
			if (furthest < 9) {
				
				if(furthest <= 1){
					return sensors[0];
				}
				
				int current = 17;
				double startAngle = calculateAngle(angles, sensors, 17, 18);
				
				while (current >= furthest) {
					double angle = calculateAngle(angles, sensors,
							current - 1, current);
					
					if (angle - startAngle < -10) {
						return sensors[current];
					}

					current--;
				}
				
			}
			if (furthest > 9) {
				double startAngle = calculateAngle(angles, sensors, 0, 1);
				int current = 2;
				
				if(furthest >= 17){
					return sensors[18];
				}
				
				while (current <= furthest) {
					double angle = calculateAngle(angles, sensors,
							current - 1, current);
					if (angle - startAngle > 10) {
						return sensors[current];
					}
					current++;
				}

			}
		}
		return Double.NaN;
	}

	
	public static Double directionOfNextCorner(SensorModel model) {
		if (Math.abs(model.getTrackPosition()) < 0.90) {

			double[] sensors = model.getTrackEdgeSensors();
			float[] angles = model.getInitialAngles();

			int furthest = 0;
			for (int i = 1; i < sensors.length; i++) {
				if (sensors[i] > sensors[furthest]) {
					furthest = i;
				}
				if (sensors[i] == sensors[furthest]) {
					if (i <= 9) {
						furthest = i;
					}
				}
			}

			return angles[furthest]
					+ Math.toDegrees(model.getAngleToTrackAxis());
		}
		return Double.NaN;
	}

	public static Double sharpnessOfNextCorner(SensorModel model) {

		if (Math.abs(model.getTrackPosition()) < 0.90) {

			double[] sensors = model.getTrackEdgeSensors();
			float[] angles = model.getInitialAngles();

			int furthest = 0;
			for (int i = 1; i < sensors.length; i++) {
				if (sensors[i] > sensors[furthest]) {
					furthest = i;
				}
				if (sensors[i] == sensors[furthest]) {
					if (i <= 9) {
						furthest = i;
					}
				}
			}

			if (furthest < 9) {

				if (furthest <= 2) {

					return -90.0;

				} else {
					if (sensors[furthest] == 200) {
						return Double.NaN;

					} else {

						double initialAngle = calculateAngle(angles, sensors,
								17, 18);
						double maxAngle = initialAngle;

						int current = 17;

						while (current >= furthest) {
							double angle = calculateAngle(angles, sensors,
									current - 1, current);
							if (angle < maxAngle) {
								maxAngle = angle;
							}

							current--;
						}

						return Math.toDegrees(maxAngle - initialAngle);

					}
				}

			} else if (furthest > 9) {

				if (furthest >= 18) {

					return 90.0;

				} else {
					if (sensors[furthest] == 200) {
						return Double.NaN;
					} else {

						double initialAngle = calculateAngle(angles, sensors,
								0, 1);
						double maxAngle = initialAngle;

						int current = 2;
						while (current <= furthest) {
							double angle = calculateAngle(angles, sensors,
									current - 1, current);
							if (angle > maxAngle) {
								maxAngle = angle;
							}

							current++;
						}

						return Math.toDegrees(maxAngle - initialAngle);

					}
				}

			} else {
				if (sensors[furthest] < 200) {
					return 0.0;
				}
			}

		}

		return Double.NaN;
	}

	private static double calculateAngle(float[] angles, double[] sensors,
			int sensor1, int sensor2) {

		double x1 = Math.sin(Math.toRadians(angles[sensor1]))
				* sensors[sensor1];
		double y1 = Math.cos(Math.toRadians(angles[sensor1]))
				* sensors[sensor1];

		double x2 = Math.sin(Math.toRadians(angles[sensor2]))
				* sensors[sensor2];
		double y2 = Math.cos(Math.toRadians(angles[sensor2]))
				* sensors[sensor2];

		if (x1 == x2) {
			if (y1 < y2) {
				return 0;
			} else {
				return Math.PI;
			}
		}

		if (y1 == y2) {
			if (x1 < x2) {
				return 0.5 * Math.PI;
			} else {
				return -0.5 * Math.PI;
			}
		}

		if (y2 > y1) {
			if (x2 > x1) {
				return Math.atan((x2 - x1) / (y2 - y1));
			} else {
				return -1 * Math.atan((x1 - x2) / (y2 - y1));
			}
		} else {
			if (x2 > x1) {
				return 0.5 * Math.PI + Math.atan((y1 - y2) / (x2 - x1));
			} else {
				return -0.5 * Math.PI - Math.atan((y1 - y2) / (x1 - x2));
			}
		}

	}
	
	public static boolean isOffTrack(SensorModel model) {
		double trackPos = model.getTrackPosition();
		return trackPos > 1.0 || trackPos < - 1.0;
	}
	
	public static boolean isRightOfAxis(SensorModel model) {
		return model.getTrackPosition() > 0.0;
	}
	
	public static boolean isLeftOfAxis(SensorModel model) {
		return !isRightOfAxis(model);
	}
}
