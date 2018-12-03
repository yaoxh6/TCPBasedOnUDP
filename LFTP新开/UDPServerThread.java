import java.io.*;
import java.net.*;
import java.util.Arrays;

public class UDPServerThread extends Thread {
    DatagramSocket dsk;
    byte[] buf = new byte[UDPUtils.BUFFER_SIZE+6];
    DatagramPacket dpk = null;
    private String SAVE_FILE_PATH = "2019.txt";
    private InetAddress IPAddress;
    private int IPPort;
    public UDPServerThread(DatagramSocket socket,InetAddress IPAddress,int IPPort) throws IOException {
        this.dsk = socket;
        this.IPAddress = IPAddress;
        this.IPPort = IPPort;
        dpk = new DatagramPacket(buf, buf.length,new InetSocketAddress(IPAddress, IPPort));
        System.out.println("Send second SYN from server");
        dpk.setData(UDPUtils.connectServer,0,UDPUtils.connectServer.length);
        dsk.send(dpk);
    }

    public void run() {
        try {
            byte[] tempReceive = new byte[100];
            dpk.setData(tempReceive,0,tempReceive.length);
            dsk.receive(dpk);
            if(UDPUtils.isEqualsByteArray(UDPUtils.upload, tempReceive, dpk.getLength())){
                System.out.println("Uploading to server.");
                dsk.receive(dpk);
                System.out.println("Receive the name of the file: " + new String(dpk.getData()).trim());
                SAVE_FILE_PATH = new String(dpk.getData()).trim();
                DownLoad(dsk);
            }
            else if(UDPUtils.isEqualsByteArray(UDPUtils.download, tempReceive, dpk.getLength())){
                System.out.println("Downloading from server.");
                dsk.receive(dpk);
                System.out.println("Receive the name of the file: " + new String(dpk.getData()).trim());
                SAVE_FILE_PATH = new String(dpk.getData()).trim();
                File file = new File(SAVE_FILE_PATH);
                if(!file.exists()){
                    System.out.println("File is not Exist");
                    dpk.setData(UDPUtils.fileNotExist);
                    dsk.send(dpk);
                    return;
                }
                UpLoad(dsk);
            }
            else{
                System.out.println("Error.");
                return;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void DownLoad(DatagramSocket dsk){
        try {

            byte[] buf = new byte[UDPUtils.BUFFER_SIZE+6];
            dpk.setData(buf,0,buf.length);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(SAVE_FILE_PATH));
            dsk.receive(dpk);

            int readSize = 0;
            int readCount = 1;
            int flushSize = 0;

            int flag = 0;
            int first = 0;
            while((readSize = dpk.getLength()) != 0){
            	if(first==0&&UDPUtils.isEqualsByteArray(UDPUtils.fileNotExist, dpk.getData(), dpk.getLength())){
					System.out.println("File is not Exist");
					first=1;
					break;
				}
                if(UDPUtils.isEqualsByteArray(UDPUtils.end,buf,dpk.getLength())){
                    byte[] a = new byte[1];
                    a[0] = 1;
                    dpk.setData(a, 0, 1);
                    dsk.send(dpk);
                    System.out.println(SAVE_FILE_PATH + " has been received");
                    break;
                }
                ReliablePacket packet = new ReliablePacket(buf);
                int t = packet.getSeqNum()&0xff;
                if((packet.check()&&t==readCount)||readSize!=UDPUtils.BUFFER_SIZE){
                    if(t==3&&flag==0){
                        dsk.receive(dpk);
                        flag++;
                        continue;
                    }
                    bos.write(packet.getData(), 0, readSize-6);
                    if(++flushSize % 1000 == 0){
                        flushSize = 0;
                        bos.flush();
                    }
                    byte[] a = new byte[1];
                    a[0] = 1;
                    dpk.setData(a, 0, 1);
                    dsk.send(dpk);

                    dpk.setData(buf,0, buf.length);
                    System.out.println("Receive count of "+ ( readCount++ ) +" !");
                    dsk.receive(dpk);
                }
                else{
                    byte[] a = new byte[1];
                    a[0] = 0;
                    dpk.setData(a, 0, 1);
                    dsk.send(dpk);

                    dpk.setData(buf,0, buf.length);
                    System.out.println("Failed count of "+ (readCount) +" !");
                    dsk.receive(dpk);
                }

            }
            bos.flush();
            bos.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    private void UpLoad(DatagramSocket dsk){
        try {
            int sendCount = 1;
            int seqnum = 1;
            int readSize = -1;
            byte[] Buf = new byte[UDPUtils.BUFFER_SIZE+6];
            byte[] receiveBuf = new byte[1];
            byte[] buf = new byte[UDPUtils.BUFFER_SIZE];
            dpk.setData(Buf, 0,Buf.length);
            RandomAccessFile accessFile = new RandomAccessFile(SAVE_FILE_PATH, "r");
            while ((readSize = accessFile.read(buf, 0, buf.length)) != -1) {
                ReliablePacket packet;
                if(readSize==UDPUtils.BUFFER_SIZE){
                    packet = new ReliablePacket((byte)seqnum, (byte)0, (byte)0, buf);
                    dpk.setData(packet.getBuf(), 0, packet.getBuf().length);
                }
                else{
                    byte[] b = Arrays.copyOfRange(buf,0,readSize);
                    packet = new ReliablePacket((byte)sendCount, (byte)0, (byte)0, b);
                    dpk.setData(packet.getBuf(), 0, packet.getBuf().length);
                }

                dsk.send(dpk);

                while(true){
                    dpk.setData(receiveBuf, 0, receiveBuf.length);
                    dsk.setSoTimeout(1000);
                    try {
                        dsk.receive(dpk);
                        // confirm server receive
                        if(receiveBuf[0]!=1){
                            System.out.println("Resend the wrong packet");
                            System.out.println(packet.getCheckSum());
                            dpk.setData(packet.getBuf(), 0, packet.getBuf().length);
                            dsk.send(dpk);
                        }
                        else
                            break;
                    } catch (SocketTimeoutException e) {
                        dpk.setData(packet.getBuf(), 0, packet.getBuf().length);
                        dsk.send(dpk);
                        System.out.println("Resend the lost packet");
                        continue;
                    }

                }

                System.out.println("Send count of " + (sendCount++) + "!");
                seqnum++;
                if(seqnum>127){
                    seqnum %= 127;
                }
                //Thread.sleep(10);
            }
            dpk.setData(UDPUtils.end,0,UDPUtils.end.length);
            dsk.send(dpk);
            dpk.setData(receiveBuf,0,receiveBuf.length);
            dsk.setSoTimeout(1000);
            while(true){
                try {
                    dsk.receive(dpk);
                    if(receiveBuf[0] == 1){
                        break;
                    }else{
                        dpk.setData(UDPUtils.end,0,UDPUtils.end.length);
                        dsk.send(dpk);
                        dpk.setData(receiveBuf,0,receiveBuf.length);
                    }
                } catch (SocketTimeoutException e) {
                    dpk.setData(UDPUtils.end,0,UDPUtils.end.length);
                    dsk.send(dpk);
                    dpk.setData(receiveBuf,0,receiveBuf.length);
                    continue;
                }

            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}