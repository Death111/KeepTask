package de.doubleslash.keeptask.controller;

import de.doubleslash.keeptask.model.Sorting.DueDate;
import de.doubleslash.keeptask.model.Sorting.Priority;
import de.doubleslash.keeptask.model.Sorting.SortingCriteria;
import de.doubleslash.keeptask.model.Sorting.SortingDirection;
import de.doubleslash.keeptask.model.WorkItem;
import de.doubleslash.keeptask.model.WorkItemBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static de.doubleslash.keeptask.TestUtils.getArrayListReverted;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class SortingControllerTest {
    private SortingController sortingController;

    @BeforeEach
    void setup() {
        sortingController = new SortingController();
    }

    @Test
    void shouldSortWorkItemsDescendingByPriorityByDefaultWhenSettingPriorityAsSortingCriteria() {
        ArrayList<WorkItem> expectedSortedWorkItems = new ArrayList<>(List.of(
                new WorkItemBuilder().setPriority(WorkItem.Priority.High).createWorkItem(),
                new WorkItemBuilder().setPriority(WorkItem.Priority.Medium).createWorkItem(),
                new WorkItemBuilder().setPriority(WorkItem.Priority.Low).createWorkItem()
        ));

        // GIVEN
        ObservableList<WorkItem> workItemsToBeSorted = FXCollections.observableArrayList(getArrayListReverted(expectedSortedWorkItems));
        sortingController.setWorkItemsToSort(workItemsToBeSorted);

        // WHEN
        sortingController.selectedSortingCriteriaList.add(new Priority());

        // THEN
        assertThat(sortingController.getSortedWorkItems()).isEqualTo(expectedSortedWorkItems);
    }

    @Test
    void shouldSortWorkItemsCorrectlyByDueDateWhenSettingDueDateAsSortingCriteria() {
        ArrayList<WorkItem> expectedSortedWorkItems = new ArrayList<>(List.of(
                new WorkItemBuilder().setDueDateTime(LocalDateTime.now().plusDays(1)).createWorkItem(),
                new WorkItemBuilder().setDueDateTime(LocalDateTime.now().plusDays(2)).createWorkItem(),
                new WorkItemBuilder().setDueDateTime(LocalDateTime.now().plusDays(3)).createWorkItem()
        ));

        // GIVEN
        ObservableList<WorkItem> workItemsToBeSorted = FXCollections.observableArrayList(getArrayListReverted(expectedSortedWorkItems));
        sortingController.setWorkItemsToSort(workItemsToBeSorted);

        // WHEN
        sortingController.selectedSortingCriteriaList.add(new DueDate());

        // THEN
        assertThat(sortingController.getSortedWorkItems()).isEqualTo(expectedSortedWorkItems);
    }

    @Test
    void shouldPutWorkItemToBottomWhenUsedSortingCriteriaNotSetOnWorkItem() {
        ArrayList<WorkItem> expectedSortedWorkItems = new ArrayList<>(List.of(
                new WorkItemBuilder().setPriority(WorkItem.Priority.High).createWorkItem(),
                new WorkItemBuilder().setPriority(WorkItem.Priority.Medium).createWorkItem(),
                new WorkItemBuilder().setPriority(null).createWorkItem()
        ));

        // GIVEN
        ObservableList<WorkItem> workItemsToBeSorted = FXCollections.observableArrayList(getArrayListReverted(expectedSortedWorkItems));
        sortingController.setWorkItemsToSort(workItemsToBeSorted);

        // WHEN
        sortingController.selectedSortingCriteriaList.add(new Priority());

        // THEN
        assertThat(sortingController.getSortedWorkItems()).isEqualTo(expectedSortedWorkItems);
    }

    @Test
    void shouldSortWorkItemsCorrectlyByPriorityAndDueDateWhenFirstSortingByPriorityAndThenByDueDate() {
        ArrayList<WorkItem> expectedSortedWorkItems = new ArrayList<>(List.of(
                new WorkItemBuilder().setPriority(WorkItem.Priority.High).setDueDateTime(LocalDateTime.now().plusDays(1)).createWorkItem(),
                new WorkItemBuilder().setPriority(WorkItem.Priority.High).setDueDateTime(LocalDateTime.now().plusDays(2)).createWorkItem(),
                new WorkItemBuilder().setPriority(WorkItem.Priority.Medium).setDueDateTime(LocalDateTime.now().plusDays(1)).createWorkItem(),
                new WorkItemBuilder().setPriority(WorkItem.Priority.Medium).setDueDateTime(LocalDateTime.now().plusDays(2)).createWorkItem(),
                new WorkItemBuilder().setPriority(WorkItem.Priority.Low).setDueDateTime(LocalDateTime.now().plusDays(1)).createWorkItem(),
                new WorkItemBuilder().setPriority(WorkItem.Priority.Low).setDueDateTime(LocalDateTime.now().plusDays(2)).createWorkItem()
        ));

        // GIVEN
        ObservableList<WorkItem> workItemsToBeSorted = FXCollections.observableArrayList(getArrayListReverted(expectedSortedWorkItems));
        sortingController.setWorkItemsToSort(workItemsToBeSorted);

        // WHEN
        sortingController.selectedSortingCriteriaList.add(new Priority());
        sortingController.selectedSortingCriteriaList.add(new DueDate());

        // THEN
        assertThat(sortingController.getSortedWorkItems()).isEqualTo(expectedSortedWorkItems);
    }

    @Test
    void shouldSortWorkItemsCorrectlyByPriorityAndDueDateWhenFirstSortingByPriorityAndThenByDueDateIncludingNullValues() {
        ArrayList<WorkItem> expectedSortedWorkItems = new ArrayList<>(List.of(
                new WorkItemBuilder().setPriority(WorkItem.Priority.High).setDueDateTime(LocalDateTime.now()).createWorkItem(),
                new WorkItemBuilder().setPriority(WorkItem.Priority.High).setDueDateTime(LocalDateTime.now().plusDays(1)).createWorkItem(),
                new WorkItemBuilder().setPriority(WorkItem.Priority.Medium).setDueDateTime(LocalDateTime.now()).createWorkItem(),
                new WorkItemBuilder().setPriority(WorkItem.Priority.Medium).setDueDateTime(LocalDateTime.now().plusDays(1)).createWorkItem(),
                new WorkItemBuilder().setPriority(WorkItem.Priority.Medium).setDueDateTime(null).createWorkItem(),
                new WorkItemBuilder().setPriority(WorkItem.Priority.Low).setDueDateTime(LocalDateTime.now()).createWorkItem(),
                new WorkItemBuilder().setPriority(WorkItem.Priority.Low).setDueDateTime(LocalDateTime.now().plusDays(1)).createWorkItem(),
                new WorkItemBuilder().setPriority(null).setDueDateTime(LocalDateTime.now()).createWorkItem(),
                new WorkItemBuilder().setPriority(null).setDueDateTime(null).createWorkItem()
        ));

        // GIVEN
        ObservableList<WorkItem> workItemsToBeSorted = FXCollections.observableArrayList(getArrayListReverted(expectedSortedWorkItems));
        sortingController.setWorkItemsToSort(workItemsToBeSorted);

        // WHEN
        sortingController.selectedSortingCriteriaList.add(new Priority());
        sortingController.selectedSortingCriteriaList.add(new DueDate());

        // THEN
        assertThat(sortingController.getSortedWorkItems()).isEqualTo(expectedSortedWorkItems);
    }

    @Test
    void shouldSortWorkItemsDescendingByPriorityWhenSettingPriorityAsSortingCriteriaAndDescendingOrder() {
        ArrayList<WorkItem> expectedSortedWorkItems = new ArrayList<>(List.of(
                new WorkItemBuilder().setPriority(WorkItem.Priority.High).createWorkItem(),
                new WorkItemBuilder().setPriority(WorkItem.Priority.Medium).createWorkItem(),
                new WorkItemBuilder().setPriority(WorkItem.Priority.Low).createWorkItem()
        ));

        // GIVEN
        ObservableList<WorkItem> workItemsToBeSorted = FXCollections.observableArrayList(getArrayListReverted(expectedSortedWorkItems));
        sortingController.setWorkItemsToSort(workItemsToBeSorted);

        // WHEN
        sortingController.selectedSortingCriteriaList.add(new Priority());

        // THEN
        assertThat(sortingController.getSortedWorkItems()).isEqualTo(expectedSortedWorkItems);
    }

    @Test
    void shouldSortWorkItemsAscendingByPriorityWhenSettingPriorityAsSortingCriteriaAndAscendingOrder() {
        ArrayList<WorkItem> expectedSortedWorkItems = new ArrayList<>(List.of(
                new WorkItemBuilder().setPriority(WorkItem.Priority.Low).createWorkItem(),
                new WorkItemBuilder().setPriority(WorkItem.Priority.Medium).createWorkItem(),
                new WorkItemBuilder().setPriority(WorkItem.Priority.High).createWorkItem()
        ));

        // GIVEN
        ObservableList<WorkItem> workItemsToBeSorted = FXCollections.observableArrayList(getArrayListReverted(expectedSortedWorkItems));
        sortingController.setWorkItemsToSort(workItemsToBeSorted);

        // WHEN
        SortingCriteria priority = new Priority();
        priority.setSortingDirection(SortingDirection.Ascending);
        sortingController.selectedSortingCriteriaList.add(priority);

        // THEN
        assertThat(sortingController.getSortedWorkItems()).isEqualTo(expectedSortedWorkItems);
    }
}
