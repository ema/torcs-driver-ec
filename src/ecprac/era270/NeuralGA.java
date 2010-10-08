package ecprac.era270;

import java.util.List;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Random;

public class NeuralGA extends GenericEA<NeuralGenome> {
    private static final int tournamentSize = 6;
    private static final Random r = new Random();

    SpamEggsGA() {
        super(100, // population size
              4, // mating pool size
              100, // number of evolutionary cycles
              "Neural network algorithm");
    }

    NeuralGenome createIndividual() {
        return new NeuralGenome();
    }

    List<NeuralGenome> evaluateFitness(List<NeuralGenome> individuals) 
    {
        return new TorcsRace<NeuralGenome>(individuals).individuals;
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
        return tournamentSelection().subList(tournamentSize - matingPoolSize, tournamentSize - 1);
    }

    private NeuralGenome[] crossover(NeuralGenome parent1, NeuralGenome parent2) 
    {
        /*
         * One-point crossover of two parents
         */
        int arrayLength = parent1.speed.length;
        int crossoverPoint = 1 + r.nextInt(arrayLength - 1);

        // two new individuals
        NeuralGenome[] offsprings = new NeuralGenome[2];
        offsprings[0] = new NeuralGenome();
        offsprings[1] = new NeuralGenome();

        // copy the first part of parent 1 and 2 to child 1 and 2 respectively 
        for (int i=0; i<crossoverPoint; i++) {
            offsprings[0].speed[i] = parent1.speed[i];
            offsprings[1].speed[i] = parent2.speed[i];
        }
    
        // copy the second part of parent 2 and 1 to child 1 and 2 respectively 
        for (int i=crossoverPoint; i<arrayLength; i++) {
            offsprings[0].speed[i] = parent2.speed[i];
            offsprings[1].speed[i] = parent1.speed[i];
        }

        return offsprings;
    }

    List<NeuralGenome> recombine(List<NeuralGenome> parents) {
        List<NeuralGenome> offsprings = new LinkedList<NeuralGenome>();

        for (int i=0; i<parents.size() - 1; i++) 
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

        for (int i=0; i<individual.speed.length; i++)
            // uniformly distributed value between 0.0 and 1.0
            if (r.nextFloat() > p)
                individual.speed[i] = (int)(individual.speed[i] + value);
            
        return individual;
    }

    void selectSurvivors() {
        List<NeuralGenome> candidates = tournamentSelection();

        for (int i=0; i<matingPoolSize; i++)
            population.remove(candidates.get(i));
    }
}
