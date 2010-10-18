package ecprac.era270;

import java.util.Random;
import java.util.List;

import ecprac.torcs.race.Race;
import ecprac.torcs.race.Race.Track;
import ecprac.torcs.race.RaceResult;
import ecprac.torcs.race.RaceResults;
import ecprac.torcs.race.Race.Termination;
import ecprac.torcs.client.Controller.Stage;

public class TorcsRace<G extends GenericGenome, D extends GenericGenomeDriver> { // extends Thread {
    public List<G> individuals;
    private D[] drivers;
    private int distance;
    private Random r = new Random();

    public TorcsRace(List<G> individuals, D[] drivers, int distance) {
        this.individuals = individuals;
        this.drivers = drivers;
        this.distance = distance;
        //start();
        run();
    }

    public void run() {
        // Random track
        //Track t = Track.fromIndex(r.nextInt(Track.values().length));
        Track t = Track.fromIndex(3);
        //System.out.println("Evaluating on track " + t);
        
        Race race = new Race();
        race.setTrack(t);
        race.setStage(Stage.RACE);
        //race.setStage(Stage.QUALIFYING);
        //race.setTermination(Termination.LAPS, 1);
        race.setTermination(Termination.DISTANCE, distance);

        // Add drivers
        for (int i=0; i<individuals.size(); i++) {
            drivers[i].init();
            drivers[i].loadGenome(individuals.get(i));
            race.addCompetitor(drivers[i]);
        }
        
        RaceResults results = race.run();
        //RaceResults results = race.runWithGUI();

        for (int i=0; i<individuals.size(); i++) {
            RaceResult result = results.get(drivers[i]);
            individuals.get(i).fitness = result.distance / result.time;
        }
    }
}
