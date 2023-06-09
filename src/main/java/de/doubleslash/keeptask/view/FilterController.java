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

import de.doubleslash.keeptask.controller.Controller;
import de.doubleslash.keeptask.model.Model;
import de.doubleslash.keeptask.model.WorkItem;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FilterController {

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
  private HBox projectFilterHbox;

  private final Model model;
  private final Controller controller;

  private final List<Predicate<WorkItem>> timeFilters = new ArrayList<>();
  private final List<String> projectNameFilters = new ArrayList<>();


  @Autowired
  public FilterController(final Model model, final Controller controller) {
    this.model = model;
    this.controller = controller;
  }

  @FXML
  private void initialize() {
    model.getWorkItems().addListener((ListChangeListener<? super WorkItem>) change -> {
      updateProjectFilterButtons();
    });
    updateProjectFilterButtons();

    alsoCompletedCheckbox.setOnAction(actionEvent -> updateFilters());

    todayToggleButton.setUserData(
        (Predicate<WorkItem>) workItem -> workItem.getDueDateTime() == null ? false
            : LocalDate.now().isEqual(workItem.getDueDateTime().toLocalDate()));
    todayToggleButton.setOnAction(toggleButtonPressedAction());

    tomorrowToggleButton.setUserData(
        (Predicate<WorkItem>) workItem -> workItem.getDueDateTime() == null ? false
            : LocalDate.now().plusDays(1).isEqual(workItem.getDueDateTime().toLocalDate()));
    tomorrowToggleButton.setOnAction(toggleButtonPressedAction());

    expiredToggleButton.setUserData(
        (Predicate<WorkItem>) workItem -> workItem.getDueDateTime() == null ? false
            : LocalDate.now().isAfter(workItem.getDueDateTime().toLocalDate()));
    expiredToggleButton.setOnAction(toggleButtonPressedAction());

    updateFilters();
  }

  private EventHandler<ActionEvent> toggleButtonPressedAction() {
    return actionEvent -> {
      ToggleButton sourceToggleButton = (ToggleButton) actionEvent.getSource();
      if (sourceToggleButton.isSelected()) {
        timeFilters.add((Predicate<WorkItem>) sourceToggleButton.getUserData());
      } else {
        timeFilters.remove(sourceToggleButton.getUserData());
      }
      updateFilters();
    };
  }

  private void updateProjectFilterButtons() {
    Set<String> projectNames = model.getWorkItems().stream().map(workItem -> workItem.getProject())
        .collect(Collectors.toSet());
    projectFilterHbox.getChildren().clear();

    // recreate buttons
    projectFilterHbox.getChildren().addAll(projectNames.stream().map(projectName -> {
              ToggleButton button = new ToggleButton(projectName);
              button.setUserData(projectName);
              button.setSelected(projectNameFilters.contains(projectName));

              button.setOnAction(actionEvent -> {
                ToggleButton sourceToggleButton = (ToggleButton) actionEvent.getSource();
                if (sourceToggleButton.isSelected()) {
                  projectNameFilters.add(projectName);
                } else {
                  projectNameFilters.remove(projectName);
                }

                controller.setLatestSelectedProject(projectName);

                updateFilters();
              });
              return button;
            }
        ).collect(Collectors.toList())
    );

    // reset previous filter - only keep the ones which still exist
    List<String> tempProjectNameFilters = projectNameFilters.stream().filter(
            projectFilter -> projectNames.stream()
                .anyMatch(projectName -> projectFilter.equals(projectName)))
        .collect(Collectors.toList());
    projectNameFilters.clear();
    projectNameFilters.addAll(tempProjectNameFilters);
  }

  private void updateFilters() {
    Predicate<WorkItem> filterPredicate = generateFilterPredicate();
    controller.setFilterPredicate(filterPredicate);
  }

  private Predicate<WorkItem> generateFilterPredicate() {
    Predicate<WorkItem> filterPredicate = (workItem) -> {
      boolean timeFilterMatches = timeFilters.isEmpty() ? true
          : timeFilters.stream().reduce(x -> false, Predicate::or).test(workItem);
      boolean projectNameMatches = projectNameFilters.isEmpty() ? true
          : projectNameFilters.stream().anyMatch(filter -> workItem.getProject().equals(filter));
      boolean completedMatches = alsoCompletedCheckbox.isSelected() ? true : !workItem.isFinished();

      return timeFilterMatches && projectNameMatches && completedMatches;
    };
    return filterPredicate;
  }

}
