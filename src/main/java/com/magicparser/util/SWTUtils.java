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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;

/**
 * @author <a href="http://www.rogiel.com">Rogiel</a>
 * 
 */
public class SWTUtils {
	private static final Map<String, Image> imageCache = new HashMap<String, Image>();

	/**
	 * Loads an image from the jar
	 * 
	 * @param widget
	 *            the SWT widget
	 * @param image
	 *            the path to image to be loaded
	 * @return the loaded image
	 */
	public static Image loadImage(Widget widget, String image) {
		return loadImage(widget.getDisplay(), image);
	}

	/**
	 * Loads an image from the jar
	 * 
	 * @param display
	 *            the SWT display
	 * @param image
	 *            the path to image to be loaded
	 * @return the loaded image
	 */
	public static Image loadImage(Display display, String image) {
		Image swtImage = imageCache.get(image);
		if (swtImage == null)
			swtImage = new Image(display,
					SWTUtils.class.getResourceAsStream(image));
		return swtImage;
	}
}
