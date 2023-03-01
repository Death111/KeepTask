package de.doubleslash.keeptask.view;


import de.doubleslash.keeptask.common.Resources;
import de.doubleslash.keeptask.model.WorkItem;
import de.doubleslash.keeptask.model.WorkItemBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        ArrayList<WorkItem> expectedSortedWorkItems = new ArrayList<>(List.of(
                new WorkItemBuilder().setPriority(WorkItem.Priority.High).createWorkItem(),
                new WorkItemBuilder().setPriority(WorkItem.Priority.Medium).createWorkItem(),
                new WorkItemBuilder().setPriority(WorkItem.Priority.Low).createWorkItem()
        ));

        // GIVEN
        ObservableList<WorkItem> workItemsToBeSorted = FXCollections.observableArrayList(getArrayListReverted(expectedSortedWorkItems));
        sortingController.setWorkItemsToSort(workItemsToBeSorted);

        // WHEN
        sortingController.sortingCriteriaList.add(SortingController.SortingCriteria.Priority);

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
        sortingController.sortingCriteriaList.add(SortingController.SortingCriteria.DueDate);

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
        sortingController.sortingCriteriaList.add(SortingController.SortingCriteria.Priority);

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
        sortingController.sortingCriteriaList.add(SortingController.SortingCriteria.Priority);
        sortingController.sortingCriteriaList.add(SortingController.SortingCriteria.DueDate);

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
        sortingController.sortingCriteriaList.add(SortingController.SortingCriteria.Priority);
        sortingController.sortingCriteriaList.add(SortingController.SortingCriteria.DueDate);

        // THEN
        assertThat(sortingController.getSortedWorkItems()).isEqualTo(expectedSortedWorkItems);
    }

    @Test
    void shouldAddSortingCriteriaWhenSelectedViaComboBox() {
        ArrayList<WorkItem> expectedSortedWorkItems = new ArrayList<>(List.of(
                new WorkItemBuilder().setPriority(WorkItem.Priority.High).createWorkItem(),
                new WorkItemBuilder().setPriority(WorkItem.Priority.Medium).createWorkItem(),
                new WorkItemBuilder().setPriority(WorkItem.Priority.Low).createWorkItem()
        ));

        // GIVEN
        ObservableList<WorkItem> workItemsToBeSorted = FXCollections.observableArrayList(getArrayListReverted(expectedSortedWorkItems));
        sortingController.setWorkItemsToSort(workItemsToBeSorted);

        // WHEN
        sortingController.getAddSortingCriteriaCbx().getSelectionModel().select(SortingController.SortingCriteria.Priority);

        // THEN
        List<Node> buttonsList = sortingController.getSortingCriteriaHBox().getChildren().stream().filter(node -> node instanceof Button).collect(Collectors.toList());
        assertThat(buttonsList).hasSize(1);
        Button button = (Button) buttonsList.get(0);
        assertThat(SortingController.SortingCriteria.valueOf(button.getText())).isEqualTo(SortingController.SortingCriteria.Priority);
        assertThat(sortingController.getSortedWorkItems()).isEqualTo(expectedSortedWorkItems);
    }

    private ArrayList<WorkItem> getArrayListReverted(ArrayList<WorkItem> arrayListToRevert) {
        ArrayList<WorkItem> revertedArrayList = (ArrayList<WorkItem>) arrayListToRevert.clone();
        Collections.reverse(revertedArrayList);
        return revertedArrayList;
    }
}
