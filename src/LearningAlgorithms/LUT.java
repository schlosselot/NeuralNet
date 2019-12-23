package LearningAlgorithms;


import Interface.LUTInterface;
import robocode.RobocodeFileOutputStream;
import robocode.RobocodeFileWriter;
import robocode.ScannedRobotEvent;

import javax.swing.table.TableStringConverter;
import java.io.*;
import java.util.*;


public class LUT implements LUTInterface {

    //Flag for debug output
    private boolean debugFlag = false;

    private double[][][][][][][][] saLUT;
    private int[] sLengths;       //lengths of state vectors
    private int aLength;          //length of actions vector
    private boolean isEmpty;
    private long trainingCounter;

    /**
     *
     * @param s states array of state groupings (each entry is the number of values within each state)
     * @param a number of actions available
     */
    public LUT(int[] s, int a){
        this.isEmpty = true;
        this.sLengths = s;
        this.aLength = a;
        saLUT = new double[s[0]][s[1]][s[2]][s[3]][s[4]][s[5]][s[6]][a];
        trainingCounter = 0;

        HashMap<double[], Double> LUT = new HashMap<double[], Double>();

    }

    /**
     * Get the index of the action with the highest q value
     * @param X state vector (no action
     * @return
     */
    public int findMaxQIndex(double[] X){
        if(X.length != sLengths.length) throw new IllegalArgumentException(
                "Error: Incorrect number of inputs @ LUT:fineMaxQ. Expected: " + (sLengths.length) + " Actual: " + X.length);
        double max = -100000000;
        ArrayList<Integer> maxList = new ArrayList<Integer>();
        for(int i = 0; i < aLength; i++){
            double q = saLUT[(int) X[0]][(int) X[1]][(int) X[2]][(int) X[3]][(int) X[4]][(int) X[5]][(int) X[6]][i];
            if(q >= max){
                if(q != max) maxList.clear();
                max = q;
                maxList.add(i);
            }
        }
        //Return the value with the largest q value. Otherwise, randomly select from the set of matching largest.
        if(maxList.size() == 1) return maxList.get(0);
        else{
            Random random = new Random();
            soutLUTdebug("Multiple Q's are equal, selecting a random high-Q: " + maxList.size());
            return maxList.get(random.nextInt(maxList.size()));
        }
    };


    /**
     * get index of maximum Q action (the Greedy State)
     * @param X state array (no action)
     * @return value of greedy action
     */
    public double getGreedyActions(double[] X) {
        if(X.length != sLengths.length) throw new IllegalArgumentException(
                "Error: Incorrect number of inputs @ LUT:getGreedyAction. Expected: " + (sLengths.length)
                        + " Actual: " + X.length);
        //System.out.println("Error spot: " + (int) X[0] + " " + (int) X[1] + " " + (int) X[2]);
        return (double) findMaxQIndex(X);
    }


    @Override
    public double outputFor(double[] X) {
        if(X.length != sLengths.length + 1) throw new IllegalArgumentException(
                "Error: Incorrect number of inputs @ LUT:outputFor. Expected: "
                        + (sLengths.length+1) + " Actual: " + X.length);

        return saLUT[(int) X[0]][(int) X[1]][(int) X[2]][(int) X[3]][(int) X[4]][(int) X[5]][(int) X[6]][(int) X[7]];
    }

    /**
     * Calculates Maximum Q (off-policy) reward
     * @param X state action array
     */
    public double outputForOffPolicy(double[] X) {
        if(X.length != sLengths.length + 1) throw new IllegalArgumentException(
                "Error: Incorrect number of inputs @ LUT:outputForGreedy. Expected: " + (sLengths.length+1) + " Actual: " + X.length);

        double[] Xa = {X[0], X[1], X[2], X[3], X[4], X[5], X[6]};
        return saLUT[(int) X[0]][(int) X[1]][(int) X[2]][(int) X[3]][(int) X[4]][(int) X[5]][(int) X[6]][findMaxQIndex(Xa)];
    }

    /**
     *
     * @return random action
     */
    public double getRandomAction(){
        Random random = new Random();
        return (double) random.nextInt(this.aLength);
    }

    public int getActionsIndex(){
        return this.sLengths.length;
    }

    @Override
    public double train(double[] X, double argValue) {
        isEmpty = false;
        if(X.length != sLengths.length + 1) throw new IllegalArgumentException(
                "Error: Incorrect number of inputs @ LUT:train. Expected: "
                + (sLengths.length+1) + " Actual: " + X.length);
        double prevValue =
                saLUT[(int) X[0]][(int) X[1]][(int) X[2]][(int) X[3]][(int) X[4]][(int) X[5]][(int) X[6]][(int) X[7]];
        this.saLUT[(int) X[0]][(int) X[1]][(int) X[2]][(int) X[3]][(int) X[4]][(int) X[5]][(int) X[6]][(int) X[7]]
                = argValue;

        //DEBUG ---------------------
        soutLUTdebug("Trained value @ " + X + ": " +
                saLUT[(int) X[0]][(int) X[1]][(int) X[2]][(int) X[3]][(int) X[4]][(int) X[5]][(int) X[6]][(int) X[7]]);

        return prevValue - argValue;
    }

