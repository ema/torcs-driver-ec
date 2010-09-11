package ecprac.era270;

import ecprac.torcs.genome.IGenome;

public class SpamEggsGenome implements IGenome {
	
	public int speed = 15;
	public double steering = 0.1;
	public double trackpos = 0.01;

    public String toString() {
        return "speed: " + speed + " steering:  " + steering + " trackpos: " + trackpos;
    }
}
