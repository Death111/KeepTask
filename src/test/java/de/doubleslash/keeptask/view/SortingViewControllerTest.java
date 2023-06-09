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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.doubleslash.keeptask.TestUtils.getArrayListReverted;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(ApplicationExtension.class)
class SortingViewControllerTest {
    SortingViewController sortingViewController;

    @Start
    private void start(Stage stage) throws IOException {
        final FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Resources.getResource(Resources.RESOURCE.FXML_SORTING_LAYOUT));
        loader.load();
        sortingViewController = loader.getController();
    }

    @Test
    void shouldAddSortingCriteriaWhenSelectedViaComboBox() {
        String expectedSelectedSortingCriteria = "Priority";
        ArrayList<WorkItem> expectedSortedWorkItems = new ArrayList<>(List.of(
                new WorkItemBuilder().setPriority(WorkItem.Priority.High).createWorkItem(),
                new WorkItemBuilder().setPriority(WorkItem.Priority.Medium).createWorkItem(),
                new WorkItemBuilder().setPriority(WorkItem.Priority.Low).createWorkItem()
        ));

        // GIVEN
        ObservableList<WorkItem> workItemsToBeSorted = FXCollections.observableArrayList(getArrayListReverted(expectedSortedWorkItems));
        sortingViewController.getSortingController().setWorkItemsToSort(workItemsToBeSorted);

        // WHEN
        sortingViewController.getAddSortingCriteriaCbx().getSelectionModel().select(expectedSelectedSortingCriteria);

        // THEN
        List<Node> buttonsList = sortingViewController.getSortingCriteriaHBox().getChildren().stream().filter(node -> node instanceof Button).collect(Collectors.toList());
        assertThat(buttonsList).hasSize(1);
        Button button = (Button) buttonsList.get(0);
        assertThat(button.getText()).isEqualTo(expectedSelectedSortingCriteria);
        assertThat(sortingViewController.getSortingController().getSortedWorkItems()).isEqualTo(expectedSortedWorkItems);
    }
}
