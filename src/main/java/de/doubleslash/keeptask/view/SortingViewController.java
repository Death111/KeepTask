package de.doubleslash.keeptask.view;

import de.doubleslash.keeptask.controller.SortingController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import org.springframework.stereotype.Component;

@Component
public class SortingViewController {
    private SortingController sortingController;
    @FXML
    private HBox sortingCriteriaHBox;
    @FXML
    private ComboBox addSortingCriteriaCbx;

    SortingController getSortingController() {
        return sortingController;
    }

    HBox getSortingCriteriaHBox() {
        return sortingCriteriaHBox;
    }

    ComboBox getAddSortingCriteriaCbx() {
        return addSortingCriteriaCbx;
    }

    @FXML
    private void initialize() {
        sortingController = new SortingController();

        addSortingCriteriaCbx.setItems(FXCollections.observableArrayList(sortingController.getSortingCriterias()));
        addSortingCriteriaCbx.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;

            sortingController.addSortingCriteriaByString(newValue.toString());
            addSortingCriteriaButton(newValue.toString());
            addSortingCriteriaCbx.getSelectionModel().clearSelection();
            addSortingCriteriaCbx.getItems().remove(newValue);
        });
    }

    private void addSortingCriteriaButton(String sortingCriteria) {
        Button sortingCriteriaButton = new Button();
        sortingCriteriaButton.setText(sortingCriteria);
        Tooltip tooltip = new Tooltip();
        tooltip.setText("Click to remove " + sortingCriteria + " sorting criteria");
        sortingCriteriaButton.setTooltip(tooltip);
        sortingCriteriaButton.setOnAction(event -> {
            sortingController.removeSortingCriteriaByString(sortingCriteria);
            addSortingCriteriaCbx.getItems().add(sortingCriteria);
            sortingCriteriaHBox.getChildren().remove(sortingCriteriaButton);
        });
        sortingCriteriaHBox.getChildren().add(sortingCriteriaHBox.getChildren().size() - 1, sortingCriteriaButton);
    }
}
