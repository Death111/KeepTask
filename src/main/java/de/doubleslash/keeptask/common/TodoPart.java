package de.doubleslash.keeptask.common;

import java.util.Objects;

public class TodoPart {
    private String stringValue;
    private boolean isLink;

    public TodoPart(String stringValue, boolean isLink) {
        this.stringValue = stringValue;
        this.isLink = isLink;
    }

    public String getStringValue() {
        return stringValue;
    }

    public boolean isLink() {
        return isLink;
    }
}
