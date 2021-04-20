import algorithms.Lockstep;
import algorithms.NonVerboseLoggingStrategy;
import experiments.FacebookDataSet;

public class Main {


    private static final String DATASETS_PATH = "datasets/";
    public static void main(String[] args) {
        FacebookDataSet.run(new Lockstep(new NonVerboseLoggingStrategy()));
    }
}
