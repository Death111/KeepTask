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

package de.doubleslash.keeptask.view;

import de.doubleslash.keeptask.common.*;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.doubleslash.keeptask.controller.Controller;
import de.doubleslash.keeptask.model.Model;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class ViewController {
    private static final Logger LOG = LoggerFactory.getLogger(ViewController.class);
    private final Model model;
    private final Controller controller;

    private class Delta {
        double x;
        double y;
    }

    private final Delta dragDelta = new Delta();
    private Stage mainStage;

    @FXML
    private Pane pane;

    @FXML
    private HBox projectFilterHbox;

    @FXML
    private Button minimizeButton;

    @FXML
    private Button closeButton;

    @FXML
    private TextField searchTextInput;

    @FXML
    private Button allButton;

    @FXML
    private ToggleButton todayToggleButton;

    @FXML
    private ToggleButton tomorrowToggleButton;

    @FXML
    private ToggleButton expiredToggleButton;

    @FXML
    private CheckBox alsoCompletedCheckbox;

    @FXML
    private TextField prioTextInput;

    @FXML
    private TextField projectTextInput;

    @FXML
    private TextField todoTextInput;

    @FXML
    private DatePicker dueDatePicker;

    @FXML
    private Button addTodoButton;

    @FXML
    private VBox workItemVBox;

    @Autowired
    public ViewController(final Model model, final Controller controller) {
        this.model = model;
        this.controller = controller;
    }

    @FXML
    private void initialize() {

        closeButton.setOnAction(ae -> openConfirmationWindow());
        minimizeButton.setOnAction(ae -> mainStage.setIconified(true));

        runUpdateMainBackgroundColor();

        // Drag stage
        pane.setOnMousePressed(mouseEvent -> {
            // record a delta distance for the drag and drop operation.
            dragDelta.x = mainStage.getX() - mouseEvent.getScreenX();
            dragDelta.y = mainStage.getY() - mouseEvent.getScreenY();
        });

        pane.setOnMouseDragged(mouseEvent -> {
            mainStage.setX(mouseEvent.getScreenX() + dragDelta.x);
            mainStage.setY(mouseEvent.getScreenY() + dragDelta.y);
        });

        addTodoButton.setOnAction((actionEvent) -> {
            LocalDate dueDate = dueDatePicker.getValue();
            LocalDateTime dueDateTime = null;

            if (dueDate != null) {
                dueDateTime = dueDate.atStartOfDay();
            }
            WorkItem newItem = new WorkItem(projectTextInput.getText(), prioTextInput.getText(), todoTextInput.getText(), LocalDateTime.now(), dueDateTime, null, false, "");
            controller.addWorkItem(newItem);

            todoTextInput.clear();
            dueDatePicker.setValue(null);
        });

        refreshTodos(Optional.empty());

        model.getWorkItems().addListener((ListChangeListener<? super WorkItem>) change -> {
            refreshTodos(Optional.empty());
            mainStage.sizeToScene();
            updateProjectFilterButtons();
        });
        updateProjectFilterButtons();

        allButton.setOnAction(actionEvent -> {
            todayToggleButton.setSelected(false);
            tomorrowToggleButton.setSelected(false);
            expiredToggleButton.setSelected(false);
        });

        alsoCompletedCheckbox.setUserData((Predicate<WorkItem>) workItem -> alsoCompletedCheckbox.isSelected() ? true : !workItem.isFinished());
        alsoCompletedCheckbox.setOnAction(actionEvent -> {
                    CheckBox sourceToggleButton = (CheckBox) actionEvent.getSource();
                    if (sourceToggleButton.isSelected()) {
                        filters.add((Predicate<WorkItem>) todayToggleButton.getUserData());
                    } else {
                        filters.remove(todayToggleButton.getUserData());
                    }
                    refreshTodos(Optional.empty());
                }
        );

        todayToggleButton.setUserData((Predicate<WorkItem>) workItem -> workItem.getDueDateTime() == null ? false : LocalDate.now().isEqual(workItem.getDueDateTime().toLocalDate()));
        todayToggleButton.setOnAction(toggleButtonPressedAction());

        tomorrowToggleButton.setUserData((Predicate<WorkItem>) workItem -> workItem.getDueDateTime() == null ? false : LocalDate.now().plusDays(1).isEqual(workItem.getDueDateTime().toLocalDate()));
        tomorrowToggleButton.setOnAction(toggleButtonPressedAction());

        expiredToggleButton.setUserData((Predicate<WorkItem>) workItem -> workItem.getDueDateTime() == null ? false : LocalDate.now().isAfter(workItem.getDueDateTime().toLocalDate()));
        expiredToggleButton.setOnAction(toggleButtonPressedAction());
    }

    private EventHandler<ActionEvent> toggleButtonPressedAction() {
        return actionEvent -> {
            ToggleButton sourceToggleButton = (ToggleButton) actionEvent.getSource();
            if (sourceToggleButton.isSelected()) {
                filters.add((Predicate<WorkItem>) todayToggleButton.getUserData());
            } else {
                filters.remove(todayToggleButton.getUserData());
            }
            refreshTodos(Optional.empty());
        };
    }

    List<Predicate<WorkItem>> filters = new ArrayList<>();

    private void updateProjectFilterButtons() {
        Set<String> projectNames = model.getWorkItems().stream().map(workItem -> workItem.getProject()).collect(Collectors.toSet());
        projectFilterHbox.getChildren().clear();
        projectFilterHbox.getChildren().addAll(projectNames.stream().map(projectName -> {
                            Button button = new Button(projectName);
                            button.setOnAction(actionEvent -> {
                                refreshTodos(Optional.of(workItem -> button.getText().equals(workItem.getProject())));
                                projectTextInput.setText(button.getText());
                            });
                            return button;
                        }
                ).collect(Collectors.toList())
        );
    }

    private void openConfirmationWindow() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.CANCEL);
        alert.setTitle("Confirm exit");
        alert.setHeaderText("Are you sure you want to close KeepTask?");

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Resources.getResource(Resources.RESOURCE.ICON_MAIN).toString()));

        stage.setAlwaysOnTop(true);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            mainStage.close();
        }
    }

    private void refreshTodos(Optional<Predicate<WorkItem>> optionalFilterPredicate) {
        ObservableList<Node> children = workItemVBox.getChildren();
        children.clear();

        ObservableList<WorkItem> workItems = model.getWorkItems();

        for (WorkItem workItem : workItems.stream().filter(filters.stream().reduce(x -> true, Predicate::and)).collect(Collectors.toList())) {
            children.add(createTodoNode(workItem));
        }
    }

    private Node createTodoNode(WorkItem workItem) {
        HBox hbox = new HBox();
        hbox.setSpacing(10.0);
        ObservableList<Node> children = hbox.getChildren();

        CheckBox completedCheckBox = new CheckBox();
        completedCheckBox.setSelected(workItem.isFinished());
        children.add(completedCheckBox);
        completedCheckBox.setOnAction(actionEvent -> controller.toggleWorkItemCompleted(workItem));

        HBox hbox2 = new HBox();
        hbox2.setSpacing(10);
        hbox2.disableProperty().bind(completedCheckBox.selectedProperty());
        ObservableList<Node> children1 = hbox2.getChildren();
        Label prioLabel = new Label(workItem.getPriority());
        if (workItem.getPriority().equalsIgnoreCase("High"))
            prioLabel.setTextFill(Color.RED);
        if (workItem.getPriority().equalsIgnoreCase("Medium"))
            prioLabel.setTextFill(Color.ORANGE);
        if (!workItem.getPriority().isEmpty())
            children1.add(prioLabel);

        Label projectLabel = new Label(workItem.getProject());
        children1.add(projectLabel);

        Label todoLabel = new Label(workItem.getTodo());
        children1.add(todoLabel);

        Label dueDateTimeLabel = new Label("Due: " + workItem.getDueDateTime());
        if (workItem.getDueDateTime() != null)
            children1.add(dueDateTimeLabel);

        Label noteLabel = new Label("Note: " + workItem.getNote());
        if (!workItem.getNote().isEmpty())
            children1.add(noteLabel);

        Label completedDateTimeLabel = new Label("Completed: " + workItem.getCompletedDateTime());
        if (workItem.isFinished()) // TODO this is only working on rerender
            children1.add(completedDateTimeLabel);

        Label createdDateTimeLabel = new Label("Created: " + workItem.getCreatedDateTime());
        //children1.add(createdDateTimeLabel);

        children.add(hbox2);

        Button editButton = new Button("",
                SvgNodeProvider.getSvgNodeWithScale(Resources.RESOURCE.SVG_PENCIL_ICON, 0.03, 0.03));
        editButton.setMaxSize(20, 18);
        editButton.setMinSize(20, 18);
        editButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        children.add(editButton);
        // TODO edit functionality

        Button deleteButton = new Button("",
                SvgNodeProvider.getSvgNodeWithScale(Resources.RESOURCE.SVG_TRASH_ICON, 0.03, 0.03));
        deleteButton.setMaxSize(20, 18);
        deleteButton.setMinSize(20, 18);
        deleteButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        children.add(deleteButton);
        deleteButton.setOnAction(actionEvent -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.initOwner(mainStage);
            alert.setTitle("Delete Todo");
            alert.setHeaderText("Confirm deletion.");
            alert.setContentText("If you delete it it will be gone.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                LOG.info("confirm deletion");
                controller.deleteWorkItem(workItem);
            } else {
                LOG.info("canceled deletion");
            }
        });

        return hbox;
    }

    private void runUpdateMainBackgroundColor() {
        Color color = model.defaultBackgroundColor.get();
        String style = StyleUtils.changeStyleAttribute(pane.getStyle(), "fx-background-color",
                "rgba(" + ColorHelper.colorToCssRgba(color) + ")");
        style = StyleUtils.changeStyleAttribute(style, "fx-border-color",
                "rgba(" + ColorHelper.colorToCssRgb(color) + ", " + 255 + ")");
        pane.setStyle(style);
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

}
