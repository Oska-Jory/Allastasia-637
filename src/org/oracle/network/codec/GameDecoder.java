package org.oracle.network.codec;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.oracle.network.external.Connection;
import org.oracle.network.packet.IncomingPacket;
import org.oracle.network.packet.Packet;
import org.oracle.utilities.Logger;
import org.oracle.utilities.Logger.Type;

/**
 * 
 * @author Oska - <format@allastasia.com>
 * Decodes and handles incoming Packets.
 */
public class GameDecoder extends FrameDecoder {

	
	/**
	 * The user whose messages are being decoded and handled.
	 */
	private Connection connection;
	
	
	/**
	 * Creates a new Game Decoder for a specific connection.
	 * @param user
	 */
	public GameDecoder(Connection connection) {
		this.connection = connection;
	}
	
	
	@Override
	protected Object decode(ChannelHandlerContext context, Channel channel,
			ChannelBuffer buffer) throws Exception {
		int opcode = buffer.readByte() & 0xff;  
		IncomingPacket incoming = IncomingPacket.HANDLERS[opcode];   
		 int size = IncomingPacket.PACKET_SIZES[opcode];
	        switch(size) {
	            case -1:
	                if(buffer.readableBytes() >= 1) {
	                    size = buffer.readByte() & 0xff;
	                } else {
	                    buffer.resetReaderIndex();
	                    return null;
	                }
	                break;
	            case -2:
	                if(buffer.readableBytes() >= 2) {
	                    size = buffer.readShort() & 0xffff;
	                } else {
	                    buffer.resetReaderIndex();
	                    return null;
	                }
	                break;
	            case -3:
	                size = buffer.readableBytes();
	                break;
	        }
	        if(incoming == null) {
	            Logger.log(Type.INFO, "Unhandled Packet - Opcode: " + opcode);
	        }
	        if(buffer.readableBytes() >= size) {
	            ChannelBuffer pBuffer = ChannelBuffers.buffer(size);
	            buffer.readBytes(pBuffer);
	            if(incoming != null)
	                incoming.execute(connection.getUser(), new Packet(opcode, null, pBuffer));
	        } else {
	            buffer.resetReaderIndex();
	        }
		return null;
	}

	
	
}
