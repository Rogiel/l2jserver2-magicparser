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

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.wb.swt.SWTResourceManager;

import com.magicparser.gui.viewers.PartTreeContentProvider;
import com.magicparser.net.packet.Part;
import com.magicparser.net.packet.ProtocolPacket;
import com.magicparser.net.packet.parts.ForPart;
import com.magicparser.util.ByteUtils;
import com.magicparser.util.SWTUtils;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class PartComposite extends Composite {
	/**
	 * The SWT styled text for hex packet dump
	 */
	private final StyledText hexPacketData;
	/**
	 * The SWT styled text for plain packet dump
	 */
	private final StyledText plainPacketData;
	/**
	 * The SWT tree viewer that displays the parsed packet
	 */
	private final TreeViewer treeViewer;
	/**
	 * The scrolled packet composite
	 */
	private final ScrolledComposite scrolledComposite;
	/**
	 * The predefined hex opcode style range
	 */
	private final StyleRange hexOpcodeStyleRange;

	/**
	 * @param parent
	 *            the parent composite
	 */
	public PartComposite(final Composite parent) {
		super(parent, SWT.NONE);
		setLayout(new FillLayout(SWT.HORIZONTAL));

		SashForm sashForm = new SashForm(this, SWT.VERTICAL);

		scrolledComposite = new ScrolledComposite(sashForm, SWT.BORDER
				| SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.addMouseMoveListener(new MouseMoveListener() {
			@Override
			public void mouseMove(MouseEvent e) {
				scrolledComposite.forceFocus();
			}
		});

		Composite composite = new Composite(scrolledComposite, SWT.NONE);
		GridLayout gl_composite = new GridLayout(2, false);
		gl_composite.verticalSpacing = 0;
		gl_composite.marginWidth = 0;
		gl_composite.marginHeight = 0;
		gl_composite.horizontalSpacing = 0;
		composite.setLayout(gl_composite);
		scrolledComposite.setContent(composite);
		scrolledComposite.pack();

		hexPacketData = new StyledText(composite, SWT.READ_ONLY);
		GridData gd_hexPacketData = new GridData(SWT.FILL, SWT.FILL, false,
				true, 1, 1);
		gd_hexPacketData.widthHint = 340;
		gd_hexPacketData.minimumWidth = 340;
		hexPacketData.setSize(320, 234);
		hexPacketData.setLayoutData(gd_hexPacketData);
		hexPacketData.setEnabled(false);
		hexPacketData.setFont(SWTResourceManager.getFont("Courier New", 9,
				SWT.NORMAL));

		plainPacketData = new StyledText(composite, SWT.READ_ONLY);
		GridData gd_plainPacketData = new GridData(SWT.FILL, SWT.FILL, true,
				true, 1, 1);
		gd_plainPacketData.widthHint = 140;
		gd_hexPacketData.minimumWidth = 140;
		plainPacketData.setLayoutData(gd_plainPacketData);
		plainPacketData.setEnabled(false);
		plainPacketData.setFont(SWTResourceManager.getFont("Courier New", 9,
				SWT.NORMAL));

		Composite treeComposite = new Composite(sashForm, SWT.BORDER);
		GridLayout compositeLayout = new GridLayout(1, false);
		compositeLayout.verticalSpacing = 0;
		compositeLayout.marginWidth = 0;
		compositeLayout.marginHeight = 0;
		compositeLayout.horizontalSpacing = 0;
		treeComposite.setLayout(compositeLayout);

		treeViewer = new TreeViewer(treeComposite, SWT.NONE);
		treeViewer.setAutoExpandLevel(10);
		treeViewer.setUseHashlookup(true);
		Tree tree = treeViewer.getTree();
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		treeViewer.setContentProvider(new PartTreeContentProvider());
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			private final StyleRange styleRange = new StyleRange();
			private final StyleRange styleRange2 = new StyleRange();
			private Part<?, ?> lastPart;

			@Override
			public void selectionChanged(SelectionChangedEvent e) {
				final Object object = ((StructuredSelection) e.getSelection())
						.getFirstElement();
				if (object instanceof Part) {
					final Part<?, ?> part = (Part<?, ?>) object;
					if (styleRange.background != null)
						styleRange.background.dispose();
					styleRange.background = null;
					hexPacketData.setStyleRange(styleRange);
					if (styleRange2.background != null)
						styleRange2.background.dispose();
					styleRange2.background = null;
					plainPacketData.setStyleRange(styleRange2);

					if (lastPart != null)
						colorizePart(lastPart);
					lastPart = part;
					styleRange.start = part.getOffset() * 3;
					styleRange.length = part.getLength() * 3 - 1;
					styleRange.background = PartComposite.this.getDisplay()
							.getSystemColor(SWT.COLOR_RED);
					hexPacketData.setStyleRange(styleRange);

					styleRange2.start = part.getOffset();
					styleRange2.length = part.getLength();
					styleRange2.background = PartComposite.this.getDisplay()
							.getSystemColor(SWT.COLOR_RED);
					plainPacketData.setStyleRange(styleRange2);

					hexPacketData.setSelection(part.getOffset() * 3);
					hexPacketData.showSelection();

					plainPacketData.setSelection(part.getOffset());
					plainPacketData.showSelection();
				}
			}
		});

		TreeViewerColumn treeViewerColumn_1 = new TreeViewerColumn(treeViewer,
				SWT.NONE);
		// treeViewerColumn_1.setEditingSupport(new PartNameEditingSupport(
		// treeViewer));
		TreeColumn trclmnName = treeViewerColumn_1.getColumn();
		trclmnName.setWidth(170);
		trclmnName.setText("Name");
		treeViewerColumn_1.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element.getClass().isArray())
					return null;
				final Part<?, ?> part = (Part<?, ?>) element;
				return part.getName();
			}

			@Override
			public Image getImage(Object element) {
				if (element.getClass().isArray())
					return null;
				final Part<?, ?> part = (Part<?, ?>) element;
				return SWTUtils.loadImage(getDisplay(),
						"/icons/" + part.getIcon() + ".png");
			}
		});

		TreeViewerColumn treeViewerColumn_2 = new TreeViewerColumn(treeViewer,
				SWT.NONE);
		TreeColumn trclmnValue = treeViewerColumn_2.getColumn();
		trclmnValue.setWidth(138);
		trclmnValue.setText("Value");
		treeViewerColumn_2.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element.getClass().isArray())
					return null;
				final Part<?, ?> part = (Part<?, ?>) element;
				return part.getValueAsString();
			}
		});

		// TreeViewerColumn treeViewerColumn_3 = new
		// TreeViewerColumn(treeViewer,
		// SWT.NONE);
		// TreeColumn trclmnReadValue = treeViewerColumn_3.getColumn();
		// trclmnReadValue.setWidth(138);
		// trclmnReadValue.setText("Value");
		// treeViewerColumn_3.setLabelProvider(new CellLabelProvider() {
		// @Override
		// public void update(ViewerCell cell) {
		// final TreeItem item = (TreeItem) cell.getItem();
		//
		// final TreeEditor editor = new TreeEditor(item.getParent());
		// final Button button = new Button(item.getParent(), SWT.NONE);
		// // button.setText("Remove part");
		// button.setImage(SWTUtils.loadImage(item, "/icons/plus.png"));
		// editor.setEditor(button, item, cell.getColumnIndex());
		// button.pack();
		//
		// item.addDisposeListener(new DisposeListener() {
		// @Override
		// public void widgetDisposed(DisposeEvent e) {
		// editor.dispose();
		// }
		// });
		// item.getParent().add
		//
		// editor.grabHorizontal = true;
		// editor.minimumWidth = button.getSize().x;
		// editor.minimumHeight = button.getSize().y;
		// editor.horizontalAlignment = SWT.LEFT;
		// }
		// });

		sashForm.setWeights(new int[] { 1, 1 });

		hexOpcodeStyleRange = new StyleRange();
		hexOpcodeStyleRange.background = hexPacketData.getDisplay()
				.getSystemColor(SWT.COLOR_YELLOW);
		hexOpcodeStyleRange.start = 0;
		hexOpcodeStyleRange.length = 2;
		hexOpcodeStyleRange.fontStyle = SWT.BOLD;
	}

	/**
	 * Sets the currently selected packet
	 * 
	 * @param packet
	 *            the packet
	 */
	public void setPacket(ProtocolPacket packet) {
		hexPacketData.setText(ByteUtils.hexDump(packet.getContent()));
		hexPacketData.setStyleRange(hexOpcodeStyleRange);

		plainPacketData.setText(ByteUtils.printData(packet.getContent(), packet
				.getContent().remaining()));
		colorizeDump(packet);

		treeViewer.setInput(packet);
		scrolledComposite.setMinSize(scrolledComposite.getContent()
				.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	/**
	 * Colorizes the whole packet hex dump
	 * 
	 * @param packet
	 *            the packet
	 */
	public void colorizeDump(ProtocolPacket packet) {
		for (final Part<?, ?> part : packet.getParts()) {
			colorizePart(part);
		}
	}

	/**
	 * Colorizes the packet part
	 * 
	 * @param part
	 *            the part
	 */
	public void colorizePart(Part<?, ?> part) {
		if (part instanceof ForPart) {
			for (Part<?, ?>[] parts : ((ForPart) part).getValue()) {
				for (Part<?, ?> forPart : parts) {
					colorizePart(forPart);
				}
			}
		} else {
			if (part.getColor() == null)
				return;
			StyleRange styleRange = new StyleRange();
			styleRange.start = part.getOffset() * 3;
			styleRange.length = part.getLength() * 3 - 1;
			styleRange.background = new Color(hexPacketData.getDisplay(),
					part.getColor());
			hexPacketData.setStyleRange(styleRange);
		}
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
