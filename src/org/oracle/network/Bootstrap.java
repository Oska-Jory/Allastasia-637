package org.oracle.network;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.oracle.utilities.Logger;
import org.oracle.utilities.Logger.Type;

/**
 * 
 * @author Oska - <format@allastasia.com>
 * 
 * The bootstrap server.
 * Binds and creates a pipeline on port 43594, gathering information
 * from external connections that are connected to that port.
 */
public class Bootstrap {

	
	/**
	 * The port of which the server will bind to.
	 */
	private int port;
	
	
	/**
	 * Creates a new Bootstrap for a given port.
	 * @param port
	 */
	public Bootstrap(int port) {
		this.port = port;
	}
	
	
	/**
	 * Returns the value of port
	 * @return
	 */
	public int getPort() {
		return port;
	}
	
	
	/**
	 * Binds to the given port.
	 * @return
	 */
	public Bootstrap bind() {
		
		ServerBootstrap bootstrap;
		Executor executor;
		
		try {
			bootstrap = new ServerBootstrap();
			executor = Executors.newCachedThreadPool();
			bootstrap.setFactory(new NioServerSocketChannelFactory(executor, executor, Runtime.getRuntime().availableProcessors()));
			bootstrap.setPipelineFactory(new OracleChannelPipelineFactory());
			bootstrap.setOption("child.tcpNoDelay", true);
			bootstrap.bind(new InetSocketAddress(port));
			Logger.log(Type.NETWORK, "Successfully binded to port: " + port);
		} catch (Exception e) {
			System.out.println("Failed to bind to port: " + port);
			e.printStackTrace();
		}
		return this;
	}
	
	
}
