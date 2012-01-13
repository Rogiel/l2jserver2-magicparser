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
package com.magicparser.util;

import com.magicparser.net.packet.Part;
import com.magicparser.net.packet.parts.ByteArrayPart;
import com.magicparser.net.packet.parts.BytePart;
import com.magicparser.net.packet.parts.DoublePart;
import com.magicparser.net.packet.parts.FloatPart;
import com.magicparser.net.packet.parts.ForPart;
import com.magicparser.net.packet.parts.IntegerPart;
import com.magicparser.net.packet.parts.LongPart;
import com.magicparser.net.packet.parts.StringPart;
import com.magicparser.net.packet.reader.ColorPartReader;
import com.magicparser.net.packet.reader.PartReader;
import com.rogiel.packetmagic.packet.ByteArrayPacketPartDescriptor;
import com.rogiel.packetmagic.packet.BytePacketPartDescriptor;
import com.rogiel.packetmagic.packet.ColorReaderDescriptor;
import com.rogiel.packetmagic.packet.DoublePacketPartDescriptor;
import com.rogiel.packetmagic.packet.FloatPacketPartDescriptor;
import com.rogiel.packetmagic.packet.ForPacketPartDescriptor;
import com.rogiel.packetmagic.packet.IntegerPacketPartDescriptor;
import com.rogiel.packetmagic.packet.LongPacketPartDescriptor;
import com.rogiel.packetmagic.packet.PacketPartDescriptor;
import com.rogiel.packetmagic.packet.ReaderDescriptor;
import com.rogiel.packetmagic.packet.StringPacketPartDescriptor;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 */
public class ProtocolUtils {
	/**
	 * @param descriptor
	 *            the packet part descriptor
	 * @return the packet part
	 */
	public static Part<?, ?> getPart(PacketPartDescriptor descriptor) {
		if (descriptor instanceof IntegerPacketPartDescriptor) {
			return new IntegerPart((IntegerPacketPartDescriptor) descriptor);
		} else if (descriptor instanceof ByteArrayPacketPartDescriptor) {
			return new ByteArrayPart((ByteArrayPacketPartDescriptor) descriptor);
		} else if (descriptor instanceof BytePacketPartDescriptor) {
			return new BytePart((BytePacketPartDescriptor) descriptor);
		} else if (descriptor instanceof StringPacketPartDescriptor) {
			return new StringPart((StringPacketPartDescriptor) descriptor);
		} else if (descriptor instanceof ForPacketPartDescriptor) {
			return new ForPart((ForPacketPartDescriptor) descriptor);
		} else if (descriptor instanceof DoublePacketPartDescriptor) {
			return new DoublePart((DoublePacketPartDescriptor) descriptor);
		} else if (descriptor instanceof LongPacketPartDescriptor) {
			return new LongPart((LongPacketPartDescriptor) descriptor);
		} else if (descriptor instanceof FloatPacketPartDescriptor) {
			return new FloatPart((FloatPacketPartDescriptor) descriptor);
		}
		return null;
	}

	/**
	 * @param descriptor
	 *            the reader descriptor
	 * @return the part reader
	 */
	public static PartReader<?, ?, ?> getPartReader(ReaderDescriptor descriptor) {
		if (descriptor instanceof ColorReaderDescriptor) {
			return new ColorPartReader((ColorReaderDescriptor) descriptor);
		}
		return null;
	}
}
