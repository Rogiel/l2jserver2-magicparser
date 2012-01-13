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

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.magicparser.net.packet.Part;
import com.rogiel.packetmagic.packet.PacketPartDescriptor;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class PartValueReaderEditingSupport extends EditingSupport {
	/**
	 * @param viewer
	 *            the column viewer
	 */
	public PartValueReaderEditingSupport(ColumnViewer viewer) {
		super(viewer);
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		if (element instanceof Part) {
			final PacketPartDescriptor descriptor = ((Part<?, ?>) element)
					.getDescriptor();
			
			
			TextCellEditor cellEditor = new TextCellEditor(
					(Composite) getViewer().getControl(), SWT.NONE);
			cellEditor.setValue(((Part<?, ?>) element).getName());
			return cellEditor;
		}
		return null;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		if (element instanceof Part) {
			Part<?, ?> part = (Part<?, ?>) element;
			return part.getName();
		}
		return null;
	}

	@Override
	protected void setValue(Object element, Object value) {
		if (element instanceof Part) {
			PacketPartDescriptor descriptor = ((Part<?, ?>) element)
					.getDescriptor();
			descriptor.setName((String) value);
			getViewer().refresh(element);
		}
	}
}
