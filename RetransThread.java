import java.net.DatagramPacket;

public class RetransThread extends Thread {
    long endTime;
    Time sendTime;
    long totalTime = 2000;
    DatagramPacket datagramPacket;
    extendQueue clientQueue;
    boolean isTimeout;
    public RetransThread(long endTime,Time sendTime,DatagramPacket datagramPacket,extendQueue clientQueue){
        this.endTime = endTime;
        this.sendTime = sendTime;
        this.datagramPacket = datagramPacket;
        this.clientQueue = clientQueue;
        isTimeout = false;
    }
    @Override
    public void run() {
        System.out.println("timer");
        endTime = System.currentTimeMillis();
        long periodTime = endTime - sendTime.startTime;
        System.out.println(sendTime.startTime+" "+endTime);
        if(periodTime > totalTime){
            isTimeout = true;
        }
    }
}
