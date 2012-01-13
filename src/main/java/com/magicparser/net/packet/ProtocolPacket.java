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
package com.magicparser.net.packet;

import java.nio.ByteBuffer;

import com.rogiel.packetmagic.packet.PacketDescriptor;
import com.rogiel.packetmagic.packet.PacketPartDescriptor;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class ProtocolPacket {
	/**
	 * The packet descriptor
	 */
	private final PacketDescriptor descritor;
	/**
	 * The protocol parts
	 */
	private final Part<?, ?>[] parts;
	/**
	 * The packet content
	 */
	private final ByteBuffer content;
	/**
	 * The packet direction
	 */
	private final PacketDirection direction;

	/**
	 * Defines the packet direction
	 * 
	 * @author <a href="http://www.rogiel.com">Rogiel</a>
	 */
	public enum PacketDirection {
		/**
		 * The packet is being sent from the client to the server
		 */
		CLIENT,
		/**
		 * The packet is being sent from the server to the client
		 */
		SERVER;
	}

	/**
	 * @param descritor
	 *            the packet descriptor
	 * @param content
	 *            the packet content
	 * @param direction
	 *            the packet direction
	 * @param parts
	 *            the packet parts
	 */
	public ProtocolPacket(PacketDescriptor descritor, ByteBuffer content,
			PacketDirection direction, final Part<?, ?>[] parts) {
		this.descritor = descritor;
		this.content = content;
		this.direction = direction;
		this.parts = parts;
	}

	/**
	 * @return the parts
	 */
	public Part<?, ?>[] getParts() {
		return parts;
	}

	/**
	 * @param name
	 *            the part name
	 * @return the part
	 */
	public Part<?, ?> getPart(String name) {
		for (final Part<?, ?> part : parts) {
			if (part.descriptor.getName().equals(name))
				return part;
		}
		return null;
	}

	/**
	 * @param descriptor
	 *            the part descriptor
	 * @return the part
	 */
	public Part<?, ?> getPart(PacketPartDescriptor descriptor) {
		for (final Part<?, ?> part : parts) {
			if (part.descriptor.equals(descriptor))
				return part;
		}
		return null;
	}

	/**
	 * @return the content
	 */
	public ByteBuffer getContent() {
		return content;
	}

	/**
	 * @return the direction
	 */
	public PacketDirection getDirection() {
		return direction;
	}

	/**
	 * @return the descritor
	 */
	public PacketDescriptor getDescritor() {
		return descritor;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder().append(descritor.getName())
				.append(" (").append('\n');
		for (final Part<?, ?> part : parts) {
			builder.append('\t').append(part.toString()).append('\n');
		}
		return builder.append(")").toString();
	}
}
