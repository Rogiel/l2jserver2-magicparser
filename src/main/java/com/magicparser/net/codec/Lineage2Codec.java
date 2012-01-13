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
package com.magicparser.net.codec;

import java.nio.ByteBuffer;
import java.util.Arrays;

import com.magicparser.net.packet.ProtocolPacket;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class Lineage2Codec implements ProtocolCodec {
	/**
	 * The cryptography keys
	 */
	private byte[] key;

	@Override
	public void decode(ByteBuffer buffer) {
		if (key == null)
			return;

		int temp = 0;
		for (int i = 0; i < buffer.remaining(); i++) {
			int temp2 = buffer.get(buffer.position() + i) & 0xFF;
			buffer.put(buffer.position() + i,
					(byte) (temp2 ^ key[i & 15] ^ temp));
			temp = temp2;
		}

		int old = key[8] & 0xff;
		old |= key[9] << 8 & 0xff00;
		old |= key[10] << 0x10 & 0xff0000;
		old |= key[11] << 0x18 & 0xff000000;

		old += buffer.remaining();

		key[8] = (byte) (old & 0xff);
		key[9] = (byte) (old >> 0x08 & 0xff);
		key[10] = (byte) (old >> 0x10 & 0xff);
		key[11] = (byte) (old >> 0x18 & 0xff);
	}

	@Override
	public void receivedKey(ProtocolPacket keyPacket) {
		final byte[] original = (byte[]) keyPacket.getPart("key").getValue();
		key = Arrays.copyOf(original, 16);
		key[8] = (byte) 0xc8;
		key[9] = (byte) 0x27;
		key[10] = (byte) 0x93;
		key[11] = (byte) 0x01;
		key[12] = (byte) 0xa1;
		key[13] = (byte) 0x6c;
		key[14] = (byte) 0x31;
		key[15] = (byte) 0x97;
	}
}
