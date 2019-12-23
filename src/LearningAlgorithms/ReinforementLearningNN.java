package LearningAlgorithms;

import Enum.Actions;
import Enum.LearningPolicy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class ReinforementLearningNN {



    //State Action Indext Constants
    private static int  state = 0;
    private static int  action = 1;

    //Flag for debug output
    private boolean debugFlag = false;

    //Object Parameters
    static NeuralNet rlNN;
    double learningRate, discountRate, exploration;
    int[] saCount;
    //private double[] previousStateActionVector;                    //holds size of each state entry AND the action entry
    //private double[] currentStateActionVector;                        //holds size of each state entry AND the action entry
    private double reward;
    private int bootstrapLength;   //Temp
    private ArrayList<double[]> stateActionSet;
    //private ArrayList<double[]> stateActionVectors; //TODO: Turn into proper circular buffer
    private SarsaRingBuffer ringBuffer;
    private double[] prevState;
    private double[] thisState;
    private double prevReward;
    private int stateActionIndex = 0;

    private boolean first;                         //Flag the first round to prevent training until enough data

    private double rewardScaler;



    /**
     *
     * @param saCount           {STATE_COUNT, ACTION_COUNT}
     * @param learningRateNN    Neural Net - Learning Rate
     * @param momentum          Neural Net - Momentum
     * @param layerCount        Neural Net - Layer Count
     * @param learningRateRL    Reinforcement Learning - Learning Rate
     * @param discountRate      Reinforcement Learning - Discount Rate
     * @param exploration       Reinforcement Learning - Exploration
     * @param bsLength          Reinforcement Learning - Bootstrap Recursion Length/Limit
     */
    public ReinforementLearningNN(int[] saCount,
                                  double learningRateNN,
                                  double momentum,
                                  int[] layerCount,

                                  double learningRateRL,
                                  double discountRate,
                                  double exploration,
                                  int bsLength) {


        this.saCount = saCount;
        this.learningRate = learningRateRL;
        this.discountRate = discountRate;
        this.exploration = exploration;
        if(bsLength <= 1) ringBuffer = new SarsaRingBuffer(1);
        else ringBuffer = new SarsaRingBuffer(bsLength);

        //Create Neural new using bipolar sigmoid activation (mode 2).
        rlNN = new NeuralNet(2, (saCount[state] + saCount[action]), layerCount, learningRateNN, momentum, -1, 1);
        initializeNN();

        //Create State-Action Pair (addressable by state/action)
        stateActionSet = new ArrayList<double[]>();
        stateActionSet.add(new double[saCount[state]]);
        stateActionSet.add(new double[saCount[action]]);

        //For the circle buffer
        double prevState[] = new double[saCount[state] + saCount[action]];
        double thisState[] = new double[saCount[state] + saCount[action]];

        rewardScaler = 50;
        reward = 0;

        first = true;                         //Flag the first round to prevent training until enough data
    }

//    /**
//     * Clear state action vectors and index
//     */
//    public void stateActionInit(){
//        this.stateActionIndex = 0;
//        stateActionVectors.clear();
//    }

    /**
     * Creates a single vector from the stateActionSet list
     * @return single vector of length: #states + #actions
     */
    public double[] makeStateActionVector(){
        double[] sav = new double[saCount[state] + saCount[action]];
        for(int i = 0; i < sav.length; i++){
            //Add States
            if(i < saCount[state]) sav[i] = stateActionSet.get(state)[i];
            //Add Actions
            else sav[i] = stateActionSet.get(action)[i-saCount[state]];
        }
        return sav;
    }

    /**
     * Creates a single vector from the stateActionSet list's state and an input action
     * @param a - action vector
     * @return single vector of length: #states + #actions
     */
    public double[] makeStateActionVector(double[] a){
        double[] sav = new double[saCount[state] + a.length];
        for(int i = 0; i < sav.length; i++){
            //Add States
            if(i < saCount[state]) sav[i] = stateActionSet.get(state)[i];
                //Add Actions
            else sav[i] = a[i-saCount[state]];
        }
        return sav;
    }

    /**
     * Creates a single vector from the stateActionSet list's state and an input action
     * @param a - action vector
     * @param s - state vector
     * @return single vector of length: #states + #actions
     */
    public double[] makeStateActionVector(double[] s, double[] a){
        double[] sav = new double[s.length + a.length];
        for(int i = 0; i < sav.length; i++){
            //Add States
            if(i < s.length) sav[i] = s[i];
                //Add Actions
            else sav[i] = a[i-s.length];
        }
        return sav;
    }

    /**
     *
     * @param policy
     * @param reward reward value
     * @return RMS error of the learning set
     */
    public double learn(LearningPolicy policy, double reward) {

        double nextQ = 0.0;
        double prevQ = 0.0;
        double newQvalue = 0.0;
        double errorSquaredSum  = 0.0;

        //Configure State Action Set first with new state, then new action following policy
        //stateActionSet.set(state, newStateVector);
        //Actions nextAction = getNextAction(policy);  //stateActionSet's action has now been set

        //soutRLdebug(nextAction + " is the next action...");
        //Add to buffer, then update buffer

        //Update ring buffer with the previous state and current state with reward.
        thisState = makeStateActionVector();
        if(!first) {
            SARSA sarsa = new SARSA(prevState, thisState, reward);
            ringBuffer.push(sarsa);
        }
        else {
            first = false;
            //thisState = makeStateActionVector();
        }
        //prevReward = reward;
        prevState = thisState;


        //stateActionVectors.add(makeStateActionVector());
        //if(stateActionVectors.size() > bootstrapLength+1) stateActionVectors.remove(0);

        if(ringBuffer.size() >= 1) {
            switch (policy) {
                case noLearningRandom:
                case noLearningGreedy:
                    break;
                case SARSA:
                case Q:

                        double currentDiscount;
                        for(int i = 1; i <= ringBuffer.size(); i++) {
                            SARSA thisSARSA = ringBuffer.peek(i);

                            //Calculate current value function
                            double currentReward = 0;
                            currentDiscount = 1;
                            for(int j = 0; j < ringBuffer.size(); j++){
                               currentReward += currentDiscount*ringBuffer.peek(i + j + 1).getReward()/ rewardScaler;
                               currentDiscount*= discountRate;
                            }


                            if (policy == LearningPolicy.SARSA) {
                                nextQ = rlNN.outputFor(thisSARSA.getNext());
                                prevQ = rlNN.outputFor(thisSARSA.getPrev());
                                //nextQ = rlNN.outputFor(stateActionVectors.get(i));
                                //prevQ = rlNN.outputFor(stateActionVectors.get(i - 1));
                            }
                            else if (policy == LearningPolicy.Q) {
                                nextQ = outputForOffPolicy(thisSARSA.getNext());
                                prevQ = rlNN.outputFor(thisSARSA.getPrev());
//                              nextQ = outputForOffPolicy(stateActionVectors.get(i));
//                              prevQ = rlNN.outputFor(stateActionVectors.get(i - 1));
                            }

                            if(learningRate > 0) {
                                //if(reward < 0) reward /= 2; ----------------------------------------------------------------------------------------------------------------------------------------------------------------
                                newQvalue = prevQ + learningRate * (currentReward + discountRate * nextQ - prevQ);
                                //System.out.println("Iteration " + i + "; reward " + currentReward + "; newQ " + newQvalue);
                                //error += pow(nn.train(inputSets[j], outputSets[j]) - outputSets[j], 2) / 2;
                                errorSquaredSum += Math.pow(newQvalue - rlNN.train(thisSARSA.getPrev(), newQvalue),2);
                                //System.out.println("Learn: Qnext = " + nextQ + "  Qprev = " + prevQ + "  Qnew: " + newQvalue + "  Reward = " + reward/qScaler);
                                //soutRLdebug("\tIndex: " + i + ", Discount: " + currentDiscount + ", Value: " + newQvalue);
                            }
                        }
                    break;
            }
        }
        return Math.sqrt(errorSquaredSum/ringBuffer.size());
    }





    public void loadNN(File file) throws IOException {
        this.rlNN.load(file);
    }

    public void saveNN(File LUTfile){
        this.rlNN.save(LUTfile);
    }


    /**
     * Set the next action for a new state.
     * @param newStateVector
     * @param policy
     * @return next chosen action
     */

    public Actions setNextAction(double[] newStateVector, LearningPolicy policy){
        this.stateActionSet.set(state, newStateVector);
        return getNextAction(policy);  //stateActionSet's action has now been set
    }

    /**
     * Get the next action based on policy.
     * This sets the action in stateActionSet.
     */
    public Actions getNextAction(LearningPolicy policy) {
        Actions a = Actions.none;
        switch(policy){
            case noLearningRandom:
                a = getRandomAction();
                this.stateActionSet.set(action, getActionVector(a));
                break;
            case noLearningGreedy:
                a = getGreedyActions(stateActionSet.get(state));
                this.stateActionSet.set(action, getActionVector(a));
                break;
            case SARSA:
            case Q:
                Random random = new Random();
                if(random.nextDouble() > this.exploration) {
                    a = getGreedyActions(stateActionSet.get(state));
                    this.stateActionSet.set(action, getActionVector(a));
                }
                else {
                    a = (getRandomAction());
                    this.stateActionSet.set(action, getActionVector(a));
                    soutRLdebug("+++ EXPLORING...");
                }
                break;
            default:
                a = Actions.none;
                this.stateActionSet.set(action, getActionVector(a));
                break;
        }
        return a;
    }

    private double outputForOffPolicy(double[] stateActionVector) {
        double[] stateVector = new double[saCount[state]];
        for(int i = 0; i < saCount[state]; i++) stateVector[i] = stateActionVector[i];

        double[] actionVector = getActionVector(getGreedyActions(stateVector));

        return rlNN.outputFor(makeStateActionVector(actionVector, stateVector));
    }


    /*
        Action Handlers
        Each Action is vector of four elements
        0 - Up/Down: Up = 1, Down = -1
        1 - Left/Right Left = -1, Right = 1
        2 - Fire


        There are 5 Actions which can be taken independently:
        1 - Move Up
        2 - Move Down
        3 - Move Left
        4 - Move Right
        5 - SmartFire
     */


    private Actions getGreedyActions(double[] x) {
        double[] actionSetTemp = {-1, -1, -1, -1, -1};
        double[] Qvector = new double[6];

        double maxQ = -100000;

        actionSetTemp[0] = 1;
        Qvector[0] = rlNN.outputFor(makeStateActionVector(x, actionSetTemp)); //Up
        if(Qvector[0] > maxQ) maxQ = Qvector[0];

        actionSetTemp[0] = -1;
        actionSetTemp[1] = 1;
        Qvector[1] = rlNN.outputFor(makeStateActionVector(x, actionSetTemp)); //Down
        if(Qvector[1] > maxQ) maxQ = Qvector[1];

        actionSetTemp[1] = -1;
        actionSetTemp[2] = 1;
        Qvector[2] = rlNN.outputFor(makeStateActionVector(x, actionSetTemp)); //Left
        if(Qvector[2] > maxQ) maxQ = Qvector[2];

        actionSetTemp[2] = -1;
        actionSetTemp[3] = 1;
        Qvector[3] = rlNN.outputFor(makeStateActionVector(x, actionSetTemp)); //Right
        if(Qvector[3] > maxQ) maxQ = Qvector[3];

        actionSetTemp[3] = -1;
        actionSetTemp[4] = 1;
        Qvector[4] = rlNN.outputFor(makeStateActionVector(x, actionSetTemp)); //Fire
        if(Qvector[4] > maxQ) maxQ = Qvector[4];

        actionSetTemp[0] = 0;
        actionSetTemp[1] = 0;
        actionSetTemp[2] = 0;
        actionSetTemp[3] = 0;
        actionSetTemp[4] = 0;
        Qvector[5] = rlNN.outputFor(makeStateActionVector(x, actionSetTemp)); //None
        if(Qvector[5] > maxQ) maxQ = Qvector[5];


        ArrayList<Integer> maxList = new ArrayList<Integer>();
        for(int i = 0; i < 6; i++){
            //System.out.println("Action: " + Actions.fromInt(i) + " --> " + Qvector[i]);
            if(Qvector[i] == maxQ) maxList.add(i);
        }

        Actions a = Actions.none;
        //If multuple matching max Q values, randomly select one.
        if(maxList.size() > 1) {
            Random random = new Random();
            a = Actions.fromInt(maxList.get(random.nextInt(maxList.size())));
//            System.out.println("randomly choosing action: " + a);
        }
        else{
            a = Actions.fromInt(maxList.get(0));
//            System.out.println("deliberately choosing action: " + a + " Q = " + maxQ);
        }

        return a;
    }

    private double[] getActionVector(Actions a){
        double[] actionSet = {-1, -1, -1, -1, -1};
        switch (a){
            case moveUp:
                actionSet[0] = 1;
                break;
            case moveDown:
                actionSet[1] = 1;
                break;
            case moveLeft:
                actionSet[2] = 1;
                break;
            case moveRight:
                actionSet[3] = 1;
                break;
            case fire:
                actionSet[4] = 1;
                break;
            case none:
                actionSet[0] = 0;
                actionSet[1] = 0;
                actionSet[2] = 0;
                actionSet[3] = 0;
                actionSet[4] = 0;
                break;
        }
//        System.out.println("Test Action Vector: " + a + " : [" + actionSet[0] + "]["+ actionSet[1] + "]["+ actionSet[2] + "]["+ actionSet[3] + "]["+ actionSet[4] + "]");
        return actionSet;
    }

    /**
     * Return a vector with a random action.
     */
    private Actions getRandomAction() {
        double[] actionSet = {0, 0, 0};
        Random random = new Random();
        int a = random.nextInt(6);
        return Actions.fromInt(a);
    }


    /**
     * Increment the LUT traning counter
     */
    public void incTrainingCounter(){
        this.rlNN.incTrainingCounter();
    }

    /**
     * get the LUt's training counter
     * @return training counter
     */
    public long getTrainingCounter() {
        return this.rlNN.getTrainingCounter();
    }

    public void resetRingBuffer(){
        this.ringBuffer.reset();
    }


    private void soutRLdebug(String s){
        if(this.debugFlag == true) System.out.println(s);
    }

    public void setExplorationRate(double r){
        this.exploration = r;
    }

    public void setDiscountRate(double d) { this.discountRate = d; }

    public double getExplorationRate() {
        return this.exploration;
    }

    public void initializeNN(){
        rlNN.initializeWeights();
    } //TODO: Ensure this isnt call innapropriately

}


