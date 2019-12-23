///*
//  _____      _        __                                        _     _                           _
// |  __ \    (_)      / _|                                      | |   | |                         (_)
// | |__) |___ _ _ __ | |_ ___  _ __ ___ ___ _ __ ___   ___ _ __ | |_  | |     ___  __ _ _ __ _ __  _ _ __   __ _
// |  _  // _ \ | '_ \|  _/ _ \| '__/ __/ _ \ '_ ` _ \ / _ \ '_ \| __| | |    / _ \/ _` | '__| '_ \| | '_ \ / _` |
// | | \ \  __/ | | | | || (_) | | | (_|  __/ | | | | |  __/ | | | |_  | |___|  __/ (_| | |  | | | | | | | | (_| |
// |_|  \_\___|_|_|_|_|_| \___/|_|__\___\___|_| |_| |_|\___|_| |_|\__| |______\___|\__,_|_|  |_| |_|_|_| |_|\__, |
// \ \        / (_) | | |     |  __ \     | |                       | |                                      __/ |
//  \ \  /\  / / _| |_| |__   | |__) |___ | |__   ___   ___ ___   __| | ___                                 |___/
//   \ \/  \/ / | | __| '_ \  |  _  // _ \| '_ \ / _ \ / __/ _ \ / _` |/ _ \
//    \  /\  /  | | |_| | | | | | \ \ (_) | |_) | (_) | (_| (_) | (_| |  __/
//  _  \/  \/   |_|\__|_| |_| |_|  \_\___/|_.__/ \___/ \___\___/_\__,_|\___| _
// | |               /\             | |                    / ____|    | |   | |
// | |__  _   _     /  \   _ __   __| |_ __ _____      __ | (___   ___| |__ | | ___  ___ ___  ___ _ __
// | '_ \| | | |   / /\ \ | '_ \ / _` | '__/ _ \ \ /\ / /  \___ \ / __| '_ \| |/ _ \/ __/ __|/ _ \ '__|
// | |_) | |_| |  / ____ \| | | | (_| | | |  __/\ V  V /   ____) | (__| | | | | (_) \__ \__ \  __/ |
// |_.__/ \__, | /_/    \_\_| |_|\__,_|_|  \___| \_/\_/ __|_____/_\___|_|_|_|_|\___/|___/___/\___|_|
// | \ | | __/ |                  | |               /_ |___ \   |__ \ / _ \/_ |/ _ \
// |  \| ||___/_   _____ _ __ ___ | |__   ___ _ __   | | __) |     ) | | | || | (_) |
// | . ` |/ _ \ \ / / _ \ '_ ` _ \| '_ \ / _ \ '__|  | ||__ <     / /| | | || |\__, |
// | |\  | (_) \ V /  __/ | | | | | |_) |  __/ |     | |___) |   / /_| |_| || |  / /
// |_| \_|\___/ \_/ \___|_| |_| |_|_.__/ \___|_|     |_|____( ) |____|\___/ |_| /_/
//   __             _    _ ____   _____             _____ __|/_  ______ _   _ _____  ___ ___
//  / _|           | |  | |  _ \ / ____|           / ____|  __ \|  ____| \ | | ____|/ _ \__ \
// | |_ ___  _ __  | |  | | |_) | |       ______  | |    | |__) | |__  |  \| | |__ | | | | ) |
// |  _/ _ \| '__| | |  | |  _ <| |      |______| | |    |  ___/|  __| | . ` |___ \| | | |/ /
// | || (_) | |    | |__| | |_) | |____           | |____| |    | |____| |\  |___) | |_| / /_
// |_| \___/|_|     \____/|____/ \_____|           \_____|_|    |______|_| \_|____/ \___/____|
// */
//
//package LearningAlgorithms;
//
//import robocode.*;
//import Enum.*;
//
//import java.awt.*;
//import java.awt.event.KeyEvent;
//import java.io.*;
//
//
//public class RobocodeRLbot extends AdvancedRobot {
//
//    //Flag for debug output
//    private boolean debugFlag = false;
//
///* _____                _              _
//  / ____|              | |            | |
// | |     ___  _ __  ___| |_ __ _ _ __ | |_ ___
// | |    / _ \| '_ \/ __| __/ _` | '_ \| __/ __|
// | |___| (_) | | | \__ \ || (_| | | | | |_\__ \
//  \_____\___/|_| |_|___/\__\__,_|_| |_|\__|___/ */
//
//    //ROBOCODE Settings
//    private final static int HEADING_QUANTA = 90;        //Quanta step size for heading
//    private final static int HEADING_QUANTA_OFFSET = 45; //Quanta step size for heading
//    private final static int POSITION_QUANTA = 100;      //Quanta step size for position
//    private final static int MOVEMENT_DISTANCE = 100;    //Distance moved each turn
//
//
//    //File handling
//    static private File LUTfile;
//    static private File statsfile;
//    private final static String LUT_FILENAME = "robocode_lut_file.csv";
//    private final static String STATS_FILENAME = "robocode_stats_file.csv";
//
//
//    //RL SETTINGS
//    private final static double LEARNING_RATE = 0.9;
//    private final static double DISCOUNT_RATE = 0.9;
//    private final static double EXPLORATION = 0.9;
//    private final static int BOOTSTRAP_LENGTH = 1;
//    //--------------------
//    private final static boolean countIntermediateRewards = false;
//    private final static double TERMINAL_REWARD = 100;
//    private final static double INTERMEDIATE_REWARD = 50;
//    private final static int ACTION_COUNT = Actions.getSize();
//    //State Settings
//    //States:  self_x(8), self_y(6), enemy_x(3), enemy_y(3), myenergy(3), enemyEnergyGainLose(3), enemyDistance(3)
//    private final static int[] STATE_COUNTS = {8,6,3,3,3,3,3};
//
//    //Action Settings
//    //Action Hash to Integer Table
//    //----Bits--------Action---------Description-----------------
//    //    00-02       Turn          -180 + 45 * TurnValue(0-8)
//    //    03-04       Move          Distance:   0 - no movement
//    //                                          1 - move away 50% enemy distance
//    //                                          2 - move towards 25% enemy distance
//    //                                          3 - move towards 50% enemy distance
//    //    05-06      Fire          Firepower = 0-(no fire), 1, 2, 3
//    //    07         Aim @ Enemy   Turn gun towards enemy
//    //    TOTAL ACTION COMBINATIONS: 2^8 = 256.
////    private final static int ACTION_COUNT = 256;
//    //Sequential Shifts, processed in order
////    private final static int ACTION_SHIFT_MOVE = 3;
////    private final static int ACTION_SHIFT_FIRE = 2;
////    private final static int ACTION_SHIFT_AIM  = 2;
////    private final static int ACTION_MOD_TURN = 0x0008;
////    private final static int ACTION_MOD_MOVE = 0x0004;
////    private final static int ACTION_MOD_FIRE = 0x0004;
////    private final static int ACTION_MOD_AIM  = 0x0002;
//
//    //Generic Constants
//    private final static int X = 0; // x index for position vector
//    private final static int Y = 1; // y index for position vector
//
//
///*_____             _   _                 __      __        _       _     _
// |  __ \           | | (_)                \ \    / /       (_)     | |   | |
// | |__) |   _ _ __ | |_ _ _ __ ___   ___   \ \  / /_ _ _ __ _  __ _| |__ | | ___  ___
// |  _  / | | | '_ \| __| | '_ ` _ \ / _ \   \ \/ / _` | '__| |/ _` | '_ \| |/ _ \/ __|
// | | \ \ |_| | | | | |_| | | | | | |  __/    \  / (_| | |  | | (_| | |_) | |  __/\__ \
// |_|  \_\__,_|_| |_|\__|_|_| |_| |_|\___|     \/ \__,_|_|  |_|\__,_|_.__/|_|\___||___/  */
//
//    // Completion Conditions
//    private final TurnCompleteCondition turnComplete = new TurnCompleteCondition(this);
//    private final MoveCompleteCondition moveComplete = new MoveCompleteCondition(this);
//    private final GunTurnCompleteCondition turnGunComplete = new GunTurnCompleteCondition(this);
//
//    // Variables to track the state of my robot
//    private int[] myRobotPosition = new int[2]; //{x, y}
//    private int myRobotHeading;
//    private int myRobotHeadingQuantized;
//    private int myRobotGunHeading;
//    private int myRobotGunBearing;
//    private int myRobotEnergy;
//    private int myRobotPreviousEnergy = 100;
//
//    // Variables to track the state of enemy robot
//    private int[] enemyPosition = new int[2];
//    private int[] enemyPositionRelative = new int[2]; //X: 0-same,1-left,2-right  //Y: 0-same,1-below,2-above
//
//    private int enemyDistance;
//    private int enemyHeading;
//    private int enemyHeadingQuantized;
//    private int enemyBearing;
//    private int enemyBearingFromGun;
//    private int enemyEnergy;
//    private int enemyAngle;             //angle between myRobot heading and enemy
//    private int enemyEnergyChange;       //0 none, 1 lose, 2 gain.
//    private int prevEnemyEnergy;
//
//    //Reinforcement Learning Variables
//    static private ReinforementLearning rlTrainer = new ReinforementLearning(
//            STATE_COUNTS, ACTION_COUNT, LEARNING_RATE, DISCOUNT_RATE, EXPLORATION, BOOTSTRAP_LENGTH);
//    static private Actions action = Actions.none;                 //current action to be taken
//    //static private int action = 0;
//
//    static private LearningPolicy policy = LearningPolicy.Q;      //default to Q learning
//    static private double currentReward = 0;
//    static private double[] stateVector = new double[STATE_COUNTS.length];
//
//    static private int winCounter = 0;
//    static private int roundCounter = 0;
//    static boolean explorationToggle = true;
//
//    static double currentExplorationRate = EXPLORATION;
///*_____                   __   ____           _____                                _   _____       _           _
// |  __ \                 / /  / __ \         / ____|                              | | |  __ \     | |         | |
// | |__) |   _ _ __      / /  | |  | |_ __   | (___   ___ __ _ _ __  _ __   ___  __| | | |__) |___ | |__   ___ | |_
// |  _  / | | | '_ \    / /   | |  | | '_ \   \___ \ / __/ _` | '_ \| '_ \ / _ \/ _` | |  _  // _ \| '_ \ / _ \| __|
// | | \ \ |_| | | | |  / /    | |__| | | | |  ____) | (_| (_| | | | | | | |  __/ (_| | | | \ \ (_) | |_) | (_) | |_
// |_|  \_\__,_|_| |_| /_/      \____/|_| |_| |_____/ \___\__,_|_| |_|_| |_|\___|\__,_| |_|  \_\___/|_.__/ \___/ \__| */
//
//    @Override
//    public void run(){
//
//        //myRobot configuration
//        setColors(Color.RED, Color.YELLOW, Color.ORANGE, Color.PINK, Color.GREEN);
//        setAdjustGunForRobotTurn(true);
//        setAdjustRadarForGunTurn(true);
//        setAdjustRadarForRobotTurn(true);
//
//        //Get lookup table and statistics files
//        LUTfile = getDataFile(LUT_FILENAME);
//        statsfile = getDataFile(STATS_FILENAME);
//        // If the current file is not empty, load to LUT
//        if (rlTrainer.getTrainingCounter() == 0) {
//            System.out.print("No battles have been trained: ");
//            try {
//                if (LUTfile.length() == 0) {
//                    System.out.println("Create New LUT File...");
//                }
//                else
//                   rlTrainer.loadLUT(LUTfile);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        else soutRobocodeDebug(rlTrainer.getTrainingCounter() + " battles have been trained!");
//        if(policy == LearningPolicy.Q || policy == LearningPolicy.SARSA) rlTrainer.incTrainingCounter();
//
//        //Count wins per 100 rounds.
//
//        if(roundCounter % 100 == 0 && roundCounter > 0) {
//            if(explorationToggle == true){
//                explorationToggle = false;
//                rlTrainer.setExplorationRate(0);
//            }
//            else{
//                explorationToggle = true;
//                rlTrainer.setExplorationRate(currentExplorationRate);
//                try {
//                    logWins();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            roundCounter = 0;
//            winCounter = 0;
//        }
//
//        if(roundCounter % 10000 == 0 && roundCounter > 0 && currentExplorationRate > 0){
//            currentExplorationRate -= 0.1;
//        }
//
//
//
//
//        //===================================================================================================//
////        if(roundCounter == 5000){
////            System.out.println("reducing exploration to 0%");
////            rlTrainer.setExplorationRate(0);
////        }
//        //====================================================================================================//
//
//
//        roundCounter++;
//        //Initialize first and next state at beginning of the round
//        soutRobocodeDebug("Initialize state action vector in RL object");
//        rlTrainer.stateActionInit();
//
//        calibrateRobotHeading();
//        //Game Loop - spin radar constantly
//        while(true)
//            turnRadarRight(20);
//    }
//
//
//    @Override
//    public void onScannedRobot(ScannedRobotEvent event) {
//
//        stateVector = getStateVector(event);
//        if (countIntermediateRewards){
//            currentReward += myRobotEnergy - myRobotPreviousEnergy;
//            myRobotPreviousEnergy = myRobotEnergy;
//        }
//
//
//        //Reinforced Learn. Also sets next action.
//        this.action = rlTrainer.learn(stateVector, policy, currentReward);
//        takeAction();
//        this.currentReward = 0.0;
//    }
//
///*           _    _____ _        _             __  _        _                     _   _
//            | |  / ____| |      | |           / / | |      | |          /\       | | (_)
//   __ _  ___| |_| (___ | |_ __ _| |_ ___     / /  | |_ __ _| | _____   /  \   ___| |_ _  ___  _ __
//  / _` |/ _ \ __|\___ \| __/ _` | __/ _ \   / /   | __/ _` | |/ / _ \ / /\ \ / __| __| |/ _ \| '_ \
// | (_| |  __/ |_ ____) | || (_| | ||  __/  / /    | || (_| |   <  __// ____ \ (__| |_| | (_) | | | |
//  \__, |\___|\__|_____/ \__\__,_|\__\___| /_/      \__\__,_|_|\_\___/_/    \_\___|\__|_|\___/|_| |_|
//   __/ |
//  |___/    */
//
//    /**
//     * Vectorize the current state into LUT coordinates
//     * @return LUT state coordinate vector
//     */
//    private double[] getStateVector(ScannedRobotEvent event){
//        // My Robot
//
//        myRobotPosition = quantizePosition((int) getX(), (int) getY());
//        myRobotEnergy = quantizeEnergy((int) getEnergy());
//
//        myRobotHeading = (int) getHeading();
//        myRobotHeadingQuantized = quantizeHeading(myRobotHeading);
//        myRobotGunHeading = (int) getGunHeading();
//        myRobotGunBearing = correctAngle(myRobotHeading - myRobotGunHeading);
//
//        // Enemy Robot
//        enemyAngle = correctAngle((int) getHeading() + (int) event.getBearing());
//        enemyPosition = quantizePosition(
//                (int) (getX() + event.getDistance()*Math.cos(Math.toRadians(enemyAngle))),
//                (int) (getY() + event.getDistance()*Math.sin(Math.toRadians(enemyAngle)))
//        );
//
//        //Set relative enemy LR positions
//        if(enemyPosition[X] < myRobotPosition[X]) enemyPositionRelative[X] = 1;
//        else if(enemyPosition[X] > myRobotPosition[X]) enemyPositionRelative[X] = 2;
//        else enemyPositionRelative[X] = 0;
//        if(enemyPosition[Y] < myRobotPosition[Y]) enemyPositionRelative[Y] = 1;
//        else if(enemyPosition[Y] > myRobotPosition[Y]) enemyPositionRelative[Y] = 2;
//        else enemyPositionRelative[Y] = 0;
//
//        //DEBUG
////        switch(enemyPositionRelative[X]){
////            case 0:
////                System.out.println("Enemy at equal X");
////                break;
////            case 1:
////                System.out.println("Enemy to the left");
////                break;
////            case 2:
////                System.out.println("Enemy to the Right");
////        }
////
////        switch (enemyPositionRelative[Y]){
////            case 0:
////                System.out.println("Enemy at equal Y");
////                break;
////            case 1:
////                System.out.println("Enemy is below ");
////                break;
////            case 2:
////                System.out.println("Enemy is above");
////                break;
////        }
//
//
//
//        //Quantize enemyenergy chnge TODO: Move to it's own method
//        enemyEnergy = (int) event.getEnergy();
//        if(enemyEnergy > prevEnemyEnergy) enemyEnergyChange = 2;
//        else if(enemyEnergy < prevEnemyEnergy) enemyEnergyChange = 1;
//        else enemyEnergyChange = 0;
//        prevEnemyEnergy = enemyEnergy;
//
//
//        enemyDistance = quantizeEnemyDistance((int) event.getDistance());
//        enemyHeading = (int) event.getHeading();
//        enemyHeadingQuantized = quantizeHeading(enemyHeading);
//        enemyBearing = (int) event.getBearing();
//        enemyBearingFromGun = correctAngle(myRobotGunBearing + enemyBearing);
//
//        soutRobocodeDebug("Setting states\n\tmyPos: (" + myRobotPosition[X] + "," + myRobotPosition[Y] + ")" +
//                "     theirPos: (" + enemyPositionRelative[X] + ","+ + enemyPositionRelative[Y] + ") +" +
//                "\n\tmyEnergy: " + myRobotEnergy + "      enemyEnergy: " + enemyEnergyChange+
//                "\n\tenemydistance: " + enemyDistance);
//        return new double[] { myRobotPosition[X], myRobotPosition[Y], enemyPositionRelative[X], enemyPositionRelative[Y],
//            myRobotEnergy, enemyEnergyChange, enemyDistance};
//    }
//
//
//    public void takeAction(){
//        soutRobocodeDebug("Taking Action: " + action + " with exploration rate: " + rlTrainer.getExplorationRate());
//
//        //Aim first in case fire action is chosen
//        aimAtEnemy();
//        waitFor(turnGunComplete);
//
//        switch (action){
//            case moveUp:
////                System.out.println("Moving Up   " + myRobotHeading);
//                if (myRobotHeading < 180)
//                    turnLeft(myRobotHeading);
//                else
//                    turnRight(360-myRobotHeading);
//                ahead(MOVEMENT_DISTANCE);
//                break;
//            case moveDown:
////                System.out.println("Moving Down   " + myRobotHeading);
//                if(myRobotHeading > 180)
//                    turnLeft(myRobotHeading-180);
//                else
//                    turnRight(180-myRobotHeading);
//                ahead(MOVEMENT_DISTANCE);
//                break;
//            case moveRight:
////                System.out.println("Moving right   " + myRobotHeading);
//                if(myRobotHeading >= 0 && myRobotHeading <= 90)
//                    turnRight(90-myRobotHeading);
//                else if(myRobotHeading > 90 && myRobotHeading <= 270)
//                    turnLeft(myRobotHeading - 90);
//                else
//                    turnRight((90 + 360)-myRobotHeading);
//                ahead(MOVEMENT_DISTANCE);
//                break;
//            case moveLeft:
////                System.out.println("Moving Left   " + myRobotHeading);
//                if(myRobotHeading > 270 && myRobotHeading < 360)
//                    turnLeft(myRobotHeading-270);
//                if(myRobotHeading > 180 && myRobotHeading < 270)
//                    turnRight(270 - myRobotHeading);
//                if(myRobotHeading >= 0 && myRobotHeading <= 90)
//                    turnLeft(90 + myRobotHeading);
//                else
//                    turnRight(270-myRobotHeading);
//                ahead(MOVEMENT_DISTANCE);
//                break;
//            case fire:
//                smartFire(enemyDistance);
//                break;
//        }
//        waitFor(turnComplete);
//        waitFor(moveComplete);
//
//
//        //deprecated code from hashing version
//        /*
//        int turn = 0;
//        int move = 0;
//        int shoot = 0;
//        int aim = 0;
//        //Action Hash to Integer Table
//        //----Bits--------Action---------Description-----------------
//        //    00-02       Turn          -180 + 45 * TurnValue(0-8)
//        //    03-05       Move          Distance:   0,4 - no movement
//        //                                          1-3 = reverse 33%*n enemy distance
//        //                                          5-7 = forward 33%*(n-4) enemy distance
//        //    06-07       Fire          Firepower = 0-(no fire), 1, 2, 3
//        //    08          Aim @ Enemy   Turn gun towards enemy
//        //    TOTAL ACTION COMBINATIONS: 2^9 = 512.
//
//        //Parse Action Hash
//        int parsedAction = this.action;
//        if(parsedAction % ACTION_MOD_TURN > 0){
//            turn = parsedAction % ACTION_MOD_TURN;
//        }
//        parsedAction  >>= ACTION_SHIFT_MOVE;
//        if(parsedAction  % ACTION_MOD_MOVE > 0){
//            move = parsedAction % ACTION_MOD_MOVE;
//        }
//        parsedAction  >>= ACTION_SHIFT_FIRE;
//        if (parsedAction % ACTION_MOD_FIRE > 0) {
//            shoot = parsedAction % ACTION_MOD_FIRE;
//        }
//        parsedAction  >>= ACTION_SHIFT_AIM;
//        if (parsedAction % ACTION_MOD_AIM > 0) {
//            aim = parsedAction;
//        }
//
//        soutRobocodeDebug("Value: " + action + "  Turn: " + turn + "  Move: " + move + "  Fire: " + shoot + "  Aim:" + aim);
//
//
//        if(turn > 0){
//            setTurnRight( (int) (180 - 22.5*turn) );
//        }
//
//        if(move > 0){
//            switch(move){
//                case 1:
//                    setAhead( (int) (-enemyDistance*POSITION_QUANTA*0.5));
//                    break;
//                case 2:
//                    setAhead( (int) (enemyDistance*POSITION_QUANTA*0.25));
//                    break;
//                case 3:
//                    setAhead( (int) (enemyDistance*POSITION_QUANTA*0.5));
//                    break;
//            }
//        }
//
//        execute();
//        waitFor(turnComplete);
//        waitFor(moveComplete);
//        if(aim == 1) {
//            aimAtEnemy();
//        }
//        //Execute set actions
//        execute();
//        waitFor(turnGunComplete);
//
//        if(shoot > 0){
//            fire(shoot);
//        }*/
//    }
//
///*            _  __
//             | |/ /
//   ___  _ __ | ' / ___ _   _ _ __  _ __ ___  ___ ___
//  / _ \| '_ \|  < / _ \ | | | '_ \| '__/ _ \/ __/ __|
// | (_) | | | | . \  __/ |_| | |_) | | |  __/\__ \__ \
//  \___/|_| |_|_|\_\___|\__, | .__/|_|  \___||___/___/
//                        __/ | |
//                       |___/|_|     */
//    @Override
//    public void onKeyPressed(KeyEvent e) {
//
//        switch (e.getKeyCode())
//        {
//            case KeyEvent.VK_0:
//                System.out.println("Exploration rate is 0%");
//                rlTrainer.setExplorationRate(0);
//                break;
//            case KeyEvent.VK_1:
//                System.out.println("Exploration rate is 20%");
//                rlTrainer.setExplorationRate(0.20);
//                break;
//            case KeyEvent.VK_2:
//                System.out.println("Exploration rate is 40%");
//                rlTrainer.setExplorationRate(0.40);
//                break;
//            case KeyEvent.VK_3:
//                System.out.println("Exploration rate is 60%");
//                rlTrainer.setExplorationRate(0.60);
//                break;
//            case KeyEvent.VK_4:
//                System.out.println("Exploration rate is 80%");
//                rlTrainer.setExplorationRate(0.80);
//                break;
//            case KeyEvent.VK_5:
//                System.out.println("Exploration rate is 100%");
//                rlTrainer.setExplorationRate(1);
//                break;
//            case KeyEvent.VK_C:
//                System.out.println("Clearing Lookup Table");
//                rlTrainer.initializeLUT();
//                winCounter = 0;
//                roundCounter = 0;
//            default:
//                break;
//        }
//    }
///*_____        _          ______                         _   _
// |  __ \      | |        |  ____|                       | | (_)
// | |  | | __ _| |_ __ _  | |__ ___  _ __ _ __ ___   __ _| |_ _ _ __   __ _
// | |  | |/ _` | __/ _` | |  __/ _ \| '__| '_ ` _ \ / _` | __| | '_ \ / _` |
// | |__| | (_| | || (_| | | | | (_) | |  | | | | | | (_| | |_| | | | | (_| |
// |_____/ \__,_|\__\__,_| |_|  \___/|_|  |_| |_| |_|\__,_|\__|_|_| |_|\__, |
//                                                                      __/ |
//                                                                     |___/   */
//    /**
//     * Take angle input and correct to between -180 and 180 degrees
//     *
//     * @param angle [degrees]
//     * @return corrected angle
//     */
//    private int correctAngle(int angle)
//    {
//        int a = angle;
//        while (a < -180) a += 360;
//        while (a > 180) a -= 360;
//        return a;
//    }
//
//    private int[] quantizePosition(int x, int y){
//        int[] r = {0,0};
//        r[X] = (x-1) / POSITION_QUANTA;
//        r[Y] = (y-1) / POSITION_QUANTA;
//        return r;
//    }
//
//    /**
//     * Quantize the enemy distance into three values
//     * @param x enemy distance as read in with RoboCode
//     * @return
//     */
//    private int quantizeEnemyDistance(int x){
//        if(x < 250) return 0;
//        if(x < 500) return 1;
//        return 2;
//    }
//
//
//    /**
//     * Quantgize energy value into 3 bins
//     * @param x energy level read by robocode
//     * @return
//     */
//    private int quantizeEnergy(int x){
//        if (x > 70) return 2;
//        else if (x > 30) return 1;
//        else return 0;
//    }
//
//    /**
//     * Quantize heading
//     * @param x -> 0 = N; 1 = E; 2 = S; 3 = W
//     * @return
//     */
//    private int quantizeHeading(int x){
//        //Angle conditioned to 0 to 360 degrees
//        x += HEADING_QUANTA_OFFSET;
//        if(x >= 360) x-= 360;
//        if(x < 0) x+= 360;
//        return x / HEADING_QUANTA;
//    }
//
//
///*______               _     _    _                 _ _
// |  ____|             | |   | |  | |               | | |
// | |____   _____ _ __ | |_  | |__| | __ _ _ __   __| | | ___ _ __ ___
// |  __\ \ / / _ \ '_ \| __| |  __  |/ _` | '_ \ / _` | |/ _ \ '__/ __|
// | |___\ V /  __/ | | | |_  | |  | | (_| | | | | (_| | |  __/ |  \__ \
// |______\_/ \___|_| |_|\__| |_|  |_|\__,_|_| |_|\__,_|_|\___|_|  |___/  */
//
//
//    public void onBattleEnded(BattleEndedEvent event)
//    {
//
//        System.out.println("+++BATTLE ENDED");
//        rlTrainer.saveLUT(LUTfile);
//
//        if(roundCounter % 100 == 0 && roundCounter > 0) {
//            try {
//                logWins();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            roundCounter = 0;
//            winCounter = 0;
//        }
//    }
//
//    public void onBulletHit(BulletHitEvent event)
//    {
//        if (countIntermediateRewards)
//            currentReward += INTERMEDIATE_REWARD;
//    }
//
//    public void onHitByBullet(HitByBulletEvent event)
//    {
//        if (countIntermediateRewards)
//            currentReward -= INTERMEDIATE_REWARD;
//    }
//
//    @Override
//    public void onHitWall(HitWallEvent event) {
//        if (countIntermediateRewards)
//            currentReward -= INTERMEDIATE_REWARD;
//    }
//
//    @Override
//    public void onHitRobot(HitRobotEvent event) {
//        if (countIntermediateRewards)
//            currentReward += INTERMEDIATE_REWARD;
//    }
//
//    public void onDeath(DeathEvent event)
//    {
//        System.out.println("+++YOU DIED!");
//        //Subtract terminal reward and learn
//        currentReward -= TERMINAL_REWARD;
//        rlTrainer.learn(stateVector, policy, currentReward);
//        currentReward = 0;
//    }
//
//    public void onWin(WinEvent event)
//    {
//        System.out.println("+++GREAT SUCCESS!");
//        //TODO:
//        // Record our number of wins for every 100 rounds
//        //mNumWinArray[(getRoundNum() - 1) / 100]++;
//
//        //Add terminal reward and learn
//        winCounter++;
//        currentReward  += TERMINAL_REWARD;
//        rlTrainer.learn(stateVector, policy, currentReward);
//        currentReward = 0;
//    }
//
//
///*_____       _           _                  _   _
// |  __ \     | |         | |       /\       | | (_)
// | |__) |___ | |__   ___ | |_     /  \   ___| |_ _  ___  _ __  ___
// |  _  // _ \| '_ \ / _ \| __|   / /\ \ / __| __| |/ _ \| '_ \/ __|
// | | \ \ (_) | |_) | (_) | |_   / ____ \ (__| |_| | (_) | | | \__ \
// |_|  \_\___/|_.__/ \___/ \__| /_/    \_\___|\__|_|\___/|_| |_|___/  */
//
//    /**
//     * Make robot face due north.
//     */
//    private void calibrateRobotHeading() {
//
//        setTurnLeft((int) getHeading());
//        execute();
//        waitFor(turnComplete);
//    }
//
//    /**
//     * moveCircle: drive in a circle with a given radius
//     */
//    public void moveCircle(double radius) {
//        setTurnRight(90);
//        setAhead(Math.PI * radius / 2);
//        execute();
//    }
//
//    public void moveCircleCW(double radius) {
//        setTurnRight(90);
//        setAhead(Math.PI * radius / 2);
//        execute();
//    }
//
//    public void moveCircleCCW(double radius) {
//        setTurnLeft(90);
//        setAhead(Math.PI * radius / 2);
//        execute();
//    }
//
//
//    public void retreat(){
//        if(enemyBearing > 0) {
//            setTurnLeft(correctAngle(180 - enemyBearing));
//        }
//        else {
//            setTurnRight(correctAngle(180+enemyBearing));
//        }
//        setAhead(MOVEMENT_DISTANCE);
//        execute();
//    }
//
//    public void advance(){
//        if(enemyBearing > 0)
//            setTurnRight(enemyBearing);
//        else setTurnLeft(enemyBearing);
//        setAhead(MOVEMENT_DISTANCE);
//
//    }
//
//    public void aimAtEnemy(){
//        int gunBearingtoEnemy = correctAngle(myRobotHeading + enemyBearing - myRobotGunHeading);
////        System.out.println("Enemy Bearing: " + enemyBearing + "  Gun Heading: " + myRobotGunHeading +
////                "  gunBearingtoEnemy: " + gunBearingtoEnemy + "\n\tEnemy Bearing from Gun: " + enemyBearingFromGun);
//        setTurnGunRight(gunBearingtoEnemy);
//    }
//
//    /**
//     * smartFire:  Custom fire method that determines firepower based on distance.
//     *
//     * @param robotDistance the distance to the robot to fire at (quantized)
//     */
//    public void smartFire(double robotDistance) {
//        fire(robotDistance+1);
//    }
//
///* _____ _        _               _      _                          _
//  / ____| |      | |             | |    | |                        | |
// | (___ | |_ __ _| |_ ___      __| | ___| |__  _   _  __ _      ___| |_ ___
//  \___ \| __/ _` | __/ __|    / _` |/ _ \ '_ \| | | |/ _` |    / _ \ __/ __|
//  ____) | || (_| | |_\__ \_  | (_| |  __/ |_) | |_| | (_| |_  |  __/ || (__ _ _ _
// |_____/ \__\__,_|\__|___( )  \__,_|\___|_.__/ \__,_|\__, ( )  \___|\__\___(_|_|_)
//                         |/                           __/ |/
//                                                     |___/       */
//
//
//    private void soutRobocodeDebug(String s){
//        if(debugFlag == true) System.out.println(s);
//    }
//
//
//    /**
//     * Write CSV of win counts every 100 rounds
//     */
//    public void logWins() throws IOException, IOException{
//        try
//        {
//            System.out.println("+++Saving Win Stats...   " + winCounter + " battles won\n");
//            FileOutputStream outFile = new FileOutputStream(statsfile, true);
//            PrintStream out = new PrintStream(outFile);
//            String s = String.format("%f,", (double) winCounter);
//            out.print(s);
//            out.flush();
//            out.close();
//        }
//        catch (IOException exception)
//        {
//            exception.printStackTrace();
//        }
//    }
//}


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

