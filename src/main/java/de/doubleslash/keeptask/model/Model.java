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

package de.doubleslash.keeptask.model;

import de.doubleslash.keeptask.model.repos.WorkItemRepository;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.scene.paint.Color;

import java.util.List;

@Component
public class Model {



    public static final Color ORIGINAL_DEFAULT_BACKGROUND_COLOR = Color.WHITE;
    public final ObjectProperty<Color> defaultBackgroundColor = new SimpleObjectProperty<>(
            ORIGINAL_DEFAULT_BACKGROUND_COLOR);
    private ObservableList<WorkItem> workItems = FXCollections.observableArrayList();
    private FilteredList<WorkItem> workFilteredItems = new FilteredList(workItems);

    private StringProperty latestSelectedProject = new SimpleStringProperty();

    @Autowired
    public Model() {
        super();
    }

    public void setWorkItems(List<WorkItem> workItems) {
        this.workItems.clear();
        this.workItems.addAll(workItems);
    }

    public ObservableList<WorkItem> getWorkItems() {
        return FXCollections.unmodifiableObservableList(workItems);
    }

    public ObservableList<WorkItem> getWorkFilteredItems() {
        return FXCollections.unmodifiableObservableList(workFilteredItems);
    }

    public FilteredList<WorkItem> getWorkFilteredList(){return workFilteredItems;}

    public StringProperty latestSelectedProjectProperty() {
        return latestSelectedProject;
    }

    public void setLatestSelectedProject(String latestSelectedProject) {
        this.latestSelectedProject.set(latestSelectedProject);
    }
}
