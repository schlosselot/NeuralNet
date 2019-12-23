package LearningAlgorithms;

public class SARSA {
    private double[] prev;
    private double[] next;
    private double reward;

    public SARSA(double[] prev, double[] next, double reward){
        this.prev = prev;
        this.next = next;
        this.reward = reward;
    }

    public void set(double[] prev, double[] next, double reward){
        this.prev = prev;
        this.next = next;
        this.reward = reward;
    }

    public double[] getPrev(){
        return this.prev;
    }

    public double[] getNext(){
        return this.next;
    }

    public double getReward(){
        return this.reward;
    }


    @Override
    public boolean equals(Object obj) {
        SARSA sarsa = (SARSA) obj;
        if(sarsa.prev == this.prev && sarsa.next == this.next && sarsa.reward == this.reward) return true;
        return false;
    }
}
