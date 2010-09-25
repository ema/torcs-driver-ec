package ecprac.era270;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.LinkedList;
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

    LinkedList<SpamEggsGenome> population = new LinkedList<SpamEggsGenome>();

    double[] bestLaps = new double[Track.values().length];

    int evals = 2;
    
    public void run() {
        // initialize population
        initialize();

        // the evolutionary loop
        while (evals < 1000) {
            evaluateAll();

            // Replace the worst individual with a new one created from the two
            // best individuals in the population. Also mutate him after
            // crossover
            Collections.sort(population);
            
            SpamEggsGenome worst = population.removeFirst();
            System.out.println("Replacing individual with fitness " + worst.fitness);

            SpamEggsGenome parent1 = population.removeLast();
            SpamEggsGenome parent2 = population.removeLast();
            System.out.println("By crossing over " + parent1.fitness + " and " + parent2.fitness);

            population.add(mutate(crossover(parent1, parent2)));
            population.add(parent1);
            population.add(parent2);

            evals += 1;
            System.out.println("evals: " + evals);
        }

        // save best
        try {
            Collections.sort(population);
            Utilities.saveGenome(population.getLast(), "best.genome");
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initialize() {
        for (int i=0; i < Track.values().length; i++)
            bestLaps[i] = 0;

        for (int i=0; i < populationSize; i++) 
            population.add(mutate(new SpamEggsGenome()));
    }

    private SpamEggsGenome mutate(SpamEggsGenome genome) {
        SpamEggsGenome genome2 = new SpamEggsGenome();
        double ng = r.nextGaussian() * 5;

        for (int i=0; i<genome.speed.length; i++)
            if (r.nextGaussian() > 0)
                genome2.speed[i] = (int)(genome.speed[i] + ng);

        return genome2;
    }

    private SpamEggsGenome crossover(SpamEggsGenome parent1, SpamEggsGenome parent2) {
        int speedArrayLength = parent1.speed.length;
        int crossoverPoint = r.nextInt(speedArrayLength);
        SpamEggsGenome offspring = new SpamEggsGenome();

        for (int i=0; i<crossoverPoint; i++)
            offspring.speed[i] = parent1.speed[i];

        for (int i=crossoverPoint; i<speedArrayLength; i++)
            offspring.speed[i] = parent2.speed[i];

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
            drivers[i].loadGenome(population.get(i));
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

            population.get(i).fitness += bestLaps[trackIndex] - bestTime;
        }
    }
    
    public void evaluateAll() {
        // resetting fitness
        for (int i=0; i<populationSize; i++)
            population.get(i).fitness = 0;

        for (Track t: Track.values()) {
            System.out.println("Evaluating on track " + t);

            evaluateOnTrack(t);
            
            for (SpamEggsGenome g: population)
                System.out.println("fitness=" + g.fitness + " for individual " + g);
        }

        // calculate the average fitness for each individual
        for (SpamEggsGenome g: population) {
            g.fitness = g.fitness / Track.values().length;
            System.out.println("Average fitness= " +  g.fitness + " for individual " + g);
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
