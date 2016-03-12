import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.zip.CRC32;

public class FileSender {
	
	HashMap hm = new HashMap();
    
	public FileSender(String[] args)
	{
		MyRcv r = new MyRcv();
		MyThread t = new MyThread();
		
		t.run();
		try{
	        InetAddress serverAdd = InetAddress.getByName(args[1]); //localhost
	        int serverPort = Integer.parseInt(args[2]); //port number of UnreliNET
	        DatagramSocket clientSocket = new DatagramSocket();
			
	        File f = new File(args[0]);
	        FileInputStream fis = new FileInputStream(f);
	        BufferedInputStream bis = new BufferedInputStream(fis);
			byte[] buffer = new byte[986];
	        int bytesRead;
	        
	        int totalNumBytes = (int)f.length(); //total file size
	        int totalPkts = totalNumBytes/986;
	        if(totalPkts % 986 != 0){
	        	totalPkts += 1; //totalNumPkts
	        }
	        totalPkts+=1; // include first packet
	        
	        int seqNum = 0;
	        byte[] totalPktsBytes = ByteBuffer.allocate(4).putInt(totalPkts).array(); //total packets indicator
	        String fileName = args[3]; 
	    	byte[] fileNameBytes = fileName.getBytes(); //filename size
	    	byte[] totalFileNameBytes = ByteBuffer.allocate(40).put(fileNameBytes).array();
	    	byte[] concatFilenameAndNumOfPkts = concat(totalFileNameBytes, totalPktsBytes);
	    	byte[] totalSeqNumBytes = ByteBuffer.allocate(4).putInt(seqNum).array();
	    	byte[] concatSeqNumFileNameNumOfPkts = concat(totalSeqNumBytes, concatFilenameAndNumOfPkts);
	    	byte[] computeChecksumOfFirstPacket = computeChecksum(concatSeqNumFileNameNumOfPkts);
	    	byte[] totalChecksumBytes = ByteBuffer.allocate(10).put(computeChecksumOfFirstPacket).array();
	    	byte[] firstPacket = concat(totalChecksumBytes, concatSeqNumFileNameNumOfPkts);

	        DatagramPacket sendFirstPkt = new DatagramPacket(firstPacket, firstPacket.length, serverAdd, serverPort);
	        clientSocket.send(sendFirstPkt);
	        hm.put(0, sendFirstPkt);
	        
		    byte[] subsequentPackets = new byte[1000];
		    while((bytesRead = fis.read(buffer)) > 0){
		    	seqNum++;
		    	byte[] data = buffer;
		    	byte[] seqNumBytes = ByteBuffer.allocate(4).putInt(seqNum).array();
		    	byte[] concatSeqNumAndData = concat(seqNumBytes, data);
		    	byte[] computeChecksumOfSubsequentPackets = computeChecksum(concatSeqNumAndData);
		    	byte[] totalChecksumBytesOfSubsequentPackets = ByteBuffer.allocate(10).put(computeChecksumOfSubsequentPackets).array();
		    	byte[] subsequentPacketToSend = concat(totalChecksumBytesOfSubsequentPackets, concatSeqNumAndData);
		        DatagramPacket sendPkt = new DatagramPacket(subsequentPacketToSend, subsequentPacketToSend.length, serverAdd, serverPort);
		        hm.put(seqNum, sendPkt);
		        clientSocket.send(sendPkt);
		    }
		    bis.close();
		    fis.close();
	        clientSocket.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
	}
	
	public class MyThread implements Runnable
	{
		@Override
		public void run() {
			while(true)
			System.out.println("hello1");
		}
		
	}
	
	public class MyRcv implements Runnable
	{
		@Override
		public void run() {
			while(true)
			System.out.println("hello2");
		}
		
	}
	
	public void buildFirstPacket()
	{
		
	}
	
	public void buildPackets()
	{
		
	}
	
	public static void main(String[] args) {
		 if (args.length != 4) {
	            System.out.println("Usage: java FileSender <path/filename> "
	                                   + "<rcvHostName> <rcvPort> <rcvFileName>");
	            System.exit(1);
	        }
		 
		 new FileSender(args);
		 
        
    }
	
	public  byte[] computeChecksum(byte[] val){
	    CRC32 crc = new CRC32();
	    crc.update(val);
	    byte[] checksum = String.valueOf(crc.getValue()).getBytes();
		return checksum;
	}
	
	public  byte[] concat(byte[] x, byte[] y){
		byte[] newLength = new byte[x.length + y.length];
		System.arraycopy(x, 0, newLength, 0, x.length);
		System.arraycopy(y, 0, newLength, x.length, y.length);
		return newLength; 
	}

}
