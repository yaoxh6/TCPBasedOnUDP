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
	private byte[] data;
	
	//client side
	public ReliablePacket(byte seqnum, byte rwnd, Byte cwnd, byte[] data){
		this._seqnum = seqnum;
		this._rwnd = rwnd;
		this._cwnd = cwnd;
		this._acknum = seqnum;
		this.buf = new byte[data.length+6];
		this.data = data;
		int length = data.length;
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
	public ReliablePacket(byte[] data){
		this.buf = new byte[data.length];
		this.buf = data;
		int length = data.length;
		this._seqnum = buf[length-6];
		this._acknum = buf[length-5];
		this._cwnd = buf[length-4];
		this._rwnd = buf[length-3];
		this._checksum = ((buf[length-2]&0xff)<<8)|(buf[length-1]&0xff);
		this.data = Arrays.copyOfRange(buf, 0, length-6);
	}
	public void setBuff(byte[] data){
		this.buf = data;
		int length = data.length;
		this._seqnum = buf[length-6];
		this._acknum = buf[length-5];
		this._cwnd = buf[length-4];
		this._rwnd = buf[length-3];
		this._checksum = ((buf[length-2]&0xff)<<8)|(buf[length-1]&0xff);
		this.data = Arrays.copyOfRange(buf, 0, length-6);
	}
	public byte[] getData(){
		return this.data;
	}
	public int computeSum(){
		int length;
		if(buf.length==0){
			return 0;
		}
		else{
			length = buf.length-2;
		}
		int sum = 0, carry = 0;
		for (int i = 0;i < length-1;i+=2){
			sum += (buf[i]&0xff)<<8|(buf[i+1]&0xff);
			carry = sum/65536;
			sum %= 65536;
			sum += carry;
			sum = sum&0x0FFFF;
		}
		carry = sum/65536;
		sum %= 65536;
		sum += carry;
		sum = sum&0x0FFFF;
		return sum;
	}
	public int computeCheckSum(){
		int length;
		if(buf.length==0){
			return 0;
		}
		else{
			length = buf.length-2;
		}
		int sum = 0, carry = 0;
		for (int i = 0;i < length-1;i+=2){
			sum += (buf[i]&0xff)<<8|(buf[i+1]&0xff);
			carry = sum/65536;
			sum %= 65536;
			sum += carry;
			sum = sum&0x0FFFF;
		}
		carry = sum/65536;
		sum %= 65536;
		sum += carry;
		sum = ~sum;
		sum = sum&0x0FFFF;
		return sum;
	}
	public boolean check(){
		if(buf.length==0){
			System.out.println("The packet is null.");
			return false;
		}
		if(_checksum+computeSum()==65535) return true;
		else {
			return false;
		}
	}
	public byte[] getBuf(){
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
