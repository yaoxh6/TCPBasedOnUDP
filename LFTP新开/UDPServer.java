import java.awt.*;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.util.Arrays;


public class UDPServer {

	private static final String SAVE_FILE_PATH = "2019.txt";
    private static DatagramPacket dpk = null;
	public static void main(String[] args) {

		System.out.println("LFTP server start....");
		byte[] buf = new byte[UDPUtils.BUFFER_SIZE+6];

		
		DatagramSocket dsk = null;
		BufferedOutputStream bos = null;
		try {
			dpk = new DatagramPacket(buf, buf.length,new InetSocketAddress(InetAddress.getByName("172.18.33.211"), UDPUtils.PORT));
			dsk = new DatagramSocket(UDPUtils.PORT + 1);
			 if(ServerConnect(dsk,dpk)){
			 	System.out.println("Connect Success");
			 	dpk.setData(buf,0,buf.length);
			 }
			 else{
			 	System.out.println("Connect Fail");
			 	return;
			 }
			 byte[] tempReceive = new byte[100];
			 dpk.setData(tempReceive,0,tempReceive.length);
			 dsk.receive(dpk);
			 if(UDPUtils.isEqualsByteArray(UDPUtils.upload, tempReceive, dpk.getLength())){
				 System.out.println("Uploading to server.");
				 UpLoad(dsk);
			 }
			 else if(UDPUtils.isEqualsByteArray(UDPUtils.download, tempReceive, dpk.getLength())){
				 System.out.println("Downloading from server.");
				 DownLoad(dsk);
			 }
			 else{
				 System.out.println("Error.");
				 return;
			 }
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				if(bos != null)
					bos.close();
				if(dsk != null)
					dsk.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}


	}
	private static void DownLoad(DatagramSocket dsk){
		try {
			
			byte[] buf = new byte[UDPUtils.BUFFER_SIZE+6];
			dpk.setData(buf,0,buf.length);
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(SAVE_FILE_PATH));
			dsk.receive(dpk);

			int readSize = 0;
			int readCount = 1;
			int flushSize = 0;

			int flag = 0;
			while((readSize = dpk.getLength()) != 0){
				if(UDPUtils.isEqualsByteArray(UDPUtils.end,buf,dpk.getLength())){
					byte[] a = new byte[1];
					a[0] = 1;
					dpk.setData(a, 0, 1);
					dsk.send(dpk);
					break;
				}
				ReliablePacket packet = new ReliablePacket(buf);
				int t = packet.getSeqNum()&0xff;
				if((packet.check()&&t==readCount)||readSize!=UDPUtils.BUFFER_SIZE){
					if(flag==0&&readCount==2){
						flag = 1;
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
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	private static void UpLoad(DatagramSocket dsk){
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
	private static boolean ServerConnect(DatagramSocket inputDSK,DatagramPacket inputDPK) {
		try{
			byte[] tempReceive = new byte[100];
			inputDPK.setData(tempReceive,0,tempReceive.length);
			inputDSK.receive(inputDPK);
			System.out.println("Test address:" + inputDPK.getAddress());
			dpk.setAddress(inputDPK.getAddress());
			if(UDPUtils.isEqualsByteArray(UDPUtils.connectClient, tempReceive, inputDPK.getLength())){
				System.out.println("Receive first SYN from client");
				System.out.println("Send second SYN from server");
				inputDPK.setData(UDPUtils.connectServer,0,UDPUtils.connectServer.length);
				inputDSK.send(inputDPK);
				return true;
			}
			return false;
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}
}