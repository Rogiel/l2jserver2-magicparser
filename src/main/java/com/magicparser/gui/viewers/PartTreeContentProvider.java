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
package com.magicparser.gui.viewers;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.magicparser.net.packet.ProtocolPacket;
import com.magicparser.net.packet.parts.ForPart;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class PartTreeContentProvider implements ITreeContentProvider {
	@Override
	public Object[] getElements(Object parent) {
		return getChildren(parent);
	}

	@Override
	public Object[] getChildren(Object obj) {
		if (obj instanceof ProtocolPacket) {
			return ((ProtocolPacket) obj).getParts();
		} else if (obj instanceof ForPart) {
			return ((ForPart) obj).getValue();
		} else if (obj.getClass().isArray()) {
			return (Object[]) obj;
		}
		return null;
	}

	@Override
	public Object getParent(Object obj) {
		return null;
	}

	@Override
	public boolean hasChildren(Object obj) {
		if (obj instanceof ProtocolPacket) {
			return true;
		} else if (obj instanceof ForPart) {
			return true;
		} else if (obj.getClass().isArray()) {
			return true;
		}
		return false;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@Override
	public void dispose() {
	}
}
