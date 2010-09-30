package ecprac.era270;

import java.util.LinkedList;

public class SpamEggsGA extends GenericEA {

    SpamEggsGA() {
        super(10, 50, 2, "Basic genetic algorithm");
    }

    SpamEggsGenome createIndividual() {
        return new SpamEggsGenome();
    }

    void evaluateFitness() {
        LinkedList<TorcsRace> races = new LinkedList<TorcsRace>();

        for (int i=0; i<populationSize; i++) {
            LinkedList<SpamEggsGenome> individuals = new LinkedList<SpamEggsGenome>();
            individuals.add(population.get(i));

            if (i+1 < populationSize)
                individuals.add(population.get(i+1));

            races.add(new TorcsRace(individuals));
        }
        /* FIXME: doesn't run in parallel
        for (TorcsRace t: races)
            try {
                t.join();
            }
            catch (InterruptedException e) {
            }
        */
    }

    LinkedList<SpamEggsGenome> selectParents() {
        // XXX
        LinkedList<SpamEggsGenome> parents = new LinkedList<SpamEggsGenome>();
        parents.add(population.get(0));
        parents.add(population.get(1));
        return parents;
    }

    LinkedList<SpamEggsGenome> recombine(LinkedList<SpamEggsGenome> parents) {
        // XXX
        return parents;
    }

    SpamEggsGenome mutate(SpamEggsGenome individual) {
        // XXX
        return individual;
    }

    void selectSurvivors() {
        // XXX
        population.removeLast();
        population.removeLast();
    }
}
