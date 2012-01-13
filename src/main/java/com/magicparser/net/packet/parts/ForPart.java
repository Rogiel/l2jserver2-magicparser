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
import java.util.ArrayList;
import java.util.List;

import com.magicparser.net.packet.DataPart;
import com.magicparser.net.packet.Part;
import com.magicparser.net.packet.ReferencedPart;
import com.magicparser.util.ProtocolUtils;
import com.rogiel.packetmagic.packet.ForPacketPartDescriptor;
import com.rogiel.packetmagic.packet.PacketPartDescriptor;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class ForPart extends
		ReferencedPart<ForPacketPartDescriptor, Part<?, ?>[][]> {
	/**
	 * @param part
	 *            the integer packet part
	 */
	public ForPart(ForPacketPartDescriptor part) {
		super(part, "for");
	}

	@Override
	public void readImpl(ByteBuffer buffer) {
		final int loops = ((Number) reference.getValue()).intValue();
		value = new DataPart<?, ?>[loops][0];
		for (int i = 0; i < loops; i++) {
			final List<DataPart<?, ?>> parts = new ArrayList<DataPart<?, ?>>();
			for (final PacketPartDescriptor part : descriptor.getParts()) {
				final DataPart<?, ?> protocolPart = (DataPart<?, ?>) ProtocolUtils
						.getPart(part);
				if (protocolPart == null)
					return;
				protocolPart.read(buffer);
				parts.add(protocolPart);
			}
			value[i] = parts.toArray(new DataPart<?, ?>[parts.size()]);
		}
	}

	@Override
	public String getValueAsString() {
		final StringBuilder builder = new StringBuilder(reference.getName())
				.append(" (").append('\n');
		for (final Part<?, ?>[] parts : value) {
			for (final Part<?, ?> part : parts) {
				builder.append('\t').append('\t').append(part.toString())
						.append('\n');
			}
		}
		return builder.append('\t').append(")").toString();
	}

	@Override
	public int getLength() {
		int length = 0;
		for (final Part<?, ?>[] parts : value) {
			for (final Part<?, ?> part : parts) {
				length += part.getLength();
			}
		}
		return length;
	}
}
