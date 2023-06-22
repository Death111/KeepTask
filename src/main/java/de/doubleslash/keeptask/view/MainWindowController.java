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

import de.doubleslash.keeptask.common.BrowserHelper;
import de.doubleslash.keeptask.common.ColorHelper;
import de.doubleslash.keeptask.common.FxmlLayout;
import de.doubleslash.keeptask.common.LinkParser;
import de.doubleslash.keeptask.common.Point;
import de.doubleslash.keeptask.common.Resources;
import de.doubleslash.keeptask.common.StyleUtils;
import de.doubleslash.keeptask.common.SvgNodeProvider;
import de.doubleslash.keeptask.controller.Controller;
import de.doubleslash.keeptask.exceptions.FXMLLoaderException;
import de.doubleslash.keeptask.model.Model;
import de.doubleslash.keeptask.model.TodoPart;
import de.doubleslash.keeptask.model.WorkItem;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MainWindowController {

  private static final Logger LOG = LoggerFactory.getLogger(MainWindowController.class);

  private final Model model;
  private final Controller controller;

  private Point dragDelta = new Point(0, 0);
  private Stage mainStage;

  @FXML
  private Pane pane;

  @FXML
  private Button minimizeButton;

  @FXML
  private Button closeButton;


  @FXML
  private VBox filterVBox;

  // TODO extract new ToDo-section into own controller
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

  // TODO extract TODO ListView to own controller
  @FXML
  private VBox workItemVBox;

  SortedList<WorkItem> sortedWorkItems;


  @Autowired
  public MainWindowController(final Model model, final Controller controller) {
    this.model = model;
    this.controller = controller;
  }

  @FXML
  private void initialize() {
    // TODO make sorting configurable
    sortedWorkItems = new SortedList<>(model.getWorkFilteredItems());
    Comparator<WorkItem> comparing = Comparator.comparing(
        workItem -> workItem.getDueDateTime() != null ? workItem.getDueDateTime()
            : LocalDateTime.MIN);
    comparing = comparing.reversed();
    sortedWorkItems.setComparator(comparing);

    loadFiltersLayout();

    closeButton.setOnAction(ae -> openConfirmationWindow());
    minimizeButton.setOnAction(ae -> mainStage.setIconified(true));

    runUpdateMainBackgroundColor();

    // Drag stage
    pane.setOnMousePressed(mouseEvent -> {
      // record a delta distance for the drag and drop operation.
      double x = mainStage.getX() - mouseEvent.getScreenX();
      double y = mainStage.getY() - mouseEvent.getScreenY();
      dragDelta = new Point(x, y);
    });

    pane.setOnMouseDragged(mouseEvent -> {
      mainStage.setX(mouseEvent.getScreenX() + dragDelta.getX());
      mainStage.setY(mouseEvent.getScreenY() + dragDelta.getY());
    });

    addTodoButton.setOnAction((actionEvent) -> {
      LocalDate dueDate = dueDatePicker.getValue();
      LocalDateTime dueDateTime = null;

      if (dueDate != null) {
        dueDateTime = dueDate.atStartOfDay();
      }
      WorkItem newItem = new WorkItem(projectTextInput.getText(), prioTextInput.getText(),
          todoTextInput.getText(), LocalDateTime.now(), dueDateTime, null, false, "");
      controller.addWorkItem(newItem);

      todoTextInput.clear();
      dueDatePicker.setValue(null);
    });

    sortedWorkItems.addListener((ListChangeListener<? super WorkItem>) change -> {
      refreshTodos();
    });

    model.latestSelectedProjectProperty().addListener((obs, old, newValue) -> {
      projectTextInput.textProperty().set(newValue);
    });

    refreshTodos();
  }

  private void loadFiltersLayout() {
    final FXMLLoader loader = FxmlLayout.createLoaderFor(Resources.RESOURCE.FXML_FILTER_LAYOUT);

    final VBox filterVBox;
    try {
      filterVBox = loader.load();
    } catch (IOException e) {
      LOG.error("Could not load filters layout", e);
      throw new RuntimeException(e);
    }
    this.filterVBox.getChildren()
        .addAll(filterVBox.getChildren()); // TODO losses original vbox attributes
    FilterController filterController = loader.getController();
  }

  private void refreshTodos() {
    ObservableList<Node> children = workItemVBox.getChildren();

    List<Node> todosAsNodes = sortedWorkItems.stream().map(this::createTodoNode).collect(
        Collectors.toList());
    children.setAll(todosAsNodes);

    if (mainStage != null) {
      mainStage.sizeToScene();
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
    if (workItem.getPriority().equalsIgnoreCase("High")) {
      prioLabel.setTextFill(Color.RED);
    }
    if (workItem.getPriority().equalsIgnoreCase("Medium")) {
      prioLabel.setTextFill(Color.ORANGE);
    }
    if (!workItem.getPriority().isEmpty()) {
      children1.add(prioLabel);
    }

    Label projectLabel = new Label(workItem.getProject());
    children1.add(projectLabel);

    HBox todoHbox = new HBox();
    String todo = workItem.getTodo();
    List<TodoPart> todoParts = new LinkParser().splitAtLinks(todo);
    for (TodoPart todoPart : todoParts) {
      Label label = new Label(todoPart.getStringValue());
      if (todoPart.isLink()) {
        label.setOnMouseClicked((actionEvent) -> BrowserHelper.openURL(todoPart.getLink()));
        label.setTooltip(new Tooltip(todoPart.getLink()));
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
    if (workItem.getDueDateTime() != null) {
      children1.add(dueDateTimeLabel);
    }

    Label noteLabel = new Label("Note: " + workItem.getNote());
    if (!workItem.getNote().isEmpty()) {
      children1.add(noteLabel);
    }

    Label completedDateTimeLabel = new Label("Completed: " + workItem.getCompletedDateTime());
    if (workItem.isFinished()) // TODO this is only working on rerender
    {
      children1.add(completedDateTimeLabel);
    }

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
    final FXMLLoader loader = FxmlLayout.createLoaderFor(
        Resources.RESOURCE.FXML_EDIT_WORKITEM_LAYOUT);
    try {
      grid = loader.load();
    } catch (final IOException e) {
      throw new FXMLLoaderException(
          String.format("Error while loading '%s'.", Resources.RESOURCE.FXML_EDIT_WORKITEM_LAYOUT),
          e);
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

    result.ifPresent(project -> {
      controller.editWorkItem(workItem, result.get());
      refreshTodos();
    });
  }

  private void runUpdateMainBackgroundColor() {
    Color color = model.defaultBackgroundColor.get();
    String style = StyleUtils.changeStyleAttribute(pane.getStyle(), "fx-background-color",
        "rgba(" + ColorHelper.colorToCssRgba(color) + ")");
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
