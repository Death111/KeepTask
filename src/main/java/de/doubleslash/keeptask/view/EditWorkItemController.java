package de.doubleslash.keeptask.view;

import de.doubleslash.keeptask.common.WorkItem;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private void initialize() {

    }

    public void initializeWith(final WorkItem workItem) {
        LOG.info("Setting values.");
        completedCheckBox.setSelected(workItem.isFinished());
        todoTextInput.setText(workItem.getTodo());
        if (workItem.getDueDateTime() != null)
            dueDateDatePicker.setValue(workItem.getDueDateTime().toLocalDate());
        priorityTextInput.setText(workItem.getPriority());
        projectTextInput.setText(workItem.getProject());
        if (workItem.getCreatedDateTime() != null)
            createdDateDatePicker.setValue(workItem.getCreatedDateTime().toLocalDate());
        if (workItem.getCompletedDateTime() != null)
            completedDateDatePicker.setValue(workItem.getCompletedDateTime().toLocalDate());
        noteTextInput.setText(workItem.getNote());
    }

    public WorkItem getWorkItemFromUserInput() {
        WorkItem workItem = new WorkItem();
        workItem.setFinished(completedCheckBox.isSelected());
        workItem.setTodo(todoTextInput.getText());
        if (dueDateDatePicker.getValue() != null)
            workItem.setDueDateTime(dueDateDatePicker.getValue().atStartOfDay());
        workItem.setPriority(priorityTextInput.getText());
        workItem.setProject(projectTextInput.getText());
        if (createdDateDatePicker.getValue() != null)
            workItem.setCreatedDateTime(createdDateDatePicker.getValue().atStartOfDay());
        if (completedDateDatePicker.getValue() != null)
            workItem.setCompletedDateTime(completedDateDatePicker.getValue().atStartOfDay());
        workItem.setNote(noteTextInput.getText());
        return workItem;
    }
}
