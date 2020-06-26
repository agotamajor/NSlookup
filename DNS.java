//Major Agota-Piroska
//maim1846
//523

import java.io.*;
import java.net.*;

public class DNS {
	private static String ServerAddress = "8.8.8.8";
	private static int ServerPort = 53;
	
	public static void main(String[] args) throws IOException {
		String domain = args[0];
		InetAddress ipAddress = InetAddress.getByName(ServerAddress);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        
        System.out.println("Server: Google DNS");
        System.out.println("Address: "+ServerAddress+"\n");
        //transaction ID
        out.writeShort(0x1234);
        //flags
        	/*
        	 * 0 = Response
        	 * 0 = Opcode: Standard query
        	 * 0 = Truncated
        	 * 1 = Recursion
        	 * 0 = reserved
        	 * 0 = non-authenticated data 
        	 */
        out.writeShort(0x0100);
        //questions
        out.writeShort(0x0001);
        //Answer RRs
        out.writeShort(0x0000);
        //Authority RRs
        out.writeShort(0x0000);
        //Additional RRs
        out.writeShort(0x0000);
        
        System.out.println("Non-authoritative answer:");
        System.out.println("Name: "+domain);
        String[] domainParts = domain.split("\\.");
        //queries
        for (int i = 0; i<domainParts.length; i++) {
            byte[] domainBytes = domainParts[i].getBytes("UTF-8");
            out.writeByte(domainBytes.length);
            out.write(domainBytes);
        }
        
        //nincs tobb resz
        out.writeByte(0x00);
        //type = host request
        out.writeShort(0x0001);
        //class = in
        out.writeShort(0x0001);
        
        byte[] dnsFrame = baos.toByteArray();
        
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket dnsReqPacket = new DatagramPacket(dnsFrame, dnsFrame.length, ipAddress, ServerPort);
        socket.send(dnsReqPacket);
        
        byte[] buf = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(buf));
        //transaction ID
        in.readShort();
        //Flags
        in.readShort();
        //Questions
        in.readShort();
        //answer RRs
        int answer = in.readShort();
        //authority RRs
        in.readShort();
        //additional RRs 
        in.readShort();
        
        //queries
        int recLen = 0;
        while ((recLen = in.readByte()) > 0) {
            byte[] record = new byte[recLen];

            for (int i = 0; i < recLen; i++) {
                record[i] = in.readByte();
            }
        }
        //record type
        in.readShort();
        //class
        in.readShort();

        System.out.print("Addresses: ");
        for (int j = 0; j < answer; j++) {
            // field 
        	in.readShort();
            //type 
        	in.readShort();
            //class 
        	in.readShort();
            //ttl 
        	in.readInt();
      
        	short addrLen = in.readShort();
        	for (int i = 0; i < addrLen; i++ ) {
                System.out.print("" + String.format("%d", (in.readByte() & 0xFF)));
                if (i != addrLen-1) {
                	System.out.print(".");
                }
            }
        	System.out.println();
        }
        
	}
}
