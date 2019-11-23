package LearningAlgorithms;

import Interface.NeuralNetInterface;

import javax.swing.text.Position;
import java.io.File;
import java.io.IOException;
import static java.lang.Math.random;

public class Neuron {
    private static final double BIAS = 1.0;

    private double[] weights;           //input weights; length = #input
    private double[] prevWeights;       //input weights; length = #input
    private double[] deltaWeights;      //difference between weight updated (current - previous)
    private double output;              //Neuron output. NOTE: THIS IS ALSO USED TO STORE ERROR DURING BACK PROPAGATION

    public Neuron(
            int inputs,                     //number of inputs to neuron
            boolean isBias
    ){

            this.output = 0.0;
            this.weights = new double[inputs + 1]; //length = number of inputs + 1 for the bias
            this.prevWeights = new double[inputs + 1];
            this.deltaWeights = new double[inputs + 1];
    }


    /**
     * Initialize all neuron weights in layer
     * @param upper Upper bound of randomized wight initialization
     * @param lower Lower bound of randomized wight initialization
     */
    public void init(double upper, double lower){
        for(int i = 0; i < this.weights.length; i++) {
            this.weights[i] = (random())*(upper-lower) + lower;
            this.prevWeights[i] = this.weights[i];
            this.deltaWeights[i] = 0.0;
        }
    }


    /**
     * Reset neuron values
     */
    public void clear(){
        for(int i = 0; i < weights.length; i++)
                weights[i] = 0.0;
    }


    /**
     * Set weight of indexed weight
     * @param i indec of weight
     * @param v new weight value
     */
    public void setWeight(int i, double v){
        weights[i] = v;
    }

    /**
     * @return array of weights
     */
    public double[] getWeights(){
        return weights;
    }

    /**
     * Store weights as previous weights
     */
    void updatePrevWeights(){
        for(int i = 0; i < this.weights.length; i++){
            this.deltaWeights[i] = this.weights[i] - this.prevWeights[i];
            this.prevWeights[i] = this.weights[i];
        }
    }


    /**
     * @return array of previous weights
     */
    public double[] getPrevWeights(){
        return weights;
    }


    /**
     *  @return array of delta weights
     */
    public double[] getDeltaWeights(){
        return deltaWeights;
    }


    /**
     * @return length of weight list
     */
    public int getWeightCount() {return weights.length;}


    /**
     * FOR TESTING - Manually set weights
     * @param w array of weight values
     */
    public void setWeights(double[] w){
        if(w.length != weights.length && weights.length > 0) throw new IllegalArgumentException("Error: weight vector length does not " +
                "match neuron input count");
        for (int i = 0; i < weights.length; i++) this.weights[i]  = w[i];
    }

    /**
     *
     * @param inputs array of inputs values for processing
     * @return sum of the product of weights and inputs: THIS DOES NOT APPLY AN ACTIVATION FUNCTION
     */
    public double solveOutput(double[] inputs){

        //Ensure the length of input matches the neuron parameters
        if(inputs.length != (this.weights.length - 1)) throw new IllegalArgumentException("Error: input vector length does not " +
                "match neuron input count. Input Length: " + inputs.length + " Neuron input count: " + (this.weights.length-1));

        //Initialize weight output with bias weight
        this.output = weights[0];
        for (int i = 0; i < inputs.length; i++) {
            this.output += weights[i+1]*inputs[i];
        }
        return this.output;
    }

    /**
     * @return neuron output value
     */
    public double getOutput(){
        return this.output;
    }

    /**
     * Set neuron output
     * @param v new neuron output value
     */
    public void setOutput(double v){
        this.output = v;
    }


    @Override
    public String toString() {
        String s = "Weights(";
        for(double w: weights){
            s += w + " ";
        }
        s += ") " + " O/P: " + output;
        return s;
    }
}
