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

package de.doubleslash.keeptask.controller;

import de.doubleslash.keeptask.model.Model;
import de.doubleslash.keeptask.model.WorkItem;
import de.doubleslash.keeptask.model.repos.WorkItemRepository;
import de.doubleslash.keeptask.view.MainWindowController;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Controller {

  private static final Logger LOG = LoggerFactory.getLogger(Controller.class);

  private final Model model;

  private WorkItemRepository workItemRepository;

  private MainWindowController mainWindowController;

  @Autowired
  public Controller(final Model model, final WorkItemRepository workItemRepository, final MainWindowController mainWindowController) {
    this.model = model;
    this.workItemRepository = workItemRepository;
    this.mainWindowController = mainWindowController;
  }

  public void init() {
    reloadWorkItemsFromRepo();
    checkForExpiredTodos();
  }

  public void addWorkItem(WorkItem workItem) {
    WorkItem savedWorkItem = workItemRepository.save(workItem);
    reloadWorkItemsFromRepo();
  }

  public void deleteWorkItem(WorkItem workItem) {
    workItemRepository.delete(workItem);
    reloadWorkItemsFromRepo();
  }

  public void toggleWorkItemCompleted(WorkItem workItem) {
    workItem.setFinished(!workItem.isFinished());
    if (workItem.isFinished()) {
      workItem.setCompletedDateTime(LocalDateTime.now());
    } else {
      workItem.setCompletedDateTime(null);
    }

    workItemRepository.save(workItem);
    reloadWorkItemsFromRepo();
  }

  public void editWorkItem(WorkItem oldItem, WorkItem newItem) {

    oldItem.setFinished(newItem.isFinished());
    oldItem.setTodo(newItem.getTodo());
    oldItem.setDueDateTime(newItem.getDueDateTime());
    oldItem.setPriority(newItem.getPriority());
    oldItem.setProject(newItem.getProject());
    oldItem.setCreatedDateTime(newItem.getCreatedDateTime());
    oldItem.setCompletedDateTime(newItem.getCompletedDateTime());
    oldItem.setNote(newItem.getNote());

    workItemRepository.save(oldItem);
    reloadWorkItemsFromRepo();
  }

  private void reloadWorkItemsFromRepo() {
    // TODO manage list internal and dont refetch everything all the time
    List<WorkItem> workItems = workItemRepository.findAll();
    model.setWorkItems(workItems);
  }

  @PreDestroy
  public void shutdown() {
    LOG.info("Controller shutdown");
  }

  public void setFilterPredicate(Predicate<WorkItem> filterPredicate) {
    LOG.debug("Filters were changed");
    model.getWorkFilteredList().setPredicate(filterPredicate);
  }

  public void setLatestSelectedProject(String projectName) {
    model.setLatestSelectedProject(projectName);
  }

  public List<WorkItem> checkForExpiredTodos() {
    LocalDateTime now = LocalDateTime.now();
    List<WorkItem> expiredTodos = model.getWorkItems().stream()
        .filter(item -> {
          LocalDateTime dueDateTime = item.getDueDateTime();
          if (dueDateTime == null) {
            return false;
          }
          return !item.isFinished() && now.isAfter(dueDateTime);
        })
        .collect(Collectors.toList());

    if (!expiredTodos.isEmpty()) {
      mainWindowController.showNotificationPopup(expiredTodos);
    }
    return expiredTodos;
  }
}
