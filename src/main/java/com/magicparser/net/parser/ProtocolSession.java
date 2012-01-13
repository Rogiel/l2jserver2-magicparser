/*
 * This file is part of l2jserver2 <l2jserver2.com>.
 *
 * l2jserver2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * l2jserver2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with l2jserver2.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.magicparser.net.parser;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;

import com.magicparser.net.ProtocolPacketParser;
import com.magicparser.net.codec.ProtocolCodec;
import com.magicparser.net.exception.InvalidPacketProtocolParserException;
import com.magicparser.net.exception.UnknownPacketProtocolParserException;
import com.magicparser.net.exception.UnknownPartTypeProtocolParserException;
import com.magicparser.net.packet.ProtocolPacket;
import com.magicparser.net.packet.ProtocolPacket.PacketDirection;
import com.magicparser.util.ByteUtils;
import com.rogiel.packetmagic.packet.ProtocolDescriptor;
import com.rogiel.packetmagic.packet.ProtocolDescriptor.ProtocolByteOrder;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class ProtocolSession {
	/**
	 * The session id
	 */
	private final long id;
	/**
	 * The protocol descriptor
	 */
	private final ProtocolDescriptor descriptor;
	/**
	 * The protocol packet parser
	 */
	private final ProtocolPacketParser parser;
	/**
	 * The client address
	 */
	private InetAddress clientAddress;

	/**
	 * Codec that decodes server packets
	 */
	private ProtocolCodec serverCodec;
	/**
	 * Codec that decodes client packets
	 */
	private ProtocolCodec clientCodec;
	/**
	 * The protocol byte buffer
	 */
	private ByteBuffer clientBuffer = ByteBuffer.allocate(1460 * 20);
	/**
	 * The protocol byte buffer
	 */
	private ByteBuffer serverBuffer = ByteBuffer.allocate(1460 * 20);

	/**
	 * @param id
	 *            the session id
	 * @param descriptor
	 *            the protocol descriptor
	 * @param parser
	 *            the protocol packet parser
	 */
	public ProtocolSession(final long id, ProtocolDescriptor descriptor,
			ProtocolPacketParser parser) {
		this.id = id;
		this.descriptor = descriptor;
		this.parser = parser;
		createCodecs();
		if (descriptor.getEndianess() == ProtocolByteOrder.LITTLE_ENDIAN) {
			clientBuffer.order(ByteOrder.LITTLE_ENDIAN);
			serverBuffer.order(ByteOrder.LITTLE_ENDIAN);
		}
	}

	/**
	 * @param packet
	 *            the {@link TCPPacket} that has been received on the session
	 * @return an parser {@link ProtocolPacket} packet. <code>null</code> if the
	 *         {@link TCPPacket} contained no data.
	 * @throws InvalidPacketProtocolParserException
	 *             if the packet data was incorrect
	 * @throws UnknownPacketProtocolParserException
	 *             if the packet descriptor could not be found
	 * @throws UnknownPartTypeProtocolParserException
	 *             if the packet part descriptor could not be found
	 */
	protected ProtocolPacket[] receivePacket(WritableByteChannel channel,
			Tcp tcp, Ip4 ip) throws InvalidPacketProtocolParserException,
			UnknownPacketProtocolParserException,
			UnknownPartTypeProtocolParserException {
		// since only clients starts connections, we can use this to match the
		// client IP and it is not necessary to ask the user which IP is client
		if (clientAddress == null) {
			if (!tcp.flags_SYN())
				return null;
			try {
				clientAddress = Inet4Address.getByAddress(ip.source());
			} catch (UnknownHostException e) {
				return null;
			}
		}
		final byte[] data = tcp.getPayload();
		if (data.length == 0)
			return null;
		final List<ProtocolPacket> packets = new ArrayList<ProtocolPacket>(1);
		ProtocolPacket protocolPacket;
		if (isClientPacket(ip)) {
			try {
				clientBuffer = ByteBuffer.wrap(data);
				while (isPacketComplete(PacketDirection.CLIENT)) {
					protocolPacket = readPacket(PacketDirection.CLIENT);
					trySetKey(protocolPacket);
					packets.add(protocolPacket);
				}
				//clientBuffer.compact();
				//clientBuffer.clear();
				//clientBuffer.put(new byte[clientBuffer.remaining()]).clear();
			} catch (RuntimeException e) {
				clientBuffer.clear();
				clientBuffer.put(new byte[clientBuffer.remaining()]).clear();
				throw e;
			}
		} else {
			try {
				serverBuffer = ByteBuffer.wrap(data);
				//serverBuffer.put(data).flip();
				while (isPacketComplete(PacketDirection.SERVER)) {
					protocolPacket = readPacket(PacketDirection.SERVER);
					trySetKey(protocolPacket);
					packets.add(protocolPacket);
				}
				//serverBuffer.compact();
				//serverBuffer.clear();
				//serverBuffer.put(new byte[clientBuffer.remaining()]).clear();
			} catch (RuntimeException e) {
				serverBuffer.clear();
				serverBuffer.put(new byte[serverBuffer.remaining()]).clear();
				throw e;
			}
		}
		return packets.toArray(new ProtocolPacket[packets.size()]);
	}

	private void trySetKey(ProtocolPacket packet) {
		if (packet != null && packet.getDescritor() != null)
			if (packet.getDescritor().equals(
					descriptor.getCodec().getKeyPacket())) {
				serverCodec.receivedKey(packet);
				clientCodec.receivedKey(packet);
			}
	}

	private ProtocolPacket readPacket(PacketDirection direction)
			throws UnknownPacketProtocolParserException,
			UnknownPartTypeProtocolParserException {
		final ByteBuffer buffer = (direction == PacketDirection.CLIENT ? clientBuffer
				: serverBuffer);
		final ProtocolCodec codec = (direction == PacketDirection.CLIENT ? clientCodec
				: serverCodec);

		final int initialPos = buffer.position();
		final int initialRemain = buffer.remaining();
		final int len = read(buffer, descriptor.getPacket().getHeaderLength()) & 0xFFFF;
		
		buffer.order(ByteOrder.LITTLE_ENDIAN);

		if (len == 0)
			return null;
		if (len < 0) {
			buffer.position(initialPos);
			throw new IllegalArgumentException(ByteUtils.hexDump(buffer));
		}
//		codec.decode(buffer);
//		final int opcode = buffer.get();
////		if(opcode == 0x19) {
////			System.out.println(ByteUtils.hexDump(buffer));
////			System.out.println(ByteUtils.printData(buffer, buffer.remaining()));
////		}
//		return null;
//		
		if (initialRemain < len)
			return null;

//		ByteBuffer packetBuffer = ByteBuffer.allocate(buffer.remaining());
//		packetBuffer.order(ByteOrder.LITTLE_ENDIAN);
//		packetBuffer.put(buffer);
//		packetBuffer.flip();

		codec.decode(buffer);
		try {
			if (direction == PacketDirection.CLIENT)
				return parser.parseClientPacket(buffer);
			else
				return parser.parseServerPacket(buffer);
		} finally {
			// buffer.limit(initialLimit);
			//buffer.position(initialPos + len);
		}
	}

	private boolean isPacketComplete(PacketDirection direction) {
		final ByteBuffer buffer = (direction == PacketDirection.CLIENT ? clientBuffer
				: serverBuffer);
		if (buffer.remaining() < descriptor.getPacket().getHeaderLength())
			return false;
		final int len = read(buffer, buffer.position(), descriptor.getPacket()
				.getHeaderLength());
		if (len == 0) {
			read(buffer, descriptor.getPacket().getHeaderLength());
			return false;
		}
		return buffer.remaining() >= len;
	}

	/**
	 * Creates the codec instances
	 */
	private void createCodecs() {
		try {
			final Class<?> codecClass = Class.forName(descriptor.getCodec()
					.getClazz());
			serverCodec = (ProtocolCodec) codecClass.newInstance();
			clientCodec = (ProtocolCodec) codecClass.newInstance();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param ip
	 *            the {@link Ip4} header
	 * @return <code>true</code> if the packet was sent by the client
	 */
	private boolean isClientPacket(Ip4 ip) {
		return Arrays.equals(clientAddress.getAddress(), ip.source());
	}

	/**
	 * @param buffer
	 *            the buffer
	 * @param len
	 *            the numeric type length
	 * @return the read numeric type
	 */
	private int read(ByteBuffer buffer, int len) {
		switch (len) {
		case 1:
			return buffer.get();
		case 2:
			return buffer.getShort();
		case 4:
			return buffer.getInt();
		case 8:
			return (int) buffer.getLong();
		default:
			throw new RuntimeException();
		}
	}

	/**
	 * @param buffer
	 *            the buffer
	 * @param len
	 *            the numeric type length
	 * @return the read numeric type
	 */
	private int read(ByteBuffer buffer, int offset, int len) {
		switch (len) {
		case 1:
			return buffer.get(offset);
		case 2:
			return buffer.getShort(offset);
		case 4:
			return buffer.getInt(offset);
		case 8:
			return (int) buffer.getLong(offset);
		default:
			throw new RuntimeException();
		}
	}

	/**
	 * @return the session id
	 */
	public long getID() {
		return id;
	}
}
