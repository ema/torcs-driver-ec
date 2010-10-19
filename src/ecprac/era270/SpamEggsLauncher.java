package ecprac.era270;

public class SpamEggsLauncher {
    public static void main(String[] args) {
        // Change the constructor call to select a different EA
        //new SpamEggsGA().run();
        new NeuralGA().run();
    }
}
