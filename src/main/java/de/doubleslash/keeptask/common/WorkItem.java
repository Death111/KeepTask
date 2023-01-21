package de.doubleslash.keeptask.common;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
public class WorkItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private long id;

    private String project;
    private  String priority;
    private String todo;
    private LocalDateTime createdDateTime;
    private LocalDateTime dueDateTime;
    private LocalDateTime completedDateTime;
    private boolean finished;
    private String note;

    public void setProject(String project) {
        this.project = project;
    }

    public void setTodo(String todo) {
        this.todo = todo;
    }

    public void setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public void setDueDateTime(LocalDateTime dueDateTime) {
        this.dueDateTime = dueDateTime;
    }

    public void setCompletedDateTime(LocalDateTime completedDateTime) {
        this.completedDateTime = completedDateTime;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public WorkItem(){
        // needed for hibernate
    }
    public WorkItem(String project, String priority, String todo, LocalDateTime createdDateTime, LocalDateTime dueDateTime, LocalDateTime completedDateTime, boolean finished, String note) {
        this.project = project;
        this.priority = priority;
        this.todo = todo;
        this.createdDateTime = createdDateTime;
        this.dueDateTime = dueDateTime;
        this.completedDateTime = completedDateTime;
        this.finished = finished;
        this.note = note;
    }

    public long getId() {
        return id;
    }

    public String getProject() {
        return project;
    }

    public String getTodo() {
        return todo;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public LocalDateTime getDueDateTime() {
        return dueDateTime;
    }

    public LocalDateTime getCompletedDateTime() {
        return completedDateTime;
    }

    public boolean isFinished() {
        return finished;
    }

    public String getNote() {
        return note;
    }
    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setId(long id) {
        this.id = id;
    }
}
