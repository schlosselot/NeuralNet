package LearningAlgorithms;

import java.util.ArrayList;
import java.util.List;

public class NetLayer {

    ArrayList<Neuron> neurons;  //list of neurons in layer
    private boolean outLayer;   //is this layer an output layer?
    double[] outputs;           //array of neuron outputs  - THESE ARE SUMS, NOT SOLVED WITH ACTIVATION FUNCTION

    /**
     *
     * @param neuronCount   Number of neurons in this layer
     * @param inputs        Number of inputs to each neuron
     * @param outLayer      Is this an output layer?
     */
    public NetLayer(
            int neuronCount,    //number of neurons in the layer
            int inputs,         //number of inputs to each neuron
            boolean outLayer    //is this the output layer?
    ){
        this.outLayer = outLayer;
        this.neurons = new ArrayList<Neuron>();
        this.outputs = new double[neuronCount];

        //Add all neurons to layer list
        for(int i = 0; i < neuronCount; i++){
            this.neurons.add(new Neuron(inputs, false));
        }
    }


    /**
     * Initialize all neuron weights in layer
     * @param upper Upper bound of randomized wight initialization
     * @param lower Lower bound of randomized wight initialization
     */
    public void init(double upper, double lower){
        for(Neuron n: neurons) n.init(upper, lower);
    }


    /**
     * FOR TESTING - Manually set weights
     * @param w List of weight vectors
     */
    public void setWeights(List<double[]> w){

        if(w.size() != neurons.size())
            throw new IllegalArgumentException("Error: weight list must be same size as " +
                "the layer size.");
        for(int i = 0; i < neurons.size(); i++) {
            neurons.get(i).setWeights(w.get(i));
        }
        this.updatePrevWeights();
    }


    /**
     * Clear all neuron weights in layer
     */
    public void clear(){
        for(Neuron n: neurons) n.clear();
    }


    /**
     * Get layer size
     * @return layer size
     */
    public int getSize(){
        return neurons.size();
    }


    /**
     * Solves the outputs of each neuron in the layer
     * @param inputs - inputs to this layer - neuron will trip exception if invalid number of inputs
     */
    public double[] solveOutput(double[] inputs){
        //iterate through neurons in layer
        for (int i = 0; i < neurons.size(); i++) {
            outputs[i] = neurons.get(i).solveOutput(inputs);
        }
        return outputs;
    }


    /**
     * @return length of weight list
     */
    public int getWeightCount() {
        return neurons.get(0).getWeightCount();
    };


    /**
     * Get Neron Output. NOTE: Activation function has not been applied.
     * @param i index of neuron output
     * @return neuron value
     */
    public double getOutput(int i){
        return this.neurons.get(i).getOutput();
    }


    /**
     * Set single neuron's ouptut
     * @param i index of neuron
     * @param v new value for neuron
     */
    public void setOutput(int i, double v){
        this.neurons.get(i).setOutput(v);
    }


    /**
     * Set all neuron outputs
     * @param v array of input values
     */
    public void setOutputs(double[] v){
        for(int i = 0; i < this.neurons.size(); i++)
            this.setOutput(i, v[i]);
    }


    /**
     *
     * @param neuron_index index of neuron to set weight
     * @param weight_index index of weight within neuron
     * @param value value of new weight
     */
    public void setWeight(int neuron_index, int weight_index, double value){
        this.neurons.get(neuron_index).setWeight(weight_index, value);
    }


    /**
     * Get list of neuron weights
     * @param n
     * @return
     */
    public double[] getWeights(int n){
        return this.neurons.get(n).getWeights();
    }


    /**
     * Update all previous weights with current weights in layer
     */
    void updatePrevWeights(){
        for(Neuron n: this.neurons) n.updatePrevWeights();
    }


    /**
     * Get list of previous neuron weights
     * @param n
     * @return
     */
    public double[] getPrevWeights(int n){
        return this.neurons.get(n).getPrevWeights();
    }


    /**
     * Get list of previous delta weights
     * @param n
     * @return
     */
    public double[] getDeltaWeights(int n){
        return this.neurons.get(n).getDeltaWeights();
    }


    @Override
    public String toString() {
        String s = "";
        for(int i = 0; i < neurons.size(); i++){
            s += "\tNeuron: " + i + " " + neurons.get(i) + "\n";
        }
        return s;
    }
}
