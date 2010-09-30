package ecprac.era270;

import java.util.Random;
import java.util.LinkedList;
import java.util.Collections;
import java.io.IOException;

import ecprac.torcs.genome.Utilities;

abstract class GenericEA {

    // population
    protected LinkedList<SpamEggsGenome> population = null;
    // random number generator
    protected Random r = new Random(); 
    // how far we are in the evolutionary process
    protected int curEvaluation;

    // constructor parameters
    protected int populationSize;
    protected int nRuns;
    protected int nRacers;
    protected String algorithmName;

    public GenericEA(int populationSize, int nRuns, int nRacers, 
                        String algorithmName) 
    {
        this.populationSize = populationSize;
        this.nRuns = nRuns;
        this.nRacers = nRacers;
        this.algorithmName = algorithmName;
    }

    // methods to be implemented by specific EAs 
    abstract SpamEggsGenome createIndividual();
    abstract void evaluateFitness();
    abstract LinkedList<SpamEggsGenome> selectParents();
    abstract LinkedList<SpamEggsGenome> recombine(LinkedList<SpamEggsGenome> parents);
    abstract SpamEggsGenome mutate(SpamEggsGenome individual);
    abstract void selectSurvivors();

    void initPopulation() {
        population = new LinkedList<SpamEggsGenome>();

        for (int i=0; i<populationSize; i++)
            population.add(createIndividual());
    }

    private LinkedList<SpamEggsGenome> getBestOrWorst(int n, boolean best) {
        LinkedList<SpamEggsGenome> individuals = new LinkedList<SpamEggsGenome>();

        // sort individuals by their fitness value
        Collections.sort(population);

        for (int i=0; i<n; i++) 
            individuals.add(population.get(best ? populationSize - i - 1 : i));
        
        return individuals;
    }

    LinkedList<SpamEggsGenome> getBest(int n) {
        return getBestOrWorst(n, true);
    }

    LinkedList<SpamEggsGenome> getWorst(int n) {
        return getBestOrWorst(n, false);
    }

    void evolve() {
        System.out.println("Evolutionary run " + curEvaluation);
        evaluateFitness();

        // parents selection
        LinkedList<SpamEggsGenome> parents = selectParents();

        // recombination
        LinkedList<SpamEggsGenome> offsprings = recombine(parents);

        // mutation
        for (SpamEggsGenome offspring: offsprings)
            population.add(mutate(offspring));

        // survivor selection
        selectSurvivors();
    }

    void run() {
        initPopulation();

        for (curEvaluation=0; curEvaluation < nRuns; curEvaluation += nRacers)
            evolve();

        try {
            SpamEggsGenome best = getBest(1).getFirst();
            Utilities.saveGenome(best, "best-" + algorithmName + ".genome");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
