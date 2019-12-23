/*
  _____      _        __                                        _     _                           _
 |  __ \    (_)      / _|                                      | |   | |                         (_)
 | |__) |___ _ _ __ | |_ ___  _ __ ___ ___ _ __ ___   ___ _ __ | |_  | |     ___  __ _ _ __ _ __  _ _ __   __ _
 |  _  // _ \ | '_ \|  _/ _ \| '__/ __/ _ \ '_ ` _ \ / _ \ '_ \| __| | |    / _ \/ _` | '__| '_ \| | '_ \ / _` |
 | | \ \  __/ | | | | || (_) | | | (_|  __/ | | | | |  __/ | | | |_  | |___|  __/ (_| | |  | | | | | | | | (_| |
 |_|  \_\___|_|_|_|_|_| \___/|_|__\___\___|_| |_| |_|\___|_| |_|\__| |______\___|\__,_|_|  |_| |_|_|_| |_|\__, |
 \ \        / (_) | | |     |  __ \     | |                       | |                                      __/ |
  \ \  /\  / / _| |_| |__   | |__) |___ | |__   ___   ___ ___   __| | ___                                 |___/
   \ \/  \/ / | | __| '_ \  |  _  // _ \| '_ \ / _ \ / __/ _ \ / _` |/ _ \
    \  /\  /  | | |_| | | | | | \ \ (_) | |_) | (_) | (_| (_) | (_| |  __/
  _  \/  \/   |_|\__|_| |_| |_|  \_\___/|_.__/ \___/ \___\___/_\__,_|\___| _
 | |               /\             | |                    / ____|    | |   | |
 | |__  _   _     /  \   _ __   __| |_ __ _____      __ | (___   ___| |__ | | ___  ___ ___  ___ _ __
 | '_ \| | | |   / /\ \ | '_ \ / _` | '__/ _ \ \ /\ / /  \___ \ / __| '_ \| |/ _ \/ __/ __|/ _ \ '__|
 | |_) | |_| |  / ____ \| | | | (_| | | |  __/\ V  V /   ____) | (__| | | | | (_) \__ \__ \  __/ |
 |_.__/ \__, | /_/    \_\_| |_|\__,_|_|  \___| \_/\_/ __|_____/_\___|_|_|_|_|\___/|___/___/\___|_|
 | \ | | __/ |                  | |               /_ |___ \   |__ \ / _ \/_ |/ _ \
 |  \| ||___/_   _____ _ __ ___ | |__   ___ _ __   | | __) |     ) | | | || | (_) |
 | . ` |/ _ \ \ / / _ \ '_ ` _ \| '_ \ / _ \ '__|  | ||__ <     / /| | | || |\__, |
 | |\  | (_) \ V /  __/ | | | | | |_) |  __/ |     | |___) |   / /_| |_| || |  / /
 |_| \_|\___/ \_/ \___|_| |_| |_|_.__/ \___|_|     |_|____( ) |____|\___/ |_| /_/
   __             _    _ ____   _____             _____ __|/_  ______ _   _ _____  ___ ___
  / _|           | |  | |  _ \ / ____|           / ____|  __ \|  ____| \ | | ____|/ _ \__ \
 | |_ ___  _ __  | |  | | |_) | |       ______  | |    | |__) | |__  |  \| | |__ | | | | ) |
 |  _/ _ \| '__| | |  | |  _ <| |      |______| | |    |  ___/|  __| | . ` |___ \| | | |/ /
 | || (_) | |    | |__| | |_) | |____           | |____| |    | |____| |\  |___) | |_| / /_
 |_| \___/|_|     \____/|____/ \_____|           \_____|_|    |______|_| \_|____/ \___/____|
 */

package LearningAlgorithms;

