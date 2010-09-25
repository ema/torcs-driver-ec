package ecprac.era270;

import java.util.Random;

import ecprac.torcs.genome.IGenome;

public class SpamEggsGenome implements IGenome, Comparable<SpamEggsGenome> {
	
	public int[] speed = new int[10];
    public double fitness;
    Random r = new Random();

    public SpamEggsGenome() {
        int val = 60 + r.nextInt(10);

        for (int i=0; i<10; i++) {
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

    public int compareTo(SpamEggsGenome o) {
        if (fitness < o.fitness)
            return -1;

        if (fitness > o.fitness)
            return 1;

        return 0;
    }
}
