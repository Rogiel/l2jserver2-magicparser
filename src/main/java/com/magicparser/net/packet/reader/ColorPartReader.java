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
package com.magicparser.net.packet.reader;

import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;

import com.magicparser.net.packet.parts.IntegerPart;
import com.rogiel.packetmagic.packet.ColorReaderDescriptor;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class ColorPartReader extends
		PartReader<ColorReaderDescriptor, IntegerPart, Integer> {
	/**
	 * @param descriptor
	 *            the reader descriptor
	 */
	public ColorPartReader(ColorReaderDescriptor descriptor) {
		super(descriptor);
	}

	@Override
	public Widget getWidget(IntegerPart part, Display display, ViewerCell cell) {
		//final Composite composite = new Composite(parent, SWT.NONE);

		int r = (part.getValue()) & 0xFF;
		int g = (part.getValue() >> 8) & 0xFF;
		int b = (part.getValue() >> 16) & 0xFF;

		cell.setBackground(new Color(display, r, g, b));
		return null;
	}

}
