package LearningAlgorithms;

import org.junit.Assert;
import org.junit.Test;

public class TestNeuron {
    Neuron n = new Neuron(4, false);
    Neuron n2 = new Neuron(6, false);

    @Test
    public void testNeuronInitialzation(){
        n.init(-1, 4);
        for(double w: n.getWeights()) Assert.assertTrue((w >= -1) && (w <= 4));
    }

    @Test
    public void testNeuronClear(){
        n.clear();
        for(double w: n.getWeights()) Assert.assertEquals(0.0, w,0.1);
    }

    @Test
    public void testNeuronOutput(){
        double[] w = {1.0, 2.0, 2.5, 0.1, 5.1};
        double[] inputs = {2.0, 2.0, 10, 10};
        n.setWeights(w);
        double result = n.solveOutput(inputs);
        Assert.assertEquals(62.0, result, 0.1);
    }

    @Test
    public void testNeuronSetWeight(){
        n2.setWeight(2, 2.0);
        n2.setWeight(5, 3.3);

        Assert.assertEquals(2.0, n2.getWeights()[2], 0.1);
        Assert.assertEquals(3.3, n2.getWeights()[5], 0.1);
    }



}
