import java.net.DatagramPacket;
import java.util.Arrays;
import java.util.zip.Checksum;

public class ReliablePacket {
	
	private int _checksum;
	private byte _seqnum;
	private byte _acknum;
	private byte _rwnd;
	private byte _cwnd;
	//buff = {packetData, seqnum(1 byte), acknum(1 byte), cwnd(1 byte), rwnd(1 byte), checksum(2 bytes)};
	private byte[] buf;
	
	
	//client side
	public ReliablePacket(byte seqnum, byte rwnd, Byte cwnd, DatagramPacket packet){
		this._seqnum = seqnum;
		this._rwnd = rwnd;
		this._cwnd = cwnd;
		this._acknum = seqnum;
		this.buf = new byte[packet.getLength()+6];
		byte[] data = packet.getData();
		int length = packet.getLength();
		for (int i = 0;i < length;i++){
			buf[i] = data[i];
		}
		buf[length] = _seqnum;
		buf[length+1] = _acknum;
		buf[length+2] = _cwnd;
		buf[length+3] = _rwnd;
		this._checksum = computeCheckSum();
		buf[length+4] = (byte)(_checksum>>8);
		buf[length+5] = (byte)(_checksum&0x0FF);
		
	}
	//server side
	public ReliablePacket(DatagramPacket packet){
		buf = packet.getData();
		int length = packet.getLength();
		this._checksum = (buf[length-2]<<8)+buf[length-1];
		this._seqnum = buf[length-6];
		this._acknum = buf[length-5];
		this._cwnd = buf[length-4];
		this._rwnd = buf[length-3];
	}
	
	public int computeCheckSum(){
		int length;
		if(buf.length==0){
			return 0;
		}
		else{
			length = buf.length-2;
		}
		byte a,b;
		int sum = 0, carry = 0;
		for (int i = 0;i < length-1;i+=2){
			a = buf[i];
			b = buf[i+1];
			sum += a+b;
			carry = sum/65536;
			sum %= 65536;
			sum += carry;
			sum = ~sum;
			sum = sum&0x0FFFF;
		}
		if(length%2==1){
			sum += (buf[length-1]<<8);
			carry = sum/65536;
			sum %= 65536;
			sum += carry;
			sum = ~sum;
			sum = sum&0x0FFFF;
		}
		return sum;
	}
	public boolean check(){
		if(buf.length==0){
			System.out.println("The packet is null.");
			return false;
		}
		if(_checksum+getCheckSum()==65535) return true;
		else {
			System.out.println("Packet "+Integer.toString(_seqnum)+" error.");
			return false;
		}
	}
	public byte[] returnBuf(){
		return buf;
	}
	public byte getSeqNum(){
		return _seqnum;
	}
	public int getCheckSum(){
		return _checksum;
	}
	public byte getAckNum(){
		return _acknum;
	}
	public byte getCwnd(){
		return _cwnd;
	}
	public byte getRwnd(){
		return _rwnd;
	}
}
