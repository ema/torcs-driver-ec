package ecprac.era270;

import java.util.List;
import java.util.LinkedList;
import java.util.Collections;

public class SpamEggsGA extends GenericEA {
    private static final int tournamentSize = 6;

    SpamEggsGA() {
        super(100, // population size
              4, // mating pool size
              10, // number of evolutionary cycles
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

    List<SpamEggsGenome> recombine(List<SpamEggsGenome> parents) {
        // XXX
        return parents;
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
