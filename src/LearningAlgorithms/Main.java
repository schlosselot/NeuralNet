package LearningAlgorithms;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Math.abs;
import static java.lang.Math.pow;

public class Main {
    private final static int    NON_CONVERGENCE_LIMIT = 1000000; //
    private final static int    TRIAL_COUNTS = 10000; //

    //Training Datasets
//    static final double[] outputSets = {0, 1, 1, 0};
//    static final double[][] inputSets = {{0,0},{0,1},{1,0},{1,1}};
//    static final double lowBound = 0;
    static final double[] outputSets = {-1, 1, 1, -1};
    static final double[][] inputSets = {{-1,-1},{-1,1},{1,-1},{1,1}};
    static final int lowBound = -1;
    //Neural net layer dimensions
    private final static int    ACTIVATION_MODE = 2; // 1 = sigmoid; 2 = bipolar sigmoid


    static final int[] layers = {4, 1};
    public static void main(String[] args) throws IOException {
//
        Date date = new Date();
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String fileName = "./logs/BackPropagation_EpochLog_" +  time.format(date) + ".log";
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));


        //create and initialize NeuralNet
        NeuralNet nn = new NeuralNet(ACTIVATION_MODE, 2, layers, 0.2, 0.0, lowBound, 1);
        double momentum = 0.2;
        int nonConvergences = 0;
        for(int i = 0; i < TRIAL_COUNTS; i++) {
            double error;
            int trialCounter = 0;

            nn.initializeWeights();
            double dError = 0;

            do {
                error = 0;
                trialCounter++;
                for (int j = 0; j < outputSets.length; j++) {
                    error += pow(nn.train(inputSets[j], outputSets[j]) - outputSets[j], 2) / 2;
                    //System.out.println(error);
                    //System.out.println(nn);
                }

                dError = error;
                //writer.append(error + "\n");  //for saving error for each epoch
                if (trialCounter > NON_CONVERGENCE_LIMIT) {
                    System.out.println("Non Convergence Detected");
                    nonConvergences++;
                    break;
                }

            } while (error > 0.05);
            writer.append(trialCounter + "\n"); //For saving number of trials to solve system
        }
        System.out.println("Number of Trials: " + TRIAL_COUNTS);
        System.out.println("Number of Non-Convergences: " + nonConvergences);
        writer.close();
    }
}
