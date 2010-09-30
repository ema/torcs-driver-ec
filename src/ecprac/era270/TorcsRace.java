package ecprac.era270;

import java.util.Random;
import java.util.LinkedList;

import ecprac.torcs.race.Race;
import ecprac.torcs.race.Race.Track;
import ecprac.torcs.race.RaceResult;
import ecprac.torcs.race.RaceResults;
import ecprac.torcs.race.Race.Termination;
import ecprac.torcs.client.Controller.Stage;

public class TorcsRace { // extends Thread {
    public LinkedList<SpamEggsGenome> individuals;
    private Random r = new Random();

    public TorcsRace(LinkedList<SpamEggsGenome> individuals) {
        this.individuals = individuals;
        //start();
        run();
    }

    public void run() {
        SpamEggsGenomeDriver[] drivers = new SpamEggsGenomeDriver[individuals.size()];

        // Random track
        Track t = Track.fromIndex(r.nextInt(Track.values().length));
        System.out.println("Evaluating on track " + t);
        
        Race race = new Race();
        race.setTrack(t);
        race.setStage(Stage.RACE);
        //race.setStage(Stage.QUALIFYING);
        //race.setTermination(Termination.LAPS, 3);
        race.setTermination(Termination.DISTANCE, 10);

        // Add drivers
        for (int i=0; i<individuals.size(); i++) {
            drivers[i] = new SpamEggsGenomeDriver();
            drivers[i].init();
            drivers[i].loadGenome(individuals.get(i));
            race.addCompetitor(drivers[i]);
        }
        
        RaceResults results = race.run();

        // TODO: think about better ways to decouple fitness calculation
        for (int i=0; i<individuals.size(); i++) {
            RaceResult result = results.get(drivers[i]);
            System.out.println("distance: " + result.distance);
            System.out.println("time: " + result.time);

            individuals.get(i).fitness = result.distance / result.time;
            
            System.out.println("fitness=" + individuals.get(i).fitness + " for individual " + individuals.get(i));
        }
    }
}
