package ecprac.era270;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;

import ecprac.torcs.client.Controller.Stage;
import ecprac.torcs.genome.Utilities;
import ecprac.torcs.race.Race;
import ecprac.torcs.race.RaceResults;
import ecprac.torcs.race.Race.Termination;
import ecprac.torcs.race.Race.Track;

public class SpamEggsEA {

    public static final int populationSize = 5;

    Random r = new Random(); 

    SpamEggsGenome[] population = new SpamEggsGenome[populationSize];

    double[] bestLaps = new double[Track.values().length];

    int evals = 2;
    
    public void run() {
        // initialize population
        initialize();

        // the evolutionary loop
        while (evals < 1000) {
            evaluateAll();

            int worstIndex = findWorstIndex();
            SpamEggsGenome[] best = findBest(2);

            System.out.println("Replacing individual " + population[worstIndex]);
            System.out.println("By crossing over " + best[0] + " and " + best[1]);

            // Replace the worst individual with a new one created from the two
            // best individuals in the population. Also mutate him after
            // crossover
            population[worstIndex] = mutate(crossover(best));

            evals += 2;
            System.out.println("evals: " + evals);
        }

        // save best
        try {
            Utilities.saveGenome(findBest(1)[0], "best.genome");
        } 
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    private SpamEggsGenome[] findBest(int n) {
        SpamEggsGenome[] best = new SpamEggsGenome[n];

        List<SpamEggsGenome> popList = Arrays.asList(population);
        Collections.sort(popList);

        for (int i=0; i<n; i++)
            best[i] = popList.get(populationSize - i - 1);

        return best;
    }

    private int findWorstIndex() {
        int worstIndex = 0;

        for (int i=1; i<populationSize; i++)
            if (population[i].fitness < population[worstIndex].fitness)
                worstIndex = i;

        return worstIndex;
    }

    public void initialize() {
        for (int i=0; i < Track.values().length; i++)
            bestLaps[i] = 0;

        for (int i=0; i < populationSize; i++) {
            SpamEggsGenome genome = mutate(new SpamEggsGenome());
            population[i] = genome;
        }
    }

    private SpamEggsGenome mutate(SpamEggsGenome genome) {
        SpamEggsGenome genome2 = new SpamEggsGenome();
        double ng = r.nextGaussian() * 5;

        for (int i=0; i<genome.speed.length; i++)
            if (r.nextGaussian() > 0)
                genome2.speed[i] = (int)(genome.speed[i] + ng);

        return genome2;
    }

    private SpamEggsGenome crossover(SpamEggsGenome[] parents) {
        int speedArrayLength = parents[0].speed.length;
        int crossoverPoint = r.nextInt(speedArrayLength);
        SpamEggsGenome offspring = new SpamEggsGenome();

        for (int i=0; i<crossoverPoint; i++)
            offspring.speed[i] = parents[0].speed[i];

        for (int i=crossoverPoint; i<speedArrayLength; i++)
            offspring.speed[i] = parents[1].speed[i];

        return offspring;
    }

    private void evaluateOnTrack(Track t) {
        SpamEggsGenomeDriver[] drivers = new SpamEggsGenomeDriver[populationSize];
        double bestTime;
        int trackIndex = t.ordinal();

        // init race
        Race race = new Race();
        race.setTrack(t);
        race.setStage(Stage.RACE);
        race.setTermination(Termination.LAPS, 2);

        // Add drivers
        for (int i=0; i<populationSize; i++) {
            drivers[i] = new SpamEggsGenomeDriver();
            drivers[i].init();
            drivers[i].loadGenome(population[i]);
            race.addCompetitor(drivers[i]);
        }
        
        // XXX: Run in Text Mode
        RaceResults results = race.run();
        //RaceResults results = race.runWithGUI();

        // if we don't have best lap time for this track
        if (bestLaps[trackIndex] == 0) {
            bestLaps[trackIndex] = Double.POSITIVE_INFINITY;

            for (int i=0; i<populationSize; i++) {
                bestTime = results.get(drivers[i]).bestLapTime;
                if (bestTime < bestLaps[trackIndex])
                    bestLaps[trackIndex] = bestTime;
            }
        }

        // adding up fitnesses on this track, we're gonna average them at the end
        for (int i=0; i<populationSize; i++) {
            bestTime = results.get(drivers[i]).bestLapTime;

            population[i].fitness += bestLaps[trackIndex] - bestTime;
        }
    }
    
    public void evaluateAll() {
        // resetting fitness
        for (int i=0; i<populationSize; i++)
            population[i].fitness = 0;

        for (Track t: Track.values()) {
            System.out.println("Evaluating on track " + t);

            for (SpamEggsGenome g: population)
                System.out.println(g);

            evaluateOnTrack(t);

            for (int i=0; i<populationSize; i++)
                System.out.println("fitness for individual " + i + ": " + population[i].fitness);
        }

        // calculate the average fitness for each individual
        for (int i=0; i<populationSize; i++) {
            population[i].fitness = population[i].fitness / Track.values().length;
            System.out.println("Average fitness for individual " + i + ": " + population[i].fitness);
        }
    }
    
    public void show() {
        try {
            Race race = new Race();
            //race.setTrack(Track.michigan);
            race.setTrack(Track.fromIndex(3));
            race.setStage(Stage.QUALIFYING);
            race.setTermination(Termination.LAPS, 3);
            
            SpamEggsGenome best = (SpamEggsGenome) Utilities.loadGenome("best.genome");
            System.out.println("Best genome: " + best);

            SpamEggsGenomeDriver driver = new SpamEggsGenomeDriver();
            driver.init();
            driver.loadGenome(best);
            race.addCompetitor(driver);

            race.runWithGUI();
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        
        /*
         * 
         * Start without arguments to run the EA, start with -show to show the best found
         * 
         */
        if(args.length > 0 && args[0].equals("-show")){
            new SpamEggsEA().show();
        } else {
            new SpamEggsEA().run();
        }
    }
}
