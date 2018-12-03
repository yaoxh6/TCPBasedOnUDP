import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;


public class UDPServer {

	private static int NumOfClient = UDPUtils.PORT + 2;
	public static void main(String[] args) {

		System.out.println("LFTP server start....");
		byte[] buf = new byte[UDPUtils.BUFFER_SIZE+6];


		DatagramSocket dsk = null;
		DatagramPacket dpk = null;
		BufferedOutputStream bos = null;
		try {
			dpk = new DatagramPacket(buf, buf.length,new InetSocketAddress(InetAddress.getByName("172.18.33.212"), UDPUtils.PORT));
			dsk = new DatagramSocket(UDPUtils.PORT + 1);
			while (true){
				if(ServerConnect(dsk,dpk)){
					System.out.println("Connect Success");
					dpk.setData(buf,0,buf.length);
				}
				else{
					System.out.println("Connect Fail");
					return;
				}
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

	private static boolean ServerConnect(DatagramSocket inputDSK,DatagramPacket inputDPK) {
		try{
			DatagramPacket dpk = inputDPK;
			byte[] tempReceive = new byte[100];
			inputDPK.setData(tempReceive,0,tempReceive.length);
			inputDSK.receive(inputDPK);
			System.out.println("From address:" + inputDPK.getAddress());
			System.out.println("From port:" + inputDPK.getPort());
			dpk.setAddress(inputDPK.getAddress());
			if(UDPUtils.isEqualsByteArray(UDPUtils.connectClient, tempReceive, inputDPK.getLength())){
				System.out.println("Receive first SYN from client");
				DatagramSocket newSocket = new DatagramSocket(NumOfClient++);
				UDPServerThread t1 = new UDPServerThread(newSocket,inputDPK.getAddress(),inputDPK.getPort());
				t1.start();
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