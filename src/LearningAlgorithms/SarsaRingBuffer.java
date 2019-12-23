package LearningAlgorithms;

import java.util.ArrayList;
import java.util.LinkedList;

public class SarsaRingBuffer {
    int arrayLength, bufferSize, index;
    ArrayList buf;

    public SarsaRingBuffer(int bufferSize){
        this.bufferSize = bufferSize;
        this.index = -1;
        this.buf = new ArrayList<SARSA>();
    }

    public void push(SARSA s){
        advanceIndex();
        if(buf.size() < bufferSize) {
            buf.add(s);
        }
        else{
            buf.set(index, s);
        }
    }

    public SARSA pop(){
        Object d = buf.get(index);
        reverseIndex();
        return (SARSA) d;
    }

    public SARSA peek(int offset){

        if(offset == 0){
            return (SARSA) buf.get(index);
        }
        else{
            int tempIndex = index+offset;
            while(tempIndex < 0  || tempIndex >= buf.size()) {
                if (tempIndex < 0) tempIndex += buf.size();
                else if (tempIndex >= buf.size()) tempIndex -= buf.size();
            }
            return (SARSA) buf.get(tempIndex);
        }
    }

    public void reset(){
        buf.clear();
    }

    private void advanceIndex(){
        this.index++;
        if(this.index >= this.bufferSize){
            this.index = 0;
        }
    }

    private void reverseIndex(){
        this.index--;
        if(this.index < 0){
            this.index = buf.size() - 1;
        }
    }

    public int size(){
        return buf.size();
    }


}


