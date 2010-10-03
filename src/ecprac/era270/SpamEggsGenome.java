package ecprac.era270;

import java.util.Arrays;
import java.util.Random;

import ecprac.torcs.genome.IGenome;

public class SpamEggsGenome extends GenericGenome {
	
	public int[] speed = new int[10];
    public double fitness;
    Random r = new Random();

    public SpamEggsGenome() {
        int val = 60 + r.nextInt(10);

        for (int i=0; i<speed.length; i++) {
            speed[i] = val;
            val += 5;
        }
    }

    public String toString() {
        String toprint = "";
        
        for (int i=0; i<10; i++)
            toprint += i + ": " + speed[i] + " ";

        return toprint;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpamEggsGenome)) return false;
            
        SpamEggsGenome other = (SpamEggsGenome)o;
        return Arrays.equals(speed, other.speed);
    }
}
