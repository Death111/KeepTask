/*
 * Copyright 2023 - Death111
 *
 * This file is part of KeepTask.
 * KeepTask is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.doubleslash.keeptask.model;

public class TodoPart {

  private String stringValue;
  private String link;
  private boolean isLink;

  public TodoPart(String stringValue, String link) {
    this.stringValue = stringValue;
    this.link = link;
    this.isLink = true;
  }

  public TodoPart(String stringValue) {
    this.stringValue = stringValue;
    this.link = "";
    this.isLink = false;
  }

  public String getStringValue() {
    return stringValue;
  }

  public String getLink() {
    return link;
  }

  public boolean isLink() {
    return isLink;
  }

  @Override
  public String toString() {
    return "TodoPart{" +
        "stringValue='" + stringValue + '\'' +
        ", link='" + link + '\'' +
        ", isLink=" + isLink +
        '}';
  }
}
