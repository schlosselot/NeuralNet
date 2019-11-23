package LearningAlgorithms;

import Enum.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class ReinforementLearning {

    //Flag for debug output
    private boolean debugFlag = false;

    //Object Parameters
    static LUT rlLUT;
    double learningRate, discountRate, exploration;
    //private double[] previousStateActionVector;                    //holds size of each state entry AND the action entry
    //private double[] currentStateActionVector;                        //holds size of each state entry AND the action entry
    private double reward;
    private int bootstrapLength;   //Temp
    private ArrayList<double[]> stateActionVectors;
    private int stateActionIndex = 0;



    public ReinforementLearning(int[] s, int a, double learningRate, double discountRate, double exploration, int bsLength) {

        this.learningRate = learningRate;
        this.discountRate = discountRate;
        this.exploration = exploration;
        this.bootstrapLength = bsLength;

        rlLUT = new LUT(s, a);
        this.stateActionVectors = new ArrayList<double[]>();

        reward = 0;
    }

    /**
     * Clear state action vectors and index
     */
    public void stateActionInit(){
        this.stateActionIndex = 0;
        stateActionVectors.clear();
    }



    public Actions learn(double[] newStateVector, LearningPolicy policy, double reward) {
        if(newStateVector.length != rlLUT.getActionsIndexFromIndexVector())
            throw new IllegalArgumentException("Error @ RL:learn(): Incorrect number of inputs");

        double nextQ = 0.0;
        double prevQ = 0.0;

        //Add NEXT/Future action vector and manage the indexing based on bootstrap length
        stateActionVectors.add(getNextStateActionVector(newStateVector, policy));
        int nextAction = (int) stateActionVectors.get(stateActionIndex)[rlLUT.getActionsIndexFromIndexVector()];

        if(this.stateActionIndex >= 1) {
            switch (policy) {
                case noLearningRandom:
                case noLearningGreedy:
                    break;
                case SARSA:
                case Q:
                    if(reward > 0)
                        bootStrap(policy, reward);
                    break;
            }
        }

        //increment stateActionIndex.
        if(this.stateActionIndex >= bootstrapLength){
            stateActionVectors.remove(0);
        }
        else stateActionIndex++;

        //Return action within stateActionVector (last entry);
        return Actions.fromInt(nextAction);
    }

    /**
     * Recursively teach the RL network while discounting the reward each cycle.
     * @param policy
     * @param reward
     */
    public void bootStrap(LearningPolicy policy, double reward){

        double nextQ = 0.0;
        double prevQ = 0.0;

        double currentDiscount = discountRate;

        soutRLdebug("Boostrapping:");
        for(int i = this.stateActionIndex; i > 0; i--) {
            if (policy == LearningPolicy.SARSA) {
                nextQ = rlLUT.outputFor(stateActionVectors.get(i));
                prevQ = rlLUT.outputFor(stateActionVectors.get(i - 1));
            }
            if (policy == LearningPolicy.Q) {
                nextQ = rlLUT.outputForOffPolicy(stateActionVectors.get(i));
                prevQ = rlLUT.outputFor(stateActionVectors.get(i - 1));
            }
            if(learningRate > 0) {
                double newQvalue = prevQ + learningRate * (reward + currentDiscount * nextQ - prevQ);
                rlLUT.train(stateActionVectors.get(i - 1), newQvalue);

                soutRLdebug("\tIndex: " + i + ", Discount: " + currentDiscount + ", Value: " + newQvalue);
            }
            reward *= discountRate;
            currentDiscount *= discountRate;
        }
    }



    public void loadLUT(File file) throws IOException {
        this.rlLUT.load(file);
    }

    public void saveLUT(File LUTfile){
        this.rlLUT.save(LUTfile);
    }


    /**
     * Update reward value
     * @param r new reward value
     */
    public void updateReward(double r){
        this.reward = r;
    }


    /**
     * Set current state and pick action
     * @param X state vector (no action)
     */
    public double[] getNextStateActionVector(double[] X, LearningPolicy policy) {
        if(X.length != rlLUT.getActionsIndexFromIndexVector())
            throw new IllegalArgumentException("Error @ RL:getNextStateActionVector(): Incorrect number of inputs");

        double nextState;

        switch(policy){
            case noLearningRandom:
                nextState = rlLUT.getRandomAction();
                break;
            case noLearningGreedy:

            case SARSA:
            case Q:
                Random random = new Random();
                if(random.nextDouble() > this.exploration) nextState = rlLUT.getGreedyActions(X);
                else {
                    nextState = rlLUT.getRandomAction();
                    soutRLdebug("+++ EXPLORING...");
                }
                break;
            default:
                nextState = 0.0;
        }

        double[] sav = {X[0], X[1], X[2], X[3], X[4], X[5], X[6], nextState};
        return sav;
    }




    /**
     * Increment the LUT traning counter
     */
    public void incTrainingCounter(){
        rlLUT.incTrainingCounter();
    }

    /**
     * get the LUt's training counter
     * @return training counter
     */
    public long getTrainingCounter() {
        return rlLUT.getTrainingCounter();
    }



    private void soutRLdebug(String s){
        if(this.debugFlag == true) System.out.println(s);
    }

    public void setExplorationRate(double r){
        this.exploration = r;
    }

    public double getExplorationRate() {
        return this.exploration;
    }

    public void initializeLUT(){
        rlLUT.initialiseLUT();
    }

}
