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

package de.doubleslash.keeptask.view;

import de.doubleslash.keeptask.model.WorkItem;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EditWorkItemController {

  private static final Logger LOG = LoggerFactory.getLogger(EditWorkItemController.class);

  @FXML
  private CheckBox completedCheckBox;

  @FXML
  private TextField todoTextInput;

  @FXML
  private DatePicker dueDateDatePicker;

  @FXML
  private TextField priorityTextInput;

  @FXML
  private TextField projectTextInput;

  @FXML
  private DatePicker createdDateDatePicker;

  @FXML
  private DatePicker completedDateDatePicker;

  @FXML
  private TextField noteTextInput;

  @FXML
  private Label dueDateStatusLabel;

  @FXML
  private void initialize() {

  }

  public void initializeWith(final WorkItem workItem) {
    LOG.info("Setting values.");
    completedCheckBox.setSelected(workItem.isFinished());
    todoTextInput.setText(workItem.getTodo());
    if (workItem.getDueDateTime() != null) {
      dueDateDatePicker.setValue(workItem.getDueDateTime().toLocalDate());
      dueDateDatePicker.valueProperty().addListener((dp, oldValue, newValue) -> {
        updateDueDateStatus(newValue.atStartOfDay());
      });
      updateDueDateStatus(workItem.getDueDateTime());
    }
    priorityTextInput.setText(workItem.getPriority());
    projectTextInput.setText(workItem.getProject());
    if (workItem.getCreatedDateTime() != null) {
      createdDateDatePicker.setValue(workItem.getCreatedDateTime().toLocalDate());
    }
    if (workItem.getCompletedDateTime() != null) {
      completedDateDatePicker.setValue(workItem.getCompletedDateTime().toLocalDate());
    }
    noteTextInput.setText(workItem.getNote());
  }

  public WorkItem getWorkItemFromUserInput() {
    WorkItem workItem = new WorkItem();
    workItem.setFinished(completedCheckBox.isSelected());
    workItem.setTodo(todoTextInput.getText());
    if (dueDateDatePicker.getValue() != null) {
      workItem.setDueDateTime(dueDateDatePicker.getValue().atStartOfDay());
    }
    workItem.setPriority(priorityTextInput.getText());
    workItem.setProject(projectTextInput.getText());
    if (createdDateDatePicker.getValue() != null) {
      workItem.setCreatedDateTime(createdDateDatePicker.getValue().atStartOfDay());
    }
    if (completedDateDatePicker.getValue() != null) {
      workItem.setCompletedDateTime(completedDateDatePicker.getValue().atStartOfDay());
    }
    workItem.setNote(noteTextInput.getText());
    return workItem;
  }

  private void updateDueDateStatus(LocalDateTime dueDateTime) {
    LocalDate now = LocalDate.now();
    LocalDate dueDate = dueDateTime.toLocalDate();
    long daysBetween = ChronoUnit.DAYS.between(now, dueDate);

    if (daysBetween < 0) {
      dueDateStatusLabel.setText(Math.abs(daysBetween) + " days expired!");
      dueDateStatusLabel.setTextFill(Color.DARKRED);
    } else if (daysBetween > 0) {
      dueDateStatusLabel.setText(daysBetween + " days remaining");
      dueDateStatusLabel.setTextFill(Color.BLACK);
    } else {
      dueDateStatusLabel.setText("Due today");
      dueDateStatusLabel.setTextFill(Color.ORANGE);
    }
  }
}
