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

import org.eclipse.swt.graphics.RGB;

import com.rogiel.packetmagic.packet.DataPacketPartDescriptor;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * @param <P>
 *            the packet part descriptor
 * @param <V>
 *            this protocol part value type
 */
public abstract class DataPart<P extends DataPacketPartDescriptor, V> extends
		Part<P, V> {
	/**
	 * @param definition
	 *            the part definition
	 * @param length
	 *            the part length
	 * @param color
	 *            the hexdump color
	 * @param icon
	 *            the part icon
	 */
	public DataPart(P definition, int length, RGB color, String icon) {
		super(definition, length, color, icon);
	}
}
