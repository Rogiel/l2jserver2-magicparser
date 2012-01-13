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
package com.magicparser.net;

import java.nio.ByteBuffer;

import com.magicparser.net.exception.UnknownPacketProtocolParserException;
import com.magicparser.net.exception.UnknownPartTypeProtocolParserException;
import com.magicparser.net.packet.ProtocolPacket;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public interface ProtocolPacketParser {
	/**
	 * Parses an packet buffer into an {@link ProtocolPacket}
	 * 
	 * @param buffer
	 *            the packet contents
	 * @return the packet
	 * @throws UnknownPacketProtocolParserException
	 *             if the packet is not known
	 * @throws UnknownPartTypeProtocolParserException
	 *             if the part type is not known
	 */
	ProtocolPacket parseClientPacket(ByteBuffer buffer)
			throws UnknownPacketProtocolParserException,
			UnknownPartTypeProtocolParserException;

	/**
	 * Parses an packet buffer into an {@link ProtocolPacket}
	 * 
	 * @param buffer
	 *            the packet contents
	 * @return the packet
	 * @throws UnknownPacketProtocolParserException
	 *             if the packet is not known
	 * @throws UnknownPartTypeProtocolParserException
	 *             if the part type is not known
	 */
	ProtocolPacket parseServerPacket(ByteBuffer buffer)
			throws UnknownPacketProtocolParserException,
			UnknownPartTypeProtocolParserException;
}
