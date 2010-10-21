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
    public static boolean useGUI = false;

    public List<G> individuals;
    private D[] drivers;
    private int meters;
    private Random r = new Random();

    public TorcsRace(List<G> individuals, D[] drivers, int meters) {
        this.individuals = individuals;
        this.drivers = drivers;
        this.meters = meters;
        //start();
        run();
    }

    public void run() {
        // TODO: allow to switch between fixed and random track selection
        Track t = Track.fromIndex(3);

        //System.out.println("Evaluating on track " + t);
        
        Race race = new Race();
        race.setTrack(t);
        race.setStage(Stage.RACE);
        //race.setStage(Stage.QUALIFYING);
        //race.setTermination(Termination.LAPS, 1);
        race.setTermination(Termination.DISTANCE, meters);

        // Add drivers
        for (int i=0; i<individuals.size(); i++) {
            drivers[i].init();
            drivers[i].loadGenome(individuals.get(i));
            race.addCompetitor(drivers[i]);
        }
        
        RaceResults results;
        if (useGUI)
            results = race.runWithGUI();
        else
            results = race.run();

        for (int i=0; i<individuals.size(); i++) {
            RaceResult result = results.get(drivers[i]);

            double averageSpeed = result.distance / result.time;
            double outsidePenalty = drivers[i].ticksOutside / 10000.0;
            double collisionPenalty = drivers[i].ticksCollision / 100000.0;

            double fitness = averageSpeed - outsidePenalty - collisionPenalty;
            //System.out.println(averageSpeed + " " + outsidePenalty + " " + collisionPenalty);
            individuals.get(i).fitness = fitness;
        }
    }
}