import robocode.*;
import Enum.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;


public class RobocodeRLbot extends AdvancedRobot {

    //Flag for debug output
    private boolean debugFlag = false;

/* _____                _              _
  / ____|              | |            | |
 | |     ___  _ __  ___| |_ __ _ _ __ | |_ ___
 | |    / _ \| '_ \/ __| __/ _` | '_ \| __/ __|
 | |___| (_) | | | \__ \ || (_| | | | | |_\__ \
  \_____\___/|_| |_|___/\__\__,_|_| |_|\__|___/ */

    //ROBOCODE Settings
    private final static int HEADING_QUANTA = 90;        //Quanta step size for heading
    private final static int HEADING_QUANTA_OFFSET = 45; //Quanta step size for heading
    private final static int POSITION_QUANTA = 100;      //Quanta step size for position
    private final static int MOVEMENT_DISTANCE = 100;    //Distance moved each turn


    //File handling
    static private File LUTfile;
    static private File statsfile;
    private final static String LUT_FILENAME = "robocode_lut_file.csv";
    private final static String STATS_FILENAME = "robocode_stats_file.csv";


    //RL SETTINGS
    private final static double LEARNING_RATE = 0.9;
    private final static double DISCOUNT_RATE = 0.9;
    private final static double EXPLORATION = 0.5;
    private final static int BOOTSTRAP_LENGTH = 1;
    //--------------------
    private final static boolean countIntermediateRewards = true;
    private final static double TERMINAL_REWARD = 50;
    private final static double INTERMEDIATE_REWARD = 10;
    private final static int ACTION_COUNT = Actions.getSize();
    //State Settings
    //States:  self_x(8), self_y(6), enemy_x(3), enemy_y(3), myenergy(3), enemyEnergyGainLose(3), enemyDistance(3)
    private final static int[] STATE_COUNTS = {8,6,3,3,3,3,3};

