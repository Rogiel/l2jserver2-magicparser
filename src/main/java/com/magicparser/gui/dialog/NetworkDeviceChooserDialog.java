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
package com.magicparser.gui.dialog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class NetworkDeviceChooserDialog extends TitleAreaDialog {
	/**
	 * The device interface
	 */
	private PcapIf device;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public NetworkDeviceChooserDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("Select interface");
		setMessage("Select the device you want to use to capture packets straight from the network stream.");
		
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		container.setLayout(new FormLayout());

		Control last = null;
		final List<PcapIf> devices = new ArrayList<PcapIf>();
		Pcap.findAllDevs(devices, new StringBuilder());
		for (final PcapIf device : devices) {
			Button deviceButton = new Button(container, SWT.RADIO);
			FormData deviceButtonLayoutData = new FormData();
			deviceButtonLayoutData.top = new FormAttachment(last, 6);
			deviceButtonLayoutData.left = new FormAttachment(0, 10);
			deviceButton.setLayoutData(deviceButtonLayoutData);
			deviceButton.setText(device.getDescription());
			deviceButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					NetworkDeviceChooserDialog.this.device = device;
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			});
			last = deviceButton;
		}
		container.pack();

		return container;
	}
	
	@Override
	public void setTitle(String newTitle) {
		super.setTitle(newTitle);
		getShell().setText(newTitle);
	}

	/**
	 * @return the device
	 */
	public PcapIf getDevice() {
		return device;
	}

	@Override
	protected void okPressed() {
		if (device == null) {

		} else {
			super.okPressed();
		}
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}
}
