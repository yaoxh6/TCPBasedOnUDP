import java.net.DatagramPacket;

public class RetransThread extends Thread {
    long endTime;
    long startTime;
    long totalTime = 2000;
    DatagramPacket datagramPacket;
    extendQueue clientQueue;
    boolean isTimeout;
    public RetransThread(long endTime,long startTime,DatagramPacket datagramPacket,extendQueue clientQueue){
        this.endTime = endTime;
        this.startTime = startTime;
        this.datagramPacket = datagramPacket;
        this.clientQueue = clientQueue;
        isTimeout = false;
        System.out.println("TimeKeeper");
    }

    @Override
    public void run() {
        while(true){
            endTime = System.currentTimeMillis();
            long periodTime = endTime - startTime;
            System.out.println(startTime+" "+endTime+" "+periodTime+" "+totalTime);
            if(periodTime >= totalTime){
                isTimeout = true;
                System.out.println("TimeOut!!!");
                ReTimeKeeper(clientQueue.getClientQueue().peek());
            }
        }
    }

    public void ReTimeKeeper(DatagramPacket datagramPacket){
        this.datagramPacket = datagramPacket;
        this.startTime = System.currentTimeMillis();
    }
}
