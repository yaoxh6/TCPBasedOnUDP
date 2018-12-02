import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.net.InetSocketAddress;
import java.util.Scanner;

public class UDPClient {

    private static final String SEND_FILE_PATH = "2018.txt";
    private static final String IP_ADDRESS = "localhost";
	public static void main(String[] args) {
		DatagramSocket dsk = null;
	    RandomAccessFile accessFile = null;
	    byte[] buf = new byte[UDPUtils.BUFFER_SIZE];
	    byte[] Buf = new byte[UDPUtils.BUFFER_SIZE+6];
		byte[] receiveBuf = new byte[1];
	    int readSize = -1;
		Scanner sc = new Scanner(System.in);
		System.out.println("LFTP client start...");
		String command;
		try {
            DatagramPacket dpk = new DatagramPacket(Buf, Buf.length, new InetSocketAddress(InetAddress.getByName("localhost"), UDPUtils.PORT + 1));
            dsk = new DatagramSocket(UDPUtils.PORT, InetAddress.getByName("localhost"));
            /*判断是否连接*/
			if(ClientConnect(dsk,dpk)){
				System.out.println("Connect Success");
			}else{
				System.out.println("Connect Fail");
				return;
			}
			while(true){
				System.out.println("please input commond");
				command = sc.nextLine();
				if(command.equals("1")){
					UpLoad(dsk);
					break;
				}
				else if(command.equals("2")){
					DownLoad();
					break;
				}
				else{
					System.out.println("Wrong Command");
				}
			}
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if(accessFile != null)
                    accessFile.close();
                if(dsk != null)
                    dsk.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}

	private static boolean ClientConnect(DatagramSocket inputDSK,DatagramPacket inputDPK) {
		try{
			System.out.println("Send first SYN from client");
			inputDPK.setData(UDPUtils.connectClient,0,UDPUtils.connectClient.length);
			inputDSK.send(inputDPK);

			byte[] tempReceive = new byte[100];
			inputDPK.setData(tempReceive,0,tempReceive.length);
			inputDSK.receive(inputDPK);
			if(UDPUtils.isEqualsByteArray(UDPUtils.connectServer, tempReceive, inputDPK.getLength())){
				System.out.println("Receive second SYN from server");
				return true;
			}
			return false;
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}

	public static void UpLoad(DatagramSocket dsk){
		try{
			int sendCount = 1;
			int seqnum = 1;
			int readSize = -1;
			byte[] Buf = new byte[UDPUtils.BUFFER_SIZE+6];
			byte[] receiveBuf = new byte[1];
			byte[] buf = new byte[UDPUtils.BUFFER_SIZE];
			RandomAccessFile accessFile = new RandomAccessFile(SEND_FILE_PATH, "r");
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
					dsk.receive(dpk);

					// confirm server receive
					if(receiveBuf[0]!=1){
						System.out.println("resend ...");
						System.out.println(packet.getCheckSum());
						dpk.setData(packet.getBuf(), 0, packet.getBuf().length);
						dsk.send(dpk);
					}
					else
						break;
				}

				System.out.println("Send count of " + (sendCount++) + "!");
				seqnum++;
				if(seqnum>127){
					seqnum %= 127;
				}
				//Thread.sleep(10);
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	public static void DownLoad(){

	}

}
