package LearningAlgorithms;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.*;

public class TrainNN {
    private final static int NON_CONVERGENCE_LIMIT = 1000; //

    //State Settings
    //States:  self_x(8), self_y(6), enemy_x(3), enemy_y(3), myenergy(3), enemyEnergyGainLose(3), enemyDistance(3)
    private final static int[] STATE_COUNTS = {8,6,3,3,3,3,3};
    private final static int ACTIVATION_MODE = 2; // 1 = sigmoid; 2 = bipolar sigmoid
    private final static int ACTION_COUNT = 6;
    private final static int NN_INPUT_COUNT = 5 + STATE_COUNTS.length;


    //File handling
    static private File LUTfile;
    private final static String LUT_FILENAME = "./Part3/lut.csv";
    static private File NNfile;
    private static int trainingCounter;
    private static int trainingIterations;

    private File errorLog;
    private static boolean stop;
    KeyListener key;
    NeuralNet NN;
    LUT lut;

    double Qscaler = 0.0;

    JFrame frame;
    Container contentPane;

    public TrainNN(String fileName, int neurons, double learningRate, double momentum, int iterations) throws IOException {

        int[] ll = {neurons, 1};

        NN = new NeuralNet(ACTIVATION_MODE, NN_INPUT_COUNT, ll, learningRate, momentum, -1, 1);
        lut = new LUT(STATE_COUNTS, ACTION_COUNT);

        Date date = new Date();
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        //String s = String.format("n%d-lr%.3f-p%.3f", HIDDEN_NEURONS, LEARNING_RATE, MOMENTUM);
        //String fileName = "./RobocodeLogs/RMSError_" + "_" + s + "_" + time.format(date) + ".log";
        errorLog = new File(fileName);
        fileName = "./Part3/NN_file_" + "_" + "_" + time.format(date) + ".csv";
        NNfile = new File(fileName);  //This will be re-written
        LUTfile = new File(LUT_FILENAME);

        //States:  self_x(8), self_y(6), enemy_x(3), enemy_y(3), myenergy(3), enemyEnergyGainLose(3), enemyDistance(3)
        this.trainingIterations = iterations;

        lut.load(LUTfile);
        NN.setTrainingCounter(lut.getTrainingCounter());
        NN.initializeWeights();

        //find maximum LUT Q value.
        this.Qscaler = lut.getMaxQ();
        System.out.println("Scaling Q values by " + this.Qscaler);

        this.trainingCounter = 0;



        //BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

        //Key Listener
        frame = new JFrame("Key Listener");
        contentPane = frame.getContentPane();
        key = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode())
                {
                    case KeyEvent.VK_P:
                        System.out.println("\n" + "Training ended by user");
                        stop = true;
                        break;
                    case KeyEvent.VK_Q:
                        System.out.println("\n"+trainingCounter+" iterations trained...");
                        break;
                    default:
                        break;
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        };
        JTextField textField = new JTextField();

        textField.addKeyListener(key);

        contentPane.add(textField, BorderLayout.NORTH);

        frame.pack();

