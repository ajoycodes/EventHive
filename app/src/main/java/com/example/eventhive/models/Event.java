package com.example.eventhive.models;

public class Event {
    private String date;
    private String month;
    private String title;
    private String venue;
    private String organizer;
    private int color;

    public Event(String date, String month, String title, String venue, String organizer, int color) {
        this.date = date;
        this.month = month;
        this.title = title;
        this.venue = venue;
        this.organizer = organizer;
        this.color = color;
    }


    public String getDate() { return date; }
    public String getMonth() { return month; }
    public String getTitle() { return title; }
    public String getVenue() { return venue; }
    public String getOrganizer() { return organizer; }
    public int getColor() { return color; }


}