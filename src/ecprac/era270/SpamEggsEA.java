package ecprac.era270;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import ecprac.torcs.client.Controller.Stage;
import ecprac.torcs.genome.Utilities;
import ecprac.torcs.race.Race;
import ecprac.torcs.race.RaceResults;
import ecprac.torcs.race.Race.Termination;
import ecprac.torcs.race.Race.Track;

public class SpamEggsEA {

	Random r = new Random(12); //124

	SpamEggsGenome[] population = new SpamEggsGenome[2];
	double[] fitness = new double[2];
	int evals;
	
	public void run() {

		// initialize population
		initialize();
		evaluateAll();

		evals = 2;

		// the evolutionary loop
		while (evals < 1000) {
			if (fitness[0] > fitness[1]) {
				// select first for replication
				population[1] = mutate(population[0]);
				evaluateAll();
			} else {
				// select second for replication
				population[0] = mutate(population[1]);
				evaluateAll();
			}
			evals+=2;
		}

		// save best
		try {
			if (fitness[0] > fitness[1]) {
				Utilities.saveGenome(population[0], "best.genome");
			} else {
				Utilities.saveGenome(population[1], "best.genome");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void initialize() {
		for (int i = 0; i < 2; i++) {
			SpamEggsGenome genome = new SpamEggsGenome();
			genome.speed = r.nextInt(200);						// maxspeed = 200
			genome.steering = r.nextDouble();
			genome.trackpos = 0.8 * r.nextDouble();
			population[i] = genome;
		}
	}

	public SpamEggsGenome mutate(SpamEggsGenome genome) {
		SpamEggsGenome genome2 = new SpamEggsGenome();
		genome2.speed = genome.speed + (int) (r.nextGaussian() * 20);
		genome2.steering = genome.steering + (0.1 * r.nextGaussian());
		genome2.trackpos = genome.trackpos + (0.1 * r.nextGaussian());
		return genome2;

	}
	
	public void evaluateAll() {

		Race race = new Race();
		
		//  One of the two ovals
		race.setTrack( Track.fromIndex(  (evals / 2) % 2 ));
		race.setStage(Stage.RACE);
		race.setTermination(Termination.LAPS, 3);
		
		// Add driver 1
		SpamEggsGenomeDriver driver1 = new SpamEggsGenomeDriver();
		driver1.init();
		driver1.loadGenome(population[0]);
		race.addCompetitor(driver1);
		
		// Add driver 2
		SpamEggsGenomeDriver driver2 = new SpamEggsGenomeDriver();
		driver2.init();
		driver2.loadGenome(population[1]);
		race.addCompetitor(driver2);
		
		// Run in Text Mode
		RaceResults results = race.run();
		//RaceResults results = race.runWithGUI();
		
		// Fitness = BestLap, except if both did not do at least one lap
		if( Double.isInfinite(results.get(driver1).bestLapTime) &&  Double.isInfinite(results.get(driver2).bestLapTime)){
			fitness[0] = results.get(driver1).distance;
			fitness[1] = results.get(driver2).distance;	
		} else {
			fitness[0] = -1 * results.get(driver1).bestLapTime;
			fitness[1] = -1 * results.get(driver2).bestLapTime;		
		}
		
	}
	
	public void show(){
		try {
			
			Race race = new Race();
			race.setTrack(Track.michigan);
			race.setStage(Stage.QUALIFYING);
			race.setTermination(Termination.LAPS, 3);
			
			SpamEggsGenome best = (SpamEggsGenome) Utilities.loadGenome("best.genome");
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
