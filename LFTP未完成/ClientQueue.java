import java.net.DatagramPacket;

public class ClientQueue{
    private int maxQueueSize;
    private int currentQueueSize;
    private DatagramPacket[] clientQueue;
    private int sendBase;
    private int nextSeqnum;

    public ClientQueue(){
        maxQueueSize = 50;
        currentQueueSize = 0;
        clientQueue = new DatagramPacket[maxQueueSize];
        int sendBase = 0;
        int nextSeqnum = 15;
    }

    public void setDatagramPacket(int pos,DatagramPacket inputData){
        clientQueue[pos] = inputData;
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

    public DatagramPacket getDataPacket(int pos) {
        return clientQueue[pos];
    }
}