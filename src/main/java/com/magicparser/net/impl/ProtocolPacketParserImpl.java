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
package com.magicparser.net.impl;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.magicparser.net.ProtocolPacketParser;
import com.magicparser.net.exception.UnknownPacketProtocolParserException;
import com.magicparser.net.exception.UnknownPartTypeProtocolParserException;
import com.magicparser.net.packet.DataPart;
import com.magicparser.net.packet.Part;
import com.magicparser.net.packet.ProtocolPacket;
import com.magicparser.net.packet.ProtocolPacket.PacketDirection;
import com.magicparser.net.packet.ReferencedPart;
import com.magicparser.util.ByteUtils;
import com.magicparser.util.ProtocolUtils;
import com.rogiel.packetmagic.packet.PacketDescriptor;
import com.rogiel.packetmagic.packet.PacketPartDescriptor;
import com.rogiel.packetmagic.packet.ProtocolDescriptor;
import com.rogiel.packetmagic.packet.ReferencedPacketPartDescriptor;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class ProtocolPacketParserImpl implements ProtocolPacketParser {
	/**
	 * The protocol descriptor
	 */
	private final ProtocolDescriptor descriptor;

	/**
	 * @param descriptor
	 *            the protocol descriptor
	 */
	public ProtocolPacketParserImpl(ProtocolDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	@Override
	public ProtocolPacket parseClientPacket(ByteBuffer buffer)
			throws UnknownPacketProtocolParserException,
			UnknownPartTypeProtocolParserException {
		buffer.mark();
		final PacketDescriptor packetDescriptor = matchPacket(buffer,
				descriptor.getPacket().getOpcodeLength(), descriptor
						.getPacket().getClient().getPacket());
		return createPacket(PacketDirection.CLIENT, packetDescriptor, buffer);
	}

	@Override
	public ProtocolPacket parseServerPacket(ByteBuffer buffer)
			throws UnknownPacketProtocolParserException,
			UnknownPartTypeProtocolParserException {
		final PacketDescriptor packetDescriptor = matchPacket(buffer,
				descriptor.getPacket().getOpcodeLength(), descriptor
						.getPacket().getServer().getPacket());
		return createPacket(PacketDirection.SERVER, packetDescriptor, buffer);
	}

	private ProtocolPacket createPacket(PacketDirection direction,
			PacketDescriptor descriptor, ByteBuffer buffer)
			throws UnknownPacketProtocolParserException,
			UnknownPartTypeProtocolParserException {
		final Part<?, ?>[] parts = parsePacket(descriptor, buffer);
//		if (parts == null)
//			throw new UnknownPacketProtocolParserException();
		buffer.clear();
		return new ProtocolPacket(descriptor, buffer, direction, parts);
	}

	private PacketDescriptor matchPacket(ByteBuffer buffer, int len,
			List<PacketDescriptor> packets)
			throws UnknownPacketProtocolParserException {
		final byte[] opcode = ByteUtils.readBytes(buffer, len);
		for (PacketDescriptor packet : packets) {
			if (Arrays.equals(packet.getOpcode(), opcode)) {
				if (packet.getExtended() != null) {
					packet = matchPacket(buffer, packet.getExtended()
							.getOpcodeLength(), packet.getExtended()
							.getPacket());
					if (packet == null) {
						packet = new PacketDescriptor();
						packet.setOpcode(opcode);
					}
					return packet;
				}
				return packet;
			}
		}
		return null;
	}

	private Part<?, ?>[] parsePacket(PacketDescriptor packet, ByteBuffer buffer)
			throws UnknownPartTypeProtocolParserException {
		final List<Part<?, ?>> parts = new ArrayList<Part<?, ?>>();
		if(packet == null)
			return null;
		if (packet.getParts() == null)
			return null;
		for (final PacketPartDescriptor part : packet.getParts()) {
			final Part<?, ?> protocolPart = ProtocolUtils.getPart(part);
			if (protocolPart == null)
				throw new UnknownPartTypeProtocolParserException(part
						.getClass().getName());
			if (protocolPart instanceof ReferencedPart) {
				((ReferencedPart<?, ?>) protocolPart).setReference(findPart(
						(((ReferencedPacketPartDescriptor) part)
								.getReferencePart().getId()), parts));
			}
			protocolPart.read(buffer);
			parts.add(protocolPart);
		}
		return parts.toArray(new Part<?, ?>[parts.size()]);
	}

	private DataPart<?, ?> findPart(String name, List<Part<?, ?>> parts) {
		for (final Part<?, ?> part : parts) {
			if (name.equals(part.getID()))
				return (DataPart<?, ?>) part;
		}
		return null;
	}
}
