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

import org.eclipse.swt.graphics.RGB;

import com.rogiel.packetmagic.packet.PacketPartDescriptor;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * @param <P>
 *            the packet part descriptor
 * @param <V>
 *            this protocol part value type
 */
public abstract class Part<P extends PacketPartDescriptor, V> {
	/**
	 * The packet part definition
	 */
	protected final P descriptor;
	/**
	 * The RGB color that that represents this part
	 */
	protected final RGB color;
	/**
	 * The icon name
	 */
	protected final String icon;
	/**
	 * The packet value
	 */
	protected V value;
	/**
	 * The part offset inside the buffer
	 */
	protected int offset;
	/**
	 * The data length
	 */
	protected int length;

	/**
	 * @param definition
	 *            the data part
	 * @param color
	 *            the hexdump color
	 * @param icon
	 *            the part icon
	 */
	public Part(final P definition, RGB color, String icon) {
		this.descriptor = definition;
		this.color = color;
		this.icon = icon;
	}

	/**
	 * @param definition
	 *            the data part
	 * @param length
	 *            the data length
	 * @param color
	 *            the hexdump color
	 * @param icon
	 *            the part icon
	 */
	public Part(final P definition, int length, RGB color, String icon) {
		this.descriptor = definition;
		this.length = length;
		this.color = color;
		this.icon = icon;
	}

	/**
	 * @param buffer
	 *            the data buffer
	 */
	public final void read(ByteBuffer buffer) {
		offset = buffer.position();
		readImpl(buffer);
	}

	/**
	 * @param buffer
	 *            the data buffer
	 */
	public abstract void readImpl(ByteBuffer buffer);

	/**
	 * @return the part name
	 */
	public String getName() {
		return descriptor.getName();
	}

	/**
	 * @return the value
	 */
	public final V getValue() {
		return value;
	}

	/**
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * @return the data length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @return the color
	 */
	public RGB getColor() {
		return color;
	}

	/**
	 * @return the icon
	 */
	public String getIcon() {
		return icon;
	}

	/**
	 * @return the part id. Possibly <code>null</code>.
	 */
	public String getID() {
		return descriptor.getId();
	}

	/**
	 * @return the value string representation
	 */
	public String getValueAsString() {
		if (value == null)
			return "null";
		return value.toString();
	}

	/**
	 * @return the descriptor
	 */
	public P getDescriptor() {
		return descriptor;
	}

	@Override
	public final String toString() {
		return new StringBuilder(descriptor.getName()).append(": ")
				.append(getValueAsString()).toString();
	}
}
