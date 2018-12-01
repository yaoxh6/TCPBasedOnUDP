import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class receiveThread implements Runnable  {

    private DatagramSocket dsk = null;
    byte[] receiveBuf = new byte[1];
    receiveThread(DatagramSocket dsk){
        this.dsk = dsk;
    }
    int num = 0;
    @Override
    public void run() {
        while(true){
            num++;
            try{
                DatagramPacket dpk = new DatagramPacket(receiveBuf, receiveBuf.length, new InetSocketAddress(InetAddress.getByName("localhost"), UDPUtils.PORT + 1));
                dpk.setData(receiveBuf, 0, receiveBuf.length);
                if(dsk==null){
                    break;
                }
                    dsk.receive(dpk);
                    System.out.println("yes" + num);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
