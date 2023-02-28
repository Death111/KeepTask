package de.doubleslash.keeptask.view;


import de.doubleslash.keeptask.common.Resources;
import de.doubleslash.keeptask.model.WorkItem;
import de.doubleslash.keeptask.model.WorkItemBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(ApplicationExtension.class)
class SortingControllerTest {
    SortingController sortingController;

    @Start
    private void start(Stage stage) throws IOException {
        final FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Resources.getResource(Resources.RESOURCE.FXML_SORTING_LAYOUT));
        loader.load();
        sortingController = loader.getController();
    }

    @Test
    void shouldSortWorkItemsCorrectlyByPriorityWhenSettingPriorityAsSortingCriteria() {
        List<WorkItem> expectedSortedWorkItems = new ArrayList<>(List.of(
                new WorkItemBuilder().setPriority(WorkItem.Priority.High).createWorkItem(),
                new WorkItemBuilder().setPriority(WorkItem.Priority.Medium).createWorkItem(),
                new WorkItemBuilder().setPriority(WorkItem.Priority.Low).createWorkItem()
        ));

        // GIVEN
        ObservableList<WorkItem> workItemsToBeSorted = FXCollections.observableArrayList(
                expectedSortedWorkItems.get(2),
                expectedSortedWorkItems.get(1),
                expectedSortedWorkItems.get(0)
        );
        sortingController.setWorkItemsToSort(workItemsToBeSorted);

        // WHEN
        sortingController.sortingCriteriaList.add(SortingController.SortingCriteria.Priority);

        // THEN
        assertThat(sortingController.getSortedWorkItems()).isEqualTo(expectedSortedWorkItems);
    }

    @Test
    void shouldSortWorkItemsCorrectlyByDueDateWhenSettingDueDateAsSortingCriteria() {
        List<WorkItem> expectedSortedWorkItems = new ArrayList<>(List.of(
                new WorkItemBuilder().setDueDateTime(LocalDateTime.now().plusDays(1)).createWorkItem(),
                new WorkItemBuilder().setDueDateTime(LocalDateTime.now().plusDays(2)).createWorkItem(),
                new WorkItemBuilder().setDueDateTime(LocalDateTime.now().plusDays(3)).createWorkItem()
        ));

        // GIVEN
        ObservableList<WorkItem> workItemsToBeSorted = FXCollections.observableArrayList(
                expectedSortedWorkItems.get(2),
                expectedSortedWorkItems.get(1),
                expectedSortedWorkItems.get(0)
        );
        sortingController.setWorkItemsToSort(workItemsToBeSorted);

        // WHEN
        sortingController.sortingCriteriaList.add(SortingController.SortingCriteria.DueDate);

        // THEN
        assertThat(sortingController.getSortedWorkItems()).isEqualTo(expectedSortedWorkItems);
    }

    @Test
    void shouldSortWorkItemsCorrectlyByPriorityAndDueDateWhenFirstSortingByPriorityAndThenByDueDate() {
        List<WorkItem> expectedSortedWorkItems = new ArrayList<>(List.of(
                new WorkItemBuilder().setPriority(WorkItem.Priority.High).setDueDateTime(LocalDateTime.now().plusDays(1)).createWorkItem(),
                new WorkItemBuilder().setPriority(WorkItem.Priority.High).setDueDateTime(LocalDateTime.now().plusDays(2)).createWorkItem(),
                new WorkItemBuilder().setPriority(WorkItem.Priority.Medium).setDueDateTime(LocalDateTime.now().plusDays(1)).createWorkItem(),
                new WorkItemBuilder().setPriority(WorkItem.Priority.Medium).setDueDateTime(LocalDateTime.now().plusDays(2)).createWorkItem(),
                new WorkItemBuilder().setPriority(WorkItem.Priority.Low).setDueDateTime(LocalDateTime.now().plusDays(1)).createWorkItem(),
                new WorkItemBuilder().setPriority(WorkItem.Priority.Low).setDueDateTime(LocalDateTime.now().plusDays(2)).createWorkItem()
        ));

        // GIVEN
        ObservableList<WorkItem> workItemsToBeSorted = FXCollections.observableArrayList(
                expectedSortedWorkItems.get(5),
                expectedSortedWorkItems.get(4),
                expectedSortedWorkItems.get(3),
                expectedSortedWorkItems.get(2),
                expectedSortedWorkItems.get(1),
                expectedSortedWorkItems.get(0)
        );
        sortingController.setWorkItemsToSort(workItemsToBeSorted);

        // WHEN
        sortingController.sortingCriteriaList.add(SortingController.SortingCriteria.Priority);
        sortingController.sortingCriteriaList.add(SortingController.SortingCriteria.DueDate);

        // THEN
        assertThat(sortingController.getSortedWorkItems()).isEqualTo(expectedSortedWorkItems);
    }
}