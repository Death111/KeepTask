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

package de.doubleslash.keeptask.controller;

import java.time.LocalDateTime;

import javax.annotation.PreDestroy;

import de.doubleslash.keeptask.model.WorkItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.doubleslash.keeptask.common.DateProvider;
import de.doubleslash.keeptask.model.Model;

@Service
public class Controller {

    private static final Logger LOG = LoggerFactory.getLogger(Controller.class);

    private final Model model;

    private final DateProvider dateProvider;

    @Autowired
    public Controller(final Model model, final DateProvider dateProvider) {
        this.model = model;
        this.dateProvider = dateProvider;
    }

    public void addWorkItem(WorkItem workItem) {
        WorkItem savedWorkItem = model.getWorkItemRepository().save(workItem);
        model.getWorkItems().add(savedWorkItem);
    }

    public void deleteWorkItem(WorkItem workItem) {
        model.getWorkItemRepository().delete(workItem);
        model.getWorkItems().remove(workItem);
    }

    public void toggleWorkItemCompleted(WorkItem workItem) {
        workItem.setFinished(!workItem.isFinished());
        if (workItem.isFinished()) workItem.setCompletedDateTime(LocalDateTime.now());
        else workItem.setCompletedDateTime(null);

        model.getWorkItemRepository().save(workItem);
    }

    @PreDestroy
    public void shutdown() {
        LOG.info("Controller shutdown");
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

        model.getWorkItemRepository().save(oldItem);
    }
}
