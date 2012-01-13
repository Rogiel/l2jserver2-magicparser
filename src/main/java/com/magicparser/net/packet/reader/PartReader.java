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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;

import com.magicparser.net.packet.Part;
import com.rogiel.packetmagic.packet.ReaderDescriptor;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public abstract class PartReader<D extends ReaderDescriptor, P extends Part<?, V>, V> {
	protected final D descriptor;

	public PartReader(D descriptor) {
		this.descriptor = descriptor;
	}

	public abstract Widget getWidget(P part, Display display, ViewerCell cell);
}
