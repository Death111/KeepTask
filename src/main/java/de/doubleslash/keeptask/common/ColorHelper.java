// Copyright 2019 doubleSlash Net Business GmbH
//
// This file is part of KeepTime.
// KeepTime is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program. If not, see <http://www.gnu.org/licenses/>.

package de.doubleslash.keeptask.common;

import javafx.scene.paint.Color;

public class ColorHelper {

  private ColorHelper() {
    throw new IllegalStateException("Utility class: ColorHelper");
  }

  public static Color randomColor() {
    return Color.BLACK;
  }

  public static String colorToCssRgba(final Color color) {
    return colorToCssRgb(color) + ", " + color.getOpacity();
  }

  public static String colorToCssRgb(final Color color) {
    return color.getRed() * 255 + ", " + color.getGreen() * 255 + ", " + color.getBlue() * 255;
  }
}
