import java.net.DatagramPacket;

public class Time {
    DatagramPacket datagramPacket;
    long startTime;
    Time(DatagramPacket datagramPacket){
        this.datagramPacket = datagramPacket;
        this.startTime = System.currentTimeMillis();
    }
}
