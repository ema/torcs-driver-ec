package ecprac.era270;

import java.util.Arrays;
//import ecprac.torcs.genome.IGenome;

public class NeuralGenome extends GenericGenome {
    public double fitness;

    // 5 edge sensors + 1 track position sensor + 1 current speed + 1 improved frontal = 8
    // 1 output: accelerator 
    public FeedForward drivingNetwork = new FeedForward(new double[8], 1);

    public String toString() {  
        return "Fitness: " + fitness;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NeuralGenome)) return false;
            
        NeuralGenome other = (NeuralGenome)o;

        return Arrays.equals(drivingNetwork.getWeights(), other.drivingNetwork.getWeights());
    }
}
