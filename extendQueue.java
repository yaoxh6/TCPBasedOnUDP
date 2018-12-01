import java.net.DatagramPacket;
import java.util.LinkedList;
import java.util.Queue;

public class extendQueue{
    private int maxQueueSize;
    private int currentQueueSize;
    private LinkedList<DatagramPacket> clientQueue;
    private int sendBase;
    private int nextSeqnum;

    public extendQueue(){
        maxQueueSize = 50;
        currentQueueSize = 0;
        int sendBase = 0;
        int nextSeqnum = 15;
        clientQueue = new LinkedList<DatagramPacket>();
    }

    public void addDatagramPacket(DatagramPacket inputData){
        clientQueue.add(inputData);
        currentQueueSize++;
    }

    public void popDatagramPacket(){
        clientQueue.pollFirst();
        currentQueueSize--;
    }
    public int getMaxQueueSize() {
        return maxQueueSize;
    }

    public int getNextSeqnum() {
        return nextSeqnum;
    }

    public int getSendBase() {
        return sendBase;
    }
    public boolean isCanWindowMove(){
        if(nextSeqnum <= currentQueueSize){
            return true;
        }
        return false;
    }

    public void moveWindow(){
        sendBase++;
        nextSeqnum++;
    }

    public void moveBackWindow(){
        sendBase = 0;
        nextSeqnum = 15;
        currentQueueSize = 0;
    }
    public int getCurrentQueueSize() {
        return currentQueueSize;
    }

    public void setCurrentQueueSize(int currentQueueSize) {
        this.currentQueueSize = currentQueueSize;
    }

    public boolean isEmpty(){
        return clientQueue.isEmpty();
    }
    public boolean isFull(){
        return currentQueueSize == maxQueueSize;
    }

    public DatagramPacket getDataPacket(int pos) {
        return clientQueue.peek();
    }

    public Queue<DatagramPacket> getClientQueue() {
        return clientQueue;
    }
}