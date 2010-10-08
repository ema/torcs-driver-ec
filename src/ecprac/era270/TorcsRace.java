package ecprac.era270;

import java.util.Random;
import java.util.List;

import ecprac.torcs.race.Race;
import ecprac.torcs.race.Race.Track;
import ecprac.torcs.race.RaceResult;
import ecprac.torcs.race.RaceResults;
import ecprac.torcs.race.Race.Termination;
import ecprac.torcs.client.Controller.Stage;

// TODO: use generics to change drivers
public class TorcsRace<G extends GenericGenome> { // extends Thread {
    public List<G> individuals;
    private Random r = new Random();

    public TorcsRace(List<G> individuals) {
        this.individuals = individuals;
        //start();
        run();
    }

    public void run() {
        NeuralGenomeDriver[] drivers = new NeuralGenomeDriver[individuals.size()];

        // Random track
        //Track t = Track.fromIndex(r.nextInt(Track.values().length));
        Track t = Track.fromIndex(3);
        //System.out.println("Evaluating on track " + t);
        
        Race race = new Race();
        race.setTrack(t);
        race.setStage(Stage.RACE);
        //race.setStage(Stage.QUALIFYING);
        race.setTermination(Termination.LAPS, 1);
        //race.setTermination(Termination.DISTANCE, 10);

        // Add drivers
        for (int i=0; i<individuals.size(); i++) {
            drivers[i] = new NeuralGenomeDriver();
            drivers[i].init();
            drivers[i].loadGenome(individuals.get(i));
            race.addCompetitor(drivers[i]);
        }
        
        RaceResults results = race.run();

        for (int i=0; i<individuals.size(); i++) {
            RaceResult result = results.get(drivers[i]);
            individuals.get(i).fitness = result.distance / result.time;
        }
    }
}
