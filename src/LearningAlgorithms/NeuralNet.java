package LearningAlgorithms;

import Interface.NeuralNetInterface;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.exp;
import static java.lang.Math.pow;

//TODO: Make activation mode an input to the back propagation method.

public class NeuralNet implements NeuralNetInterface {

    /**
     * Initialization Constants
     */
    private final static double INIT_UPPER = 0.5;
    private final static double INIT_LOWER = -0.5;
    private final static int    ACTIVATION_MODE = 2; // 1 = sigmoid; 2 = bipolar sigmoid
    /**
     * Neural Net Input Parameters
     */
    private double inputs;              //number of inputs
    private double learningRate;        //learning rate
    private double momentum;            //gradient descent momentum
    private double upBound, lowBound;   //Sigmoid boundaries
    //Neural Net Layers
    private List<NetLayer> layers;

    /**
     *  NeuralNetXOR Class Contstructor
     * @param inputs number of inputs
     *  @param layerLengths vector containing number of neurons in each layer (hidden + output) [int]
     *  @param learningRate
     *  @param momentum
     *  @param upBound  upper boundary of the sigmoid at the output
     *  @param lowBound lower boundary of the sigmoid at the output
     **/
    public NeuralNet(int inputs,
                     int[] layerLengths,
                     double learningRate,
                     double momentum,
                     double lowBound,
                     double upBound){
        this.inputs = inputs;
        this.learningRate = learningRate;
        this.momentum = momentum;
        this.upBound = upBound;
        this.lowBound = lowBound;
        //Initialize list of layers including the output layer
        layers = new ArrayList<NetLayer>();

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
        switch (ACTIVATION_MODE){
            case 1:
                return sigmoid(y);
            case 2:
                return bipolarSigmoid(y);
            default:
                throw new IllegalArgumentException("Invalid setting for ACTIVATION_MODE");
        }
    }

    public double dActivation(double y){
        switch (ACTIVATION_MODE){
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
        return 2/(1+exp(-x)) - 1;
    }

    /**
     * Derivative of Bipolar Sigmoid
     * @param x The input
     * @return
     */
    public double dSigmoid(double x){ return x*(1-x); } //{return sigmoid(x)*(1-sigmoid(x));}


    @java.lang.Override
    public double sigmoid(double x) {
        return 1/(1+exp(-x));
    }

    /**
     * Derivative of Bipolar Sigmoid
     * @param x The input
     * @return
     */
    public double dBipolarSigmoid(double x){
        return 0.5*(1+x)*(1-x);
    }

    @java.lang.Override
    public double customSigmoid(double x) {return 0.0;}
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

        return outputs[0]; //This will be the output layer's final output
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
                    layers.get(i).setWeight(0, j, wPrev[j] + momentum*wDelta[j] + learningRate*d*x); //Assumes single output in output layer
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
                        double w = layers.get(i+i).getWeights(k)[j];
                        errorSum += layers.get(i+1).getOutput(k)*w; //Note: output of next neuron now stores error
                    }
                    double d = dActivation(y)*errorSum;
                    layers.get(i).setOutput(j, d);

                    //Iterate  and update all weights entering neuron, process bias first
                    layers.get(i).setWeight(j, 0, wPrev[0] + momentum*wDelta[0] + learningRate*d*bias);
                    for(int k = 1; k < layers.get(i).getWeightCount(); k++){
                        double x = layers.get(i-1).getOutput(k-1);  //Input from previous neuron output to each layer
                        layers.get(i).setWeight(j, k,wPrev[k] + momentum*wDelta[k] + learningRate*d*x); //Assumes single output in output layer
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
                        layers.get(i).setWeight(j, k,wPrev[k] + momentum*wDelta[k] + learningRate*d*X[k-1]);
                    }
                }
            }
        } //End iterate through layers
        for(NetLayer l: layers)
            l.updatePrevWeights();  //Update previous weights with current weights
        return output;
    }

    @Override
    public void save(File argFile) {

    }

    @Override
    public void load(String argFileName) throws IOException, IOException {

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
