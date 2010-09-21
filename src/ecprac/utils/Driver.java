package ecprac.utils;


import java.io.IOException;
import java.util.LinkedList;

import ecprac.awl900.SnorkGenome;
import ecprac.awl900.SnorkGenomeDriver;
import ecprac.torcs.client.Controller.Stage;
import ecprac.torcs.controller.GenomeDriver;
import ecprac.torcs.genome.Utilities;
import ecprac.torcs.race.Race;
import ecprac.torcs.race.RaceResult;
import ecprac.torcs.race.RaceResults;
import ecprac.torcs.race.Race.Termination;
import ecprac.torcs.race.Race.Track;

public class Driver {
	
	public static void main(String[] args) throws InterruptedException {
		Race race;
		LinkedList<SnorkGenomeDriver> goodDrivers = new LinkedList<SnorkGenomeDriver>();
			String dir = System.getProperty("user.home") + "/genomes/";
			System.out.println(dir);

		while (goodDrivers.size() < 5) {
			SnorkGenomeDriver driver = new SnorkGenomeDriver();
			driver.init();
			SnorkGenome sg = new SnorkGenome();
			driver.loadGenome(sg);
			System.out.println("Genome Driver: " + driver.getGenome());
			race = new Race();
			race.setTrack(Track.michigan);
			race.setStage(Stage.RACE);
			race.setTermination(Termination.LAPS, 2);
			race.addCompetitor(driver);
			RaceResults rrs = race.runRace(true);

			RaceResult rr = rrs.get(driver);
			System.out.println(rr.finished + " " + rr.time + " " + rr.distance +
					" " + rr.bestLapTime + " " + rr.laps);
			if (rr.finished == true) {
				goodDrivers.add(driver);
				System.out.println("Good one..." + goodDrivers.size());
			}
		}
	
		int i = 0;
		for (SnorkGenomeDriver driver : goodDrivers) {
			System.out.println("Genome Driver: " + driver.getGenome());
			try {
				Utilities.saveGenome(driver.getGenome(), dir + "genome_" + i++ + ".genome");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
}
