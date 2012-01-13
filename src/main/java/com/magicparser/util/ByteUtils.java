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

import java.nio.ByteBuffer;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class ByteUtils {
	/**
	 * Returns the hex dump of the given byte array
	 * 
	 * @param b
	 *            the byte array
	 * @return A string with the hex dump
	 */
	public static String rawHexDump(byte... b) {
		if (b == null)
			return "";

		int size = b.length;
		if (size == 0)
			return "";

		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < size; i++) {
			buf.append(zeropad(Integer.toHexString(byteToUInt(b[i]))
					.toUpperCase(), 2));
			buf.append(" ");
		}
		buf.delete(buf.length() - 1, buf.length());
		return buf.toString();
	}

	/**
	 * Returns the hex dump of the given byte array as 16 bytes per line
	 * 
	 * @param b
	 *            the byte array
	 * @return A string with the hex dump
	 */
	public static String hexDump(byte... b) {
		if (b == null)
			return "";
		StringBuffer buf = new StringBuffer();
		int size = b.length;
		for (int i = 0; i < size; i++) {
			if ((i + 1) % 16 == 0) {
				buf.append(zeropad(Integer.toHexString(byteToUInt(b[i]))
						.toUpperCase(), 2));
				buf.append('\n');
			} else {
				buf.append(zeropad(Integer.toHexString(byteToUInt(b[i]))
						.toUpperCase(), 2));
				buf.append(" ");
			}
		}
		return buf.toString();
	}

	/**
	 * Returns the hex dump of the given byte array as 16 bytes per line
	 * 
	 * @param buffer
	 *            the byte buffer
	 * @return A string with the hex dump
	 */
	public static String hexDump(ByteBuffer buffer) {
		if (buffer == null)
			return "";
		StringBuffer buf = new StringBuffer();
		int size = buffer.remaining();
		for (int i = 0; i < size; i++) {
			if ((i + 1) % 16 == 0) {
				buf.append(zeropad(
						Integer.toHexString(
								byteToUInt(buffer.get(buffer.position() + i)))
								.toUpperCase(), 2));
				buf.append('\n');
			} else {
				buf.append(zeropad(
						Integer.toHexString(
								byteToUInt(buffer.get(buffer.position() + i)))
								.toUpperCase(), 2));
				buf.append(" ");
			}
		}
		return buf.toString();
	}

	/**
	 * Returns the unsigned value of a byte
	 * 
	 * @param the
	 *            byte witch u want to convert
	 */
	public static int byteToUInt(byte b) {
		return b & 0xFF;
	}

	public static String zeropad(String number, int size) {
		if (number.length() >= size)
			return number;
		return repeat("0", size - number.length()) + number;
	}

	public static String repeat(String str, int repeat) {
		StringBuffer buf = new StringBuffer();

		for (int i = 0; i < repeat; i++)
			buf.append(str);

		return buf.toString();
	}

	public static byte[] readBytes(ByteBuffer buffer, int len) {
		final byte[] data = new byte[len];
		buffer.get(data);
		return data;
	}

	public static String printData(ByteBuffer buffer, int len) {
		StringBuilder result = new StringBuilder();

		int counter = 0;
		int length = buffer.remaining();

		for (int i = 0; i < len; i++) {
//			if (counter % 16 == 0) {
//				result.append(fillHex(i, 4) + ": ");
//			}

			//result.append(fillHex(buffer.get(i) & 0xff, 2) + " ");
			counter++;
			if (counter == 16) {
				//result.append("   ");

				int charpoint = i - 15;
				for (int a = 0; a < 16; a++) {
					int t1 = buffer.get(charpoint++);
					if (t1 > 0x1f && t1 < 0x80) {
						result.append((char) t1);
					} else {
						result.append('.');
					}
				}

				result.append('\n');
				counter = 0;
			}
		}

		int rest = length % 16;
		if (rest > 0) {
			for (int i = 0; i < 17 - rest; i++) {
				//result.append("   ");
			}

			int charpoint = length - rest;
			for (int a = 0; a < rest; a++) {
				int t1 = buffer.get(charpoint++);
				if (t1 > 0x1f && t1 < 0x80) {
					result.append((char) t1);
				} else {
					result.append('.');
				}
			}

			result.append('\n');
		}

		return result.toString();
	}
	
	public static String fillHex(int data, int digits)
	{
		String number = Integer.toHexString(data);
		
		for (int i=number.length(); i< digits; i++)
		{
			number = "0" + number;
		}
		
		return number;
	}

	public static String toAnsci(byte[] data, int from, int to)
	{
		StringBuilder result = new StringBuilder();

		for(int i = from; i < to; i++)
		{
			int t1 = data[i];
			if (t1 > 0x1f && t1 < 0x80)
			{
				result.append((char)t1);
			}
			else
			{
				result.append('.');
			}
		}
		return result.toString();
	}
}
