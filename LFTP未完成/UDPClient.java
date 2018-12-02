import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class UDPClient {

    public static extendQueue clientQueue = new extendQueue();
//	private static final String SEND_FILE_PATH = "2018.flv";
	public static void main(String[] args) {
		System.out.println("LFTP client start...");
		try{
			DatagramSocket dsk = new DatagramSocket(UDPUtils.PORT, InetAddress.getByName("localhost"));
			sendThread st1 = new sendThread(dsk,clientQueue);
			receiveThread st2 = new receiveThread(dsk,clientQueue);
			Thread t1 = new Thread(st1);
			Thread t2 = new Thread(st2);
			t1.start();
			t2.start();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

//		long startTime = System.currentTimeMillis();
//
//		byte[] buf = new byte[UDPUtils.BUFFER_SIZE];
//		byte[] receiveBuf = new byte[1];
//
//		ClientQueue clientQueue = new ClientQueue();
//		RandomAccessFile accessFile = null;
//		DatagramPacket dpk = null;
//		DatagramSocket dsk = null;
//		int readSize = -1;
//		try {
//			accessFile = new RandomAccessFile(SEND_FILE_PATH,"r");
//			dpk = new DatagramPacket(buf, buf.length,new InetSocketAddress(InetAddress.getByName("localhost"), UDPUtils.PORT + 1));
//			dsk = new DatagramSocket(UDPUtils.PORT, InetAddress.getByName("localhost"));
//
//			/*判断是否连接*/
//			if(ClientConnect(dsk,dpk)){
//				System.out.println("Connect Success");
//			}else{
//				System.out.println("Connect Fail");
//				return;
//			}
//
////			int sendCount = 0;
////			while((readSize = accessFile.read(buf,0,buf.length)) != -1){
////				dpk.setData(buf, 0, readSize);
////				dsk.send(dpk);
////				{
////					while(true){
////						dpk.setData(receiveBuf, 0, receiveBuf.length);
////						dsk.receive(dpk);
////
////						if(!UDPUtils.isEqualsByteArray(UDPUtils.successData,receiveBuf,dpk.getLength())){
////							System.out.println("resend ...");
////							dpk.setData(buf, 0, readSize);
////							dsk.send(dpk);
////						}else
////							break;
////					}
////				}
////				System.out.println("send count of "+(++sendCount)+"!");
////			}
//			int sendCount = 0;
//			while((readSize = accessFile.read(buf,0,buf.length)) != -1){
//				dpk.setData(buf, 0, readSize);
//				dsk.send(dpk);
//				clientQueue.setDatagramPacket(sendCount,dpk);
//				System.out.println("Send count of "+(++sendCount)+"!");
//			}
//
//			while(true){
//				System.out.println("Client send exit message ....");
//				dpk.setData(UDPUtils.exitData,0,UDPUtils.exitData.length);
//				dsk.send(dpk);
//
//				dpk.setData(receiveBuf,0,receiveBuf.length);
//				dsk.receive(dpk);
//				// byte[] receiveData = dpk.getData();
//				if(!UDPUtils.isEqualsByteArray(UDPUtils.exitData, receiveBuf, dpk.getLength())){
//					System.out.println("Client Resend exit message ....");
//					dsk.send(dpk);
//				}else
//					break;
//			}
//		}catch (Exception e) {
//			e.printStackTrace();
//		} finally{
//			try {
//				if(accessFile != null)
//					accessFile.close();
//				if(dsk != null)
//					dsk.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//
//		long endTime = System.currentTimeMillis();
//		System.out.println("time:"+(endTime - startTime));
//	}

//	private static boolean ClientConnect(DatagramSocket inputDSK,DatagramPacket inputDPK) {
//		try{
//			System.out.println("Send first SYN from client");
//			inputDPK.setData(UDPUtils.connectClient,0,UDPUtils.connectClient.length);
//			inputDSK.send(inputDPK);
//
//			byte[] tempReceive = new byte[100];
//			inputDPK.setData(tempReceive,0,tempReceive.length);
//			inputDSK.receive(inputDPK);
//			if(UDPUtils.isEqualsByteArray(UDPUtils.connectServer, tempReceive, inputDPK.getLength())){
//				System.out.println("Receive second SYN from server");
//				return true;
//			}
//			return false;
//		}
//		catch (Exception e){
//			e.printStackTrace();
//		}
//		return false;
//	}

}