    /**
     * Write CSV of look up table
     * @param argFile of type File.
     */
    @Override
    public void save(File argFile) {
        try
        {
            System.out.println("+++Saving LUT...\n");

            FileOutputStream outFile = new FileOutputStream(argFile);
            PrintStream out = new PrintStream(outFile);
            out.format("%f", (float) trainingCounter);

            //out.print(trainingCounter);

            int lineCounter = 0;
            for(int i = 0; i < sLengths[0]; i++){
                for(int j = 0; j < sLengths[1]; j++){
                    for(int k = 0; k < sLengths[2]; k++){
                        for(int l = 0; l < sLengths[3]; l++) {
                            for (int m = 0; m < sLengths[4]; m++) {
                                for (int n = 0; n < sLengths[5]; n++) {
                                    for (int o = 0; o < sLengths[6]; o++) {
                                        for (int p = 0; p < aLength; p++) {
                                            lineCounter++;
                                            if (lineCounter % 20 == 0) {
                                                soutLUTdebug(" ");
                                            }
                                            soutLUTdebugNoReturn(String.valueOf(saLUT[i][j][k][l][m][n][o][p]) + ",");
                                            //out.print((float) saLUT[i][j][k][l][m][n][o][p]);
                                            out.format(",%f", (float) saLUT[i][j][k][l][m][n][o][p]);
             }}}}}}}}

            soutLUTdebug("LUT file size is " + argFile.length() + " bytes with " + lineCounter + " entries");
            out.close();
            outFile.close();
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }
    }


    @Override
    public void load(File file) throws IOException, IOException {
        try
        {
            System.out.print("Loading LUT...   ");
            BufferedReader in = new BufferedReader(new FileReader(file));
            String line = in.readLine();
            String[] values = line.split(",");
            //Scanner scanner = new Scanner(file);


            //Test values vector length
            int vLength = this.aLength*this.sLengths[0]*this.sLengths[1]*this.sLengths[2]*this.sLengths[3]
                    *this.sLengths[4]*this.sLengths[5]*this.sLengths[6];

//            if(vLength+1 != values.length) System.out.println("\nInvalid LUT file lengths:\n" +
//                    "\tExpected " + (vLength+1) + " entries, found " + values.length + ".\n" +
//                    "\tCreating empty LUT...");

//            else {
                trainingCounter = (long) Double.parseDouble(values[0]);
                //trainingCounter = (long) scanner.nextLong();
                System.out.println(trainingCounter + " training sessions counted");
                int a = 1;
                for(int i = 0; i < sLengths[0]; i++){
                    for(int j = 0; j < sLengths[1]; j++){
                        for(int k = 0; k < sLengths[2]; k++){
                            for(int l = 0; l < sLengths[3]; l++) {
                                for (int m = 0; m < sLengths[4]; m++) {
                                    for (int n = 0; n < sLengths[5]; n++) {
                                        for (int o = 0; o < sLengths[6]; o++) {
                                            for (int p = 0; p < aLength; p++) {
                                                //System.out.printf(String.valueOf(Double.parseDouble(values[a])) + ", ");
                                                //if(a%10 == 0) System.out.println(" ");
                                                soutLUTdebug("Loading #" + a + "/" + vLength + " saLUT[" + i + "][" + j + "][" + k + "][" + l + "] = " + Double.parseDouble(values[a]));
                                                saLUT[i][j][k][l][m][n][o][p] = Double.parseDouble(values[a]);
                                                //if(!scanner.hasNext()) System.out.println("---Not enough entries in LUT file!");
                                                //saLUT[i][j][k][l][m][n][o][p] = (double) scanner.nextFloat();
                                                a++;
                 }}}}}}}}
                System.out.println("\tLUT loaded OK! Number of training battles: " + trainingCounter + "\n");


            in.close();
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
        }

    }

    /**
     * Find the absolute maximum q value in LUT
     * @return
     */
    public double getMaxQ(){
        double max = 0.0;
        for(int i = 0; i < sLengths[0]; i++){
            for(int j = 0; j < sLengths[1]; j++){
                for(int k = 0; k < sLengths[2]; k++){
                    for(int l = 0; l < sLengths[3]; l++) {
                        for (int m = 0; m < sLengths[4]; m++) {
                            for (int n = 0; n < sLengths[5]; n++) {
                                for (int o = 0; o < sLengths[6]; o++) {
                                    for (int p = 0; p < aLength; p++) {
                                         double q = Math.abs(saLUT[i][j][k][l][m][n][o][p]);
                                         if (q > max) max = q;
                                    }}}}}}}}
        return max;
    }
    @Override
    public void initialiseLUT() {
        trainingCounter = 0;
        System.out.println("+++Initializing LUT...");
        isEmpty = true;
        for(int i = 0; i < sLengths[0]; i++){
            for(int j = 0; j < sLengths[1]; j++){
                for(int k = 0; k < sLengths[2]; k++){
                    for(int l = 0; l < sLengths[3]; l++) {
                        for (int m = 0; m < sLengths[4]; m++) {
                            for (int n = 0; n < sLengths[5]; n++) {
                                for (int o = 0; o < sLengths[6]; o++) {
                                    for (int p = 0; p < aLength; p++) {
                                        double[] idx = {i, j, k, l, m, n, o, p};        //index array for training method
                                        this.train(idx, 0.0);
         }}}}}}}}
    }

    /**
     * Increment the number of time this LUT has been trained
     */
    public void incTrainingCounter(){
        trainingCounter++;
    }

    /**
     * Get the training counter
     * @return trainingCounter
     */
    public long getTrainingCounter() { return this.trainingCounter;}


    @Override
    public int indexFor(double[] X) {
        return 0;
    }

    private void soutLUTdebug(String s){
        if(debugFlag == true) System.out.println(s);
    }

    private void soutLUTdebugNoReturn(String s){
        if(debugFlag == true) System.out.print(s);
    }

}
