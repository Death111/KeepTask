package de.doubleslash.keeptask.view;


import de.doubleslash.keeptask.common.Resources;
import de.doubleslash.keeptask.model.WorkItem;
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
                new WorkItem("TEST", WorkItem.Priority.High, "todo", LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, false, ""),
                new WorkItem("TEST", WorkItem.Priority.Medium, "todo", LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, false, ""),
                new WorkItem("TEST", WorkItem.Priority.Low, "todo", LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, false, "")
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
}