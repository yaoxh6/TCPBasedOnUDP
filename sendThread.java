import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.rmi.server.ExportException;

public class sendThread extends Thread  {

    private static final String SEND_FILE_PATH = "2018.txt";

    DatagramSocket dsk = null;
    RandomAccessFile accessFile = null;
    byte[] buf = new byte[UDPUtils.BUFFER_SIZE];
    byte[] Buf = new byte[UDPUtils.BUFFER_SIZE+6];
    int readSize = -1;
    extendQueue clientQueue = null;
    RetransThread rt;
    Thread t3;
    boolean firstTime = true;

    public sendThread(DatagramSocket dsk,extendQueue clientQueue){
        this.dsk = dsk;
        this.clientQueue = clientQueue;
        rt = new RetransThread(System.currentTimeMillis(),System.currentTimeMillis(),null,clientQueue,dsk);
        t3 = new Thread(rt);
        t3.start();
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    @Override
    public void run() {
//        if(firstTime){
//            firstTime = false;
//            t3.start();
//        }
        try {
            accessFile = new RandomAccessFile(SEND_FILE_PATH, "r");
            DatagramPacket dpk = new DatagramPacket(Buf, Buf.length, new InetSocketAddress(InetAddress.getByName("localhost"), UDPUtils.PORT + 1));
            int sendCount = 0;
            while ((readSize = accessFile.read(buf, 0, buf.length)) != -1) {
                ReliablePacket packet = new ReliablePacket((byte)sendCount, (byte)0, (byte)0, buf);
                dpk.setData(packet.getBuf(), 0, readSize+6);
                System.out.println("packet checkSum : "+packet.getCheckSum());
                synchronized (clientQueue){
                    dsk.send(dpk);
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
