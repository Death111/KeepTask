package de.doubleslash.keeptask.model;

public class TodoPart {
    private String stringValue;
    private String link;
    private boolean isLink;

    public TodoPart(String stringValue, String link) {
        this.stringValue = stringValue;
        this.link = link;
        this.isLink = true;
    }

    public TodoPart(String stringValue) {
        this.stringValue = stringValue;
        this.link = "";
        this.isLink = false;
    }

    public String getStringValue() {
        return stringValue;
    }

    public String getLink() {
        return link;
    }

    public boolean isLink() {
        return isLink;
    }

    @Override
    public String toString() {
        return "TodoPart{" +
                "stringValue='" + stringValue + '\'' +
                ", link='" + link + '\'' +
                ", isLink=" + isLink +
                '}';
    }
}
