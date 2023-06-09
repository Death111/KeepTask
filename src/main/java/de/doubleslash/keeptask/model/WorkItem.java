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

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class WorkItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false, nullable = false)
  private long id;

  private String project;
  private String priority;
  private String todo;
  private LocalDateTime createdDateTime;
  private LocalDateTime dueDateTime;
  private LocalDateTime completedDateTime;
  private boolean finished;
  private String note;

  public void setProject(String project) {
    this.project = project;
  }

  public void setTodo(String todo) {
    this.todo = todo;
  }

  public void setCreatedDateTime(LocalDateTime createdDateTime) {
    this.createdDateTime = createdDateTime;
  }

  public void setDueDateTime(LocalDateTime dueDateTime) {
    this.dueDateTime = dueDateTime;
  }

  public void setCompletedDateTime(LocalDateTime completedDateTime) {
    this.completedDateTime = completedDateTime;
  }

  public void setFinished(boolean finished) {
    this.finished = finished;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public WorkItem() {
    // needed for hibernate
  }

  public WorkItem(String project, String priority, String todo, LocalDateTime createdDateTime,
      LocalDateTime dueDateTime, LocalDateTime completedDateTime, boolean finished, String note) {
    this.project = project;
    this.priority = priority;
    this.todo = todo;
    this.createdDateTime = createdDateTime;
    this.dueDateTime = dueDateTime;
    this.completedDateTime = completedDateTime;
    this.finished = finished;
    this.note = note;
  }

  public long getId() {
    return id;
  }

  public String getProject() {
    return project;
  }

  public String getTodo() {
    return todo;
  }

  public LocalDateTime getCreatedDateTime() {
    return createdDateTime;
  }

  public LocalDateTime getDueDateTime() {
    return dueDateTime;
  }

  public LocalDateTime getCompletedDateTime() {
    return completedDateTime;
  }

  public boolean isFinished() {
    return finished;
  }

  public String getNote() {
    return note;
  }

  public String getPriority() {
    return priority;
  }

  public void setPriority(String priority) {
    this.priority = priority;
  }

  public void setId(long id) {
    this.id = id;
  }
}
