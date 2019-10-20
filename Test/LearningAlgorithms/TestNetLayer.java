package LearningAlgorithms;


import org.junit.Assert;
import org.junit.Test;

public class TestNetLayer{

    NetLayer l = new NetLayer(4, 2, false);

    @Test
    public void testSetWeightsAndGetOutput(){
        l.setWeight(0, 0, 0.1);
        l.setWeight(0, 1, 0.2);
        l.setWeight(0, 2, 0.3);

        l.setWeight(1, 0, -0.45);
        l.setWeight(1, 1, 0.4);
        l.setWeight(1, 2, 0.2);

        l.setWeight(2, 0, 0.33);
        l.setWeight(2, 1, -0.2);
        l.setWeight(2, 2, 0.1);

        l.setWeight(3, 0, 0.5);
        l.setWeight(3, 1, 0.4);
        l.setWeight(3, 2, -0.5);

        double[] input = {2,3};
        double[] results = l.solveOutput((input));
        Assert.assertEquals(1.4, results[0],0.01);
        Assert.assertEquals(0.95, results[1], 0.01);
        Assert.assertEquals(0.23, results[2], 0.01);
        Assert.assertEquals(-0.2, results[3], 0.01);
    }

    @Test
    public void testSetOutputs(){

    }



}
