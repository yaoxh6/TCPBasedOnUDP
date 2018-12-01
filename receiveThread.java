import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class receiveThread extends Thread  {

    private DatagramSocket dsk = null;
    byte[] receiveBuf = new byte[1];
    extendQueue clientQueue = null;

    public receiveThread(DatagramSocket dsk,extendQueue clientQueue){
        this.dsk = dsk;
        this.clientQueue = clientQueue;
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
                synchronized (clientQueue){
                    if(!clientQueue.isEmpty()){
                        clientQueue.popDatagramPacket();
                        System.out.println("remove" + num);
                    }
                }
                //System.out.println("yes" + num);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
