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
package com.magicparser.net.packet.parts;

import java.nio.ByteBuffer;

import org.eclipse.swt.graphics.RGB;

import com.magicparser.net.packet.DataPart;
import com.rogiel.packetmagic.packet.LongPacketPartDescriptor;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public class LongPart extends DataPart<LongPacketPartDescriptor, Long> {
	/**
	 * @param part
	 *            the packet part
	 */
	public LongPart(LongPacketPartDescriptor part) {
		super(part, 8, new RGB(153, 102, 153), "Q");
	}

	@Override
	public void readImpl(ByteBuffer buffer) {
		value = buffer.getLong();
	}
}
