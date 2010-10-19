package ecprac.era270;

import java.util.List;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Random;

public class NeuralGA extends GenericEA<NeuralGenome> {
    private static final int tournamentSize = 6;
    private static final Random r = new Random();

    NeuralGA() {
        super(100, // population size
              6, // mating pool size
              200, // number of evolutionary cycles
              "Neural network algorithm");
    }

    NeuralGenome createIndividual() {
        return new NeuralGenome();
    }

    List<NeuralGenome> evaluateFitness(List<NeuralGenome> individuals) 
    {
        NeuralGenomeDriver[] drivers = new NeuralGenomeDriver[individuals.size()];
        for (int i=0; i<individuals.size(); i++)
            drivers[i] = new NeuralGenomeDriver();

        return new TorcsRace<NeuralGenome,NeuralGenomeDriver>(individuals, drivers, meters).individuals;
    }

    private List<NeuralGenome> tournamentSelection() {
        // Pick #tournamentSize random individuals (candidates)
        Collections.shuffle(population);

        List<NeuralGenome> candidates = new LinkedList<NeuralGenome>();
        for (int i=0; i<tournamentSize; i++)
            candidates.add(population.get(i));

        List<NeuralGenome> parents = evaluateFitness(candidates);
        Collections.sort(parents);

        return parents;
    }

    List<NeuralGenome> selectParents() {
        /* 
         * Tournament selection of #matingPoolSize individuals
         */
        return tournamentSelection().subList(tournamentSize - matingPoolSize, tournamentSize);
    }

    private NeuralGenome[] crossover(NeuralGenome parent1, NeuralGenome parent2) 
    {
        /*
         * One-point crossover of two parents
         */

        double[] weights1 = parent1.drivingNetwork.getWeights();
        double[] weights2 = parent2.drivingNetwork.getWeights();

        int arrayLength = weights1.length;
        int crossoverPoint = 1 + r.nextInt(arrayLength - 1);

        // two new individuals
        NeuralGenome[] offsprings = new NeuralGenome[2];
        offsprings[0] = new NeuralGenome();
        offsprings[1] = new NeuralGenome();

        double[] child1 = new double[arrayLength];
        double[] child2 = new double[arrayLength];

        // copy the first part of parent 1 and 2 to child 1 and 2 respectively 
        for (int i=0; i<crossoverPoint; i++) {
            child1[i] = weights1[i];
            child2[i] = weights2[i];
        }
    
        // copy the second part of parent 2 and 1 to child 1 and 2 respectively 
        for (int i=crossoverPoint; i<arrayLength; i++) {
            child1[i] = weights2[i];
            child2[i] = weights1[i];
        }

        offsprings[0].drivingNetwork.changeWeights(child1);
        offsprings[1].drivingNetwork.changeWeights(child2);

        return offsprings;
    }

    List<NeuralGenome> recombine(List<NeuralGenome> parents) {
        List<NeuralGenome> offsprings = new LinkedList<NeuralGenome>();

        for (int i=0; i<parents.size() - 1; i+=2) 
            for (NeuralGenome offspring: crossover(parents.get(i), parents.get(i + 1)))
                offsprings.add(offspring);

        return offsprings;
    }

    NeuralGenome mutate(NeuralGenome individual) {
        /*
         * Creep Mutation
         */
        
        // Random "normally" distributed value with mean 0.0 and standard
        // deviation 1.0 
        double value = r.nextGaussian();

        // Mutation probability
        double p = 0.4;

        double[] weights = individual.drivingNetwork.getWeights();

        for (int i=0; i<weights.length; i++)
            // uniformly distributed value between 0.0 and 1.0
            if (r.nextFloat() > p)
                weights[i] += value;
            
        individual.drivingNetwork.changeWeights(weights);
        return individual;
    }

    void selectSurvivors() {
        List<NeuralGenome> candidates = tournamentSelection();

        for (int i=0; i<matingPoolSize; i++)
            population.remove(candidates.get(i));
    }
}
