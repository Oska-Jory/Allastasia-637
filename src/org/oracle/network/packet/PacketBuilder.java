package org.oracle.network.packet;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.oracle.network.packet.Packet.Type;

public class PacketBuilder {

	public static final int[] BIT_MASK = new int[32];

	static {
		for(int i = 0; i < BIT_MASK.length; i++) {
			BIT_MASK[i] = (1 << i) - 1;
		}
	}

    /**
     * Separator
     */

    private int opcode;

    private Type type;

    private ChannelBuffer buffer;

    private int bitIndex;

	public PacketBuilder() {
		this(-1, Type.FIXED);
	}

	public PacketBuilder(int opcode) {
		this(opcode, Type.FIXED);
	}

	public PacketBuilder(int opcode, Type type) {
		this.opcode = opcode;
		this.type = type;
        this.buffer = ChannelBuffers.dynamicBuffer();
	}

	public int getPosition() {
		return buffer.writerIndex();
	}

    public PacketBuilder writeBytes(ChannelBuffer b) {
        buffer.writeBytes(b);
        return this;
    }

    public PacketBuilder writeBytes(byte[] b) {
        buffer.writeBytes(b);
        return this;
    }
    
    public PacketBuilder writeBytes(byte[] b, int index, int length) {
        buffer.writeBytes(b, index, length);
        return this;
    }

    public PacketBuilder writeByte(int i) {
        buffer.writeByte(i);
        return this;
    }

    public PacketBuilder writeByteA(int i) {
        buffer.writeByte(i + 128);
        return this;
    }

    public PacketBuilder writeByteC(int i) {
        buffer.writeByte(-i);
        return this;
    }
    
    
    public PacketBuilder skip(int i) {
    	buffer.skipBytes(i);
    	return this;
    }

    public PacketBuilder writeByteS(int i) {
        buffer.writeByte(128 - i);
        return this;
    }

    public PacketBuilder writeTriByte(int i) {
        buffer.writeByte(i >> 16);
        buffer.writeByte(i >> 8);
        buffer.writeByte(i);
        return this;
    }
    

    public PacketBuilder writeShort(int i) {
        buffer.writeShort(i);
        return this;
    }
    
    
    public PacketBuilder writeRS2String(String string) {
        buffer.writeBytes(string.getBytes());
        buffer.writeByte(0);
        return this;
    }


    public PacketBuilder writeShortA(int i) {
        buffer.writeByte(i >> 8);
        buffer.writeByte(i + 128);
        return this;
    }

    public PacketBuilder writeLEShort(int i) {
        buffer.writeByte(i);
        buffer.writeByte(i >> 8);
        return this;
    }

    public PacketBuilder writeLEShortA(int i) {
        buffer.writeByte(i + 128);
        buffer.writeByte(i >> 8);
        return this;
    }

    public PacketBuilder writeInt(int i) {
        buffer.writeInt(i);
        return this;
    }

	public PacketBuilder writeInt1(int i) {
        buffer.writeByte(i >> 8);
        buffer.writeByte(i);
        buffer.writeByte(i >> 24);
        buffer.writeByte(i >> 16);
		return this;
	}

	public PacketBuilder writeInt2(int i) {
        buffer.writeByte(i >> 16);
        buffer.writeByte(i >> 24);
        buffer.writeByte(i);
        buffer.writeByte(i >> 8);
		return this;
	}

	public PacketBuilder writeLEInt(int i) {
        buffer.writeByte(i);
        buffer.writeByte(i >> 8);
        buffer.writeByte(i >> 16);
        buffer.writeByte(i >> 24);
		return this;
	}

    public PacketBuilder writeJAGString(String s) {
        buffer.writeByte(0);
        buffer.writeBytes(s.getBytes());
        buffer.writeByte(0);
        return this;
    }

    public PacketBuilder writeString(String s) {
        buffer.writeBytes(s.getBytes());
        buffer.writeByte(0);
        return this;
    }

    public PacketBuilder writeSmart(int i) {
        if(i >= 128) {
            buffer.writeShort(i + 32768);
        } else {
            buffer.writeByte(i);
        }
        return this;
    }

    public PacketBuilder writeLong(long i) {
        buffer.writeLong(i);
        return this;
    }

	public PacketBuilder initBitAccess() {
		bitIndex = buffer.writerIndex() * 8;
		return this;
	}

	public PacketBuilder finishBitAccess() {
		buffer.writerIndex((bitIndex + 7) / 8);
		return this;
	}

	public PacketBuilder putBits(int bitValue, int value) {
		int bytePos = bitIndex >> 3;
		int offset = 8 - (bitIndex & 7);
		bitIndex += bitValue;
		int pos = (bitIndex + 7) / 8;
		while (pos + 1 > buffer.capacity()) {
			buffer.writeByte((byte) 0);
		}
		buffer.writerIndex(pos);
		byte b;
		for (; bitValue > offset; offset = 8) {
			b = buffer.getByte(bytePos);
			buffer.setByte(bytePos, (byte) (b & ~BIT_MASK[offset]));
			buffer.setByte(bytePos, (byte) (b | (value >> (bitValue - offset)) & BIT_MASK[offset]));
			bytePos++;
			bitValue -= offset;
		}
		b = buffer.getByte(bytePos);
		if (bitValue == offset) {
			buffer.setByte(bytePos, (byte) (b & ~BIT_MASK[offset]));
			buffer.setByte(bytePos, (byte) (b | value & BIT_MASK[offset]));
		} else {
			buffer.setByte(bytePos, (byte) (b & ~(BIT_MASK[bitValue] << (offset - bitValue))));
			buffer.setByte(bytePos, (byte) (b | (value & BIT_MASK[bitValue]) << (offset - bitValue)));
		}
		return this;
	}

    public ChannelBuffer getBuffer() {
        return buffer;
    }

    public Packet toPacket() {
        return new Packet(opcode, type, buffer);
    }

    public ChannelFuture write(Channel channel) {
        return channel.write(toPacket());
    }
	
}
