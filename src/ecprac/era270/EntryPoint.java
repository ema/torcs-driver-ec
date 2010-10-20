package ecprac.era270;

import java.io.IOException;

import ecprac.torcs.client.Controller.Stage;
import ecprac.torcs.genome.Utilities;
import ecprac.torcs.race.Race;
import ecprac.torcs.race.RaceResults;
import ecprac.torcs.race.Race.Termination;
import ecprac.torcs.race.Race.Track;

public class EntryPoint {
    
    private static Race createRace() {
        Race r = new Race();
        r.setTrack(Track.alpine);
        r.setStage(Stage.RACE);
        r.setTermination(Termination.LAPS, 2);
        return r;
    }

    private static void showBestOne() {
        // XXX: generalize for drivers != NeuralGenomeDriver
        Race r = createRace();

        try {
            NeuralGenome best = (NeuralGenome) Utilities.loadGenome("best.genome");
            NeuralGenomeDriver d = new NeuralGenomeDriver();
            d.init();
            d.loadGenome(best);
            r.addCompetitor(d);
            r.runWithGUI();
        }
        catch (Exception E) {System.out.println(E);}
    }

    private static void showRandom(int n) {
        Race r = createRace();
        
        for (int i=0; i<n; i++) {
            NeuralGenome driver = new NeuralGA().createIndividual();
            NeuralGenomeDriver d = new NeuralGenomeDriver();
            d.init();
            d.loadGenome(driver);
            r.addCompetitor(d);
            r.runWithGUI();
        }
    }
    
    public static void main(String[] args) {
        // Change the constructor call to select a different EA
        //new SpamEggsGA().run();
        new NeuralGA().run();
        
        // Show best driver
        //showBestOne();

        // Show random individuals
        //showRandom(4);
    }
}
