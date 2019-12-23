package LearningAlgorithms;

import Interface.NeuralNetInterface;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.exp;
import static java.lang.Math.pow;


/**
 * OBJECT SETTINGS:
 *  ActivationMode: Selects input polarity and activation function to use.
 *      1 = Binary inputs and output with Sigmoid activation
 *      2 = Bipolar inputs and output with bipolar sigmoid activation
 */
//TODO: Make activation mode an input to the back propagation method.

public class NeuralNet implements NeuralNetInterface {

    /**
     * Initialization Constants
     */
    private final static double INIT_UPPER = 0.5;
    private final static double INIT_LOWER = -0.5;

    /**
     * Neural Net Input Parameters
     */
    private int activationMode;          //Determines which mode of activation function and inputs will be used
    private double inputs;              //number of inputs
    private double learningRate;        //learning rate
    private double momentum;            //gradient descent momentum
    private double upBound, lowBound;   //Sigmoid boundaries
    //Neural Net Layers
    private List<NetLayer> layers;

    private long trainingCounter;

    /**
     *  NeuralNetXOR Class Contstructor
     *  @param activationMode input, output, and activation function selection
     *  @param inputs number of inputs
     *  @param layerLengths vector containing number of neurons in each layer (hidden + output) [int]
     *  @param learningRate
     *  @param momentum
     *  @param upBound  upper boundary of the sigmoid at the output
     *  @param lowBound lower boundary of the sigmoid at the output
     **/
    public NeuralNet(int activationMode,
                     int inputs,
                     int[] layerLengths,
                     double learningRate,
                     double momentum,
                     double lowBound,
                     double upBound){
        this.activationMode = activationMode;
        this.inputs = inputs;
        this.learningRate = learningRate;
        this.momentum = momentum;
        this.upBound = upBound;
        this.lowBound = lowBound;
        //Initialize list of layers including the output layer
        layers = new ArrayList<NetLayer>();

        trainingCounter = 0;

        //NOTE: This module will only be tested using one hidden layer and one output for this commit
        //TODO: Update test cases before committing neural nets with more than one hidden layer, or
        // more than one output.

        //Create first hidden layer
        layers.add(new NetLayer(layerLengths[0], inputs, false));

        //Create other hidden layers; initialize the last layer as output.
        for(int i = 1; i < (layerLengths.length - 1); i++)
            layers.add(new NetLayer(layerLengths[i], layerLengths[i-1], false));

        //Create output layer
        layers.add(new NetLayer(layerLengths[layerLengths.length-1],
                layerLengths[layerLengths.length-2], true));
    }

    //-----------------------------------------Begin Activation Functions---------------------------------------------//
    public double activation(double y){
        switch (this.activationMode){
            case 1:
                return sigmoid(y);
            case 2:
                return bipolarSigmoid(y);
            default:
                throw new IllegalArgumentException("Invalid setting for ACTIVATION_MODE");
        }
    }

    public double dActivation(double y){
        switch (this.activationMode){
            case 1:
                return dSigmoid(y);
            case 2:
                return dBipolarSigmoid(y);
            default:
                throw new IllegalArgumentException("Invalid setting for ACTIVATION_MODE");
        }
    }


    /**
     *  Bipolar Sigmoid
     *  @param x The input
     *  @return f(x) = -1 + 2/(1 + e(-x))
     **/
    public double bipolarSigmoid(double x) {
        double r = 2/(1+exp(-x)) - 1;
        if(r > upBound) r = upBound;
        else if (r < lowBound) r = lowBound;
        return r;
    }

    /**
     * Derivative of Bipolar Sigmoid
     * @param x The input
     * @return
     */
    public double dSigmoid(double x){
        double r =  x*(1-x);
        if(r > upBound) r = upBound;
        else if (r < lowBound) r = lowBound;
        return r;
    }

    @java.lang.Override
    public double sigmoid(double x) {
        double r = 1/(1+exp(-x));
        if(r > upBound) r = upBound;
        else if (r < lowBound) r = lowBound;
        return r;
    }

    /**
     * Derivative of Bipolar Sigmoid
     * @param x The input
     * @return
     */
    public double dBipolarSigmoid(double x){
        double r = 0.5*(1+x)*(1-x);
        if(r > upBound) r = upBound;
        else if (r < lowBound) r = lowBound;
        return r;
    }

    @java.lang.Override
    public double customSigmoid(double x) {
        double r = 0.5*(1+x)*(1-x);
        if(r > upBound) r = upBound;
        else if (r < lowBound) r = lowBound;
        return r;
    }
    //-------------------------------------------End Activation Functions---------------------------------------------//


    @Override
    public void initializeWeights() {
        for(NetLayer l : layers) l.init(INIT_UPPER, INIT_LOWER);
    }

