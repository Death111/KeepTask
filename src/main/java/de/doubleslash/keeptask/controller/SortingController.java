package de.doubleslash.keeptask.controller;

import de.doubleslash.keeptask.model.Sorting.DueDate;
import de.doubleslash.keeptask.model.Sorting.Priority;
import de.doubleslash.keeptask.model.Sorting.SortingCriteria;
import de.doubleslash.keeptask.model.Sorting.SortingDirection;
import de.doubleslash.keeptask.model.WorkItem;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SortingController {
    final ObservableList<SortingCriteria> selectedSortingCriteriaList = FXCollections.observableArrayList();
    private List<SortingCriteria> possibleSortingCriteriaList;
    private SortedList<WorkItem> sortedWorkItems;

    public SortingController() {
        possibleSortingCriteriaList = Arrays.asList(
                new Priority(),
                new DueDate()
        );
        selectedSortingCriteriaList.addListener((ListChangeListener<? super SortingCriteria>) change -> updateComparator());
    }

    private Comparator<WorkItem> getSortingComparator() {
        Comparator comparator = null;
        SortingCriteria sortingCriteria;

        if (selectedSortingCriteriaList.size() > 0) {
            sortingCriteria = selectedSortingCriteriaList.get(0);
            comparator = Comparator.comparing(sortingCriteria.getOrderFunction(), Comparator.nullsLast(getComparatorBySortingDirection(sortingCriteria.getSortingDirection())));
        }

        for (int i = 1; i < selectedSortingCriteriaList.size(); i++) {
            sortingCriteria = selectedSortingCriteriaList.get(i);
            comparator = comparator.thenComparing(sortingCriteria.getOrderFunction(), Comparator.nullsLast(getComparatorBySortingDirection(sortingCriteria.getSortingDirection())));
        }
        return comparator;
    }

    private Comparator getComparatorBySortingDirection(final SortingDirection sortingDirection) {
        Comparator comparator = null;

        switch (sortingDirection) {
            case Ascending:
                comparator = Comparator.naturalOrder();
                break;

            case Descending:
                comparator = Comparator.reverseOrder();
                break;

            default:
                throw new RuntimeException("The selected sorting direction could not be mapped to a comparator.");
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

    public void addSortingCriteriaByString(String sortingCriteriaString) {
        SortingCriteria foundSortingCriteria = possibleSortingCriteriaList.stream().filter(sortingCriteria -> sortingCriteria.getName() == sortingCriteriaString).findFirst().get();
        selectedSortingCriteriaList.add(foundSortingCriteria);
    }

    public void removeSortingCriteriaByString(String sortingCriteriaString) {
        SortingCriteria foundSortingCriteria = possibleSortingCriteriaList.stream().filter(sortingCriteria -> sortingCriteria.getName() == sortingCriteriaString).findFirst().get();
        selectedSortingCriteriaList.remove(foundSortingCriteria);
    }

    public List<String> getSortingCriterias() {
        return possibleSortingCriteriaList.stream().map(SortingCriteria::getName).collect(Collectors.toList());
    }
}
