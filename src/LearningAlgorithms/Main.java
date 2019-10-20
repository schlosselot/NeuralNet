package LearningAlgorithms;

import static java.lang.Math.pow;

public class Main {
    //Training Datasets
    static final double[] outputSets = {0, 1, 1, 0};
    static final double[][] inputSets = {{0,0},{0,1},{1,0},{1,1}};
    static final double lowBound = 0;
//    static final double[] outputSets = {-1, 1, 1, -1};
//    static final double[][] inputSets = {{-1,-1},{-1,1},{1,-1},{1,1}};
//    static final int lowBound = -1;
    //Neural net layer dimensions
    private final static int    ACTIVATION_MODE = 1; // 1 = sigmoid; 2 = bipolar sigmoid


    static final int[] layers = {4, 1};
    public static void main(String[] args) {
//        int[] layers = {4,1};
//        NeuralNet__ nn = new NeuralNet__(2, layers,0.2, 0.00);
//
//        nn.initializeWeights();
//        nn.zeroWeights();


        //create and initialize NeuralNet
        NeuralNet nn = new NeuralNet(ACTIVATION_MODE, 2, layers, 0.2, 0.5, lowBound, 1);
        nn.initializeWeights();

        double error;
        int trialCounter = 0;
        do{
            error = 0;
            trialCounter++;
            for(int j = 0; j < outputSets.length; j++) {
               error += pow(nn.train(inputSets[j], outputSets[j]) - outputSets[j], 2)/2;
                System.out.println(error);
                //System.out.println(nn);
            }
            System.out.println("Trial #: " + trialCounter + "  Error: " + error);

        } while (error > 0.05);
    }
}