        frame.setVisible(true);
        stop = false;
    }

    /**
     * Train one iteration through the lookup table
     */
    public double trainNN(){
        int[] s = STATE_COUNTS;
        int a = ACTION_COUNT;
        int testCounter = 0;
        double error = 0;
        double[] ss = {0,0,0,0,0,0,0,0,0,0,0,0};
        //System.out.println(s[0] + ", " + s[1] + ", " + s[2] + ", " + s[3] + ", " + s[4] + ", " + s[5] + ", " + s[6] + ", " + a);
        for(int i = 0; i < s[0]; i++){
            for(int j = 0; j < s[1]; j++){
                for(int k = 0; k < s[2]; k++){

                    for(int l = 0; l < s[3]; l++) {
                        for (int m = 0; m < s[4]; m++) {
                            for (int n = 0; n < s[5]; n++) {
                                for (int o = 0; o < s[6]; o++) {
                                    for (int p = 0; p < a; p++) {

                                        //Get LUT Data
                                        double inputSet[] = {i, j, k, l, m, n, o, p};
                                        double output = lut.outputFor(inputSet);
                                        double outputScaled = output / this.Qscaler;

                                        //Scale Data for NN // sa = scaled actions
                                        //double[] sa = {0, 0, -1}; //{xmove, ymove, fire}
                                        ss[7] = -1;
                                        ss[8] = -1;
                                        ss[9] = -1;
                                        ss[10] = -1;
                                        ss[11] = -1;

                                        switch (p){
                                            case 0: //Up
                                                ss[7] = 1;
                                                break;
                                            case 1: //Down
                                                ss[8] = 1;
                                                break;
                                            case 2: //Left
                                                ss[9] = 1;
                                                break;
                                            case 3: //Right
                                                ss[10] = 1;
                                                break;
                                            case 4: //Fire
                                                ss[11] = 1;
                                                break;
                                            case 5: //None
                                                ss[7] = 0;
                                                ss[8] = 0;
                                                ss[9] = 0;
                                                ss[10] = 0;
                                                ss[11] = 0;
                                                break;
                                            default:
                                                throw new IllegalStateException("Unexpected value: " + p);

                                        }

                                        //scale states
                                        ss[0] = 0;
                                        ss[1] = 0;
                                        ss[2] = 0;
                                        ss[3] = 0;
                                        ss[4] = 0;
                                        ss[5] = 0;
                                        ss[6] = 0;

                                        ss[0] = ((double) (i) + 0.5) / 4.0 - 1.0;
                                        ss[1] = ((double) (j) + 0.5) / 3.0 - 1.0;

                                        switch(k){  //enemy x, left or right
                                            case 0:
                                                ss[2] = 0.0;
                                                break;
                                            case 1:
                                                ss[2] = -1.0;
                                                break;
                                            case 2:
                                                ss[2] = 1.0;
                                                break;
                                        }

                                        switch(l){ //enemy y, down or up
                                            case 0:
                                                ss[3] = 0.0;
                                                break;
                                            case 1:
                                                ss[3] = -1.0;
                                                break;
                                            case 2:
                                                ss[3] = 1.0;
                                                break;
                                        }

                                        switch(m){ //my energy
                                            case 0:
                                                ss[4] = 0.2;
                                                break;
                                            case 1:
                                                ss[4] = 0.5;
                                                break;
                                            case 2:
                                                ss[4] = 0.8;
                                                break;
                                        }

                                        switch(n){ //enemy energy, down or up
                                            case 0:
                                                ss[5] = 0.0;
                                                break;
                                            case 1:
                                                ss[5] = -1.0;
                                                break;
                                            case 2:
                                                ss[5] = 1.0;
                                                break;
                                        }

                                        switch(o){ //enemy distance  (distance/1000)
                                            case 0:
                                                ss[6] = 0.125;
                                                break;
                                            case 1:
                                                ss[6] = 0.375;
                                                break;
                                            case 2:
                                                ss[6] = 0.75;
                                                break;
                                        }

                                        //double[] inputSetScaled = {ss[0], ss[1], ss[2], ss[3], ss[4], ss[5], ss[6], sa[0], sa[1], sa[2]};
                                        double thisError = Math.pow(NN.train(ss, outputScaled) - outputScaled,2);
                                        error += thisError;
                                        testCounter++;

                                        //System.out.println(String.format("States: %d, %d, %d, %d, %d, %d, %d, %d  Error: %f", i, j, k, l, m, n, o, p, thisError));

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        double RMSerror = Math.sqrt(error/69984);
        if(trainingCounter % 10 == 0){
            System.out.println("");
        }
        trainingCounter++;
        System.out.print(String.format("%4.6f  ",RMSerror));
        return RMSerror;
    }

    /**
     * Iterate the training process until the error threshold has been reached
     * @param error error threshold
     * @return
     */
    public int trainIterator(double error) throws IOException {
        error = Math.abs(error);
        double localError = 1000000000;
        while (localError > error && stop == false && trainingCounter < trainingIterations){
            localError = this.trainNN();

            //Log error after every iteration
            try
            {
                FileOutputStream outFile = new FileOutputStream(errorLog, true);
                PrintStream out = new PrintStream(outFile);
                String s = String.format("%f,", localError);
                out.print(s);
                out.flush();
                out.close();
            }
            catch (IOException exception)
            {
                System.out.println("Failed to log Epoch");
                exception.printStackTrace();
            }

            //writer.append(localError + ","); //For saving number of trials to solve system

            if(this.trainingCounter > NON_CONVERGENCE_LIMIT){
                System.out.println("Failed to converge after " + NON_CONVERGENCE_LIMIT + " attempts");
                return 0;
            }

        }
        if(stop == false) System.out.println("Converged after " + this.trainingCounter + " iterations");
        else System.out.println("\n" + this.trainingCounter + " iterations trained");

        NN.save(NNfile);
        System.out.println("Saved new NN file for Robocode");
        frame.dispose();


        return this.trainingCounter;
    }
}