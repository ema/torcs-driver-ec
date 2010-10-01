package ecprac.era270;

import java.util.List;
import java.util.LinkedList;
import java.util.Collections;

public class SpamEggsGA extends GenericEA {
    private static final int tournamentSize = 6;

    SpamEggsGA() {
        super(100, // population size
              4, // mating pool size
              20, // number of evolutionary cycles
              "Basic genetic algorithm");
    }

    SpamEggsGenome createIndividual() {
        return new SpamEggsGenome();
    }

    List<SpamEggsGenome> evaluateFitness(List<SpamEggsGenome> individuals) 
    {
        return new TorcsRace(individuals).individuals;
    }

    List<SpamEggsGenome> selectParents() {
        /* 
         * Tournament selection of #matingPoolSize individuals
         */

        // Pick #tournamentSize random individuals (candidates)
        Collections.shuffle(population);

        List<SpamEggsGenome> candidates = new LinkedList<SpamEggsGenome>();
        for (int i=0; i<tournamentSize; i++)
            candidates.add(population.get(i));

        List<SpamEggsGenome> parents = evaluateFitness(candidates);
        Collections.sort(parents);

        // return the best #matingPoolSize individuals
        return parents.subList(tournamentSize - matingPoolSize, tournamentSize - 1);
    }

    private SpamEggsGenome[] crossover(SpamEggsGenome parent1, SpamEggsGenome parent2) 
    {
        /*
         * One-point crossover of two parents
         */
        int arrayLength = parent1.speed.length;
        int crossoverPoint = 1 + new java.util.Random().nextInt(arrayLength - 1);

        // two new individuals
        SpamEggsGenome[] offsprings = new SpamEggsGenome[2];
        offsprings[0] = new SpamEggsGenome();
        offsprings[1] = new SpamEggsGenome();

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

    List<SpamEggsGenome> recombine(List<SpamEggsGenome> parents) {
        List<SpamEggsGenome> offsprings = new LinkedList<SpamEggsGenome>();

        for (int i=0; i<parents.size() - 1; i++) 
            for (SpamEggsGenome offspring: crossover(parents.get(i), parents.get(i + 1)))
                offsprings.add(offspring);

        return offsprings;
    }

    SpamEggsGenome mutate(SpamEggsGenome individual) {
        // XXX
        return individual;
    }

    void selectSurvivors() {
        // XXX
        LinkedList<SpamEggsGenome> individual = (LinkedList<SpamEggsGenome>)population;
        individual.removeLast();
        individual.removeLast();
    }
}
