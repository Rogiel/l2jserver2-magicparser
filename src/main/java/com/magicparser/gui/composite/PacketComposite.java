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
package com.magicparser.gui.composite;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.magicparser.net.packet.ProtocolPacket;
import com.magicparser.net.packet.ProtocolPacket.PacketDirection;
import com.magicparser.util.ByteUtils;
import com.magicparser.util.SWTUtils;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class PacketComposite extends Composite {
	/**
	 * The part panel
	 */
	private final PartComposite partComposite;
	/**
	 * The SWT table
	 */
	private final Table table;
	private final PacketContentProvider tableContentProvider;
	/**
	 * The JFace table viewer
	 */
	private final TableViewer tableViewer;
	private Text packetSearchBox;

	/**
	 * @param parent
	 *            the parent composite
	 * @param partComposite
	 *            the part composite
	 */
	public PacketComposite(final Composite parent, PartComposite partComposite) {
		super(parent, SWT.NONE);
		this.partComposite = partComposite;
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
		setLayout(gridLayout);

		Composite searchComposite = new Composite(this, SWT.NONE);
		GridLayout gl_searchComposite = new GridLayout(1, false);
		gl_searchComposite.verticalSpacing = 0;
		gl_searchComposite.marginWidth = 0;
		gl_searchComposite.marginHeight = 0;
		gl_searchComposite.horizontalSpacing = 0;
		searchComposite.setLayout(gl_searchComposite);

		packetSearchBox = new Text(searchComposite, SWT.BORDER);
		GridData gd_packetSearchBox = new GridData(SWT.LEFT, SWT.CENTER, false,
				false, 1, 1);
		gd_packetSearchBox.widthHint = 161;
		packetSearchBox.setLayoutData(gd_packetSearchBox);
		packetSearchBox.setText("Search for packets...");
		packetSearchBox.setBounds(0, 0, 76, 21);

		packetSearchBox.addFocusListener(new FocusListener() {
			private final String defaultValue = packetSearchBox.getText();

			@Override
			public void focusGained(FocusEvent e) {
				final String value = packetSearchBox.getText();
				if (value.equals(defaultValue))
					packetSearchBox.setText("");
			}

			@Override
			public void focusLost(FocusEvent e) {
				final String value = packetSearchBox.getText();
				if (value.equals(""))
					packetSearchBox.setText(defaultValue);
			}
		});

		tableViewer = new TableViewer(this, SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.setHeaderVisible(true);
		tableViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					@Override
					public void selectionChanged(SelectionChangedEvent e) {
						final ProtocolPacket packet = (ProtocolPacket) ((StructuredSelection) e
								.getSelection()).getFirstElement();
						if (packet != null)
							PacketComposite.this.partComposite
									.setPacket(packet);
					}
				});
		tableContentProvider = new PacketContentProvider();
		tableViewer.setContentProvider(tableContentProvider);
		tableViewer.setInput(new Object());
		packetSearchBox.addModifyListener(new ModifyListener() {
			private final String defaultValue = packetSearchBox.getText();

			@Override
			public void modifyText(ModifyEvent e) {
				final String query = packetSearchBox.getText();
				if (query.isEmpty() || query.equals(defaultValue)) {
					tableViewer.resetFilters();
					return;
				}
				final ViewerFilter filter = new ViewerFilter() {
					@Override
					public boolean select(Viewer viewer, Object parentElement,
							Object element) {
						if (element instanceof ProtocolPacket) {
							final ProtocolPacket packet = (ProtocolPacket) element;
							if (packet.getDescritor().getName() != null) {
								return packet.getDescritor().getName()
										.toLowerCase()
										.startsWith(query.toLowerCase());
							}
						}
						return false;
					}
				};
				tableViewer.resetFilters();
				tableViewer.addFilter(filter);
				tableViewer.refresh();
			}
		});
		
		final ViewerFilter filter = new ViewerFilter() {
			@Override
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {
				if (element instanceof ProtocolPacket) {
					final ProtocolPacket packet = (ProtocolPacket) element;
					if(packet.getDescritor() == null)
						return false;
					if (packet.getDescritor().getName() != null) {
						return packet.getDescritor().getName()
								.toLowerCase()
								.startsWith("SM_HTML");
					}
				}
				return false;
			}
		};
		tableViewer.addFilter(filter);

		TableViewerColumn tableViewerColumn = new TableViewerColumn(
				tableViewer, SWT.NONE);
		TableColumn tblclmnId = tableViewerColumn.getColumn();
		tblclmnId.setResizable(false);
		tblclmnId.setWidth(60);
		tblclmnId.setText("ID");
		tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				final ProtocolPacket packet = (ProtocolPacket) element;
				return ByteUtils.rawHexDump(packet.getDescritor().getOpcode());
			}

			@Override
			public Image getImage(Object element) {
				final ProtocolPacket packet = (ProtocolPacket) element;
				if (packet.getDirection() == PacketDirection.CLIENT) {
					return SWTUtils.loadImage(PacketComposite.this,
							"/icons/fromClient.png");
				} else {
					return SWTUtils.loadImage(PacketComposite.this,
							"/icons/fromServer.png");
				}
			}
		});

		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(
				tableViewer, SWT.NONE);
		TableColumn tblclmnName = tableViewerColumn_1.getColumn();
		tblclmnName.setResizable(false);
		tblclmnName.setWidth(324);
		tblclmnName.setText("Name");
		tableViewerColumn_1.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				final ProtocolPacket packet = (ProtocolPacket) element;
				return packet.getDescritor().getName();
			}
		});

		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(
				tableViewer, SWT.NONE);
		TableColumn tblclmnLength = tableViewerColumn_2.getColumn();
		tblclmnLength.setResizable(false);
		tblclmnLength.setWidth(100);
		tblclmnLength.setText("Length");
		tableViewerColumn_2.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				final ProtocolPacket packet = (ProtocolPacket) element;
				return Integer.toString(packet.getContent().remaining());
			}
		});
	}

	/**
	 * Adds a new packet to the table
	 * 
	 * @param packet
	 *            the packet to be added
	 */
	public void addPacket(final ProtocolPacket packet) {
		table.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				tableContentProvider.addPacket(packet);
			}
		});
	}

	/**
	 * Removes an existing packet from the table
	 * 
	 * @param packet
	 *            the packet to be removed
	 */
	public void removePacket(final ProtocolPacket packet) {
		table.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				tableContentProvider.removePacket(packet);
			}
		});
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	private class PacketContentProvider implements IStructuredContentProvider {
		private final List<ProtocolPacket> packets = new ArrayList<ProtocolPacket>();

		@Override
		public Object[] getElements(Object inputElement) {
			return packets.toArray();
		}

		/**
		 * Adds a new packet to the table
		 * 
		 * @param packet
		 *            the packet to be added
		 */
		public void addPacket(final ProtocolPacket packet) {
			packets.add(packet);
			tableViewer.add(packet);
		}

		/**
		 * Removes an existing packet from the table
		 * 
		 * @param packet
		 *            the packet to be removed
		 */
		public void removePacket(final ProtocolPacket packet) {
			packets.remove(packet);
			tableViewer.remove(packet);
		}

		@Override
		public void dispose() {
			packets.clear();
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}
}
