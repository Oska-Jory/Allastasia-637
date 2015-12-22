package org.oracle.network.codec;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.oracle.network.codec.login.LoginDecoder;
import org.oracle.network.packet.PacketBuilder;
import org.oracle.utilities.Logger;
import org.oracle.utilities.Logger.Type;
import org.oracle.utilities.StaticObjects;

/**
 * @author Oska - <format@allastasia.com>
 * Decodes the Handshake Protocol.
 */
public class HandshakeDecoder extends FrameDecoder {

	@Override
	protected Object decode(ChannelHandlerContext context, Channel channel,
			ChannelBuffer buffer) throws Exception {
		
		if (context.getPipeline().get(HandshakeDecoder.class) != null) {
			context.getPipeline().remove(this);
		}
		
		int opcode = buffer.readByte() & 0xff;
		Logger.log(Type.DECODER, "Message Received: Operation code (" + opcode + ")");
		PacketBuilder builder = new PacketBuilder();
		
		switch (opcode) {
		case 15: // JS5 Request
			int revision = buffer.readInt();
			
			if (revision != StaticObjects.BUILD) {
				builder.writeByte(6).write(channel);
			} else {
				builder.writeByte(0);
				
				for (int i : StaticObjects.ELEMENT_SIZES)
					builder.writeInt(i);
				
				builder.write(channel);
				channel.getPipeline().addBefore("handler", "decoder", new OnDemandDecoder());
			}
			break;
			
		case 14: // Login Request
			channel.getPipeline().addBefore("handler", "decoder", new LoginDecoder());
			builder.writeByte(0).write(channel);
			break; 
		}
		return null;
	}
 
}
