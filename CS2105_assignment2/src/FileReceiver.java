
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.zip.CRC32;

public class FileReceiver {

	public static void main(String[] args) {
		try{
			// check if the number of command line argument is 1
			if (args.length != 1) {
				System.out.println("Usage: java FileReceiver port");
				System.exit(1);
			}
	
			DatagramSocket serverSocket = new DatagramSocket(Integer.parseInt(args[0]));
	
			byte[] rcvBuffer = new byte[1000];
			FileOutputStream fos = null;
	
			String fileName = null;
			boolean lastFile =false;
			int count=0;
			while (!lastFile) {
				
				DatagramPacket rcvedPkt = new DatagramPacket(rcvBuffer, rcvBuffer.length);
				serverSocket.receive(rcvedPkt);

				ByteBuffer crcBuffer = ByteBuffer.wrap(rcvedPkt.getData(), 0, 10);
				byte[] temp = new byte[10];
				crcBuffer.get(temp,0,10);
				
				ByteBuffer dataToCheck = ByteBuffer.wrap(rcvedPkt.getData(), 10, rcvedPkt.getLength()-10);
				byte[] dataToCheckArry = new byte[rcvedPkt.getLength()-10];
				dataToCheck.get(dataToCheckArry,0,rcvedPkt.getLength()-10);
				
				String receiverCRC = computeChecksum(dataToCheckArry);
				if(new String(temp).trim().equals(receiverCRC.trim())){
				}
				else{
				}
				
				ByteBuffer seqNumBuffer = ByteBuffer.wrap(rcvedPkt.getData(), 10, 4);
				int length = seqNumBuffer.getInt();
				System.out.println(length);
				
				
				/*if (fileName == null) {
					// this is my first packet
					fileName = new String(rcvedPkt.getData(), 0, rcvedPkt.getLength());
					fos = new FileOutputStream(fileName);
				} else {
					count++;
					byte[] rcvBytes = rcvedPkt.getData();
					byte[] condition = Arrays.copyOfRange(rcvBytes, 0, 1);
					String conditionString = new String(condition, 0, condition.length);
					byte[] rcvDate = Arrays.copyOfRange(rcvBytes, 1, rcvedPkt.getLength());
					
					fos.write(rcvDate, 0, rcvDate.length);
					if(conditionString.equals("0"))
					{
						lastFile=true;
					}
				}*/
	
				
			}
			//System.out.println(count);
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static String computeChecksum(byte[] val){
	    CRC32 crc = new CRC32();
	    crc.update(val);
	    String checksum = Long.toString(crc.getValue());
		return checksum;
	}

}
