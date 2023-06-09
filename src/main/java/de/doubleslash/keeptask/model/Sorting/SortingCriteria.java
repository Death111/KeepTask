package de.doubleslash.keeptask.model.Sorting;

import de.doubleslash.keeptask.model.WorkItem;

import java.util.function.Function;

public abstract class SortingCriteria {
    protected String name = null;
    protected SortingDirection sortingDirection = SortingDirection.Ascending;

    public String getName() {
        return name;
    }

    public SortingDirection getSortingDirection() {
        return sortingDirection;
    }

    public void setSortingDirection(SortingDirection sortingDirection) {
        this.sortingDirection = sortingDirection;
    }

    public abstract Function<WorkItem, ? extends Comparable> getOrderFunction();
}
