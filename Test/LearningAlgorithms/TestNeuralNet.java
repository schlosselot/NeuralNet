package LearningAlgorithms;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

public class TestNeuralNet {


    @Test
    public void testWeightInit(){
        //Test Initialization of Random wight Variables to ensure they random (sd > 0.01)and between -0.5 and 0.5
        int[] ll = {4, 1};
        NeuralNet nn = new NeuralNet(1, 2, ll, 0.1, 0.1, 0, 1);
        nn.initializeWeights();

        double sum = 0.0;

        //Check -0.5 to 0.5 boundaries; obtain standard deviation assuming 0 average

        List<NetLayer> layers = nn.getLayers();
        for (NetLayer l: layers) {
            for(int i = 0; i < l.getSize(); i++) {
                for(double w: l.getWeights(i)) {
                    Assert.assertTrue(w <= abs(0.5));
                    sum += w * w;
                }
            }
        }

        double sd = sqrt(sum / 12);
        Assert.assertTrue(sd > 0.01);

        //Test zeroing of weights
        nn.zeroWeights();
        sum = 0.0;
        for (NetLayer l: layers) {
            for(int i = 0; i < l.getSize(); i++) {
                for(double w: l.getWeights(i)) {
                    Assert.assertTrue(w <= abs(0.5));
                    sum += w * w;
                }
            }
        }
        Assert.assertEquals(0.0, sum, 0.00);
    }


    @Test
    //1 Hidden layer, 2 input, 4, 1 output
    public void testSolver_1_Hidden(){
        double[] inputs = {2, 3};
        int[] ll = {4, 1};
        NeuralNet nn = new NeuralNet(1,2, ll, 0.1, 0.1, 0, 1);

        ArrayList<double[]> neurons1 = new ArrayList<double[]>();
        ArrayList<double[]> neurons2= new ArrayList<double[]>();

        ArrayList<ArrayList<double[]>> layers = new ArrayList<ArrayList<double[]>>();
        double[][] weights1 = { {0.1, 0.2, 0.3}, {-0.45, 0.4, 0.2}, {0.33, -0.2, 0.1}, {0.5, 0.4, -0.5}};
        double[] weights2 = {0.22, -0.45, -0.33, 0.4, 0.65};

        neurons1.add(weights1[0]);
        neurons1.add(weights1[1]);
        neurons1.add(weights1[2]);
        neurons1.add(weights1[3]);
        layers.add(neurons1);
        neurons2.add(weights2);
        layers.add(neurons2);

        nn.setWeights(layers);

        //Test Output
        Assert.assertEquals(0.53408612, nn.outputFor(inputs), 0.0000001);
        //Test Training Function
        Assert.assertEquals(0.5340861189553597, nn.outputFor(inputs), 0.0000001);
    }

    @Test
    //2 Hidden layer, 3 input, 2, 2, 1 output
    public void testSolver_2_Hidden(){
        double[] inputs = {2, 3, 4};
        int[] ll = {2, 2, 1};
        NeuralNet nn = new NeuralNet(1, 3, ll, 0.1, 0.1, 0, 1);

        ArrayList<double[]> neurons1 = new ArrayList<double[]>();
        ArrayList<double[]> neurons2= new ArrayList<double[]>();
        ArrayList<double[]> neurons3= new ArrayList<double[]>();
        ArrayList<ArrayList<double[]>> layers = new ArrayList<ArrayList<double[]>>();
        double[][] weights1 = { {0.1, 0.2, 0.3, 0.4}, {0.5, 1, 0.1, 0.2} };
        double[][] weights2 = { {0.6, 0.25, 0.10}, {0.7, 0.05, 0.10} };
        double[] weights3 = {0.8, 0.5, 0.25};
        neurons1.add(weights1[0]);
        neurons1.add(weights1[1]);
        layers.add(neurons1);
        neurons2.add(weights2[0]);
        neurons2.add(weights2[1]);
        layers.add(neurons2);
        neurons3.add(weights3);
        layers.add(neurons3);

        nn.setWeights(layers);
        //Test Output
        Assert.assertEquals(0.79149672, nn.outputFor(inputs), 0.0000001);
        //Test Training Function
        Assert.assertEquals(0.7914967151851122, nn.outputFor(inputs), 0.0000001);
    }


}
