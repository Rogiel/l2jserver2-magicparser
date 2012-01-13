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
package com.magicparser.gui;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXB;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.jnetpcap.PcapIf;

import com.magicparser.gui.composite.PacketComposite;
import com.magicparser.gui.composite.PartComposite;
import com.magicparser.gui.dialog.NetworkDeviceChooserDialog;
import com.magicparser.net.packet.ProtocolPacket;
import com.magicparser.net.packet.ProtocolPacket.PacketDirection;
import com.magicparser.net.parser.AbstractProtocolParser;
import com.magicparser.net.parser.FileProtocolParser;
import com.magicparser.net.parser.LiveProtocolParser;
import com.magicparser.net.parser.PacketListener;
import com.magicparser.net.parser.ProtocolSession;
import com.magicparser.util.SWTUtils;
import com.rogiel.packetmagic.packet.ProtocolDescriptor;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class MainWindow {
	/**
	 * This window Shell
	 */
	private final Shell shell;
	/**
	 * All protocol sessions created
	 */
	private final Map<ProtocolSession, UISessionEntry> sessions = new HashMap<ProtocolSession, UISessionEntry>();
	/**
	 * The part panel
	 */
	private final PartComposite partComposite;
	/**
	 * The packet tab folder
	 */
	private final TabFolder packetTabs;
//	/**
//	 * @wbp.nonvisual location=183,19
//	 */
//	private final TrayItem trayItem;
	/**
	 * The packet listener
	 */
	private final PacketListener packetListener = new UIPacketListener();
	/**
	 * The progress bar
	 */
	private final ProgressBar progressBar;
	private int serverPackets = 0;
	private final Label serverPacketsCountLabel;
	private int clientPackets = 0;
	private final Label clientPacketsCountLabel;

	/**
	 * @param display
	 *            the SWT display
	 */
	public MainWindow(Display display) {
		shell = new Shell(display);
		shell.setCapture(true);
		shell.setMinimumSize(1000, 600);
		shell.setText("SWT Application");
		GridLayout shellLayout = new GridLayout(1, false);
		shellLayout.verticalSpacing = 0;
		shellLayout.marginWidth = 0;
		shellLayout.horizontalSpacing = 0;
		shellLayout.marginHeight = 0;
		shell.setLayout(shellLayout);

//		trayItem = new TrayItem(display.getSystemTray(), SWT.NONE);
//		trayItem.setText("MagicParser");
//		trayItem.setVisible(true);
//		trayItem.setImage(SWTUtils.loadImage(trayItem, "/icons/packet.png"));
//		trayItem.addSelectionListener(new SelectionListener() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				if (shell.isVisible()) {
//					shell.setVisible(false);
//				} else {
//					shell.setVisible(true);
//				}
//			}
//
//			@Override
//			public void widgetDefaultSelected(SelectionEvent e) {
//				widgetSelected(e);
//			}
//		});
//		final Menu trayMenu = new Menu(shell, SWT.POP_UP);
//		MenuItem exitMenuItem = new MenuItem(trayMenu, SWT.PUSH);
//		exitMenuItem.setText("Exit");
//		exitMenuItem.addSelectionListener(new SelectionListener() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				shell.dispose();
//			}
//
//			@Override
//			public void widgetDefaultSelected(SelectionEvent e) {
//				widgetSelected(e);
//			}
//		});
//		final MenuItem openMenuItem = new MenuItem(trayMenu, SWT.PUSH);
//		openMenuItem.setText("Open");
//		openMenuItem.addSelectionListener(new SelectionListener() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				if (!shell.isVisible())
//					shell.setVisible(true);
//			}
//
//			@Override
//			public void widgetDefaultSelected(SelectionEvent e) {
//				widgetSelected(e);
//			}
//		});
//
//		trayItem.addMenuDetectListener(new MenuDetectListener() {
//			@Override
//			public void menuDetected(MenuDetectEvent e) {
//				openMenuItem.setEnabled(!shell.isVisible());
//				trayMenu.setVisible(true);
//			}
//		});

		Menu mainMenu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(mainMenu);

		MenuItem fileMenu = new MenuItem(mainMenu, SWT.CASCADE);
		fileMenu.setText("File");

		Menu fileMenuEntry = new Menu(fileMenu);
		fileMenu.setMenu(fileMenuEntry);

		MenuItem openDumpMenuEntry = new MenuItem(fileMenuEntry, SWT.NONE);
		openDumpMenuEntry.setText("Open dump");
		openDumpMenuEntry.setImage(SWTUtils.loadImage(openDumpMenuEntry,
				"/icons/plus.png"));
		openDumpMenuEntry.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(shell, SWT.OPEN);
				fd.setText("Open pcap dump");
				// fd.setFilterPath("C:/");
				String[] filterExt = { "*.pcap", "*.psl" };
				fd.setFilterExtensions(filterExt);
				String selected = fd.open();
				if (selected == null)
					return;
				startParser(new File(selected));
			}
		});

		MenuItem liveCaptureMenuEntry = new MenuItem(fileMenuEntry, SWT.NONE);
		liveCaptureMenuEntry.setText("Live capture");
		liveCaptureMenuEntry.setImage(SWTUtils.loadImage(liveCaptureMenuEntry,
				"/icons/plus.png"));
		liveCaptureMenuEntry.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final NetworkDeviceChooserDialog deviceChooser = new NetworkDeviceChooserDialog(
						shell);
				deviceChooser.setBlockOnOpen(true);
				if (deviceChooser.open() == Window.OK) {
					final PcapIf device = deviceChooser.getDevice();
					System.out.println(device);
					startParser(device);
				}
			}
		});

		SashForm sashForm = new SashForm(shell, SWT.NONE);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));
		packetTabs = new TabFolder(sashForm, SWT.NONE);
		partComposite = new PartComposite(sashForm);

		sashForm.setWeights(new int[] { 25, 25 });

		DropTarget dropTarget = new DropTarget(shell, DND.DROP_MOVE);
		dropTarget.setTransfer(new Transfer[] { FileTransfer.getInstance() });

		Composite statusBar = new Composite(shell, SWT.NONE);
		statusBar.setVisible(true);
		statusBar.setLayout(new GridLayout(4, false));
		GridData gd_statusBar = new GridData(SWT.FILL, SWT.FILL, true, false,
				1, 1);
		gd_statusBar.widthHint = 0;
		statusBar.setLayoutData(gd_statusBar);
		
				Composite serverPackets_1 = new Composite(statusBar, SWT.NONE);
				serverPackets_1.setLayout(new RowLayout(SWT.HORIZONTAL));
				
						Label serverPacketsIcon = new Label(serverPackets_1, SWT.NONE);
						serverPacketsIcon.setImage(SWTUtils.loadImage(serverPacketsIcon,
								"/icons/fromServer.png"));
						
								serverPacketsCountLabel = new Label(serverPackets_1, SWT.NONE);
								serverPacketsCountLabel.setLayoutData(new RowData(40, SWT.DEFAULT));
		
				Composite clientPackets_1 = new Composite(statusBar, SWT.NONE);
				clientPackets_1.setLayout(new RowLayout(SWT.HORIZONTAL));
				
						Label clientPacketsIcon = new Label(clientPackets_1, SWT.NONE);
						clientPacketsIcon.setImage(SWTUtils.loadImage(clientPacketsIcon,
								"/icons/fromClient.png"));
						
								clientPacketsCountLabel = new Label(clientPackets_1, SWT.NONE);
								clientPacketsCountLabel.setLayoutData(new RowData(40, SWT.DEFAULT));
								
								Composite composite = new Composite(statusBar, SWT.NONE);
								composite.setLayout(new FillLayout(SWT.HORIZONTAL));
								GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
								gd_composite.heightHint = 0;
								composite.setLayoutData(gd_composite);
								composite.setSize(-1, 0);
						
								progressBar = new ProgressBar(statusBar, SWT.SMOOTH | SWT.INDETERMINATE);
								progressBar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
								progressBar.setVisible(true);

		dropTarget.addDropListener(new DropTargetAdapter() {
			public void drop(DropTargetEvent event) {
				String fileList[] = null;
				FileTransfer ft = FileTransfer.getInstance();
				if (ft.isSupportedType(event.currentDataType)) {
					fileList = (String[]) event.data;
				}
				for (final String file : fileList) {
					startParser(new File(file));
				}
			}
		});
	}

	/**
	 * Displays the window and block until the window is closed.
	 */
	public void open() {
		shell.open();
	}

	/**
	 * @return <code>true</code> if the window has been disposed
	 */
	public boolean isDisposed() {
		return shell.isDisposed();
	}

	/**
	 * @return the partPanel
	 */
	public PartComposite getPartPanel() {
		return partComposite;
	}

	/**
	 * Starts a new parser instance for the given file
	 * 
	 * @param file
	 *            the file to be parsed
	 */
	private void startParser(File file) {
		// init parser
		final ProtocolDescriptor protocol = JAXB.unmarshal(new File(
				"protocol/lineage2_protocol.xml"), ProtocolDescriptor.class);
		try {
			final AbstractProtocolParser parser = new FileProtocolParser(
					protocol, packetListener, file);
			new Thread(new Runnable() {
				@Override
				public void run() {
					shell.getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							progressBar.setVisible(true);
						}
					});
					parser.run();
					shell.getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							progressBar.setVisible(false);
						}
					});
				}
			}).start();
		} catch (IOException e) {
		} catch (IllegalArgumentException e) {
		}
	}

	/**
	 * Starts a new parser instance for the given file
	 * 
	 * @param device
	 *            the device to start the live capture
	 */
	private void startParser(PcapIf device) {
		// init parser
		final ProtocolDescriptor protocol = JAXB.unmarshal(new File(
				"protocol/lineage2_protocol.xml"), ProtocolDescriptor.class);
		try {
			final AbstractProtocolParser parser = new LiveProtocolParser(
					protocol, packetListener, device.getName());
			new Thread(new Runnable() {
				@Override
				public void run() {
					shell.getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							progressBar.setVisible(true);
						}
					});
					parser.run();
					shell.getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							progressBar.setVisible(false);
						}
					});
				}
			}).start();
		} catch (IOException e) {
		} catch (IllegalArgumentException e) {
		}
	}

	/**
	 * @param session
	 *            the protocol session
	 * @param packet
	 *            the protocol packet
	 */
	private void addPacket(final ProtocolSession session,
			final ProtocolPacket packet) {
		if (packet == null)
			return;
		shell.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				UISessionEntry entry = sessions.get(session);
				if (entry == null) {
					final TabItem tab = new TabItem(packetTabs, SWT.NONE);
					tab.setText("Session " + session.getID());
					final Composite compositeTab = new Composite(packetTabs,
							SWT.NONE);
					compositeTab.setLayout(new FillLayout());
					final PacketComposite table = new PacketComposite(
							compositeTab, partComposite);
					tab.setControl(compositeTab);
					entry = new UISessionEntry(table, tab);
					sessions.put(session, entry);
				}
				if (packet.getDirection() == PacketDirection.CLIENT) {
					clientPackets++;
					clientPacketsCountLabel.setText(Integer
							.toString(clientPackets));
				} else {
					serverPackets++;
					serverPacketsCountLabel.setText(Integer
							.toString(serverPackets));
				}
				entry.packetComposite.addPacket(packet);
			}
		});
	}

	/**
	 * An simple session entry displayed on the UI
	 * 
	 * @author <a href="http://www.rogiel.com">Rogiel</a>
	 */
	private class UISessionEntry {
		/**
		 * The packet composite
		 */
		private final PacketComposite packetComposite;
		/**
		 * The tab item
		 */
		@SuppressWarnings("unused")
		private final TabItem tabItem;

		/**
		 * @param packetComposite
		 *            the packet table
		 * @param tabItem
		 *            the tab item
		 */
		public UISessionEntry(PacketComposite packetComposite, TabItem tabItem) {
			this.packetComposite = packetComposite;
			this.tabItem = tabItem;
		}
	}

	/**
	 * The Packet listener binded to the UI
	 * 
	 * @author <a href="http://www.rogiel.com">Rogiel</a>
	 */
	private class UIPacketListener implements PacketListener {
		@Override
		public void receivePacket(ProtocolSession session, ProtocolPacket packet) {
			addPacket(session, packet);
		}

		@Override
		public boolean onException(Throwable e) {
			e.printStackTrace();
			return true;
		}
	}
}
