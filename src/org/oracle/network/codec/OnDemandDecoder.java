package org.oracle.network.codec;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.oracle.cache.CacheManager;
import org.oracle.network.packet.Packet;
import org.oracle.network.packet.PacketBuilder;
import org.oracle.task.Task.Priority;
import org.oracle.task.TaskManager;

/**
 * 
 * @author Oska - <format@allastasia.com>
 * Decodes the OnDemand Protocol.
 */
public class OnDemandDecoder extends FrameDecoder {
	
	@Override
	protected Object decode(ChannelHandlerContext context, Channel channel,
			ChannelBuffer buffer) throws Exception {
		while (buffer.readableBytes() >= 4) {
			int opcode = buffer.readByte() & 0xff;
			int cacheId = buffer.readByte() & 0xff;
			int fileId = buffer.readShort() & 0xffff;
			
			switch (opcode) {
				case 1:
				case 0:
					TaskManager.execute(new Runnable() {

						@Override
						public void run() {
							Packet response = CacheManager.generateFile(cacheId, fileId, opcode);	
							channel.write(response);
						}
					}, Priority.HIGH);
					break;
			}
			
		}
		return null;
	}

}
