package LearningAlgorithms;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Math.abs;
import static java.lang.Math.pow;


public class Main {
    private final static int NON_CONVERGENCE_LIMIT = 1000000; //
    private final static int TRIAL_COUNTS = 500; //

    //Training Datasets

    //Neural net layer dimensions
    private final static int ACTIVATION_MODE = 2; // 1 = sigmoid; 2 = bipolar sigmoid


    public static void main(String[] args) throws IOException {
        int hiddenNeurons = 100;
        double learningRate = 0.05;
        double momentum = 0.005;

//        for (int n = 6; n <= 36; n += 2) {
//            for (double lr = 0.01; lr <= 0.41; lr += 0.01) {
//                 double m = 0;
                double[] mm = {0, 0.1, 0.3, 0.5};
                for(int i = 0; i < 4; i++){
                    double m = mm[i];
                    double lr = learningRate;
                    int n = hiddenNeurons;
//                    Date date = new Date();
                   System.out.println("+++++++ Start test with learning rate " + lr + " momentum " + m +" and " + n + " hidden neurons");
//                    SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
//                    String s = String.format("n%d-lr%.3f-p%.3f", n, lr, m);
//                    String fileName = "./RobocodeLogs/RMSError_" + "_" + s + "_" + time.format(date) + ".csv";
                    String s = String.format("neurons=%d-learningRate=%.3f-momentum%.3f.csv", n, lr, m);
                    TrainNN tNN = new TrainNN(s, n, lr, m, TRIAL_COUNTS);
                    tNN.trainIterator(0.001);
                }
//            }
//        }

//        String s = String.format("n%d-lr%.3f-p%.3f", hiddenNeurons, learningRate, momentum);
//        Date date = new Date();
//        System.out.println("+++++++ Start test with learning rate " + learningRate+ " and " + hiddenNeurons + " hidden neurons");
//        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
//        TrainNN tNN = new TrainNN("./RobocodeLogs/NN_RMS_errors_" + time.format(date) + "_" + s +".csv", hiddenNeurons, learningRate,momentum, TRIAL_COUNTS);
//        tNN.trainIterator(0.01);
    }
}

//public class Main {
//     private final static int NON_CONVERGENCE_LIMIT = 1000000; //
//     private final static int TRIAL_COUNTS = 1000; //
//
////    Training Datasets
////    static final double[] outputSets = {0, 1, 1, 0};
////    static final double[][] inputSets = {{0,0},{0,1},{1,0},{1,1}};
////    static final double lowBound = 0;
//    static final double[] outputSets = {-1, 1, 1, -1};
//    static final double[][] inputSets = {{-1,-1},{-1,1},{1,-1},{1,1}};
//    static final int lowBound = -1;
//    //Neural net layer dimensions
//    private final static int    ACTIVATION_MODE = 2; // 1 = sigmoid; 2 = bipolar sigmoid
//
//
//    static final int[] layers = {4, 1};
//    public static void main(String[] args) throws IOException {
////
//        Date date = new Date();
//        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
//        String fileName = "./logs/BackPropagation_EpochLog_" +  time.format(date) + ".log";
//        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
//
//
//        //create and initialize NeuralNet
//        NeuralNet nn = new NeuralNet(ACTIVATION_MODE, 2, layers, 0.2, 0.9, lowBound, 1);
//        double momentum = 0.2;
//        int nonConvergences = 0;
//        for(int i = 0; i < TRIAL_COUNTS; i++) {
//            double error;
//            int trialCounter = 0;
//
//            nn.initializeWeights();
//            double dError = 0;
//
//            do {
//                error = 0;
//                trialCounter++;
//                for (int j = 0; j < outputSets.length; j++) {
//                    error += pow(nn.train(inputSets[j], outputSets[j]) - outputSets[j], 2) / 2;
//                    //System.out.println(error);
//                    //System.out.println(nn);
//                }
//
//                dError = error;
//                //writer.append(error + "\n");  //for saving error for each epoch
//                if (trialCounter > NON_CONVERGENCE_LIMIT) {
//                    System.out.println("Non Convergence Detected");
//                    nonConvergences++;
//                    break;
//                }
//
//            } while (error > 0.05);
//            writer.append(trialCounter + "\n"); //For saving number of trials to solve system
//        }
//        System.out.println("Number of Trials: " + TRIAL_COUNTS);
//        System.out.println("Number of Non-Convergences: " + nonConvergences);
//        writer.close();
//    }
//}
