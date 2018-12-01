import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class sendThread extends Thread  {

    private static final String SEND_FILE_PATH = "2018.flv";

    private DatagramSocket dsk = null;
    RandomAccessFile accessFile = null;
    byte[] buf = new byte[UDPUtils.BUFFER_SIZE];
    int readSize = -1;
    extendQueue clientQueue = null;

    public sendThread(DatagramSocket dsk,extendQueue clientQueue){
        this.dsk = dsk;
        this.clientQueue = clientQueue;
    }
    @Override
    public void run() {
        try {
            accessFile = new RandomAccessFile(SEND_FILE_PATH, "r");
            DatagramPacket dpk = new DatagramPacket(buf, buf.length, new InetSocketAddress(InetAddress.getByName("localhost"), UDPUtils.PORT + 1));
            int sendCount = 0;
            while ((readSize = accessFile.read(buf, 0, buf.length)) != -1) {
                dpk.setData(buf, 0, readSize);
                dsk.send(dpk);
                synchronized (clientQueue){
                    clientQueue.addDatagramPacket(dpk);
                    System.out.println("clientQueueLength:"+clientQueue.getCurrentQueueSize());
                    System.out.println("Send count of " + (++sendCount) + "!");
                }
                //Thread.sleep(10);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
//            try {
//                if(accessFile != null)
//                    accessFile.close();
//                if(dsk != null)
//                    dsk.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }
}
