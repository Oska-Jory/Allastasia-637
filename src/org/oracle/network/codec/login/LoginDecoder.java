package org.oracle.network.codec.login;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.oracle.Launcher;
import org.oracle.entity.EntityManager;
import org.oracle.entity.user.User;
import org.oracle.entity.user.UserDefinition;
import org.oracle.network.ActionSender;
import org.oracle.network.codec.GameDecoder;
import org.oracle.network.external.Connection;
import org.oracle.network.packet.PacketBuilder;
import org.oracle.utilities.BufferUtils;
import org.oracle.utilities.Misc;
import org.oracle.utilities.StaticObjects;
import org.oracle.utilities.XTEA;

/**
 * 
 * @author Oska
 *
 */
public class LoginDecoder extends ReplayingDecoder<LoginState> {

	public LoginDecoder() {
		checkpoint(LoginState.PRE_STAGE);
	}
	
	private Connection connection;
	
	@Override
	protected Object decode(ChannelHandlerContext context, Channel channel,
			ChannelBuffer buffer, LoginState state) throws Exception {
		if (state == LoginState.LOBBY_FINALIZATION || state == LoginState.LOGIN_FINALIZATION)
			connection = new Connection(channel);
		
		switch (state) {
		case PRE_STAGE:
			 if ((buffer.writerIndex() - buffer.readerIndex()) >= 3) {
                 int loginType = buffer.readByte() & 0xFF;
                 System.out.println("" + loginType);
                 int loginPacketSize = buffer.readShort() & 0xFFFF;
                 if (loginPacketSize != (buffer.writerIndex() - buffer.readerIndex())) {
                     throw new IOException("Mismatched login packet size.");
                 }
                 int clientVersion = buffer.readInt();
                 if (clientVersion != StaticObjects.BUILD) {
                     throw new IOException("Incorrect revision read");
                 }
                 switch (loginType) {
                 case 16:
                 case 18:
                     checkpoint(LoginState.LOGIN_FINALIZATION);
                 	break;
                 case 19:
                     checkpoint(LoginState.LOBBY_FINALIZATION);
                     break;
                 default:
                     throw new IOException("Incorrect login type");
                 }
             }
			break;
			
		case LOBBY_FINALIZATION:
			 if (buffer.readable()) {
                 buffer.readShort();
                 int rsaHeader = buffer.readByte();
                 if (rsaHeader != 10) {
                     throw new IOException("Invalid RSA header.");
                 }
                 int[] keys = new int[4];
                 for (int i = 0; i < keys.length; i++) {
                     keys[i] = buffer.readInt();
                 }
                 buffer.readLong();
                 String password = BufferUtils.readRS2String(buffer);

                 buffer.readLong(); // client key
                 buffer.readLong(); // other client key

                 byte[] block = new byte[buffer.writerIndex() - buffer.readerIndex()];

                 buffer.readBytes(block);

                 ChannelBuffer decryptedPayload = ChannelBuffers.wrappedBuffer(XTEA.decrypt(keys, block, 0, block.length));
                 String name = BufferUtils.readRS2String(decryptedPayload).toLowerCase();
                 for (char c : name.toCharArray()) {
                     if (!Misc.allowed(c)) {
                         channel.write(new PacketBuilder().writeByte(3).toPacket()).addListener(ChannelFutureListener.CLOSE);
                         return null;
                     }
                 }
                 decryptedPayload.readByte(); // screen settings?
                 decryptedPayload.readByte();
                 for (int i = 0; i < 24; i++) {
                     decryptedPayload.readByte();
                 }
                 BufferUtils.readRS2String(decryptedPayload); // settings
                 decryptedPayload.readInt();
                 for (int i = 0; i < 34; i++) {
                     decryptedPayload.readInt();
                 }
                 connection.setInLobby(true);
                 connection.setUser(new User(connection, new UserDefinition(name, password, 2)));
                 EntityManager.storeUser(connection.getUser(), false);
                 context.getPipeline().replace("decoder", "decoder", new GameDecoder(connection));
			 }
			break;
			
		case LOGIN_FINALIZATION:
			if (buffer.readable()) {
                buffer.readShort();
                int rsaHeader = buffer.readByte();
                if (rsaHeader != 10) {
                    throw new IOException("Invalid RSA header.");
                }
                int[] keys = new int[4];
                for (int i = 0; i < keys.length; i++) {
                    keys[i] = buffer.readInt();
                }
                buffer.readLong();
                String password = BufferUtils.readRS2String(buffer);

                buffer.readLong(); // client key
                buffer.readLong(); // other client key

                byte[] block = new byte[buffer.writerIndex() - buffer.readerIndex()];

                buffer.readBytes(block);

                ChannelBuffer decryptedPayload = ChannelBuffers.wrappedBuffer(XTEA.decrypt(keys, block, 0, block.length));
                String name = BufferUtils.readRS2String(decryptedPayload).toLowerCase();
                for (char c : name.toCharArray()) {
                    if (!Misc.allowed(c)) {
                        channel.write(new PacketBuilder().writeByte(3).toPacket()).addListener(ChannelFutureListener.CLOSE);
                        return null;
                    }
                }
                decryptedPayload.readByte();
                int mode = decryptedPayload.readByte();
                int width = decryptedPayload.readShort();
                int height = decryptedPayload.readShort();
                decryptedPayload.readByte();
                //	System.out.println("Width: "+width+" Height: "+height+" Mode: "+mode);
                for (int i = 0; i < 24; i++) {
                    decryptedPayload.readByte();
                }
                BufferUtils.readRS2String(decryptedPayload);
                decryptedPayload.readInt();
                decryptedPayload.skipBytes(decryptedPayload.readByte() & 0xff);
                connection.setInLobby(false);
                connection.setDisplayMode(mode);
                connection.setUser(new User(connection, new UserDefinition(name, password, 2)));
                ActionSender.loginResponse(connection.getUser());
                Launcher.getWorld().storeUser(connection.getUser());
                //System.out.println("Full Login request  name=" + name + " password=" + password);
                context.getPipeline().replace("decoder", "decoder", new GameDecoder(connection));
                //InterfaceDecoder.switchWindow(session.getPlayer(), mode);
            }
			break;
		
		}
		return null;
	}

}
