package ecprac.era270;

import java.util.Random;
import java.util.List;
import java.util.LinkedList;
import java.util.Collections;
import java.io.IOException;

import ecprac.torcs.genome.Utilities;

abstract class GenericEA {

    // population
    protected List<SpamEggsGenome> population = null;
    // random number generator
    protected Random r = new Random(); 
    // how far we are in the evolutionary process
    protected int curEvaluation;

    // constructor parameters
    protected int populationSize;
    protected int matingPoolSize;
    protected int nRuns;
    protected String algorithmName;

    public GenericEA(int populationSize, int matingPoolSize, int nRuns, 
                        String algorithmName) 
    {
        this.populationSize = populationSize;
        this.matingPoolSize = matingPoolSize;
        this.nRuns = nRuns;
        this.algorithmName = algorithmName;
    }

    // methods to be implemented by specific EAs 
    abstract SpamEggsGenome createIndividual();
    abstract List<SpamEggsGenome> evaluateFitness(List<SpamEggsGenome> individuals);
    abstract List<SpamEggsGenome> selectParents();
    abstract List<SpamEggsGenome> recombine(List<SpamEggsGenome> parents);
    abstract SpamEggsGenome mutate(SpamEggsGenome individual);
    abstract void selectSurvivors();

    void initPopulation() {
        population = new LinkedList<SpamEggsGenome>();

        for (int i=0; i<populationSize; i++)
            population.add(createIndividual());
    }

    private List<SpamEggsGenome> getBestOrWorst(int n, boolean best) {
        List<SpamEggsGenome> individuals = new LinkedList<SpamEggsGenome>();

        // sort individuals by their fitness value
        Collections.sort(population);

        for (int i=0; i<n; i++) 
            individuals.add(population.get(best ? populationSize - i - 1 : i));
        
        return individuals;
    }

    List<SpamEggsGenome> getBest(int n) {
        return getBestOrWorst(n, true);
    }

    List<SpamEggsGenome> getWorst(int n) {
        return getBestOrWorst(n, false);
    }

    void evolve() {
        // parents selection
        List<SpamEggsGenome> parents = selectParents();

        // recombination
        List<SpamEggsGenome> offsprings = recombine(parents);

        // mutation
        for (SpamEggsGenome offspring: offsprings)
            population.add(mutate(offspring));

        // survivor selection
        selectSurvivors();

        System.out.print("Evolutionary run " + curEvaluation);
        System.out.println(" best fitness " + getBest(1).get(0).fitness);
    }

    void run() {
        initPopulation();

        for (curEvaluation=0; curEvaluation < nRuns; curEvaluation++)
            evolve();

        try {
            SpamEggsGenome best = getBest(1).get(0);
            Utilities.saveGenome(best, "best-" + algorithmName + ".genome");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
