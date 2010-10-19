package ecprac.era270;

import java.util.Random;

import ecprac.torcs.client.Action;
import ecprac.torcs.client.SensorModel;
import ecprac.torcs.controller.GenomeDriver;
import ecprac.torcs.genome.IGenome;

public class SpamEggsGenomeDriver extends GenericGenomeDriver {

    private int[] speed;
    private Random r = new Random();

    public void loadGenome(IGenome genome) {
        if (genome instanceof SpamEggsGenome) {
            SpamEggsGenome llgenome = (SpamEggsGenome) genome;
            speed = llgenome.speed;
        } else {
            System.err.println("Invalid Genome assigned");
        }
    }

    public void control(Action action, SensorModel sensors) {
        super.control(action, sensors);

        /* naive overtaking strategy */
        double opponents[] = sensors.getOpponentSensors();
        if (opponents[17] < 20 || opponents[18] < 20 || opponents[19] < 20) {
            double rand = (double)r.nextInt(6) / 10.0;

            if (sensors.getTrackPosition() < 0)
                action.steering += rand;
            else
                action.steering -= rand;
        }

        int curDirection = headingTowards(sensors);

        // 9 - (curDirection % 9)
        int speedIdx = curDirection;
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
