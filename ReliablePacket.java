import java.net.DatagramPacket;

public class ReliablePacket {
	
	private int _checksum;
	private int _seqnum;
	private int _type;
	private int _rwnd;
	private int _cwnd;
	
	private DatagramPacket _packet;
	
	public ReliablePacket(int seqnum, int type, DatagramPacket packet){
		this._seqnum = seqnum;
		this._type = type;
		this._packet = packet;
		this._checksum = getCheckSum();
	}
	private int getCheckSum(){
		int length = _packet.getLength();
		byte[] data = _packet.getData();
		byte a,b;
		int sum = 0, carry = 0;
		for (int i = 0;i < length-1;i+=2){
			a = data[i];
			b = data[i+1];
			sum += a+b;
			carry = sum/65536;
			sum %= 65536;
			sum += carry;
			sum = ~sum;
			sum = sum&0x0FFFF;
		}
		if(length%2==1){
			sum += (data[length-1]<<8);
			carry = sum/65536;
			sum %= 65536;
			sum += carry;
			sum = ~sum;
			sum = sum&0x0FFFF;
		}
		return sum;
	}
	public boolean check(){
		if(_packet==null){
			System.out.println("The packet is null.");
			return false;
		}
		if(_checksum+getCheckSum()==65535) return true;
		else {
			System.out.println("Packet "+Integer.toString(_seqnum)+" error.");
			return false;
		}
	}
}
