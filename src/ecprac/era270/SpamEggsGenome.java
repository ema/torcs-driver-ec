package ecprac.era270;

import ecprac.torcs.genome.IGenome;

public class SpamEggsGenome implements IGenome {
	
	public int[] speed = new int[10];

    public SpamEggsGenome() {
        int val = 60;

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
}
