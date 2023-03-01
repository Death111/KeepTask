package de.doubleslash.keeptask.view;

import de.doubleslash.keeptask.model.WorkItem;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.function.Function;

@Component
public class SortingController {
    final ObservableList<SortingCriteria> sortingCriteriaList = FXCollections.observableArrayList();
    private SortedList<WorkItem> sortedWorkItems;
    @FXML
    private HBox sortingCriteriaHBox;
    @FXML
    private ComboBox addSortingCriteriaCbx;

    private static Function<WorkItem, ? extends Comparable> orderBy(SortingCriteria criteria) {
        switch (criteria) {
            case Priority:
                return WorkItem::getPriority;
            case DueDate:
                return WorkItem::getDueDateTime;
            default:
                throw new IllegalArgumentException("" + criteria);
        }
    }

    HBox getSortingCriteriaHBox() {
        return sortingCriteriaHBox;
    }

    ComboBox getAddSortingCriteriaCbx() {
        return addSortingCriteriaCbx;
    }

    @FXML
    private void initialize() {
        sortingCriteriaList.addListener((ListChangeListener<? super SortingCriteria>) change -> {
            if (!change.next()) return;

            updateComparator();

            if (!change.wasAdded()) return;

            SortingCriteria addedSortingCriteria = change.getAddedSubList().get(0);
            Button sortingCriteriaButton = new Button();
            sortingCriteriaButton.setText(addedSortingCriteria.toString());
            Tooltip tooltip = new Tooltip();
            tooltip.setText("Click to remove " + addedSortingCriteria + " sorting criteria");
            sortingCriteriaButton.setTooltip(tooltip);
            sortingCriteriaButton.setOnAction(event -> {
                sortingCriteriaList.remove(addedSortingCriteria);
                addSortingCriteriaCbx.getItems().add(addedSortingCriteria);
                sortingCriteriaHBox.getChildren().remove(sortingCriteriaButton);
            });
            sortingCriteriaHBox.getChildren().add(sortingCriteriaHBox.getChildren().size() - 1, sortingCriteriaButton);
        });

        addSortingCriteriaCbx.setItems(FXCollections.observableArrayList(SortingCriteria.values()));
        addSortingCriteriaCbx.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;

            sortingCriteriaList.add((SortingCriteria) newValue);
            addSortingCriteriaCbx.getSelectionModel().clearSelection();
            addSortingCriteriaCbx.getItems().remove(newValue);
        });
    }

    private Comparator<WorkItem> getSortingComparator() {
        Comparator comparator = null;
        if (sortingCriteriaList.size() > 0) {
            comparator = Comparator.comparing(orderBy(sortingCriteriaList.get(0)), Comparator.nullsLast(Comparator.naturalOrder()));
        }
        for (int i = 1; i < sortingCriteriaList.size(); i++) {
            SortingCriteria sortingCriteria = sortingCriteriaList.get(i);
            comparator = comparator.thenComparing(orderBy(sortingCriteria), Comparator.nullsLast(Comparator.naturalOrder()));
        }
        return comparator;
    }

    private void updateComparator() {
        Comparator<WorkItem> comparator = getSortingComparator();
        sortedWorkItems.setComparator(comparator);
    }

    public void setWorkItemsToSort(ObservableList<WorkItem> workItemsToSort) {
        sortedWorkItems = new SortedList<>(workItemsToSort);
        updateComparator();
    }

    public SortedList<WorkItem> getSortedWorkItems() {
        return sortedWorkItems;
    }

    enum SortingCriteria {
        Priority, DueDate;
    }
}
