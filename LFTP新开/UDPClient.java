import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.util.Scanner;

public class UDPClient {

    private static final String SEND_FILE_PATH = "2018.flv";
    private static final String IP_ADDRESS = "localhost";
    private static DatagramPacket dpk;
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
            dpk = new DatagramPacket(Buf, Buf.length, new InetSocketAddress(InetAddress.getByName("localhost"), UDPUtils.PORT + 1));
            dsk = new DatagramSocket(UDPUtils.PORT, InetAddress.getByName("localhost"));

			if(ClientConnect(dsk,dpk)){
				System.out.println("Connect Success");
			}else{
				System.out.println("Connect Fail");
				return;
			}
			while(true){
				System.out.println("please input commond");
				command = sc.nextLine();
//				CommandRegex cr = new CommandRegex(command);
//				if(cr.getIsValid()){
//					System.out.println(cr.getUpOrDownLoad());
//					System.out.println(cr.getIpAddress());
//					System.out.println(cr.getFilePath());
//				}
//				else{
//					System.out.println("Wrong Command");
//				}
				if(command.equals("1")){
					dpk.setData(UDPUtils.download,0,UDPUtils.download.length);
					dsk.send(dpk);
					UpLoad(dsk);
					break;
				}
				else if(command.equals("2")){
					dpk.setData(UDPUtils.upload,0,UDPUtils.upload.length);
					dsk.send(dpk);
					DownLoad(dsk);
					break;
				}
				else{
					//System.out.println("Wrong Command");
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
			dpk.setData(Buf, 0, Buf.length);
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
				dsk.setSoTimeout(1000);
				while(true){
					try {
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
					} catch (SocketTimeoutException e) {
						dpk.setData(packet.getBuf(), 0, packet.getBuf().length);
						dsk.send(dpk);
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
					}
					else{
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
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	public static void DownLoad(DatagramSocket dsk){
		try{
		    byte[] Buf = new byte[UDPUtils.BUFFER_SIZE+6];
		    
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(SEND_FILE_PATH));
			dpk.setData(Buf, 0, Buf.length);
			dsk.receive(dpk);

			int readSize = 0;
			int readCount = 1;
			int flushSize = 0;

			while((readSize = dpk.getLength()) != 0){
				if(UDPUtils.isEqualsByteArray(UDPUtils.end,Buf,dpk.getLength())){
					byte[] a = new byte[1];
					a[0] = 1;
					dpk.setData(a, 0, 1);
					dsk.send(dpk);
					break;
				}
				ReliablePacket packet = new ReliablePacket(Buf);
				int t = packet.getSeqNum()&0xff;
				if((packet.check()&&t==readCount)||readSize!=UDPUtils.BUFFER_SIZE){
					bos.write(packet.getData(), 0, readSize-6);
					if(++flushSize % 1000 == 0){
						flushSize = 0;
						bos.flush();
					}
					byte[] a = new byte[1];
					a[0] = 1;
					dpk.setData(a, 0, 1);
					dsk.send(dpk);
					dpk.setData(Buf,0, Buf.length);
					System.out.println("Receive count of "+ ( readCount++ ) +" !");
					dsk.receive(dpk);
				}
				else{
					byte[] a = new byte[1];
					a[0] = 0;
					dpk.setData(a, 0, 1);
					dsk.send(dpk);

					dpk.setData(Buf,0, Buf.length);
					System.out.println("Failed count of "+ (readCount) +" !");
					dsk.receive(dpk);
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		
	}
}