    @Override
    public void zeroWeights() {
        for(NetLayer l : layers) l.clear();
    }

    //This method assumes a single output neural net.
    //TODO: Revise to output array to handle multiple outputs
        @Override
    public double outputFor(double[] X) {

        //Solve input layer
        double[] outputs = layers.get(0).solveOutput(X);
        //Apply activation function
        for(int i = 0; i < outputs.length; i++) outputs[i] = activation(outputs[i]);
        layers.get(0).setOutputs(outputs);  //store layer's activated outputs in neuron objects

        //Solve middle and output layers. The input to each solver is previous layer's output.
        for(int l = 1; l < layers.size(); l++){
            outputs = layers.get(l).solveOutput(outputs);
            //Apply activation function
            for(int i = 0; i < outputs.length; i++) outputs[i] = activation(outputs[i]);
            layers.get(l).setOutputs(outputs);  //store layer's activated outputs in neuron objects
        }

        //Condition outputs to meet boundary requirements
        for(int i = 0; i < outputs.length; i++) {
            if (outputs[i] < lowBound) outputs[i] = lowBound;
            else if (outputs[i] > upBound) outputs[i] = upBound;
        }

        return outputs[0]; //This will be the output layer's output
    }


    /**
     *
     * @param l layer index
     * @param i neuron index
     */
    void getOutput(int l, int i){
        layers.get(l).getOutput(i);
    }


    /**
     * FOR TESTING - Manually set weights
     * @param w 2D array of weight values
     */
    public void setWeights(ArrayList<ArrayList<double[]>> w){
        for(int i = 0; i < w.size(); i++){
            layers.get(i).setWeights(w.get(i));
        }
    }


    /**
     * FOR TESTING - return entire layers list.
     * @return list of layers in neural net
     */
    public List<NetLayer> getLayers(){
        return layers;
    }

    //Note: Returns forward error.
    @Override
    public double train(double[] X, double argValue) {
        return backPropagate(X, argValue);
    }


    /**
     *
     * @param l layer index
     * @param n index of neuron in layer
     * @param i index of neuron input weight
     * @param v value of new weight
     */
    void setWeight(int l, int n, int i, double v){
        layers.get(l).setWeight(n, i, v);
    }


    /**
     * Back Propagate - start with output layer and iterate to first hidden layer
     * @param X - input set
     * @param argValue - expected output
     * @return output from initial forward propagation step
     */
    public double backPropagate(double[] X, double argValue){
        //Forward Propagate
        double output = outputFor(X);

        //Iterate through layers in reverse
        for(int i = layers.size()-1; i >= 0; i--){

            //Process output layer
            if (i == (layers.size() - 1)){
                //Update neuron output  (notes, slide 15)
                double wPrev[] = layers.get(i).getPrevWeights(0);  //get list of previous weights
                double wDelta[] = layers.get(i).getDeltaWeights(0);
                double y = layers.get(i).getOutput(0 );
                double d = dActivation(y)*(argValue-y);
                layers.get(i).setOutput(0, d); //Assumes single output layer

                //Iterate weights into neuron and update all weights entering output neuron
                layers.get(i).setWeight(0, 0, wPrev[0] + learningRate*d*bias); //Assumes single output in output layer
                for(int j = 1; j < layers.get(i).getWeightCount(); j++){
                    double x = layers.get(i-1).getOutput(j-1);  //Input from previous neuron output to each layer
                    double value = wPrev[j] + momentum*wDelta[j] + learningRate*d*x;
                    //if(value > upBound) value = upBound;
                    //else if(value < lowBound) value = lowBound;
                    layers.get(i).setWeight(0, j, value); //Assumes single output in output layer
                }

            }
            //TODO: Priority - Test and debug this else if section
            //Process neurons in hidden layers with hidden inputs
            else if (i > 0){
                //Iterate through neurons in this layer to calculate new output error (notes, slide 16)
                for (int j = 1; 0 < layers.get(i).getSize(); j++) {   //Skip the bias term: j starts at 1
                    //Update neuron output
                    double wPrev[] = layers.get(i).getPrevWeights(j);  //get list of previous weights
                    double wDelta[] = layers.get(i).getDeltaWeights(j);
                    double y = layers.get(i).getOutput(j);
                    double errorSum = 0;

                    //Iterate through next layers neurons to accumulate error
                    for(int k = 0; k < layers.get(i+1).getSize(); k++){
                        double w = layers.get(i+1).getWeights(k)[j];
                        errorSum += layers.get(i+1).getOutput(k)*w; //Note: output of next neuron now stores error
                    }
                    double d = dActivation(y)*errorSum;
                    layers.get(i).setOutput(j, d);

                    //Iterate  and update all weights entering neuron, process bias first
                    layers.get(i).setWeight(j, 0, wPrev[0] + momentum*wDelta[0] + learningRate*d*bias);
                    for(int k = 1; k < layers.get(i).getWeightCount(); k++){
                        double x = layers.get(i-1).getOutput(k-1);  //Input from previous neuron output to each layer
                        double value = wPrev[k] + momentum*wDelta[k] + learningRate*d*x;
                        //if(value > upBound) value = upBound;
                        // if(value < lowBound) value = lowBound;
                        layers.get(i).setWeight(j, k, value); //Assumes single output in output layer
                    }
                }
            }

            //TODO: remove redundant code using in else statement...
            //Process neurons in hidden layers with system inputs to calculate new output error
            else {
                //Iterate through neurons in this layer to calculate new output error (notes, slide 16)
                for (int j = 0; j < layers.get(i).getSize(); j++) {   //Skip the bias term: j starts at 1
                    //Update neuron output
                    double wPrev[] = layers.get(i).getPrevWeights(j);  //get list of previous weights
                    double wDelta[] = layers.get(i).getDeltaWeights(j);
                    double y = layers.get(i).getOutput(j);
                    double errorSum = 0;

                    //Iterate through next layers neurons to accumulate error.
                    for(int k = 0; k < layers.get(i+1).getSize(); k++){
                        double w = layers.get(i+1).getWeights(k)[j];
                        errorSum += layers.get(i+1).getOutput(k)*w;  //Note: output of next neuron now stores error
                    }
                    double d = dActivation(y)*errorSum;
                    layers.get(i).setOutput(j, d);

                    //Iterate  and update all weights entering neuron, process bias first
                    layers.get(i).setWeight(j, 0, wPrev[0] + momentum*wDelta[0] + learningRate*d*bias);
                    for(int k = 1; k < layers.get(i).getWeightCount(); k++){
                        double value = wPrev[k] + momentum*wDelta[k] + learningRate*d*X[k-1];
                        //if(value > upBound) value = upBound;
                        //else if(value < lowBound) value = lowBound;
                        layers.get(i).setWeight(j, k, value);
                    }
                }
            }
        } //End iterate through layers
        for(NetLayer l: layers)
            l.updatePrevWeights();  //Update previous weights with current weights
        output = outputFor(X);
        return output;
    }


