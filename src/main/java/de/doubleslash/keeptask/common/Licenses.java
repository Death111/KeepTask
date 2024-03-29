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

public enum Licenses {
  ;
  private final String path;
  private final String name;
  private final String url;

  private Licenses(final String licensePath, final String licenseName, final String urlWebsite) {
    path = licensePath;
    name = licenseName;
    url = urlWebsite;
  }

  public String getPath() {
    return path;
  }

  public String getName() {
    return name;
  }

  public String getUrl() {
    return url;
  }
}
