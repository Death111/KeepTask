package de.doubleslash.keeptask.view;

import de.doubleslash.keeptask.controller.Controller;
import de.doubleslash.keeptask.model.WorkItem;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class NotificationController {

    private final Controller controller;

    @Autowired
    public NotificationController(Controller controller) {
        this.controller = controller;
    }

    public void showExpiredTodos(Stage primaryStage) {
        List<WorkItem> expiredTodos = controller.getExpiredTodos();
        for (WorkItem todo : expiredTodos) {
            showNotificationDialog(todo, primaryStage);
        }
    }

    private void showNotificationDialog(WorkItem todo, Stage primaryStage) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Expired Todo Notification");
        dialog.setHeaderText("You have an expired todo:");
        dialog.initOwner(primaryStage);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/layouts/NotificationDialog.fxml"));
        try {
            GridPane gridPane = loader.load();
            Label todoDetails = new Label(todo.getTodo());
            gridPane.add(todoDetails, 0, 0);
            dialog.getDialogPane().setContent(gridPane);
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        result.ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                handleUserAction(todo);
            }
        });
    }

    private void handleUserAction(WorkItem todo) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Todo Action");
        alert.setHeaderText("Choose an action for the todo:");
        alert.setContentText("Complete, Add Note, Change Due Date, Skip");

        ButtonType completeButton = new ButtonType("Complete");
        ButtonType addNoteButton = new ButtonType("Add Note");
        ButtonType changeDueDateButton = new ButtonType("Change Due Date");
        ButtonType skipButton = new ButtonType("Skip");

        alert.getButtonTypes().setAll(completeButton, addNoteButton, changeDueDateButton, skipButton);

        Optional<ButtonType> result = alert.showAndWait();
        result.ifPresent(buttonType -> {
            if (buttonType == completeButton) {
                controller.completeTodo(todo);
            } else if (buttonType == addNoteButton) {
                controller.addNoteToTodo(todo);
            } else if (buttonType == changeDueDateButton) {
                controller.changeDueDateOfTodo(todo);
            }
        });
    }
}
