package com.eventsystem.models;

public class EventPreferenceField {
    private int fieldId;
    private int eventId;
    private String fieldLabel;
    private String fieldType; 
    private String dropdownOptions;

    
    public EventPreferenceField(int fieldId, int eventId, String fieldLabel, String fieldType, String dropdownOptions) {
        this.fieldId = fieldId;
        this.eventId = eventId;
        this.fieldLabel = fieldLabel;
        this.fieldType = fieldType;
        this.dropdownOptions = dropdownOptions;
    }

 
    public int getFieldId() { return fieldId; }
    public void setFieldId(int fieldId) { this.fieldId = fieldId; }

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public String getFieldLabel() { return fieldLabel; }
    public void setFieldLabel(String fieldLabel) { this.fieldLabel = fieldLabel; }

    public String getFieldType() { return fieldType; }
    public void setFieldType(String fieldType) { this.fieldType = fieldType; }

    public String getDropdownOptions() { return dropdownOptions; }
    public void setDropdownOptions(String dropdownOptions) { this.dropdownOptions = dropdownOptions; }
}