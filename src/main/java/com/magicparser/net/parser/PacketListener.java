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

import com.magicparser.net.packet.ProtocolPacket;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public interface PacketListener {
	/**
	 * Notifies the listener that a new packet has been received on the chain.
	 * 
	 * @param session
	 *            the session
	 * @param packet
	 *            the packet
	 */
	void receivePacket(ProtocolSession session, ProtocolPacket packet);

	/**
	 * Notifies the listener that an exception was thrown on the protocol chain
	 * 
	 * @param e
	 *            the exception thrown at the parser level
	 * @return <code>true</code> if the parser should stop
	 */
	boolean onException(Throwable e);
}
