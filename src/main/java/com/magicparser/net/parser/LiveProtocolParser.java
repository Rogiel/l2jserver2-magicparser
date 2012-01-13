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

import java.io.File;
import java.io.IOException;

import org.jnetpcap.Pcap;

import com.rogiel.packetmagic.packet.ProtocolDescriptor;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class LiveProtocolParser extends AbstractProtocolParser {
	/**
	 * @param descriptor
	 *            the protocol descriptor
	 * @param listener
	 *            the packet listener
	 * @param device
	 *            the the device to capture traffic from
	 * @throws IOException
	 *             if any error occur while reading the file
	 */
	public LiveProtocolParser(ProtocolDescriptor descriptor,
			PacketListener listener, String device) throws IOException {
		super(descriptor, listener, Pcap.openLive(device, 0, 0, 60,
				new StringBuilder()));
	}

	/**
	 * @param descriptor
	 *            the protocol descriptor
	 * @param listener
	 *            the packet listener
	 * @param device
	 *            the the device to capture traffic from
	 * @param dumpToFile
	 *            the file to dump the captured packets
	 */
	public LiveProtocolParser(ProtocolDescriptor descriptor,
			PacketListener listener, String device, File dumpToFile) {
		super(descriptor, listener, Pcap.openLive(device, 0, 0, 60,
				new StringBuilder()), dumpToFile);
		// TODO Auto-generated constructor stub
	}
}
