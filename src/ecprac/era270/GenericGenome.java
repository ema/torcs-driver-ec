package ecprac.era270;

import java.util.Random;

import ecprac.torcs.genome.IGenome;

abstract class GenericGenome implements IGenome, Comparable<GenericGenome> {
	
    public double fitness;
    Random r = new Random();

    abstract public String toString();
    abstract public boolean equals(Object o);

    public int compareTo(GenericGenome o) {
        if (fitness < o.fitness)
            return -1;

        if (fitness > o.fitness)
            return 1;

        return 0;
    }
}
