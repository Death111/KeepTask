package de.doubleslash.keeptask.model;

import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.paint.Color;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Model {


  public static final Color ORIGINAL_DEFAULT_BACKGROUND_COLOR = Color.WHITE;
  public final ObjectProperty<Color> defaultBackgroundColor = new SimpleObjectProperty<>(
      ORIGINAL_DEFAULT_BACKGROUND_COLOR);
  private final ObservableList<WorkItem> workItems = FXCollections.observableArrayList();
  private final ObservableList<WorkItem> unmodifiableWorkItems = FXCollections.unmodifiableObservableList(
      workItems);
  private final FilteredList<WorkItem> workFilteredItems = new FilteredList<>(workItems);
  private final ObservableList<WorkItem> unmodifiableWorkItemsFiltered = FXCollections.unmodifiableObservableList(
      workFilteredItems);

  private final StringProperty latestSelectedProject = new SimpleStringProperty();

  @Autowired
  public Model() {
    super();
  }

  public void setWorkItems(List<WorkItem> workItems) {
    this.workItems.setAll(workItems);
  }

  public ObservableList<WorkItem> getWorkItems() {
    return unmodifiableWorkItems;
  }

  public ObservableList<WorkItem> getWorkFilteredItems() {
    return unmodifiableWorkItemsFiltered;
  }

  public FilteredList<WorkItem> getWorkFilteredList() {
    return workFilteredItems;
  }

  public StringProperty latestSelectedProjectProperty() {
    return latestSelectedProject;
  }

  public void setLatestSelectedProject(String latestSelectedProject) {
    this.latestSelectedProject.set(latestSelectedProject);
  }
}
