package de.doubleslash.keeptask.controller;

import de.doubleslash.keeptask.model.WorkItem;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SortingController {
    final ObservableList<SortingCriteria> sortingCriteriaList = FXCollections.observableArrayList();
    private SortedList<WorkItem> sortedWorkItems;

    public SortingController() {
        sortingCriteriaList.addListener((ListChangeListener<? super SortingCriteria>) change -> updateComparator());
    }

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

    public void addSortingCriteriaByString(String sortingCriteriaString) {
        sortingCriteriaList.add(SortingCriteria.valueOf(sortingCriteriaString));
    }

    public void removeSortingCriteriaByString(String sortingCriteriaString) {
        sortingCriteriaList.remove(SortingCriteria.valueOf(sortingCriteriaString));
    }

    public List<String> getSortingCriterias() {
        return Arrays.stream(SortingCriteria.values()).map(sortingCriteria -> sortingCriteria.toString()).collect(Collectors.toList());
    }

    enum SortingCriteria {
        Priority, DueDate;
    }
}