    //Action Settings
    //Action Hash to Integer Table
    //----Bits--------Action---------Description-----------------
    //    00-02       Turn          -180 + 45 * TurnValue(0-8)
    //    03-04       Move          Distance:   0 - no movement
    //                                          1 - move away 50% enemy distance
    //                                          2 - move towards 25% enemy distance
    //                                          3 - move towards 50% enemy distance
    //    05-06      Fire          Firepower = 0-(no fire), 1, 2, 3
    //    07         Aim @ Enemy   Turn gun towards enemy
    //    TOTAL ACTION COMBINATIONS: 2^8 = 256.
//    private final static int ACTION_COUNT = 256;
    //Sequential Shifts, processed in order
//    private final static int ACTION_SHIFT_MOVE = 3;
//    private final static int ACTION_SHIFT_FIRE = 2;
//    private final static int ACTION_SHIFT_AIM  = 2;
//    private final static int ACTION_MOD_TURN = 0x0008;
//    private final static int ACTION_MOD_MOVE = 0x0004;
//    private final static int ACTION_MOD_FIRE = 0x0004;
//    private final static int ACTION_MOD_AIM  = 0x0002;

    //Generic Constants
    private final static int X = 0; // x index for position vector
    private final static int Y = 1; // y index for position vector


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
    private int[] myRobotPosition = new int[2]; //{x, y}
    private int myRobotHeading;
    private int myRobotHeadingQuantized;
    private int myRobotGunHeading;
    private int myRobotGunBearing;
    private int myRobotEnergy;
    private int myRobotPreviousEnergy = 100;

