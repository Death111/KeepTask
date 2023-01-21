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
import de.doubleslash.keeptask.exceptions.FXMLLoaderException;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
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

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        refreshTodos();

        model.getWorkItems().addListener((ListChangeListener<? super WorkItem>) change -> {
            refreshTodos();
            mainStage.sizeToScene();
            updateProjectFilterButtons();
        });
        updateProjectFilterButtons();


        alsoCompletedCheckbox.setOnAction(actionEvent -> {
                    refreshTodos();
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
                timeFilters.add((Predicate<WorkItem>) sourceToggleButton.getUserData());
            } else {
                timeFilters.remove(sourceToggleButton.getUserData());
            }
            refreshTodos();
        };
    }

    List<Predicate<WorkItem>> timeFilters = new ArrayList<>();
    List<Predicate<WorkItem>> projectFilters = new ArrayList<>();

    private void updateProjectFilterButtons() {
        Set<String> projectNames = model.getWorkItems().stream().map(workItem -> workItem.getProject()).collect(Collectors.toSet());
        projectFilterHbox.getChildren().clear();
        projectFilters.clear();
        projectFilterHbox.getChildren().addAll(projectNames.stream().map(projectName -> {
                            ToggleButton button = new ToggleButton(projectName);
                            button.setUserData((Predicate<WorkItem>) workItem -> button.getText().equals(workItem.getProject()));

                            button.setOnAction(actionEvent -> {
                                ToggleButton sourceToggleButton = (ToggleButton) actionEvent.getSource();
                                if (sourceToggleButton.isSelected()) {
                                    projectFilters.add((Predicate<WorkItem>) sourceToggleButton.getUserData());
                                } else {
                                    projectFilters.remove(sourceToggleButton.getUserData());
                                }
                                projectTextInput.setText(button.getText());
                                refreshTodos();
                            });
                            return button;
                        }
                ).collect(Collectors.toList())
        );
    }

    private void refreshTodos() {
        ObservableList<Node> children = workItemVBox.getChildren();
        children.clear();

        ObservableList<WorkItem> workItems = model.getWorkItems();

        Stream<WorkItem> a = workItems.stream();
        if (!timeFilters.isEmpty()) a = a.filter(timeFilters.stream().reduce(x -> false, Predicate::or));
        if (!projectFilters.isEmpty()) a = a.filter(projectFilters.stream().reduce(x -> false, Predicate::or));
        a = a.filter(workItem -> alsoCompletedCheckbox.isSelected() ? true : !workItem.isFinished());

        List<WorkItem> filteredItems = a.collect(Collectors.toList());

        for (WorkItem workItem : filteredItems) {
            Node todoNode = createTodoNode(workItem);
            children.add(todoNode);
        }
        if (mainStage != null)
            mainStage.sizeToScene();
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

        HBox todoHbox = new HBox();
        String todo = workItem.getTodo();
        List<TodoPart> todoParts = new LinkParser().splitAtLinks(todo);
        for (TodoPart todoPart : todoParts) {
            Label label = new Label(todoPart.getStringValue());
            if (todoPart.isLink()) {
                label.setOnMouseClicked((actionEvent) -> BrowserHelper.openURL(todoPart.getStringValue()));
                Color normalLinkColor = Color.BLUE;
                label.setTextFill(normalLinkColor);
                label.setUnderline(true);
                label.setOnMouseEntered((a) -> label.setTextFill(Color.AQUA));
                label.setOnMouseExited((a) -> label.setTextFill(normalLinkColor));
            }
            todoHbox.getChildren().add(label);
        }
        children1.add(todoHbox);

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
        editButton.setOnAction((actionEvent) -> editTodoClicked(workItem));

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

    private void editTodoClicked(WorkItem workItem) {

        final Dialog<WorkItem> dialog = new Dialog<>();
        dialog.setTitle("Edit work item");
        dialog.setHeaderText("Edit it!");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid;
        final FXMLLoader loader = new FXMLLoader(Resources.getResource(Resources.RESOURCE.FXML_EDIT_WORKITEM_LAYOUT));

        try {
            grid = loader.load();
        } catch (final IOException e) {
            throw new FXMLLoaderException(String.format("Error while loading '%s'.", Resources.RESOURCE.FXML_EDIT_WORKITEM_LAYOUT), e);
        }
        EditWorkItemController editWorkItemController = loader.getController();
        editWorkItemController.initializeWith(workItem);
        dialog.getDialogPane().setContent(grid);
        dialog.initOwner(mainStage);
        dialog.setResultConverter((dialogButton) -> {
            if (dialogButton == ButtonType.OK) {
                return editWorkItemController.getWorkItemFromUserInput();
            }
            return null;
        });

        final Optional<WorkItem> result = dialog.showAndWait();

        result.ifPresent(project ->{
            controller.editWorkItem(workItem, result.get());
            refreshTodos();
        });
    }

    private void runUpdateMainBackgroundColor() {
        Color color = model.defaultBackgroundColor.get();
        String style = StyleUtils.changeStyleAttribute(pane.getStyle(), "fx-background-color",
                "rgba(" + ColorHelper.colorToCssRgba(color) + ")");
        style = StyleUtils.changeStyleAttribute(style, "fx-border-color",
                "rgba(" + ColorHelper.colorToCssRgb(color) + ", " + 255 + ")");
        pane.setStyle(style);
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

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
        mainStage.sizeToScene();
    }

}
