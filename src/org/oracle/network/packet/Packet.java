package org.oracle.network.packet;

import org.jboss.netty.buffer.ChannelBuffer;

public class Packet {

	 public static enum Type {
	        FIXED,
	        VAR_BYTE,
	        VAR_SHORT,
	    }

	    /**
	     * Separator
	     */

	    private int opcode;

	    private Type type;

	    private ChannelBuffer buffer;

	    public Packet(int opcode, Type type, ChannelBuffer buffer) {
	        this.opcode = opcode;
	        this.type = type;
	        this.buffer = buffer;
	    }

	    public void skip(int bytes) {
	        buffer.readerIndex(buffer.readerIndex() + bytes);
	        buffer.resetReaderIndex();
	    }

	    public void readBytes(byte[] bytes) {
	        buffer.readBytes(bytes);
	    }

	    public int readByte() {
	        return buffer.readByte();
	    }

	    public int readUnsignedByte() {
	        return buffer.readByte() & 0xff;
	    }

		public int readByteA() {
			return readUnsignedByte() - 128;
		}

		public int readUnsignedByteA() {
			return readByteA() & 0xff;
		}

	    public int readShort() {
	        return buffer.readShort();
	    }

		public int readLEShort() {
			int i = (readUnsignedByte() + (readUnsignedByte() << 8));
			if (i > 32767) {
				i -= 0x10000;
			}
			return i;
		}

		public int readLEShortA() {
			int i = (readUnsignedByteA() + (readUnsignedByte() << 8));
			if (i > 32767) {
				i -= 0x10000;
			}
			return i;
		}

	    public int readInt() {
	        return buffer.readInt();
	    }

		public int readSmart() {
			int value = readUnsignedByte();
			if (value < 128) {
				return value;
			}
			return value << 8 | readUnsignedByte() - 32768;
		}

	    public String readString() {
	        String s = "";
	        byte b;
	        while(buffer.readable() && (b = buffer.readByte()) != 0) {
	            s += (char) b;
	        }
	        return s;
	    }

	    public boolean isRaw() {
	        return opcode == -1;
	    }

	    public int getOpcode() {
	        return opcode;
	    }

	    public Type getType() {
	        return type;
	    }

	    public ChannelBuffer getBuffer() {
	        return buffer;
	    }

	    public int getLength() {
	        return buffer.writerIndex();
	    }

		public int getPosition() {
			return buffer.readerIndex();
		}

		public int remaining() {
			return getLength() - getPosition();
		}
	
}
