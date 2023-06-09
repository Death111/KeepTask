package de.doubleslash.keeptask.model.Sorting;

import de.doubleslash.keeptask.model.WorkItem;

import java.util.function.Function;

public class Priority extends SortingCriteria {
    public Priority() {
        name = "Priority";
        sortingDirection = SortingDirection.Descending;
    }

    @Override
    public Function<WorkItem, ? extends Comparable> getOrderFunction() {
        return WorkItem::getPriority;
    }
}