    // Variables to track the state of enemy robot
    private int[] enemyPosition = new int[2];
    private int[] enemyPositionRelative = new int[2]; //X: 0-same,1-left,2-right  //Y: 0-same,1-below,2-above

    private int enemyDistance;
    private int enemyHeading;
    private int enemyHeadingQuantized;
    private int enemyBearing;
    private int enemyBearingFromGun;
    private int enemyEnergy;
    private int enemyAngle;             //angle between myRobot heading and enemy
    private int enemyEnergyChange;       //0 none, 1 lose, 2 gain.
    private int prevEnemyEnergy;

    //Reinforcement Learning Variables
    static private ReinforementLearning rlTrainer = new ReinforementLearning(
            STATE_COUNTS, ACTION_COUNT, LEARNING_RATE, DISCOUNT_RATE, EXPLORATION, BOOTSTRAP_LENGTH);
    static private Actions action = Actions.none;                 //current action to be taken
    //static private int action = 0;

    static private LearningPolicy policy = LearningPolicy.Q;      //default to Q learning
    static private double currentReward = 0;
    static private double[] stateVector = new double[STATE_COUNTS.length];

    static private int winCounter = 0;
    static private int roundCounter = 0;
    static boolean explorationToggle = true;

    static double currentExplorationRate = EXPLORATION;
    static private long[] actionCounter = {0,0,0,0,0,0};
    static private long[] rewardCounter = {0,0,0,0};


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
        LUTfile = getDataFile(LUT_FILENAME);
        statsfile = getDataFile(STATS_FILENAME);
        // If the current file is not empty, load to LUT
        if (rlTrainer.getTrainingCounter() == 0) {
            System.out.print("No battles have been trained: ");
            try {
                if (LUTfile.length() == 0) {
                    System.out.println("Create New LUT File...");
                }
                else
                    rlTrainer.loadLUT(LUTfile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else soutRobocodeDebug(rlTrainer.getTrainingCounter() + " battles have been trained!");
        if(policy == LearningPolicy.Q || policy == LearningPolicy.SARSA) rlTrainer.incTrainingCounter();

        //Count wins per 100 rounds.

        if(roundCounter % 100 == 0 && roundCounter > 0) {
            if(explorationToggle == true){
                explorationToggle = false;
                rlTrainer.setExplorationRate(0);
            }
            else{
                explorationToggle = true;
                rlTrainer.setExplorationRate(currentExplorationRate);
                try {
                    logWins();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            roundCounter = 0;
            winCounter = 0;
            zeroActionCounter();
            zeroRewardCounter();
        }

//        if(roundCounter % 4000 == 0 && roundCounter > 0 && currentExplorationRate > 0){
//            currentExplorationRate -= 0.1;
//        }

        myRobotPreviousEnergy = 100;


        //===================================================================================================//
//        if(roundCounter == 5000){
//            System.out.println("reducing exploration to 0%");
//            rlTrainer.setExplorationRate(0);
//        }
        //====================================================================================================//


        roundCounter++;
        //Initialize first and next state at beginning of the round
        soutRobocodeDebug("Initialize state action vector in RL object");
        rlTrainer.stateActionInit();

        calibrateRobotHeading();
        //Game Loop - spin radar constantly
        while(true)
            turnRadarRight(20);
    }


    @Override
    public void onScannedRobot(ScannedRobotEvent event) {

        stateVector = getStateVector(event);
        if (countIntermediateRewards) {
            //currentReward += (myRobotEnergy - myRobotPreviousEnergy)/4; /////////////////////////////////
            myRobotPreviousEnergy = myRobotEnergy;
        }
        //Reinforced Learn. Also sets next action.
        this.action = rlTrainer.learn(stateVector, policy, currentReward);
        takeAction();
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

        myRobotPosition = quantizePosition((int) getX(), (int) getY());
        myRobotEnergy = quantizeEnergy((int) getEnergy());

        myRobotHeading = (int) getHeading();
        myRobotHeadingQuantized = quantizeHeading(myRobotHeading);
        myRobotGunHeading = (int) getGunHeading();
        myRobotGunBearing = correctAngle(myRobotHeading - myRobotGunHeading);

        // Enemy Robot
        enemyAngle = correctAngle((int) getHeading() + (int) event.getBearing());
        enemyPosition = quantizePosition(
                (int) (getX() + event.getDistance()*Math.sin(Math.toRadians(enemyAngle))),
                (int) (getY() + event.getDistance()*Math.cos(Math.toRadians(enemyAngle)))
        );

        //Set relative enemy LR positions
        if(enemyPosition[X] < myRobotPosition[X]) enemyPositionRelative[X] = 1;
        else if(enemyPosition[X] > myRobotPosition[X]) enemyPositionRelative[X] = 2;
        else enemyPositionRelative[X] = 0;
        if(enemyPosition[Y] < myRobotPosition[Y]) enemyPositionRelative[Y] = 1;
        else if(enemyPosition[Y] > myRobotPosition[Y]) enemyPositionRelative[Y] = 2;
        else enemyPositionRelative[Y] = 0;

        //DEBUG
//        switch(enemyPositionRelative[X]){
//            case 0:
//                System.out.println("Enemy at equal X");
//                break;
//            case 1:
//                System.out.println("Enemy to the left");
//                break;
//            case 2:
//                System.out.println("Enemy to the Right");
//        }
//
//        switch (enemyPositionRelative[Y]){
//            case 0:
//                System.out.println("Enemy at equal Y");
//                break;
//            case 1:
//                System.out.println("Enemy is below ");
//                break;
//            case 2:
//                System.out.println("Enemy is above");
//                break;
//        }



        //Quantize enemyenergy chnge TODO: Move to it's own method
        enemyEnergy = (int) event.getEnergy();
        if(enemyEnergy > prevEnemyEnergy) enemyEnergyChange = 2;
        else if(enemyEnergy < prevEnemyEnergy) enemyEnergyChange = 1;
        else enemyEnergyChange = 0;
        prevEnemyEnergy = enemyEnergy;


        enemyDistance = quantizeEnemyDistance((int) event.getDistance());
        enemyHeading = (int) event.getHeading();
        enemyHeadingQuantized = quantizeHeading(enemyHeading);
        enemyBearing = (int) event.getBearing();
        enemyBearingFromGun = correctAngle(myRobotGunBearing + enemyBearing);

//        soutRobocodeDebug("Setting states\n\tmyPos: (" + myRobotPosition[X] + "," + myRobotPosition[Y] + ")" +
//                "     theirPos: (" + enemyPositionRelative[X] + ","+ + enemyPositionRelative[Y] + ") +" +
//                "\n\tmyEnergy: " + myRobotEnergy + "      enemyEnergy: " + enemyEnergyChange+
//                "\n\tenemydistance: " + enemyDistance);
        return new double[] { myRobotPosition[X], myRobotPosition[Y], enemyPositionRelative[X], enemyPositionRelative[Y],
                myRobotEnergy, enemyEnergyChange, enemyDistance};
    }


    public void takeAction(){
//        soutRobocodeDebug("Taking Action: " + action + " with exploration rate: " + rlTrainer.getExplorationRate());

        //Aim first in case fire action is chosen
        aimAtEnemy();
        waitFor(turnGunComplete);

        switch (action){
            case moveUp:
                actionCounter[0]++;
//                System.out.println("Moving Up   " + myRobotHeading);
                if (myRobotHeading < 180)
                    turnLeft(myRobotHeading);
                else
                    turnRight(360-myRobotHeading);
                ahead(MOVEMENT_DISTANCE);
                break;
            case moveDown:
                actionCounter[1]++;
//                System.out.println("Moving Down   " + myRobotHeading);
                if(myRobotHeading > 180)
                    turnLeft(myRobotHeading-180);
                else
                    turnRight(180-myRobotHeading);
                ahead(MOVEMENT_DISTANCE);
                break;
            case moveRight:
                actionCounter[3]++;
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
                actionCounter[2]++;
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
                actionCounter[4]++;
                smartFire(enemyDistance);
                break;
            case none:
                actionCounter[5]++;
                break;
        }
        waitFor(turnComplete);
        waitFor(moveComplete);


        //deprecated code from hashing version
        /*
        int turn = 0;
        int move = 0;
        int shoot = 0;
        int aim = 0;
        //Action Hash to Integer Table
        //----Bits--------Action---------Description-----------------
        //    00-02       Turn          -180 + 45 * TurnValue(0-8)
        //    03-05       Move          Distance:   0,4 - no movement
        //                                          1-3 = reverse 33%*n enemy distance
        //                                          5-7 = forward 33%*(n-4) enemy distance
        //    06-07       Fire          Firepower = 0-(no fire), 1, 2, 3
        //    08          Aim @ Enemy   Turn gun towards enemy
        //    TOTAL ACTION COMBINATIONS: 2^9 = 512.
        //Parse Action Hash
        int parsedAction = this.action;
        if(parsedAction % ACTION_MOD_TURN > 0){
            turn = parsedAction % ACTION_MOD_TURN;
        }
        parsedAction  >>= ACTION_SHIFT_MOVE;
        if(parsedAction  % ACTION_MOD_MOVE > 0){
            move = parsedAction % ACTION_MOD_MOVE;
        }
        parsedAction  >>= ACTION_SHIFT_FIRE;
        if (parsedAction % ACTION_MOD_FIRE > 0) {
            shoot = parsedAction % ACTION_MOD_FIRE;
        }
        parsedAction  >>= ACTION_SHIFT_AIM;
        if (parsedAction % ACTION_MOD_AIM > 0) {
            aim = parsedAction;
        }
        soutRobocodeDebug("Value: " + action + "  Turn: " + turn + "  Move: " + move + "  Fire: " + shoot + "  Aim:" + aim);
        if(turn > 0){
            setTurnRight( (int) (180 - 22.5*turn) );
        }
        if(move > 0){
            switch(move){
                case 1:
                    setAhead( (int) (-enemyDistance*POSITION_QUANTA*0.5));
                    break;
                case 2:
                    setAhead( (int) (enemyDistance*POSITION_QUANTA*0.25));
                    break;
                case 3:
                    setAhead( (int) (enemyDistance*POSITION_QUANTA*0.5));
                    break;
            }
        }
        execute();
        waitFor(turnComplete);
        waitFor(moveComplete);
        if(aim == 1) {
            aimAtEnemy();
        }
        //Execute set actions
        execute();
        waitFor(turnGunComplete);
        if(shoot > 0){
            fire(shoot);
        }*/
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
                System.out.println("Clearing Lookup Table");
                rlTrainer.initializeLUT();
                winCounter = 0;
                roundCounter = 0;
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


/*______               _     _    _                 _ _
 |  ____|             | |   | |  | |               | | |
 | |____   _____ _ __ | |_  | |__| | __ _ _ __   __| | | ___ _ __ ___
 |  __\ \ / / _ \ '_ \| __| |  __  |/ _` | '_ \ / _` | |/ _ \ '__/ __|
 | |___\ V /  __/ | | | |_  | |  | | (_| | | | | (_| | |  __/ |  \__ \
 |______\_/ \___|_| |_|\__| |_|  |_|\__,_|_| |_|\__,_|_|\___|_|  |___/  */

    @Override
    public void onRoundEnded(RoundEndedEvent event) {
        System.out.println("Action counts.  U: " + actionCounter[0] + "   D: " + actionCounter[1] + "   L: " + actionCounter[2]
                + "   R: " + actionCounter[3] + "   F: " + actionCounter[4] + "   N: " + actionCounter[5]);
        System.out.println("Reward counts. iHit+: " + rewardCounter[0] + "  enHit-: " + rewardCounter[1] + "  wall: " +
                rewardCounter[2] + "  bump: " + rewardCounter[3]);
    }

    public void onBattleEnded(BattleEndedEvent event)
    {

        System.out.println("+++BATTLE ENDED");
        rlTrainer.saveLUT(LUTfile);

        if(roundCounter % 100 == 0 && roundCounter > 0) {
            try {
                logWins();
            } catch (IOException e) {
                e.printStackTrace();
            }
            roundCounter = 0;
            winCounter = 0;
        }
    }

    public void onBulletHit(BulletHitEvent event)
    {
        soutRobocodeDebug("---Hit by Bullet");
        rewardCounter[0]++;
        if (countIntermediateRewards)
            currentReward += INTERMEDIATE_REWARD;
    }

    public void onHitByBullet(HitByBulletEvent event)
    {
        soutRobocodeDebug("+++Hit Enemy");
        rewardCounter[1]++;
        if (countIntermediateRewards)
            currentReward -= INTERMEDIATE_REWARD;
    }

    @Override
    public void onHitWall(HitWallEvent event) {
        soutRobocodeDebug("---Hit Wall");
        rewardCounter[2]++;
        if (countIntermediateRewards)
            currentReward -= INTERMEDIATE_REWARD;
    }

    @Override
    public void onHitRobot(HitRobotEvent event) {
        rewardCounter[3]++;
        if (countIntermediateRewards){
            if(event.isMyFault()){
                currentReward += INTERMEDIATE_REWARD;
                soutRobocodeDebug("+++Bump Enemy");
            }
            else{
                currentReward -= INTERMEDIATE_REWARD;
                soutRobocodeDebug("---Bumped by enemy/4");
            }
        }

    }

    public void onDeath(DeathEvent event)
    {
        System.out.println("+++YOU DIED!");
        //Subtract terminal reward and learn
        currentReward -= TERMINAL_REWARD;
        rlTrainer.learn(stateVector, policy, currentReward);
        currentReward = 0;
    }

    public void onWin(WinEvent event)
    {
        System.out.println("+++GREAT SUCCESS!");
        //TODO:
        // Record our number of wins for every 100 rounds
        //mNumWinArray[(getRoundNum() - 1) / 100]++;

        //Add terminal reward and learn
        winCounter++;
        currentReward  += TERMINAL_REWARD;
        rlTrainer.learn(stateVector, policy, currentReward);
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
     * @param robotDistance the distance to the robot to fire at (quantized)
     */
    public void smartFire(double robotDistance) {

        fire(3 - robotDistance);
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
            String s = String.format("%f,", (double) winCounter);
            out.print(s);
            out.flush();
            out.close();
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }
    }

    private void zeroActionCounter(){
        for( int i = 0; i < 6; i++){
            this.actionCounter[i] = 0;
        }
    }

    private void zeroRewardCounter(){
        for( int i = 0; i < 4; i++){
            this.rewardCounter[i] = 0;
        }
    }
}