import Enum.Actions;
import Enum.LearningPolicy;
import robocode.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DasSchlosselot extends AdvancedRobot {

    //Flag for debug output
    private boolean debugFlag = false;
    private boolean errorLogEnable = false;
    private final static int EPOCHS_PER_SAMPLE = 100;

/* _____                _              _
  / ____|              | |            | |
 | |     ___  _ __  ___| |_ __ _ _ __ | |_ ___
 | |    / _ \| '_ \/ __| __/ _` | '_ \| __/ __|
 | |___| (_) | | | \__ \ || (_| | | | | |_\__ \
  \_____\___/|_| |_|___/\__\__,_|_| |_|\__|___/ */

    //RL SETTINGS
    private final static double LEARNING_RATE_RL = 0.9;  //0.9
    private final static double DISCOUNT_RATE = .5;    //0.9
    private final static double EXPLORATION = 0.9;
    private final static int BOOTSTRAP_LENGTH = 20;
    static private LearningPolicy policy = LearningPolicy.Q;      //default to Q learning

    //NN SETTINGS
    private final static double LEARNING_RATE_NN = 0.23;
    private final static double MOMENTUM = 0.00;
    private final static int HIDDEN_NEURONS = 18;
    private final static int[] LAYER_COUNT = {HIDDEN_NEURONS, 1};

    //GAME RL SETTINGS
    private final static boolean countIntermediateRewards = true;
    private final static double TERMINAL_REWARD = 50;
    private final static double INTERMEDIATE_REWARD = 15;
    private final static int ACTION_COUNT = 5;
    private final static int STATE_COUNT = 7;
    private final static int[] saCount = {STATE_COUNT, ACTION_COUNT};

    //ROBOCODE Settings
    private final static int HEADING_QUANTA = 90;        //Quanta step size for heading
    private final static int HEADING_QUANTA_OFFSET = 45; //Quanta step size for heading
    private final static int POSITION_QUANTA = 100;      //Quanta step size for position
    private final static int MOVEMENT_DISTANCE = 100;    //Distance moved each turn

    //Generic Constants
    private final static int X = 0; // x index for position vector
    private final static int Y = 1; // y index for position vector

    //File handling
    static private File NNfile;
    static private File statsfile;
    static private File actionsfile;
    static private File qLogFile;

    private final static Date date = new Date();
    private final static SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    private final static String s = String.format("_n%d-lr%.3f-p%.3f-lr%.3f-d%.3f-e%.3f-bs%d", HIDDEN_NEURONS, LEARNING_RATE_NN, MOMENTUM,
            LEARNING_RATE_RL, DISCOUNT_RATE, EXPLORATION, BOOTSTRAP_LENGTH);
    private final static String NN_FILENAME = time.format(date) +"_robocode_NN_file.csv";
    private final static String STATS_FILENAME = time.format(date) +"_robocode_stats_" + s + ".csv";
    private final static String ACTIONSTATS_FILENAME = time.format(date) +"_robocode_actionstats_" +  s + ".csv";
    private final static String Q_LOGFILE_NAME = time.format(date) +"_robocode_NN_train_errors"+  s +".csv";;

//    String qLogFileName = "qLogfile" + ".csv"
//    qLogFile = getDataFile(qLogFileName);

    private boolean saveFlag = false;




/*_____             _   _                 __      __        _       _     _
 |  __ \           | | (_)                \ \    / /       (_)     | |   | |
 | |__) |   _ _ __ | |_ _ _ __ ___   ___   \ \  / /_ _ _ __ _  __ _| |__ | | ___  ___
 |  _  / | | | '_ \| __| | '_ ` _ \ / _ \   \ \/ / _` | '__| |/ _` | '_ \| |/ _ \/ __|
 | | \ \ |_| | | | | |_| | | | | | |  __/    \  / (_| | |  | | (_| | |_) | |  __/\__ \
 |_|  \_\__,_|_| |_|\__|_|_| |_| |_|\___|     \/ \__,_|_|  |_|\__,_|_.__/|_|\___||___/  */

    // Completion Conditions
    private final TurnCompleteCondition turnComplete = new TurnCompleteCondition(this);
    private final MoveCompleteCondition moveComplete = new MoveCompleteCondition(this);
    private final GunTurnCompleteCondition turnGunComplete = new GunTurnCompleteCondition(this);

    // Variables to track the state of my robot
    private double[] myRobotPosition = new double[2]; //{x, y}
    private int myRobotHeading;
    private int myRobotHeadingQuantized;
    private int myRobotGunHeading;
    private int myRobotGunBearing;
    private double myRobotEnergy;
    private double myRobotPreviousEnergy = 100;

    // Variables to track the state of enemy robot
    private double[] enemyPosition = new double[2];
    private double[] enemyPositionRelative = new double[2]; //X: 0-same,1-left,2-right  //Y: 0-same,1-below,2-above

    private double enemyDistance;
    private int enemyHeading;
    private int enemyHeadingQuantized;
    private int enemyBearing;
    private int enemyBearingFromGun;
    private int enemyEnergy;
    private double enemyAngle;             //angle between myRobot heading and enemy
    private double enemyEnergyChange;       //0 none, 1 lose, 2 gain.
    private double prevEnemyEnergy;

    //Reinforcement Learning Variables
    static private ReinforementLearningNN rlTrainer = new ReinforementLearningNN(
            saCount, LEARNING_RATE_NN, MOMENTUM, LAYER_COUNT, LEARNING_RATE_RL, DISCOUNT_RATE ,EXPLORATION, BOOTSTRAP_LENGTH);

    static private Actions action = Actions.none;                 //current action to be taken
    //static private int action = 0;





    static private double currentReward = 0;
    static private double[] prevStateVector = new double[STATE_COUNT];
    static private double[] stateVector = new double[STATE_COUNT];

    static private int winCounter = 0;
    static private int roundCounter = 0;
    static private long[] actionCounter = {0,0,0,0,0,0};
    static private final long[] actionZero = {0,0,0,0,0,0};
    static boolean explorationToggle = true;
    static private double qRmsSum = 0.0;
    static private double rewardSum = 0.0;

    private boolean first = true;                         //Flag the first round to prevent training until enough data

    static double currentExplorationRate = EXPLORATION;
/*_____                   __   ____           _____                                _   _____       _           _
 |  __ \                 / /  / __ \         / ____|                              | | |  __ \     | |         | |
 | |__) |   _ _ __      / /  | |  | |_ __   | (___   ___ __ _ _ __  _ __   ___  __| | | |__) |___ | |__   ___ | |_
 |  _  / | | | '_ \    / /   | |  | | '_ \   \___ \ / __/ _` | '_ \| '_ \ / _ \/ _` | |  _  // _ \| '_ \ / _ \| __|
 | | \ \ |_| | | | |  / /    | |__| | | | |  ____) | (_| (_| | | | | | | |  __/ (_| | | | \ \ (_) | |_) | (_) | |_
 |_|  \_\__,_|_| |_| /_/      \____/|_| |_| |_____/ \___\__,_|_| |_|_| |_|\___|\__,_| |_|  \_\___/|_.__/ \___/ \__| */

    @Override
    public void run(){

        //myRobot configuration
        setColors(Color.RED, Color.YELLOW, Color.ORANGE, Color.PINK, Color.GREEN);
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);

        //Get lookup table and statistics files
        NNfile = getDataFile(NN_FILENAME);
        statsfile = getDataFile(STATS_FILENAME);
        actionsfile = getDataFile(ACTIONSTATS_FILENAME);
        qLogFile = getDataFile(Q_LOGFILE_NAME);

        // If the current file is not empty, load to LUT
        if (rlTrainer.getTrainingCounter() == 0) {
            System.out.println("Initializing Neural Net: ");
            try {
                if (NNfile.length() == 0) {
                    System.out.println("No neural net file detected, creating new neural net file...");
                }
                else{
                    rlTrainer.loadNN(NNfile);
                    System.out.println(rlTrainer.getTrainingCounter() + " battles have been trained");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else soutRobocodeDebug(rlTrainer.getTrainingCounter() + " battles have been trained!");
        if(policy == LearningPolicy.Q || policy == LearningPolicy.SARSA) rlTrainer.incTrainingCounter();

        //Count wins per 100 rounds.

        if (policy == LearningPolicy.noLearningGreedy) policy = LearningPolicy.Q;
        else policy = LearningPolicy.noLearningGreedy;

        if(roundCounter % (EPOCHS_PER_SAMPLE*2) == 0 && roundCounter > 0) {

            try {
                logWins();
                logActionsAndRmsAvg();
            } catch (IOException e) {
                e.printStackTrace();
            }


            winCounter = 0;
            //roundCounter = 0;
            qRmsSum = 0;
            rewardSum = 0;
            zeroActionCounter();
        }


        if(roundCounter % 2000 == 0 && roundCounter > 0 && currentExplorationRate > 0){
            currentExplorationRate -= 0.1;
            rlTrainer.setExplorationRate(currentExplorationRate);
        }

        roundCounter++;
        //Initialize first and next state at beginning of the round
        soutRobocodeDebug("Initialize state action vector in RL object");


        calibrateRobotHeading();

        System.out.println("Stats:  U: " + actionCounter[0] + "   D: " + actionCounter[1] + "   R: " + actionCounter[2]
        + "   L: " + actionCounter[3] + "   F: " + actionCounter[4] + "   N: " + actionCounter[5] + "   RMS:" +
                 qRmsSum/EPOCHS_PER_SAMPLE + "Wins: " + winCounter + "/" + (roundCounter%100 + 1));
        //Game Loop - spin radar constantly
        while(true)
            turnRadarRight(20);
    }


    @Override
    public void onScannedRobot(ScannedRobotEvent event) {

        stateVector = getStateVector(event);
//        if (countIntermediateRewards) {
//            currentReward += (myRobotEnergy - myRobotPreviousEnergy) * 20;
//            myRobotPreviousEnergy = myRobotEnergy;
//        }
        //Reinforced Learn. Also sets next action.

        this.action = rlTrainer.setNextAction(stateVector, policy);         //learn will get this state vector
        double qRMS = rlTrainer.learn(policy, currentReward);
        try {
            qLog(qRMS);
        } catch (IOException e) {
            e.printStackTrace();
        }

        qRmsSum += qRMS;
        if (policy == LearningPolicy.noLearningGreedy) rewardSum += currentReward;
        takeAction();

        prevStateVector = stateVector;

        this.currentReward = 0.0;
    }

/*           _    _____ _        _             __  _        _                     _   _
            | |  / ____| |      | |           / / | |      | |          /\       | | (_)
   __ _  ___| |_| (___ | |_ __ _| |_ ___     / /  | |_ __ _| | _____   /  \   ___| |_ _  ___  _ __
  / _` |/ _ \ __|\___ \| __/ _` | __/ _ \   / /   | __/ _` | |/ / _ \ / /\ \ / __| __| |/ _ \| '_ \
 | (_| |  __/ |_ ____) | || (_| | ||  __/  / /    | || (_| |   <  __// ____ \ (__| |_| | (_) | | | |
  \__, |\___|\__|_____/ \__\__,_|\__\___| /_/      \__\__,_|_|\_\___/_/    \_\___|\__|_|\___/|_| |_|
   __/ |
  |___/    */

    /**
     * Vectorize the current state into LUT coordinates
     * @return LUT state coordinate vector
     */
    private double[] getStateVector(ScannedRobotEvent event){
        // My Robot
        myRobotPosition = scalePosition(getX(), getY());
        myRobotEnergy = scaleEnergy(getEnergy());


        myRobotHeading = (int) getHeading();

        myRobotGunHeading = (int) getGunHeading();
        myRobotGunBearing = correctAngle(myRobotHeading - myRobotGunHeading);

        // Enemy Robot
        enemyAngle = correctAngle((int) getHeading() + (int) event.getBearing());

        enemyPosition = scalePosition(
                (getX() + event.getDistance()*Math.sin(Math.toRadians(enemyAngle))),
                (getY() + event.getDistance()*Math.cos(Math.toRadians(enemyAngle)))
        );


        if(enemyPosition[X] < myRobotPosition[X]) enemyPositionRelative[X] = -1;
        else if(enemyPosition[X] > myRobotPosition[X]) enemyPositionRelative[X] = 1;
        else enemyPositionRelative[X] = 0;
        if(enemyPosition[Y] < myRobotPosition[Y]) enemyPositionRelative[Y] = -1;
        else if(enemyPosition[Y] > myRobotPosition[Y]) enemyPositionRelative[Y] = 1;
        else enemyPositionRelative[Y] = 0;

        //Quantize enemyenergy chnange TODO: Move to it's own method
        enemyEnergy = (int) event.getEnergy();
        if(enemyEnergy > prevEnemyEnergy) enemyEnergyChange = 1;
        else if(enemyEnergy < prevEnemyEnergy) enemyEnergyChange = -1;
        else enemyEnergyChange = 0;
        prevEnemyEnergy = enemyEnergy;


        enemyDistance = scaleDistance((int) event.getDistance());
        enemyHeading = (int) event.getHeading();
        enemyHeadingQuantized = quantizeHeading(enemyHeading);
        enemyBearing = (int) event.getBearing();
        enemyBearingFromGun = correctAngle(myRobotGunBearing + enemyBearing);

        soutRobocodeDebug("Setting states\n\tmyPos: (" + myRobotPosition[X] + "," + myRobotPosition[Y] + ")" +
                "     theirPos: (" + enemyPositionRelative[X] + ","+ + enemyPositionRelative[Y] + ") +" +
                "\n\tmyEnergy: " + myRobotEnergy + "      enemyEnergy: " + enemyEnergyChange+
                "\n\tenemydistance: " + enemyDistance);

        return new double[] { myRobotPosition[X], myRobotPosition[Y], enemyPositionRelative[X], enemyPositionRelative[Y],
            myRobotEnergy, enemyEnergyChange, enemyDistance};
    }


    public void takeAction(){
        soutRobocodeDebug("Taking Action: " + action + " with exploration rate: " + rlTrainer.getExplorationRate());

        //Aim first in case fire action is chosen
        aimAtEnemy();
        waitFor(turnGunComplete);

        switch (action){
            case moveUp:
                if (policy == LearningPolicy.noLearningGreedy) actionCounter[0]++;
//                System.out.println("Moving Up   " + myRobotHeading);
                if (myRobotHeading < 180)
                    turnLeft(myRobotHeading);
                else
                    turnRight(360-myRobotHeading);
                ahead(MOVEMENT_DISTANCE);
                break;
            case moveDown:
                if (policy == LearningPolicy.noLearningGreedy) actionCounter[1]++;
//                System.out.println("Moving Down   " + myRobotHeading);
                if(myRobotHeading > 180)
                    turnLeft(myRobotHeading-180);
                else
                    turnRight(180-myRobotHeading);
                ahead(MOVEMENT_DISTANCE);
                break;
            case moveRight:
                if (policy == LearningPolicy.noLearningGreedy) actionCounter[2]++;
//                System.out.println("Moving right   " + myRobotHeading);
                if(myRobotHeading >= 0 && myRobotHeading <= 90)
                    turnRight(90-myRobotHeading);
                else if(myRobotHeading > 90 && myRobotHeading <= 270)
                    turnLeft(myRobotHeading - 90);
                else
                    turnRight((90 + 360)-myRobotHeading);
                ahead(MOVEMENT_DISTANCE);
                break;
            case moveLeft:
                if (policy == LearningPolicy.noLearningGreedy) actionCounter[3]++;
//                System.out.println("Moving Left   " + myRobotHeading);
                if(myRobotHeading > 270 && myRobotHeading < 360)
                    turnLeft(myRobotHeading-270);
                if(myRobotHeading > 180 && myRobotHeading < 270)
                    turnRight(270 - myRobotHeading);
                if(myRobotHeading >= 0 && myRobotHeading <= 90)
                    turnLeft(90 + myRobotHeading);
                else
                    turnRight(270-myRobotHeading);
                ahead(MOVEMENT_DISTANCE);
                break;
            case fire:
                if (policy == LearningPolicy.noLearningGreedy) actionCounter[4]++;
                smartFire(enemyDistance);
                break;
            case none:
                if (policy == LearningPolicy.noLearningGreedy) actionCounter[5]++;
                break;
        }
        waitFor(turnComplete);
        waitFor(moveComplete);
    }

/*            _  __
             | |/ /
   ___  _ __ | ' / ___ _   _ _ __  _ __ ___  ___ ___
  / _ \| '_ \|  < / _ \ | | | '_ \| '__/ _ \/ __/ __|
 | (_) | | | | . \  __/ |_| | |_) | | |  __/\__ \__ \
  \___/|_| |_|_|\_\___|\__, | .__/|_|  \___||___/___/
                        __/ | |
                       |___/|_|     */
    @Override
    public void onKeyPressed(KeyEvent e) {

        switch (e.getKeyCode())
        {
            case KeyEvent.VK_0:
                System.out.println("Exploration rate is 0%");
                rlTrainer.setExplorationRate(0);
                break;
            case KeyEvent.VK_1:
                System.out.println("Exploration rate is 20%");
                rlTrainer.setExplorationRate(0.20);
                break;
            case KeyEvent.VK_2:
                System.out.println("Exploration rate is 40%");
                rlTrainer.setExplorationRate(0.40);
                break;
            case KeyEvent.VK_3:
                System.out.println("Exploration rate is 60%");
                rlTrainer.setExplorationRate(0.60);
                break;
            case KeyEvent.VK_4:
                System.out.println("Exploration rate is 80%");
                rlTrainer.setExplorationRate(0.80);
                break;
            case KeyEvent.VK_5:
                System.out.println("Exploration rate is 100%");
                rlTrainer.setExplorationRate(1);
                break;
            case KeyEvent.VK_C:
                System.out.println("Reinitializing Neural Net");
                rlTrainer.initializeNN();
                winCounter = 0;
                roundCounter = 0;
            case KeyEvent.VK_E:
                if(errorLogEnable){
                    System.out.println("disabling error log");
                    errorLogEnable = false;
                }
                else{
                    System.out.println("enabling error log");
                    errorLogEnable = true;
                }
                break;
            case KeyEvent.VK_S:
                System.out.println("+++Neural Net Data will be saved at the end of this battle.");
                saveFlag = true;
            default:

                break;
        }
    }

/*_____        _          ______                         _   _
 |  __ \      | |        |  ____|                       | | (_)
 | |  | | __ _| |_ __ _  | |__ ___  _ __ _ __ ___   __ _| |_ _ _ __   __ _
 | |  | |/ _` | __/ _` | |  __/ _ \| '__| '_ ` _ \ / _` | __| | '_ \ / _` |
 | |__| | (_| | || (_| | | | | (_) | |  | | | | | | (_| | |_| | | | | (_| |
 |_____/ \__,_|\__\__,_| |_|  \___/|_|  |_| |_| |_|\__,_|\__|_|_| |_|\__, |
                                                                      __/ |
                                                                     |___/   */
    /**
     * Take angle input and correct to between -180 and 180 degrees
     *
     * @param angle [degrees]
     * @return corrected angle
     */
    private int correctAngle(int angle)
    {
        int a = angle;
        while (a < -180) a += 360;
        while (a > 180) a -= 360;
        return a;
    }

    private int[] quantizePosition(int x, int y){
        int[] r = {0,0};
        r[X] = (x-1) / POSITION_QUANTA;
        r[Y] = (y-1) / POSITION_QUANTA;
        return r;
    }

    /**
     * Quantize the enemy distance into three values
     * @param x enemy distance as read in with RoboCode
     * @return
     */
    private int quantizeEnemyDistance(int x){
        if(x < 250) return 0;
        if(x < 500) return 1;
        return 2;
    }


    /**
     * Quantgize energy value into 3 bins
     * @param x energy level read by robocode
     * @return
     */
    private int quantizeEnergy(int x){
        if (x > 70) return 2;
        else if (x > 30) return 1;
        else return 0;
    }

    /**
     * Quantize heading
     * @param x -> 0 = N; 1 = E; 2 = S; 3 = W
     * @return
     */
    private int quantizeHeading(int x){
        //Angle conditioned to 0 to 360 degrees
        x += HEADING_QUANTA_OFFSET;
        if(x >= 360) x-= 360;
        if(x < 0) x+= 360;
        return x / HEADING_QUANTA;
    }

    /**
     * Scale the position from 0->800 by 0->600 to -1.0->1.0 by -1.0->1.0;
     * @param x, y input (X,Y) vector
     * @return
     */
    private double[] scalePosition(double x, double y){
        double[] ss = {0,0};
        ss[X] = x/400 - 1;
        ss[Y] = y/300 - 1;
        return ss;
    }

    private double scaleEnergy(double e){
        return (e - 50) / 100;
    }

    private double scaleDistance(double d){
        return d/1000;
    }

    /**
     *
     *
     * @return
     */
    private double[] scaleEnemyPositon(){
        double[] ss = {0,0};
        ss[X] = Math.cos(Math.toRadians(this.enemyAngle));
        ss[Y] = Math.sin(Math.toRadians(this.enemyAngle));
        return ss;
    }


/*______               _     _    _                 _ _
 |  ____|             | |   | |  | |               | | |
 | |____   _____ _ __ | |_  | |__| | __ _ _ __   __| | | ___ _ __ ___
 |  __\ \ / / _ \ '_ \| __| |  __  |/ _` | '_ \ / _` | |/ _ \ '__/ __|
 | |___\ V /  __/ | | | |_  | |  | | (_| | | | | (_| | |  __/ |  \__ \
 |______\_/ \___|_| |_|\__| |_|  |_|\__,_|_| |_|\__,_|_|\___|_|  |___/  */

    @Override
    public void onRoundEnded(RoundEndedEvent event) {
//        try {
//            qLog(0, true);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        //rlTrainer.learn(policy, currentReward);
        if(saveFlag == true){
            rlTrainer.saveNN(NNfile);
            saveFlag = false;
        }
        super.onRoundEnded(event);

        rlTrainer.resetRingBuffer();
    }

    public void onBattleEnded(BattleEndedEvent event)
    {
        System.out.println("+++BATTLE ENDED");
        System.out.println("Round counter = " + roundCounter);
        rlTrainer.saveNN(NNfile);

        if(roundCounter % 100 == 0 && roundCounter > 0) {
            try {
                logWins();
                logActionsAndRmsAvg();
            } catch (IOException e) {
                e.printStackTrace();
            }
            roundCounter = 0;
            winCounter = 0;
        }

    }

    public void onBulletHit(BulletHitEvent event)
    {
        if (countIntermediateRewards)
            currentReward += INTERMEDIATE_REWARD;
    }

    public void onHitByBullet(HitByBulletEvent event)
    {
        if (countIntermediateRewards)
            currentReward -= INTERMEDIATE_REWARD/2;
    }

    @Override
    public void onHitWall(HitWallEvent event) {
        if (countIntermediateRewards)
            currentReward -= INTERMEDIATE_REWARD*2;
    }

    @Override
    public void onHitRobot(HitRobotEvent event) {
        if (countIntermediateRewards)
            currentReward += INTERMEDIATE_REWARD;
    }

    public void onDeath(DeathEvent event)
    {
        //Subtract terminal reward and learn
        currentReward -= TERMINAL_REWARD/2;
        rlTrainer.learn(policy, currentReward);
        currentReward = 0;
    }

    public void onWin(WinEvent event)
    {
        currentReward  += TERMINAL_REWARD;
        rlTrainer.learn(policy, currentReward);
        //Add terminal reward and learn
        if(policy == LearningPolicy.noLearningGreedy) {
            winCounter++;
        }
        currentReward = 0;
    }


/*_____       _           _                  _   _
 |  __ \     | |         | |       /\       | | (_)
 | |__) |___ | |__   ___ | |_     /  \   ___| |_ _  ___  _ __  ___
 |  _  // _ \| '_ \ / _ \| __|   / /\ \ / __| __| |/ _ \| '_ \/ __|
 | | \ \ (_) | |_) | (_) | |_   / ____ \ (__| |_| | (_) | | | \__ \
 |_|  \_\___/|_.__/ \___/ \__| /_/    \_\___|\__|_|\___/|_| |_|___/  */

    /**
     * Make robot face due north. 
     */
    private void calibrateRobotHeading() {

        setTurnLeft((int) getHeading());
        execute();
        waitFor(turnComplete);
    }

    /**
     * moveCircle: drive in a circle with a given radius
     */
    public void moveCircle(double radius) {
        setTurnRight(90);
        setAhead(Math.PI * radius / 2);
        execute();
    }

    public void moveCircleCW(double radius) {
        setTurnRight(90);
        setAhead(Math.PI * radius / 2);
        execute();
    }

    public void moveCircleCCW(double radius) {
        setTurnLeft(90);
        setAhead(Math.PI * radius / 2);
        execute();
    }


    public void retreat(){
        if(enemyBearing > 0) {
            setTurnLeft(correctAngle(180 - enemyBearing));
        }
        else {
            setTurnRight(correctAngle(180+enemyBearing));
        }
        setAhead(MOVEMENT_DISTANCE);
        execute();
    }

    public void advance(){
        if(enemyBearing > 0)
            setTurnRight(enemyBearing);
        else setTurnLeft(enemyBearing);
        setAhead(MOVEMENT_DISTANCE);

    }

    public void aimAtEnemy(){
        int gunBearingtoEnemy = correctAngle(myRobotHeading + enemyBearing - myRobotGunHeading);
//        System.out.println("Enemy Bearing: " + enemyBearing + "  Gun Heading: " + myRobotGunHeading +
//                "  gunBearingtoEnemy: " + gunBearingtoEnemy + "\n\tEnemy Bearing from Gun: " + enemyBearingFromGun);
        setTurnGunRight(gunBearingtoEnemy);
    }

    /**
     * smartFire:  Custom fire method that determines firepower based on distance.
     *
     * @param robotDistance the distance to the robot to fire at
     */
    public void smartFire(double robotDistance) {
        if (robotDistance > 0.50 || getEnergy() < 15) {
            soutRobocodeDebug("Smart Fire - close range");
            fire(1);
        } else if (robotDistance > 0.250) {
            soutRobocodeDebug("Smart Fire - long range");
            fire(2);
        } else {
            soutRobocodeDebug("Smart Fire - mid range");
            fire(3);
        }
    }

/* _____ _        _               _      _                          _
  / ____| |      | |             | |    | |                        | |
 | (___ | |_ __ _| |_ ___      __| | ___| |__  _   _  __ _      ___| |_ ___
  \___ \| __/ _` | __/ __|    / _` |/ _ \ '_ \| | | |/ _` |    / _ \ __/ __|
  ____) | || (_| | |_\__ \_  | (_| |  __/ |_) | |_| | (_| |_  |  __/ || (__ _ _ _
 |_____/ \__\__,_|\__|___( )  \__,_|\___|_.__/ \__,_|\__, ( )  \___|\__\___(_|_|_)
                         |/                           __/ |/
                                                     |___/       */


    private void soutRobocodeDebug(String s){
        if(debugFlag == true) System.out.println(s);
    }


    /**
     * Write CSV of win counts every 100 rounds
     */
    public void logWins() throws IOException, IOException{
        try
        {
            System.out.println("+++Saving Win Stats...   " + winCounter + " battles won\n");
            FileOutputStream outFile = new FileOutputStream(statsfile, true);
            PrintStream out = new PrintStream(outFile);
            String s = String.format("%f\n", (double) winCounter);
            out.print(s);
            out.flush();
            out.close();
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }
    }

    public void logActionsAndRmsAvg() throws IOException, IOException{
        try
        {
            System.out.println("+++Saving Action Stats...   ");
            FileOutputStream outFile = new FileOutputStream(actionsfile, true);
            PrintStream out = new PrintStream(outFile);
            double[] normalizedActions = {0,0,0,0,0,0};
            double sum = 0;
            for(int i = 0; i < 6; i++){
                sum += actionCounter[i];
            }
            String s = String.format("%f,%f,%f,%f,%f,%f,%f,%f,%f\n", sum, actionCounter[0]/sum, actionCounter[1]/sum,
                    actionCounter[2]/sum, actionCounter[3]/sum, actionCounter[4]/sum, actionCounter[5]/sum,
                    qRmsSum /EPOCHS_PER_SAMPLE, rewardSum / EPOCHS_PER_SAMPLE);
            out.print(s);
            out.flush();
            out.close();
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }
    }

    public void qLog(double data) throws IOException, IOException{
        if(errorLogEnable) {
            try {

                FileOutputStream outFile = new FileOutputStream(qLogFile, true);
                PrintStream out = new PrintStream(outFile);
                String s = "";
                s = String.format("%f\n", data);
                out.print(s);
                out.flush();
                out.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }
    
    private void zeroActionCounter(){
        for( int i = 0; i < 6; i++){
            this.actionCounter[i] = 0;
        }
    }

}
