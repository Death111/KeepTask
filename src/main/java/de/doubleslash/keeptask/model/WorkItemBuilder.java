package de.doubleslash.keeptask.model;

import java.time.LocalDateTime;

public class WorkItemBuilder {
    private String project;
    private WorkItem.Priority priority;
    private String todo;
    private LocalDateTime createdDateTime;
    private LocalDateTime dueDateTime;
    private LocalDateTime completedDateTime;
    private boolean finished;
    private String note;

    public WorkItemBuilder setProject(String project) {
        this.project = project;
        return this;
    }

    public WorkItemBuilder setPriority(WorkItem.Priority priority) {
        this.priority = priority;
        return this;
    }

    public WorkItemBuilder setTodo(String todo) {
        this.todo = todo;
        return this;
    }

    public WorkItemBuilder setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
        return this;
    }

    public WorkItemBuilder setDueDateTime(LocalDateTime dueDateTime) {
        this.dueDateTime = dueDateTime;
        return this;
    }

    public WorkItemBuilder setCompletedDateTime(LocalDateTime completedDateTime) {
        this.completedDateTime = completedDateTime;
        return this;
    }

    public WorkItemBuilder setFinished(boolean finished) {
        this.finished = finished;
        return this;
    }

    public WorkItemBuilder setNote(String note) {
        this.note = note;
        return this;
    }

    public WorkItem createWorkItem() {
        return new WorkItem(project, priority, todo, createdDateTime, dueDateTime, completedDateTime, finished, note);
    }
}