package LUT;

import LearningAlgorithms.LUT;
import org.junit.Assert;
import org.junit.Test;
import robocode.AdvancedRobot;

import java.io.File;

public class LUTtest {

//    @Test
//    public void testLUTreadwrite1(){
//        int[] s = {20, 1, 5};
//        int a = 3;
//        LUT lut = new LUT(s, a);
//
//        //write to
//        for(int i = 0; i < s[0]; i++){
//            for(int j = 0; j < s[1]; j++){
//                for(int k = 0; k < s[2]; k++){
//                    for(int l = 0; l < a; l++){
//                        double[] idx = {i, j, k, l};
//                        lut.train(idx, (double) (i*100+j*10+k)/(100-l)/3);
//                    }
//                }
//            }
//        }
//
//        double[] states = {7, 0, 3};
//        Assert.assertEquals(lut.getGreedyActions(states), 2, 0.001);
//
//        for(int i = 0; i < a; i++){
//            for(int j = 0; j < s[2]; j++){
//                for(int k = 0; k < s[1]; k++){
//                    for(int l = 0; l < s[0]; l++){
//                        double[] idx = {l, k, j, i};
//                        Assert.assertEquals((double) (l*100+k*10+j)/(100-i)/3, lut.outputFor(idx), 0.001);
//                    }
//                }
//            }
//        }
//    }

    @Test
    public void offPolicyActionSelect(){

        int[] s = {4, 4, 4, 6, 9, 9, 9};
        int a = 6;
        LUT lut = new LUT(s, a);

        double[] stateAction1 = {1,2,3,4,5,6,7,0};
        double[] stateAction2 = {1,2,3,4,5,6,7,1};
        double[] stateAction3 = {1,2,3,4,5,6,7,2};
        double[] stateAction4 = {1,2,3,4,5,6,7,3};
        double[] stateAction5 = {1,2,3,4,5,6,7,4};
        double[] stateAction6 = {1,2,3,4,5,6,7,5};

        lut.train(stateAction1, 0.5);
        lut.train(stateAction2, 1);
        lut.train(stateAction3, 12);
        lut.train(stateAction4, 0.1);
        lut.train(stateAction5, 40);
        lut.train(stateAction6, 2);

        Assert.assertEquals(40, lut.outputForOffPolicy(stateAction1), 0.001);
        Assert.assertEquals(40, lut.outputForOffPolicy(stateAction2), 0.001);
        Assert.assertEquals(40, lut.outputForOffPolicy(stateAction3), 0.001);
        Assert.assertEquals(40, lut.outputForOffPolicy(stateAction4), 0.001);
        Assert.assertEquals(40, lut.outputForOffPolicy(stateAction5), 0.001);
        Assert.assertEquals(40, lut.outputForOffPolicy(stateAction6), 0.001);

        Assert.assertEquals(0.5, lut.outputFor(stateAction1), 0.001);
        Assert.assertEquals(1, lut.outputFor(stateAction2), 0.001);
        Assert.assertEquals(12, lut.outputFor(stateAction3), 0.001);
        Assert.assertEquals(0.1, lut.outputFor(stateAction4), 0.001);
        Assert.assertEquals(40, lut.outputFor(stateAction5), 0.001);
        Assert.assertEquals(2, lut.outputFor(stateAction6), 0.001);
    }

    @Test
    public void getGreedyIndexTest(){

        int[] s = {4, 4, 4, 6, 9, 9, 9};
        int a = 6;
        LUT lut = new LUT(s, a);

        double[] stateAction1 = {1,2,3,4,5,6,7,0};
        double[] stateAction2 = {1,2,3,4,5,6,7,1};
        double[] stateAction3 = {1,2,3,4,5,6,7,2};
        double[] stateAction4 = {1,2,3,4,5,6,7,3};
        double[] stateAction5 = {1,2,3,4,5,6,7,4};
        double[] stateAction6 = {1,2,3,4,5,6,7,5};

        lut.train(stateAction1, 0.5);
        lut.train(stateAction2, 1);
        lut.train(stateAction3, 12);
        lut.train(stateAction4, 0.1);
        lut.train(stateAction5, 40);
        lut.train(stateAction6, 2);
        double[] stateActionIdx = {1,2,3,4,5,6,7};
        Assert.assertEquals(4, lut.findMaxQIndex(stateActionIdx), 0.001);


    }
}
