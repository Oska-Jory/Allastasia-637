package org.oracle.network;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.DefaultChannelPipeline;
import org.oracle.network.codec.HandshakeDecoder;
import org.oracle.network.external.ConnectionHandler;

/**
 * 
 * @author Oska - <format@allastasia.com>
 * The pipeline that parses information to and from external connections.
 */
public class OracleChannelPipelineFactory implements ChannelPipelineFactory {

	@Override
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = new DefaultChannelPipeline();
		pipeline.addLast("handler", new ConnectionHandler());
		pipeline.addLast("decoder", new HandshakeDecoder());
		return pipeline;
	}

}
