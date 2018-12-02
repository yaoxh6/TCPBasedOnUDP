//import java.io.BufferedOutputStream;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.net.InetAddress;
//import java.net.InetSocketAddress;
//
//public class UDPServerThread{
//	private static String SAVE_FILE_PATH;
//	public UDPServerThread(String filename){
//		this.SAVE_FILE_PATH = filename;
//	}
//	public static void main(String[] args) {
//		bos = new BufferedOutputStream(new FileOutputStream(SAVE_FILE_PATH));
//		dsk.receive(dpk);
//
//		int readSize = 0;
//		int readCount = 1;
//		int flushSize = 0;
//
//		while((readSize = dpk.getLength()) != 0){
//			ReliablePacket packet = new ReliablePacket(buf);
//			int t = packet.getSeqNum()&0xff;
//			if((packet.check()&&t==readCount)||readSize!=UDPUtils.BUFFER_SIZE){
//				bos.write(packet.getData(), 0, readSize-6);
//				if(++flushSize % 1000 == 0){
//					flushSize = 0;
//					bos.flush();
//				}
//				byte[] a = new byte[1];
//				a[0] = 1;
//				dpk.setData(a, 0, 1);
//				dsk.send(dpk);
//
//				dpk.setData(buf,0, buf.length);
//				System.out.println("Receive count of "+ ( readCount++ ) +" !");
//				dsk.receive(dpk);
//			}
//			else{
//				byte[] a = new byte[1];
//				a[0] = 0;
//				dpk.setData(a, 0, 1);
//				dsk.send(dpk);
//
//				dpk.setData(buf,0, buf.length);
//				System.out.println("Failed count of "+ (readCount) +" !");
//				dsk.receive(dpk);
//			}
//
//		}
//			bos.flush();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally{
//			try {
//				if(bos != null)
//					bos.close();
//				if(dsk != null)
//					dsk.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//}