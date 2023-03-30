package de.doubleslash.keeptask.model.Sorting;

import de.doubleslash.keeptask.model.WorkItem;

import java.util.function.Function;

public class DueDate extends SortingCriteria {
    public DueDate() {
        name = "Due Date";
        sortingDirection = SortingDirection.Ascending;
    }

    @Override
    public Function<WorkItem, ? extends Comparable> getOrderFunction() {
        return WorkItem::getDueDateTime;
    }
}
