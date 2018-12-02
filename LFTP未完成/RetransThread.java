
import java.io.EOFException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;

public class RetransThread extends Thread {
    long endTime;
    long startTime;
    long totalTime = 2000;
    DatagramPacket datagramPacket;
    extendQueue clientQueue;
    boolean isTimeout;
    DatagramSocket dsk;
    public RetransThread(long endTime,long startTime,DatagramPacket datagramPacket,extendQueue clientQueue,DatagramSocket dsk){
        this.endTime = endTime;
        this.startTime = startTime;
        this.datagramPacket = datagramPacket;
        this.dsk = dsk;
        this.clientQueue = clientQueue;
        isTimeout = false;
        System.out.println("TimeKeeper");
    }

    @Override
    public void run() {
        while(true){
            endTime = System.currentTimeMillis();
            long periodTime = endTime - startTime;
            //System.out.println(startTime+" "+endTime+" "+periodTime+" "+totalTime);
            if(periodTime >= totalTime && datagramPacket == clientQueue.getClientQueue().peek()){
                isTimeout = true;
                System.out.println("TimeOut!!!!");
                ReTimeKeeper();
                try{
                    Retransmission();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
            else if(periodTime <= totalTime && datagramPacket != clientQueue.getClientQueue().peek()){
                ReTimeKeeper();
            }
        }
    }

    public void ReTimeKeeper(){
        this.isTimeout = false;
        this.datagramPacket = clientQueue.getClientQueue().peek();
        this.startTime = System.currentTimeMillis();
    }

    public void Retransmission() throws IOException {
        dsk.send(datagramPacket);
        //System.out.println("Retransmission");
        ReliablePacket packet = new ReliablePacket(datagramPacket);

        System.out.println("Retransmission " + packet.getSeqNum()+"");
    }
}
