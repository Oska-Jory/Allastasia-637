package org.oracle.network.external;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.oracle.network.packet.Packet;
import org.oracle.utilities.Logger;
import org.oracle.utilities.Logger.Type;

/**
 * @author Oska - <format@allastasia.com>
 * Handles any incoming connections.
 */
public class ConnectionHandler extends SimpleChannelHandler {

	
	/**
	 * Handles a connected connection.
	 */
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
		if (ConnectionManager.getConnections().containsKey(ctx.getChannel())) {
			Logger.log(Type.CONNECTION, "Connection successfully reconnected!");
			return;
		}
		ConnectionManager.store(ctx.getChannel());
		Logger.log(Type.INFO, "There are now " + ConnectionManager.getConnections().size() + " active connections.");
	}
	
	
	/**
	 * Handles a disconnected connection.
	 */
	@Override
	public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
		if (!ConnectionManager.getConnections().containsKey(ctx.getChannel())) {
			Logger.log(Type.CONNECTION, "Connection already removed!");
			return;
		}
		ConnectionManager.drop(ctx.getChannel());
	}
	
	
	/**
	 * Handles incoming messages.
	 */
	@SuppressWarnings("incomplete-switch")
	@Override
	public void writeRequested(ChannelHandlerContext context, MessageEvent event) {
		if (event.getMessage() instanceof Packet) {
            Packet packet = (Packet) event.getMessage();
            ChannelBuffer buffer = ChannelBuffers.buffer(packet.getLength() + 4);
            if(!packet.isRaw()) {
                int opcode = packet.getOpcode();
                if(opcode < 128) {
                    buffer.writeByte(opcode);
                } else {
                    buffer.writeByte(128);
                    buffer.writeByte(opcode);
                }
                switch(packet.getType()) {
                    case VAR_BYTE:
                        buffer.writeByte(packet.getLength());
                        break;
                    case VAR_SHORT:
                        buffer.writeShort(packet.getLength());
                        break;
                }
            }
            buffer.writeBytes(packet.getBuffer());
            Channels.write(context, event.getFuture(), buffer);
        }
	}
	
	/**
	 * Handles any exception from the connection.
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		Throwable t = e.getCause();
		
		if (!(t instanceof IOException)) {
			Logger.log(Type.EXCEPTION, "Exception Caught from external connection: " + ctx.getChannel().getLocalAddress());
			t.printStackTrace();
		}
	}
	
}
