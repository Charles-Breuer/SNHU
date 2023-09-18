package com.zybooks.projectcs360;

public class EventModel
{
    //------------- Class Fields ----------------//
    private long m_ID;
    private String m_Date;
    private String m_Time;
    private String m_Title;
    private String m_Description;

    //------------- Constructor(s) ----------------//
    public EventModel(long id, String date, String time, String title, String description) {
        this.m_ID = id;
        this.m_Date = date;
        this.m_Time = time;
        this.m_Title = title;
        this.m_Description = description;
    }
    public EventModel() {}

    //------------- Getters and Setters ----------------//
    public long getId() {
        return m_ID;
    }

    public String getDate() {
        return m_Date;
    }

    public String getTime() {
        return m_Time;
    }

    public String getTitle() {
        return m_Title;
    }

    public String getDescription() {
        return m_Description;
    }

    public void setId(long id) {
        this.m_ID = id;
    }

    public void setDate(String date) {
        this.m_Date = date;
    }

    public void setTime(String time) {
        this.m_Time = time;
    }

    public void setTitle(String title) {
        this.m_Title = title;
    }

    public void setDescription(String description) {
        this.m_Description = description;
    }
}