    public void setMomentum(double m){
        this.momentum = m;
    }

    /**
     * Get individual neuron weight
     * @param l layer index in NN
     * @param n neuron index in layer
     * @param w weight index in neuron
     * @return  neuron weight w in layer l, neuron n
     */
    public double getWeight(int l, int n, int w){return this.layers.get(l).getWeight(n, w);}


    @Override
    public void save(File argFile) {
        try
        {
            System.out.println("+++Saving Neuron weights to file...\n");

            FileOutputStream outFile = new FileOutputStream(argFile);
            PrintStream out = new PrintStream(outFile);

            out.format("%f,", (double) trainingCounter);

            for(int i = 0; i < this.layers.size(); i++){                      //iterate layer
                for(int j = 0; j < layers.get(i).getSize(); j++){             //iterate neurons
                    double[] w = layers.get(i).getWeights(j);
                    for(int k = 0; k < w.length; k++){                          //iterate input weights
                        out.format("%f,", w[k]);
                    }
                }
            }
            out.close();
            outFile.close();
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }


        //TODO: develop this method to store weights
    }


    @Override
    public void load(File argFile) throws IOException, IOException {
        try
        {
            System.out.println("Loading neural net from file..   ");
            BufferedReader in = new BufferedReader(new FileReader(argFile));
            String line = in.readLine();
            String[] values = line.split(",");

            this.trainingCounter = (long) Double.parseDouble(values[0]);
            int a = 1;
            for(int i = 0; i < this.layers.size(); i++){                      //iterate layer
                for(int j = 0; j < layers.get(i).getSize(); j++){             //iterate neurons
                    for(int k = 0; k < layers.get(i).getWeightCount(); k++){                        //iterate input weights
                        layers.get(i).setWeight(j, k, Double.parseDouble(values[a]));
                        a++;
                    }
                }
            }
            in.close();
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }
    }


    /**
     * Set/Initialize the training counter
     * @return trainingCounter
     */
    void setTrainingCounter(long c){
        trainingCounter = c;
    }

    /**
     * Get the training counter
     * @return trainingCounter
     */
    long getTrainingCounter(){
        return trainingCounter;
    }

    /**
     * Increment the number of time this LUT has been trained
     */
    public void incTrainingCounter(){
        trainingCounter++;
    }



    @Override
    public String toString() {
        String s = "";
        for(int i = 0; i < layers.size(); i++){
            s += "Layer " + i + "\n" + layers.get(i);
        }
        return s;
    }
}
