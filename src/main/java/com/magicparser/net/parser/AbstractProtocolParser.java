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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

import org.jnetpcap.Pcap;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;

import com.magicparser.net.ProtocolPacketParser;
import com.magicparser.net.impl.ProtocolPacketParserImpl;
import com.magicparser.net.packet.ProtocolPacket;
import com.rogiel.packetmagic.packet.ProtocolDescriptor;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public abstract class AbstractProtocolParser implements Runnable {
	/**
	 * The JPcap captor
	 */
	private final Pcap captor;
	/**
	 * The protocol descriptor
	 */
	private final ProtocolDescriptor descriptor;
	/**
	 * The packet parser
	 */
	private final ProtocolPacketParser parser;
	/**
	 * The packet listener
	 */
	private final PacketListener listener;
	/**
	 * The where all packets in the protocol are dumped.
	 */
	private final File dumpToFile;
	/**
	 * The list of all active sessions
	 */
	private final Map<Long, ProtocolSession> sessions = new HashMap<Long, ProtocolSession>();

	/**
	 * @param descriptor
	 *            the protocol descriptor
	 * @param listener
	 *            the packet listener
	 * @param captor
	 *            the packet captor instance
	 */
	public AbstractProtocolParser(ProtocolDescriptor descriptor,
			PacketListener listener, Pcap captor) {
		this.descriptor = descriptor;
		this.parser = new ProtocolPacketParserImpl(descriptor);
		this.captor = captor;
		this.listener = listener;
		this.dumpToFile = null;
	}

	/**
	 * @param descriptor
	 *            the protocol descriptor
	 * @param listener
	 *            the packet listener
	 * @param captor
	 *            the packet captor instance
	 * @param dumpToFile
	 *            the file to dump all captured packets
	 */
	public AbstractProtocolParser(ProtocolDescriptor descriptor,
			PacketListener listener, Pcap captor, File dumpToFile) {
		this.descriptor = descriptor;
		this.parser = new ProtocolPacketParserImpl(descriptor);
		this.captor = captor;
		this.listener = listener;
		this.dumpToFile = dumpToFile;
	}

	@Override
	public void run() {
		final FileChannel channel;
		if (dumpToFile != null) {
			try {
				channel = new FileOutputStream(dumpToFile).getChannel();
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
		} else {
			channel = null;
		}

		captor.loop(-1, new PcapPacketHandler<Object>() {
			private final Ip4 ip = new Ip4();
			private final Tcp tcp = new Tcp();

			@Override
			public void nextPacket(PcapPacket packet, Object user) {
				if (!(packet.hasHeader(ip) && packet.hasHeader(tcp)))
					return;
				if (!(tcp.source() == descriptor.getPort() || tcp.destination() == descriptor
						.getPort()))
					return;
				try {
					if (tcp.flags_SYN()) {
						createSession(tcp.source() * tcp.destination())
								.receivePacket(tcp, ip);
						return;
					} else if (tcp.flags_RST()) {
						destroySession(getSession(tcp.source()
								* tcp.destination()));
						return;
					} else {
						final ProtocolSession session = getSession(tcp.source()
								* tcp.destination());
						final ProtocolPacket[] protocolPackets = session
								.receivePacket(tcp, ip);
						if (protocolPackets == null)
							return;
						for (final ProtocolPacket protocolPacket : protocolPackets) {
							listener.receivePacket(session, protocolPacket);
						}
					}
				} catch (Throwable e) {
					if (listener.onException(e))
						return;
				}
			}
		}, null);
		// captor.loop(-1, new IpReassembly(5 * 1000, new IpReassemblyHandler()
		// {
		// private final Ip4 ip = new Ip4();
		// private final Tcp tcp = new Tcp();
		//
		// @Override
		// public void nextPacket(JMemoryPacket packet, PcapHeader header,
		// JBuffer buffer) {
		// if (!(packet.hasHeader(ip) && packet.hasHeader(tcp)))
		// return;
		// if (!(tcp.source() == descriptor.getPort() || tcp.destination() ==
		// descriptor
		// .getPort()))
		// return;
		// try {
		// if (tcp.flags_SYN()) {
		// createSession(tcp.source() * tcp.destination())
		// .receivePacket(channel, tcp, ip);
		// return;
		// } else if (tcp.flags_RST()) {
		// destroySession(getSession(tcp.source()
		// * tcp.destination()));
		// return;
		// } else {
		// final ProtocolSession session = getSession(tcp.source()
		// * tcp.destination());
		// final ProtocolPacket[] protocolPackets = session
		// .receivePacket(channel, tcp, ip);
		// if (protocolPackets == null)
		// return;
		// for (final ProtocolPacket protocolPacket : protocolPackets) {
		// listener.receivePacket(session, protocolPacket);
		// }
		// }
		// } catch (Throwable e) {
		// if (listener.onException(e))
		// return;
		// }
		// }
		// }), null);
	}

	/**
	 * Tries to locate an existing TCP session. If no session is found,
	 * <code>null</code> is returned.
	 * 
	 * @param sessionId
	 *            the session id
	 * @return the protocol session
	 */
	private ProtocolSession getSession(long sessionId) {
		return sessions.get(sessionId);
	}

	/**
	 * Creates a new TCP session
	 * 
	 * @param sessionId
	 *            the session id
	 * @return the protocol session
	 */
	private ProtocolSession createSession(long sessionId) {
		ProtocolSession session = sessions.get(sessionId);
		if (session == null) {
			session = new ProtocolSession(sessionId, descriptor, parser);
			sessions.put(sessionId, session);
		}
		return session;
	}

	/**
	 * Destroys an TCP session
	 * 
	 * @param session
	 *            the tcp session
	 */
	private void destroySession(ProtocolSession session) {
		sessions.remove(session.getID());
	}
}
