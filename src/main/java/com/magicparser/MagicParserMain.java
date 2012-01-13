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
package com.magicparser;

import org.eclipse.swt.widgets.Display;

import com.magicparser.gui.MainWindow;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class MagicParserMain {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final Display display = Display.getDefault();
		final MainWindow window = new MainWindow(display);
		window.open();

		while (!window.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		// window.open();
	}
}
