package de.doubleslash.keeptask.view;

import de.doubleslash.keeptask.model.WorkItem;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ExpiredTodoDialogController {

  private static final Logger LOG = LoggerFactory.getLogger(ExpiredTodoDialogController.class);

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
  private Button reschedule1DayButton;

  @FXML
  private Button reschedule2DaysButton;

  private WorkItem workItem;

  @FXML
  private void initialize() {
    reschedule1DayButton.setOnAction(event -> reschedule(1));
    reschedule2DaysButton.setOnAction(event -> reschedule(2));
  }

  public void initializeWith(final WorkItem workItem) {
    LOG.info("Setting values.");
    this.workItem = workItem;
    completedCheckBox.setSelected(workItem.isFinished());
    todoTextInput.setText(workItem.getTodo());
    if (workItem.getDueDateTime() != null) {
      dueDateDatePicker.setValue(workItem.getDueDateTime().toLocalDate());
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
    WorkItem updatedWorkItem = new WorkItem();
    updatedWorkItem.setFinished(completedCheckBox.isSelected());
    updatedWorkItem.setTodo(todoTextInput.getText());
    if (dueDateDatePicker.getValue() != null) {
      updatedWorkItem.setDueDateTime(dueDateDatePicker.getValue().atStartOfDay());
    }
    updatedWorkItem.setPriority(priorityTextInput.getText());
    updatedWorkItem.setProject(projectTextInput.getText());
    if (createdDateDatePicker.getValue() != null) {
      updatedWorkItem.setCreatedDateTime(createdDateDatePicker.getValue().atStartOfDay());
    }
    if (completedDateDatePicker.getValue() != null) {
      updatedWorkItem.setCompletedDateTime(completedDateDatePicker.getValue().atStartOfDay());
    }
    updatedWorkItem.setNote(noteTextInput.getText());
    return updatedWorkItem;
  }

  private void reschedule(int days) {
    if (workItem != null) {
      workItem.setDueDateTime(LocalDateTime.now().plusDays(days));
      Stage stage = (Stage) reschedule1DayButton.getScene().getWindow();
      stage.close();
    }
  }
}
